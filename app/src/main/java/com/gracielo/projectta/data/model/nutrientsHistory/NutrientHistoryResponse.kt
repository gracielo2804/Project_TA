package com.gracielo.projectta.data.model.nutrientsHistory


import com.google.gson.annotations.SerializedName

data class NutrientHistoryResponse(
    @SerializedName("code")
    var code: Int,
    @SerializedName("dataHistory")
    var dataHistory: List<DataHistory>,
    @SerializedName("message")
    var message: String
)