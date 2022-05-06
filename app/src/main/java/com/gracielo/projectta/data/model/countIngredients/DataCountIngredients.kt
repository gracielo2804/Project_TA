package com.gracielo.projectta.data.model.countIngredients


import com.google.gson.annotations.SerializedName

data class DataCountIngredients(
    @SerializedName("count")
    var count: Int,
    @SerializedName("name")
    var name: String
)