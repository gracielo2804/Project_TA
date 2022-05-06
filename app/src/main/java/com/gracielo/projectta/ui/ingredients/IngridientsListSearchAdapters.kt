package com.gracielo.projectta.ui.ingredients

import android.content.ClipData.Item
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.AsyncPagedListDiffer
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gracielo.projectta.R
import com.gracielo.projectta.data.source.local.entity.Ingredients
import com.gracielo.projectta.data.source.local.entity.IngredientsSearch


class IngridientsListSearchAdapters(private val callback : IngridientsSearchItemCallback) :
    PagedListAdapter<IngredientsSearch, IngridientsListSearchAdapters.ViewHolder>(DIFF_CALLBACK) {

    companion object {

        private const val TYPE_UNSELECT = 0
        private const val TYPE_SELECTED = 1
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<IngredientsSearch>() {
            override fun areItemsTheSame(oldItem: IngredientsSearch, newItem: IngredientsSearch): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: IngredientsSearch, newItem: IngredientsSearch): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(data: IngredientsSearch) {
            Glide
                .with(itemView.context)
                .load("https://spoonacular.com/cdn/ingredients_100x100/${data.image}")
                .apply(RequestOptions().override(100, 100))
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(itemView.findViewById(R.id.imageIngredients))

            itemView.findViewById<TextView>(R.id.txtIngridientsName).text = data.name
            itemView.setOnClickListener{
                callback.onItemSearchClicked(data)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = when (viewType){
            TYPE_SELECTED -> R.layout.ingridients_item_list_checked
            TYPE_UNSELECT -> R.layout.ingridients_item_list
            else -> throw IllegalArgumentException("invalid type")
        }

        val view=LayoutInflater.from(parent.context).inflate(layout,parent,false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(getItem(position)!!.isSelected){
            TYPE_SELECTED
        } else TYPE_UNSELECT
    }



}
interface IngridientsSearchItemCallback {
    fun onItemSearchClicked(data: IngredientsSearch)
}