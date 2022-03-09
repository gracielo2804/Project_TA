package com.gracielo.projectta.ui.recipe

import android.widget.ArrayAdapter
import com.gracielo.projectta.R
import android.content.Context
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


import android.view.LayoutInflater


class ArrayAdapterCustom(private val mContext: Context, list: List<String>) :
    ArrayAdapter<String?>(mContext, 0, list.toMutableList() as List<String?>) {
    private var stringList: List<String> = ArrayList()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItem = convertView
        if (listItem == null) listItem =
            LayoutInflater.from(mContext).inflate(R.layout.layout_text, parent, false)

        val currentString = stringList[position]
        val stringSplit=currentString.split("-----")
        var string1=""
        var string2=""
        if(stringSplit.size>1){
            string1=stringSplit[0]
            string2=getColoredSpanned(stringSplit[1],"#FF0000")
        }
        else string1 = stringSplit[0]
        val stringDisplay = string1+string2
        val name = listItem?.findViewById<View>(R.id.list_content) as TextView
        name.text = Html.fromHtml(stringDisplay)
        return listItem
    }

    init {
        stringList = list
    }

    private fun getColoredSpanned(text: String, color: String): String {
        return "<font color=$color>$text</font>"
    }
}