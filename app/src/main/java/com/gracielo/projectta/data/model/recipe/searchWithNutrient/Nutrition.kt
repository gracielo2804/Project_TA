package com.gracielo.projectta.data.model.recipe.searchWithNutrient


import com.google.gson.annotations.SerializedName

data class Nutrition(
    @SerializedName("nutrients")
    var nutrients: List<Nutrient>
)