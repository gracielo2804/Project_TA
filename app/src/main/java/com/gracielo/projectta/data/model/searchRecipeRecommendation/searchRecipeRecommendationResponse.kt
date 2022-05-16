package com.gracielo.projectta.data.model.searchRecipeRecommendation


import com.google.gson.annotations.SerializedName

data class searchRecipeRecommendationResponse(
    @SerializedName("code")
    var code: Int,
    @SerializedName("data")
    var dataSearchRecommendation: DataSearchRecommendation,
    @SerializedName("message")
    var message: String
)