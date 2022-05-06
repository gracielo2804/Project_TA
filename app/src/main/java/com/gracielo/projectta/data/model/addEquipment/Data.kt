package com.gracielo.projectta.data.model.addEquipment


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("equipment")
    var equipment: List<Equipment>
)