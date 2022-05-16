package com.gracielo.projectta.data.model.searchRecipeRecommendation


import com.google.gson.annotations.SerializedName

data class DataSearchRecommendation(
    @SerializedName("id")
    var id: Int?,
    @SerializedName("image")
    var image: String?,
    @SerializedName("title")
    var title: String?
)