package com.gracielo.projectta.data.source.local.database

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.gracielo.projectta.data.source.local.entity.*


@Dao
interface AppDao {

    @Query("SELECT * FROM user")
    fun getUser(): LiveData<UserEntity>

    @Query("SELECT * FROM ingredients ORDER BY name ASC")
//        fun getIngredients(): DataSource.Factory<Int,Ingredients>
        fun getIngredients(): LiveData<List<Ingredients>>

    @Query("SELECT * FROM ingredients  where isSelected=1 ORDER BY name ASC")
    fun getSelectedIngredients(): LiveData<List<Ingredients>>

    @Query("SELECT * FROM ingredients where name LIKE '%' || :search || '%' ORDER BY name ASC")
//    fun getSearchIngredients(search:String): DataSource.Factory<Int,IngredientsSearch>
    fun getSearchIngredients(search:String): LiveData<List<Ingredients>>

    @Query("SELECT * FROM shopping_list where id_user=:id_user")
    fun getShoppingList(id_user:String): LiveData<List<ShoppingListEntity>>

    @Query("SELECT * FROM favourite_recipe where id_user=:id_user")
    fun getFavRecipe(id_user:String): LiveData<List<FavouriteRecipeEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = FavouriteRecipeEntity::class)
    fun insertListFavRecipe(favRecipeList: List<FavouriteRecipeEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = FavouriteRecipeEntity::class)
    fun insertFavRecipe(favRecipe: FavouriteRecipeEntity)
    @Delete
    fun deleteFavRecipe(favRecipe: FavouriteRecipeEntity)

    @Query("SELECT * FROM ingredients  where isSelected=1 ORDER BY name ASC")
    fun getSelectedIngredientsPaged(): DataSource.Factory<Int,Ingredients>

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = UserEntity::class )
    fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = Ingredients::class)
    fun insertIngredients(ingredientsList: List<Ingredients>)

    @Insert(entity = ShoppingListEntity::class)
    fun insertShoppingList(shoppingListEntity: ShoppingListEntity)

    @Query("SELECT * FROM userNutrients")
    fun getUserNutrient(): LiveData<UserNutrientsEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserNutrient(nutrients: UserNutrientsEntity)
    @Update
    fun updateUserNutrient(nutrients: UserNutrientsEntity)

    @Delete
    fun deleteUserNutrient(nutrients: UserNutrientsEntity)

    @Update
    fun updateIngredients(ingredients: Ingredients)

    @Update
    fun updateUser(user:UserEntity)

    @Delete
    fun deleteUser(user:UserEntity)

    @Delete
    fun deleteShoppingList(shoppingList:ShoppingListEntity)
}
