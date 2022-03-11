package com.gracielo.projectta.data.source.local.database

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.gracielo.projectta.data.source.local.entity.Ingredients
import com.gracielo.projectta.data.source.local.entity.ShoppingListEntity
import com.gracielo.projectta.data.source.local.entity.UserEntity


@Dao
interface AppDao {

    @Query("SELECT * FROM user")
    fun getUser(): LiveData<UserEntity>

    @Query("SELECT * FROM ingredients ORDER BY name ASC")
        fun getIngredients(): DataSource.Factory<Int,Ingredients>

    @Query("SELECT * FROM ingredients  where isSelected=1 ORDER BY name ASC")
    fun getSelectedIngredients(): LiveData<List<Ingredients>>

    @Query("SELECT * FROM shopping_list where id_user=:id_user")
    fun getShoppingList(id_user:String): LiveData<List<ShoppingListEntity>>

    @Query("SELECT * FROM ingredients  where isSelected=1 ORDER BY name ASC")
    fun getSelectedIngredientsPaged(): DataSource.Factory<Int,Ingredients>

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = UserEntity::class )
    fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = Ingredients::class)
    fun insertIngredients(ingredientsList: List<Ingredients>)

    @Insert(entity = ShoppingListEntity::class)
    fun insertShoppingList(shoppingListEntity: ShoppingListEntity)


    @Update
    fun updateIngredients(ingredients: Ingredients)

    @Update
    fun updateUser(user:UserEntity)

    @Delete
    fun deleteUser(user:UserEntity)
}
