package com.gracielo.projectta.ui.ingredients

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.gracielo.projectta.data.source.local.entity.Ingredients
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityIngridientsListBinding
import com.gracielo.projectta.ui.recipe.RecipeSearchResultActivity
import com.gracielo.projectta.viewmodel.IngredientsViewModel
import com.gracielo.projectta.viewmodel.ViewModelFactory
import com.gracielo.projectta.vo.Status

class IngridientsList : AppCompatActivity(), IngridientsItemCallback {

    private lateinit var binding:ActivityIngridientsListBinding
    private lateinit var  viewModel: IngredientsViewModel
    private lateinit var  adapters: IngridientsListAdapters
    val apiServices= ApiServices()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityIngridientsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel= obtainViewModel(this)

        val fabItemsSelected = binding.fabListIngredientsSelected
        val fabSearchRecipe = binding.fabSearchRecipe
        val recyclerView = binding.rvIngridientsList
        val fastScroller = binding.fastScroller
        adapters = IngridientsListAdapters(this@IngridientsList)
        fastScroller.setRecyclerView(recyclerView)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapters
        }

        viewModel.getIngredients().observe(this){ listIngredients ->
            if(listIngredients!=null){
                when(listIngredients.status){
                    Status.SUCCESS -> {
                        recyclerView.adapter?.let { adapters ->
                            when (adapters) {
                                is IngridientsListAdapters -> {
                                    adapters.submitList(listIngredients.data)
                                    adapters.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                    Status.ERROR -> {
                        Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        var stringIngredients:String=""

        viewModel.getSelectedIngredients().observe(this){
            if (it.isEmpty()){
                fabSearchRecipe.visibility= View.INVISIBLE
                fabItemsSelected.visibility= View.INVISIBLE
            }
            else{
                fabSearchRecipe.visibility= View.VISIBLE
                fabItemsSelected.visibility= View.VISIBLE
                fabItemsSelected.text= "${it.size} Items Selected"
                var ctr=1
                stringIngredients=""
                it?.forEach {ingredients->
                    stringIngredients += if(ctr==it.size){
                        ingredients.name
                    } else{
                        "${ingredients.name},+"
                    }
                    ctr++
                }
            }
        }

        fabSearchRecipe.setOnClickListener {
//            val ingredientsListParam = viewModel.getSelectedIngredients().value
//
//            var stringIngredients:String =""
//            var ctr=1
//            ingredientsListParam?.forEach {
//                stringIngredients += if(ctr==ingredientsListParam.size){
//                    it.name
//                } else{
//                    "${it.name},+"
//                }
//                ctr++
//            }
            if(stringIngredients!=""){
                val intent = Intent(this,RecipeSearchResultActivity::class.java)
                intent.putExtra("paramIngredients", stringIngredients)
                startActivity(intent)
            }
            apiServices.searchRecipe(stringIngredients){
                Log.d("DebugSearchRecipe",it?.data.toString())
            }
        }
        fabItemsSelected.setOnClickListener {
            val intent = Intent (this, SelectedIngredientsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun obtainViewModel(activity:AppCompatActivity): IngredientsViewModel {
        val factory: ViewModelFactory? = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory!!).get(IngredientsViewModel::class.java)
    }

    override fun onItemClicked(data: Ingredients) {
        data.isSelected= !data.isSelected
        Log.d("dataIngredients",data.toString())
        viewModel.updateIngredients(data)
    }
}