package com.example.seka.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.seka.data.local.entity.KeuanganItem
import com.example.seka.data.local.entity.TabunganItem
import com.example.seka.data.local.entity.TransactionType
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object PDFGenerator {
    private val TITLE_FONT = Font(Font.FontFamily.HELVETICA, 18f, Font.BOLD)
    private val HEADER_FONT = Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD)
    private val NORMAL_FONT = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL)

    fun createTabunganPDF(context: Context, tabunganItems: List<TabunganItem>) {
        val dateFormatter = SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.getDefault())
        val fileName = "Tabungan_${dateFormatter.format(Date())}.pdf"
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        val document = Document(PageSize.A4)
        PdfWriter.getInstance(document, FileOutputStream(filePath))
        document.open()

        // Title
        val title = Paragraph("Laporan Tabungan", TITLE_FONT)
        title.alignment = Element.ALIGN_CENTER
        title.spacingAfter = 20f
        document.add(title)

        // Date
        val dateParagraph = Paragraph("Tanggal: ${SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())}", NORMAL_FONT)
        dateParagraph.alignment = Element.ALIGN_RIGHT
        dateParagraph.spacingAfter = 20f
        document.add(dateParagraph)

        // Table
        val table = PdfPTable(5)
        table.widthPercentage = 100f

        // Setting column widths
        table.setWidths(floatArrayOf(1f, 3f, 2f, 2f, 2f))

        // Add header cells
        addCell(table, "No.", HEADER_FONT)
        addCell(table, "Nama Barang", HEADER_FONT)
        addCell(table, "Target (Rp)", HEADER_FONT)
        addCell(table, "Terkumpul (Rp)", HEADER_FONT)
        addCell(table, "Progress", HEADER_FONT)

        // Add data rows
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

        for ((index, tabungan) in tabunganItems.withIndex()) {
            addCell(table, (index + 1).toString(), NORMAL_FONT)
            addCell(table, tabungan.nama, NORMAL_FONT)
            addCell(table, formatCurrency(tabungan.hargaTarget), NORMAL_FONT)
            addCell(table, formatCurrency(tabungan.tabunganTerkumpul), NORMAL_FONT)

            val progressPercentage = if (tabungan.hargaTarget > 0) {
                (tabungan.tabunganTerkumpul / tabungan.hargaTarget * 100).toInt().toString() + "%"
            } else {
                "0%"
            }
            addCell(table, progressPercentage, NORMAL_FONT)
        }

        document.add(table)

        // Summary
        document.add(Paragraph("\n"))

        val totalTarget = tabunganItems.sumOf { it.hargaTarget }
        val totalTerkumpul = tabunganItems.sumOf { it.tabunganTerkumpul }
        val totalPercentage = if (totalTarget > 0) {
            (totalTerkumpul / totalTarget * 100).toInt().toString() + "%"
        } else {
            "0%"
        }

        val summary = Paragraph("Total Target: ${formatCurrency(totalTarget)}\n" +
                "Total Terkumpul: ${formatCurrency(totalTerkumpul)}\n" +
                "Progress Keseluruhan: $totalPercentage", HEADER_FONT)
        summary.alignment = Element.ALIGN_LEFT
        document.add(summary)

        document.close()

        // Share the PDF
        sharePDF(context, filePath)
    }

    fun createKeuanganPDF(context: Context, keuanganItems: List<KeuanganItem>,
                          totalIncome: Double, totalExpense: Double) {
        val dateFormatter = SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.getDefault())
        val fileName = "Keuangan_${dateFormatter.format(Date())}.pdf"
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        val document = Document(PageSize.A4)
        PdfWriter.getInstance(document, FileOutputStream(filePath))
        document.open()

        // Title
        val title = Paragraph("Laporan Keuangan", TITLE_FONT)
        title.alignment = Element.ALIGN_CENTER
        title.spacingAfter = 20f
        document.add(title)

        // Date
        val dateParagraph = Paragraph("Tanggal: ${SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())}", NORMAL_FONT)
        dateParagraph.alignment = Element.ALIGN_RIGHT
        dateParagraph.spacingAfter = 20f
        document.add(dateParagraph)

        // Table
        val table = PdfPTable(6)
        table.widthPercentage = 100f

        // Setting column widths
        table.setWidths(floatArrayOf(1f, 2f, 3f, 2f, 2f, 2f))

        // Add header cells
        addCell(table, "No.", HEADER_FONT)
        addCell(table, "Tanggal", HEADER_FONT)
        addCell(table, "Keterangan", HEADER_FONT)
        addCell(table, "Kategori", HEADER_FONT)
        addCell(table, "Pemasukan", HEADER_FONT)
        addCell(table, "Pengeluaran", HEADER_FONT)

        // Add data rows
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        for ((index, item) in keuanganItems.withIndex()) {
            addCell(table, (index + 1).toString(), NORMAL_FONT)
            addCell(table, dateFormat.format(item.tanggal), NORMAL_FONT)
            addCell(table, "${item.judul}\n${item.deskripsi}", NORMAL_FONT)
            addCell(table, item.kategori, NORMAL_FONT)

            if (item.tipe == TransactionType.INCOME) {
                addCell(table, formatCurrency(item.jumlah), NORMAL_FONT)
                addCell(table, "", NORMAL_FONT)
            } else {
                addCell(table, "", NORMAL_FONT)
                addCell(table, formatCurrency(item.jumlah), NORMAL_FONT)
            }
        }

        document.add(table)

        // Summary
        document.add(Paragraph("\n"))

        val balance = totalIncome - totalExpense

        val summary = Paragraph("Total Pemasukan: ${formatCurrency(totalIncome)}\n" +
                "Total Pengeluaran: ${formatCurrency(totalExpense)}\n" +
                "Saldo: ${formatCurrency(balance)}", HEADER_FONT)
        summary.alignment = Element.ALIGN_LEFT
        document.add(summary)

        document.close()

        // Share the PDF
        sharePDF(context, filePath)
    }

    private fun addCell(table: PdfPTable, text: String, font: Font) {
        val cell = PdfPCell(Phrase(text, font))
        cell.horizontalAlignment = Element.ALIGN_CENTER
        cell.verticalAlignment = Element.ALIGN_MIDDLE
        cell.paddingTop = 5f
        cell.paddingBottom = 5f
        table.addCell(cell)
    }

    private fun formatCurrency(amount: Double): String {
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return currencyFormat.format(amount)
    }

    private fun sharePDF(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Handle exception if no PDF reader is available
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "application/pdf"
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(Intent.createChooser(
                shareIntent, "Bagikan PDF menggunakan"
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }
}