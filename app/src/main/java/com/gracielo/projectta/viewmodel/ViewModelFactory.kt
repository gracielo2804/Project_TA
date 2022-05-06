package com.gracielo.projectta.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gracielo.projectta.data.AppRepository
import com.gracielo.projectta.dataInjection.Injection

class ViewModelFactory private constructor(private val appRepository: AppRepository) : ViewModelProvider.NewInstanceFactory() {
    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(application: Application): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(Injection.provideRepository(application.applicationContext))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(appRepository.localDataSource.appDao) as T
        }
        else if (modelClass.isAssignableFrom(IngredientsViewModel::class.java)) {
            return IngredientsViewModel(appRepository) as T
        }
        else if (modelClass.isAssignableFrom(ShoppingListViewModel::class.java)) {
            return ShoppingListViewModel(appRepository.localDataSource.appDao) as T
        }
        else if (modelClass.isAssignableFrom(FavRecipeViewModel::class.java)) {
            return FavRecipeViewModel(appRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}