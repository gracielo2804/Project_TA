package com.gracielo.projectta.data.model.userListEquipment


import com.google.gson.annotations.SerializedName

data class UserListEquipmentResponse(
    @SerializedName("code")
    var code: Int,
    @SerializedName("dataEquipment")
    var dataEquipment: DataEquipmentUser,
    @SerializedName("message")
    var message: String
)