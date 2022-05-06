package com.gracielo.projectta.ui.history.searchRecipeHistory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivitySearchRecipeHistoryBinding
import com.gracielo.projectta.util.FunHelper
import com.gracielo.projectta.viewmodel.UserViewModel

class SearchRecipeHistoryActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySearchRecipeHistoryBinding
    var apiServices = ApiServices()
    var helper = FunHelper
    lateinit  var userViewModel : UserViewModel
    val adapters = SearchRecipeHistoryGridAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchRecipeHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel = helper.obtainUserViewModel(this)
        userViewModel.getUser().observeOnce(this){
            if(it!=null){
                apiServices.getUserSearchRecipeHistory(it.id){historyData->
                    if(historyData!=null){
                        adapters.setData(historyData.searchRecipeDataHistory)
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
}