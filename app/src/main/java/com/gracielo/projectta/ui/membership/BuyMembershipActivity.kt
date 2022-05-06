package com.gracielo.projectta.ui.membership

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gracielo.projectta.R
import com.gracielo.projectta.data.source.local.entity.UserEntity
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityBuyMembershipBinding
import com.gracielo.projectta.ui.setting.SettingListAdapter
import com.gracielo.projectta.util.FunHelper
import com.gracielo.projectta.util.FunHelper.observeOnce
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


class BuyMembershipActivity : AppCompatActivity() {

    private lateinit var binding:ActivityBuyMembershipBinding
    lateinit var viewModel: UserViewModel
    var helper=FunHelper
    private var membershipList = ArrayList<String>()
    val adapters = SettingListAdapter()
    lateinit var dataUser : UserEntity
    val itemDetails1 = ItemDetails("1", 79000.0, 1, "Basic Plan")
    val itemDetails2 = ItemDetails("2", 149000.0, 1, "Vegetarian Plan ")
    val itemDetails3 = ItemDetails("3", 149000.0, 1, "Low Carb Plan")
    val itemDetails4 = ItemDetails("4", 199000.0, 1, "All in One Plan")
    var itemDetailsList = ArrayList<ItemDetails>()
    var hargaList=ArrayList<Int>()
    val apiServices = ApiServices()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyMembershipBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidThreeTen.init(this)


        itemDetailsList.add(itemDetails1)
        itemDetailsList.add(itemDetails2)
        itemDetailsList.add(itemDetails3)
        itemDetailsList.add(itemDetails4)

        hargaList.add(itemDetails1.price.toInt())
        hargaList.add(itemDetails2.price.toInt())
        hargaList.add(itemDetails3.price.toInt())
        hargaList.add(itemDetails4.price.toInt())

        dataUser = UserEntity("1","1","1","1","0","",1,1,"1",1.1,1,1)

        viewModel=helper.obtainUserViewModel(this)
        viewModel.getUser().observeOnce(this){
            dataUser=it
        }

        membershipList.add("Basic Plan")
        membershipList.add("Vegetarian Plan")
        membershipList.add("Low Carb Plan")
        membershipList.add("All in One Plan")
        adapters.setData(membershipList)

        binding.btnBuyBasic.setOnClickListener {
            initMidtrans(0)
        }
        binding.btnBuyLowCarb.setOnClickListener {
            initMidtrans(2)
        }
        binding.btnBuyVegetarian.setOnClickListener {
            initMidtrans(1)
        }
        binding.btnBuyAllInOne.setOnClickListener {
            initMidtrans(3)
        }

        supportActionBar?.hide()
    }


    fun initMidtrans(itemSelected:Int){

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddhhmmss ")
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