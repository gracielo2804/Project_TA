package com.gracielo.projectta.ui.shoppingList

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gracielo.projectta.R
import com.gracielo.projectta.databinding.ItemShoppingListIngredientsBinding

class ShoppingListIngredientsAdapter: RecyclerView.Adapter<ShoppingListIngredientsAdapter.ViewHolder>() {

    private var listIngredients =  ArrayList<String>()
    private var listIngredientsDetail =  ArrayList<String>()
    var onItemClick: ((String) -> Unit)? = null

    fun setData(newlistIngredients : List<String>?, newlistIngredientsDetail : List<String>?){
        if(newlistIngredients == null || newlistIngredientsDetail == null) return
        listIngredients.clear()
        listIngredientsDetail.clear()
        listIngredients.addAll(newlistIngredients)
        listIngredientsDetail.addAll(newlistIngredientsDetail)
        notifyDataSetChanged()

    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemShoppingListIngredientsBinding.bind(view)
        private val IngredientsName = binding.txtIngredientsNameShoppingList
        private val IngredientsDetail = binding.txtIngredientsDetailsShoppingList

        fun bindRecipe(ingredientsName: String,ingredientsDetail: String) {
            IngredientsName.text= ingredientsName
            IngredientsDetail.text = ingredientsDetail
        }
        init{
            binding.root.setOnClickListener{onItemClick?.invoke(listIngredients[bindingAdapterPosition])}
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder=
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_shopping_list_ingredients, parent, false)
        )

    override fun getItemCount(): Int = listIngredients.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("DataShoppingList", listIngredients[position])
        val ingredientsName = listIngredients[position]
        val ingredientsDetail = listIngredientsDetail[position]
        holder.bindRecipe(ingredientsName,ingredientsDetail)
    }

}