package com.gracielo.projectta.ui.admin.pdfMembership

import android.content.Context
import android.graphics.*
import kotlin.Throws
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfPCell
import androidx.core.content.ContextCompat
import com.gracielo.projectta.R
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import com.itextpdf.text.*
import com.itextpdf.text.pdf.Barcode128
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.lang.NullPointerException

internal object PDFUtilityMembershipBar {
    private val TAG = PDFUtilityMembershipBar::class.java.simpleName
    private val FONT_TITLE: Font = Font(Font.FontFamily.TIMES_ROMAN, 18f, Font.BOLD)
    private val FONT_SUBTITLE: Font = Font(Font.FontFamily.TIMES_ROMAN, 14f, Font.NORMAL)
    private val FONT_SUBTITLE_DATE: Font = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.NORMAL)
    private val FONT_CELL: Font = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.NORMAL)
    private val FONT_COLUMN: Font = Font(Font.FontFamily.TIMES_ROMAN, 14f, Font.BOLD)
    private var reportDate:String = ""
    private var createdDate:String = ""
    private var image:ByteArray?=null

    @Throws(Exception::class)
    fun setReportDate(date:String){
        reportDate = date
    }

    @Throws(Exception::class)
    fun setImage(byteArray: ByteArray){
        image=byteArray
    }
    @Throws(Exception::class)
    fun setCreatedDate(date:String){
       createdDate = date
    }
    @Throws(Exception::class)
    fun createPdf(
        mContext: Context,
        mCallback: OnDocumentClose?,
        filePath: String,
        isPortrait: Boolean,
    ) {
        if (filePath == "") {
            throw NullPointerException("PDF File Name can't be null or blank. PDF File can't be created")
        }
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
            Thread.sleep(50)
        }
        val document = Document()
        document.setMargins(24f, 24f, 32f, 32f)
        document.pageSize = if (isPortrait) PageSize.A4 else PageSize.A4.rotate()
        val pdfWriter = PdfWriter.getInstance(document, FileOutputStream(filePath))
        pdfWriter.setFullCompression()
        pdfWriter.pageEvent = PageNumeration(createdDate)
        document.open()
        setMetaData(document)
        addHeader(mContext, document)
        addEmptyLine(document, 1)
        document.add(getImage(image!!,false))

        document.close()
        try {
            pdfWriter.close()
        } catch (ex: Exception) {
            Log.e(TAG, "Error While Closing pdfWriter : $ex")
        }
        mCallback?.onPDFDocumentClose(file)
    }

    @Throws(DocumentException::class)
    private fun addEmptyLine(document: Document, number: Int) {
        for (i in 0 until number) {
            document.add(Paragraph(" "))
        }
    }

    private fun setMetaData(document: Document) {
        document.addCreationDate()
        //document.add(new Meta("",""));
//        document.addAuthor("RAVEESH G S")
//        document.addCreator("RAVEESH G S")
//        document.addHeader("DEVELOPER", "RAVEESH G S")
        document.addAuthor("Gracielo Justine S")
        document.addCreator("Gracielo Justine S")
        document.addHeader("DEVELOPER", "Gracielo Justine S")
    }

    @Throws(Exception::class)
    private fun addHeader(mContext: Context, document: Document) {
        val table = PdfPTable(3)
        table.widthPercentage = 100f
        table.setWidths(floatArrayOf(2f, 7f, 2f))
        table.defaultCell.border = PdfPCell.NO_BORDER
        table.defaultCell.verticalAlignment = Element.ALIGN_CENTER
        table.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
        var cell: PdfPCell
        run {
            /*LEFT TOP LOGO*/
            val d = ContextCompat.getDrawable(mContext, R.drawable.logo_apps)
            val bmp = (d as BitmapDrawable?)!!.bitmap
            val stream = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val logo = Image.getInstance(stream.toByteArray())
            logo.widthPercentage = 80f
            logo.scaleToFit(120f, 75f)
            cell = PdfPCell(logo)
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.verticalAlignment = Element.ALIGN_MIDDLE
            cell.isUseAscender = true
            cell.border = PdfPCell.NO_BORDER
            cell.setPadding(2f)
            table.addCell(cell)
        }
        run {

            /*MIDDLE TEXT*/cell = PdfPCell()
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.border = PdfPCell.NO_BORDER
            cell.setPadding(8f)
            cell.isUseAscender = true
            var temp = Paragraph("Healthy Foods Application Report", FONT_TITLE)
            temp.alignment = Element.ALIGN_CENTER
            cell.addElement(temp)
            temp = Paragraph("Membership Subscription Report", FONT_SUBTITLE)
            temp.alignment = Element.ALIGN_CENTER
            cell.addElement(temp)
            temp = Paragraph(reportDate, FONT_SUBTITLE_DATE)
            temp.alignment = Element.ALIGN_CENTER
            cell.addElement(temp)
            table.addCell(cell)
        }
        /* RIGHT TOP LOGO*/run {

            cell = PdfPCell()
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.verticalAlignment = Element.ALIGN_MIDDLE
            cell.isUseAscender = true
            cell.border = PdfPCell.NO_BORDER
            cell.setPadding(2f)
            table.addCell(cell)
        }

        //Paragraph paragraph=new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN, 2.0f, Font.NORMAL, BaseColor.BLACK));
        //paragraph.add(table);
        //document.add(paragraph);
        document.add(table)
    }

    @Throws(DocumentException::class)
    private fun createDataTableImage(imgae:ByteArray): PdfPTable {
        val table1 = PdfPTable(1)
        table1.widthPercentage = 100f
        table1.setWidths(floatArrayOf(1f))
        table1.headerRows = 0
        table1.defaultCell.border = PdfPCell.NO_BORDER
        table1.defaultCell.verticalAlignment = Element.ALIGN_CENTER
        table1.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
        var cell: PdfPCell
        run {

        }
        return table1
    }
    @Throws(DocumentException::class)
    private fun createDataTable(dataTable: List<Array<String>>): PdfPTable {
        val table1 = PdfPTable(6)
        table1.widthPercentage = 100f
        table1.setWidths(floatArrayOf(1f,2f,3f,2f,1f,1f))
        table1.headerRows = 1
        var cell_color=  BaseColor.WHITE
        table1.defaultCell.verticalAlignment = Element.ALIGN_CENTER
        table1.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
        var cell: PdfPCell
        run {
            cell = PdfPCell()
            cell.image = getImage(image!!, false)
            cell.horizontalAlignment = Element.ALIGN_LEFT
            cell.verticalAlignment = Element.ALIGN_MIDDLE
            cell.paddingLeft = 2f
            cell.paddingRight = 2f
            cell.paddingTop = 2f
            cell.paddingBottom = 2f
            cell.backgroundColor = cell_color
            table1.addCell(cell)
        }
        return table1
    }

    @Throws(DocumentException::class)
    private fun createSignBox(): PdfPTable {
        val outerTable = PdfPTable(1)
        outerTable.widthPercentage = 100f
        outerTable.defaultCell.border = PdfPCell.NO_BORDER
        val innerTable = PdfPTable(2)
        run {
            innerTable.widthPercentage = 100f
            innerTable.setWidths(floatArrayOf(1f, 1f))
            innerTable.defaultCell.border = PdfPCell.NO_BORDER

            //ROW-1 : EMPTY SPACE
            var cell = PdfPCell()
            cell.border = PdfPCell.NO_BORDER
            cell.fixedHeight = 60f
            innerTable.addCell(cell)

            //ROW-2 : EMPTY SPACE
            cell = PdfPCell()
            cell.border = PdfPCell.NO_BORDER
            cell.fixedHeight = 60f
            innerTable.addCell(cell)

//            //ROW-3 : Content Left Aligned
//            cell = PdfPCell()
//            var temp = Paragraph(Phrase("Signature of Supervisor", FONT_SUBTITLE))
//            cell.addElement(temp)
//            temp = Paragraph(Phrase("( RAVEESH G S )", FONT_SUBTITLE))
//            temp.paddingTop = 4f
//            temp.alignment = Element.ALIGN_LEFT
//            cell.addElement(temp)
//            cell.horizontalAlignment = Element.ALIGN_LEFT
//            cell.border = PdfPCell.NO_BORDER
//            cell.setPadding(4f)
//            innerTable.addCell(cell)
//
//            //ROW-4 : Content Right Aligned
//            cell = PdfPCell(Phrase("Signature of Staff ", FONT_SUBTITLE))
//            cell.horizontalAlignment = Element.ALIGN_RIGHT
//            cell.border = PdfPCell.NO_BORDER
//            cell.setPadding(4f)
//            innerTable.addCell(cell)
        }
        val signRow = PdfPCell(innerTable)
        signRow.horizontalAlignment = Element.ALIGN_LEFT
        signRow.border = PdfPCell.NO_BORDER
        signRow.setPadding(4f)
        outerTable.addCell(signRow)
        return outerTable
    }

    @Throws(Exception::class)
    private fun getImage(imageByte: ByteArray, isTintingRequired: Boolean): Image {
        val paint = Paint()
        if (isTintingRequired) {
            paint.colorFilter = PorterDuffColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN)
        }
        val input = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.size)
        var output = Bitmap.createBitmap(input.width, input.height, Bitmap.Config.ARGB_8888)
//        output = Bitmap.createScaledBitmap(output,input.width/2,input.height/2,false)
        val canvas = Canvas(output)
        canvas.drawBitmap(input, 0f, 0f, paint)
        val stream = ByteArrayOutputStream()
        output.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val image = Image.getInstance(stream.toByteArray())
        image.widthPercentage = 80f
        return image
    }

    private fun getBarcodeImage(pdfWriter: PdfWriter, barcodeText: String): Image {
        val barcode = Barcode128()
        //barcode.setBaseline(-1); //BARCODE TEXT ABOVE
        barcode.font = null
        barcode.code = barcodeText
        barcode.codeType = Barcode128.CODE128
        barcode.textAlignment = Element.ALIGN_BASELINE
        return barcode.createImageWithBarcode(pdfWriter.directContent, BaseColor.BLACK, null)
    }

    interface OnDocumentClose {
        fun onPDFDocumentClose(file: File?)
    }
}