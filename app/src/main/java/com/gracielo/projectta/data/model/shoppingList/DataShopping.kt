package com.gracielo.projectta.data.model.shoppingList


import com.google.gson.annotations.SerializedName

data class DataShopping(
    @SerializedName("id_recipe")
    var idRecipe: String,
    @SerializedName("id_shopping_list")
    var idShoppingList: String,
    @SerializedName("id_users")
    var idUsers: String,
    @SerializedName("recipe_name")
    var recipeName: String,
    @SerializedName("ingredients_list")
    var ingredientsList: String
)