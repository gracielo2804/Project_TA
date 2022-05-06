package com.gracielo.projectta.data.model.admin.allUser


import com.google.gson.annotations.SerializedName

data class AllUserResponse(
    @SerializedName("code")
    var code: Int,
    @SerializedName("dataAllUser")
    var dataAllUser: List<DataAllUser>,
    @SerializedName("message")
    var message: String
)