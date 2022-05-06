package com.gracielo.projectta.data.model.recipeCount


import com.google.gson.annotations.SerializedName

data class CountRecipeResponse(
    @SerializedName("code")
    var code: Int,
    @SerializedName("dataCountRecipe")
    var dataCountRecipe: List<DataCountRecipe>,
    @SerializedName("message")
    var message: String
)