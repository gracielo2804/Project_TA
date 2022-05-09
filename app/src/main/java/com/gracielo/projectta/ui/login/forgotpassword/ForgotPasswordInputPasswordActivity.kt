package com.gracielo.projectta.ui.login.forgotpassword

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityForgotPasswordInputPasswordBinding
import com.gracielo.projectta.ui.login.TestLoginActivity

class ForgotPasswordInputPasswordActivity : AppCompatActivity() {

    private lateinit var binding:ActivityForgotPasswordInputPasswordBinding
    var passPassword=false
    var passConpassword=false
    var checkPassword=false
    var username =""
    var email=""
    val apiServices=ApiServices()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordInputPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(intent.hasExtra("username"))username=intent.getStringExtra("username").toString()
        if(intent.hasExtra("email"))email=intent.getStringExtra("email").toString()

        val txtFPass= binding.txtFieldPassForgot
        val txtPass= binding.passForgot
        val txtFConPass= binding.txtFieldConPassForgot
        val txtConPass= binding.conPassForgot

        txtPass.setOnFocusChangeListener { view, b ->
            if(!b){
                if(txtPass.text.toString().isEmpty()){
                    passPassword=false
                }else{
                    Log.d("password","${txtPass.text.toString()} - ${txtConPass.text.toString()}")
                    txtFPass.error=null
                    passPassword=true
                    if(passConpassword){
                        if(txtPass.text.toString()!=txtConPass.text.toString()){
                            checkPassword=false
                            txtFConPass.error = "Password And Confirmation Password Must Be The Same"
                            txtFPass.error = "Password And Confirmation Password Must Be The Same"
                        }
                        else{
                            txtFConPass.error = null
                            checkPassword=true
                        }
                    }
                }

            }
        }
        txtConPass.setOnFocusChangeListener { view, b ->
            if(!b){
                if(txtConPass.text.toString().isEmpty()){
                    passConpassword=false
                }else{
                    txtFConPass.error=null
                    passConpassword=true
                    if(passPassword){
                        Log.d("password","${txtPass.text.toString()} - ${txtConPass.text.toString()}")
                        if(txtPass.text.toString()!=txtConPass.text.toString()){
                            checkPassword=false
                            txtFConPass.error = "Password And Confirmation Password Must Be The Same"
                            txtFPass.error = "Password And Confirmation Password Must Be The Same"
                        }
                        else{
                            txtFConPass.error = null
                            checkPassword=true
                        }
                    }
                }

            }
        }

        binding.btnResetPassword.setOnClickListener {
            binding.pbarForgotPassUsernameEmail.visibility= View.VISIBLE
            txtFPass.clearFocus()
            txtFConPass.clearFocus()
            if(checkPassword){
                txtFPass.error=null
                txtFConPass.error=null
                apiServices.resetPassword(username,email,txtConPass.text.toString()){
                    binding.pbarForgotPassUsernameEmail.visibility= View.INVISIBLE
                    if(it?.code==1){
                        Toast.makeText(this,"Password Updated", Toast.LENGTH_SHORT).show()
                        val intentt= Intent(this, TestLoginActivity::class.java)
                        startActivity(intentt)
                        finishAffinity()
                    }
                    else{
                        Toast.makeText(this,"Code: ${it?.code}, ${it?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Cancel Reset?")
            .setMessage("Are you sure you want to cancel the reset password process ?") // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton("Yes"
            ) { _, _ ->
//                Toast.makeText(this,"Successfully Logout",Toast.LENGTH_SHORT).show()
                val intentt= Intent(this, TestLoginActivity::class.java)
                startActivity(intentt)
                finishAffinity()
//                viewModel.getUser()?.removeObserver(){}

            } // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.no, null)
//            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }
}