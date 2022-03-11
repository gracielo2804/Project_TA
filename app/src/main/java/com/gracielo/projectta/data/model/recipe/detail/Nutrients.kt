package com.gracielo.projectta.data.model.recipe.detail

import com.google.gson.annotations.SerializedName

data class Nutrients(
    @SerializedName("name")
    val name:String,

    @SerializedName("amount")
    val amount:Double,

    @SerializedName("unit")
    val unit:String,

    @SerializedName("percentOfDailyNeed")
    val percentOfDailyNeed:Double
)
