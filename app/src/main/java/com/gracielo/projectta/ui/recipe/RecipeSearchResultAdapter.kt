package com.gracielo.projectta.ui.recipe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gracielo.projectta.R
import com.gracielo.projectta.data.model.recipe.detail.RecipeDetailResponseItem
import com.gracielo.projectta.databinding.CardGridLayoutBinding

class RecipeSearchResultAdapter : RecyclerView.Adapter<RecipeSearchResultAdapter.ViewHolder>() {

    private var listData =  ArrayList<RecipeDetailResponseItem>()
    var onItemClick: ((RecipeDetailResponseItem) -> Unit)? = null

    fun setData(newListData : List<RecipeDetailResponseItem>?){
        if(newListData == null) return
        listData.clear()
        listData.addAll(newListData)
        notifyDataSetChanged()

    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = CardGridLayoutBinding.bind(view)
        private val imageView:ImageView = view.findViewById(R.id.image_card_grid)
        private val recipeNameText : TextView = view.findViewById(R.id.text_card_grid)

        fun bindRecipe(recipe: RecipeDetailResponseItem) {
           recipeNameText.text = recipe.title
            Glide
                .with(itemView.context)
                .load(recipe.image)
                .apply(RequestOptions().override(200, 175))
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(imageView)
        }
        init{
            binding.root.setOnClickListener{onItemClick?.invoke(listData[bindingAdapterPosition])}
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder=
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.card_grid_layout, parent, false)
        )

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listData[position]
//        holder.itemView.setOnClickListener {
//            val intentKirim = Intent(holder.itemView.context,RecipeDetailActivity::class.java)
//            intentKirim.putExtra("idrecipe",it.id)
//            startActivity(holder.itemView.context,intentKirim,null)
//        }
        holder.bindRecipe(data)
    }

}