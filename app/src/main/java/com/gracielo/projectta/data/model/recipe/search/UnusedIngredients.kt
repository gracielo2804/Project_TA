package com.gracielo.projectta.data.model.recipe.search


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UnusedIngredients(
    @SerializedName("aisle")
    var aisle: String?,
    @SerializedName("amount")
    var amount: Double,
    @SerializedName("id")
    var id: Int,
    @SerializedName("image")
    var image: String,
    @SerializedName("name")
    var name: String,
    @SerializedName("original")
    var original: String,
    @SerializedName("originalName")
    var originalName: String,
    @SerializedName("unit")
    var unit: String,
    @SerializedName("unitLong")
    var unitLong: String,
    @SerializedName("unitShort")
    var unitShort: String
) : Parcelable