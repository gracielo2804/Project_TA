package com.gracielo.projectta.ui.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.gracielo.projectta.R
import com.gracielo.projectta.data.source.local.entity.UserEntity
import com.gracielo.projectta.data.source.local.entity.UserNutrientsEntity
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityEditProfileBinding
import com.gracielo.projectta.util.FunHelper
import com.gracielo.projectta.util.FunHelper.observeOnce
import com.gracielo.projectta.viewmodel.UserViewModel

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var userViewModel: UserViewModel
    val helper = FunHelper
    private lateinit var dataUser: UserEntity
    private var dataUserNutrient :UserNutrientsEntity?=null
    var apiServices = ApiServices()
    var statusObserve = false
    var tipeActivities = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val itemsDropdown = listOf("Simple Activities, No Exercises", "Light Active with Sport or Exercises 1-3 days a week"
            ,"Moderately Active with Sport or Exercises 3-5 days a week","Hard Sport or Exercises 6 or 7 days a week",
            "Doing physical job or an Athlete do very hard Exercises or sport")
        val adapterDropdown = ArrayAdapter(this, R.layout.list_item_dropdown_search_shopping_list, itemsDropdown)

        val dropdown = binding.dropDownListActivitiesAutoTextEdit
        (dropdown as? AutoCompleteTextView)?.setAdapter(adapterDropdown)

        dataUser = UserEntity("1","1","1","1","0","",1,1,"1",1.1,1,1)
        userViewModel = helper.obtainUserViewModel(this)
        userViewModel.getUser().observeOnce(this){
            dataUser=it
            binding.etUmurEdit.setText(dataUser.age.toString())
            binding.etHeightEdit.setText(dataUser.height.toString())
            binding.etWeightEdit.setText(dataUser.weight.toString())
            dropdown.setText(adapterDropdown.getItem(dataUser.activities),false)
//            binding.txtKaloriEdit.text = dataUser.kalori.toString()
            tipeActivities = dataUser.activities
        }
        userViewModel.getUserNutrients().observeOnce(this){
            dataUserNutrient=it
        }



        binding.btnEditAge.setOnClickListener {
            if(binding.btnEditAge.text=="Edit"){
                binding.btnEditAge.text="Simpan"
                binding.txtInputUmurEdit.isEnabled=true
            }
            else if(binding.btnEditAge.text=="Simpan"){
                dataUser.age=binding.etUmurEdit.text.toString().toInt()
                updateDataUser()
                userViewModel.update(dataUser)
                apiServices.updateUser(dataUser){}
                binding.btnEditAge.text="Edit"
                binding.txtInputUmurEdit.isEnabled=false
            }
        }

        binding.btnEditHeight.setOnClickListener {
            if(binding.btnEditHeight.text=="Edit"){
                binding.btnEditHeight.text="Simpan"
                binding.txtInputHeightEdit.isEnabled=true
            }
            else if(binding.btnEditHeight.text=="Simpan"){
                dataUser.height=binding.etHeightEdit.text.toString().toInt()
                updateDataUser()
                userViewModel.update(dataUser)
                apiServices.updateUser(dataUser){}
                binding.btnEditHeight.text="Edit"
                binding.txtInputHeightEdit.isEnabled=false
            }
        }
        binding.btnEditWeight.setOnClickListener {
            if(binding.btnEditWeight.text=="Edit"){
                binding.btnEditWeight.text="Simpan"
                binding.txtInputWeightEdit.isEnabled=true
            }
            else if(binding.btnEditWeight.text=="Simpan"){
                dataUser.weight=binding.etWeightEdit.text.toString().toInt()
                updateDataUser()
                userViewModel.update(dataUser)
                apiServices.updateUser(dataUser){}
                binding.btnEditWeight.text="Edit"
                binding.txtInputWeightEdit.isEnabled=false
            }
        }
        binding.btnChangePasswordPage.setOnClickListener {
            val intentt = Intent(this,ChangePasswordSettingActivity::class.java)
            startActivity(intentt)
        }


        Handler(Looper.getMainLooper()).postDelayed({

        },500)


        dropdown.onItemClickListener = AdapterView.OnItemClickListener{
                adapterView, view, i, l ->
            tipeActivities=i
            dataUser.activities = tipeActivities
            updateDataUser()

        }
    }

    fun updateDataUser(){
        var kalori = 0.0
        var makskarbo:Double=0.0

        if(dataUser.gender=="Male"){
            kalori = 66 + (13.7 * dataUser.weight) + (5 * dataUser.height) - (6.78 * dataUser.age)
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
        else if(dataUser.gender=="Female"){
            kalori = 66 + (13.7 * dataUser.weight) + (5 * dataUser.height) - (6.78 * dataUser.age)
            when {
                dataUser.age<13 -> makskarbo=254.0
                dataUser.age in 13..18 -> makskarbo = 275.0
                dataUser.age in 19..29 -> makskarbo = 309.0
                dataUser.age in 30..49 -> makskarbo = 323.0
                dataUser.age in 50..64 -> makskarbo = 285.0
                dataUser.age in 65..80 -> makskarbo = 285.0
                dataUser.age > 80 -> makskarbo = 232.0
            }
        }
        if(tipeActivities==0)kalori*=1.2
        else if(tipeActivities==1)kalori*=1.375
        else if(tipeActivities==2)kalori*=1.55
        else if(tipeActivities==3)kalori*=1.725
        else if(tipeActivities==4)kalori*=1.9
        kalori = String.format("%.1f", kalori).toDouble()
        val makslemak = String.format("%.1f", (kalori*0.3)/9).toDouble()
        val maksgula = String.format("%.1f", (kalori*0.1)/4).toDouble()
        val maksProtein = dataUser.weight*1.5
//        binding.txtKaloriEdit.text = kalori.toString()




        dataUser.kalori=kalori
        dataUserNutrient?.maxKarbo=makskarbo
        dataUserNutrient?.maxGula=maksgula
        dataUserNutrient?.maxLemak=makslemak
        dataUserNutrient?.maxKalori=kalori
        dataUserNutrient?.maxProtein=maksProtein
        userViewModel.update(dataUser)
        userViewModel.updateUserNutrients(dataUserNutrient!!)
        apiServices.updateUser(dataUser){}




    }

}