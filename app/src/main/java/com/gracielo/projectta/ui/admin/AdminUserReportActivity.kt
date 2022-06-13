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
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import com.gracielo.projectta.R
import com.gracielo.projectta.data.model.admin.allMembershipTransaction.DataAllMembership
import com.gracielo.projectta.data.model.admin.allUser.DataAllUser
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityAdminUserReportBinding
import com.gracielo.projectta.ui.admin.chart.ChartValueCustomFormatter
import com.gracielo.projectta.ui.admin.ingredients.AdminIngredientActivity
import com.gracielo.projectta.ui.admin.pdfUser.PDFUtilityUser
import com.gracielo.projectta.ui.admin.recipe.AdminRecipeActivity
import com.jakewharton.threetenabp.AndroidThreeTen
import de.codecrafters.tableview.model.TableColumnWeightModel
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.ByteArrayOutputStream
import java.io.File

class AdminUserReportActivity : AppCompatActivity(),PDFUtilityUser.OnDocumentClose{

    private lateinit var binding : ActivityAdminUserReportBinding
    private val apiServices = ApiServices()
    private lateinit var pieChart: PieChart
    private lateinit var barChart: BarChart
    private val dataAllUser = mutableListOf<DataAllUser>()
    var listUmur = mutableListOf<String>()
    var listLogin = mutableListOf<String>()
    var counterUmur = mutableListOf<Int>()
    var counterLogin = mutableListOf<Int>()
    var tipe=0

    val TABLE_HEADERS_HOME = arrayOf("Username","Name","Email","Email Verified","Expired","Last Login")
    lateinit var adapters:UserDetailReportAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminUserReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidThreeTen.init(this)

        binding.tableViewAdminUser.visibility=View.INVISIBLE
        binding.openUserMenu.setOnClickListener {
            showPopup(it)
        }

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
        barChart = binding.barChartUserReport
        barChart.visibility = View.INVISIBLE
        apiServices.getAllUsers {
            dataAllUser.addAll(it!!.dataAllUser)
            adapters = UserDetailReportAdapter(this,
                dataAllUser as ArrayList<DataAllUser?>
            )
            adapters.setDatas(dataAllUser)
            val headerAdapter = SimpleTableHeaderAdapter(this,*TABLE_HEADERS_HOME)
            headerAdapter.setTextSize(12)
            headerAdapter.setPaddings(10,20,10,20)
            binding.tableViewAdminUser.headerAdapter= headerAdapter

            binding.tableViewAdminUser.setDataAdapter(adapters)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if(dataAllUser.size==0){
                Log.d("data","DataKosong")
            }
            else{
                olahDataTransaksi(dataAllUser)
                olahDataTransaksiBarChart(dataAllUser)
            }
        },1000)

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
    fun olahDataTransaksiBarChart(dataUser:List<DataAllUser>){
        // --> Prepare Data
        listLogin.clear()
        counterLogin.clear()

        listLogin.add("Today")
        listLogin.add("Last 7 Days")
        listLogin.add("Last 30 Days")
        listLogin.add("Last 365 Days")


        for (i in listLogin.indices){
            counterLogin.add(0);
        }

        for (i in dataUser.indices){
            val text = dataUser[i].last_login
            val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val dateTimeUserLogin = LocalDateTime.parse(text, pattern)
            val dateTimeUserLoginString = dateTimeUserLogin.format(pattern)

            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val dateTimeNow = current.format(formatter)

            val tanggalStringUser = dateTimeUserLoginString.substring(0,10)
            val tanggalStringNow = dateTimeNow.substring(0,10)

            if(tanggalStringNow==tanggalStringUser)counterLogin[0]++
            if(dateTimeUserLogin.isAfter(current.minusDays(7)))counterLogin[1]++
            if(dateTimeUserLogin.isAfter(current.minusDays(30)))counterLogin[2]++
            if(dateTimeUserLogin.isAfter(current.minusDays(365)))counterLogin[3]++
        }
        // --> End Prepare Data

        // --> Prepare Piechart


        barChart.setXAxisRenderer(
            AdminHomeActivity.CustomXAxisRenderer(
                barChart.getViewPortHandler(),
                barChart.getXAxis(),
                barChart.getTransformer(YAxis.AxisDependency.LEFT)
            )
        )
        val entries: ArrayList<BarEntry> = ArrayList()
        for (i in listLogin.indices){
            entries.add(BarEntry(i.toFloat(), counterLogin[i].toFloat()))
        }

        val barDataSet = BarDataSet(entries, "")
        barDataSet.setColors(Color.parseColor("#4DD0E1"),
            Color.parseColor("#FFF176"),
            Color.parseColor("#FF8A65"),
            Color.rgb(255,223,0),
            Color.YELLOW,
            Color.RED)

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
    inner class MyAxisFormatter : IndexAxisValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val index = value.toInt()
            if (index < listLogin.size) {
                if(listLogin[index].length>10){
//                    val subawal = listPaket[index].substring(0,9)
//                    val subakhir = listPaket[index].substring(10,listPaket[index].length-1)
                    val splitString = listLogin[index].split(" ")
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
                    return listLogin[index]
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
    private fun showPopup(v: View) {
        val popup = PopupMenu(this, v)
        popup.inflate(R.menu.menu_admin_user)
        popup.setOnMenuItemClickListener(object: PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                return when (item?.itemId) {
                    R.id.item_user_export -> {
                        var bitmapbars= binding.barChartUserReport.drawToBitmap()
                        bitmapbars= Bitmap.createScaledBitmap(bitmapbars,500,673,false)
                        val stream = ByteArrayOutputStream()
                        bitmapbars.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        val imageBar = stream.toByteArray()

                        var bitmappie= binding.chartUserReport.drawToBitmap()
                        bitmappie= Bitmap.createScaledBitmap(bitmappie,500,673,false)
                        val stream1 = ByteArrayOutputStream()
                        bitmappie.compress(Bitmap.CompressFormat.PNG, 100, stream1)
                        val imagePie = stream1.toByteArray()

                        val current = LocalDateTime.now()
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        val formatted = current.format(formatter)

                        val pdfMaker = PDFUtilityUser
                        val path =
                            "${getExternalFilesDir(null)}${File.separator} user report.pdf"
                        try {
                            pdfMaker.setImageBar(imageBar)
                            pdfMaker.setImagePie(imagePie)
                            pdfMaker.setCreatedDate(formatted)
                            pdfMaker.createPdf(
                                v.context,
                                this@AdminUserReportActivity,
                                getData(dataAllUser),
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
                    R.id.item_change_chart -> {
                        if(tipe==0){
                            tipe=1
                            binding.reportUserTitle.text = "All Users Login Count Chart"
                            barChart.visibility = View.VISIBLE
                            pieChart.visibility = View.INVISIBLE
                            binding.tableViewAdminUser.visibility = View.INVISIBLE
                        }
                        else{
                            tipe=0
                            binding.reportUserTitle.text = "All Users Age Chart"
                            barChart.visibility = View.INVISIBLE
                            pieChart.visibility = View.VISIBLE
                            binding.tableViewAdminUser.visibility = View.INVISIBLE
                        }
                        true
                    }
                    R.id.item_user_detail -> {
                        binding.reportUserTitle.text = "All Users Detail Data"
                        barChart.visibility = View.INVISIBLE
                        pieChart.visibility = View.INVISIBLE
                        binding.tableViewAdminUser.visibility = View.VISIBLE
                        true
                    }
                    else -> false
                }
            }
        })
        popup.show()


//        PopupMenu(this, v).apply {
//            setOnMenuItemClickListener(object: PopupMenu.OnMenuItemClickListener {
//                override fun onMenuItemClick(item: MenuItem?): Boolean {
//                    return when (item?.itemId) {
//                        R.id.item_ingredients_recipe_export -> {
//                            val inflater = LayoutInflater.from(applicationContext)
//                            val view = inflater.inflate(R.layout.pdf_membership_report, null)
//                            val page: AbstractViewRenderer =
//                                object : AbstractViewRenderer(applicationContext, R.layout.pdf_membership_report) {
//                                    override fun initView(view: View) {
//                                        val barchart= view.findViewById<BarChart>(R.id.barChart_pdfMembership)
//                                        val textTanggal = view.findViewById<TextView>(R.id.reportMembershipSubscriptionDateReportMembership)
//                                        if(dataTransaksiMembershipFilterTanggal.size==0){
//                                            olahDataPDFTransaksi(dataTransaksiMembership,barchart)
//                                            textTanggal.text="All Time"
//                                            barchart.invalidate()
//                                        }
//                                        else{
//                                            olahDataPDFTransaksi(dataTransaksiMembershipFilterTanggal,barchart)
//                                            textTanggal.text = "$startDateString - $endDateString"
//                                            barchart.invalidate()
//                                        }
//                                        barchart.invalidate()
//                                    }
//
//                                }
//                            val page2: AbstractViewRenderer =
//                                object : AbstractViewRenderer(applicationContext, R.layout.pdf_membership_detail_report) {
//                                    override fun initView(view: View) {
//                                        val bindingReport = PdfMembershipDetailReportBinding.inflate(layoutInflater)
//                                        val TABLE_HEADERS = arrayOf("Transaction ID","Date","Users,","Package Name","Price","Status")
//                                        val textTanggal = view.findViewById<TextView>(R.id.reportMembershipSubscriptionDateReportMembership)
////                                        val tableDetail = bindingReport.tableViewMembershipReport
//
//                                        val tableDetail =  bindingReport.tableViewMembershipReport
//                                        val dataAdapter: MutableList<DataAllMembership>
//                                        lateinit var adapters:MembershipDetailReportAdapter
//                                        Log.d("CountTransaksi " , "Size = ${dataTransaksiMembershipFilterTanggal.size} atau ${dataTransaksiMembership.size}")
//                                        if(dataTransaksiMembershipFilterTanggal.size==0){
//                                            dataAdapter = dataTransaksiMembership
//                                            textTanggal.text="All Time"
//                                        }
//                                        else{
//                                            dataAdapter = dataTransaksiMembershipFilterTanggal
//                                            textTanggal.text = "$startDateString - $endDateString"
//                                        }
//                                        adapters = MembershipDetailReportAdapter(this@AdminHomeActivity,
//                                            dataAdapter as ArrayList<DataAllMembership?>
//                                        )
//                                        adapters.setDatas(dataAdapter)
//                                        tableDetail.headerAdapter= SimpleTableHeaderAdapter(this@AdminHomeActivity,*TABLE_HEADERS)
//                                        tableDetail.setDataAdapter(adapters)
//                                        tableDetail.invalidate()
//                                    }
//                                }
//
//                            page.isReuseBitmap = true
//                            page2.isReuseBitmap=true
//                            val doc= PdfDocument(applicationContext)
//                            doc.addPage(page)
//                            doc.addPage(page2)
//                            doc.setRenderWidth(1500)
//                            doc.setRenderHeight(2115)
//                            doc.setOrientation(PdfDocument.A4_MODE.PORTRAIT)
//                            doc.fileName = "PDFMembershipReport.pdf"
//                            doc.setSaveDirectory(applicationContext.getExternalFilesDir(null))
//                            doc.setInflateOnMainThread(false)
//                            doc.setListener(object : PdfDocument.Callback {
//                                override fun onComplete(file: File) {
//                                    Log.i(PdfDocument.TAG_PDF_MY_XML, "Complete")
//                                    renderPdf(this@AdminHomeActivity,file)
//                                }
//
//                                override fun onError(e: java.lang.Exception) {
//                                    Log.i(PdfDocument.TAG_PDF_MY_XML, "Error")
//                                }
//                            })
//
//                            doc.createPdf(this@AdminHomeActivity)
//
//                            true
//                        }
//                        R.id.item_membership_edit -> {
////                            dosomething()
//                            true
//                        }
//                        R.id.item_membership_detail -> {
//                            if(tipe==0){
//                                tipe=1
//                                val item:MenuItem = menu.findItem(R.id.item_membership_detail)
//                                item.title = "Transaction Chart"
//                                binding.barChartView.visibility = View.INVISIBLE
//                                binding.tableViewAdminHome.visibility = View.VISIBLE
//                            }
//                            else{
//                                tipe=0
//                                val item:MenuItem = menu.findItem(R.id.item_membership_detail)
//                                item.title = "Transaction Detail"
//                                binding.barChartView.visibility = View.VISIBLE
//                                binding.tableViewAdminHome.visibility = View.INVISIBLE
//                            }
//
//                            true
//                        }
//                        else -> false
//                    }
//                }
//            })
//            inflate(R.menu.menu_admin_membership)
//            show()
//        }
    }

    private fun getData(dataTransaksi : MutableList<DataAllUser>): List<Array<String>> {
        var count = dataTransaksi.size

        val temp: MutableList<Array<String>> = java.util.ArrayList()
        for (i in 0 until count) {
            val data = dataTransaksi[i]
            temp.add(arrayOf(data.username,data.nama, data.email,checkstatus(data.emailVerified),data.expired,data.last_login))
        }
        return temp

    }

    override fun onPDFDocumentClose(file: File?) {
        Toast.makeText(this,"Pdf Created",Toast.LENGTH_SHORT).show()
        renderPdf(this,file!!)
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

    private fun checkstatus(status:String):String{
        var hasilreturn=""
        when (status) {
            "0" -> {hasilreturn="Not Verified"}
            "1" -> {hasilreturn="Verified"}
        }
        return hasilreturn
    }
}