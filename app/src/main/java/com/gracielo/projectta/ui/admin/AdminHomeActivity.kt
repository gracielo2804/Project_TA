package com.gracielo.projectta.ui.admin

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
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
import com.gracielo.projectta.ui.admin.membership.EditMembershipActivity
import com.gracielo.projectta.ui.admin.recipe.AdminRecipeActivity
import com.gracielo.projectta.ui.admin.pdfMembership.PDFUtilityMembershipBar
import com.gracielo.projectta.ui.admin.pdfMembership.PDFUtilityMembershipDetail
import com.jakewharton.threetenabp.AndroidThreeTen
import de.codecrafters.tableview.model.TableColumnWeightModel
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter
import org.apache.pdfbox.io.MemoryUsageSetting
import org.apache.pdfbox.multipdf.PDFMergerUtility
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class AdminHomeActivity : AppCompatActivity(), PDFUtilityMembershipDetail.OnDocumentClose,PDFUtilityMembershipBar.OnDocumentClose {

    private lateinit var binding :ActivityAdminHomeBinding
    private val apiServices = ApiServices()
//    private lateinit var barChart:HorizontalBarChart
    private lateinit var barChart:BarChart
    private val dataTransaksiMembership = mutableListOf<DataAllMembership>()
    private val dataTransaksiMembershipFilterTanggal = mutableListOf<DataAllMembership>()
    var listPaket = mutableListOf<String>()
    var counterPackage = mutableListOf<Int>()
    var jumlahpendapatan = 0
    var startDateString = ""
    var endDateString = ""
    var tipe=0

    private var startDate: Calendar? = null
    private var endDate: Calendar? = null
    private val nowDate = Calendar.getInstance()

    val TABLE_HEADERS_HOME = arrayOf("ID","Date","Users","Plan","Price","Status")
    var dataAdapter = mutableListOf<DataAllMembership>()
    lateinit var adapters:MembershipDetailReportAdapter

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
        val tableDetailHome =  binding.tableViewAdminHome
        tableDetailHome.visibility = View.INVISIBLE


        binding.openMembershipMenu.setOnClickListener {
            showPopup(it)
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
                 startDateString = df.format(Date(it.first))
                 endDateString = df.format(Date(it.second))
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
                jumlahpendapatan = hitungPendapatan(dataTransaksiMembershipFilterTanggal)
                if(dataTransaksiMembershipFilterTanggal.size==0){
                    dataAdapter = dataTransaksiMembership
                }
                else{
                    dataAdapter = dataTransaksiMembershipFilterTanggal
                }
                adapters = MembershipDetailReportAdapter(this@AdminHomeActivity,
                    dataAdapter as ArrayList<DataAllMembership?>
                )
                adapters.setDatas(dataAdapter)
                tableDetailHome.headerAdapter= SimpleTableHeaderAdapter(this@AdminHomeActivity,*TABLE_HEADERS_HOME)
                val columnModel = TableColumnWeightModel(6)
                tableDetailHome.setDataAdapter(adapters)
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
                    jumlahpendapatan = hitungPendapatan(dataTransaksiMembership)
                    if(dataTransaksiMembershipFilterTanggal.size==0){
                        dataAdapter = dataTransaksiMembership
                    }
                    else{
                        dataAdapter = dataTransaksiMembershipFilterTanggal
                    }
                    adapters = MembershipDetailReportAdapter(this@AdminHomeActivity,
                        dataAdapter as ArrayList<DataAllMembership?>
                    )
                    adapters.setDatas(dataAdapter)
                    tableDetailHome.headerAdapter= SimpleTableHeaderAdapter(this@AdminHomeActivity,*TABLE_HEADERS_HOME)
                    val columnModel = TableColumnWeightModel(6)
                    tableDetailHome.setDataAdapter(adapters)
                }
            },1000)
        }
        else {
            olahDataTransaksi(dataTransaksiMembership)
            jumlahpendapatan = hitungPendapatan(dataTransaksiMembership)
            if(dataTransaksiMembershipFilterTanggal.size==0){
                dataAdapter = dataTransaksiMembership
            }
            else{
                dataAdapter = dataTransaksiMembershipFilterTanggal
            }
            adapters = MembershipDetailReportAdapter(this@AdminHomeActivity,
                dataAdapter as ArrayList<DataAllMembership?>
            )
            adapters.setDatas(dataAdapter)
            tableDetailHome.headerAdapter= SimpleTableHeaderAdapter(this@AdminHomeActivity,*TABLE_HEADERS_HOME)
            val columnModel = TableColumnWeightModel(6)
            tableDetailHome.setDataAdapter(adapters)
        }


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

    fun hitungPendapatan(listTransaksi:List<DataAllMembership>) : Int{
        var total=0
        for (i in listTransaksi.indices){
            if(listTransaksi[i].statusTrans=="1"){
                total+=listTransaksi[i].nominal.toInt()
            }
        }
        return total
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

    private fun showPopup(v: View) {
        val popup = PopupMenu(this, v)
        popup.inflate(R.menu.menu_admin_membership)
        val menu = popup.menu
        popup.setOnMenuItemClickListener(object: PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                return when (item?.itemId) {
                    R.id.item_membership_export -> {

                        var bitmapbars= binding.barChartView.drawToBitmap()
                        Log.d("Bitmap Size : ","${bitmapbars.width/2} - ${bitmapbars.height/2}")
                        bitmapbars= Bitmap.createScaledBitmap(bitmapbars,500,673,false)
                        binding.imageviewadminshow.setImageBitmap(bitmapbars)
                        val stream = ByteArrayOutputStream()
                        bitmapbars.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        val image = stream.toByteArray()

//                        val bitmapBar = loadBitmapFromView(binding.barChartView)
//                        val baos = ByteArrayOutputStream()
//                        bitmapBar?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//                        val imageInByte: ByteArray = baos.toByteArray()

                        val pdfMaker = PDFUtilityMembershipBar
                        pdfMaker.setImage(image)
                        binding.pbarAdminHome.visibility = View.VISIBLE
                        var tanggal =""
                        val current = LocalDateTime.now()
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        val formatted = current.format(formatter)
                        println("Current Date and Time is: $formatted")
                        if(dataTransaksiMembershipFilterTanggal.size==0){
                            tanggal ="All Time"
                        }
                        else{
                            tanggal = "$startDateString - $endDateString"
                        }
                        val path =
                            "${getExternalFilesDir(null)}${File.separator} membership report.pdf"
                        try {
                            pdfMaker.setReportDate(tanggal)
                            pdfMaker.setCreatedDate(formatted)
                            pdfMaker.createPdf(
                                v.context,
                                this@AdminHomeActivity,
                                path,
                                true
                            )

                        } catch (e: Exception) {
                            binding.pbarAdminHome.visibility = View.INVISIBLE
                            e.printStackTrace()
                            Log.e("PDFNEW", "Error Creating Pdf")
                            Toast.makeText(v.context, "Error Creating Pdf", Toast.LENGTH_SHORT)
                                .show()
                        }

                        val pdfMaker1 = PDFUtilityMembershipDetail
                        binding.pbarAdminHome.visibility = View.VISIBLE
                        var dataMembership = mutableListOf<DataAllMembership>()
                        println("Current Date and Time is: $formatted")
                        if(dataTransaksiMembershipFilterTanggal.size==0){
                            dataMembership.addAll(dataTransaksiMembership)
                            tanggal ="All Time"
                        }
                        else{
                            dataMembership.addAll(dataTransaksiMembershipFilterTanggal)
                            tanggal = "$startDateString - $endDateString"
                        }
                        val path1 =
                            "${getExternalFilesDir(null)}${File.separator} newExport.pdf"
                        try {
                            pdfMaker1.setReportDate(tanggal)
                            pdfMaker1.setCreatedDate(formatted)
                            pdfMaker1.createPdf(
                                v.context,
                                this@AdminHomeActivity,
                                getData(dataMembership),
                                path1,
                                true
                            )
                        } catch (e: Exception) {
                            binding.pbarAdminHome.visibility = View.INVISIBLE
                            e.printStackTrace()
                            Log.e("PDFNEW", "Error Creating Pdf")
                            Toast.makeText(v.context, "Error Creating Pdf", Toast.LENGTH_SHORT)
                                .show()
                        }
                        var mergers = PDFMergerUtility()
                        val paths1 = "${getExternalFilesDir(null)}${File.separator} PDFMembershipReport.pdf"
                        val paths2 = "${getExternalFilesDir(null)}${File.separator} newExport.pdf"
                        mergers.addSource(paths1)
                        mergers.addSource(paths2)
                        val p = "${getExternalFilesDir(null)}${File.separator} merged.pdf"

                        val fileOutputStream = FileOutputStream(p)
                        try {
                            mergers.destinationStream = fileOutputStream
                            mergers.mergeDocuments(MemoryUsageSetting.setupTempFileOnly())
                        } finally {
                            fileOutputStream.close()
                        }

                        true
                    }
                    R.id.item_membership_edit -> {
                        val intentt = Intent(this@AdminHomeActivity,EditMembershipActivity::class.java)
                        startActivity(intentt)
                        true
                    }
                    R.id.item_membership_new_export -> {
//                        val pdfMaker = PDFUtilityMembershipDetail
//                        binding.pbarAdminHome.visibility = View.VISIBLE
//                        var dataMembership = mutableListOf<DataAllMembership>()
//                        var tanggal =""
//                        val current = LocalDateTime.now()
//                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
//                        val formatted = current.format(formatter)
//                        println("Current Date and Time is: $formatted")
//                        if(dataTransaksiMembershipFilterTanggal.size==0){
//                            dataMembership.addAll(dataTransaksiMembership)
//                            tanggal ="All Time"
//                        }
//                        else{
//                            dataMembership.addAll(dataTransaksiMembershipFilterTanggal)
//                            tanggal = "$startDateString - $endDateString"
//                        }
//                        val path =
//                        "${getExternalFilesDir(null)}${File.separator} newExport.pdf"
//                        try {
//                            pdfMaker.setReportDate(tanggal)
//                            pdfMaker.setCreatedDate(formatted)
//                            pdfMaker.createPdf(
//                                v.context,
//                                this@AdminHomeActivity,
//                                getData(dataMembership),
//                                path,
//                                true
//                            )
////                            var mergers = PDFMergerUtility()
////                            val paths1 = "${getExternalFilesDir(null)}${File.separator} PDFMembershipReport.pdf"
////                            val paths2 = "${getExternalFilesDir(null)}${File.separator} newExport.pdf"
////                            mergers.addSource(paths1)
////                            mergers.addSource(paths2)
////                            val p = "${getExternalFilesDir(null)}${File.separator} merged.pdf"
////
////                            val fileOutputStream = FileOutputStream(p)
////                            try {
////                                mergers.destinationStream = fileOutputStream
////                                mergers.mergeDocuments(MemoryUsageSetting.setupTempFileOnly())
////                            } finally {
////                                fileOutputStream.close()
////                            }
//
//                        } catch (e: Exception) {
//                            binding.pbarAdminHome.visibility = View.INVISIBLE
//                            e.printStackTrace()
//                            Log.e("PDFNEW", "Error Creating Pdf")
//                            Toast.makeText(v.context, "Error Creating Pdf", Toast.LENGTH_SHORT)
//                                .show()
//                        }



                        return true
                    }
                    R.id.item_membership_detail -> {
                        if(tipe==0){
                            tipe=1
                            val item:MenuItem = menu.findItem(R.id.item_membership_detail)
                            item.title = "Transaction Chart"
                            binding.barChartView.visibility = View.INVISIBLE
                            binding.tableViewAdminHome.visibility = View.VISIBLE
                        }
                        else{
                            tipe=0
                            val item:MenuItem = menu.findItem(R.id.item_membership_detail)
                            item.title = "Transaction Detail"
                            binding.barChartView.visibility = View.VISIBLE
                            binding.tableViewAdminHome.visibility = View.INVISIBLE
                        }
                        true
                    }
                    else -> false
                }
            }
        })
        popup.show()

    }
    fun olahDataPDFTransaksi(dataTransaksi:List<DataAllMembership>, barChart: BarChart){
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
//        barChart.animateY(3000)

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
        binding.pbarAdminHome.visibility = View.INVISIBLE
        Toast.makeText(this,"Pdf Created",Toast.LENGTH_SHORT).show()
//        renderPdf(this@AdminHomeActivity,file!!)
    }

    private fun getData(dataTransaksi : MutableList<DataAllMembership>): List<Array<String>> {
        var count = dataTransaksi.size
       
        val temp: MutableList<Array<String>> = ArrayList()
        for (i in 0 until count) {
            val data = dataTransaksi[i]
            temp.add(arrayOf(data.idTrans,data.tanggal, data.namaCust,data.namaPaket,data.nominal,checkstatus(data.statusTrans)))
        }
        return temp

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