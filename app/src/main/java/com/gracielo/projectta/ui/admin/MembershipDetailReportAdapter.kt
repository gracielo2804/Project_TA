package com.gracielo.projectta.ui.admin

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.gracielo.projectta.data.model.admin.allMembershipTransaction.DataAllMembership
import de.codecrafters.tableview.TableDataAdapter


class MembershipDetailReportAdapter(context: Context, listDatas: ArrayList<DataAllMembership?>) :
    TableDataAdapter<DataAllMembership?>(context,listDatas) {

    private var listData =  ArrayList<DataAllMembership>()

    fun setDatas(newListData : List<DataAllMembership>?){
        Log.d("CountDatamembershipTrans " , "Size = ${newListData?.size}")
        if(newListData == null) return
        listData.clear()
        listData.addAll(newListData)
        notifyDataSetChanged()

    }



    override fun getCellView(rowIndex: Int, columnIndex: Int, parentView: ViewGroup?): View? {
       val dataMembership= listData[rowIndex]
        return when (columnIndex) {
            0 ->   renderColumn2(dataMembership.idTrans)
            1 -> renderColumn2(dataMembership.tanggal)
            2 -> renderColumn2(dataMembership.namaCust)
            3 -> renderColumn2(dataMembership.namaPaket)
            4 -> renderColumn2(dataMembership.nominal)
            5 ->
                if(dataMembership.statusTrans=="0"){
                    return renderColumn2("Failed")
                }
                else if(dataMembership.statusTrans=="2"){
                    return renderColumn2("Pending")
                }
                else{
                    return renderColumn2("Success")
                }

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