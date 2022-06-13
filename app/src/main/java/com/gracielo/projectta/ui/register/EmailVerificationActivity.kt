package com.gracielo.projectta.ui.register

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gracielo.projectta.data.model.DataUser
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityEmailVerificationBinding
import com.gracielo.projectta.ui.datadiri.DataDiriActivity
import com.gracielo.projectta.ui.homepage.HomeActivity

class EmailVerificationActivity : AppCompatActivity() {

    private lateinit var  binding:ActivityEmailVerificationBinding
    private lateinit var textEmail : TextView
    private lateinit var  buttonFinish : Button
    private lateinit var  dataUser: DataUser
    val apiServices= ApiServices()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        textEmail=binding.txtEmail
        buttonFinish=binding.btnFinishVerification

        if(intent.hasExtra("dataUser"))dataUser = intent.getParcelableExtra("dataUser")!!
        textEmail.text = dataUser.email
        apiServices.sendVerificationEmail(dataUser.id,dataUser.email,dataUser.name){
            if(it?.code!=1){
                Toast.makeText(this,"Email Verification Error Please Try Again",Toast.LENGTH_SHORT).show()
            }
        }


        buttonFinish.setOnClickListener {
            var dataUserResponse:DataUser?
            apiServices.getUserDataByID(dataUser.id){
                if(it?.code==1){
                    dataUserResponse=it.dataUser
                    if(dataUserResponse?.emailVerified=="0"){
                        Toast.makeText(this,"Email Not Verified",Toast.LENGTH_SHORT).show()
                    }
                    else if(dataUserResponse?.emailVerified=="1"){
                        apiServices.getUserProfileData(it.dataUser!!.id){ ApiResponses ->
                            if(ApiResponses?.code==1){
                                var intentKirim = Intent(this, HomeActivity::class.java)
                                intentKirim.putExtra("dataUser", it.dataUser)
                                intentKirim.putExtra("dataUserProfile", it.dataUserProfile)
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
            }
        }



    }
}