package com.gracielo.projectta.ui.membership

import android.app.AlertDialog
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.gracielo.projectta.R
import com.gracielo.projectta.data.model.membershipPackage.DataMembership
import com.gracielo.projectta.data.source.local.entity.ShoppingListEntity
import com.gracielo.projectta.data.source.local.entity.UserEntity
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityBuyMembershipBinding
import com.gracielo.projectta.notification.DailyReminder
import com.gracielo.projectta.ui.login.TestLoginActivity
import com.gracielo.projectta.ui.setting.SettingListAdapter
import com.gracielo.projectta.util.FunHelper
import com.gracielo.projectta.util.FunHelper.observeOnce
import com.gracielo.projectta.viewmodel.ShoppingListViewModel
import com.gracielo.projectta.viewmodel.UserViewModel
import com.jakewharton.threetenabp.AndroidThreeTen
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ItemDetails
import com.midtrans.sdk.corekit.models.ShippingAddress
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.text.NumberFormat
import java.util.Locale
import kotlin.collections.ArrayList


class BuyMembershipActivity : AppCompatActivity() {

    private lateinit var binding:ActivityBuyMembershipBinding
    lateinit var viewModel: UserViewModel
    var helper=FunHelper
    lateinit var dataUser : UserEntity
    var listPackage = mutableListOf<DataMembership>()
    var itemDetailsList = ArrayList<ItemDetails>()
    var hargaList=ArrayList<Int>()
    val apiServices = ApiServices()

    private var listShoppingListUser = mutableListOf<ShoppingListEntity>()
    private lateinit var shoppingListViewModel: ShoppingListViewModel
    val daily= DailyReminder()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyMembershipBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidThreeTen.init(this)

        if(intent.hasExtra("Expired")){
            Log.d("Expired","Masuk Account Expired")
            binding.layoutAlertMessage.visibility= View.VISIBLE
        }
        if(intent.hasExtra("Change")){
            Log.d("Change","Masuk Account Change")
            val tipe = intent.getStringExtra("Tipe")
            if(tipe=="1"){

            }
            else if(tipe=="2"){
                binding.layoutMembershipBasic.visibility = View.GONE
                binding.pemisahBuy1.visibility = View.GONE
                binding.layoutMembershipLowCarb.visibility = View.GONE
                binding.pemisahBuy2.visibility = View.GONE
            }
            else if(tipe=="3"){
                binding.layoutMembershipBasic.visibility = View.GONE
                binding.pemisahBuy1.visibility = View.GONE
                binding.layoutMembershipVegetarian.visibility = View.GONE
                binding.pemisahBuy3.visibility = View.GONE
            }
            else if(tipe=="4"){
                binding.layoutMembershipBasic.visibility = View.GONE
                binding.pemisahBuy1.visibility = View.GONE
                binding.layoutMembershipVegetarian.visibility = View.GONE
                binding.pemisahBuy3.visibility = View.GONE
                binding.layoutMembershipLowCarb.visibility = View.GONE
                binding.pemisahBuy2.visibility = View.GONE

            }

            binding.layoutAlertMessage.visibility= View.GONE
        }
        else binding.layoutAlertMessage.visibility= View.GONE

        apiServices.getMembershipPackage {
            if(it?.code==1){
                listPackage.addAll(it.dataMembership)
                for(i in listPackage.indices){
                    val itemDetail = ItemDetails((i+1).toString(), listPackage[i].price.toDouble(), 1,listPackage[i].name)
                    itemDetailsList.add(itemDetail)
                    hargaList.add(listPackage[i].price)

                    val formatter = NumberFormat.getCurrencyInstance(Locale.GERMANY)
                    val numbertemp = formatter.format(listPackage[i].price)
                    val number = numbertemp.split(",")[0]

                    if(listPackage[i].idPackage=="1"){
                        binding.txtHargaBasic.text = "Rp. ${number},-"
                    }
                    else if(listPackage[i].idPackage=="2"){
                        binding.txtVegetarianPrice.text = "Rp. ${number},-"
                    }
                    else if(listPackage[i].idPackage=="3"){
                        binding.txtHargaLowCarb.text = "Rp. ${number},-"
                    }
                    else if(listPackage[i].idPackage=="4"){
                        binding.txtAllInOnePrice.text = "Rp. ${number},-"
                    }
                }
            }
        }


        dataUser = UserEntity("1","1","1","1","0","",1,1,"1",1.1,1,1)

        viewModel=helper.obtainUserViewModel(this)
        shoppingListViewModel = helper.obtainShoppingViewModel(this)
        viewModel.getUser().observeOnce(this){
            dataUser=it
            shoppingListViewModel.getShoppingList(it.id).observeOnce(this){list->
                listShoppingListUser.addAll(list)
            }
        }


        binding.btnBuyBasic.setOnClickListener {
            initMidtrans(0)
        }
        binding.btnBuyVegetarian.setOnClickListener {
            initMidtrans(1)
        }
        binding.btnBuyLowCarb.setOnClickListener {
            initMidtrans(2)
        }
        binding.btnBuyAllInOne.setOnClickListener {
            initMidtrans(3)
        }

        supportActionBar?.hide()
    }

    override fun onBackPressed() {
        if(intent.hasExtra("Expired")){
            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout from this user ?")
                .setPositiveButton("Yes"
                ) { _, _ ->
                    daily.cancelAlarm(this)
                    viewModel.delete(dataUser)
                    viewModel.getUserNutrients().observeOnce(this){userNutrients->
                        apiServices.saveUserNutrientHistory(userNutrients){}
                        viewModel.deleteUserNutrients(userNutrients)
                    }
                    if(listShoppingListUser.size>0){
                        for(i in listShoppingListUser.indices){
                            shoppingListViewModel.delete(listShoppingListUser[i])
                        }
                    }
                    helper.deleteAllSelectedIngredients(this)
                    val intent = Intent(this, TestLoginActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }
                .setNegativeButton(android.R.string.no, null)
                .show()
        }
        else finish()

    }


    fun initMidtrans(itemSelected:Int){

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddhhmmss")
        val formatterDatetime = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss ")
        val formatted = current.format(formatter)
        val formattedDatetime = current.format(formatter)

        val ammount = itemDetailsList[itemSelected].price
        val idPaket= itemSelected+1
        SdkUIFlowBuilder.init()
            .setClientKey(R.string.MIDTRANS_CLIENT_KEY.toString()) // client_key is mandatory
            .setContext(applicationContext) // context is mandatory
            .setTransactionFinishedCallback { result->
                if(result.response!=null){
                    if(result.response.orderId!=null){
                        val orderId=result.response.orderId
                        var status=-1
                        var expired=""
                        // Handle finished transaction here.
                        if(result.status == TransactionResult.STATUS_FAILED){
                            status= 0
                        }
                        else if(result.status == TransactionResult.STATUS_PENDING){
                            status= 2
                        }
                        else if(result.status == TransactionResult.STATUS_INVALID){
                            status= 0
                        }
                        else if(result.status == TransactionResult.STATUS_SUCCESS){
                            status =1
                        }
                        val transaction = TransactionMembership(orderId,dataUser.id,formattedDatetime,ammount.toInt(),idPaket,status,expired)
                        apiServices.InsertMembershipTransaction(transaction){}
                    }
                }
            } // set transaction finish callback (sdk callback)
            .setMerchantBaseUrl("https://skripsicielo.masuk.id/midtrans.php/") //set merchant url (required)
            .enableLog(true) // enable sdk log (optional)
            .setColorTheme(
                CustomColorTheme(
                    "#FFE51255",
                    "#B61548",
                    "#FFE51255"
                )
            ) // set theme. it will replace theme on snap theme on MAP ( optional)
            .setLanguage("en") //`en` for English and `id` for Bahasa
            .buildSDK()



        var transactionRequest = TransactionRequest("Billing-${dataUser.id}-${formatted}", hargaList[itemSelected].toDouble())
        var customerDetails = CustomerDetails()
        customerDetails.customerIdentifier = dataUser.id
        customerDetails.firstName = dataUser.name
        customerDetails.email = dataUser.email


        val shippingAddress = ShippingAddress()
        shippingAddress.address = " "
        shippingAddress.city = " "
        shippingAddress.postalCode = "12345"
        customerDetails.shippingAddress = shippingAddress

        // Create array list and add above item details in it and then set it to transaction request.
        val itemDetailsListSent: ArrayList<ItemDetails> = ArrayList()
        itemDetailsListSent.add(itemDetailsList[itemSelected])

        // Set item details into the transaction request.
        transactionRequest.itemDetails = itemDetailsListSent

        transactionRequest.customerDetails = customerDetails

//        val billInfoModel = BillInfoModel(BILL_INFO_KEY, BILL_INFO_VALUE)
//        // Set the bill info on transaction details
//        transactionRequest.billInfoModel = billInfoModel


        MidtransSDK.getInstance().transactionRequest=transactionRequest
        MidtransSDK.getInstance().startPaymentUiFlow(this)
    }

}