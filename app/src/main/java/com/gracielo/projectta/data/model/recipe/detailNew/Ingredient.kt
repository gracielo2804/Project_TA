package com.gracielo.projectta.data.model.recipe.detailNew


import com.google.gson.annotations.SerializedName

data class Ingredient(
    @SerializedName("id")
    var id: Int,
    @SerializedName("image")
    var image: String,
    @SerializedName("localizedName")
    var localizedName: String,
    @SerializedName("name")
    var name: String
)