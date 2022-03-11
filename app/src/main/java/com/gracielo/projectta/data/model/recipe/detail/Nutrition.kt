package com.gracielo.projectta.data.model.recipe.detail

import com.google.gson.annotations.SerializedName

data class Nutrition(

    @SerializedName("nutrients")
    val nutrients:List<Nutrients>,

    @SerializedName("caloricBreakdown")
    val caloricBreakdown:CaloricBreakdown,

    @SerializedName("weightPerServing")
    val weightPerServing:WeightPerServing,


)
