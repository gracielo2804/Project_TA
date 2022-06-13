package com.gracielo.projectta.data.model.recipe.detail


import com.google.gson.annotations.SerializedName

data class RecipeDetailResponseItem(
    @SerializedName("aggregateLikes")
    var aggregateLikes: Int,

    @SerializedName("analyzedInstructions")
    var analyzedInstructions: List<analyzedInstructions>,

    @SerializedName("cheap")
    var cheap: Boolean,

    @SerializedName("creditsText")
    var creditsText: String,

    @SerializedName("cuisines")
    var cuisines: List<Any>,

    @SerializedName("nutrition")
    var nutrition: Nutrition,

    @SerializedName("dairyFree")
    var dairyFree: Boolean,

    @SerializedName("diets")
    var diets: List<String>,

    @SerializedName("dishTypes")
    var dishTypes: List<String>,

    @SerializedName("extendedIngredients")
    var extendedIngredients: List<ExtendedIngredient>,

    @SerializedName("gaps")
    var gaps: String,

    @SerializedName("glutenFree")
    var glutenFree: Boolean,

    @SerializedName("healthScore")
    var healthScore: Int,

    @SerializedName("id")
    var id: Int,

    @SerializedName("image")
    var image: String,

    @SerializedName("imageType")
    var imageType: String,

    @SerializedName("instructions")
    var instructions: Any,

    @SerializedName("lowFodmap")
    var lowFodmap: Boolean,

    @SerializedName("occasions")
    var occasions: List<Any>,

    @SerializedName("originalId")
    var originalId: Any,

    @SerializedName("pricePerServing")
    var pricePerServing: Double,

    @SerializedName("readyInMinutes")
    var readyInMinutes: Int,

    @SerializedName("servings")
    var servings: Int,

    @SerializedName("sourceName")
    var sourceName: String,

    @SerializedName("sourceUrl")
    var sourceUrl: String,

    @SerializedName("spoonacularScore")
    var spoonacularScore: Int,

    @SerializedName("summary")
    var summary: String,

    @SerializedName("sustainable")
    var sustainable: Boolean,

    @SerializedName("title")
    var title: String,

    @SerializedName("vegan")
    var vegan: Boolean,

    @SerializedName("vegetarian")
    var vegetarian: Boolean,

    @SerializedName("veryHealthy")
    var veryHealthy: Boolean,

    @SerializedName("veryPopular")
    var veryPopular: Boolean,

    @SerializedName("weightWatcherSmartPoints")
    var weightWatcherSmartPoints: Int,
)