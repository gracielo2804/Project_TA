package com.gracielo.projectta.ui.shoppingList

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.gracielo.projectta.databinding.ActivityShoppingListBinding
import com.gracielo.projectta.R
import com.gracielo.projectta.data.source.local.entity.ShoppingListEntity
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.ui.history.HistoryHomeActivity
import com.gracielo.projectta.ui.homepage.HomeActivity
import com.gracielo.projectta.ui.ingredients.IngridientsList
import com.gracielo.projectta.ui.setting.SettingActivity
import com.gracielo.projectta.viewmodel.ShoppingListViewModel
import com.gracielo.projectta.viewmodel.UserViewModel
import com.gracielo.projectta.viewmodel.ViewModelFactory

class ShoppingListActivity : AppCompatActivity() {

    private lateinit var binding : ActivityShoppingListBinding
    lateinit var viewModel: UserViewModel
    lateinit var shoppingListViewModel: ShoppingListViewModel
    private var idUser = ""
    private var listRecipe = mutableListOf<ShoppingListEntity>()
    private var listIngredients = mutableMapOf<String,List<String>>()
    private var listIngredientsDetail = mutableMapOf<String,List<String>>()
    private val adapters = ShoppingListRecipeAdapter()
    private var apiServices = ApiServices()
    private var listRecipeName = ArrayList<String>()
//    private var shoppingList = mutableMapOf<String,List<String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val rvShoppingList = binding.rvShoppingList

        viewModel = obtainUserViewModel(this)
        shoppingListViewModel = obtainShoppingListViewModel(this)
        viewModel.getUser()?.observe(this){
            idUser = it.id
        }
        withDelay(500){
            shoppingListViewModel.getShoppingList(idUser).observe(this){
                if(!it.isNullOrEmpty()){
                    rvShoppingList.visibility= View.VISIBLE
                    binding.txtShoppingEmpty.visibility=View.INVISIBLE
                    Log.d("ShoppingList",it[0].ingredients_list)
                    listRecipe.clear(); listIngredients.clear()
                    for(i in it.indices){
                        listRecipe.add(it[i])
                        var rawString = it[i].ingredients_list
                        val listIngredientsSplit = mutableListOf<String>()
                        val listIngredientsDetailSplit = mutableListOf<String>()
                        var ingredientsSplit = rawString.split(";-")
                        ingredientsSplit.forEach {
                            val ingredientsValue = it.split(" /? ")
                            listIngredientsSplit.add(ingredientsValue[0])
                            listIngredientsDetailSplit.add(ingredientsValue[1])
                        }
                        apiServices.getRecipeName(it[i].id_recipe){response->
                            listRecipeName.add(response?.message!!)
                            listIngredients.put(response?.message!!,listIngredientsSplit)
                            listIngredientsDetail.put(response?.message!!,listIngredientsDetailSplit)
                        }
                    }
                    Log.d("DataShoppingList","${listRecipeName.size}")
                    Handler(Looper.getMainLooper()).postDelayed({
                        adapters.setData(listRecipe,listRecipeName,listIngredients,listIngredientsDetail)
                        rvShoppingList.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = adapters
                        }
                        rvShoppingList.adapter = adapters
                    },500)

                }
                else{
                    rvShoppingList.visibility= View.INVISIBLE
                    binding.txtShoppingEmpty.visibility=View.VISIBLE
                }
            }

            val fabAdd = binding.fabAdd
            fabAdd.setOnClickListener {
                intent = Intent(this, IngridientsList::class.java)
                startActivity(intent)
            }
            val bottomMenu = binding.bottomnavigationbar
            bottomMenu.selectedItemId= R.id.mShopList
            bottomMenu.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.mHome -> {
                        val intent = Intent(this, HomeActivity::class.java)
                        finish()
                        startActivity(intent)
                    }
                    R.id.mShopList -> {
                        //Do Nothing cz this is the activity currently active
//                        val intent = Intent(this, ShoppingListActivity::class.java)
//                        finish()
//                        startActivity(intent)
                    }
                    R.id.mHistory -> {
                        val intent = Intent(this, HistoryHomeActivity::class.java)
                        finish()
                        startActivity(intent)

                    }
                    R.id.mPerson -> {
                        val intent = Intent(this, SettingActivity::class.java)
                        finish()
                        startActivity(intent)
                    }
                }
                true
            }
//            binding.imageView6.setOnClickListener {
//                finish()
//            }
        }

        val items = listOf("Recipe", "Ingredients")
        val adapter = ArrayAdapter(this, R.layout.list_item_dropdown_search_shopping_list, items)

//        val dropdown = binding.dropDownAutoTextSearchByShoppingList
//        (dropdown as? AutoCompleteTextView)?.setAdapter(adapter)
        supportActionBar?.hide()
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
}