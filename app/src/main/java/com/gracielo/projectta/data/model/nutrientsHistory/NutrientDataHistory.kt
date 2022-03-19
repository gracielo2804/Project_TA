package com.gracielo.projectta.data.model.nutrientsHistory


import com.google.gson.annotations.SerializedName

data class NutrientDataHistory(
    @SerializedName("calories")
    var calories: String,
    @SerializedName("carbohydrate")
    var carbohydrate: String,
    @SerializedName("fat")
    var fat: String,
    @SerializedName("protein")
    var protein: String,
    @SerializedName("sugar")
    var sugar: String,
    @SerializedName("tanggal")
    var tanggal: String
)