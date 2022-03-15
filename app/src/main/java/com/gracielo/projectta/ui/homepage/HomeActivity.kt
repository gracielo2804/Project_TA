package com.gracielo.projectta.ui.homepage

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gracielo.projectta.R
import com.gracielo.projectta.data.model.DataUserProfile
import com.gracielo.projectta.data.source.local.entity.UserEntity
import com.gracielo.projectta.databinding.ActivityHomeBinding
import com.gracielo.projectta.viewmodel.UserViewModel
import com.gracielo.projectta.viewmodel.ViewModelFactory
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.bumptech.glide.Glide
import com.gracielo.projectta.data.source.local.entity.Ingredients
import com.gracielo.projectta.data.source.local.entity.UserNutrientsEntity
import com.gracielo.projectta.ui.ingredients.IngridientsList
import com.gracielo.projectta.ui.setting.SettingActivity
import com.gracielo.projectta.ui.shoppingList.ShoppingListActivity
import com.jakewharton.threetenabp.AndroidThreeTen
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
//    var dataUser:DataUser? = null
    var dataUserProfile:DataUserProfile?=null
    var dataUserNutrients:UserNutrientsEntity? = null
    var selectedIngredients = mutableListOf<Ingredients>()
    lateinit var dataUser :UserEntity
    lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AndroidThreeTen.init(this)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        binding.progressBar.visibility= View.INVISIBLE
        setContentView(binding.root)
        dataUser = UserEntity("1","1","1","1",1,1,"1",1.1,1)
        viewModel = obtainViewModel(this@HomeActivity)
        viewModel.getUser()?.observeOnce(this){
            dataUser=it
            binding.textUserLoginHome.text= dataUser.name
//            binding.txtDashboardKalori.text = "0/${dataUser?.kalori}"
        }
        viewModel.getUserNutrients().observe(this){
            dataUserNutrients = it
            if(it==null){
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val formatted = current.format(formatter)
                val makslemak = String.format("%.1f", (dataUser.kalori*0.3)/9).toDouble()
                val maksgula = String.format("%.1f", (dataUser.kalori*0.2)/4).toDouble()
                var makskarbo:Double=0.0
                if(dataUser.gender == "Female"){
                    when {
                        dataUser.age<10 -> makskarbo=254.0
                        dataUser.age in 13..18 -> makskarbo = 275.0
                        dataUser.age in 19..29 -> makskarbo = 309.0
                        dataUser.age in 30..49 -> makskarbo = 323.0
                        dataUser.age in 50..64 -> makskarbo = 285.0
                        dataUser.age in 65..80 -> makskarbo = 285.0
                        dataUser.age > 80 -> makskarbo = 232.0
                    }
                }
                else if(dataUser.gender=="Male"){
                    when {
                        dataUser.age<10 -> makskarbo=254.0
                        dataUser.age in 10..12 -> makskarbo = 289.0
                        dataUser.age in 13..15 -> makskarbo = 340.0
                        dataUser.age in 16..18 -> makskarbo = 368.0
                        dataUser.age in 19..29 -> makskarbo = 375.0
                        dataUser.age in 30..49 -> makskarbo = 394.0
                        dataUser.age in 50..64 -> makskarbo = 349.0
                        dataUser.age in 65..80 -> makskarbo = 309.0
                        dataUser.age > 80 -> makskarbo = 248.0
                    }
                }
                val userNutrientsEntity = UserNutrientsEntity(dataUser.id,dataUser.kalori,0.0,makskarbo,0.0,maksgula,0.0,makslemak,0.0,formatted)
                viewModel.insertUserNutrients(userNutrientsEntity)
            }
            else if (it!=null){
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val formatted = current.format(formatter)
                var userNutrientsEntity = it
                if(it.tanggal!=formatted){
                    userNutrientsEntity.gula_consumed=0.0
                    userNutrientsEntity.kalori_consumed=0.0
                    userNutrientsEntity.karbo_consumed=0.0
                    userNutrientsEntity.lemak_consumed=0.0
                    userNutrientsEntity.tanggal=formatted
                }
                viewModel.updateUserNutrients(userNutrientsEntity)
            }
            ChangeData()
        }

        val fabAdd = binding.fabAdd
        fabAdd.setOnClickListener {
            intent = Intent(this, IngridientsList::class.java)
            startActivity(intent)
        }

        val bottomMenu = binding.bottomnavigationbar
        bottomMenu.selectedItemId=R.id.mHome
        bottomMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.mHome -> {
                    //Do Nothing cz this is the activity currently active
                }
                R.id.mShopList -> {
                    val intent = Intent(this,ShoppingListActivity::class.java)
                    finish()
                    startActivity(intent)
                }
                R.id.mSetting -> {
                    val intent = Intent(this,SettingActivity::class.java)
                    finish()
                    startActivity(intent)
                }
                R.id.mPerson -> {

                }
            }
           true
        }

        Log.d("DATABASE",dataUser.toString())
    }

    private fun obtainViewModel(activity: AppCompatActivity): UserViewModel {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(UserViewModel::class.java)
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

    fun ChangeData(){
        if(dataUserNutrients!=null){
            binding.txtDashboardKalori.text = dataUserNutrients!!.kalori_consumed.toString() + "/" + dataUserNutrients!!.maxKalori.toString()
            binding.txtDashboardGula.text = dataUserNutrients!!.gula_consumed.toString() + "/" + dataUserNutrients!!.maxGula.toString()
            binding.txtDashboardGaram.text = dataUserNutrients!!.karbo_consumed.toString() + "/" + dataUserNutrients!!.maxKarbo.toString()
            binding.txtDashboardLemak.text = dataUserNutrients!!.lemak_consumed.toString() + "/" + dataUserNutrients!!.maxLemak.toString()
            if(dataUserNutrients!!.kalori_consumed>dataUserNutrients!!.maxKalori){
                Glide.with(this).load(R.drawable.arrowup).into(binding.imgDashboardKalori)
                val Temp: Double = ((dataUserNutrients!!.kalori_consumed- dataUserNutrients!!.maxKalori)/dataUserNutrients!!.maxKalori) * 100
                binding.txtPersenKalori.text = Temp.toInt().toString() +"% Higher than daily limit"
            }
            else{
                Glide.with(this).load(R.drawable.arrowdown).into(binding.imgDashboardKalori)
                val Temp: Double = ((dataUserNutrients!!.kalori_consumed- dataUserNutrients!!.maxKalori)/dataUserNutrients!!.maxKalori) * 100
                binding.txtPersenKalori.text = Temp.toInt().toString() +"% Lower than daily limit"
            }
            if(dataUserNutrients!!.gula_consumed>dataUserNutrients!!.maxGula){
                Glide.with(this).load(R.drawable.arrowup).into(binding.imgDashboardGula)
                val Temp = ((dataUserNutrients!!.gula_consumed-dataUserNutrients!!.maxGula)/dataUserNutrients!!.maxGula) * 100
                binding.txtPersenGula.text = Temp.toInt().toString() +"% Higher than daily limit"
            }
            else{
                Glide.with(this).load(R.drawable.arrowdown).into(binding.imgDashboardGula)
                val Temp = ((dataUserNutrients!!.gula_consumed-dataUserNutrients!!.maxGula)/dataUserNutrients!!.maxGula) * 100
                binding.txtPersenGula.text = Temp.toInt().toString() +"% Lower than daily limit"
            }
            if(dataUserNutrients!!.karbo_consumed>dataUserNutrients!!.maxKarbo){
                Glide.with(this).load(R.drawable.arrowup).into(binding.imgDashboardGaram)
                val Temp = ((dataUserNutrients!!.karbo_consumed-dataUserNutrients!!.maxKarbo)/dataUserNutrients!!.maxKarbo) * 100
                binding.txtPersenGaram.text = Temp.toInt().toString() +"% Higher than daily limit"
            }
            else{
                Glide.with(this).load(R.drawable.arrowdown).into(binding.imgDashboardGaram)
                val Temp = ((dataUserNutrients!!.karbo_consumed-dataUserNutrients!!.maxKarbo)/dataUserNutrients!!.maxKarbo) * 100
                binding.txtPersenGaram.text = Temp.toInt().toString() +"% Lower than daily limit"
            }
            if(dataUserNutrients!!.lemak_consumed > dataUserNutrients!!.maxLemak){
                Glide.with(this).load(R.drawable.arrowup).into(binding.imgDashboardLemak)
                val Temp = ((dataUserNutrients!!.lemak_consumed-dataUserNutrients!!.maxLemak)/dataUserNutrients!!.maxLemak) * 100
                binding.txtPersenLemak.text = Temp.toInt().toString() +"% Higher than daily limit"
            }
            else{
                Glide.with(this).load(R.drawable.arrowdown).into(binding.imgDashboardLemak)
                val Temp = ((dataUserNutrients!!.lemak_consumed-dataUserNutrients!!.maxLemak)/dataUserNutrients!!.maxLemak) * 100
                binding.txtPersenLemak.text = Temp.toInt().toString() +"% Lower than daily limit"
            }
        }

    }

//    private val usersObserver = Observer<UserEntity> { users ->
//        dataUser=users
//        binding.textUserLoginHome.text= dataUser?.name
//    }
}