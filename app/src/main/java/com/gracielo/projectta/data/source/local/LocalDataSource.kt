package com.gracielo.projectta.data.source.local

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.gracielo.projectta.data.source.local.database.AppDao
import com.gracielo.projectta.data.source.local.entity.*
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
//    fun getAllIngredients(): PagingSource<Int,Ingredients>  = appDao.getIngredients()
//    fun getAllIngredients(): DataSource.Factory<Int,Ingredients>  = appDao.getIngredients()
    fun getAllIngredients(): LiveData<List<Ingredients>>  = appDao.getIngredients()
//    fun getSearchIngredients(search:String): DataSource.Factory<Int, IngredientsSearch> = appDao.getSearchIngredients(search)
    fun getSearchIngredients(search:String): LiveData<List<Ingredients>> = appDao.getSearchIngredients(search)
    fun getSelectedIngredientsPaged(): DataSource.Factory<Int,Ingredients>  = appDao.getSelectedIngredientsPaged()
    fun getSelectedIngredients(): LiveData<List<Ingredients>> = appDao.getSelectedIngredients()

    fun getShoppingList(id_user : String) : LiveData<List<ShoppingListEntity>> = appDao.getShoppingList(id_user)
    fun getShoppingListFilterIngredients(id_user : String, ingredients:String) : LiveData<List<ShoppingListEntity>> = appDao.getShoppingListFilterIngredients(id_user,ingredients)
    fun getShoppingListFilterRecipe(id_user : String, recipe:String) : LiveData<List<ShoppingListEntity>> = appDao.getShoppingListFilterRecipe(id_user,recipe)
    fun insertShoppingList(shoppingListEntity: ShoppingListEntity) = Executors.newSingleThreadExecutor().execute() {
        appDao.insertShoppingList(shoppingListEntity)
    }
    fun deleteShoppingList(shoppingListEntity: ShoppingListEntity) = Executors.newSingleThreadExecutor().execute(){
        appDao.deleteShoppingList(shoppingListEntity)
    }

    fun getFavRecipe(id_user : String) : LiveData<List<FavouriteRecipeEntity>> = appDao.getFavRecipe(id_user)
    fun insertFavRecipe(favRecipe: FavouriteRecipeEntity) = Executors.newSingleThreadExecutor().execute() {
        appDao.insertFavRecipe(favRecipe)
    }
    fun insertFavRecipeList(favRecipeList: List<FavouriteRecipeEntity>) = Executors.newSingleThreadExecutor().execute() {
        appDao.insertListFavRecipe(favRecipeList)
    }
    fun deleteFavRecipe(favRecipe: FavouriteRecipeEntity) = Executors.newSingleThreadExecutor().execute(){
        appDao.deleteFavRecipe(favRecipe)
    }

    fun updateUser(user:UserEntity) = Executors.newSingleThreadExecutor().execute(){
        appDao.updateUser(user)
    }
    fun updateIngredients(ingredients: Ingredients) = Executors.newSingleThreadExecutor().execute{
        appDao.updateIngredients(ingredients)
    }
    fun deleteUser(user:UserEntity) = Executors.newSingleThreadExecutor().execute(){
        appDao.deleteUser(user)
    }

    fun getUserNutrient():LiveData<UserNutrientsEntity> = appDao.getUserNutrient()
    fun insertUserNutrient(nutrients: UserNutrientsEntity)= appDao.insertUserNutrient(nutrients)
    fun updateUserNutrient(nutrients: UserNutrientsEntity)= appDao.updateUserNutrient(nutrients)
    fun deleteUserNutrient(nutrients: UserNutrientsEntity) = Executors.newSingleThreadExecutor().execute(){
        appDao.deleteUserNutrient(nutrients)
    }

    fun getUser() : LiveData<UserEntity> = appDao.getUser()


}