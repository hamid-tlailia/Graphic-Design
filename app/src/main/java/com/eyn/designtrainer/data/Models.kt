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
    val savedBlocks: List<SavedBlock> = emptyList(),
)

/**
 * A block the user saved for reuse in any project. [category] is a [BlockKind] name or a name
 * the user typed. A free block carries its own copy of the drawing in [design].
 */
@Serializable
data class SavedBlock(
    val id: String = newId(),
    val name: String,
    val category: String,
    val block: Block,
    val design: Design? = null,
    val created: Long = System.currentTimeMillis(),
)

@Serializable
data class ColorPair(val fg: Long, val bg: Long)

// ---------------------------------------------------------------- Freeform canvas

@Serializable
enum class ElType {
    BOX, TEXT, BUTTON, INPUT, IMAGE, CIRCLE, ICON, DIVIDER,
    CARD, SEARCH, CHIP, CHECKBOX, TOGGLE, SLIDER, PROGRESS, RATING, AVATAR,
    SHAPE, FRAME, PAYMENT,
}

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
    val padH: Float = 0f,
    val padV: Float = 0f,
    val vAlign: Int = 1,        // 0 top, 1 center, 2 bottom
    val rotation: Float = 0f,
    val shadow: Float = 0f,
    val font: Int = 0,          // 0 IBM Plex, 1 Cairo, 2 Tajawal
    val lineHeight: Float = 1.35f,
    val letterSpacing: Float = 0f,
    val italic: Boolean = false,
    val underline: Boolean = false,
    val bg2: Long? = null,      // gradient end colour
    val gradDir: Int = 0,       // 0 vertical, 1 horizontal, 2 diagonal
    val image: String = "",     // absolute path of an imported photo
    val backImage: String = "", // photo shown behind [image] (e.g. after background removal)
    val fit: Int = 0,           // 0 crop, 1 fit
    val value: Float = 0.6f,    // progress / slider / rating / toggle / checkbox state
    val locked: Boolean = false,
    val iconStyle: Int = 0,     // IconFont style: 0 rounded, 1 solid, 2 outlined, 3 sharp, 4 two-tone
    val shape: Int = 0,         // SHAPE: index into Shapes.all; FRAME: index into Shapes.frames
    val inner: Int = 0,         // decorative inner line: 0 none, 1 dotted, 2 dashed, 3 solid
    val innerColor: Long = 0xFFFFFFFF,
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
    val bgImage: String = "",
    val updated: Long = System.currentTimeMillis(),
)

// ---------------------------------------------------------------- Block builder

@Serializable
enum class BlockKind { HEADER, HERO, SEARCH, CATEGORIES, CARDS, LIST, TEXT, FORM, BUTTON, FOOTER, CUSTOM }

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
    val textScale: Float = 1f,
    val minHeight: Float = 0f,
    val pinBottom: Boolean = false, // keep at the bottom of the page (footers, tab bars)
    val image: String = "",         // photo for hero / card blocks
    val items: List<BlockItem> = emptyList(), // editable tabs / rows / chips (empty = defaults)
    val active: Int = 0,            // highlighted item (current tab in a tab bar)
    val widthPct: Float = 1f,       // content width as a fraction of the page
    val itemHeight: Float = 0f,     // button / field / image height (0 = default)
    val body: String = "",          // long text (product description, alert details)
    val price: String = "",         // product price
    val icon: String = "",          // block-level icon (alerts)
    val hidden: Set<String> = emptySet(), // optional parts the user switched off (see BlockOption)
    val designId: String = "",      // free block: the canvas design drawn inside it
)

@Serializable
data class BlockItem(
    val label: String,
    val icon: String = "",
    val value: String = "",
    val image: String = "",
    val note: String = "",  // review text
    val qty: Int = 1,       // cart quantity
    val on: Boolean = false, // toggle / checkbox state
)

@Serializable
data class Page(val id: String = newId(), val name: String, val blocks: List<Block> = emptyList(), val height: Float = 640f)

@Serializable
data class AppProject(
    val id: String = newId(),
    val name: String,
    val logoStyle: Int = 0, // 0 icon + text, 1 letter, 2 text only
    val logoIcon: String = "coffee",
    val palette: Int = 0,   // index into Palettes, or -1 for [customColors]
    val customColors: List<Long> = emptyList(), // primary, surface, accent, background, text
    val font: Int = 2,      // 0 Tajawal, 1 Cairo, 2 IBM Plex
    val logoFont: Int = -1, // index into FontCatalog; -1 follows the app font
    val logoShape: Int = 0, // 0 rounded square, 1 circle, 2 no background
    val logoLayout: Int = 0, // 0 side by side, 1 stacked
    val logoScale: Float = 1f,
    val logoSpacing: Float = 0f,
    val tagline: String = "",
    val iconStyle: Int = 0, // IconFont style used across the app's blocks
    val applyIdentity: Boolean = true,
    val pages: List<Page> = emptyList(),
    val updated: Long = System.currentTimeMillis(),
)
