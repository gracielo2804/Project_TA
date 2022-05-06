package com.gracielo.projectta.ui.history.nutrientHistory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityNutrientHistoryBinding
import com.gracielo.projectta.util.FunHelper
import com.gracielo.projectta.viewmodel.UserViewModel

class NutrientHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNutrientHistoryBinding
    var apiServices = ApiServices()
    var helper = FunHelper
    lateinit  var userViewModel :UserViewModel
    val adapters = NutrientHistoryAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNutrientHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel = helper.obtainUserViewModel(this)
        userViewModel.getUser().observeOnce(this){
            if(it!=null){
                apiServices.getUserNutrientHistory(it.id){nutrientData->
                    if(nutrientData!=null){
                        adapters.setData(nutrientData.dataHistory)
                        binding.rvNutritentHistory.apply {
                            layoutManager = LinearLayoutManager(this@NutrientHistoryActivity)
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