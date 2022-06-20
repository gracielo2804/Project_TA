package com.gracielo.projectta.data.model.recipe.detailNew


import com.google.gson.annotations.SerializedName

data class Step(
    @SerializedName("equipment")
    var equipment: List<Equipment>,
    @SerializedName("ingredients")
    var ingredients: List<Ingredient>,
    @SerializedName("length")
    var length: Length,
    @SerializedName("number")
    var number: Int,
    @SerializedName("step")
    var step: String
)