package com.gracielo.projectta.data.model.recipe.search


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecipeResponseItem(
    @SerializedName("id")
    var id: Int,

    @SerializedName("image")
    var image: String,

    @SerializedName("imageType")
    var imageType: String,

    @SerializedName("likes")
    var likes: Int,

    @SerializedName("missedIngredientCount")
    var missedIngredientCount: Int,

    @SerializedName("missedIngredients")
    var missedIngredients: List<MissedIngredient>?,

    @SerializedName("title")
    var title: String,

    @SerializedName("unusedIngredients")
    var UnusedIngredients: List<UnusedIngredients>,

    @SerializedName("usedIngredientCount")
    var usedIngredientCount: Int,

    @SerializedName("usedIngredients")
    var usedIngredients: List<UsedIngredient>?
) : Parcelable