package com.gracielo.projectta.data.model.listEquipment


import com.google.gson.annotations.SerializedName

data class AllEquipmentResponse(
    @SerializedName("code")
    var code: Int,
    @SerializedName("dataEquipment")
    var dataEquipment: List<DataEquipment>,
    @SerializedName("message")
    var message: String
)