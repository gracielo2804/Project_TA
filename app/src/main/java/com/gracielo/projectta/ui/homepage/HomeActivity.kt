package com.gracielo.projectta.ui.homepage

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gracielo.projectta.R
import com.gracielo.projectta.data.model.DataUserProfile
import com.gracielo.projectta.databinding.ActivityHomeBinding
import com.gracielo.projectta.viewmodel.UserViewModel
import com.gracielo.projectta.viewmodel.ViewModelFactory
import android.view.View
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.gracielo.projectta.data.model.recipe.RecipeRecommendation
import com.gracielo.projectta.data.model.recipeCount.DataCountRecipe
import com.gracielo.projectta.data.source.local.entity.*
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.notification.DailyReminder
import com.gracielo.projectta.ui.history.HistoryHomeActivity
import com.gracielo.projectta.ui.ingredients.IngridientsList
import com.gracielo.projectta.ui.setting.SettingActivity
import com.gracielo.projectta.ui.shoppingList.ShoppingListActivity
import com.gracielo.projectta.util.FunHelper
import com.gracielo.projectta.viewmodel.FavRecipeViewModel
import com.gracielo.projectta.viewmodel.ShoppingListViewModel
import com.gracielo.projectta.vo.Status
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.delay
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class HomeActivity : AppCompatActivity(), RecommendedItemCallback {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Test String", R.string.MIDTRANS_CLIENT_KEY.toString())

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
                            var adapters = FavouriteRecipeAdapter()
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
                                        var recipename=""
                                        apiServices.getRecipeDetail(data.idRecipe){recipe->
                                            if (recipe != null) {
                                                recipename= recipe.data.title
                                            }
                                        }
                                        var shoppingList = ShoppingListEntity(data.idShoppingList,data.idUsers,data.idRecipe,recipename,data.ingredientsList)
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

    }

    private fun obtainViewModel(activity: AppCompatActivity): UserViewModel {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(UserViewModel::class.java)
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

    override fun onItemClicked(data: RecipeRecommendation) {
//        TODO("Not yet implemented")
    }

//    private val usersObserver = Observer<UserEntity> { users ->
//        dataUser=users
//        binding.textUserLoginHome.text= dataUser?.name
//    }
}