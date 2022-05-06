package com.gracielo.projectta.ui.history.nutrientHistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gracielo.projectta.R
import com.gracielo.projectta.data.model.nutrientsHistory.NutrientDataHistory
import com.gracielo.projectta.data.model.recipe.search.RecipeResponseItem
import com.gracielo.projectta.databinding.CardGridLayoutBinding
import com.gracielo.projectta.databinding.NutrientHistoryItemBinding

class NutrientHistoryAdapter : RecyclerView.Adapter<NutrientHistoryAdapter.ViewHolder>() {

    private var listData =  ArrayList<NutrientDataHistory>()
    var onItemClick: ((NutrientDataHistory) -> Unit)? = null

    fun setData(newListData : List<NutrientDataHistory>?){
        if(newListData == null) return
        listData.clear()
        listData.addAll(newListData)
        notifyDataSetChanged()

    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = NutrientHistoryItemBinding.bind(view)
        private val txtCalories = binding.txtCaloriesNutrientHistory
        private val txtCarbohydrate = binding.txtCarbohydrateNutrientHistory
        private val txtFat = binding.txtFatNutrientHistory
        private val txtSugar = binding.txtSugarNutrientHistory
        private val txtProtein = binding.txtProteinNutrientHistory
        private val txtTanggal = binding.txtTanggalNutrientHistory

        fun bindRecipe(nutrient: NutrientDataHistory) {
           txtCalories.text = "${nutrient.calories} kcal"
           txtCarbohydrate.text = "${nutrient.carbohydrate} g"
           txtSugar.text = "${nutrient.sugar} g"
           txtFat.text = "${nutrient.fat} g"
           txtProtein.text = "${nutrient.protein} g"
            txtTanggal.text = nutrient.tanggal

        }
        init{
            binding.root.setOnClickListener{onItemClick?.invoke(listData[bindingAdapterPosition])}
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder=
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.nutrient_history_item, parent, false)
        )

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listData[position]
        holder.bindRecipe(data)
    }

}