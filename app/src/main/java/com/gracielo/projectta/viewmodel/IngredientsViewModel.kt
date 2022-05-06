package com.gracielo.projectta.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.gracielo.projectta.data.AppRepository
import com.gracielo.projectta.data.source.local.LocalDataSource
import com.gracielo.projectta.data.source.local.database.AppDao
import com.gracielo.projectta.data.source.local.entity.Ingredients
import com.gracielo.projectta.data.source.local.entity.IngredientsSearch
import com.gracielo.projectta.data.source.local.entity.UserEntity
import com.gracielo.projectta.vo.Resource

class IngredientsViewModel (private val repository: AppRepository) : ViewModel()  {

    fun getIngredients(): LiveData<Resource<List<Ingredients>>> {
        return repository.getIngredients()
    }
    fun getSearchIngredients(search:String): LiveData<Resource<List<Ingredients>>> {
        return repository.getSearchIngredients(search)
    }


//    fun getSelectedIngredientsPaged(): LiveData<PagedList<Ingredients>> {
//        val config = PagedList.Config.Builder().apply {
//            setEnablePlaceholders(false)
//            setInitialLoadSizeHint(20)
//            setPageSize(20)
//        }.build()
//        return LivePagedListBuilder(repository.localDataSource.getAllIngredients(), config).build()
//    }
    fun updateIngredients(ingredients: Ingredients){
        repository.localDataSource.updateIngredients(ingredients)
    }
    fun getSelectedIngredients(): LiveData<List<Ingredients>> {
        return repository.localDataSource.getSelectedIngredients()
    }


}