package com.gracielo.projectta.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gracielo.projectta.data.model.DataUser
import com.gracielo.projectta.data.model.DataUserProfile
import com.gracielo.projectta.data.source.local.entity.UserEntity

import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityLoginBinding
import com.gracielo.projectta.ui.datadiri.DataDiriActivity
import com.gracielo.projectta.ui.homepage.HomeActivity
import com.gracielo.projectta.ui.register.EmailVerificationActivity
import com.gracielo.projectta.ui.register.RegisterActivity
import com.gracielo.projectta.viewmodel.UserViewModel
import com.gracielo.projectta.viewmodel.ViewModelFactory

class TestLoginActivity : AppCompatActivity() {
//
    private lateinit var binding: ActivityLoginBinding
    val apiServices= ApiServices()
//
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel =obtainViewModel(this@TestLoginActivity)
        viewModel.getUser()?.observe(this,usersObserver)

        val username= binding.usernameLog
        val password= binding.passwordLog
        val btnRegister= binding.btnToRegister
        val btnLogin = binding.login

//        //Test Image
//        val image = binding.testImage
//        Glide.with(this)
//        .load("http://192.168.0.109/P10-myservice_php/myservice/Images/Test/foto1.jpeg") // image url
////        .error(R.drawable.imagenotfound)  // any image in case of error
//        .override(200, 200) // resizing
//        .centerCrop()
//        .into(image)

    btnRegister.setOnClickListener {
            val intentKirim = Intent(this, RegisterActivity::class.java)
            intentKirim.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(intentKirim)
        }
        btnLogin.setOnClickListener {
//                Log.d("data",userReg.toString())
            apiServices.Login(username.text.toString(),password.text.toString()){
                if (it?.code==1){
                    Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show()
                    Log.d("dataReturn", it.dataUser?.id.toString())
                    if(it.dataUser?.emailVerified=="0"){
                        val intent = Intent(this, EmailVerificationActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                        intent.putExtra("dataUser",it.dataUser)
                        startActivity(intent)
                    }
                    else if(it.dataUser?.emailVerified=="1"){
                        apiServices.getUserProfileData(it.dataUser.id){ ApiResponses ->
                            if(ApiResponses?.code==1){
                                val datauser : DataUser = it.dataUser
                                val dataUserProfile : DataUserProfile = ApiResponses.dataUserProfile!!
                                val userEntity = UserEntity(
                                    datauser.id,
                                    datauser.name,
                                    datauser.username,
                                    datauser.email,
                                    dataUserProfile.height,
                                    dataUserProfile.weight,
                                    dataUserProfile.gender,
                                    dataUserProfile.kalori,
                                    dataUserProfile.age
                                )
                                viewModel.insert(userEntity)
                                var intentKirim = Intent(this, HomeActivity::class.java)
                                intentKirim.putExtra("dataUser", it.dataUser)
                                intentKirim.putExtra("dataUserProfile", ApiResponses.dataUserProfile)
                                startActivity(intentKirim)
                            }
                            else if(ApiResponses?.code==2){
                                var intentKirim = Intent(this, DataDiriActivity::class.java)
                                intentKirim.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                                intentKirim.putExtra("id", it.dataUser.id)
                                startActivity(intentKirim)
                            }
                        }
                    }

                }
                else{
                    Toast.makeText(this, it?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
    private fun obtainViewModel(activity: AppCompatActivity): UserViewModel {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(UserViewModel::class.java)
    }
    private val usersObserver = Observer<UserEntity> { users ->
        if (users != null) {
            val intentt= Intent(this,HomeActivity::class.java)
            intentt.putExtra("datauserentity",users)
            startActivity(intentt)
        }
    }
}