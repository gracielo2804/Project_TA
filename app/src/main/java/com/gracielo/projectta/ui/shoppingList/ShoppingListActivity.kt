package com.gracielo.projectta.ui.shoppingList

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.gracielo.projectta.databinding.ActivityShoppingListBinding
import com.gracielo.projectta.R
import com.gracielo.projectta.ui.homepage.HomeActivity
import com.gracielo.projectta.ui.setting.SettingActivity
import com.gracielo.projectta.viewmodel.ShoppingListViewModel
import com.gracielo.projectta.viewmodel.UserViewModel
import com.gracielo.projectta.viewmodel.ViewModelFactory

class ShoppingListActivity : AppCompatActivity() {

    private lateinit var binding : ActivityShoppingListBinding
    lateinit var viewModel: UserViewModel
    lateinit var shoppingListViewModel: ShoppingListViewModel
    private var idUser = ""
    private var listRecipe = mutableListOf<String>()
    private var listIngredients = mutableMapOf<String,List<String>>()
    private var listIngredientsDetail = mutableMapOf<String,List<String>>()
    private val adapters = ShoppingListRecipeAdapter()
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
                    Log.d("ShoppingList",it[0].ingredients_list)
                    listRecipe.clear(); listIngredients.clear(); listRecipe.clear();
                    for(i in it.indices){
                        listRecipe.add(it[i].recipe_name)
                        var rawString = it[i].ingredients_list
                        val listIngredientsSplit = mutableListOf<String>()
                        val listIngredientsDetailSplit = mutableListOf<String>()
                        var ingredientsSplit = rawString.split(";-")
                        ingredientsSplit.forEach {
                            val ingredientsValue = it.split(" /? ")
                            listIngredientsSplit.add(ingredientsValue[0])
                            listIngredientsDetailSplit.add(ingredientsValue[1])
                        }
                        listIngredients.put(listRecipe[i],listIngredientsSplit)
                        listIngredientsDetail.put(listRecipe[i],listIngredientsDetailSplit)
                    }
                    adapters.setData(listRecipe,listIngredients,listIngredientsDetail)
                    rvShoppingList.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = adapters
                    }
                    rvShoppingList.adapter = adapters

                }
                else{

                }
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
                    R.id.mSetting -> {
                        val intent = Intent(this, SettingActivity::class.java)
                        finish()
                        startActivity(intent)

                    }
                    R.id.mPerson -> {

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

        val dropdown = binding.dropDownAutoTextSearchByShoppingList
        (dropdown as? AutoCompleteTextView)?.setAdapter(adapter)

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