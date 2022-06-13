package com.gracielo.projectta.ui.admin

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.gracielo.projectta.data.model.admin.allMembershipTransaction.DataAllMembership
import com.gracielo.projectta.data.model.admin.allUser.DataAllUser
import com.jakewharton.threetenabp.AndroidThreeTen
import de.codecrafters.tableview.TableDataAdapter
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat


class UserDetailReportAdapter(context: Context, listDatas: ArrayList<DataAllUser?>):
    TableDataAdapter<DataAllUser>(context,listDatas){


    init{
        AndroidThreeTen.init(context)
    }
    private var listData =  ArrayList<DataAllUser>()


    fun setDatas(newListData : List<DataAllUser>?){
        Log.d("CountDataUser " , "Size = ${newListData?.size}")
        if(newListData == null) return
        listData.clear()
        listData.addAll(newListData)
        notifyDataSetChanged()

    }



    override fun getCellView(rowIndex: Int, columnIndex: Int, parentView: ViewGroup?): View? {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatted = current.format(formatter)
        val df = SimpleDateFormat("yyyy-MM-dd")
        val dataMembership= listData[rowIndex]
        val expired = dataMembership.expired
        var statusExpired = 0
        val expiredDate= df.parse(expired)
        val currentDate = df.parse(formatted)

        if(expiredDate.before(currentDate))statusExpired = 0
        else statusExpired = 1
        return when (columnIndex) {
            0 ->   renderColumn2(dataMembership.username)
            1 -> renderColumn2(dataMembership.nama)
            2 -> renderColumn2(dataMembership.email)
            3 -> if(dataMembership.emailVerified=="0"){
                return renderColumn2("Not Verified")
            }
            else if(dataMembership.emailVerified=="1"){
                return renderColumn2("Verified")
            }
            else{
                return renderColumn2("Not Verified")
            }
            4 ->if(statusExpired!=0){
                return renderColumn2(expired)
            }
            else{
                return renderColumn2Red(expired)
            }

            5 -> renderColumn2(dataMembership.last_login)

            else -> renderColumn2("")
        }
    }

    private fun renderColumn2(data:String): View? {
        val textView = TextView(context)
        textView.setText(data.trim())
        textView.textSize= 10f
        textView.setPadding(10, 10, 10, 10)
        return textView
    }
    private fun renderColumn2Red(data:String): View? {
        val textView = TextView(context)
        textView.setText(data.trim())
        textView.setTextColor(Color.RED)
        textView.textSize= 10f
        textView.setPadding(10, 10, 10, 10)
        return textView
    }

}