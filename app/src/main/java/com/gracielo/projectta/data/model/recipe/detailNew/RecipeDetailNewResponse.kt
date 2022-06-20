package com.gracielo.projectta.data.model.recipe.detailNew


import com.google.gson.annotations.SerializedName

data class RecipeDetailNewResponse(
    @SerializedName("aggregateLikes")
    var aggregateLikes: Int,
    @SerializedName("analyzedInstructions")
    var analyzedInstructions: List<AnalyzedInstruction>,
    @SerializedName("cheap")
    var cheap: Boolean,
    @SerializedName("cookingMinutes")
    var cookingMinutes: Int,
    @SerializedName("creditsText")
    var creditsText: Any,
    @SerializedName("cuisines")
    var cuisines: List<String>,
    @SerializedName("dairyFree")
    var dairyFree: Boolean,
    @SerializedName("diets")
    var diets: List<Any>,
    @SerializedName("dishTypes")
    var dishTypes: List<String>,
    @SerializedName("extendedIngredients")
    var extendedIngredients: List<Any>,
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
    var instructions: String,
    @SerializedName("lowFodmap")
    var lowFodmap: Boolean,
    @SerializedName("occasions")
    var occasions: List<Any>,
    @SerializedName("openLicense")
    var openLicense: Int,
    @SerializedName("originalId")
    var originalId: Any,
    @SerializedName("preparationMinutes")
    var preparationMinutes: Int,
    @SerializedName("pricePerServing")
    var pricePerServing: Double,
    @SerializedName("readyInMinutes")
    var readyInMinutes: Int,
    @SerializedName("servings")
    var servings: Int,
    @SerializedName("sourceName")
    var sourceName: Any,
    @SerializedName("sourceUrl")
    var sourceUrl: String,
    @SerializedName("spoonacularSourceUrl")
    var spoonacularSourceUrl: String,
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
    @SerializedName("winePairing")
    var winePairing: WinePairing
)