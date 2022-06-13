package com.gracielo.projectta.ui.history.nutrientHistory

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
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.gracielo.projectta.R
import com.gracielo.projectta.data.model.nutrientsHistory.NutrientDataHistory
import com.gracielo.projectta.data.source.local.entity.UserNutrientsEntity
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityNutrientHistoryBinding
import com.gracielo.projectta.ui.shoppingList.PDFUtilityShoppingList
import com.gracielo.projectta.util.FunHelper
import com.gracielo.projectta.viewmodel.UserViewModel
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.File

class NutrientHistoryActivity : AppCompatActivity(),PDFUtilityNutrientHistory.OnDocumentClose{

    private lateinit var binding: ActivityNutrientHistoryBinding
    var apiServices = ApiServices()
    var helper = FunHelper
    lateinit  var userViewModel :UserViewModel
    val adapters = NutrientHistoryAdapter()
    val dataHistoryNutrient = mutableListOf<NutrientDataHistory>()
    lateinit var userNutrientEntity:UserNutrientsEntity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNutrientHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel = helper.obtainUserViewModel(this)
        userViewModel.getUserNutrients().observeOnce(this){
            userNutrientEntity =it
        }
        userViewModel.getUser().observeOnce(this){
            if(it!=null){
                apiServices.getUserNutrientHistory(it.id){nutrientData->
                    if(nutrientData!=null){
                        adapters.setData(nutrientData.dataHistory)
                        binding.rvNutritentHistory.apply {
                            layoutManager = LinearLayoutManager(this@NutrientHistoryActivity)
                            adapter = adapters
                        }
                        dataHistoryNutrient.addAll(nutrientData.dataHistory)
                    }
                }
            }
        }

        binding.openNutritionHistoryMenu.setOnClickListener {
            showPopup(it)
        }

        supportActionBar?.hide()
    }
    fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }

    private fun showPopup(v: View) {
        PopupMenu(this, v).apply {
            setOnMenuItemClickListener(object: PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem?): Boolean {
                    return when (item?.itemId) {
                        R.id.item_ingredients_recipe_export -> {
                            val pdfMaker = PDFUtilityNutrientHistory
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
                                    this@NutrientHistoryActivity,
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
        var count = dataHistoryNutrient.size
        val temp: MutableList<Array<String>> = java.util.ArrayList()
        for (i in 0 until count) {
            val data = dataHistoryNutrient[i]
            var nutrientLimit =""
            nutrientLimit +="Calories : ${userNutrientEntity.maxKalori} kcal \n"
            nutrientLimit +="Fat : ${userNutrientEntity.maxLemak} g \n"
            nutrientLimit +="Carbohydrate : ${userNutrientEntity.maxKarbo} g \n"
            nutrientLimit +="Sugar : ${userNutrientEntity.maxGula} g \n"
            nutrientLimit +="Protein : ${userNutrientEntity.maxProtein} g"

            var nutrientHistory = ""
            nutrientHistory +="Calories : ${data.calories} kcal \n"
            nutrientHistory +="Fat : ${data.fat} g \n"
            nutrientHistory +="Carbohydrate : ${data.carbohydrate} g \n"
            nutrientHistory +="Sugar : ${data.sugar} g \n"
            nutrientHistory +="Protein : ${data.protein} g"

            temp.add(arrayOf(data.tanggal,nutrientLimit,nutrientHistory))
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


}