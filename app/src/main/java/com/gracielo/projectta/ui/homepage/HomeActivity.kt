package com.gracielo.projectta.ui.homepage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gracielo.projectta.R
import com.gracielo.projectta.data.model.DataUser
import com.gracielo.projectta.data.model.DataUserProfile
import com.gracielo.projectta.data.source.local.entity.UserEntity
import com.gracielo.projectta.databinding.ActivityHomeBinding
import com.gracielo.projectta.viewmodel.UserViewModel
import com.gracielo.projectta.viewmodel.ViewModelFactory

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
//    var dataUser:DataUser? = null
    var dataUserProfile:DataUserProfile?=null
    private var dataUser :UserEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        if(intent.hasExtra("dataUser")){
//            dataUser=intent.getParcelableExtra("dataUser")
//        }
        if(intent.hasExtra("dataUserProfile")){
            dataUserProfile=intent.getParcelableExtra("dataUserProfile")
            Log.d("DATAUSER",dataUserProfile.toString())

        }
        val viewModel = obtainViewModel(this@HomeActivity)
        viewModel.getUser()?.observe(this){
            dataUser=it
            binding.textUserLoginHome.text= dataUser?.name
            binding.txtDashboardKalori.text = "0/${dataUser?.kalori.toString()}"
            Log.d("DATABASE",dataUser.toString())
        }

        Log.d("DATABASE",dataUser.toString())
    }

    private fun obtainViewModel(activity: AppCompatActivity): UserViewModel {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(UserViewModel::class.java)
    }

//    private val usersObserver = Observer<UserEntity> { users ->
//        dataUser=users
//        binding.textUserLoginHome.text= dataUser?.name
//    }
}