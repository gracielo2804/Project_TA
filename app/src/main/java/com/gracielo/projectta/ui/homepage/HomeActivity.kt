package com.gracielo.projectta.ui.homepage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gracielo.projectta.R
import com.gracielo.projectta.data.model.DataUser
import com.gracielo.projectta.data.model.DataUserProfile
import com.gracielo.projectta.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    var dataUser:DataUser? = null
    var dataUserProfile:DataUserProfile?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(intent.hasExtra("dataUser")){
            dataUser=intent.getParcelableExtra("dataUser")
        }
        if(intent.hasExtra("dataUserProfile")){
            dataUserProfile=intent.getParcelableExtra("dataUserProfile")
        }
        binding.textUserLoginHome.text=dataUser?.name
    }
}