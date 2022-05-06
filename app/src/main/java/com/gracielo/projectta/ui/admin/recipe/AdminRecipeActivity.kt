package com.gracielo.projectta.ui.admin.recipe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.gracielo.projectta.R
import com.gracielo.projectta.data.model.countIngredients.DataCountIngredients
import com.gracielo.projectta.data.model.recipeCount.DataCountRecipe
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityAdminRecipeBinding
import com.gracielo.projectta.ui.admin.AdminHomeActivity
import com.gracielo.projectta.ui.admin.AdminUserReportActivity
import com.gracielo.projectta.ui.admin.ingredients.AdminIngredientActivity
import com.gracielo.projectta.ui.admin.ingredients.IngredientsListCountAdapter
import com.gracielo.projectta.util.FunHelper
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter

class AdminRecipeActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAdminRecipeBinding
    var listRecipeCount = mutableListOf<DataCountRecipe>()
    var apiServices = ApiServices()
    val TABLE_HEADERS = arrayOf("Recipe Name","User Search Count")
    lateinit var adapters: RecipeListCountAdapter
    var helper = FunHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        apiServices.getRecipeCount {
            if (it?.code==1){
                listRecipeCount.addAll(it.dataCountRecipe)
                adapters = RecipeListCountAdapter(this,listRecipeCount as ArrayList<DataCountRecipe>)
                adapters.setDatas(listRecipeCount)
                val tableView = binding.tableView
                tableView.headerAdapter= SimpleTableHeaderAdapter(this,*TABLE_HEADERS)
                tableView.setDataAdapter(adapters)
            }
        }

        val bottomNavBar = binding.adminBottomMenu
        bottomNavBar.selectedItemId = R.id.adminRecipe
        bottomNavBar.setOnItemSelectedListener {
            when(it.itemId){
                R.id.adminSubscription->{
                    val intent = Intent(this, AdminHomeActivity::class.java)
                    finish()
                    startActivity(intent)

                }
                R.id.adminUser->{
                    val intent = Intent(this, AdminUserReportActivity::class.java)
                    finish()
                    startActivity(intent)
                }
                R.id.adminIngredients->{
                    val intent = Intent(this, AdminIngredientActivity::class.java)
                    finish()
                    startActivity(intent)

                }
                R.id.adminRecipe->{
//                    intent = Intent(this, AdminRecipeActivity::class.java)
//                    finish()
//                    startActivity(intent)
                }
            }
            true
        }
    }
}