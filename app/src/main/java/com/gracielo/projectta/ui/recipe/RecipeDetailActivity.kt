package com.gracielo.projectta.ui.recipe

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gracielo.projectta.R
import com.gracielo.projectta.data.model.recipe.detail.RecipeDetailResponseItem
import com.gracielo.projectta.data.model.recipe.search.RecipeResponseItem
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityRecipeDetailBinding


class RecipeDetailActivity : AppCompatActivity() {
    private lateinit var binding :ActivityRecipeDetailBinding
    private var recipeData: RecipeResponseItem? = null
    private var recipeDataDetail: RecipeDetailResponseItem? = null
    private lateinit var idRecipe:String
    private val apiServices = ApiServices()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val listView = binding.listIngredientsRecipe
        listView.setOnTouchListener { v, event ->
            val action = event.action
            when (action) {
                MotionEvent.ACTION_DOWN ->                 // Disallow ScrollView to intercept touch events.
                    v.parent.requestDisallowInterceptTouchEvent(true)
                MotionEvent.ACTION_UP ->                 // Allow ScrollView to intercept touch events.
                    v.parent.requestDisallowInterceptTouchEvent(false)
            }

            // Handle ListView touch events.
            v.onTouchEvent(event)
            true
        }
        val listViewSteps = binding.listStepsRecipe

        if(intent.hasExtra("recipe"))recipeData = intent.getParcelableExtra("recipe")
        idRecipe = recipeData?.id.toString()
        val listMissing = recipeData?.missedIngredients

        apiServices.getRecipeDetail(idRecipe){
            if(it?.code==1){
                recipeDataDetail= it.data
                recipeDataDetail?.image = recipeData!!.image
                binding.imageView6.setOnClickListener {
                    finish()
                }
                binding.txtRecipeNameDetail.text = recipeData?.title

                Glide
                    .with(applicationContext)
                    .load(recipeDataDetail?.image)
                    .apply(RequestOptions().override(600, 600))
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(binding.recipeImageDetail)

                var hasMissing = false

                val listIngredientsString = mutableListOf<String>()
                for (i in recipeDataDetail!!.extendedIngredients.indices) {
                    val recipe = recipeDataDetail!!.extendedIngredients[i]
                    var ingredientsStringItem = "\u2022 ${recipeDataDetail!!.extendedIngredients[i].original}"
                    for(j in listMissing!!.indices){
                        if(listMissing[j].id == recipeDataDetail!!.extendedIngredients[i].id ){
                            hasMissing=true
                            ingredientsStringItem+= " ----- (Missing Ingredients)"
                        }
                    }
                    listIngredientsString.add(ingredientsStringItem)
                }
                val listIngredients= ArrayAdapterCustom(this, listIngredientsString)
                listView.adapter = listIngredients

                val listStepString = mutableListOf<String>()
                for (i in recipeDataDetail!!.analyzedInstructions.indices) {
                    val stepData= recipeDataDetail!!.analyzedInstructions[i]
                    stepData.steps.forEach {
                        listStepString.add("${it.number}. ${it.step}")
                    }
                }
                val listStep = ArrayAdapterCustom(this,listStepString)
                listViewSteps.adapter=listStep
                listViewSteps.setOnTouchListener { v, event ->
                    val action = event.action
                    when (action) {
                        MotionEvent.ACTION_DOWN ->                 // Disallow ScrollView to intercept touch events.
                            v.parent.requestDisallowInterceptTouchEvent(true)
                        MotionEvent.ACTION_UP ->                 // Allow ScrollView to intercept touch events.
                            v.parent.requestDisallowInterceptTouchEvent(false)
                    }

                    // Handle ListView touch events.
                    v.onTouchEvent(event)
                    true
                }
            }
        }
        supportActionBar?.hide()

    }

    private fun getColoredSpanned(text: String, color: String): String? {
        return "<font color=$color>$text</font>"
    }
}