package com.gracielo.projectta.ui.homepage

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.gracielo.projectta.R
import com.gracielo.projectta.apriori.DataIterator
import com.gracielo.projectta.apriori.NamedItem
import com.gracielo.projectta.data.model.recipe.RecipeRecommendation
import com.gracielo.projectta.data.model.recipeCount.DataCountRecipe
import com.gracielo.projectta.data.source.local.entity.FavouriteRecipeEntity
import com.gracielo.projectta.data.source.local.entity.ShoppingListEntity
import com.gracielo.projectta.data.source.local.entity.UserEntity
import com.gracielo.projectta.data.source.local.entity.UserNutrientsEntity
import com.gracielo.projectta.data.source.remote.network.ApiConfig
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityHomeBinding
import com.gracielo.projectta.notification.DailyReminder
import com.gracielo.projectta.ui.history.HistoryHomeActivity
import com.gracielo.projectta.ui.ingredients.IngridientsList
import com.gracielo.projectta.ui.recipe.RecipeDetailActivity
import com.gracielo.projectta.ui.setting.SettingActivity
import com.gracielo.projectta.ui.shoppingList.ShoppingListActivity
import com.gracielo.projectta.util.FunHelper
import com.gracielo.projectta.viewmodel.FavRecipeViewModel
import com.gracielo.projectta.viewmodel.ShoppingListViewModel
import com.gracielo.projectta.viewmodel.UserViewModel
import com.gracielo.projectta.viewmodel.ViewModelFactory
import com.gracielo.projectta.vo.Status
import com.jakewharton.threetenabp.AndroidThreeTen
import de.mrapp.apriori.Apriori
import de.mrapp.apriori.Sorting
import de.mrapp.apriori.metrics.Confidence
import okhttp3.ResponseBody
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class HomeActivity : AppCompatActivity(), RecommendedItemCallback,FavouriteItemCallback {

    private lateinit var binding: ActivityHomeBinding
    private var dataUserNutrients:UserNutrientsEntity? = null
    lateinit var dataUser :UserEntity
    lateinit var viewModel: UserViewModel
    private lateinit var shoppingListViewModel: ShoppingListViewModel
    private lateinit var favRecipeViewModel: FavRecipeViewModel
    var apiServices = ApiServices()
    private var listRecipeCount = mutableListOf<DataCountRecipe>()
    var recipeRecommendation = mutableListOf<RecipeRecommendation>()
    private val helper =FunHelper
    private var listShoppingListUser = mutableListOf<ShoppingListEntity>()
    private var listFavRecipe = mutableListOf<FavouriteRecipeEntity>()
    val daily= DailyReminder()
    private lateinit var recomendationAdapter:RecommendedRecipeAdapter

    var listStringItemset = mutableListOf(arrayOf<String>())
    var listStringPredict = mutableListOf(arrayOf<String>())
    var aprioriListItemPredict = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AndroidThreeTen.init(this)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        binding.progressBar.visibility= View.INVISIBLE
        setContentView(binding.root)

        apiServices.getRecipeCount {
            if (it?.code==1){
                listRecipeCount.addAll(it.dataCountRecipe)
                for(i in 0..2){
                    val imageurl="https://spoonacular.com/recipeImages/${listRecipeCount[i].idRecipe}-556x370.jpg"
                    val recommendation = RecipeRecommendation(listRecipeCount[i].idRecipe.toInt()
                        ,listRecipeCount[i].recipeName,imageurl,"Most Searched")
                    recipeRecommendation.add(recommendation)
                }
            }
        }

        val receiver = ComponentName(applicationContext, DailyReminder::class.java)
        applicationContext.packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
        daily.setDailyReminder(this)

        dataUser = UserEntity("1","1","1","1","0","",1,1,"1",1.1,1,1)
        viewModel = obtainViewModel(this@HomeActivity)
        shoppingListViewModel = helper.obtainShoppingViewModel(this)
        favRecipeViewModel = helper.obtainFavRecipeViewModel(this)
        viewModel.getUser().observeOnce(this) {
            dataUser = it
            binding.headerHomePageTitle.text = "Hi, ${dataUser.name}"
            shoppingListViewModel.getShoppingList(it.id).observeOnce(this@HomeActivity){list->
                listShoppingListUser.addAll(list)
            }
            favRecipeViewModel.getFavRecipe(it.id).observe(this){
                if (it != null) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            listFavRecipe.clear()
                            val dataFavorite = it.data
                            listFavRecipe.addAll(dataFavorite!!)
                            binding.txtEmptyFavouriteHome.visibility=View.INVISIBLE
                            binding.rvListFavouriteRecipe.visibility=View.VISIBLE
                            var adapters = FavouriteRecipeAdapter(this)
                            adapters.setData(listFavRecipe)
                            binding.rvListFavouriteRecipe.apply {
                                layoutManager = LinearLayoutManager(this@HomeActivity,
                                    LinearLayoutManager.HORIZONTAL,false)
                                adapter=adapters
                            }
                        }
                        Status.ERROR -> {
                            Toast.makeText(
                                this@HomeActivity,
                                "Check your internet connection",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            Handler(Looper.getMainLooper()).postDelayed({
                for (i in listShoppingListUser.indices){
                    shoppingListViewModel.delete(listShoppingListUser[i])
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    apiServices.getShoppingListUser(it.id){response->
                        if (response != null) {
                            if(response.code==1){
                                if(response.dataShoppingList.isNotEmpty()){
                                    for(i in response.dataShoppingList.indices){
                                        var data = response.dataShoppingList[i]
                                        var shoppingList = ShoppingListEntity(data.idShoppingList,data.idUsers,data.idRecipe,data.recipeName,data.ingredientsList)
                                        shoppingListViewModel.insert(shoppingList)
                                    }
                                }
                                else{
                                    binding.txtEmptyFavouriteHome.visibility=View.VISIBLE
                                }
                            }
                        }
                    }
                },500)
                Log.d("RVadapter","Masuk Adapter Atas")
                recomendationAdapter = RecommendedRecipeAdapter(this@HomeActivity)
                recomendationAdapter.setData(recipeRecommendation)
                binding.rvListRecommendedRecipe.apply {
                    adapter=recomendationAdapter
                    layoutManager=LinearLayoutManager(this@HomeActivity,
                        LinearLayoutManager.HORIZONTAL,false)
                }


            },1000)
    //            binding.txtDashboardKalori.text = "0/${dataUser?.kalori}"
        }
        if(dataUser.height==1){
            Handler(Looper.getMainLooper()).postDelayed({
                observeuserNutrient()
            },500)
        }
        else {
            observeuserNutrient()
        }


        val fabAdd = binding.fabAdd
        fabAdd.setOnClickListener {
            intent = Intent(this, IngridientsList::class.java)
            startActivity(intent)
        }

        val bottomMenu = binding.bottomnavigationbar
        bottomMenu.selectedItemId=R.id.mHome
        bottomMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.mHome -> {
                    //Do Nothing cz this is the activity currently active
                }
                R.id.mShopList -> {
                    val intent = Intent(this,ShoppingListActivity::class.java)
                    finish()
                    startActivity(intent)
                }
                R.id.mHistory -> {
                    val intent = Intent(this, HistoryHomeActivity::class.java)
                    finish()
                    startActivity(intent)
                }
                R.id.mPerson -> {
                    val intent = Intent(this,SettingActivity::class.java)
                    finish()
                    startActivity(intent)
                }
            }
           true
        }


        supportActionBar?.hide()
        Handler(Looper.getMainLooper()).postDelayed({
            if(dataUser.tipe!="0"){
                val retrofit = ApiConfig.provideApiService()
                val path = "dataIngredient_${dataUser.id}.txt"
                apiServices.maketextdataingredients(dataUser.id){
                    if(it?.code==1){
                        retrofit.getUserSearchtxtFile(path).enqueue(
                            object : Callback<ResponseBody> {
                                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                    Log.d("dataapi",t.message.toString())
                                }
                                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                    if (response.isSuccessful) {
                                        val writtenToDisk = writeResponseBodyToDisk(response.body()!!)
                                        if(writtenToDisk){
                                            val minSupport = 0.07
                                            val apriori = Apriori.Builder<NamedItem>(minSupport).generateRules(0.75).create()
                                            val iterable = Iterable { DataIterator(File("${getExternalFilesDir(null)}${File.separator}dataIngredient_${dataUser.id}.txt")) }
                                            val output = apriori.execute(iterable)
                                            val ruleSet = output.ruleSet
                                            val frequentItemSets = output.frequentItemSets
                                            val sorting = Sorting.forItemSets().withOrder(Sorting.Order.DESCENDING)//
                                            val sortedFrequentItemSets = frequentItemSets.sort(sorting)
                                            val sortingRules = Sorting.forAssociationRules().withOrder(Sorting.Order.DESCENDING).byOperator(
                                                Confidence()
                                            )
                                            val sortedRules = ruleSet?.sort(sortingRules)
                                            sortedRules?.forEach {
                                                var itemsetTemp = it.body.toString()
                                                var predictTemp = it.head.toString()

                                                itemsetTemp = itemsetTemp.replace("[","")
                                                itemsetTemp= itemsetTemp.replace("]","")

                                                predictTemp = predictTemp.replace("[","")
                                                predictTemp = predictTemp.replace("]","")

                                                val listItemItemSetTemp=itemsetTemp.split(',')
                                                val listItemPredictTemp = predictTemp.split(',')

                                                listStringItemset.add(listItemItemSetTemp.toTypedArray())
                                                listStringPredict.add(listItemPredictTemp.toTypedArray())
                                            }
                                            listStringPredict.removeAt(0)
                                            listStringItemset.removeAt(0)
                                            listStringItemset.forEach{
                                                var stringShow=""
                                                it.forEach {contain->
                                                    val abc = contain.trim()
                                                    stringShow+="$abc "
                                                }
//                                Log.d("ListItemItemSet ",stringShow)
                                            }
                                            listStringPredict.forEach{
                                                var stringShow=""
                                                it.forEach {contain->
                                                    val abc = contain.trim()
                                                    stringShow+="$abc "
                                                }
//                                Log.d("ListItemPredict ",stringShow)
                                            }
                                            for(i in listStringItemset.indices){
                                                var listitemset = listStringItemset[i]
                                                var listpredict = listStringPredict[i]
                                                var stringItemSetPredict=""
                                                listitemset.forEach {
                                                        contain->
                                                    var abc = contain.trim()
                                                    abc = abc.replace("_"," ")
                                                    stringItemSetPredict+="$abc,"
                                                }
                                                for(j in listpredict.indices){
                                                    if(j == listpredict.size-1){
                                                        var abc = listpredict[j].trim()
                                                        abc = abc.replace("_"," ")
                                                        stringItemSetPredict+= abc
                                                    }
                                                    else{
                                                        val abc = listpredict[j].trim()
                                                        stringItemSetPredict+="$abc,"
                                                    }
                                                }
                                                aprioriListItemPredict.add(stringItemSetPredict)
                                            }
                                            aprioriListItemPredict.forEach{
                                                Log.d("ListItemSetPredictApriori ",it)
                                            }
                                            for (i in 0..2){
                                                if(dataUser.tipe=="1"){searchRecommendedBasic(i)}
                                                else if(dataUser.tipe=="2"){searchRecommendedVege(i)}
                                                else if(dataUser.tipe=="3"){searchRecommendedLowCarb(i)}
                                                else if(dataUser.tipe=="4"){searchRecommendedLowCarb(i);searchRecommendedVege(i)}

//                                Log.d("Data Recommendation",recipeRecommendation.toString())
                                            }
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                var recipeRecommendationTemp = mutableListOf<RecipeRecommendation>()
                                                var recipeRecommendationTempIsi = recipeRecommendation
                                                Log.d("sizeRec","${recipeRecommendationTempIsi.size}")
                                                Log.d("sizeRec", recipeRecommendationTempIsi.toString())
                                                for (q in 0..3){
                                                    Log.d("sizeRecLoop","${recipeRecommendationTempIsi.size}")
                                                    when (q) {
                                                        0 -> {
                                                            //Most Searched
                                                            for(j in recipeRecommendationTempIsi.indices){
                                                                if(recipeRecommendationTempIsi[j].tipe=="Most Searched"){
                                                                    recipeRecommendationTemp.add(recipeRecommendationTempIsi[j])
                                                                }
                                                            }
                                                        }
                                                        1 -> {
                                                            //Basic
                                                            for(a in recipeRecommendationTempIsi.indices){
                                                                if(recipeRecommendationTempIsi[a].tipe=="Basic"){
                                                                    var checkRemove=false
                                                                    var removeindex=0
                                                                    for(b in recipeRecommendationTemp.indices){
                                                                        if(recipeRecommendationTemp[b].recipeId==recipeRecommendationTempIsi[a].recipeId
                                                                            && recipeRecommendationTemp[b].tipe!="Basic"){
                                                                            checkRemove=true
                                                                            removeindex=b
                                                                        }
                                                                    }
                                                                    recipeRecommendationTemp.add(recipeRecommendationTempIsi[a])
                                                                    if(checkRemove)recipeRecommendationTemp.removeAt(removeindex)
                                                                }
                                                            }
                                                        }
                                                        2 -> {
                                                            //Vegetarian
                                                            for(a in recipeRecommendationTempIsi.indices){
                                                                if(recipeRecommendationTempIsi[a].tipe=="Vegetarian"){
                                                                    var checkRemove=false
                                                                    var removeindex=0
                                                                    for(b in recipeRecommendationTemp.indices){
                                                                        if(recipeRecommendationTemp[b].recipeId==recipeRecommendationTempIsi[a].recipeId
                                                                            && recipeRecommendationTemp[b].tipe!="Vegetarian"){
                                                                            checkRemove=true
                                                                            removeindex=b
                                                                        }
                                                                    }
                                                                    recipeRecommendationTemp.add(recipeRecommendationTempIsi[a])
                                                                    if(checkRemove)recipeRecommendationTemp.removeAt(removeindex)
                                                                }
                                                            }
                                                        }
                                                        3 -> {
                                                            //Low Carb
                                                            for(a in recipeRecommendationTempIsi.indices){
                                                                if(recipeRecommendationTempIsi[a].tipe=="Low Carb"){
                                                                    var checkRemove=false
                                                                    var removeindex=0
                                                                    for(b in recipeRecommendationTemp.indices){
                                                                        if(recipeRecommendationTemp[b].recipeId==recipeRecommendationTempIsi[a].recipeId
                                                                            && recipeRecommendationTemp[b].tipe!="Low Carb"){
                                                                            checkRemove=true
                                                                            removeindex=b
                                                                        }
                                                                    }
                                                                    recipeRecommendationTemp.add(recipeRecommendationTempIsi[a])
                                                                    if(checkRemove)recipeRecommendationTemp.removeAt(removeindex)
                                                                }
                                                            }
                                                        }
                                                    }
                                                    Log.d("recipeRec", recipeRecommendationTemp.toString())
                                                }
                                                recipeRecommendation.clear()
                                                recipeRecommendation.addAll(recipeRecommendationTemp)
                                                recomendationAdapter.setData(recipeRecommendation)
                                                recomendationAdapter.notifyDataSetChanged()
                                            },4000)


//                            Log.d("OutputApriori",sortedFrequentItemSets.toString())
//                            Log.d("OutputAprioriRule",sortedRules.toString())
                                        }
                                    } else {
                                        Log.d("FileDownload", "server contact failed")
                                    }
                                }
                            }
                        )
                    }
                }
            }
        },500)
    }

    private fun obtainViewModel(activity: AppCompatActivity): UserViewModel {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(UserViewModel::class.java)
    }
    fun searchRecommendedBasic(index:Int){
        apiServices.searchRecipeRecommendation(aprioriListItemPredict[index]){
            if(it?.code==1){
                val dataRecomendation = it.dataSearchRecommendation
                dataRecomendation.forEach {recipe->
                    val newRecommend= RecipeRecommendation(
                        recipe.id!!,recipe.title!!,
                        recipe.image!!,"Basic"
                    )
                    recipeRecommendation.add(newRecommend)
                }
            }
//            if(it?.dataSearchRecommendation?.id!=null){
//                val newRecommend= RecipeRecommendation(
//                    it.dataSearchRecommendation.id!!,it.dataSearchRecommendation.title!!,
//                    it.dataSearchRecommendation.image!!,"Basic"
//                )
//                recipeRecommendation.add(newRecommend)
//            }
        }
    }
    fun searchRecommendedLowCarb(index:Int){
        apiServices.searchRecipeLowCarb(aprioriListItemPredict[index]){
            if(it?.code==1){
                val dataRecomendation = it.dataSearchRecommendation
                dataRecomendation.forEach {recipe->
                    val newRecommend= RecipeRecommendation(
                        recipe.id!!,recipe.title!!,
                        recipe.image!!,"Low Carb"
                    )
                    recipeRecommendation.add(newRecommend)
                }
            }
//            if(it?.dataSearchRecommendation?.id!=null){
//                val newRecommend= RecipeRecommendation(
//                    it.dataSearchRecommendation.id!!,it.dataSearchRecommendation.title!!,
//                    it.dataSearchRecommendation.image!!,"Low Carb"
//                )
//                recipeRecommendation.add(newRecommend)
//            }
        }
    }
    fun searchRecommendedVege(index:Int){
        apiServices.searchRecipeVegetarianRecommendation(aprioriListItemPredict[index]){
            if(it?.code==1){
                val dataRecomendation = it.dataSearchRecommendation
                dataRecomendation.forEach {recipe->
                    val newRecommend= RecipeRecommendation(
                        recipe.id!!,recipe.title!!,
                        recipe.image!!,"Vegetarian"
                    )
                    recipeRecommendation.add(newRecommend)
                }
            }
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Exit App")
            .setMessage("Are you sure you want to exit the application ?") // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton("Yes"
            ) { _, _ ->
//                Toast.makeText(this,"Successfully Logout",Toast.LENGTH_SHORT).show()
              finishAffinity()
//                viewModel.getUser()?.removeObserver(){}

            } // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.no, null)
//            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }


    fun observeuserNutrient(){
        viewModel.getUserNutrients().observeOnce(this){
            dataUserNutrients = it
            Log.d("userNutrient",dataUserNutrients.toString())
            if(it==null){
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val formatted = current.format(formatter)
                val makslemak = String.format("%.1f", (dataUser.kalori*0.3)/9).toDouble()
                val maksgula = String.format("%.1f", (dataUser.kalori*0.2)/4).toDouble()
                var makskarbo:Double=0.0
                if(dataUser.gender == "Female"){
                    when {
                        dataUser.age<10 -> makskarbo=254.0
                        dataUser.age in 13..18 -> makskarbo = 275.0
                        dataUser.age in 19..29 -> makskarbo = 309.0
                        dataUser.age in 30..49 -> makskarbo = 323.0
                        dataUser.age in 50..64 -> makskarbo = 285.0
                        dataUser.age in 65..80 -> makskarbo = 285.0
                        dataUser.age > 80 -> makskarbo = 232.0
                    }
                }
                else if(dataUser.gender=="Male"){
                    when {
                        dataUser.age<10 -> makskarbo=254.0
                        dataUser.age in 10..12 -> makskarbo = 289.0
                        dataUser.age in 13..15 -> makskarbo = 340.0
                        dataUser.age in 16..18 -> makskarbo = 368.0
                        dataUser.age in 19..29 -> makskarbo = 375.0
                        dataUser.age in 30..49 -> makskarbo = 394.0
                        dataUser.age in 50..64 -> makskarbo = 349.0
                        dataUser.age in 65..80 -> makskarbo = 309.0
                        dataUser.age > 80 -> makskarbo = 248.0
                    }
                }
                val maksProtein = dataUser.weight*1.5
                val userNutrientsEntity = UserNutrientsEntity(dataUser.id,dataUser.kalori,0.0,makskarbo,0.0,maksgula,0.0,makslemak,0.0,maksProtein,0.0,formatted)
                viewModel.insertUserNutrients(userNutrientsEntity)
            }
            else {
                apiServices.saveUserNutrientHistory(dataUserNutrients!!){}
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val formatted = current.format(formatter)
                var userNutrientsEntity = it
                if(it.tanggal!=formatted){
                    userNutrientsEntity.gula_consumed=0.0
                    userNutrientsEntity.kalori_consumed=0.0
                    userNutrientsEntity.karbo_consumed=0.0
                    userNutrientsEntity.lemak_consumed=0.0
                    userNutrientsEntity.protein_consumed=0.0
                    userNutrientsEntity.tanggal=formatted
                }
                else {

                }
                viewModel.updateUserNutrients(userNutrientsEntity)
            }
            ChangeData()
        }
    }

    fun ChangeData(){
        if(dataUserNutrients!=null){
            binding.txtDashboardKalori.text = String.format("%.2f", dataUserNutrients!!.kalori_consumed) + "/" + dataUserNutrients!!.maxKalori.toString()
            binding.txtDashboardGula.text = String.format("%.2f", dataUserNutrients!!.gula_consumed) + "/" + dataUserNutrients!!.maxGula.toString()
            binding.txtDashboardGaram.text =String.format("%.2f", dataUserNutrients!!.karbo_consumed) + "/" + dataUserNutrients!!.maxKarbo.toString()
            binding.txtDashboardLemak.text = String.format("%.2f", dataUserNutrients!!.lemak_consumed) + "/" + dataUserNutrients!!.maxLemak.toString()
            binding.txtDashboardProtein.text = String.format("%.2f", dataUserNutrients!!.protein_consumed) + "/" + dataUserNutrients!!.maxProtein.toString()
            if(dataUserNutrients!!.kalori_consumed>dataUserNutrients!!.maxKalori){
                Glide.with(this).load(R.drawable.arrowup).into(binding.imgDashboardKalori)
                val Temp: Double = ((dataUserNutrients!!.kalori_consumed- dataUserNutrients!!.maxKalori)/dataUserNutrients!!.maxKalori) * 100
                binding.txtPersenKalori.text = Temp.toInt().toString() +"% Higher than daily limit"
            }
            else{
                Glide.with(this).load(R.drawable.arrowdown).into(binding.imgDashboardKalori)
                val Temp: Double = ((dataUserNutrients!!.kalori_consumed- dataUserNutrients!!.maxKalori)/dataUserNutrients!!.maxKalori) * 100
                binding.txtPersenKalori.text =(Temp.toInt()*-1).toString() +"% Lower than daily limit"
            }
            if(dataUserNutrients!!.gula_consumed>dataUserNutrients!!.maxGula){
                Glide.with(this).load(R.drawable.arrowup).into(binding.imgDashboardGula)
                val Temp = ((dataUserNutrients!!.gula_consumed-dataUserNutrients!!.maxGula)/dataUserNutrients!!.maxGula) * 100
                binding.txtPersenGula.text = Temp.toInt().toString() +"% Higher than daily limit"
            }
            else{
                Glide.with(this).load(R.drawable.arrowdown).into(binding.imgDashboardGula)
                val Temp = ((dataUserNutrients!!.gula_consumed-dataUserNutrients!!.maxGula)/dataUserNutrients!!.maxGula) * 100
                binding.txtPersenGula.text = (Temp.toInt()*-1).toString() +"% Lower than daily limit"
            }
            if(dataUserNutrients!!.karbo_consumed>dataUserNutrients!!.maxKarbo){
                Glide.with(this).load(R.drawable.arrowup).into(binding.imgDashboardGaram)
                val Temp = ((dataUserNutrients!!.karbo_consumed-dataUserNutrients!!.maxKarbo)/dataUserNutrients!!.maxKarbo) * 100
                binding.txtPersenGaram.text = Temp.toInt().toString() +"% Higher than daily limit"
            }
            else{
                Glide.with(this).load(R.drawable.arrowdown).into(binding.imgDashboardGaram)
                val Temp = ((dataUserNutrients!!.karbo_consumed-dataUserNutrients!!.maxKarbo)/dataUserNutrients!!.maxKarbo) * 100
                binding.txtPersenGaram.text = (Temp.toInt()*-1).toString() +"% Lower than daily limit"
            }
            if(dataUserNutrients!!.lemak_consumed > dataUserNutrients!!.maxLemak){
                Glide.with(this).load(R.drawable.arrowup).into(binding.imgDashboardLemak)
                val Temp = ((dataUserNutrients!!.lemak_consumed-dataUserNutrients!!.maxLemak)/dataUserNutrients!!.maxLemak) * 100
                binding.txtPersenLemak.text = Temp.toInt().toString() +"% Higher than daily limit"
            }
            else{
                Glide.with(this).load(R.drawable.arrowdown).into(binding.imgDashboardLemak)
                val Temp = ((dataUserNutrients!!.lemak_consumed-dataUserNutrients!!.maxLemak)/dataUserNutrients!!.maxLemak) * 100
                binding.txtPersenLemak.text = (Temp.toInt()*-1).toString() +"% Lower than daily limit"
            }
            if(dataUserNutrients!!.protein_consumed > dataUserNutrients!!.maxProtein){
                Glide.with(this).load(R.drawable.arrowup).into(binding.imgDashboardProtein)
                val Temp = ((dataUserNutrients!!.protein_consumed-dataUserNutrients!!.maxProtein)/dataUserNutrients!!.maxProtein) * 100
                binding.txtPersenProtein.text = Temp.toInt().toString() +"% Higher than daily limit"
            }
            else{
                Glide.with(this).load(R.drawable.arrowdown).into(binding.imgDashboardProtein)
                val Temp = ((dataUserNutrients!!.protein_consumed-dataUserNutrients!!.maxProtein)/dataUserNutrients!!.maxProtein) * 100
                binding.txtPersenProtein.text = (Temp.toInt()*-1).toString() +"% Lower than daily limit"
            }
        }
    }

    private fun writeResponseBodyToDisk(body: ResponseBody): Boolean {
        return try {
            // todo change the file location/name according to your needs
            val futureStudioIconFile =
                File("${getExternalFilesDir(null)}${File.separator}dataIngredient_${dataUser.id}.txt")
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(409600)
                val fileSize = body.contentLength()
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(futureStudioIconFile)
                while (true) {
                    val read: Int = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                    Log.d("FileDownload", "file download: $fileSizeDownloaded of $fileSize")
                }
                outputStream.flush()
                true
            } catch (e: IOException) {
                Log.d("FileDownload", "atas ${e.message}")
                false
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: IOException) {
            Log.d("FileDownload", "bawah ${e.message}")
            false
        }
    }

    override fun onItemClicked(data: RecipeRecommendation) {
        val intentt= Intent(this,RecipeDetailActivity::class.java)
        intentt.putExtra("idrecipe",data.recipeId.toString())
        startActivity(intentt)
    }

    override fun onFavItemClicked(data: FavouriteRecipeEntity) {
        val intentt= Intent(this, RecipeDetailActivity::class.java)
        intentt.putExtra("idrecipe",data.id_recipe)
        startActivity(intentt)
    }

//    private val usersObserver = Observer<UserEntity> { users ->
//        dataUser=users
//        binding.textUserLoginHome.text= dataUser?.name
//    }
}