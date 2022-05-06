package com.gracielo.projectta.viewmodel

import androidx.lifecycle.ViewModel
import com.gracielo.projectta.data.AppRepository
import com.gracielo.projectta.data.source.local.entity.FavouriteRecipeEntity

class FavRecipeViewModel (appRepository: AppRepository) : ViewModel()  {

    private val appRepository: AppRepository = appRepository

    fun insertFavRecipe(favouriteRecipeEntity: FavouriteRecipeEntity)= appRepository.insertFavRecipe(favouriteRecipeEntity)
    fun getFavRecipe(id_user:String) = appRepository.getFavRecipe(id_user)
    fun deleteFavRecipe(favouriteRecipeEntity: FavouriteRecipeEntity)= appRepository.deleteFavRecipe(favouriteRecipeEntity)


}