package com.gracielo.projectta.ui.login.forgotpassword

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityForgotPasswordBinding
import com.gracielo.projectta.ui.homepage.HomeActivity

class ForgotPasswordUsernameEmailActivity : AppCompatActivity() {

    private lateinit var binding :ActivityForgotPasswordBinding
    val apiServices = ApiServices()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSendOTP.setOnClickListener {
            binding.pbarForgotPassUsernameEmail.visibility = View.VISIBLE
            var email = binding.emailForgot.text.toString()
            var username = binding.usernameForgot.text.toString()
            if(email.isNotEmpty() && username.isNotEmpty()){
                apiServices.sendOTPForgotPass(username,email){
                    binding.pbarForgotPassUsernameEmail.visibility = View.INVISIBLE
                    if(it?.code==1){
                        val intentt= Intent(this, ForgotPasswordOTPActivity::class.java)
                        intentt.putExtra("username",username)
                        intentt.putExtra("email",email)
                        startActivity(intentt)
                        finishAffinity()
                    }else{
                        if(it?.code==-2){
                            Toast.makeText(this,"User Not Found",Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Toast.makeText(this,"Code: ${it?.code}, ${it?.message}",Toast.LENGTH_SHORT).show()
                        }

                    }
                }
            }
        }
    }
}