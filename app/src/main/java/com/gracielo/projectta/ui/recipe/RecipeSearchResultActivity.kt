package com.gracielo.projectta.ui.recipe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gracielo.projectta.data.model.recipe.detail.RecipeDetailResponseItem
import com.gracielo.projectta.data.model.recipe.search.RecipeResponseItem
import com.gracielo.projectta.data.model.recipe.searchWithNutrient.DataSearchRecipeResponse
import com.gracielo.projectta.data.source.local.entity.UserEntity
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityRecipeSearchResultBinding
import com.gracielo.projectta.util.FunHelper
import com.gracielo.projectta.util.FunHelper.observeOnce
import com.gracielo.projectta.viewmodel.IngredientsViewModel
import com.gracielo.projectta.viewmodel.UserViewModel

class RecipeSearchResultActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRecipeSearchResultBinding
    private lateinit var rv_recipe:RecyclerView
    private lateinit var viewModel: IngredientsViewModel
    private lateinit var userviewModel: UserViewModel
    var helper = FunHelper
    lateinit var dataUser : UserEntity
    private var paramIngredients:String = ""
    val apiServices= ApiServices()
    private var dataRecipe = ArrayList<RecipeDetailResponseItem>()
    private val adapters = RecipeSearchResultAdapter()
    var listIngredientsSearch = ""
    var listNutrientParam = ArrayList<Int>()
    var listIngredientsStringSplit = mutableListOf<String>()
    var maxCalories=0;var minCalories=0
    var maxSugar=0;var minSugar=0
    var maxProtein=0;var minProtein=0
    var maxCarbohydrate=0;var minCarbohydrate=0
    var maxFat=0;var minFat=0
    lateinit var pbar:ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecipeSearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pbar=binding.pbarSearchResult
        pbar.visibility=View.VISIBLE


        dataUser = UserEntity("1","1","1","1","0","",1,1,"1",1.1,1,1)

        userviewModel = helper.obtainUserViewModel(this)
        userviewModel.getUser().observeOnce(this){
            dataUser=it
        }
        rv_recipe = binding.rvSearchResult

        if(intent.hasExtra("paramIngredients")) paramIngredients =
            intent.getStringExtra("paramIngredients").toString()
        if(intent.hasExtra("listNutrient")){
            listNutrientParam = intent.getIntegerArrayListExtra("listNutrient")!!
            maxCalories = listNutrientParam[0]
            minCalories=listNutrientParam[1]
            maxCarbohydrate=listNutrientParam[2]
            minCarbohydrate=listNutrientParam[3]
            maxProtein=listNutrientParam[4]
            minProtein=listNutrientParam[5]
            maxSugar=listNutrientParam[6]
            minSugar=listNutrientParam[7]
            maxFat=listNutrientParam[8]
            minFat=listNutrientParam[9]
        }

        if(paramIngredients!=""){
            apiServices.searchRecipe(paramIngredients){
                if(it?.code==1){
                    dataRecipe= it.data as ArrayList<RecipeDetailResponseItem>
                    var dataRecipeSearchFilter = mutableListOf<RecipeDetailResponseItem>()
                    if(dataRecipe.size==0){
                        pbar.visibility=View.INVISIBLE
                        binding.rvSearchResult.visibility=View.INVISIBLE
                        binding.txtEmptySearchRecipeResult.visibility=View.VISIBLE
                    }
                    else{
                        binding.rvSearchResult.visibility=View.VISIBLE
                        binding.txtEmptySearchRecipeResult.visibility=View.INVISIBLE
                        dataRecipe.forEach {item->
                            val image = item.image.split("-")
                            val imageandext =image[1].split(".")
                            item.image="${image[0]}-636x393.${imageandext[1]}"
                        }
                        if(listNutrientParam.isNullOrEmpty()){

                        }
                        else{
                            dataRecipe.forEach {item->
                                var passCalories=false
                                var passFat=false
                                var passCarbohydrate=false
                                var passSugar=false
                                var passProtein=false
                                var StringLog = ""
                                for(i in item.nutrition!!.nutrients.indices){
                                    val nutrients = item?.nutrition!!.nutrients[i]
                                    if(nutrients.name=="Calories"){
                                        StringLog +="Calories : ${nutrients.amount} - max: $maxCalories - min : $minCalories"
                                        if(maxCalories>0 && minCalories>0){
                                            if(nutrients.amount<=maxCalories && nutrients.amount>=minCalories){
                                                passCalories=true
                                            }
                                        }
                                        else if(maxCalories==0 && minCalories==0){
                                            passCalories=true
                                        }
                                        else if(maxCalories==0 && minCalories>0){
                                            if(nutrients.amount>=minCalories)passCalories=true
                                        }
                                        else if(maxCalories>=0 && minCalories==0){
                                            if(nutrients.amount<=maxCalories)passCalories=true
                                        }
                                        StringLog +="$passCalories ; "
                                    }  
                                    else if(nutrients.name=="Fat"){
                                        StringLog +="Fat : ${nutrients.amount} - max: $maxFat - min : $minFat"
                                        if(maxFat>0 && minFat>0){
                                            if(nutrients.amount<=maxFat && nutrients.amount>=minFat){
                                                passFat=true
                                            }
                                        }
                                        else if(maxFat==0 && minFat==0){
                                            passFat=true
                                        }
                                        else if(maxFat==0 && minFat>0){
                                            if(nutrients.amount>=minFat)passFat=true
                                        }
                                        else if(maxFat>=0 && minFat==0){
                                            if(nutrients.amount<=maxFat)passFat=true
                                        }
                                        StringLog +="$passFat ; "
                                    }
                                    else if(nutrients.name=="Carbohydrates"){
                                        StringLog +="Carbohydrate : ${nutrients.amount} - max: $maxCarbohydrate - min : $minCarbohydrate "
                                        if(maxCarbohydrate>0 && minCarbohydrate>0){
                                            if(nutrients.amount<=maxCarbohydrate && nutrients.amount>=minCarbohydrate){
                                                passCarbohydrate=true
                                            }
                                        }
                                        else if(maxCarbohydrate==0 && minCarbohydrate==0){
                                            passCarbohydrate=true
                                        }
                                        else if(maxCarbohydrate==0 && minCarbohydrate>0){
                                            if(nutrients.amount>=minCarbohydrate)passCarbohydrate=true
                                        }
                                        else if(maxCarbohydrate>=0 && minCarbohydrate==0){
                                            if(nutrients.amount<=maxCarbohydrate)passCarbohydrate=true
                                        }
                                        StringLog +="$passCarbohydrate ; "
                                    }

                                    else if(nutrients.name=="Sugar"){
                                        StringLog +="Sugar : ${nutrients.amount} - max: $maxSugar - min : $minSugar "
                                        if(maxSugar>0 && minSugar>0){
                                            if(nutrients.amount<=maxSugar && nutrients.amount>=minSugar){
                                                passSugar=true
                                            }
                                        }
                                        else if(maxSugar==0 && minSugar==0){
                                            passSugar=true
                                        }
                                        else if(maxSugar==0 && minSugar>0){
                                            if(nutrients.amount>=minSugar)passSugar=true
                                        }
                                        else if(maxSugar>=0 && minSugar==0){
                                            if(nutrients.amount<=maxSugar)passSugar=true
                                        }
                                        StringLog +="$passSugar ; "
                                    }
                                    else if(nutrients.name=="Protein"){
                                        StringLog +="Protein : ${nutrients.amount} - max: $maxProtein - min : $minProtein "
                                        if(maxProtein>0 && minProtein>0){
                                            if(nutrients.amount<=maxProtein && nutrients.amount>=minProtein){
                                                passProtein=true
                                            }
                                        }
                                        else if(maxProtein==0 && minProtein==0){
                                            passProtein=true
                                        }
                                        else if(maxProtein==0 && minProtein>0){
                                            if(nutrients.amount>=minProtein)passProtein=true
                                        }
                                        else if(maxProtein>=0 && minProtein==0){
                                            if(nutrients.amount<=maxProtein)passProtein=true
                                        }
                                        StringLog +="$passProtein ; "
                                    }
                                }

                                StringLog = "$StringLog ${item.id}"
                                Log.d("RecipeNutrients",StringLog)

                                if(passCalories&&passCarbohydrate&&passFat&&passProtein&&passSugar){
                                    dataRecipeSearchFilter.add(item)
                                }
                            }
                            if(dataRecipeSearchFilter.size==0){
                                binding.rvSearchResult.visibility=View.INVISIBLE
                                binding.txtEmptySearchRecipeResult.visibility=View.VISIBLE
                            }
                            dataRecipe.clear()
                            dataRecipe.addAll(dataRecipeSearchFilter)
                        }
                        pbar.visibility=View.INVISIBLE
                        adapters.setData(dataRecipe)
                    }
                }
            }


            listIngredientsStringSplit=paramIngredients.split(",+").toMutableList()
            for (i in listIngredientsStringSplit.indices){
                if(i < listIngredientsStringSplit.size-1){
                    listIngredientsSearch+="${listIngredientsStringSplit[i]}, "
                }
                else listIngredientsSearch+= listIngredientsStringSplit[i]
            }
        }

        adapters.onItemClick = {
            apiServices.insertUserSearchRecipe(dataUser.id,it.id.toString(),listIngredientsSearch,it.title){}
            apiServices.getEquipmentFromRecipe(it.id.toString()){response->
                for (i in response?.data?.equipment?.indices!!){
                    apiServices.insertEquipment(response.data.equipment[i].name,response.data.equipment[i].image){}
                }
            }
            val intent = Intent(this,RecipeDetailActivity::class.java)
//            intent.putExtra("recipe",it)
            intent.putExtra("idrecipe",it.id.toString())
            startActivity(intent)
        }

        rv_recipe.apply {
            layoutManager = GridLayoutManager(this@RecipeSearchResultActivity,2)
            adapter = adapters
        }
    }
}