package com.gracielo.projectta.data.model.shoppingList


import com.google.gson.annotations.SerializedName

data class ShoppingListResponse(
    @SerializedName("code")
    var code: Int,
    @SerializedName("dataShoppingList")
    var dataShoppingList: List<DataShopping>,
    @SerializedName("message")
    var message: String
)