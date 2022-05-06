package com.gracielo.projectta.data.model.countIngredients


import com.google.gson.annotations.SerializedName

data class CountIngredientsResponse(
    @SerializedName("code")
    var code: Int,
    @SerializedName("dataCountIngredients")
    var dataCountIngredients: List<DataCountIngredients>,
    @SerializedName("message")
    var message: String
)