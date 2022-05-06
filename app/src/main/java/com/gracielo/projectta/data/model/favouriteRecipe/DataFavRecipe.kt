package com.gracielo.projectta.data.model.favouriteRecipe


import com.google.gson.annotations.SerializedName

data class DataFavRecipe(
    @SerializedName("id_recipe")
    var idRecipe: String,
    @SerializedName("id_recipe_favourite")
    var idFavRecipe: String,
    @SerializedName("id_users")
    var idUsers: String,
)