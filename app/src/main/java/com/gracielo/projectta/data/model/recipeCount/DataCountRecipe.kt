package com.gracielo.projectta.data.model.recipeCount


import com.google.gson.annotations.SerializedName

data class DataCountRecipe(
    @SerializedName("id_recipe")
    var idRecipe: String,
    @SerializedName("jumlah")
    var jumlah: String,
    @SerializedName("recipe_name")
    var recipeName: String
)