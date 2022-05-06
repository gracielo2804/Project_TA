package com.gracielo.projectta.ui.admin

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.gracielo.projectta.R
import com.gracielo.projectta.data.model.admin.allMembershipTransaction.DataAllMembership
import com.gracielo.projectta.data.model.admin.allUser.DataAllUser
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityAdminUserReportBinding
import com.gracielo.projectta.ui.admin.chart.ChartValueCustomFormatter
import com.gracielo.projectta.ui.admin.ingredients.AdminIngredientActivity
import com.gracielo.projectta.ui.admin.recipe.AdminRecipeActivity
import java.util.ArrayList

class AdminUserReportActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAdminUserReportBinding
    private val apiServices = ApiServices()
    private lateinit var pieChart: PieChart
    private val dataAllUser = mutableListOf<DataAllUser>()
    var listUmur = mutableListOf<String>()
    var counterUmur = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminUserReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavBar = binding.adminBottomMenu
        bottomNavBar.selectedItemId = R.id.adminUser
        bottomNavBar.setOnItemSelectedListener {
            when(it.itemId){
                R.id.adminSubscription->{
                    val intent = Intent(this, AdminHomeActivity::class.java)
                    finish()
                    startActivity(intent)

                }
                R.id.adminUser->{
//                    val intent = Intent(this, AdminUserReportActivity::class.java)
//                    finish()
//                    startActivity(intent)
                }
                R.id.adminIngredients->{
                    val intent = Intent(this, AdminIngredientActivity::class.java)
                    finish()
                    startActivity(intent)

                }
                R.id.adminRecipe->{
                    intent = Intent(this, AdminRecipeActivity::class.java)
                    finish()
                    startActivity(intent)
                }

            }
            true
        }
        pieChart = binding.chartUserReport
        apiServices.getAllUsers {
            dataAllUser.addAll(it!!.dataAllUser)
        }
        if(dataAllUser.size==0){
            Handler(Looper.getMainLooper()).postDelayed({
                if(dataAllUser.size==0){
                    Log.d("data","DataKosong")
                }
                else{
                    olahDataTransaksi(dataAllUser)
                }
            },1000)
        }
        else olahDataTransaksi(dataAllUser)

    }

    fun olahDataTransaksi(dataUser:List<DataAllUser>){
        // --> Prepare Data
        listUmur.clear()
        counterUmur.clear()

        listUmur.add("0-24 Years Old")
        listUmur.add("25-49 Years Old")
        listUmur.add("49+ Years Old")


        for (i in listUmur.indices){
            counterUmur.add(0);

        }

        for (i in dataUser.indices){
            Log.d("DataUserAll",dataUser[i].toString())
            if(dataUser[i].age.toInt() in 1..24)counterUmur[0]++
            else if(dataUser[i].age.toInt() in 25..49)counterUmur[1]++
            else{
                counterUmur[2]++
            }
        }
        // --> End Prepare Data

        // --> Prepare Piechart


        val entries: ArrayList<PieEntry> = ArrayList()
        for (i in listUmur.indices){
            Log.d("DataUserAll",counterUmur[i].toString())
            entries.add(PieEntry(counterUmur[i].toFloat(), listUmur[i]))
        }

        val pieDataSet = PieDataSet(entries, "")
        pieDataSet.setColors(
            Color.parseColor("#4DD0E1"),
            Color.parseColor("#FFF176"),
            Color.parseColor("#FF8A65"),
            Color.rgb(255,223,0),
            Color.YELLOW,
            Color.RED)

        pieDataSet.valueFormatter = ChartValueCustomFormatter()
        pieDataSet.valueTextSize = 12f
        pieDataSet.valueTypeface = Typeface.DEFAULT_BOLD

        val data = PieData(pieDataSet)
        data.setDrawValues(true)
        pieChart.data = data
        pieChart.legend.orientation = Legend.LegendOrientation.VERTICAL
        pieChart.legend.isWordWrapEnabled = true
        pieChart.legend.textSize=14f
        pieChart.extraBottomOffset = 32f
        pieChart.description.text=""
        pieChart.isRotationEnabled=false




        //draw chart
        pieChart.invalidate()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Logout Admin")
            .setMessage("You Will Be Logged Out Of The Admin Section ?") // Specifying a listener allows you to take an action before dismissing the dialog.
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
}