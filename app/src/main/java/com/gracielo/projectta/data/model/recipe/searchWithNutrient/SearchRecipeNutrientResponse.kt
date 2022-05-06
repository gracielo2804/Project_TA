package com.gracielo.projectta.data.model.recipe.searchWithNutrient


import com.google.gson.annotations.SerializedName

data class SearchRecipeNutrientResponse(
    @SerializedName("code")
    var code: Int,
    @SerializedName("data")
    var `data`: List<DataSearchRecipeResponse>,
    @SerializedName("message")
    var message: String
)