package com.gracielo.projectta.data.model.recipe.detail


import com.google.gson.annotations.SerializedName

data class Measures(
    @SerializedName("metric")
    var metric: Metric,
    @SerializedName("us")
    var us: Us
)