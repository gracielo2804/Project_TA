package com.gracielo.projectta.ui.admin.ingredients

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.gracielo.projectta.data.model.countIngredients.DataCountIngredients
import de.codecrafters.tableview.TableDataAdapter


class IngredientsListCountAdapter(context: Context, listDatas: ArrayList<DataCountIngredients>) :
    TableDataAdapter<DataCountIngredients>(context,listDatas) {

    private var listData =  ArrayList<DataCountIngredients>()

    fun setDatas(newListData : List<DataCountIngredients>?){
        Log.d("CountRecipe1 " , "Size = ${newListData?.size}")
        if(newListData == null) return
        listData.clear()
        listData.addAll(newListData)
        notifyDataSetChanged()

    }



    override fun getCellView(rowIndex: Int, columnIndex: Int, parentView: ViewGroup?): View? {
        val dataIngredients= listData[rowIndex]
        return when (columnIndex) {
            0 ->   renderColumn2(dataIngredients.name)
            1 -> renderColumn2(dataIngredients.count.toString())
            else -> renderColumn2("")
        }
    }

    private fun renderColumn2(data:String): View? {
        val textView = TextView(context)
        textView.setText(data.trim())
        textView.setPadding(20, 10, 20, 10)
        return textView
    }

}