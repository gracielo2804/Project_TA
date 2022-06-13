package com.gracielo.projectta.viewmodel

import androidx.lifecycle.ViewModel
import com.gracielo.projectta.data.source.local.LocalDataSource
import com.gracielo.projectta.data.source.local.database.AppDao
import com.gracielo.projectta.data.source.local.entity.ShoppingListEntity

class ShoppingListViewModel (appDao: AppDao) : ViewModel()  {

    private val localDataSource: LocalDataSource = LocalDataSource.getInstance(appDao)

    fun insert(shoppingListEntity: ShoppingListEntity)= localDataSource.insertShoppingList(shoppingListEntity)
    fun getShoppingList(id_user:String) = localDataSource.getShoppingList(id_user)
    fun getShoppingListFilterIngredients(id_user:String,ingredients:String) = localDataSource.getShoppingListFilterIngredients(id_user,ingredients)
    fun getShoppingListFilterRecipe(id_user:String,recipe:String) = localDataSource.getShoppingListFilterRecipe(id_user,recipe)
    fun delete(shoppingListEntity: ShoppingListEntity)= localDataSource.deleteShoppingList(shoppingListEntity)


}