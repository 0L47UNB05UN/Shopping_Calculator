package com.example.shoppingcalculator.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.os.Environment
import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingcalculator.R
import com.example.shoppingcalculator.model.ShoppingItem
import com.example.shoppingcalculator.model.ShoppingList
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ShoppingViewModel(application: Application) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    val context = application.applicationContext!!

    val priceSym = listOf<String>(
        context.getString(R.string.ngn),
        context.getString(R.string.gbp),
        context.getString(R.string.usd),
        context.getString(R.string.ghs),
        context.getString(R.string.jpy),
        context.getString(R.string.inr)
    )
    val currentList = mutableStateOf( ShoppingList( 0, context.getString(R.string.new_list)) )
    val savedLists = mutableStateListOf<String>()
    val sld = mutableStateOf(false)
    val rfd = mutableStateOf(false)
    val listState = LazyListState()
    var appSettings = mutableStateOf<Map<String, Any>>( emptyMap() )

    init {
        loadSavedLists()
        loadSettingsOnce()
    }

    fun loadSettingsOnce(){
        appSettings.value = loadSettings()
    }

    fun addItem(name: String, price: Float, quantity: Int) {
        val newItem = ShoppingItem(
            id = currentList.value.items.size + 1,
            name = name,
            price = price,
            quantity = quantity
        )
        currentList.value = currentList.value.copy(
            items = (currentList.value.items + newItem).toMutableList()
        )
    }

    fun deleteItem(item: ShoppingItem) {
        currentList.value = currentList.value.copy(
            items = currentList.value.items.filter { it.id != item.id }.toMutableList()
        )
    }

    fun toggleItemChecked(item: ShoppingItem) {
        val updatedItems = currentList.value.items.map {
            if (it.id == item.id) it.copy(isChecked = !it.isChecked) else it
        }.toMutableList()
        currentList.value = currentList.value.copy(items = updatedItems)
    }

    fun calculateTotal(): String {
        return NumberFormat.getNumberInstance(Locale.US).format(
            currentList.value.items
            .filter { !it.isChecked }
            .sumOf { (it.price * it.quantity).toDouble() }
            .toFloat()
        )
    }

    fun loadSavedLists() {
        savedLists.clear()
        val folder = File(context.getExternalFilesDir(null), context.getString(R.string.saved_list ) )
        folder.listFiles()?.forEach { savedLists.add( it.name.removeSuffix( ".txt" )) }
    }

    fun updateSettings(key: String, value: Any){
        appSettings.value = appSettings.value.toMutableMap().apply { put(key, value) }
        saveSettings()
    }

    fun saveSettings(){
        viewModelScope.launch {
            val folder = File(context.getExternalFilesDir(null), context.getString(R.string.settings_dir))
            val file = File(folder, context.getString(R.string.settings_file))
            val writer = FileWriter(file)
            appSettings.value.forEach { key, value->
                writer.write("${key}: ${value}\n")
            }
            writer.close()
        }
    }

    fun loadSettings(): Map<String, Any> {
        val settings = mutableMapOf(
            context.getString(R.string.price_sym) to priceSym[0],
            context.getString(R.string.dark_mode) to false,
            context.getString(R.string.save_file) to false,
            context.getString(R.string.keyboard) to false
        )
        val folder = File(context.getExternalFilesDir(null), context.getString(R.string.settings_dir))
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val file = File(folder, context.getString(R.string.settings_file))
        if ( file.createNewFile() ){
            val writer = FileWriter(file)
            settings.forEach { key, value ->
                writer.write("${key}: ${value}\n")
            }
            writer.close()
            return  settings
        }
        settings.clear()
        val lines = file.readLines()
        lines.forEach { line->
            val theKey = line.substringBefore(":").trim()
            val theValue = line.substringAfter(": ").trim()
            settings.put(
                key=theKey,
                value= if (theValue.length > 1) theValue.toBoolean() else theValue
            )
        }
        return settings
    }

    fun renameFile(filename: String, newName: String){
        viewModelScope.launch{
            val folder = File(context.getExternalFilesDir(null), context.getString(R.string.saved_list))
            val file = File(folder, "$filename.txt")
            val newFile = File(folder, "$newName.txt")
            if (file.renameTo(newFile)) Log.d("mine", "successfully renamed")
            else Log.d("mine", "Failed to rename file")
        }
        loadSavedLists()
    }

    fun deleteFile(filename: String){
        viewModelScope.launch {
            val folder = File(context.getExternalFilesDir(null), context.getString(R.string.saved_list))
            val file = File(folder, "$filename.txt")
            try {
                file.delete()
                Log.d("mine", "deleted file successfully")
            } catch (_: Exception) {
                Log.d("mine", "failed to delete file")
            }
        }
        loadSavedLists()
    }

    fun loadListFromFile(filename: String): Boolean {
        return try {
            val folder = File(context.getExternalFilesDir(null), context.getString(R.string.saved_list) )
            val file = File(folder, "$filename.txt")

            if (!file.exists()) return false

            val lines = file.readLines()
            val newItems = mutableListOf<ShoppingItem>()
            var currentIndex = 0

            // Skip header (first 2 lines)
            for (i in 3 until lines.size-1) {
                val line = lines[i].trim()
                if (line.isEmpty()) continue

                // Item parsing logic
                when (currentIndex % 4) {
                    0 -> { // Name line (e.g., "1. Apples")
                        val itemName = line.substringAfter(". ").trim()
                        newItems.add(ShoppingItem(
                            id = newItems.size + 1,
                            name = itemName,
                            price = 0f,
                            quantity = 1
                        ))
                    }
                    1 -> { // Quantity line (e.g., "   Quantity: 5")
                        newItems.last().quantity = line.substringAfter( "${context.getString(R.string.qty) }: ").trim().toInt()
                    }
                    2 -> { // Price line (e.g., "   Price: $1.99")
                        val priceStr = line.replace("[^\\d.-]".toRegex(), "")       //.substringAfter( "${appSettings.value[ context.getString(R.string.price_sym) ]}" ).trim()
                        newItems.last().price = priceStr.toFloat()
                    }
                    3 -> { // Checked status (e.g., "   [✓]")
                        newItems.last().isChecked = line.contains("✓")
                    }
                }
                currentIndex++
            }
            currentList.value = currentList.value.copy(
                name = filename,
                items = newItems
            )
            true
        } catch (_: Exception) {
            false
        }
    }

    fun saveListToFile(): Boolean {
        return try {
            // 1. Create directory if it doesn't exist
            val folder = File(context.getExternalFilesDir(null), context.getString(R.string.saved_list) )
            if (!folder.exists()) {
                folder.mkdirs()
            }
            val stringBuilder = StringBuilder()
            val displayDateFormat = SimpleDateFormat("MMMM dd, yyyy hh:mm a", Locale.getDefault())
            stringBuilder.append("Shopping List\n")
            stringBuilder.append("Generated on: ${displayDateFormat.format(Date())}\n\n")

            currentList.value.items.forEachIndexed { index, item ->
                stringBuilder.append("${index + 1}. ${item.name}\n")
                stringBuilder.append("   ${context.getString(R.string.qty)}: ${item.quantity}\n")
                stringBuilder.append(
                    "   ${context.getString(R.string.price)}: ${appSettings.value[ context.getString(R.string.price_sym)]}${"%.2f".format(item.price)}\n"
                )
                stringBuilder.append("   ${if (item.isChecked) "[✓]" else "[ ]"}\n\n")
            }
            stringBuilder.append("\n${context.getString(R.string.total)}: $${ calculateTotal() }")
            val fileContent = stringBuilder.toString()

            val outputFile = File(folder, currentList.value.name +".txt")
            val writer = FileWriter(outputFile)
            try {
                writer.write(fileContent)
                true
            } finally {
                writer.close()
            }
        } catch (_: Exception) {
            false
        }
    }

    fun createShareIntent(): Intent? {
        val pdfFile = createPdfDocument() ?: return null
        return createShareIntentForPdf(pdfFile)
    }

    private fun createPdfDocument(): File? {
        val document = PdfDocument()
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 12f
        }
        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 18f
            isFakeBoldText = true
        }

        try {
            // Create a page
            val pageInfo = PageInfo.Builder(595, 842, 1).create() // A4 size at 72dpi
            val page = document.startPage(pageInfo)

            var yPos = 30f
            val margin = 30f

            // Draw title
            document.writeToPage(currentList.value.name, titlePaint, margin, yPos, page)
            yPos += titlePaint.textSize + 20f

            // Draw date
            val dateFormat = SimpleDateFormat("dd MMMM yyyy hh:mm a", Locale.getDefault())
            document.writeToPage("Created: ${dateFormat.format(Date())}", paint, margin, yPos, page)
            yPos += paint.textSize + 30f

            // Draw items
            currentList.value.items.forEachIndexed { index, item ->
                yPos = document.addLine("${index + 1}. ${item.name}", paint, margin, yPos, page)
                yPos = document.addLine("   ${context.getString(R.string.qty)}: ${item.quantity}", paint, margin, yPos, page)
                yPos = document.addLine("   ${context.getString(R.string.price)}: ${appSettings.value[context.getString(R.string.price_sym)]}${"%.2f".format(item.price)}", paint, margin, yPos, page)
                yPos = document.addLine("   ${context.getString(R.string.total)}: ${item.price * item.quantity}", paint, margin, yPos, page)
                yPos = document.addLine("   ${if (item.isChecked) "[✓]" else "[ ]"}", paint, margin, yPos, page)
                yPos += 10f // Extra space between items
            }

            // Draw total
            yPos += 20f
            document.writeToPage("${context.getString(R.string.unpaid_bal)}: ${appSettings.value[context.getString(R.string.price_sym)]}${ calculateTotal() }",
                titlePaint, margin, yPos, page)

            document.finishPage(page)

            // Save to file
            val folder = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "shopping_lists")
            if (!folder.exists()) {
                folder.mkdirs()
            }

            val pdfFile = File(folder, "${currentList.value.name}.pdf")
            val outputStream = FileOutputStream(pdfFile)
            document.writeTo(outputStream)
            document.close()
            outputStream.close()

            return pdfFile
        } catch (e: IOException) {
            e.printStackTrace()
            document.close()
            return null
        }
    }

    private fun createShareIntentForPdf(pdfFile: File): Intent {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            pdfFile
        )

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "application/pdf"
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        return shareIntent
    }
}

fun PdfDocument.writeToPage(text: String, paint: Paint, x: Float, y: Float, page: PdfDocument.Page) {
    val canvas = page.canvas
    canvas.drawText(text, x, y, paint)
}

fun PdfDocument.addLine(text: String, paint: Paint, x: Float, y: Float, page: PdfDocument.Page): Float {
    writeToPage(text, paint, x, y, page)
    return y + paint.textSize + 5f
}