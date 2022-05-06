package com.gracielo.projectta.util

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gracielo.projectta.viewmodel.*

object FunHelper {
    fun obtainIngredientsViewModel(activity: AppCompatActivity): IngredientsViewModel {
        val factory: ViewModelFactory? = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory!!).get(IngredientsViewModel::class.java)
    }

    fun obtainUserViewModel(activity: AppCompatActivity): UserViewModel {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory!!).get(UserViewModel::class.java)
    }

    fun obtainShoppingViewModel(activity: AppCompatActivity): ShoppingListViewModel {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory!!).get(ShoppingListViewModel::class.java)
    }
    fun obtainFavRecipeViewModel(activity: AppCompatActivity): FavRecipeViewModel {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(FavRecipeViewModel::class.java)
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

    fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }

}