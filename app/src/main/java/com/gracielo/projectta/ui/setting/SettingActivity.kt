package com.gracielo.projectta.ui.setting

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gracielo.projectta.R
import com.gracielo.projectta.data.model.listEquipment.DataEquipment
import com.gracielo.projectta.data.model.userListEquipment.DataEquipmentUser
import com.gracielo.projectta.data.source.local.entity.ShoppingListEntity
import com.gracielo.projectta.data.source.local.entity.UserEntity
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivitySettingBinding
import com.gracielo.projectta.notification.DailyReminder
import com.gracielo.projectta.ui.equipment.ListAlatMasakActivity
import com.gracielo.projectta.ui.history.HistoryHomeActivity
import com.gracielo.projectta.ui.homepage.HomeActivity
import com.gracielo.projectta.ui.ingredients.DataEquipmentAdapter
import com.gracielo.projectta.ui.ingredients.IngridientsList
import com.gracielo.projectta.ui.login.TestLoginActivity
import com.gracielo.projectta.ui.membership.BuyMembershipActivity
import com.gracielo.projectta.ui.membership.MembershipHistory
import com.gracielo.projectta.ui.shoppingList.ShoppingListActivity
import com.gracielo.projectta.util.FunHelper
import com.gracielo.projectta.viewmodel.IngredientsViewModel
import com.gracielo.projectta.viewmodel.ShoppingListViewModel
import com.gracielo.projectta.viewmodel.UserViewModel

class SettingActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySettingBinding
    private lateinit var rv_setting: RecyclerView
    private lateinit var userViewModel: UserViewModel
    private lateinit var ingredientsViewModel: IngredientsViewModel
    private lateinit var shoppingListViewModel: ShoppingListViewModel
    private var settingList = ArrayList<String>()
    var listAllEquipment = ArrayList<DataEquipment>()
    var dataEquipmentUser = DataEquipmentUser("","")
    var listAllEquipmentAdaptersTemp = ArrayList<DataEquipmentAdapter>()
    var listAllEquipmentAdapters = ArrayList<DataEquipment>()
    val adapters = SettingListAdapter()
    val adapterequipment = dataEquipmentAdapter()
    val helper = FunHelper
    private lateinit var dataUser:UserEntity
    private var listShoppingListUser = mutableListOf<ShoppingListEntity>()
    var apiServices = ApiServices()
    val daily= DailyReminder()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ingredientsViewModel = helper.obtainIngredientsViewModel(this)
        userViewModel = helper.obtainUserViewModel(this)
        shoppingListViewModel = helper.obtainShoppingViewModel(this)

        dataUser = UserEntity("1","1","1","1","0","",1,1,"1",1.1,1,1)

        userViewModel.getUser().observeOnce(this){
            dataUser=it
            if(it.gender=="Male"){binding.userPicture.setImageResource(R.drawable.male_profile)} else if(it.gender=="Female"){binding.userPicture.setImageResource(R.drawable.female_profile)}
            binding.textUserName.text=it.name
            if(it.tipe=="0"){
                binding.textTipe.text="Trial"
                binding.textTipe.setTextColor(Color.BLACK)
            } else if(it.tipe=="1"){

                binding.textTipe.text="Basic Plan"
                binding.textTipe.setTextColor(Color.BLACK)
            } else if(it.tipe=="2"){
                binding.textTipe.text="Vegetarian Plan"
                binding.textTipe.setTextColor(Color.GREEN)
            } else if(it.tipe=="3"){
                binding.textTipe.text="Low Carb Plan"
                binding.textTipe.setTextColor(Color.rgb(160,82,45))
            } else if(it.tipe=="4"){
                binding.textTipe.text="All in One Plan"
                binding.textTipe.setTextColor(Color.rgb(255,223,0))
            }
            val expired = it.expired.split(" ")
            binding.textUserName.text=it.name
            binding.birthdayUser.text="${it.age} years old"
            binding.textExpired.text=expired[0]
            binding.emailUser.text=it.email


            refreshEquipment()
            shoppingListViewModel.getShoppingList(it.id).observeOnce(this){list->
                listShoppingListUser.addAll(list)
            }
        }

        binding.rvListEquipmentUser.apply {
            layoutManager  = LinearLayoutManager(this@SettingActivity,LinearLayoutManager.HORIZONTAL,false)
            adapter=adapterequipment
        }
        Handler(Looper.getMainLooper()).postDelayed({

        },500)
        binding.btnEditEquipmentsSelected.setOnClickListener {
            val intent = Intent(this,ListAlatMasakActivity::class.java)
            startActivity(intent)
        }
        var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                refreshEquipment()
            }
        }
        rv_setting = binding.rvSettingList
        Handler(Looper.getMainLooper()).postDelayed({
            settingList.add("Notification Settings")
            if(dataUser.tipe=="0"){
                settingList.add("Buy Membership")
            }
            else {
                settingList.add("Change Membership")
            }
            settingList.add("Membership Subscription History")
            settingList.add("Logout")
            adapters.setData(settingList)

            rv_setting.apply {
                layoutManager= LinearLayoutManager(context)
                adapter = adapters
            }
        },500)

        binding.btnEditProfile.setOnClickListener{
            val intent = Intent(this,EditProfileActivity::class.java)
            resultLauncher.launch(intent)


        }

        adapters.onItemClick = {
            when (it){
                "Logout" ->{
                    AlertDialog.Builder(this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout from this user ?")
                        .setPositiveButton("Yes"
                        ) { _, _ ->
                            apiServices.updateUser(dataUser){
                                if(it?.code==1){

                                }
                            }
                            daily.cancelAlarm(this)
                            userViewModel.delete(dataUser)
                            userViewModel.getUserNutrients().observeOnce(this){userNutrients->
                                apiServices.saveUserNutrientHistory(userNutrients){}
                                userViewModel.deleteUserNutrients(userNutrients)
                            }
                           if(listShoppingListUser.size>0){
                               for(i in listShoppingListUser.indices){
                                   shoppingListViewModel.delete(listShoppingListUser[i])
                               }
                           }
                            helper.deleteAllSelectedIngredients(this)
                            val intent = Intent(this,TestLoginActivity::class.java)
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                        }
                        .setNegativeButton(android.R.string.no, null)
                        .show()
                }
                "Change Membership" ->{
                    val userType= dataUser.tipe
                    var intentKirim = Intent(this, BuyMembershipActivity::class.java)
                    intentKirim.putExtra("Change","Change")
                    intentKirim.putExtra("Tipe",dataUser.tipe)
                    startActivity(intentKirim)
                }
                "Buy Membership" ->{
                    val intent = Intent(this, BuyMembershipActivity::class.java)
                    startActivity(intent)
                }
                "Membership Subscription History" ->{
                    val intent = Intent(this, MembershipHistory::class.java)
                    startActivity(intent)
                }
                "Notification Settings"->{
                    val intent = Intent(this, NotificationSettingActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        val fabAdd = binding.fabAdd
        fabAdd.setOnClickListener {
            intent = Intent(this, IngridientsList::class.java)
            startActivity(intent)
        }

        val bottomMenu = binding.bottomnavigationbar
        bottomMenu.selectedItemId= R.id.mPerson
        bottomMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.mHome -> {

                    val intent = Intent(this,HomeActivity::class.java)
                    finish()
                    startActivity(intent)
                }
                R.id.mShopList -> {
                    val intent = Intent(this,ShoppingListActivity::class.java)
                    finish()
                    startActivity(intent)
                }
                R.id.mHistory -> {
                    val intent = Intent(this, HistoryHomeActivity::class.java)
                    finish()
                    startActivity(intent)
                }
                R.id.mPerson -> {
                    //Do Nothing cz this is the activity currently active
//                    val intent = Intent(this,SettingActivity::class.java)
//                    finish()
//                    startActivity(intent)
                }
            }
            true
        }
        supportActionBar?.hide()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Exit App")
            .setMessage("Are you sure you want to exit the application ?") // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton("Yes"
            ) { _, _ ->
//                Toast.makeText(this,"Successfully Logout",Toast.LENGTH_SHORT).show()
                finishAffinity()
//                viewModel.getUser()?.removeObserver(){}

            } // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.no, null)
//            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }

    private fun refreshEquipment(){
        Log.d("equipmentListSetting","masuk")
        apiServices.getAllEquipment {equipment->
            if(equipment?.code==1) {
                listAllEquipment.addAll(equipment.dataEquipment)
                for (i in listAllEquipment.indices) {
                    val data = DataEquipmentAdapter(
                        listAllEquipment[i].id,
                        listAllEquipment[i].image,
                        listAllEquipment[i].name,
                        false
                    )
                    listAllEquipmentAdaptersTemp.add(data)
                }
                apiServices.getUserSelectedEquipment(dataUser.id){
                    if(it?.code==1){
                        binding.rvListEquipmentUser.visibility= View.VISIBLE
                        binding.noItemEquipmentUser.visibility= View.INVISIBLE
                        dataEquipmentUser = it.dataEquipment
                        var listIdEquipmentUser = dataEquipmentUser.ListIDEquipment.split(", ")
                        for (i in listIdEquipmentUser.indices){
                            for (j in listAllEquipmentAdaptersTemp.indices){
                                if(listIdEquipmentUser[i]==listAllEquipmentAdaptersTemp[j].id)listAllEquipmentAdaptersTemp[j].isSelected=true
                            }
                        }
                        for (i in listAllEquipmentAdaptersTemp.indices){
                            if(listAllEquipmentAdaptersTemp[i].isSelected) {
                                val dataeqp = DataEquipment(listAllEquipmentAdaptersTemp[i].id,listAllEquipmentAdaptersTemp[i].image,listAllEquipmentAdaptersTemp[i].name)
                                listAllEquipmentAdapters.add(dataeqp)
                            }
                        }
                    }
                    else if(it?.code==-2){
                        binding.rvListEquipmentUser.visibility= View.INVISIBLE
                        binding.noItemEquipmentUser.visibility= View.VISIBLE
                    }
                }

            }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            Log.d("equipmentListSetting",listAllEquipmentAdapters.size.toString())
            adapterequipment.setData(listAllEquipmentAdapters)
            adapterequipment.notifyDataSetChanged()
            Log.d("equipmentListSetting",adapterequipment.itemCount.toString())
        },500)
    }


}