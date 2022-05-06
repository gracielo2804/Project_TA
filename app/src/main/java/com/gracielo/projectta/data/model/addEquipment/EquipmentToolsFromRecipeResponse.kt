package com.gracielo.projectta.data.model.addEquipment


import com.google.gson.annotations.SerializedName

data class EquipmentToolsFromRecipeResponse(
    @SerializedName("code")
    var code: Int,
    @SerializedName("data")
    var `data`: Data,
    @SerializedName("message")
    var message: String
)