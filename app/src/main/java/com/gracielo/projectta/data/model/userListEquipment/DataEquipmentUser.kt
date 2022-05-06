package com.gracielo.projectta.data.model.userListEquipment


import com.google.gson.annotations.SerializedName

data class DataEquipmentUser(
    @SerializedName("id_equipment")
    var ListIDEquipment: String,
    @SerializedName("id_user")
    var idUser: String
)