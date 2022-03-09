package com.gracielo.projectta.data.model.recipe.detail

import com.google.gson.annotations.SerializedName

data class analyzedInstructions(
    @SerializedName("name")
    val name:String,
    @SerializedName("steps")
    val steps : List<Steps>
)
