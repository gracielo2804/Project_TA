package com.gracielo.projectta.data.model.recipe.detail


import com.google.gson.annotations.SerializedName

data class ExtendedIngredient(
    @SerializedName("aisle")
    var aisle: String?,
    @SerializedName("amount")
    var amount: Double,
    @SerializedName("consistency")
    var consistency: String,
    @SerializedName("id")
    var id: Int,
    @SerializedName("image")
    var image: String,
    @SerializedName("measures")
    var measures: Measures,
    @SerializedName("meta")
    var meta: List<String>,
    @SerializedName("name")
    var name: String,
    @SerializedName("nameClean")
    var nameClean: String,
    @SerializedName("original")
    var original: String,
    @SerializedName("originalName")
    var originalName: String,
    @SerializedName("unit")
    var unit: String
)