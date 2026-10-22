package com.eyn.designtrainer.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.serialization.Serializable
import java.util.UUID

fun newId(): String = UUID.randomUUID().toString().take(8)

/** ARGB stored as Long so it survives JSON. */
fun Long.c(): Color = Color(this.toInt())
fun Color.argb(): Long = toArgb().toLong() and 0xFFFFFFFFL

@Serializable
data class AppData(
    val theme: String = "system", // system | light | dark
    val lang: String = "",        // ar | en ("" = follow device on first launch)
    val onboarded: Boolean = false,
    val name: String = "",
    val points: Int = 0,
    val answered: Int = 0,
    val correct: Int = 0,
    val bestRun: Int = 0,
    val streak: Int = 0,
    val lastDay: Long = -1,
    val activity: Map<Long, Int> = emptyMap(),
    val completed: Set<String> = emptySet(),
    val progress: Map<String, Int> = emptyMap(),
    val bookmarks: Set<String> = emptySet(),
    val dailyDay: Long = -1,
    val dailyDone: Int = 0,
    val practiceWins: Int = 0,
    val designs: List<Design> = emptyList(),
    val projects: List<AppProject> = emptyList(),
    val savedPairs: List<ColorPair> = emptyList(),
)

@Serializable
data class ColorPair(val fg: Long, val bg: Long)

// ---------------------------------------------------------------- Freeform canvas

@Serializable
enum class ElType { BOX, TEXT, BUTTON, INPUT, IMAGE, CIRCLE, ICON, DIVIDER }

@Serializable
data class El(
    val id: String = newId(),
    val type: ElType,
    val x: Float, val y: Float, val w: Float, val h: Float,
    val bg: Long = 0x00000000,
    val fg: Long = 0xFF1D1A3F,
    val text: String = "",
    val fontSize: Float = 14f,
    val bold: Boolean = false,
    val radius: Float = 0f,
    val borderW: Float = 0f,
    val borderColor: Long = 0xFFEAE8F0,
    val align: Int = 0, // 0 left, 1 center, 2 right (absolute, canvas is LTR)
    val opacity: Float = 1f,
    val icon: String = "",
    val visible: Boolean = true,
)

@Serializable
data class Design(
    val id: String = newId(),
    val name: String,
    val kind: String,
    val w: Float,
    val h: Float,
    val bg: Long,
    val els: List<El>,
    val updated: Long = System.currentTimeMillis(),
)

// ---------------------------------------------------------------- Block builder

@Serializable
enum class BlockKind { HEADER, HERO, SEARCH, CATEGORIES, CARDS, LIST, TEXT, FORM, BUTTON, FOOTER }

@Serializable
data class Block(
    val id: String = newId(),
    val kind: BlockKind,
    val variant: Int = 0,
    val title: String = "",
    val subtitle: String = "",
    val button: String = "",
    val bg: Long? = null,   // null = take from the project's palette
    val align: Int = 0,     // 0 start, 1 center
    val padding: Float = 16f,
    val radius: Float = 16f,
)

@Serializable
data class Page(val id: String = newId(), val name: String, val blocks: List<Block> = emptyList())

@Serializable
data class AppProject(
    val id: String = newId(),
    val name: String,
    val logoStyle: Int = 0, // 0 icon + text, 1 letter, 2 text only
    val logoIcon: String = "coffee",
    val palette: Int = 0,
    val font: Int = 2,      // 0 Tajawal, 1 Cairo, 2 IBM Plex
    val applyIdentity: Boolean = true,
    val pages: List<Page> = emptyList(),
    val updated: Long = System.currentTimeMillis(),
)
