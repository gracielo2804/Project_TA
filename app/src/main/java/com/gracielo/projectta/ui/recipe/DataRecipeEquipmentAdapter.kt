package com.gracielo.projectta.ui.recipe

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gracielo.projectta.R
import com.gracielo.projectta.data.model.listEquipment.DataEquipment
import com.gracielo.projectta.databinding.CardGridLayoutBinding

class DataRecipeEquipmentAdapter : RecyclerView.Adapter<DataRecipeEquipmentAdapter.ViewHolder>() {

    private var listName =  ArrayList<String>()
    private var listNameUser = ArrayList<String>()
    private var listImage =  ArrayList<String>()
    var onItemClick: ((String) -> Unit)? = null

    fun setData(newListName : List<String> ,newListImage:List<String>,newListNameUser:List<String>){
        listName.clear()
        listName.addAll(newListName)
        listImage.clear()
        listImage.addAll(newListImage)
        listNameUser.clear()
        listNameUser.addAll(newListNameUser)
        notifyDataSetChanged()

    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = CardGridLayoutBinding.bind(view)
        private val imageView: ImageView = view.findViewById(R.id.image_card_grid)
        private val recipeNameText : TextView = view.findViewById(R.id.text_card_grid)

        fun bindRecipe(name:String,image:String) {
            var checkname= false
            for(i in listNameUser.indices){
                if(listNameUser[i]==name){
                    checkname=true
                }
            }
            recipeNameText.text = name
            if(!checkname){
                recipeNameText.setTextColor(Color.RED)
            }


            Glide
                .with(itemView.context)
                .load("https://spoonacular.com/cdn/equipment_500x500/${image}")
                .apply(RequestOptions().override(150, 110))
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(imageView)
        }
        init{
            binding.root.setOnClickListener{onItemClick?.invoke(listName[bindingAdapterPosition])}
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.card_grid_layout, parent, false)
        )

    override fun getItemCount(): Int = listName.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val name = listName[position]
        val image = listImage[position]
        holder.bindRecipe(name,image)
    }

}