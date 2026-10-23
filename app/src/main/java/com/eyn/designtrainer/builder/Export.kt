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

    fun jpg(ctx: Context, bmp: Bitmap, name: String): File {
        // JPEG has no alpha: flatten onto white first.
        val flat = Bitmap.createBitmap(bmp.width, bmp.height, Bitmap.Config.ARGB_8888)
        android.graphics.Canvas(flat).apply { drawColor(android.graphics.Color.WHITE); drawBitmap(bmp, 0f, 0f, null) }
        return File(dir(ctx), safe(name) + ".jpg").apply { outputStream().use { flat.compress(Bitmap.CompressFormat.JPEG, 95, it) } }
    }

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

    /** A stored photo as a downsized base64 data URI, so exported code is self-contained. */
    fun dataUri(path: String, max: Int = 900): String? = runCatching {
        val bounds = android.graphics.BitmapFactory.Options().apply { inJustDecodeBounds = true }
        android.graphics.BitmapFactory.decodeFile(path, bounds)
        var sample = 1
        while (maxOf(bounds.outWidth, bounds.outHeight) / (sample * 2) >= max) sample *= 2
        val bmp = android.graphics.BitmapFactory.decodeFile(path, android.graphics.BitmapFactory.Options().apply { inSampleSize = sample })
            ?: return@runCatching null
        // Cut-outs keep their transparency as PNG; photos go out as JPEG.
        val png = path.endsWith(".png", ignoreCase = true)
        val out = java.io.ByteArrayOutputStream()
        bmp.compress(if (png) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG, 82, out)
        "data:image/${if (png) "png" else "jpeg"};base64," + android.util.Base64.encodeToString(out.toByteArray(), android.util.Base64.NO_WRAP)
    }.getOrNull()

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
    fun saveToGallery(ctx: Context, bmp: Bitmap, name: String, jpeg: Boolean = false): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return false
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, safe(name) + "_" + System.currentTimeMillis() + if (jpeg) ".jpg" else ".png")
            put(MediaStore.Images.Media.MIME_TYPE, if (jpeg) "image/jpeg" else "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Eyn")
        }
        val uri = ctx.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return false
        return ctx.contentResolver.openOutputStream(uri)?.use {
            if (jpeg) {
                val flat = Bitmap.createBitmap(bmp.width, bmp.height, Bitmap.Config.ARGB_8888)
                android.graphics.Canvas(flat).apply { drawColor(android.graphics.Color.WHITE); drawBitmap(bmp, 0f, 0f, null) }
                flat.compress(Bitmap.CompressFormat.JPEG, 95, it)
            } else bmp.compress(Bitmap.CompressFormat.PNG, 100, it)
        } ?: false
    }
}
