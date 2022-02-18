package com.gracielo.projectta.data.source.local.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.gracielo.projectta.data.source.local.entity.UserEntity

@Dao
interface AppDao {

    @Query("SELECT * FROM user")
    fun getUser(): LiveData<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserEntity)

    @Update
    fun updateUser(user:UserEntity)
}