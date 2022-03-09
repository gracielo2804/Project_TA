package com.gracielo.projectta.data.model.recipe.detail

import com.google.gson.annotations.SerializedName

data class Steps(
    @SerializedName("number")
    val number:String,

    @SerializedName("step")
    val step:String,

)
