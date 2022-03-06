package com.gracielo.projectta.util

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.gracielo.projectta.viewmodel.IngredientsViewModel
import com.gracielo.projectta.viewmodel.UserViewModel
import com.gracielo.projectta.viewmodel.ViewModelFactory

object FunHelper {
    fun obtainIngredientsViewModel(activity: AppCompatActivity): IngredientsViewModel {
        val factory: ViewModelFactory? = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory!!).get(IngredientsViewModel::class.java)
    }

    fun obtainUserViewModel(activity: AppCompatActivity): UserViewModel {
        val factory: ViewModelFactory? = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory!!).get(UserViewModel::class.java)
    }
    fun deleteAllSelectedIngredients(activity: AppCompatActivity){
        val ingredientsViewModel = obtainIngredientsViewModel(activity)
        ingredientsViewModel.getSelectedIngredients().observe(activity){
            if (it.isNotEmpty()){
                it?.forEach {ingredients->
                    ingredients.isSelected=false
                    ingredientsViewModel.updateIngredients(ingredients)
                }
            }
        }
    }

}