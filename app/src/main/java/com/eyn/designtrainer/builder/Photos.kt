package com.eyn.designtrainer.builder

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.util.LruCache
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

/** Imports photos from the gallery into app storage and loads them for the canvas. */
object Photos {
    private const val MAX = 1600
    private val cache = LruCache<String, ImageBitmap>(16)

    fun peek(path: String): ImageBitmap? = cache.get(path)

    /** Copies (and downsizes) a picked photo into app storage; returns its path. */
    suspend fun import(ctx: Context, uri: Uri): String? = withContext(Dispatchers.IO) {
        runCatching {
            val bmp = decode(ctx, uri) ?: return@runCatching null
            val dir = File(ctx.filesDir, "images").apply { mkdirs() }
            val f = File(dir, UUID.randomUUID().toString() + ".jpg")
            f.outputStream().use { bmp.compress(Bitmap.CompressFormat.JPEG, 88, it) }
            f.absolutePath
        }.getOrNull()
    }

    private fun decode(ctx: Context, uri: Uri): Bitmap? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // ImageDecoder applies EXIF rotation for us.
            return ImageDecoder.decodeBitmap(ImageDecoder.createSource(ctx.contentResolver, uri)) { d, info, _ ->
                val s = maxOf(info.size.width, info.size.height)
                if (s > MAX) d.setTargetSize(info.size.width * MAX / s, info.size.height * MAX / s)
                d.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        }
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        ctx.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        var sample = 1
        while (maxOf(bounds.outWidth, bounds.outHeight) / (sample * 2) >= MAX) sample *= 2
        val bmp = ctx.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = sample })
        } ?: return null
        val rotation = ctx.contentResolver.openInputStream(uri)?.use {
            when (ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
        } ?: 0f
        return if (rotation == 0f) bmp
        else Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, Matrix().apply { postRotate(rotation) }, true)
    }

    suspend fun load(path: String): ImageBitmap? = cache.get(path) ?: withContext(Dispatchers.IO) {
        runCatching { BitmapFactory.decodeFile(path)?.asImageBitmap() }.getOrNull()?.also { cache.put(path, it) }
    }
}

@Composable
fun PhotoImage(path: String, fit: Int, modifier: Modifier = Modifier) {
    var bmp by remember(path) { mutableStateOf(Photos.peek(path)) }
    LaunchedEffect(path) { if (bmp == null) bmp = Photos.load(path) }
    bmp?.let { Image(it, null, modifier, contentScale = if (fit == 1) ContentScale.Fit else ContentScale.Crop) }
}
