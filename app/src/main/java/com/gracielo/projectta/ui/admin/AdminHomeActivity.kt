package com.gracielo.projectta.ui.admin

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import com.google.android.material.datepicker.MaterialDatePicker
import com.gracielo.projectta.R
import com.gracielo.projectta.data.model.admin.allMembershipTransaction.DataAllMembership
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityAdminHomeBinding
import com.gracielo.projectta.ui.admin.chart.ChartValueCustomFormatter
import com.gracielo.projectta.ui.admin.ingredients.AdminIngredientActivity
import com.gracielo.projectta.ui.admin.recipe.AdminRecipeActivity
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import com.jakewharton.threetenabp.AndroidThreeTen
import org.threeten.bp.format.DateTimeFormatter
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class AdminHomeActivity : AppCompatActivity() {

    private lateinit var binding :ActivityAdminHomeBinding
    private val apiServices = ApiServices()
//    private lateinit var barChart:HorizontalBarChart
    private lateinit var barChart:BarChart
    private val dataTransaksiMembership = mutableListOf<DataAllMembership>()
    private val dataTransaksiMembershipFilterTanggal = mutableListOf<DataAllMembership>()
    var listPaket = mutableListOf<String>()
    var counterPackage = mutableListOf<Int>()

    private var startDate: Calendar? = null
    private var endDate: Calendar? = null
    private val nowDate = Calendar.getInstance()

    val calendarId=0

    companion object{
        const val DATE_PICKER_START = 0
        const val DATE_PICKER_END = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidThreeTen.init(this)

//        binding.btnTestPDF.setOnClickListener {
//            //create object of Document class
//            val mDoc = Document()
//            //pdf file name
//            val mFileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
//            //pdf file path
//            val filePath= "${getExternalFilesDir(null)}/ $mFileName.pdf"
//            val mFilePath = Environment.getExternalStorageDirectory().toString() + "/" + mFileName +".pdf"
//            try {
//                //create instance of PdfWriter class
//                PdfWriter.getInstance(mDoc, FileOutputStream(filePath))
//
//                //open the document for writing
//                mDoc.open()
//
//                //get text from EditText i.e. textEt
//                val mText = "abc123"
//
//                //add author of the document (metadata)
//                mDoc.addAuthor("Gracielo Justine")
//
//                //add paragraph to the document
//                mDoc.add(Paragraph(mText))
//
//                //close document
//                mDoc.close()
//
//                //show file saved message with file name and path
//                Toast.makeText(this, "$mFileName.pdf\nis saved to\n$filePath", Toast.LENGTH_SHORT).show()
//            }
//            catch (e: Exception){
//                //if anything goes wrong causing exception, get and show exception message
//                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
//            }
//        }

        binding.btnTestPDF.setOnClickListener {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            page.canvas.drawText("asdf",0f,0f,Paint())
            pdfDocument.finishPage(page)
            val filePath = File(getExternalFilesDir(null), "bitmapPdf.pdf")
            pdfDocument.writeTo(FileOutputStream(filePath))
            pdfDocument.close()
        }

        val bottomNavBar = binding.adminBottomMenu
        bottomNavBar.selectedItemId = R.id.adminSubscription
        bottomNavBar.setOnItemSelectedListener {
            when(it.itemId){
                R.id.adminSubscription->{
                    //Do Nothing

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
                    intent = Intent(this, AdminRecipeActivity::class.java)
                    finish()
                    startActivity(intent)
                }

            }
            true
        }

        startDate = Calendar.getInstance()
        endDate = Calendar.getInstance()
        val year: Int = nowDate.get(Calendar.YEAR)
        val month: Int = nowDate.get(Calendar.MONTH)
        val day: Int = nowDate.get(Calendar.DAY_OF_MONTH)

        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
        val now = Calendar.getInstance()
        dateRangePicker.setSelection(androidx.core.util.Pair(nowDate.timeInMillis, nowDate.timeInMillis))

        binding.btnSelectDateMembershipSubscriptionReport.setOnClickListener {
            dataTransaksiMembershipFilterTanggal.clear()
            val picker = dateRangePicker.build()
            picker.show(this@AdminHomeActivity.supportFragmentManager, picker.toString())
            picker.addOnNegativeButtonClickListener {dataTransaksiMembershipFilterTanggal.addAll(dataTransaksiMembership) }
            picker.addOnPositiveButtonClickListener {
                val myFormatString = "yyyy-MM-dd"; // for example

                val formatter = DateTimeFormatter.ofPattern(myFormatString)
                val df = SimpleDateFormat("yyyy-MM-dd")
                val startDateString = df.format(Date(it.first))
                val endDateString = df.format(Date(it.second))
                val startDate =  df.parse(startDateString)
                val endDate =  df.parse(endDateString)
                binding.reportMembershipSubscriptionDate.text = "$startDateString - $endDateString"
                for (i in dataTransaksiMembership.indices){
                    val tanggalData = df.parse(dataTransaksiMembership[i].tanggal)
                    Log.d("TanggalTrans","${tanggalData} - Start : $startDate - End : $endDate ")
                    if((tanggalData.after(startDate)||tanggalData.equals(startDate))
                        && (tanggalData.before(endDate)|| tanggalData.equals(endDate))){
                        Log.d("TanggalTrans","Masuk Filter")
                        dataTransaksiMembershipFilterTanggal.add(dataTransaksiMembership[i])
                    }
                }
                olahDataTransaksi(dataTransaksiMembershipFilterTanggal)
                Log.d("DataTrans",dataTransaksiMembershipFilterTanggal.size.toString())
            }
        }


//        barChart=binding.horizontalbarChartView
        barChart=binding.barChartView

        apiServices.getAllMembershipTransaction {
            dataTransaksiMembership.addAll(it!!.dataAllMembership)
        }
        if(dataTransaksiMembership.size==0){
            Handler(Looper.getMainLooper()).postDelayed({
                if(dataTransaksiMembership.size==0){
                    Log.d("data","DataKosong")
                }
                else{
                    olahDataTransaksi(dataTransaksiMembership)
                }
            },1000)
        }
        else olahDataTransaksi(dataTransaksiMembership)
    }

    fun olahDataTransaksi(dataTransaksi:List<DataAllMembership>){
        // --> Prepare Data
        listPaket.clear()
        counterPackage.clear()

        listPaket.add("Basic Plan")
        listPaket.add("Vegetarian Plan")
        listPaket.add("Low Carb Plan")
        listPaket.add("All in One Plan")
        listPaket.add("Pending")
        listPaket.add("Failed")

        for (i in listPaket.indices){
            counterPackage.add(0);
        }

        for (i in dataTransaksi.indices){
            if(dataTransaksi[i].statusTrans=="0")counterPackage[5]++
            else if(dataTransaksi[i].statusTrans=="2")counterPackage[4]++
            else{
                if(dataTransaksi[i].idPaket=="1"){counterPackage[0]++}
                else if(dataTransaksi[i].idPaket=="2"){counterPackage[1]++}
                else if(dataTransaksi[i].idPaket=="3"){counterPackage[2]++}
                else if(dataTransaksi[i].idPaket=="4"){counterPackage[3]++}
            }
        }
        // --> End Prepare Data

        // --> Prepare Barchart
        barChart.setXAxisRenderer(
            CustomXAxisRenderer(
                barChart.getViewPortHandler(),
                barChart.getXAxis(),
                barChart.getTransformer(YAxis.AxisDependency.LEFT)
            )
        )
        val entries: ArrayList<BarEntry> = ArrayList()
        for (i in listPaket.indices){
            entries.add(BarEntry(i.toFloat(), counterPackage[i].toFloat()))
        }

        val barDataSet = BarDataSet(entries, "")
        barDataSet.setColors(Color.LTGRAY,Color.GREEN,Color.rgb(160,82,45),Color.rgb(255,223,0),Color.YELLOW,Color.RED)

        barDataSet.valueFormatter = ChartValueCustomFormatter()
        barDataSet.valueTextSize = 12f
        barDataSet.valueTypeface = Typeface.DEFAULT_BOLD

        val data = BarData(barDataSet)
        barChart.data = data


        //hide grid lines
        barChart.axisLeft.setDrawGridLines(false)
        val xAxis=barChart.xAxis
        barChart.extraBottomOffset = 32f
        barChart.xAxis.setDrawGridLines(false)
        barChart.xAxis.setDrawAxisLine(false)

        //remove right y-axis
        barChart.axisRight.isEnabled = false

        //remove legend
        barChart.legend.isEnabled = false

        barChart.setDrawValueAboveBar(true)


        //remove description label
        barChart.description.isEnabled = true
        barChart.description.setPosition(0f,0f)


        //add animation
        barChart.animateY(3000)

        // to draw label on xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.xOffset=50f
        xAxis.valueFormatter = MyAxisFormatter()
        xAxis.setDrawLabels(true)


        xAxis.granularity = 1f
        xAxis.textSize = 12f



        //draw chart
        barChart.invalidate()
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

    inner class MyAxisFormatter : IndexAxisValueFormatter() {

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val index = value.toInt()
             if (index < listPaket.size) {
                if(listPaket[index].length>10){
//                    val subawal = listPaket[index].substring(0,9)
//                    val subakhir = listPaket[index].substring(10,listPaket[index].length-1)
                    val splitString = listPaket[index].split(" ")
                    var stringTampil = ""
                    for (i in splitString.indices){
                        if(i==0){
                            stringTampil+="${splitString[0]}\n"
                        }
                        else stringTampil+="${splitString[i]} "
                    }
                    return stringTampil
                }
                 else {
                    return listPaket[index]
                 }


            }
             else {
                return   ""
            }
        }
    }
    class CustomXAxisRenderer(
        viewPortHandler: ViewPortHandler?,
        xAxis: XAxis?,
        trans: Transformer?
    ) :
        XAxisRenderer(viewPortHandler, xAxis, trans) {
        override fun drawLabel(
            c: Canvas?,
            formattedLabel: String,
            x: Float,
            y: Float,
            anchor: MPPointF?,
            angleDegrees: Float
        ) {
            val line = formattedLabel.split("\n").toTypedArray()
            Utils.drawXAxisValue(c, line[0], x, y, mAxisLabelPaint, anchor, angleDegrees)
            if(line.size==2){
                Utils.drawXAxisValue(
                    c,
                    line[1],
                    x,
                    y + mAxisLabelPaint.textSize,
                    mAxisLabelPaint,
                    anchor,
                    angleDegrees
                )
            }

        }
    }
    fun isDateAfter(startDate:String,endDate:String):Boolean
    {
        try
        {
            val myFormatString = "yyyy-MM-dd"; // for example
            val df = SimpleDateFormat(myFormatString);
            val date1 = df.parse(endDate)
            val startingDate = df.parse(startDate)

            return date1.after(startingDate);
        }
        catch (e:Exception)
        {
            return false
        }
    }
}