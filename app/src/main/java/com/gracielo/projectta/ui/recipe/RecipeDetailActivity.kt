package com.gracielo.projectta.ui.recipe

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gracielo.projectta.R
import com.gracielo.projectta.data.model.recipe.detail.RecipeDetailResponseItem
import com.gracielo.projectta.data.model.recipe.search.MissedIngredient
import com.gracielo.projectta.data.model.recipe.search.RecipeResponseItem
import com.gracielo.projectta.data.source.local.entity.ShoppingListEntity
import com.gracielo.projectta.data.source.local.entity.UserNutrientsEntity
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityRecipeDetailBinding
import com.gracielo.projectta.ui.homepage.HomeActivity
import com.gracielo.projectta.viewmodel.ShoppingListViewModel
import com.gracielo.projectta.viewmodel.UserViewModel
import com.gracielo.projectta.viewmodel.ViewModelFactory
import kotlinx.coroutines.delay


class RecipeDetailActivity : AppCompatActivity() {
    private lateinit var binding :ActivityRecipeDetailBinding
    private var recipeData: RecipeResponseItem? = null
    private var recipeDataDetail: RecipeDetailResponseItem? = null
    lateinit var viewModel: UserViewModel
    lateinit var shoppingListViewModel: ShoppingListViewModel
    private var idUser = ""
    private lateinit var idRecipe:String
    private var listShoppingList = mutableListOf<ShoppingListEntity>()
    private val apiServices = ApiServices()
    private var calories=0.0; var sugar=0.0; var fat=0.0; var carbohydrate = 0.0
    var idListIngridients = mutableListOf<Int>()
    var hasMissing = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = obtainUserViewModel(this)
        shoppingListViewModel = obtainShoppingListViewModel(this)
        viewModel.getUser()?.observe(this){
            idUser = it.id
        }
        if(idUser!=""){
            shoppingListViewModel.getShoppingList(idUser).observe(this){
                Log.d("DataShoppingList",it.toString())
                listShoppingList.addAll(it)
            }
        }
        else{
           withDelay(500){
               shoppingListViewModel.getShoppingList(idUser).observe(this){
                    Log.d("DataShoppingList",it.toString())
                    listShoppingList.addAll(it)
               }
           }
        }

        var fromSimilar = false
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

        var listMissing = mutableListOf<MissedIngredient>()
        if(intent.hasExtra("recipe")){
            fromSimilar=false
            recipeData = intent.getParcelableExtra("recipe")
            idRecipe = recipeData?.id.toString()
            recipeData?.usedIngredients?.forEach {
                idListIngridients.add(it.id)
            }
            recipeData?.UnusedIngredients?.forEach {
                idListIngridients.add(it.id)
            }
            if(recipeData?.missedIngredientCount!! >0){
                listMissing.addAll(recipeData?.missedIngredients!!)
            }
        }
        var listIngredientsSearch = mutableListOf<Int>()
        if(intent.hasExtra("SimilarRecipeID")){
            fromSimilar=true
            idRecipe=intent.getIntExtra("SimilarRecipeID",0).toString()
            listIngredientsSearch.addAll(intent.getIntArrayExtra("listIngredients")!!.toList())
            idListIngridients.addAll(listIngredientsSearch)
        }


        apiServices.getRecipeDetail(idRecipe){
            if(it?.code==1){
                recipeDataDetail= it.data
//                recipeDataDetail?.image = recipeData!!.image
                binding.imageView6.setOnClickListener {
                    finish()
                }
                binding.txtRecipeNameDetail.text = recipeDataDetail?.title

                Glide
                    .with(applicationContext)
                    .load(recipeDataDetail?.image)
                    .apply(RequestOptions().override(600, 600))
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(binding.recipeImageDetail)



                val listIngredientsString = mutableListOf<String>()
                for (i in recipeDataDetail!!.extendedIngredients.indices) {
                    val recipe = recipeDataDetail!!.extendedIngredients[i]
                    var ingredientsStringItem = "\u2022 ${recipeDataDetail!!.extendedIngredients[i].original}"
                    if(!fromSimilar){
                        for(j in listMissing.indices){
                            if(listMissing[j].id == recipeDataDetail!!.extendedIngredients[i].id ){
                                hasMissing=true
                                ingredientsStringItem+= " ----- (Missing Ingredients)"
                            }
                        }
                    }
                    else if(fromSimilar){
                        var isMissing = true
                        for(j in listIngredientsSearch.indices){
                            if(listIngredientsSearch[j] == recipeDataDetail!!.extendedIngredients[i].id ){
                                isMissing=false
                            }
                        }
                        if(isMissing){
                            ingredientsStringItem+= " ----- (Missing Ingredients)"
                            hasMissing=true
                        }
                    }
                    listIngredientsString.add(ingredientsStringItem)
                }
                binding.buttonAddShoppingList.setOnClickListener {
                    AlertDialog.Builder(this)
                        .setTitle("Add To Shopping List")
                        .setMessage("Are you sure you want to add the ingredients list to your shopping list ? ") // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton("Yes"
                        ) { _, _ ->
                            var listIngredient = ""
                            for (i in recipeDataDetail?.extendedIngredients?.indices!!){
                                if(i != recipeDataDetail!!.extendedIngredients.size-1){
                                    listIngredient += "${recipeDataDetail!!.extendedIngredients[i].name} /? ${recipeDataDetail!!.extendedIngredients[i].original} ;-"
                                }
                                else listIngredient += "${recipeDataDetail!!.extendedIngredients[i].name} /? ${recipeDataDetail!!.extendedIngredients[i].original}"
                            }
//                            recipeDataDetail?.extendedIngredients?.forEach {
//                                listIngredient.add(it.name)
//                            }
                            val idShoppingList = "${idUser}-${idRecipe}"
                            val shoppingListEntity = ShoppingListEntity(idShoppingList,idUser,idRecipe,recipeDataDetail!!.title,listIngredient)
                            if(listShoppingList.isEmpty()){
                                shoppingListViewModel.insert(shoppingListEntity)
                            }
                            else{
                                var checkRecipe = true
                                for(i in listShoppingList.indices){
                                    if(listShoppingList[i].id_recipe==idRecipe){
                                        checkRecipe=false
                                    }
                                }
                                if(checkRecipe){
                                    shoppingListViewModel.insert(shoppingListEntity)
                                    Toast.makeText(this,"Succesfully Add Recipe Ingredients List To Shopping List",Toast.LENGTH_SHORT).show()
                                }
                                else if (!checkRecipe){
                                    Toast.makeText(this,"This Recipe Ingredients List Already in Your Shopping List",Toast.LENGTH_SHORT).show()
                                }
                            }


                        } // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
//            .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
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

                binding.textTime.text="${recipeDataDetail?.readyInMinutes} Minutes"
                binding.txtServingsRecipe.text= "${recipeDataDetail?.servings} Servings"

                for(i in recipeDataDetail?.nutrition!!.nutrients.indices){
//                    Log.d("Nutrition Data", recipeDataDetail?.nutrition!!.nutrients[i].toString())
                    val nutrients = recipeDataDetail?.nutrition!!.nutrients[i]
                    if(nutrients.name=="Calories"){ binding.txtCaloriesRecipe.text = "${nutrients.amount} ${nutrients.unit}"; calories = nutrients.amount}
                    else if(nutrients.name=="Fat"){ binding.txtFatRecipe.text = "${nutrients.amount} ${nutrients.unit}"; fat = nutrients.amount}
                    else if(nutrients.name=="Carbohydrates"){ binding.txtCarbohydrateRecipe.text = "${nutrients.amount} ${nutrients.unit}" ; carbohydrate = nutrients.amount}
                    else if(nutrients.name=="Sugar")sugar= nutrients.amount
                }
                var additionalInfo =""
                if(recipeDataDetail!!.diets.isEmpty()){
                    additionalInfo =" - "
                }
                else {
                    for(i in recipeDataDetail!!.diets.indices){
                        if(i< recipeDataDetail!!.diets.size-1){
                            additionalInfo += "${recipeDataDetail!!.diets[i]} - "
                        }
                        else additionalInfo += recipeDataDetail!!.diets[i]
                    }
                }

                binding.txtAdditionalInfoRecipe.text = additionalInfo
            }
        }

        apiServices.getSimilarRecipe(idRecipe){
            if(it?.code==1){
                val similarRecipe = it.data
                val similarRecipeAdapter = SimilarRecipeAdapter()
                similarRecipeAdapter.setData(similarRecipe)
                binding.rvListSimilarRecipe.apply {
                    layoutManager = LinearLayoutManager(this@RecipeDetailActivity,LinearLayoutManager.HORIZONTAL,false)
                    adapter=similarRecipeAdapter
                }
                similarRecipeAdapter.onItemClick = {
                    val idRecipe = it.id
                    val intent = Intent(this,RecipeDetailActivity::class.java)
                    intent.putExtra("SimilarRecipeID",idRecipe)
                    intent.putExtra("listIngredients",idListIngridients.toIntArray())
                    Log.d("dataIngredients",idListIngridients.toString())
                    startActivity(intent)
//                    apiServices.getRecipeDetail(idRecipe = idRecipe.toString()){
//                        if(it?.code==1){
//
//                            startActivity(intent)
//                        }
//                    }
                }
            }
        }

        binding.fabMakeRecipe.setOnClickListener {
            var messageString :String =""
            val stringMissing = "there are still missing ingredients, it will cause inaccuracies in nutritional data between the recipe and your food. Continuing to eat it?"
            val stringNotMissing = "Are you sure want to eat this? "
            if(hasMissing){
                messageString = "there are still missing ingredients, it will cause inaccuracies in nutritional data between the recipe and your food. Continuing to eat it?"
            }
            else if(!hasMissing){
                messageString ="Are you sure want to eat this? "
            }
            AlertDialog.Builder(this)
                .setTitle("Eat This Food")
                .setMessage(messageString) // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("Yes"
                ) { _, _ ->
                    eatFood()
                    val intent = Intent(this,HomeActivity::class.java)
                    finish()
                    startActivity(intent)
                } // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
//            .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }
        supportActionBar?.hide()

    }

    private fun getColoredSpanned(text: String, color: String): String? {
        return "<font color=$color>$text</font>"
    }
    private fun obtainUserViewModel(activity: AppCompatActivity): UserViewModel {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(UserViewModel::class.java)
    }

    private fun obtainShoppingListViewModel(activity: AppCompatActivity): ShoppingListViewModel {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(ShoppingListViewModel::class.java)
    }
    fun withDelay(delay : Long, block : () -> Unit) {
        Handler().postDelayed(Runnable(block), delay)
    }
    fun eatFood(){
        var userNutrientsEntity:UserNutrientsEntity? = null
        viewModel.getUserNutrients().observe(this@RecipeDetailActivity){
            if(it!=null){
                userNutrientsEntity = it
            }
        }
        withDelay(500){
            if(userNutrientsEntity!=null){
                userNutrientsEntity!!.lemak_consumed+=fat
                userNutrientsEntity!!.karbo_consumed+=carbohydrate
                userNutrientsEntity!!.gula_consumed+=sugar
                userNutrientsEntity!!.kalori_consumed+=calories
                viewModel.updateUserNutrients(userNutrientsEntity!!)
            }
        }
    }
}