package com.gracielo.projectta.data.model.recipe.detail

import com.google.gson.annotations.SerializedName

data class RecipeDetailListResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data:List<RecipeDetailResponseItem>
)
