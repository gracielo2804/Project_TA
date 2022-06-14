package com.gracielo.projectta.ui.recipe

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gracielo.projectta.R
import com.gracielo.projectta.data.model.recipe.detail.RecipeDetailResponseItem
import com.gracielo.projectta.data.model.recipe.search.MissedIngredient
import com.gracielo.projectta.data.model.recipe.search.RecipeResponseItem
import com.gracielo.projectta.data.source.local.entity.FavouriteRecipeEntity
import com.gracielo.projectta.data.source.local.entity.ShoppingListEntity
import com.gracielo.projectta.data.source.local.entity.UserEntity
import com.gracielo.projectta.data.source.local.entity.UserNutrientsEntity
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityRecipeDetailBinding
import com.gracielo.projectta.ui.homepage.HomeActivity
import com.gracielo.projectta.util.FunHelper
import com.gracielo.projectta.util.FunHelper.observeOnce
import com.gracielo.projectta.viewmodel.FavRecipeViewModel
import com.gracielo.projectta.viewmodel.ShoppingListViewModel
import com.gracielo.projectta.viewmodel.UserViewModel
import com.gracielo.projectta.viewmodel.ViewModelFactory
import com.gracielo.projectta.vo.Status


class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var binding :ActivityRecipeDetailBinding
    private var recipeData: RecipeResponseItem? = null
    private var recipeDataDetail: RecipeDetailResponseItem? = null
    lateinit var viewModel: UserViewModel
    lateinit var shoppingListViewModel: ShoppingListViewModel
    lateinit var favRecipeViewModel: FavRecipeViewModel
    private var idUser = ""
    private lateinit var idRecipe:String
    private var helper = FunHelper
    private var listShoppingList = mutableListOf<ShoppingListEntity>()
    private var listFavRecipe = mutableListOf<FavouriteRecipeEntity>()
    private val apiServices = ApiServices()
    private var calories=0.0; var sugar=0.0; var fat=0.0; var carbohydrate = 0.0;var protein=0.0
    var idListIngridients = mutableListOf<Int>()
    var hasMissing = false
    lateinit var datauser:UserEntity
    var isFavorite=false
    companion object
    {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "TugasAkhir channel"
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        datauser = UserEntity("1","1","1","1","0","",1,1,"1",1.1,1,1)

        viewModel = obtainUserViewModel(this)
        shoppingListViewModel = obtainShoppingListViewModel(this)
        favRecipeViewModel = helper.obtainFavRecipeViewModel(this)
        viewModel.getUser().observe(this){
            idUser = it.id
            datauser=it
            shoppingListViewModel.getShoppingList(idUser).observe(this){
                Log.d("DataShoppingList",it.toString())
                listShoppingList.addAll(it)
            }
            if(datauser.tipe!="0"){
                ObserveFavRecipe()
                withDelay(1000){
                    if(listFavRecipe.isEmpty()){
                        binding.imageFavouriteRecipe.setImageResource(R.drawable.favourite_border)
                        binding.imageFavouriteRecipe.setTag(R.drawable.favourite_border)
                    }
                    else{
                        for(i in listFavRecipe.indices){
                            if(listFavRecipe[i].id_recipe==idRecipe){
                                binding.imageFavouriteRecipe.setImageResource(R.drawable.favourite_fill)
                                binding.imageFavouriteRecipe.setTag(R.drawable.favourite_fill)
                            }
                        }

                    }
                }

            }
        }

        binding.imageFavouriteRecipe.setOnClickListener {
            if(datauser.tipe=="0"){
                AlertDialog.Builder(this)
                    .setTitle("Limited Features")
                    .setMessage("Please buy membership plan to use this features") // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton("Close"
                    ) { _, _ ->
                    } // A null listener allows the button to dismiss the dialog and take no further action.
                    .show()
            }
            else{
                Log.d("ClickImage","Click")
                if(it.tag==R.drawable.favourite_border){
                    Log.d("ClickImage","Masuk Add")
                    val idFavRecipe = "${idUser} - ${recipeDataDetail?.id}"
                    val dataFavRecipe = FavouriteRecipeEntity(idFavRecipe,idUser,recipeDataDetail?.id.toString())
                    favRecipeViewModel.insertFavRecipe(dataFavRecipe)
                    apiServices.InsertUpdateFavouriteRecipe(dataFavRecipe){}
                    binding.imageFavouriteRecipe.setImageResource(R.drawable.favourite_fill)
                    binding.imageFavouriteRecipe.setTag(R.drawable.favourite_fill)
                    Toast.makeText(this@RecipeDetailActivity,"Added to Favourite",Toast.LENGTH_SHORT).show()

                }
                else if(it.tag==R.drawable.favourite_fill){
                    Log.d("ClickImage","Masuk Remove")
                    val idFavRecipe = "${idUser} - ${recipeDataDetail?.id}"
                    val dataFavRecipe = FavouriteRecipeEntity(idFavRecipe,idUser,recipeDataDetail?.id.toString())
                    favRecipeViewModel.deleteFavRecipe(dataFavRecipe)
                    apiServices.InsertUpdateFavouriteRecipe(dataFavRecipe){}
                    binding.imageFavouriteRecipe.setImageResource(R.drawable.favourite_border)
                    binding.imageFavouriteRecipe.setTag(R.drawable.favourite_border)
                    Toast.makeText(this@RecipeDetailActivity,"Removed From Favourite",Toast.LENGTH_SHORT).show()
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
        if(intent.hasExtra("idrecipe")){
            Log.d("IDRECIPEDETAIL","Masuk acitivty detail")
            idRecipe = intent.getStringExtra("idrecipe").toString()
            apiServices.getRecipeDetail(idRecipe){
                if(it?.code==1){
                    recipeDataDetail= it.data
                    apiServices.insertRecipeDetail(recipeDataDetail!!) {}

                    var dataIngredients=""
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
                        var ingredientsStringItem = "\u2022 ${recipeDataDetail!!.extendedIngredients[i].original}"
                        if(i != recipeDataDetail!!.extendedIngredients.size-1){
                            dataIngredients += "${recipeDataDetail!!.extendedIngredients[i].name}, "
                        }
                        else{
                            dataIngredients += recipeDataDetail!!.extendedIngredients[i].name
                        }


                        if(!fromSimilar){
                            for(j in listMissing.indices){
                                if(listMissing[j].id == recipeDataDetail!!.extendedIngredients[i].id ){
                                    hasMissing=true
//                                ingredientsStringItem+= " ----- (Missing Ingredients)"
                                }
                            }
                        }
//                        else if(fromSimilar){
//                            var isMissing = true
//                            for(j in listIngredientsSearch.indices){
//                                if(listIngredientsSearch[j] == recipeDataDetail!!.extendedIngredients[i].id ){
//                                    isMissing=false
//                                }
//                            }
//                            if(isMissing){
////                            ingredientsStringItem+= " ----- (Missing Ingredients)"
//                                hasMissing=true
//                            }
//                        }
                        listIngredientsString.add(ingredientsStringItem)
                    }
//                apiServices.insertUserSearchRecipe(idUser,idRecipe,dataIngredients,recipeDataDetail!!.title){}
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
                                apiServices.insertUpdateShoppingList(shoppingListEntity){}
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
                        else if(nutrients.name=="Sugar"){binding.txtSugarRecipe.text = "${nutrients.amount} ${nutrients.unit}";sugar= nutrients.amount}
                        else if(nutrients.name=="Protein"){binding.txtProteinRecipe.text = "${nutrients.amount} ${nutrients.unit}";protein= nutrients.amount}
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
        }
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
                apiServices.insertRecipeDetail(recipeDataDetail!!) {}

                var dataIngredients=""
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
                    var ingredientsStringItem = "\u2022 ${recipeDataDetail!!.extendedIngredients[i].original}"
                    if(i != recipeDataDetail!!.extendedIngredients.size-1){
                        dataIngredients += "${recipeDataDetail!!.extendedIngredients[i].name}, "
                    }
                    else{
                        dataIngredients += recipeDataDetail!!.extendedIngredients[i].name
                    }


                    if(!fromSimilar){
                        for(j in listMissing.indices){
                            if(listMissing[j].id == recipeDataDetail!!.extendedIngredients[i].id ){
                                hasMissing=true
//                                ingredientsStringItem+= " ----- (Missing Ingredients)"
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
//                            ingredientsStringItem+= " ----- (Missing Ingredients)"
                            hasMissing=true
                        }
                    }
                    listIngredientsString.add(ingredientsStringItem)
                }
//                apiServices.insertUserSearchRecipe(idUser,idRecipe,dataIngredients,recipeDataDetail!!.title){}
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
                            apiServices.insertUpdateShoppingList(shoppingListEntity){}
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
                    else if(nutrients.name=="Sugar"){binding.txtSugarRecipe.text = "${nutrients.amount} ${nutrients.unit}";sugar= nutrients.amount}
                    else if(nutrients.name=="Protein"){binding.txtProteinRecipe.text = "${nutrients.amount} ${nutrients.unit}";protein= nutrients.amount}
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
            messageString=stringNotMissing
            AlertDialog.Builder(this)
                .setTitle("Eat This Food ?")
                .setMessage(messageString) // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("Yes"
                ) { _, _ ->
                    eatFood()
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
            var fatTemp=0.0;var carbohydrateTemp=0.0;var sugarTemp=0.0
            var caloriesTemp=0.0;var proteintemp=0.0
            var showDialogBool = false
            var overFat=false;var overCarbohydrate=false;var overCalories=false
            var overProtein=false; var overSugar=false
            if(userNutrientsEntity!=null){

                fatTemp =  userNutrientsEntity!!.lemak_consumed+fat
                if(fatTemp>userNutrientsEntity!!.maxLemak){
                    overFat=true
                    showDialogBool=true
                }

                carbohydrateTemp =  userNutrientsEntity!!.karbo_consumed+carbohydrate
                if(carbohydrateTemp>userNutrientsEntity!!.maxKarbo){
                    overCarbohydrate=true
                    showDialogBool=true
                }

                sugarTemp = sugar+ userNutrientsEntity!!.gula_consumed
                if(sugarTemp>userNutrientsEntity!!.maxGula){
                    overSugar = true
                    showDialogBool=true
                }

                caloriesTemp = userNutrientsEntity!!.kalori_consumed+calories
                if(caloriesTemp>userNutrientsEntity!!.maxKalori){
                    overCalories = true
                    showDialogBool=true
                }

               proteintemp = userNutrientsEntity!!.protein_consumed+protein
                if(proteintemp>userNutrientsEntity!!.maxProtein){
                    overProtein = true
                    showDialogBool=true
                }
            }
            var message=""
            if(showDialogBool){
                var messageTemp = "There Will Be Excess "
                if(overCalories)messageTemp+="Calories, "
                if(overFat)messageTemp+="Fat, "
                if(overCarbohydrate)messageTemp+="Carbohydrate, "
                if(overSugar)messageTemp+="Sugar, "
                if(overProtein)messageTemp+="Protein, "
                var messageLength = messageTemp.length
                for (i in messageTemp.indices){
                    if(i < messageLength - 2){
                        message+=messageTemp[i]
                    }
                }
                message+=". Still Continue To eat ?"
                AlertDialog.Builder(this)
                    .setTitle("Excess Nutrients to be Consumed ")
                    .setMessage(message) // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton("Yes"
                    ) { _, _ ->
                        userNutrientsEntity!!.lemak_consumed = String.format("%.2f", fatTemp).toDouble()
                        userNutrientsEntity!!.karbo_consumed = String.format("%.2f", carbohydrateTemp).toDouble()
                        userNutrientsEntity!!.gula_consumed = String.format("%.2f", sugarTemp).toDouble()
                        userNutrientsEntity!!.kalori_consumed = String.format("%.2f", caloriesTemp).toDouble()
                        userNutrientsEntity!!.protein_consumed = String.format("%.2f", proteintemp).toDouble()
                        viewModel.updateUserNutrients(userNutrientsEntity!!)
                        if(overCalories)sendNotification("Calories",20001)
                        if(overFat)sendNotification("Fat",20002)
                        if(overCarbohydrate)sendNotification("Carbohydrate",20003)
                        if(overSugar)sendNotification("Sugar",20004)
                        if(overProtein)sendNotification("Protein",20005)
                        val intent = Intent(this,HomeActivity::class.java)
                        finish()
                        startActivity(intent)
                    } // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
//            .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
            }
            else{
                userNutrientsEntity!!.lemak_consumed = String.format("%.2f", fatTemp).toDouble()
                userNutrientsEntity!!.karbo_consumed = String.format("%.2f", carbohydrateTemp).toDouble()
                userNutrientsEntity!!.gula_consumed = String.format("%.2f", sugarTemp).toDouble()
                userNutrientsEntity!!.kalori_consumed = String.format("%.2f", caloriesTemp).toDouble()
                userNutrientsEntity!!.protein_consumed = String.format("%.2f", proteintemp).toDouble()
                viewModel.updateUserNutrients(userNutrientsEntity!!)
                val intent = Intent(this,HomeActivity::class.java)
                finish()
                startActivity(intent)
            }


        }
    }
    private fun ObserveFavRecipe(){
        favRecipeViewModel.getFavRecipe(idUser).observe(this
        ) { listFavRecipeData ->
            if (listFavRecipeData != null) {
                when (listFavRecipeData.status) {
                    Status.SUCCESS -> {
                        listFavRecipe.clear()
                        val dataFavorite = listFavRecipeData.data
                        listFavRecipe.addAll(dataFavorite!!)
                        var fav = false
                        Log.d("listFav", "List Fav Size ${dataFavorite.size}")
                        for (i in listFavRecipe.indices) {
                            Log.d("listFav", "List Fav ${listFavRecipe[i].id_recipe} - ${recipeDataDetail?.id}")
                            if (recipeDataDetail != null) {
                                if (recipeDataDetail?.id.toString() == listFavRecipe[i].id_recipe) {
                                    Log.d("listFav", "Masuk Sama Favorite")
                                    fav = true
                                }
                                if(idRecipe==listFavRecipe[i].id_recipe){
                                    Log.d("listFav", "Masuk Sama Favorite")
                                    fav = true
                                }
                            }
                        }
                        if (fav) {
                            binding.imageFavouriteRecipe.setImageResource(R.drawable.favourite_fill)
                            binding.imageFavouriteRecipe.setTag(R.drawable.favourite_fill) }
                        else {
                            binding.imageFavouriteRecipe.setImageResource(R.drawable.favourite_border)
                            binding.imageFavouriteRecipe.setTag(R.drawable.favourite_border)
                        }
                    }
                    Status.ERROR -> {
                        Toast.makeText(
                            this@RecipeDetailActivity,
                            "Check your internet connection",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
    fun sendNotification(title:String,notificationID:Int){
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_alert)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.notification_alert))
            .setContentTitle("Daily Nutrition")
            .setContentText("Your Daily ${title} Consumption Exceeds Your Daily Limit")
            .setVibrate( longArrayOf(0, 250, 500))
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /* Create or update. */
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = CHANNEL_NAME
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(0, 250, 500)
            mBuilder.setChannelId(CHANNEL_ID)
            mNotificationManager.createNotificationChannel(channel)
        }

        val notification = mBuilder.build()
        mNotificationManager.notify(notificationID, notification)
    }
}