package com.eyn.designtrainer.builder

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.core.content.FileProvider
import java.io.File

/** Saving and sharing rendered designs, PDFs and generated code. */
object Exporter {
    private fun dir(ctx: Context) = File(ctx.cacheDir, "exports").apply { mkdirs() }

    private fun safe(name: String) = name.replace(Regex("[^\\p{L}\\p{N}_-]+"), "_").trim('_').ifEmpty { "eyn" }

    suspend fun capture(layer: GraphicsLayer): Bitmap =
        layer.toImageBitmap().asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, false)

    fun png(ctx: Context, bmp: Bitmap, name: String): File =
        File(dir(ctx), safe(name) + ".png").apply { outputStream().use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) } }

    fun pdf(ctx: Context, pages: List<Bitmap>, name: String): File {
        val doc = PdfDocument()
        pages.forEachIndexed { i, b ->
            val page = doc.startPage(PdfDocument.PageInfo.Builder(b.width, b.height, i + 1).create())
            page.canvas.drawBitmap(b, 0f, 0f, null)
            doc.finishPage(page)
        }
        val f = File(dir(ctx), safe(name) + ".pdf")
        f.outputStream().use { doc.writeTo(it) }
        doc.close()
        return f
    }

    fun text(ctx: Context, content: String, fileName: String): File =
        File(dir(ctx), fileName).apply { writeText(content) }

    private fun uri(ctx: Context, f: File): Uri = FileProvider.getUriForFile(ctx, ctx.packageName + ".files", f)

    fun share(ctx: Context, files: List<File>, mime: String) {
        val uris = ArrayList(files.map { uri(ctx, it) })
        val intent = if (uris.size == 1) Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_STREAM, uris[0])
        else Intent(Intent.ACTION_SEND_MULTIPLE).putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
        intent.type = mime
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        ctx.startActivity(Intent.createChooser(intent, null))
    }

    /** Writes into Pictures/Eyn. Needs no permission on Android 10+. */
    fun saveToGallery(ctx: Context, bmp: Bitmap, name: String): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return false
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, safe(name) + "_" + System.currentTimeMillis() + ".png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Eyn")
        }
        val uri = ctx.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return false
        return ctx.contentResolver.openOutputStream(uri)?.use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) } ?: false
    }
}
