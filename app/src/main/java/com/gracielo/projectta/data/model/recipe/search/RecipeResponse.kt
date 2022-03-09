package com.gracielo.projectta.data.model.recipe.search


import com.google.gson.annotations.SerializedName


data class RecipeResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: List<RecipeResponseItem>
)