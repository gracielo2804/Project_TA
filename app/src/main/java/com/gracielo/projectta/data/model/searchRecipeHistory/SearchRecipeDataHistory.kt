package com.gracielo.projectta.data.model.searchRecipeHistory


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class SearchRecipeDataHistory(
    @SerializedName("id_recipe")
    var idRecipe: String,
    @SerializedName("recipe_name")
    var recipe_name: String,
    @SerializedName("ingredients_used")
    var ingredientsUsed: String
):Parcelable