package com.gracielo.projectta.ui.ingredients

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gracielo.projectta.R
import com.gracielo.projectta.data.source.local.entity.Ingredients
import com.gracielo.projectta.databinding.IngridientsItemListDeleteBinding

class IngridientsListSelectedAdapters:
    RecyclerView.Adapter<IngridientsListSelectedAdapters.ViewHolder>() {

    private var listData = mutableListOf<Ingredients>()
    var onItemClick: ((Ingredients) -> Unit)? = null

    fun setData(newList : List<Ingredients>){
        if(newList==null) return
        listData.clear()
        listData.addAll(newList)
        notifyDataSetChanged()

    }


    inner class ViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = IngridientsItemListDeleteBinding.bind(itemView)
        fun bind(data: Ingredients) {
            Glide
                .with(itemView.context)
                .load("https://spoonacular.com/cdn/ingredients_100x100/${data.image}")
                .apply(RequestOptions().override(100, 100))
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(itemView.findViewById(R.id.imageIngredients))

            itemView.findViewById<TextView>(R.id.txtIngridientsName).text = data.name
        }
        init{
            binding.root.setOnClickListener { onItemClick?.invoke(listData[layoutPosition]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = R.layout.ingridients_item_list_delete

        val view=LayoutInflater.from(parent.context).inflate(layout,parent,false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listData[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int {
       return listData.size
    }

}
