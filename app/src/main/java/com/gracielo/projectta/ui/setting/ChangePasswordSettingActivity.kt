package com.gracielo.projectta.ui.setting

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gracielo.projectta.data.source.local.entity.UserEntity
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityChangePasswordSettingBinding
import com.gracielo.projectta.util.FunHelper
import com.gracielo.projectta.util.FunHelper.observeOnce
import com.gracielo.projectta.viewmodel.UserViewModel

class ChangePasswordSettingActivity : AppCompatActivity() {

    private lateinit var binding:ActivityChangePasswordSettingBinding
    val apiServices = ApiServices()
    private lateinit var viewModel: UserViewModel
    private val helper = FunHelper
    lateinit var dataUser : UserEntity

    var passCurPassword=false

    var passPassword=false
    var passConpassword=false
    var checkConPassword=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dataUser = UserEntity("1","1","1","1","0","",1,1,"1",1.1,1,1)
        viewModel = helper.obtainUserViewModel(this)
        viewModel.getUser().observeOnce(this){
            dataUser = it
        }

        var newPassword=binding.newPasswordChangePass
        var newConPassword=binding.newConPasswordChangePass

        newConPassword.setOnFocusChangeListener { view, b ->
            if(!b){
                if(newConPassword.text.toString().isEmpty()){
                    passConpassword= false
                }
                else{
                    binding.txtFieldNewConPasswordChange.error=null
                    passConpassword=true
                    if(passPassword){
                        if(newPassword.text.toString()!=newConPassword.text.toString()){
                            checkConPassword=false
                            binding.txtFieldNewConPasswordChange.error = "Password And Confirmation Password Must Be The Same"
                            binding.txtFieldNewPasswordChange.error = "Password And Confirmation Password Must Be The Same"
                        }
                        else{
                            binding.txtFieldNewConPasswordChange.error=null
                            checkConPassword=true
                        }
                    }
                }
            }
        }
        newPassword.setOnFocusChangeListener { view, b ->
            if(!b){
                if(newPassword.text.toString().isEmpty()){
                    passPassword= false
                }
                else{
                    binding.txtFieldNewPasswordChange.error=null
                    passPassword=true
                    if(passConpassword){
                        if(newPassword.text.toString()!=newConPassword.text.toString()){
                            checkConPassword=false
                            binding.txtFieldNewConPasswordChange.error = "Password And Confirmation Password Must Be The Same"
                            binding.txtFieldNewPasswordChange.error = "Password And Confirmation Password Must Be The Same"
                        }
                        else{
                            binding.txtFieldNewConPasswordChange.error=null
                            checkConPassword=true
                        }
                    }
                }
            }
        }

        binding.currentPasswordChangePass.setOnFocusChangeListener { view, b ->
            if(!b){
                if (binding.currentPasswordChangePass.text.toString().isEmpty()){
                    passCurPassword = false
                    binding.txtFieldCurrentPasswordChange.error = "This Field Must Be Filled"
                }
                else{
                    passCurPassword = true
                    binding.txtFieldCurrentPasswordChange.error = null
                }
            }
        }

        binding.btnSubmitChangePassword.setOnClickListener {
            if(checkConPassword && passCurPassword && passConpassword && passPassword){
                binding.btnSubmitChangePassword.isClickable=false
                binding.pbarChangePass.visibility = View.VISIBLE
                apiServices.changePassword(
                    binding.currentPasswordChangePass.text.toString(),
                    binding.newPasswordChangePass.text.toString(),
                    dataUser.id
                ){
                    binding.btnSubmitChangePassword.isClickable=true
                    binding.pbarChangePass.visibility = View.GONE
                    if(it?.code==1){
                        binding.txtFieldCurrentPasswordChange.error = null
                        Toast.makeText(this,"Password Updated",Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    else if(it?.code==-2){
                        binding.txtFieldCurrentPasswordChange.error = "Wrong Password"
//                            Toast.makeText(this,"Incorrect Password",Toast.LENGTH_SHORT).show()
                    }
                }
                Handler(Looper.getMainLooper()).postDelayed({

                },1000)
            }
        }
    }
}