package com.gracielo.projectta.data.model.admin.allUser


import com.google.gson.annotations.SerializedName

data class DataAllUser(
    @SerializedName("age")
    var age: String,
    @SerializedName("email")
    var email: String,
    @SerializedName("email_verified")
    var emailVerified: String,
    @SerializedName("expired")
    var expired: String,
    @SerializedName("id")
    var id: String,
    @SerializedName("nama")
    var nama: String,
    @SerializedName("tipe")
    var tipe: String,
    @SerializedName("username")
    var username: String,
    @SerializedName("last_login")
    var last_login: String
)