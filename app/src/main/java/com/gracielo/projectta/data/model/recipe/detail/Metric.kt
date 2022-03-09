package com.gracielo.projectta.data.model.recipe.detail


import com.google.gson.annotations.SerializedName

data class Metric(
    @SerializedName("amount")
    var amount: Double,
    @SerializedName("unitLong")
    var unitLong: String,
    @SerializedName("unitShort")
    var unitShort: String
)