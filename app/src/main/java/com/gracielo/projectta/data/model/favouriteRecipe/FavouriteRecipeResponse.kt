package com.gracielo.projectta.data.model.favouriteRecipe


import com.google.gson.annotations.SerializedName

data class FavouriteRecipeResponse(
    @SerializedName("code")
    var code: Int,
    @SerializedName("dataFavouriteRecipe")
    var dataFavRecipe: List<DataFavRecipe>,
    @SerializedName("message")
    var message: String
)