package com.gracielo.projectta.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.gracielo.projectta.data.model.AddUsers
import com.gracielo.projectta.data.model.DataUser
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    val apiServices=ApiServices()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email=binding.emailReg
        val password=binding.passwordReg
        val conpassword=binding.conpasswordReg
        val username=binding.usernameReg
        val nama=binding.nameReg
        val btnAdd=binding.btnReqister

        btnAdd.setOnClickListener{
            if(password.text.toString() == conpassword.text.toString()){
                val userReg=AddUsers(
                    function = "adduser",
                    id=null,
                    username = username.text.toString(),
                    password = username.text.toString(),
                    email = email.text.toString(),
                    nama = nama.text.toString(),
                )
                var statusUsername=false
                apiServices.checkUsername(username.text.toString()){
                    if(it?.code==1){
                        statusUsername=true
                    }
                }
                if(statusUsername){
                    apiServices.addUser(userReg){
                        Log.d("data",it.toString())
                        if (it?.code==1){
                            Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show()
                            val dataUser = it.dataUser

                            intent = Intent(this,EmailVerificationActivity::class.java)
                            intent.putExtra("dataUser",dataUser)
                            startActivity(intent)
                        }
                        else{
                            Toast.makeText(this, "Error Registering new User", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    Toast.makeText(this, "Username Already Taken", Toast.LENGTH_SHORT).show()
                }
//                Log.d("data",userReg.toString())

            }
            else{
                Toast.makeText(this, "Password and Password Confirmation doesn't match", Toast.LENGTH_SHORT).show()
            }

        }

    }
}