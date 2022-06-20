package com.gracielo.projectta.ui.shoppingList

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gracielo.projectta.R
import com.gracielo.projectta.data.source.local.entity.ShoppingListEntity
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ItemShoppingListBinding

class ShoppingListRecipeAdapter(private val callback : ShoppingListCallback): RecyclerView.Adapter<ShoppingListRecipeAdapter.ViewHolder>() {

    private var listRecipe = ArrayList<ShoppingListEntity>()
    private var listRecipeName = ArrayList<String>()
    private var listIngredients =  mutableMapOf<String,List<String>>()
    private var listIngredientsDetail =  mutableMapOf<String,List<String>>()
    var onItemClick: ((String) -> Unit)? = null
    private var apiServices = ApiServices()

    fun setData(newListRecipe : List<ShoppingListEntity>,newListRecipeName : List<String>, newlistIngredients : Map<String,List<String>>?, newlistIngredientsDetail : Map<String,List<String>>?){
        if(newlistIngredients == null || newlistIngredientsDetail == null) return
        listRecipe.clear()
        listIngredients.clear()
        listIngredientsDetail.clear()
        listRecipe.addAll(newListRecipe)
        listRecipeName.addAll(newListRecipeName)
        listIngredients.putAll(newlistIngredients)
        listIngredientsDetail.putAll(newlistIngredientsDetail)
        for(i in listRecipe.indices){
            apiServices.getRecipeName(listRecipe[i].id_recipe){
                if (it != null) {
                    if(it.code==1){
                        listRecipeName.add(it.message)
                    }
                }

            }
        }
        notifyDataSetChanged()
        Log.d("ShoppingList2",listRecipe.toString())
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemShoppingListBinding.bind(itemView)
        private val recipeName = binding.txtRecipeNameShoppingList
        val rvIngredients = binding.rvIngredientsShoppingList
        val buttondelete= binding.btnDelete
        val ingredientsAdapter = ShoppingListIngredientsAdapter()

        fun bindRecipe(recipeData: ShoppingListEntity, position: Int) {
            recipeName.text= listRecipe[position].recipe_name
            Glide
                .with(itemView.context)
                .load("https://spoonacular.com/recipeImages/${recipeData.id_recipe}-556x370.jpg")
//                .apply(RequestOptions().override(100, 100))
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(binding.imageViewRecipeShoppingList)
            buttondelete.setOnClickListener {
                callback.onButtonDeleteClicked(recipeData)
            }
        }
        init{
            binding.root.setOnClickListener{onItemClick?.invoke(listRecipeName[bindingAdapterPosition])}
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder=
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_shopping_list, parent, false)
        )

    override fun getItemCount(): Int = listRecipe.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindRecipe(listRecipe[position],position)
        val recipeName = listRecipe[position].recipe_name
        val listIngredients = listIngredients[recipeName]
        val listIngredientsDetail = listIngredientsDetail[recipeName]
        holder.ingredientsAdapter.setData(listIngredients,listIngredientsDetail)
        holder.rvIngredients.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = holder.ingredientsAdapter
        }
    }
}

interface ShoppingListCallback{
    fun onButtonDeleteClicked(recipeData: ShoppingListEntity)
}