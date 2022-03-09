package com.gracielo.projectta.ui.recipe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gracielo.projectta.data.model.recipe.search.RecipeResponseItem
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
                    dataRecipe.forEach {item->
                        val image = item.image.split("-")
                        val imageandext =image[1].split(".")
                        item.image="${image[0]}-636x393.${imageandext[1]}"
                    }
                    adapters.setData(dataRecipe)
                }
            }
        }

        adapters.onItemClick = {
            val intent = Intent(this,RecipeDetailActivity::class.java)
            intent.putExtra("recipe",it)
            startActivity(intent)
        }

        rv_recipe.apply {
            layoutManager = GridLayoutManager(this@RecipeSearchResultActivity,2)
            adapter = adapters
        }
    }
}