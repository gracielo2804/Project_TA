package com.gracielo.projectta.ui.history.searchRecipeHistory

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.gracielo.projectta.data.model.searchRecipeHistory.SearchRecipeDataHistory
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivitySearchRecipeHistoryBinding
import com.gracielo.projectta.ui.recipe.RecipeDetailActivity
import com.gracielo.projectta.util.FunHelper
import com.gracielo.projectta.viewmodel.UserViewModel

class SearchRecipeHistoryActivity : AppCompatActivity(),SearchRecipeHistoryItemCallback {

    private lateinit var binding : ActivitySearchRecipeHistoryBinding
    var apiServices = ApiServices()
    var helper = FunHelper
    lateinit  var userViewModel : UserViewModel
    lateinit var adapters:SearchRecipeHistoryGridAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchRecipeHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapters = SearchRecipeHistoryGridAdapter(this@SearchRecipeHistoryActivity)
        userViewModel = helper.obtainUserViewModel(this)
        userViewModel.getUser().observeOnce(this){
            if(it!=null){
                apiServices.getUserSearchRecipeHistory(it.id){historyData->
                    if(historyData!=null){
                        val listTemp = historyData.searchRecipeDataHistory
                        val list= mutableListOf<SearchRecipeDataHistory>()
                        for (i in listTemp.size-1 downTo 0){
                            list.add(listTemp[i])
                        }
                        Log.d("list hist",list.toString())
                        adapters.setData(list)
                        binding.rvNutritentHistory.apply {
                            layoutManager = GridLayoutManager(this@SearchRecipeHistoryActivity,2)
                            adapter = adapters
                        }
                    }
                }
            }
        }

        supportActionBar?.hide()
    }


    fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }

    override fun onItemClicked(data: SearchRecipeDataHistory) {
//        TODO("Not yet implemented")
        val intentt= Intent(this, RecipeDetailActivity::class.java)
        intentt.putExtra("idrecipe",data.idRecipe.toString())
        startActivity(intentt)
    }
}