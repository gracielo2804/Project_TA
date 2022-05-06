package com.gracielo.projectta.ui.homepage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gracielo.projectta.R
import com.gracielo.projectta.data.model.recipe.RecipeRecommendation

class RecommendedRecipeAdapter(private val callback : RecommendedItemCallback) :
    RecyclerView.Adapter<RecommendedRecipeAdapter.ViewHolder>() {

    private var listData =  ArrayList<RecipeRecommendation>()
    var onItemClick: ((RecipeRecommendation) -> Unit)? = null
    companion object {
        private const val TYPE_MOST_SEARCHED = 0
        private const val TYPE_BASIC = 1
        private const val TYPE_LOW_CARB = 2
        private const val TYPE_VEGETARIAN = 3

    }
    fun setData(newListData : List<RecipeRecommendation>?){
        if(newListData == null) return
        listData.clear()
        listData.addAll(newListData)
        notifyDataSetChanged()

    }

    inner class ViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(data: RecipeRecommendation) {
            Glide
                .with(itemView.context)
                .load(data.imageUrl)
                .apply(RequestOptions().override(100, 100))
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(itemView.findViewById(R.id.image_card_grid))

            itemView.findViewById<TextView>(R.id.text_card_grid).text = data.recipeName
            itemView.setOnClickListener{
                callback.onItemClicked(data)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = when (viewType){
            TYPE_MOST_SEARCHED -> R.layout.card_user_most_search
            TYPE_VEGETARIAN -> R.layout.card_vegetarian_recipe
            TYPE_LOW_CARB -> R.layout.card_low_carb_recipe
            TYPE_BASIC -> R.layout.card_recommendation_recipe
            else -> throw IllegalArgumentException("invalid type")
        }

        val view= LayoutInflater.from(parent.context).inflate(layout,parent,false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listData[position]
        holder.bind(data)
    }

    override fun getItemViewType(position: Int): Int {
        return when(listData[position].tipe){
           "Most Searched"-> TYPE_MOST_SEARCHED
            "Basic"-> TYPE_BASIC
            "Vegetarian"-> TYPE_VEGETARIAN
            "Low Carb"-> TYPE_LOW_CARB
            else -> 0
        }
    }


    override fun getItemCount(): Int {
        return listData.size
    }


}
interface RecommendedItemCallback {
    fun onItemClicked(data: RecipeRecommendation)
}