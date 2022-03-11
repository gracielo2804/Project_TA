package com.gracielo.projectta.data.model.recipe.similar


import com.google.gson.annotations.SerializedName

data class SimilarRecipeResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: List<SimilarRecipeResponseItem>
)