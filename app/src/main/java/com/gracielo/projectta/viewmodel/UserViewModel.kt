package com.gracielo.projectta.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.gracielo.projectta.data.source.local.LocalRepository
import com.gracielo.projectta.data.source.local.entity.UserEntity

class UserViewModel (private val application: Application) : ViewModel()  {

    private val localRepository: LocalRepository? = LocalRepository.getInstance(application)

    fun insert(users: UserEntity)= localRepository?.insertUser(users)
    fun update(users: UserEntity)= localRepository?.update(users)
    fun getUser(): LiveData<UserEntity>? = localRepository?.getUser()


}