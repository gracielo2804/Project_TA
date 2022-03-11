package com.gracielo.projectta.data.model.recipe.similar


import com.google.gson.annotations.SerializedName

data class SimilarRecipeResponseItem(
    @SerializedName("id")
    var id: Int,
    @SerializedName("imageType")
    var imageType: String,
    @SerializedName("readyInMinutes")
    var readyInMinutes: Int,
    @SerializedName("servings")
    var servings: Int,
    @SerializedName("sourceUrl")
    var sourceUrl: String,
    @SerializedName("title")
    var title: String
)