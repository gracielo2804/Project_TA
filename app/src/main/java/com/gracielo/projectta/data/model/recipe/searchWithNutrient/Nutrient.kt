package com.gracielo.projectta.data.model.recipe.searchWithNutrient


import com.google.gson.annotations.SerializedName

data class Nutrient(
    @SerializedName("ammount")
    var ammount: Double,
    @SerializedName("name")
    var name: String
)