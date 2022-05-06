package com.gracielo.projectta.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.gracielo.projectta.data.source.local.LocalDataSource
import com.gracielo.projectta.data.source.local.database.AppDao
import com.gracielo.projectta.data.source.local.entity.ShoppingListEntity
import com.gracielo.projectta.data.source.local.entity.UserEntity

class ShoppingListViewModel (appDao: AppDao) : ViewModel()  {

    private val localDataSource: LocalDataSource = LocalDataSource.getInstance(appDao)

    fun insert(shoppingListEntity: ShoppingListEntity)= localDataSource.insertShoppingList(shoppingListEntity)
    fun getShoppingList(id_user:String) = localDataSource.getShoppingList(id_user)
    fun delete(shoppingListEntity: ShoppingListEntity)= localDataSource.deleteShoppingList(shoppingListEntity)


}