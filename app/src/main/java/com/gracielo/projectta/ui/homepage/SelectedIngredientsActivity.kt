package com.gracielo.projectta.ui.homepage

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.paging.PositionalDataSource
import androidx.recyclerview.widget.LinearLayoutManager
import com.gracielo.projectta.data.source.local.entity.Ingredients
import com.gracielo.projectta.databinding.ActivitySelectedIngredientsBinding
import com.gracielo.projectta.viewmodel.IngredientsViewModel
import com.gracielo.projectta.viewmodel.ViewModelFactory
import com.gracielo.projectta.vo.Status
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class SelectedIngredientsActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySelectedIngredientsBinding
    private lateinit var adapters : IngridientsListSelectedAdapters
    private lateinit var  viewModel : IngredientsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySelectedIngredientsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = obtainViewModel(this)
        val fabItemsSelected = binding.fabNumberSelectedIngredients
        val fabBackToList = binding.fabBackToIngredientsList
        val recyclerView = binding.rvIngredientsListSelected
        val fastScroller = binding.fastScrollerSelected

        adapters = IngridientsListSelectedAdapters()
        adapters.onItemClick = {
            it.isSelected = !it.isSelected
            viewModel.updateIngredients(it)
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapters
        }
        fastScroller.setRecyclerView(recyclerView)

        viewModel.getSelectedIngredients().observe(this){ listIngredients ->
            if(listIngredients!=null){
                fabItemsSelected.text= "${listIngredients.size} Items Selected"
                recyclerView.adapter?.let { adapters ->
                    when (adapters) {
                        is IngridientsListSelectedAdapters -> {
                            adapters.setData(listIngredients)
                            adapters.notifyDataSetChanged()
                        }
                    }
                }
            }
            else  fabItemsSelected.text= "No Items Selected"
        }

        fabBackToList.setOnClickListener {
            finish()
        }

    }

    private fun obtainViewModel(activity:AppCompatActivity): IngredientsViewModel {
        val factory: ViewModelFactory? = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory!!).get(IngredientsViewModel::class.java)
    }


}