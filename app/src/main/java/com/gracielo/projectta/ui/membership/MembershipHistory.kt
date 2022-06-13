package com.gracielo.projectta.ui.membership

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.gracielo.projectta.R
import com.gracielo.projectta.data.model.membershipTransactionHistory.DataMembershipHistory
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityMembershipHistoryBinding
import com.gracielo.projectta.ui.history.nutrientHistory.PDFUtilityNutrientHistory
import com.gracielo.projectta.util.FunHelper
import com.gracielo.projectta.util.FunHelper.observeOnce
import com.gracielo.projectta.viewmodel.UserViewModel
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.File

class MembershipHistory : AppCompatActivity(),PDFUtilityMembershipHistoryUser.OnDocumentClose {

    private lateinit var binding : ActivityMembershipHistoryBinding
    private val apiServices = ApiServices()
    lateinit var viewModel: UserViewModel
    private var helper= FunHelper
    val adapters = MembershipHistoryAdapter()
    val dataMembership = mutableListOf<DataMembershipHistory>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembershipHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        apiServices.tempFuncUpdateStatus {  }
        val rvHistory=binding.rvMembershipHistory

        viewModel=helper.obtainUserViewModel(this)
        viewModel.getUser().observeOnce(this){
            apiServices.GetUserMembershipTransaction(it.id){transaction->
                if(transaction!=null){
                    adapters.setData(transaction.dataMembershipHistory)
                    rvHistory.apply {
                        layoutManager = LinearLayoutManager(this@MembershipHistory)
                        adapter = adapters
                    }
                    dataMembership.addAll(transaction.dataMembershipHistory)
                }
            }
        }
        supportActionBar?.hide()
        binding.openHistoryMembershipMenu.setOnClickListener {
            showPopup(it)
        }
    }

    private fun showPopup(v: View) {
        PopupMenu(this, v).apply {
            setOnMenuItemClickListener(object: PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem?): Boolean {
                    return when (item?.itemId) {
                        R.id.item_ingredients_recipe_export -> {
                            val pdfMaker = PDFUtilityMembershipHistoryUser
                            val current = LocalDateTime.now()
                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                            val formatted = current.format(formatter)
                            println("Current Date and Time is: $formatted")
                            val path =
                                "${getExternalFilesDir(null)}${File.separator} Nutrition History Report.pdf"
                            try {
                                pdfMaker.setCreatedDate(formatted)
                                pdfMaker.createPdf(
                                    v.context,
                                    this@MembershipHistory,
                                    getData(),
                                    path,
                                    true
                                )

                            } catch (e: Exception) {
                                e.printStackTrace()
                                Log.e("PDFNEW", "Error Creating Pdf")
                                Toast.makeText(v.context, "Error Creating Pdf", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            true
                        }
                        else -> false
                    }
                }

            })
            inflate(R.menu.menu_admin_ingredients_recipe)
            show()
        }
    }


    private fun getData(): List<Array<String>> {
        var count = dataMembership.size
        val temp: MutableList<Array<String>> = java.util.ArrayList()
        for (i in 0 until count) {
            val data = dataMembership[i]

            temp.add(arrayOf(data.idTransaksi,data.tanggal,data.namaPaket,data.nominal,checkstatus(data.status)))
        }
        return temp

    }

    private fun renderPdf(context: Context, filePath: File) {
        val uri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            filePath
        )
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(uri, "application/pdf")

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {

        }
    }

    override fun onPDFDocumentClose(file: File?) {
        Toast.makeText(this,"Pdf Created", Toast.LENGTH_SHORT).show()
        renderPdf(this,file!!)
    }

    private fun checkstatus(status:String):String{
        var hasilreturn=""
        when (status) {
            "0" -> {hasilreturn="Failed"}
            "1" -> {hasilreturn="Success"}
            "2" -> {hasilreturn="Pending"}
        }
        return hasilreturn
    }

}