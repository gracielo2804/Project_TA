package com.gracielo.projectta.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gracielo.projectta.R
import com.gracielo.projectta.databinding.SettingItemListBinding

class HistoryListAdapter : RecyclerView.Adapter<HistoryListAdapter.ViewHolder>() {

    private var listData =  ArrayList<String>()
    var onItemClick: ((String) -> Unit)? = null

    fun setData(newListData : List<String>?){
        if(newListData == null) return
        listData.clear()
        listData.addAll(newListData)
        notifyDataSetChanged()

    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = SettingItemListBinding.bind(view)
        private val settingNameText : TextView = view.findViewById(R.id.textSettingName)

        fun bindRecipe(text: String) {
            settingNameText.text = text
        }
        init{
            binding.root.setOnClickListener{onItemClick?.invoke(listData[bindingAdapterPosition])}
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder=
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.setting_item_list, parent, false)
        )

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listData[position]
        holder.bindRecipe(data)
    }

}