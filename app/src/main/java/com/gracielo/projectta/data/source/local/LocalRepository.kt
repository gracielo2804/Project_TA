package com.gracielo.projectta.data.source.local

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import com.gracielo.projectta.data.source.local.database.AppDao
import com.gracielo.projectta.data.source.local.database.AppDatabase
import com.gracielo.projectta.data.source.local.entity.UserEntity
import java.util.concurrent.Executors

class LocalRepository (private val application: Application, private val appDao: AppDao) {
    companion object {
        @Volatile
        private var instance: LocalRepository? = null

        fun getInstance(application: Application): LocalRepository? {
            return instance ?: synchronized(LocalRepository::class.java) {
                if (instance == null) {
                    val database = AppDatabase.getInstance(application.applicationContext)
                    instance = LocalRepository(application,database.appDao())
                }
                return instance
            }
        }
    }
    fun insertUser(user:UserEntity) = Executors.newSingleThreadExecutor().execute(){
        appDao.insertUser(user)
    }

    fun update(user:UserEntity) = Executors.newSingleThreadExecutor().execute(){
        appDao.updateUser(user)
    }

    fun getUser() : LiveData<UserEntity> = appDao.getUser()


}