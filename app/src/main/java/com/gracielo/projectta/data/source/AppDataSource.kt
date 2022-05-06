package com.gracielo.projectta.data.source

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import com.gracielo.projectta.data.source.local.entity.FavouriteRecipeEntity
//import androidx.paging.PagingData
import com.gracielo.projectta.data.source.local.entity.Ingredients
import com.gracielo.projectta.data.source.local.entity.IngredientsSearch
import com.gracielo.projectta.vo.Resource

interface AppDataSource {
//    fun getIngredients(): LiveData<Resource<PagingData<Ingredients>>>
//    fun getIngredients(): LiveData<Resource<PagedList<Ingredients>>>
    fun getIngredients(): LiveData<Resource<List<Ingredients>>>
//    fun getSearchIngredients(search:String): LiveData<Resource<PagedList<IngredientsSearch>>>
    fun getSearchIngredients(search:String): LiveData<Resource<List<Ingredients>>>

    fun getFavRecipe(idUser:String):LiveData<Resource<List<FavouriteRecipeEntity>>>
    fun insertFavRecipe(favRecipe:FavouriteRecipeEntity)
    fun insertListFavRecipe(listFavRecipe:List<FavouriteRecipeEntity>)
    fun deleteFavRecipe(favRecipe:FavouriteRecipeEntity)
}
