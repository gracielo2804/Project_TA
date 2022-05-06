package com.gracielo.projectta.ui.history

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gracielo.projectta.R
import com.gracielo.projectta.databinding.ActivityHistoryHomeBinding
import com.gracielo.projectta.ui.history.nutrientHistory.NutrientHistoryActivity
import com.gracielo.projectta.ui.history.searchRecipeHistory.SearchRecipeHistoryActivity
import com.gracielo.projectta.ui.homepage.HomeActivity
import com.gracielo.projectta.ui.ingredients.IngridientsList
import com.gracielo.projectta.ui.login.TestLoginActivity
import com.gracielo.projectta.ui.setting.SettingActivity
import com.gracielo.projectta.ui.setting.SettingListAdapter
import com.gracielo.projectta.ui.shoppingList.ShoppingListActivity

class HistoryHomeActivity : AppCompatActivity() {

    private lateinit var binding : ActivityHistoryHomeBinding
    private lateinit var rvHistory: RecyclerView
    private var historyList = ArrayList<String>()
    val adapters = HistoryListAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvHistory = binding.rvHistoryList
        historyList.add("Recipe Search History")
        historyList.add("Daily Nutrient History")
        adapters.setData(historyList)

        rvHistory.apply {
            layoutManager= LinearLayoutManager(context)
            adapter = adapters
        }

        adapters.onItemClick = {
            when (it){
                "Daily Nutrient History" ->{
                    val intent = Intent(this, NutrientHistoryActivity::class.java)
                    startActivity(intent)
                }
                "Recipe Search History" ->{
                    val intent = Intent(this, SearchRecipeHistoryActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        val fabAdd = binding.fabAdd
        fabAdd.setOnClickListener {
            intent = Intent(this, IngridientsList::class.java)
            startActivity(intent)
        }

        val bottomMenu = binding.bottomnavigationbar
        bottomMenu.selectedItemId= R.id.mHistory
        bottomMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.mHome -> {

                    val intent = Intent(this, HomeActivity::class.java)
                    finish()
                    startActivity(intent)
                }
                R.id.mShopList -> {
                    val intent = Intent(this, ShoppingListActivity::class.java)
                    finish()
                    startActivity(intent)
                }
                R.id.mHistory -> {
                    //Do Nothing cz this is the activity currently active
//                    val intent = Intent(this, HistoryHomeActivity::class.java)
//                    finish()
//                    startActivity(intent)
                }
                R.id.mPerson -> {
                    //Do Nothing cz this is the activity currently active
                    val intent = Intent(this, SettingActivity::class.java)
                    finish()
                    startActivity(intent)
                }
            }
            true
        }
        supportActionBar?.hide()


    }
}