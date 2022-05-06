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
import com.gracielo.projectta.ui.equipment.EquipmentToolsAdapter


class IngridientsListSearchsAdapters(private val callback : IngridientsListSearchsItemCallback) :
    RecyclerView.Adapter<IngridientsListSearchsAdapters.ViewHolder>() {

    private var listData =  ArrayList<Ingredients>()
    var onItemClick: ((Ingredients) -> Unit)? = null
    companion object {

        private const val TYPE_UNSELECT = 0
        private const val TYPE_SELECTED = 1

    }
    fun setData(newListData : List<Ingredients>?){
        if(newListData == null) return
        listData.clear()
        listData.addAll(newListData)
        notifyDataSetChanged()

    }

    inner class ViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(data: Ingredients) {
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
        val data = listData[position]
        holder.bind(data)
    }

    override fun getItemViewType(position: Int): Int {
        return if(listData[position].isSelected){
            TYPE_SELECTED
        } else TYPE_UNSELECT
    }


    override fun getItemCount(): Int {
        return listData.size
    }


}
interface IngridientsListSearchsItemCallback {
    fun onItemSearchClicked(data: Ingredients)
}