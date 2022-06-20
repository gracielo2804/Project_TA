package com.gracielo.projectta.ui.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gracielo.projectta.data.model.AddUsers
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    val apiServices=ApiServices()
    var checkUsername=true
    var passUsername=false
    var passPassword=false
    var passConpassword=false
    var checkConPassword=false
    var passEmail=false
    var passNama=false

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

        username.setOnFocusChangeListener{ view, b->
            if(!b){
                if(username.text.toString().isEmpty()){
                    passUsername=false
                }
                else{
                    passUsername=true
                    binding.txtFieldUsernameReg.error=null
                    apiServices.checkUsername(username.text.toString()){
                        if(it?.code==1){
                            checkUsername=true
                            binding.txtFieldUsernameReg.error=null
                        }
                        else if(it?.code==-2){
                            checkUsername=false
                            binding.txtFieldUsernameReg.error = "Username Has Been Taken"
                        }
                    }
                }
            }
        }
        password.setOnFocusChangeListener { view, b ->
            if(!b){
                if(password.text.toString().isEmpty()){
                    passPassword=false
                }else{
                    binding.txtFieldPasswordReg.error=null
                    passPassword=true
                    if(passConpassword){
                        if(password.text.toString()!=conpassword.text.toString()){
                            checkConPassword=false
                            binding.txtFieldConPasswordReg.error = "Password And Confirmation Password Must Be The Same"
                            binding.txtFieldConPasswordReg.error = "Password And Confirmation Password Must Be The Same"
                        }
                        else{
                            binding.txtFieldConPasswordReg.error = null
                            checkConPassword=true
                        }
                    }
                }

            }
        }
        conpassword.setOnFocusChangeListener { view, b ->
            if(!b){
                if(conpassword.text.toString().isEmpty()){
                    passConpassword=false
                }else{
                    passConpassword=true
                    binding.txtFieldConPasswordReg.error=null
                    if(passPassword){
                        if(password.text.toString()!=conpassword.text.toString()){
                            checkConPassword=false
                            binding.txtFieldConPasswordReg.error = "Password And Confirmation Password Must Be The Same"
                            binding.txtFieldConPasswordReg.error = "Password And Confirmation Password Must Be The Same"
                        }
                        else{
                            binding.txtFieldConPasswordReg.error = null
                            checkConPassword=true
                        }
                    }
                }
            }
        }
        email.setOnFocusChangeListener{view, b->
            if(!b){
                if(email.text.toString().isEmpty()){
                    passEmail=false
                }else{
                    binding.txtFieldEmailReg.error=null
                    passEmail=true
                }
            }
        }
        nama.setOnFocusChangeListener{view, b->
            if(nama.text.toString().isEmpty()){
                passNama=false
            }else{
                binding.txtFieldNameReg.error=null
                passNama=true
            }
        }

        btnAdd.setOnClickListener{
            nama.clearFocus()
            username.clearFocus()
            password.clearFocus()
            conpassword.clearFocus()
            email.clearFocus()
            if(checkUsername && checkConPassword && passUsername && passPassword && passConpassword && passEmail &&passNama){
                val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
                if(Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
                    val userReg=AddUsers(
                        function = "adduser",
                        id=null,
                        username = username.text.toString(),
                        password = password.text.toString(),
                        email = email.text.toString(),
                        nama = nama.text.toString(),
                    )
                    var statusUsername=false
                    apiServices.checkUsername(username.text.toString()){
                        if(it?.code==1){
                            statusUsername=true
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
                    }
                }
                else{
                    binding.txtFieldEmailReg.error="Email Not Valid"
                }

            }
            else{
                if(!passUsername){
                    binding.txtFieldUsernameReg.error="This Field Must Be Filled"
                }
                if(!passPassword){
                    binding.txtFieldPasswordReg.error="This Field Must Be Filled"
                }
                if(!passConpassword){
                    binding.txtFieldConPasswordReg.error="This Field Must Be Filled"
                }
                if(!passEmail){
                    binding.txtFieldEmailReg.error="This Field Must Be Filled"
                }
                if(!passNama){
                    binding.txtFieldNameReg.error="This Field Must Be Filled"
                }
            }
        }

    }
}