package com.gracielo.projectta.ui.admin.ingredients

import android.app.AlertDialog
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
import com.gracielo.projectta.R
import com.gracielo.projectta.data.model.admin.allMembershipTransaction.DataAllMembership
import com.gracielo.projectta.data.model.countIngredients.DataCountIngredients
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityAdminIngredientBinding
import com.gracielo.projectta.ui.admin.AdminHomeActivity
import com.gracielo.projectta.ui.admin.AdminUserReportActivity
import com.gracielo.projectta.ui.admin.pdfIngredientsRecipe.PDFUtilityIngredientsRecipe
import com.gracielo.projectta.ui.admin.recipe.AdminRecipeActivity
import com.gracielo.projectta.util.FunHelper
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.File

class AdminIngredientActivity : AppCompatActivity(), PDFUtilityIngredientsRecipe.OnDocumentClose {
    private lateinit var binding: ActivityAdminIngredientBinding
    var listIngredientCountTemp = mutableListOf<DataCountIngredients>()
    var listIngredientCount = mutableListOf<DataCountIngredients>()
    var apiServices = ApiServices()
    var helper = FunHelper
    val TABLE_HEADERS = arrayOf("Ingredients","Total Used")
//    val TABLE_HEADERS = arrayOf("Transaction ID","Date","Users,","Package Name","Price","Status")
    lateinit var adapters: IngredientsListCountAdapter
//    lateinit var adapters: MembershipDetailReportAdapter
    var dataCountIngredientsString = mutableListOf(arrayOf<String>())
    private val DATA_TO_SHOW =
        arrayOf(arrayOf("This", "is", "a", "test"), arrayOf("and", "a", "second", "test"))

    private val dataTransaksiMembership = mutableListOf<DataAllMembership>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminIngredientBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        apiServices.getAllMembershipTransaction {
//            dataTransaksiMembership.addAll(it!!.dataAllMembership)
//        }

        apiServices.getIngredientsCount {
            if (it?.code==1){
                listIngredientCountTemp.addAll(it.dataCountIngredients)
                for(i in listIngredientCountTemp.indices){
                    var highestCount = listIngredientCountTemp[i]
                    for(j in i..listIngredientCountTemp.size-1){
                        if(listIngredientCountTemp[j].count>highestCount.count){
                            var temp = listIngredientCountTemp[j]
                            listIngredientCountTemp[j]=highestCount
                            highestCount=temp
                        }
                    }
                    listIngredientCount.add(highestCount)
                }
                for(i in listIngredientCount.indices){
//                    Log.d("CountRecipe " , " $i -- ${listIngredientCount[i].toString()}")
                }
                for(i in listIngredientCount.indices){
                    val data = arrayOf(listIngredientCount[i].name,listIngredientCount[i].count.toString())
                    dataCountIngredientsString.add(data)
                }
                adapters = IngredientsListCountAdapter(this,
                    listIngredientCount as ArrayList<DataCountIngredients>
                )
//                adapters = MembershipDetailReportAdapter(this,dataTransaksiMembership as ArrayList<DataAllMembership?>)
                adapters.setDatas(listIngredientCount)
//                adapters.setDatas(dataTransaksiMembership)
                Log.d("CountRecipe " , "Size = ${adapters.count}")
                val tableView = binding.tableView
                tableView.headerAdapter= SimpleTableHeaderAdapter(this,*TABLE_HEADERS)
                tableView.setDataAdapter(adapters)
            }
        }

        binding.openIngredientsMenu.setOnClickListener{
            showPopup(it)
        }


        val bottomNavBar = binding.adminBottomMenu
        bottomNavBar.selectedItemId = R.id.adminIngredients
        bottomNavBar.setOnItemSelectedListener {
            when(it.itemId){
                R.id.adminSubscription->{
                    val intent = Intent(this, AdminHomeActivity::class.java)
                    finish()
                    startActivity(intent)

                }
                R.id.adminUser->{
                    val intent = Intent(this, AdminUserReportActivity::class.java)
                    finish()
                    startActivity(intent)
                }
                R.id.adminIngredients->{
//                    val intent = Intent(this, AdminIngredientActivity::class.java)
//                    finish()
//                    startActivity(intent)

                }
                R.id.adminRecipe->{
                    intent = Intent(this, AdminRecipeActivity::class.java)
                    finish()
                    startActivity(intent)
                }
            }
            true
        }


    }
    private class IngredientsComparator : Comparator<DataCountIngredients> {
        override fun compare(p0: DataCountIngredients, p1: DataCountIngredients): Int {
            return p0.name.compareTo(p1.name)
        }
    }
    private class IngredientsComparatorCount : Comparator<DataCountIngredients> {
        override fun compare(p0: DataCountIngredients, p1: DataCountIngredients): Int {
            return p0.count.compareTo(p1.count)
        }
    }

    private fun showPopup(v: View) {
        PopupMenu(this, v).apply {
            setOnMenuItemClickListener(object: PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem?): Boolean {
                    return when (item?.itemId) {

                        R.id.item_ingredients_recipe_export -> {
                            val pdfMaker = PDFUtilityIngredientsRecipe
                            val current = LocalDateTime.now()
                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                            val formatted = current.format(formatter)
                            println("Current Date and Time is: $formatted")
                            val path =
                                "${getExternalFilesDir(null)}${File.separator} Ingredients Report.pdf"
                            try {
                                pdfMaker.setType("Ingredients")
                                pdfMaker.setCreatedDate(formatted)
                                pdfMaker.createPdf(
                                    v.context,
                                    this@AdminIngredientActivity,
                                    getData(listIngredientCount),
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
    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Logout Admin")
            .setMessage("You Will Be Logged Out from Admin ?") // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton("Yes"
            ) { _, _ ->
//                Toast.makeText(this,"Successfully Logout",Toast.LENGTH_SHORT).show()
                finish()
//                viewModel.getUser()?.removeObserver(){}

            } // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.no, null)
//            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun getData(dataTransaksi : MutableList<DataCountIngredients>): List<Array<String>> {
        var count = dataTransaksi.size

        val temp: MutableList<Array<String>> = java.util.ArrayList()
        for (i in 0 until count) {
            val data = dataTransaksi[i]
            temp.add(arrayOf(data.name,data.count.toString()))
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
        Toast.makeText(this,"Pdf Created",Toast.LENGTH_SHORT).show()
        renderPdf(this@AdminIngredientActivity,file!!)
    }
}