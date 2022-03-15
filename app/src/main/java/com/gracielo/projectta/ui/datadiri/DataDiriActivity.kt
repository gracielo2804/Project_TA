package com.gracielo.projectta.ui.datadiri

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gracielo.projectta.R
import com.gracielo.projectta.data.model.DataUser
import com.gracielo.projectta.data.model.DataUserProfile
import com.gracielo.projectta.data.source.local.entity.UserEntity
import com.gracielo.projectta.data.source.local.entity.UserNutrientsEntity
import com.gracielo.projectta.data.source.remote.network.ApiResponses
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityDataDiriBinding
import com.gracielo.projectta.ui.homepage.HomeActivity
import com.gracielo.projectta.viewmodel.UserViewModel
import com.gracielo.projectta.viewmodel.ViewModelFactory
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class DataDiriActivity : AppCompatActivity() {

    private lateinit var binding:ActivityDataDiriBinding
    private lateinit var viewModel :UserViewModel

    var kalori=0.0
    var height=0
    var weight=0
    var age=0
    lateinit var kaloriText : TextView
    var gender=""
    lateinit var id: String

    val apiServices= ApiServices()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDataDiriBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra("id")){
            id= intent.getStringExtra("id")!!
        }

        viewModel=obtainViewModel(this@DataDiriActivity)
        val pbar=binding.pbarDataDiri

        kaloriText = binding.txtKalori
        var fab=binding.fabNextToBM
        var heightText=binding.etHeight
        var weightText=binding.etWeight
        var ageText=binding.etUmur
        var genderField=binding.dropdownGenderSelect

        pbar.visibility=View.INVISIBLE
        val items = listOf("Male","Female")
        val adapter = ArrayAdapter(this, R.layout.item_gender, items)
        genderField.setAdapter(adapter)

        heightText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(count>0){
                    height=heightText.text.toString().toInt()
                    hitungkalori()
                }
                if(count==0){
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        weightText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(count>0){
                    weight=weightText.text.toString().toInt()
                    hitungkalori()
                }
                if(count==0){
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        ageText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(count>0){
//                    binding.txtInputTinggiBadan.error = null
//                    binding.txtInputTinggiBadan.helperText = ""
                    age=ageText.text.toString().toInt()
                    hitungkalori()
                }
                if(count==0){
//                    binding.txtInputTinggiBadan.helperText = "*Required"
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        genderField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(count>0){
                    gender=genderField.text.toString()
                    hitungkalori()

                }
                if(count==0){
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        fab.setOnClickListener {
            if(heightText.text!!.isNotEmpty() && weightText.text!!.isNotEmpty() && ageText.text!!.isNotEmpty() && genderField.text!!.isNotEmpty()){

                pbar.visibility= View.VISIBLE
                pbar.isIndeterminate=true
                val dataDiri= DataUserProfile(
                    id,height,weight,gender,kalori,age
                )

                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val formatted = current.format(formatter)
                val makslemak = String.format("%.1f", (kalori*0.3)/90).toDouble()
                val maksgula = String.format("%.1f", (kalori*0.2)/4).toDouble()
                var makskarbo:Double=0.0
                if(gender == "Female"){
                    when {
                        age<10 -> makskarbo=254.0
                        age in 13..18 -> makskarbo = 275.0
                        age in 19..29 -> makskarbo = 309.0
                        age in 30..49 -> makskarbo = 323.0
                        age in 50..64 -> makskarbo = 285.0
                        age in 65..80 -> makskarbo = 285.0
                        age > 80 -> makskarbo = 232.0
                    }
                }
                else if(gender=="Male"){
                    when {
                        age<10 -> makskarbo=254.0
                        age in 10..12 -> makskarbo = 289.0
                        age in 13..15 -> makskarbo = 340.0
                        age in 16..18 -> makskarbo = 368.0
                        age in 19..29 -> makskarbo = 375.0
                        age in 30..49 -> makskarbo = 394.0
                        age in 50..64 -> makskarbo = 349.0
                        age in 65..80 -> makskarbo = 309.0
                        age > 80 -> makskarbo = 248.0
                    }
                }

                val userNutrientsEntity = UserNutrientsEntity(id,kalori,0.0,makskarbo,0.0,maksgula,0.0,makslemak,0.0,formatted)
                apiServices.addUserProfileData(dataDiri){
                    if(it?.code==1){
                        var dataUserProfile:DataUserProfile? = null
                        var dataUser: DataUser? = null
                        apiServices.getUserProfileData(id){ ApiResponses ->
                            if(ApiResponses?.code==1){
                                Log.d("DataDiri","Masuk DataUserProfile")
                                dataUserProfile=ApiResponses.dataUserProfile
                            }
                        }
                        apiServices.getUserDataByID(id){ApiResponses ->
                            if(ApiResponses?.code==1){
                                Log.d("DataDiri","Masuk DataUser")
                                dataUser=ApiResponses.dataUser
                            }
                        }
                        Handler(Looper.getMainLooper()).postDelayed(
                            {
                                // This method will be executed once the timer is over
                                Log.d("DataDiri",dataUserProfile.toString() + " - " +dataUser.toString())
                                if(dataUserProfile!=null && dataUser!=null){
                                    val intentKirim = Intent(this,HomeActivity::class.java)
                                    val userEntity = UserEntity(
                                        dataUser!!.id,
                                        dataUser!!.name,
                                        dataUser!!.username,
                                        dataUser!!.email,
                                        dataUserProfile!!.height,
                                        dataUserProfile!!.weight,
                                        dataUserProfile!!.gender,
                                        dataUserProfile!!.kalori,
                                        dataUserProfile!!.age
                                    )
                                    viewModel.insert(userEntity)
                                    viewModel.insertUserNutrients(userNutrientsEntity)
                                    intentKirim.putExtra("dataUser",dataUser)
                                    intentKirim.putExtra("dataUserProfile",dataUserProfile)
                                    pbar.visibility= View.INVISIBLE
                                    startActivity(intentKirim)
                                }
                            },
                            2000 // value in milliseconds
                        )
                    }

                }
            }
        }

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.gender_menu, menu)
        return true
    }
    fun hitungkalori(){
        if(weight>0 && weight>0 && age>0 && gender!=""){
            if(gender=="Male"){
                kalori = 66 + (13.7 * weight) + (5 * height) + (6.78 * age)
            }
            else if(gender=="Female"){
                kalori = 655 + (9.6 * weight) + (1.8 * height) + (4.7 * age)
            }
            Toast.makeText(baseContext, kalori.toString(),Toast.LENGTH_SHORT).show()
        }
        changeTextKalori()
    }
    fun changeTextKalori(){
        kaloriText.text=kalori.toString()
    }

    private fun obtainViewModel(activity: AppCompatActivity): UserViewModel {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(UserViewModel::class.java)
    }

}