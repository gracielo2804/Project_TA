package com.gracielo.projectta.data.model.recipe.detailNew


import com.google.gson.annotations.SerializedName

data class Length(
    @SerializedName("number")
    var number: Int,
    @SerializedName("unit")
    var unit: String
)