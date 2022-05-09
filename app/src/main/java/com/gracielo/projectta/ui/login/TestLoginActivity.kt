package com.gracielo.projectta.ui.login

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gracielo.projectta.apriori.DataIterator
import com.gracielo.projectta.apriori.NamedItem
import com.gracielo.projectta.data.model.DataUser
import com.gracielo.projectta.data.model.DataUserProfile
import com.gracielo.projectta.data.model.nutrientsHistory.NutrientDataHistory
import com.gracielo.projectta.data.source.local.entity.UserEntity
import com.gracielo.projectta.data.source.local.entity.UserNutrientsEntity
import com.gracielo.projectta.data.source.remote.network.ApiConfig
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityLoginBinding
import com.gracielo.projectta.ui.admin.AdminHomeActivity
import com.gracielo.projectta.ui.datadiri.DataDiriActivity
import com.gracielo.projectta.ui.homepage.HomeActivity
import com.gracielo.projectta.ui.login.forgotpassword.ForgotPasswordOTPActivity
import com.gracielo.projectta.ui.login.forgotpassword.ForgotPasswordUsernameEmailActivity
import com.gracielo.projectta.ui.register.EmailVerificationActivity
import com.gracielo.projectta.ui.register.RegisterActivity
import com.gracielo.projectta.viewmodel.UserViewModel
import com.gracielo.projectta.viewmodel.ViewModelFactory
import com.jakewharton.threetenabp.AndroidThreeTen
import de.mrapp.apriori.Apriori
import de.mrapp.apriori.Sorting
import okhttp3.ResponseBody
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class TestLoginActivity : AppCompatActivity() {
//
    private lateinit var binding: ActivityLoginBinding
    val apiServices= ApiServices()

//
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiServices.tempFuncUpdateStatus{  }
        binding.pbarLogin.visibility= View.INVISIBLE

        val viewModel =obtainViewModel(this@TestLoginActivity)
        viewModel.getUser().observeOnce(this){users->
            if (users != null) {
                binding.pbarLogin.visibility= View.VISIBLE
                viewModel.delete(users)
                var dataUser:DataUser
                var dataUserProfile:DataUserProfile
                apiServices.getUserDataByID(users.id){
                    if(it?.code==1){
                        dataUser=it.dataUser!!
                        apiServices.getUserProfileData(dataUser.id){msg->
                            if(msg?.code==1){
                                dataUserProfile=msg.dataUserProfile!!
                                val userEntity = UserEntity(
                                    dataUser.id,
                                    dataUser.name,
                                    dataUser.username,
                                    dataUser.email,
                                    dataUser.tipe,
                                    dataUser.expired,
                                    dataUserProfile.height,
                                    dataUserProfile.weight,
                                    dataUserProfile.gender,
                                    dataUserProfile.kalori,
                                    dataUserProfile.age,
                                    dataUserProfile.activities
                                )
                                viewModel.insert(userEntity)
                                val intentt= Intent(this,HomeActivity::class.java)
                                startActivity(intentt)
                            }
                        }
                    }
                }
            }
        }
        binding.txtForgotPass.setOnClickListener {
            val intentt= Intent(this, ForgotPasswordUsernameEmailActivity::class.java)
            startActivity(intentt)
        }

        AndroidThreeTen.init(this)
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatted = current.format(formatter)

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
            binding.pbarLogin.visibility= View.VISIBLE
//                Log.d("data",userReg.toString())
            apiServices.Login(username.text.toString(),password.text.toString()){
                if (it?.code==1){
                    if(it.message=="Admin Login"){
                        Toast.makeText(this, "Admin Login Successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, AdminHomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                        startActivity(intent)
                    }
                    else
                    {
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
                                    val nutrientHistoryList = mutableListOf<NutrientDataHistory>()
                                    val dataUserProfile : DataUserProfile = ApiResponses.dataUserProfile!!
                                    val userEntity = UserEntity(
                                        datauser.id,
                                        datauser.name,
                                        datauser.username,
                                        datauser.email,
                                        datauser.tipe,
                                        datauser.expired,
                                        dataUserProfile.height,
                                        dataUserProfile.weight,
                                        dataUserProfile.gender,
                                        dataUserProfile.kalori,
                                        dataUserProfile.age,
                                        dataUserProfile.activities
                                    )
                                    viewModel.insert(userEntity)
                                    val makslemak = String.format("%.1f", (userEntity.kalori*0.3)/9).toDouble()
                                    val maksgula = String.format("%.1f", (userEntity.kalori*0.2)/4).toDouble()
                                    var makskarbo:Double=0.0
                                    if(userEntity.gender == "Female"){
                                        when {
                                            userEntity.age<10 -> makskarbo=254.0
                                            userEntity.age in 13..18 -> makskarbo = 275.0
                                            userEntity.age in 19..29 -> makskarbo = 309.0
                                            userEntity.age in 30..49 -> makskarbo = 323.0
                                            userEntity.age in 50..64 -> makskarbo = 285.0
                                            userEntity.age in 65..80 -> makskarbo = 285.0
                                            userEntity.age > 80 -> makskarbo = 232.0
                                        }
                                    }
                                    else if(userEntity.gender=="Male"){
                                        when {
                                            userEntity.age<10 -> makskarbo=254.0
                                            userEntity.age in 10..12 -> makskarbo = 289.0
                                            userEntity.age in 13..15 -> makskarbo = 340.0
                                            userEntity.age in 16..18 -> makskarbo = 368.0
                                            userEntity.age in 19..29 -> makskarbo = 375.0
                                            userEntity.age in 30..49 -> makskarbo = 394.0
                                            userEntity.age in 50..64 -> makskarbo = 349.0
                                            userEntity.age in 65..80 -> makskarbo = 309.0
                                            userEntity.age > 80 -> makskarbo = 248.0
                                        }
                                    }
                                    val maksProtein = userEntity.weight*1.5
                                    var userNutrientsEntity = UserNutrientsEntity(userEntity.id,userEntity.kalori,0.0,makskarbo,0.0,maksgula,0.0,makslemak,0.0,maksProtein,0.0,formatted)
                                    var intentKirim = Intent(this, HomeActivity::class.java)
                                    apiServices.getUserNutrientHistory(datauser.id){ nutrientHistory ->
                                        nutrientHistoryList.addAll(nutrientHistory!!.dataHistory)

                                    }
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        if(nutrientHistoryList.size==0 || nutrientHistoryList.isNullOrEmpty()){

                                        }
                                        else{
                                            for(i in nutrientHistoryList.indices){
                                                if(nutrientHistoryList[i].tanggal==formatted){
                                                    userNutrientsEntity.lemak_consumed=nutrientHistoryList[i].fat.toDouble()
                                                    userNutrientsEntity.gula_consumed=nutrientHistoryList[i].sugar.toDouble()
                                                    userNutrientsEntity.kalori_consumed=nutrientHistoryList[i].calories.toDouble()
                                                    userNutrientsEntity.karbo_consumed=nutrientHistoryList[i].carbohydrate.toDouble()
                                                    userNutrientsEntity.protein_consumed=nutrientHistoryList[i].protein.toDouble()
                                                }
                                            }
                                        }
                                        viewModel.insertUserNutrients(userNutrientsEntity)
                                    },500)
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
                else{
                    binding.pbarLogin.visibility= View.INVISIBLE
                    Toast.makeText(this, "Login Error", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val retrofit = ApiConfig.provideApiService()
        retrofit.getUserSearchtxtFile().enqueue(
            object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("dataapi",t.message.toString())
                }
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Log.d("FileDownload", "server contacted and has file")
                        val writtenToDisk = writeResponseBodyToDisk(response.body()!!)
                        Log.d("FileDownload", "file download was a success? $writtenToDisk")
                        if(writtenToDisk){
                            val minSupport = 0.1
                            val apriori = Apriori.Builder<NamedItem>(minSupport).create()
                            val iterable = Iterable { DataIterator(File("${getExternalFilesDir(null)}${File.separator} dataIngredients.txt"))}
                            val output = apriori.execute(iterable)

                            val frequentItemSets = output.frequentItemSets
                            val sorting = Sorting.forItemSets().withOrder(Sorting.Order.DESCENDING) //
                            val sortedFrequentItemSets = frequentItemSets.sort(sorting)
                            Log.d("OutputApriori",sortedFrequentItemSets.toString())
                        }
                    } else {
                        Log.d("FileDownload", "server contact failed")
                    }
                }
            }
        )

    }
    private fun obtainViewModel(activity: AppCompatActivity): UserViewModel {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(UserViewModel::class.java)
    }
    private val usersObserver = Observer<UserEntity> { users ->

    }

    fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }

    private fun writeResponseBodyToDisk(body: ResponseBody): Boolean {
        return try {
            // todo change the file location/name according to your needs
            val futureStudioIconFile =
                File("${getExternalFilesDir(null)}${File.separator} dataIngredients.txt")
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(409600)
                val fileSize = body.contentLength()
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(futureStudioIconFile)
                while (true) {
                    val read: Int = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                    Log.d("FileDownload", "file download: $fileSizeDownloaded of $fileSize")
                }
                outputStream.flush()
                true
            } catch (e: IOException) {
                Log.d("FileDownload", "atas ${e.message}")
                false
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: IOException) {
            Log.d("FileDownload", "bawah ${e.message}")
            false
        }
    }
    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Exit App")
            .setMessage("Are you sure you want to exit the application ?") // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton("Yes"
            ) { _, _ ->
//                Toast.makeText(this,"Successfully Logout",Toast.LENGTH_SHORT).show()
                finishAffinity()
//                viewModel.getUser()?.removeObserver(){}

            } // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.no, null)
//            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }
}