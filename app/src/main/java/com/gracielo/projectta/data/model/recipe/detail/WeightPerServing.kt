package com.gracielo.projectta.data.model.recipe.detail

import com.google.gson.annotations.SerializedName

data class WeightPerServing (

    @SerializedName("ammount")
    val ammount:Double,

    @SerializedName("unit")
    val unit:String

)
