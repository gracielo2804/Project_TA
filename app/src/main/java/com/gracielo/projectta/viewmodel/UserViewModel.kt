package com.gracielo.projectta.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.gracielo.projectta.data.source.local.LocalDataSource
import com.gracielo.projectta.data.source.local.database.AppDao
import com.gracielo.projectta.data.source.local.entity.UserEntity
import com.gracielo.projectta.data.source.local.entity.UserNutrientsEntity

class UserViewModel (private val appDao: AppDao) : ViewModel()  {

    private val localDataSource: LocalDataSource = LocalDataSource.getInstance(appDao)

    fun insert(users: UserEntity)= localDataSource.insertUser(users)
    fun update(users: UserEntity)= localDataSource.updateUser(users)
    fun delete(users:UserEntity)= localDataSource.deleteUser(users)
    fun getUser(): LiveData<UserEntity> = localDataSource.getUser()

    fun getUserNutrients() = localDataSource.getUserNutrient()
    fun updateUserNutrients(userNutrientsEntity: UserNutrientsEntity) = localDataSource.updateUserNutrient(userNutrientsEntity)
    fun insertUserNutrients(userNutrientsEntity: UserNutrientsEntity) = localDataSource.insertUserNutrient(userNutrientsEntity)
    fun deleteUserNutrients(userNutrientsEntity: UserNutrientsEntity) = localDataSource.deleteUserNutrient(userNutrientsEntity)

}