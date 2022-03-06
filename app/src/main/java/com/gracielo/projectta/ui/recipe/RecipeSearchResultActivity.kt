package com.gracielo.projectta.ui.recipe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gracielo.projectta.data.model.RecipeResponseItem
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityRecipeSearchResultBinding
import com.gracielo.projectta.viewmodel.IngredientsViewModel

class RecipeSearchResultActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRecipeSearchResultBinding
    private lateinit var rv_recipe:RecyclerView
    private lateinit var viewModel: IngredientsViewModel
    private var paramIngredients:String = ""
    val apiServices= ApiServices()
    private var dataRecipe = ArrayList<RecipeResponseItem>()
    val adapters = RecipeSearchResultAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecipeSearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rv_recipe = binding.rvSearchResult

        if(intent.hasExtra("paramIngredients")) paramIngredients =
            intent.getStringExtra("paramIngredients").toString()

        if(paramIngredients!=""){
            apiServices.searchRecipe(paramIngredients){
                if(it?.code==1){
                    dataRecipe= it.data as ArrayList<RecipeResponseItem>
                    adapters.setData(dataRecipe)
                }
            }
        }

        adapters.onItemClick = {
            Log.d("dataRecipe", it.toString())
        }

        rv_recipe.apply {
            layoutManager = GridLayoutManager(this@RecipeSearchResultActivity,2)
            adapter = adapters
        }
    }
}