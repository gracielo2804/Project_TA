package com.gracielo.projectta.data.source.local

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.gracielo.projectta.data.source.local.database.AppDao
import com.gracielo.projectta.data.source.local.database.AppDatabase
import com.gracielo.projectta.data.source.local.entity.Ingredients
import com.gracielo.projectta.data.source.local.entity.ShoppingListEntity
import com.gracielo.projectta.data.source.local.entity.UserEntity
import java.util.concurrent.Executors

class LocalDataSource (val appDao: AppDao) {
    companion object {
        private var instance: LocalDataSource? = null
        fun getInstance(appDao : AppDao): LocalDataSource = instance?: LocalDataSource(appDao)
    }

    fun insertUser(user:UserEntity) = Executors.newSingleThreadExecutor().execute(){
        appDao.insertUser(user)
    }

    fun insertIngredients(ingredientsList:List<Ingredients>) = Executors.newSingleThreadExecutor().execute(){
        appDao.insertIngredients(ingredientsList)
    }
    fun getAllIngredients(): DataSource.Factory<Int,Ingredients>  = appDao.getIngredients()
    fun getSelectedIngredientsPaged(): DataSource.Factory<Int,Ingredients>  = appDao.getSelectedIngredientsPaged()
    fun getSelectedIngredients(): LiveData<List<Ingredients>> = appDao.getSelectedIngredients()

    fun getShoppingList(id_user : String) : LiveData<List<ShoppingListEntity>> = appDao.getShoppingList(id_user)
    fun insertShoppingList(shoppingListEntity: ShoppingListEntity) = Executors.newSingleThreadExecutor().execute() {
        appDao.insertShoppingList(shoppingListEntity)
    }

    fun updateUser(user:UserEntity) = Executors.newSingleThreadExecutor().execute(){
        appDao.updateUser(user)
    }
    fun updateIngredients(ingredients: Ingredients) = Executors.newSingleThreadExecutor().execute(){
        appDao.updateIngredients(ingredients)
    }
    fun deleteUser(user:UserEntity) = Executors.newSingleThreadExecutor().execute(){
        appDao.deleteUser(user)
    }

    fun getUser() : LiveData<UserEntity> = appDao.getUser()


}