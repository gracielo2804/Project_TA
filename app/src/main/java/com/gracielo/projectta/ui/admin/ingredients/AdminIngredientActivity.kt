package com.gracielo.projectta.ui.admin.ingredients

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.gracielo.projectta.R
import com.gracielo.projectta.data.model.countIngredients.DataCountIngredients
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityAdminIngredientBinding
import com.gracielo.projectta.ui.admin.AdminHomeActivity
import com.gracielo.projectta.ui.admin.AdminUserReportActivity
import com.gracielo.projectta.ui.admin.recipe.AdminRecipeActivity
import com.gracielo.projectta.util.FunHelper
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter

class AdminIngredientActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminIngredientBinding
    var listIngredientCountTemp = mutableListOf<DataCountIngredients>()
    var listIngredientCount = mutableListOf<DataCountIngredients>()
    var apiServices = ApiServices()
    var helper = FunHelper
    val TABLE_HEADERS = arrayOf("Ingredients","Total Used")
    lateinit var adapters: IngredientsListCountAdapter
    var dataCountIngredientsString = mutableListOf(arrayOf<String>())
    private val DATA_TO_SHOW =
        arrayOf(arrayOf("This", "is", "a", "test"), arrayOf("and", "a", "second", "test"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminIngredientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiServices.getIngredientsCount {
            if (it?.code==1){
                listIngredientCountTemp.addAll(it.dataCountIngredients)
                for(i in listIngredientCountTemp.indices){
                    var highestCount = listIngredientCountTemp[i]
                    for(j in i..listIngredientCountTemp.size-1){
                        if(listIngredientCountTemp[j].count>highestCount.count){
                            var temp = listIngredientCountTemp[j]
                            listIngredientCountTemp[j]=highestCount
                            highestCount=temp
                        }
                    }
                    listIngredientCount.add(highestCount)
                }
                for(i in listIngredientCount.indices){
//                    Log.d("CountRecipe " , " $i -- ${listIngredientCount[i].toString()}")
                }
                for(i in listIngredientCount.indices){
                    val data = arrayOf(listIngredientCount[i].name,listIngredientCount[i].count.toString())
                    dataCountIngredientsString.add(data)
                }
                adapters = IngredientsListCountAdapter(this,
                    listIngredientCount as ArrayList<DataCountIngredients>
                )
                adapters.setDatas(listIngredientCount)
                Log.d("CountRecipe " , "Size = ${adapters.count}")
                val tableView = binding.tableView
                tableView.headerAdapter= SimpleTableHeaderAdapter(this,*TABLE_HEADERS)
                tableView.setDataAdapter(adapters)
//                tableView.setColumnComparator(0,IngredientsComparator())
//                tableView.setColumnComparator(1,IngredientsComparatorCount())
            }
        }





        val bottomNavBar = binding.adminBottomMenu
        bottomNavBar.selectedItemId = R.id.adminIngredients
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
//                    val intent = Intent(this, AdminIngredientActivity::class.java)
//                    finish()
//                    startActivity(intent)

                }
                R.id.adminRecipe->{
                    intent = Intent(this, AdminRecipeActivity::class.java)
                    finish()
                    startActivity(intent)
                }
            }
            true
        }


    }
    private class IngredientsComparator : Comparator<DataCountIngredients> {
        override fun compare(p0: DataCountIngredients, p1: DataCountIngredients): Int {
            return p0.name.compareTo(p1.name)
        }
    }
    private class IngredientsComparatorCount : Comparator<DataCountIngredients> {
        override fun compare(p0: DataCountIngredients, p1: DataCountIngredients): Int {
            return p0.count.compareTo(p1.count)
        }
    }
}