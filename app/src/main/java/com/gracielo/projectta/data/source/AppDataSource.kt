package com.gracielo.projectta.data.source

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.gracielo.projectta.data.source.local.entity.Ingredients
import com.gracielo.projectta.vo.Resource

interface AppDataSource {
    fun getIngredients(): LiveData<Resource<PagedList<Ingredients>>>
}