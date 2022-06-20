package com.gracielo.projectta.data.model.recipe.detailNew


import com.google.gson.annotations.SerializedName

data class AnalyzedInstruction(
    @SerializedName("name")
    var name: String,
    @SerializedName("steps")
    var steps: List<Step>
)