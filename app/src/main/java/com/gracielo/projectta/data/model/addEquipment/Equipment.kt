package com.gracielo.projectta.data.model.addEquipment


import com.google.gson.annotations.SerializedName

data class Equipment(
    @SerializedName("image")
    var image: String,
    @SerializedName("name")
    var name: String
)