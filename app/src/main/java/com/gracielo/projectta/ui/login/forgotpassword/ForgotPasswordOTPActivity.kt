package com.gracielo.projectta.ui.login.forgotpassword

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityForgotPasswordBinding
import com.gracielo.projectta.databinding.ActivityForgotPasswordOtpActivityBinding
import com.gracielo.projectta.ui.login.TestLoginActivity

class ForgotPasswordOTPActivity : AppCompatActivity() {

    private lateinit var binding : ActivityForgotPasswordOtpActivityBinding
    val apiServices = ApiServices()
    var username =""
    var email=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordOtpActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(intent.hasExtra("username"))username=intent.getStringExtra("username").toString()
        if(intent.hasExtra("email"))email=intent.getStringExtra("email").toString()
        binding.txtEmail.text=email
        binding.btnVerifyCode.setOnClickListener {
            binding.pbarForgotPassCode.visibility = View.VISIBLE
            val code = binding.otpForgotpass.text.toString()
            apiServices.verifyResetOTP(username,email,code){
                binding.pbarForgotPassCode.visibility = View.INVISIBLE
                if(it?.code==1){
                    Toast.makeText(this,"Verification Successful", Toast.LENGTH_SHORT).show()
                    val intentt= Intent(this, ForgotPasswordInputPasswordActivity::class.java)
                    intentt.putExtra("username",username)
                    intentt.putExtra("email",email)
                    startActivity(intentt)
                    finishAffinity()
                }
                else{
                    if(it?.code==-3){
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
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
                finishAffinity()
                val intentt= Intent(this, TestLoginActivity::class.java)
                startActivity(intentt)
//                viewModel.getUser()?.removeObserver(){}

            } // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.no, null)
//            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }
}