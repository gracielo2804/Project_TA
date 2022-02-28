package com.gracielo.projectta.ui.homepage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.gracielo.projectta.data.source.local.entity.Ingredients
import com.gracielo.projectta.databinding.ActivityIngridientsListBinding
import com.gracielo.projectta.viewmodel.IngredientsViewModel
import com.gracielo.projectta.viewmodel.ViewModelFactory
import com.gracielo.projectta.vo.Status

class IngridientsList : AppCompatActivity(), IngridientsItemCallback {

    private lateinit var binding:ActivityIngridientsListBinding
    private lateinit var  viewModel: IngredientsViewModel
    private lateinit var  adapters: IngridientsListAdapters
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityIngridientsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel= obtainViewModel(this)

        val fabItemsSelected = binding.fabListIngredientsSelected
        val fabSearchRecipe = binding.fabSearchRecipe
        val recyclerView = binding.rvIngridientsList
        val fastScroller = binding.fastScroller
        adapters = IngridientsListAdapters(this@IngridientsList)
        fastScroller.setRecyclerView(recyclerView)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapters
        }

        viewModel.getIngredients().observe(this){ listIngredients ->
            if(listIngredients!=null){
                when(listIngredients.status){
                    Status.SUCCESS -> {
                        recyclerView.adapter?.let { adapters ->
                            when (adapters) {
                                is IngridientsListAdapters -> {
                                    adapters.submitList(listIngredients.data)
                                    adapters.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                    Status.ERROR -> {
                        Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewModel.getSelectedIngredients().observe(this){
            if (it.isEmpty()){
                fabSearchRecipe.visibility= View.INVISIBLE
                fabItemsSelected.visibility= View.INVISIBLE
            }
            else{
                fabSearchRecipe.visibility= View.VISIBLE
                fabItemsSelected.visibility= View.VISIBLE
                fabItemsSelected.text= "${it.size} Items Selected"
            }
        }

        fabSearchRecipe.setOnClickListener {

        }
        fabItemsSelected.setOnClickListener {
            val intent = Intent (this,SelectedIngredientsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun obtainViewModel(activity:AppCompatActivity): IngredientsViewModel {
        val factory: ViewModelFactory? = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory!!).get(IngredientsViewModel::class.java)
    }

    override fun onItemClicked(data: Ingredients) {
        data.isSelected= !data.isSelected
        Log.d("dataIngredients",data.toString())
        viewModel.updateIngredients(data)
    }
}