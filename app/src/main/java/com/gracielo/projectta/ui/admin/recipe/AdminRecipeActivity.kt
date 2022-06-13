package com.gracielo.projectta.ui.admin.recipe

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
import com.gracielo.projectta.data.model.recipeCount.DataCountRecipe
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityAdminRecipeBinding
import com.gracielo.projectta.ui.admin.AdminHomeActivity
import com.gracielo.projectta.ui.admin.AdminUserReportActivity
import com.gracielo.projectta.ui.admin.pdfIngredientsRecipe.PDFUtilityIngredientsRecipe
import com.gracielo.projectta.ui.admin.ingredients.AdminIngredientActivity
import com.gracielo.projectta.util.FunHelper
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.File

class AdminRecipeActivity : AppCompatActivity(), PDFUtilityIngredientsRecipe.OnDocumentClose {
    private lateinit var binding:ActivityAdminRecipeBinding
    var listRecipeCount = mutableListOf<DataCountRecipe>()
    var apiServices = ApiServices()
    val TABLE_HEADERS = arrayOf("Recipe Name","User Search Count")
    lateinit var adapters: RecipeListCountAdapter
    var helper = FunHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        apiServices.getRecipeCount {
            if (it?.code==1){
                listRecipeCount.addAll(it.dataCountRecipe)
                adapters = RecipeListCountAdapter(this,listRecipeCount as ArrayList<DataCountRecipe>)
                adapters.setDatas(listRecipeCount)
                val tableView = binding.tableView
                tableView.headerAdapter= SimpleTableHeaderAdapter(this,*TABLE_HEADERS)
                tableView.setDataAdapter(adapters)
            }
        }

        binding.openRecipeMenu.setOnClickListener{
            showPopup(it)
        }

        val bottomNavBar = binding.adminBottomMenu
        bottomNavBar.selectedItemId = R.id.adminRecipe
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
                    val intent = Intent(this, AdminIngredientActivity::class.java)
                    finish()
                    startActivity(intent)

                }
                R.id.adminRecipe->{
//                    intent = Intent(this, AdminRecipeActivity::class.java)
//                    finish()
//                    startActivity(intent)
                }
            }
            true
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
                                "${getExternalFilesDir(null)}${File.separator} Recipe Report.pdf"
                            try {
                                pdfMaker.setType("Recipe")
                                pdfMaker.setCreatedDate(formatted)
                                pdfMaker.createPdf(
                                    v.context,
                                    this@AdminRecipeActivity,
                                    getData(listRecipeCount),
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
    private fun getData(dataTransaksi : MutableList<DataCountRecipe>): List<Array<String>> {
        var count = dataTransaksi.size

        val temp: MutableList<Array<String>> = java.util.ArrayList()
        for (i in 0 until count) {
            val data = dataTransaksi[i]
            temp.add(arrayOf(data.recipeName,data.jumlah.toString()))
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
        renderPdf(this@AdminRecipeActivity,file!!)
    }
}