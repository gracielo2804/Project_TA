package com.gracielo.projectta.data.model.recipe.detail

import com.google.gson.annotations.SerializedName

data class CaloricBreakdown (

    @SerializedName("percentProtein")
    val percentProtein:Double,

    @SerializedName("percentFat")
    val percentFat:Double,

    @SerializedName("percentCarbs")
    val percentCarbs:Double,
)
