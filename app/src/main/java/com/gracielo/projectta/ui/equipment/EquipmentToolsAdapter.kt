package com.gracielo.projectta.ui.equipment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gracielo.projectta.R
import com.gracielo.projectta.databinding.ListEquipmentItemsBinding
import com.gracielo.projectta.ui.ingredients.DataEquipmentAdapter

class EquipmentToolsAdapter : RecyclerView.Adapter<EquipmentToolsAdapter.ViewHolder>() {

    private var listData =  ArrayList<DataEquipmentAdapter>()
    var onItemClick: ((DataEquipmentAdapter) -> Unit)? = null
    companion object {
        private const val TYPE_UNSELECT = 0
        private const val TYPE_SELECTED = 1
    }
    fun setData(newListData : List<DataEquipmentAdapter>?){
        if(newListData == null) return
        listData.clear()
        listData.addAll(newListData)
        notifyDataSetChanged()

    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ListEquipmentItemsBinding.bind(view)
        private val image = binding.imageEquipment
        private val textEquipment = binding.txtEquipmentName

        fun bindRecipe(equipment: DataEquipmentAdapter) {
            textEquipment.text=equipment.name
            Glide
                .with(itemView.context)
                .load("https://spoonacular.com/cdn/equipment_500x500/${equipment.image}")
                .apply(RequestOptions().override(100, 100))
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(image)
        }
        init{
            binding.root.setOnClickListener{onItemClick?.invoke(listData[bindingAdapterPosition])}
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = when (viewType){
            TYPE_SELECTED -> R.layout.list_equipment_items_checked
            TYPE_UNSELECT -> R.layout.list_equipment_items
            else -> throw IllegalArgumentException("invalid type")
        }
        val view=LayoutInflater.from(parent.context).inflate(layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = listData.size
    override fun getItemViewType(position: Int): Int {
        return if(listData[position].isSelected){
            TYPE_SELECTED
        } else TYPE_UNSELECT
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listData[position]
        holder.bindRecipe(data)
    }

}