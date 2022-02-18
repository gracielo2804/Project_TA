package com.gracielo.projectta.data.source.remote.network

import android.util.Log
import com.gracielo.projectta.data.model.AddUsers
import com.gracielo.projectta.data.model.DataUserProfile
import com.gracielo.projectta.data.model.MessageResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiServices {

    fun addUser(userData: AddUsers, onResult: (MessageResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.registerUser(
            username = userData.username,
            function = "adduser",
            email=userData.email,
            password = userData.password,
            name=userData.nama
        ).enqueue(
            object : Callback<MessageResponse> {
                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    onResult(null)
                }
                override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                    Log.d("dataapi",response.body().toString())
                    val addedUser = response.body()
                    onResult(addedUser)
                }
            }
        )
    }

    fun Login(username: String,password:String , onResult: (MessageResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.Login(
            function = "login",
            username = username,
            password = password,
        ).enqueue(
            object : Callback<MessageResponse> {
                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    onResult(null)
                }
                override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                    Log.d("dataapi",response.body().toString())
                    val data = response.body()
                    onResult(data)
                }
            }
        )
    }

    fun getUserProfileData(id:String , onResult: (MessageResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.getDataUserProfile(
            function = "getUserProfileData",
            id_users= id
        ).enqueue(
            object : Callback<MessageResponse> {
                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    onResult(null)
                }
                override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                    Log.d("dataapi",response.body().toString())
                    val data = response.body()
                    onResult(data)
                }
            }
        )
    }

    fun getUserDataByID(id:String , onResult: (MessageResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.getUserDataByID(
            function = "getUserDataByID",
            id_users= id
        ).enqueue(
            object : Callback<MessageResponse> {
                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    onResult(null)
                }
                override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                    Log.d("dataapi",response.body().toString())
                    val data = response.body()
                    onResult(data)
                }
            }
        )
    }

    fun addUserProfileData(dataUserProfile: DataUserProfile, onResult: (MessageResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.addUserProfileData(
            function = "addUserProfileData",
            id_users = dataUserProfile.id_users,
            height = dataUserProfile.height,
            weight = dataUserProfile.weight,
            gender = dataUserProfile.gender,
            age = dataUserProfile.age,
            kalori = dataUserProfile.kalori
        ).enqueue(
            object : Callback<MessageResponse> {
                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    onResult(null)
                }
                override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                    Log.d("dataapi",response.body().toString())
                    val addedUser = response.body()
                    onResult(addedUser)
                }
            }
        )
    }

    fun uploadPicture(function : RequestBody , body: MultipartBody.Part, onResult: (ResponseBody?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.uploadPicture(
            function = function,
            imagePart = body
        ).enqueue(
            object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                   t.printStackTrace()
                    onResult(null)
                }
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Log.d("data",response.body()!!.string())
                    val addedUser = response.body()
                    onResult(addedUser)

                }
            }
        )
    }

    fun sendVerificationEmail(id: String,email:String,name:String, onResult: (MessageResponse?) -> Unit){
        val retrofit = ApiConfig.provideApiService()
        retrofit.sendVerificationEmail(
            function = "testSendEmail",
            id_users = id,
            email_recipient = email,
            recipient_name = name
        ).enqueue(
            object : Callback<MessageResponse> {
                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    onResult(null)
                }
                override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                    Log.d("dataapi",response.body().toString())
                    val messageResponse = response.body()
                    onResult(messageResponse)
                }
            }
        )
    }


}