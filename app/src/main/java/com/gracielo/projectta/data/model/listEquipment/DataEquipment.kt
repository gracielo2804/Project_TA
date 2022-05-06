package com.gracielo.projectta.data.model.listEquipment


import com.google.gson.annotations.SerializedName

data class DataEquipment(
    @SerializedName("id")
    var id: String,
    @SerializedName("image")
    var image: String,
    @SerializedName("name")
    var name: String
)