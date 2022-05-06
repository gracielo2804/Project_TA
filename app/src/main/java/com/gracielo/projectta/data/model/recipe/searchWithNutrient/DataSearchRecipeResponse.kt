package com.gracielo.projectta.data.model.recipe.searchWithNutrient


import com.google.gson.annotations.SerializedName

data class DataSearchRecipeResponse(
    @SerializedName("id")
    var id: Int,
    @SerializedName("image")
    var image: String,
    @SerializedName("nutrition")
    var nutrition: Nutrition,
    @SerializedName("title")
    var title: String
)