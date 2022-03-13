package com.gracielo.projectta.ui.shoppingList

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gracielo.projectta.R
import com.gracielo.projectta.databinding.ItemShoppingListBinding
import com.gracielo.projectta.databinding.ItemShoppingListIngredientsBinding

class ShoppingListRecipeAdapter: RecyclerView.Adapter<ShoppingListRecipeAdapter.ViewHolder>() {

    private var listRecipe = ArrayList<String>()
    private var listIngredients =  mutableMapOf<String,List<String>>()
    private var listIngredientsDetail =  mutableMapOf<String,List<String>>()
    var onItemClick: ((String) -> Unit)? = null

    fun setData(newListRecipe : List<String>, newlistIngredients : Map<String,List<String>>?, newlistIngredientsDetail : Map<String,List<String>>?){
        if(newlistIngredients == null || newlistIngredientsDetail == null) return
        listRecipe.clear()
        listIngredients.clear()
        listIngredientsDetail.clear()
        listRecipe.addAll(newListRecipe)
        listIngredients.putAll(newlistIngredients)
        listIngredientsDetail.putAll(newlistIngredientsDetail)
        notifyDataSetChanged()
        Log.d("ShoppingList2",listRecipe.toString())
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemShoppingListBinding.bind(view)
        private val recipeName = binding.txtRecipeNameShoppingList
        val rvIngredients = binding.rvIngredientsShoppingList
        val ingredientsAdapter = ShoppingListIngredientsAdapter()

        fun bindRecipe(recipeNameParam: String) {
            recipeName.text= recipeNameParam
        }
        init{
            binding.root.setOnClickListener{onItemClick?.invoke(listRecipe[bindingAdapterPosition])}
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder=
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_shopping_list, parent, false)
        )

    override fun getItemCount(): Int = listRecipe.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipeName = listRecipe[position]
        val listIngredients = listIngredients[recipeName]
        val listIngredientsDetail = listIngredientsDetail[recipeName]
        holder.ingredientsAdapter.setData(listIngredients,listIngredientsDetail)
        holder.rvIngredients.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = holder.ingredientsAdapter
        }
        holder.bindRecipe(recipeName)
    }

}