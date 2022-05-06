package com.gracielo.projectta.ui.ingredients

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.gracielo.projectta.R
import com.gracielo.projectta.data.source.local.entity.Ingredients
import com.gracielo.projectta.data.source.local.entity.IngredientsSearch
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityIngridientsListBinding
import com.gracielo.projectta.ui.recipe.RecipeSearchFilterNutrientActivity
import com.gracielo.projectta.ui.recipe.RecipeSearchResultActivity
import com.gracielo.projectta.util.FunHelper
import com.gracielo.projectta.util.FunHelper.observeOnce
import com.gracielo.projectta.viewmodel.IngredientsViewModel
import com.gracielo.projectta.viewmodel.ViewModelFactory
import com.gracielo.projectta.vo.Resource
import com.gracielo.projectta.vo.Status
import kotlin.math.max


class IngridientsList : AppCompatActivity(), IngridientsItemCallback ,IngridientsListSearchsItemCallback{

    private lateinit var binding:ActivityIngridientsListBinding
    private lateinit var  viewModel: IngredientsViewModel
    var helper = FunHelper
    private lateinit var  adapters: IngridientsListAdapters
    private lateinit var  adaptersearch: IngridientsListSearchsAdapters
    val apiServices= ApiServices()
    lateinit var fabItemsSelected:ExtendedFloatingActionButton
    lateinit var fabSearchRecipe :ExtendedFloatingActionButton
    lateinit var recyclerView:RecyclerView
    var stringIngredients=""
    var stringIngredientsV2=""

    var maxCalories=0;var minCalories=0
    var maxSugar=0;var minSugar=0
    var maxProtein=0;var minProtein=0
    var maxCarbohydrate=0;var minCarbohydrate=0
    var maxFat=0;var minFat=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityIngridientsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel= obtainViewModel(this)

        fabItemsSelected = binding.fabListIngredientsSelected
        fabSearchRecipe = binding.fabSearchRecipe
        recyclerView = binding.rvIngridientsList
//        val fastScroller = binding.fastScroller
        adapters = IngridientsListAdapters(this@IngridientsList)
        adaptersearch = IngridientsListSearchsAdapters(this@IngridientsList)
//        fastScroller.setRecyclerView(recyclerView)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapters
        }
        var resultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result->
            if(result.resultCode== RESULT_OK){
                val intentData= result.data
                if (intentData != null) {
                    maxCalories = intentData.getIntExtra("maxCalories",0)
                    minCalories = intentData.getIntExtra("minCalories",0)
                    
                    maxFat = intentData.getIntExtra("maxFat",0)
                    minFat = intentData.getIntExtra("minFat",0)
                    
                    maxCarbohydrate = intentData.getIntExtra("maxCarbohydrate",0)
                    minCarbohydrate = intentData.getIntExtra("minCarbohydrate",0)
                    
                    maxProtein = intentData.getIntExtra("maxProtein",0)
                    minProtein = intentData.getIntExtra("minProtein",0)
                    
                    maxSugar = intentData.getIntExtra("maxSugar",0)
                    minSugar = intentData.getIntExtra("minSugar",0)

                    if(maxSugar==0 && minSugar==0){
                        binding.txtSugarShowFilter.text="No Filter"
                    }
                    else if(maxSugar==0 && minSugar>0){
                        binding.txtSugarShowFilter.text="More Than ${minSugar}"
                    }
                    else if(minSugar==0 && maxSugar>0){
                        binding.txtSugarShowFilter.text="Less Than ${maxSugar}"
                    }
                    else if(minSugar>0 && maxSugar>0){
                        binding.txtSugarShowFilter.text="${minSugar} - ${maxSugar}"
                    }

                    if(maxCalories==0 && minCalories==0){
                        binding.txtCaloriesShowFilter.text="No Filter"
                    }
                    else if(maxCalories==0 && minCalories>0){
                        binding.txtCaloriesShowFilter.text="More Than ${minCalories}"
                    }
                    else if(minCalories==0 && maxCalories>0){
                        binding.txtCaloriesShowFilter.text="Less Than ${maxCalories}"
                    }
                    else if(minCalories>0 && maxCalories>0){
                        binding.txtCaloriesShowFilter.text="${minCalories} - ${maxCalories}"
                    }
                    
                    if(maxFat==0 && minFat==0){
                        binding.txtFatShowFilter.text="No Filter"
                    }
                    else if(maxFat==0 && minFat>0){
                        binding.txtFatShowFilter.text="More Than ${minFat}"
                    }
                    else if(minFat==0 && maxFat>0){
                        binding.txtFatShowFilter.text="Less Than ${maxFat}"
                    }
                    else if(minFat>0 && maxFat>0){
                        binding.txtFatShowFilter.text="${minFat} - ${maxFat}"
                    }
                    
                    if(maxCarbohydrate==0 && minCarbohydrate==0){
                        binding.txtCarbohydrateShowFilter.text="No Filter"
                    }
                    else if(maxCarbohydrate==0 && minCarbohydrate>0){
                        binding.txtCarbohydrateShowFilter.text="More Than ${minCarbohydrate}"
                    }
                    else if(minCarbohydrate==0 && maxCarbohydrate>0){
                        binding.txtCarbohydrateShowFilter.text="Less Than ${maxCarbohydrate}"
                    }
                    else if(minCarbohydrate>0 && maxCarbohydrate>0){
                        binding.txtCarbohydrateShowFilter.text="${minCarbohydrate} - ${maxCarbohydrate}"
                    }
                    
                    if(maxProtein==0 && minProtein==0){
                        binding.txtProteinShowFilter.text="No Filter"
                    }
                    else if(maxProtein==0 && minProtein>0){
                        binding.txtProteinShowFilter.text="More Than ${minProtein}"
                    }
                    else if(minProtein==0 && maxProtein>0){
                        binding.txtProteinShowFilter.text="Less Than ${maxProtein}"
                    }
                    else if(minProtein>0 && maxProtein>0){
                        binding.txtProteinShowFilter.text="${minProtein} - ${maxProtein}"
                    }

                }
            }
        }
        binding.btnFilterNutrient.setOnClickListener {
            val intent = Intent(this,RecipeSearchFilterNutrientActivity::class.java)
            intent.putExtra("minCalories",minCalories)
            intent.putExtra("maxCalories",maxCalories)

            intent.putExtra("minSugar",minSugar)
            intent.putExtra("maxSugar",maxSugar)

            intent.putExtra("minProtein",minProtein)
            intent.putExtra("maxProtein",maxProtein)

            intent.putExtra("minCarbohydrate",minCarbohydrate)
            intent.putExtra("maxCarbohydrate",maxCarbohydrate)

            intent.putExtra("minFat",minFat)
            intent.putExtra("maxFat",maxFat)
            resultLauncher.launch(intent)
            overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
        }

        viewModel.getIngredients().observe(this,object :Observer<Resource<List<Ingredients>>>{
            override fun onChanged(listIngredients: Resource<List<Ingredients>>?) {
                if(listIngredients!=null){
                    when(listIngredients.status){
                        Status.SUCCESS -> {
                            recyclerView.adapter?.let { adapters ->
                                when (adapters) {
                                    is IngridientsListAdapters -> {
                                        adapters.setData(listIngredients.data)
                                        adapters.notifyDataSetChanged()
                                    }
                                }
                            }
                        }
                        Status.ERROR -> {
                            Toast.makeText(this@IngridientsList, "Check your internet connection", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                viewModel.getIngredients().removeObserver(this)
            }
        })
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
                stringIngredientsV2=""
                it?.forEach {ingredients->
                    stringIngredients += if(ctr==it.size){
                        ingredients.name
                    } else{
                        "${ingredients.name},+"
                    }
                    stringIngredientsV2 += if(ctr==it.size){
                        ingredients.name
                    } else{
                        "${ingredients.name},"
                    }
                    ctr++
                }
//                stringIngredientsV2 = stringIngredientsV2.replace(" ","%20")

//                stringIngredients = stringIngredientsV2.replace(" ","%20")
            }
        }

//        viewModel.getIngredients().observeOnce(this){ listIngredients ->
//            if(listIngredients!=null){
//                when(listIngredients.status){
//                    Status.SUCCESS -> {
//                        recyclerView.adapter?.let { adapters ->
//                            when (adapters) {
//                                is IngridientsListAdapters -> {
//                                    adapters.submitList(listIngredients.data)
//                                    adapters.notifyDataSetChanged()
//                                }
//                            }
//                        }
//                    }
//                    Status.ERROR -> {
//                        Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        }


        binding.imageBackIngredientList.setOnClickListener {
            finish()
        }
        binding.btnSearchIngredients.setOnClickListener {
            recyclerView.adapter=adaptersearch
            viewModel.getSearchIngredients(binding.etIngredientsSearch.text.toString()).observe(this
            ) { listIngredients ->
                viewModel.getSearchIngredients(binding.etIngredientsSearch.text.toString())
                    .removeObservers(this@IngridientsList)
                if (listIngredients != null) {
                    when (listIngredients.status) {
                        Status.SUCCESS -> {
                            recyclerView.adapter?.let { adapters ->
                                when (adapters) {
                                    is IngridientsListSearchsAdapters -> {
                                        adapters.setData(listIngredients.data)
                                    }
                                }
                            }
                        }
                        Status.ERROR -> {
                            Toast.makeText(
                                this@IngridientsList,
                                "Error Search Ingredients",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
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
                if(minCarbohydrate!=0||maxCarbohydrate!=0||minCalories!=0||maxCalories!=0||minSugar!=0||maxSugar!=0
                    ||minProtein!=0||maxProtein!=0||minFat!=0||maxFat!=0){
                    intent.putExtra("paramIngredients", stringIngredients)
                    val listParamNutritent = ArrayList<Int>()
                    listParamNutritent.add(maxCalories)
                    listParamNutritent.add(minCalories)
                    listParamNutritent.add(maxCarbohydrate)
                    listParamNutritent.add(minCarbohydrate)
                    listParamNutritent.add(maxProtein)
                    listParamNutritent.add(minProtein)
                    listParamNutritent.add(maxSugar)
                    listParamNutritent.add(minSugar)
                    listParamNutritent.add(maxFat)
                    listParamNutritent.add(minFat)
                    intent.putIntegerArrayListExtra("listNutrient",listParamNutritent)
                }
                else{
                    intent.putExtra("paramIngredients", stringIngredients)
                }
                startActivity(intent)

            }


        }
        fabItemsSelected.setOnClickListener {
            val intent = Intent (this, SelectedIngredientsActivity::class.java)
            startActivity(intent)
        }
        binding.etIngredientsSearch.setOnKeyListener { view, i, keyEvent ->
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                (i == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press
                binding.btnSearchIngredients.performClick()
                return@setOnKeyListener true
            }
            else{
                return@setOnKeyListener  false
            }
        }
    }

    private fun obtainViewModel(activity:AppCompatActivity): IngredientsViewModel {
        val factory: ViewModelFactory? = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory!!).get(IngredientsViewModel::class.java)
    }

    override fun onItemClicked(data: Ingredients) {
        data.isSelected= !data.isSelected
        viewModel.updateIngredients(data)
    }
    override fun onItemSearchClicked(data: Ingredients) {
        data.isSelected= !data.isSelected
        viewModel.updateIngredients(data)
    }

    fun transformIngredients(data:IngredientsSearch):Ingredients{
        return Ingredients(data.id,data.name,data.image,data.isSelected)
    }



}