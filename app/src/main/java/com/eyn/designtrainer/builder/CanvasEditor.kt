package com.eyn.designtrainer.builder

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Redo
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.FactCheck
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.AlignHorizontalCenter
import androidx.compose.material.icons.rounded.AlignHorizontalLeft
import androidx.compose.material.icons.rounded.AlignHorizontalRight
import androidx.compose.material.icons.rounded.AlignVerticalBottom
import androidx.compose.material.icons.rounded.AlignVerticalCenter
import androidx.compose.material.icons.rounded.AlignVerticalTop
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.CenterFocusStrong
import androidx.compose.material.icons.rounded.CheckBox
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Crop169
import androidx.compose.material.icons.rounded.CropFree
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.FitScreen
import androidx.compose.material.icons.rounded.FlipToBack
import androidx.compose.material.icons.rounded.FlipToFront
import androidx.compose.material.icons.rounded.FormatAlignCenter
import androidx.compose.material.icons.rounded.FormatAlignLeft
import androidx.compose.material.icons.rounded.FormatAlignRight
import androidx.compose.material.icons.rounded.FormatBold
import androidx.compose.material.icons.rounded.FormatItalic
import androidx.compose.material.icons.rounded.FormatUnderlined
import androidx.compose.material.icons.rounded.Grid4x4
import androidx.compose.material.icons.rounded.HorizontalRule
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Input
import androidx.compose.material.icons.rounded.Label
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.LinearScale
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.OpenWith
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.SmartButton
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarHalf
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.Title
import androidx.compose.material.icons.rounded.ToggleOn
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.VerticalAlignBottom
import androidx.compose.material.icons.rounded.VerticalAlignCenter
import androidx.compose.material.icons.rounded.VerticalAlignTop
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.rounded.WebAsset
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eyn.designtrainer.data.BlockKind
import com.eyn.designtrainer.data.Design
import com.eyn.designtrainer.data.El
import com.eyn.designtrainer.data.ElType
import com.eyn.designtrainer.data.argb
import com.eyn.designtrainer.data.c
import com.eyn.designtrainer.data.newId
import com.eyn.designtrainer.i18n.L
import com.eyn.designtrainer.i18n.Lang
import com.eyn.designtrainer.i18n.LocalLang
import com.eyn.designtrainer.i18n.tr
import com.eyn.designtrainer.ui.LocalData
import com.eyn.designtrainer.ui.LocalNav
import com.eyn.designtrainer.ui.LocalVm
import com.eyn.designtrainer.ui.components.Segmented
import com.eyn.designtrainer.ui.components.SquareIconButton
import com.eyn.designtrainer.ui.components.SwatchRow
import com.eyn.designtrainer.ui.components.bounceClick
import com.eyn.designtrainer.ui.components.dotPattern
import com.eyn.designtrainer.ui.theme.Eyn
import com.eyn.designtrainer.ui.theme.Type
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

// ============================================================================ state

class EditorState(initial: Design) {
    var design by mutableStateOf(initial)
        private set
    var selectedId by mutableStateOf<String?>(null)
    var guideV by mutableStateOf<Float?>(null)
    var guideH by mutableStateOf<Float?>(null)
    var canUndo by mutableStateOf(false)
        private set
    var canRedo by mutableStateOf(false)
        private set
    var copiedStyle by mutableStateOf<El?>(null)
        private set

    private val undoStack = ArrayDeque<Design>()
    private val redoStack = ArrayDeque<Design>()
    private var lastKey: String? = null

    val selected: El? get() = design.els.firstOrNull { it.id == selectedId }

    /** Consecutive edits with the same [key] (a drag, a slider, holding +) collapse into one undo step. */
    fun edit(key: String? = null, f: (Design) -> Design) {
        if (key == null || key != lastKey) {
            undoStack.addLast(design)
            if (undoStack.size > 100) undoStack.removeFirst()
            redoStack.clear()
        }
        lastKey = key
        design = f(design)
        sync()
    }

    fun breakChain() { lastKey = null }

    fun updateEl(id: String, key: String? = null, f: (El) -> El) =
        edit(key) { d -> d.copy(els = d.els.map { if (it.id == id) f(it) else it }) }

    fun undo() {
        val prev = undoStack.removeLastOrNull() ?: return
        redoStack.addLast(design); design = prev; lastKey = null; sync()
        if (selected == null) selectedId = null
    }

    fun redo() {
        val next = redoStack.removeLastOrNull() ?: return
        undoStack.addLast(design); design = next; lastKey = null; sync()
    }

    private fun sync() { canUndo = undoStack.isNotEmpty(); canRedo = redoStack.isNotEmpty() }

    fun add(type: ElType, lang: Lang, variant: Int = 0, kind: String = "visa") {
        val ar = lang == Lang.AR
        fun s(a: String, e: String) = if (ar) a else e
        val (w, h) = when (type) {
            ElType.BOX -> 160f to 100f
            ElType.CARD -> 240f to 140f
            ElType.TEXT -> 220f to 32f
            ElType.BUTTON -> 160f to 48f
            ElType.CHIP -> 96f to 32f
            ElType.INPUT, ElType.SEARCH -> 280f to 48f
            ElType.IMAGE -> 200f to 140f
            ElType.CIRCLE, ElType.AVATAR -> 64f to 64f
            ElType.ICON -> 32f to 32f
            ElType.DIVIDER -> 240f to 1f
            ElType.CHECKBOX -> 200f to 28f
            ElType.TOGGLE -> 52f to 30f
            ElType.SLIDER -> 240f to 24f
            ElType.PROGRESS -> 240f to 8f
            ElType.RATING -> 140f to 24f
            ElType.SHAPE -> if (variant in WIDE_SHAPES) 220f to 80f else 120f to 120f
            ElType.FRAME -> design.w - 32f to design.h - 32f
            ElType.PAYMENT -> 72f to 44f
        }
        val el = El(
            type = type,
            x = ((design.w - w) / 2).roundToInt().toFloat(),
            y = if (type == ElType.FRAME) 16f else ((minOf(design.h, 640f) - h) / 2).roundToInt().toFloat(),
            w = w, h = h,
            bg = when (type) {
                ElType.BOX, ElType.IMAGE, ElType.AVATAR, ElType.CHIP, ElType.PROGRESS, ElType.SLIDER -> LILAC
                ElType.BUTTON, ElType.CHECKBOX, ElType.TOGGLE -> NAVY
                ElType.INPUT, ElType.CARD -> WHITE
                ElType.SEARCH -> CREAM
                ElType.CIRCLE, ElType.SHAPE -> CORAL
                ElType.DIVIDER -> LINE
                else -> CLEAR
            },
            fg = when (type) {
                ElType.BUTTON, ElType.CIRCLE, ElType.SHAPE -> WHITE
                ElType.INPUT, ElType.SEARCH -> GRAY
                ElType.ICON, ElType.IMAGE, ElType.AVATAR, ElType.CHIP, ElType.SLIDER -> NAVY
                ElType.PROGRESS -> CORAL
                ElType.RATING -> 0xFFFFC845
                else -> INK
            },
            text = when (type) {
                ElType.TEXT -> s("نص جديد", "New text")
                ElType.BUTTON -> s("زر", "Button")
                ElType.INPUT -> s("اكتب هنا…", "Type here…")
                ElType.SEARCH -> s("بحث…", "Search…")
                ElType.CHIP -> s("وسم", "Tag")
                ElType.CHECKBOX -> s("تذكرني", "Remember me")
                else -> ""
            },
            fontSize = when (type) { ElType.TEXT -> 18f; ElType.CHIP -> 13f; else -> 15f },
            bold = type == ElType.TEXT || type == ElType.BUTTON || type == ElType.CHIP,
            radius = when (type) {
                ElType.BOX -> 12f; ElType.CARD -> 16f; ElType.BUTTON, ElType.INPUT -> 14f; ElType.IMAGE -> 16f
                ElType.SEARCH -> 24f; ElType.CHIP -> 16f; ElType.PROGRESS -> 4f; else -> 0f
            },
            borderW = when (type) { ElType.INPUT -> 1f; ElType.FRAME -> 3f; else -> 0f },
            borderColor = when (type) { ElType.TOGGLE -> 0xFFD5D3E0; ElType.FRAME -> NAVY; else -> LINE },
            shadow = if (type == ElType.CARD) 8f else 0f,
            align = when (type) {
                ElType.BUTTON, ElType.CIRCLE, ElType.CHIP, ElType.SHAPE -> 1
                else -> if (ar) 2 else 0
            },
            icon = when (type) { ElType.ICON -> "star"; ElType.PAYMENT -> kind; else -> "" },
            shape = variant,
            value = when (type) { ElType.CHECKBOX, ElType.TOGGLE -> 1f; ElType.RATING -> 0.8f; ElType.SLIDER -> 0.5f; else -> 0.6f },
        )
        edit { it.copy(els = it.els + el) }
        selectedId = el.id
    }

    fun duplicate() {
        val s = selected ?: return
        val copy = s.copy(id = newId(), x = s.x + 12, y = s.y + 12, locked = false)
        edit { d -> d.copy(els = d.els + copy) }
        selectedId = copy.id
    }

    fun delete() {
        val id = selectedId ?: return
        edit { d -> d.copy(els = d.els.filter { it.id != id }) }
        selectedId = null
    }

    /** Moves the element within the z-order (positive = towards the front). */
    fun moveZ(id: String, delta: Int) = edit { d ->
        val list = d.els.toMutableList()
        val i = list.indexOfFirst { it.id == id }
        val j = (i + delta).coerceIn(0, list.lastIndex)
        if (i < 0 || i == j) d else d.copy(els = list.apply { add(j, removeAt(i)) })
    }

    /** The smallest visible element underneath [e]'s centre — what "centre inside" aligns to. */
    fun containerOf(e: El): El? {
        val cx = e.x + e.w / 2; val cy = e.y + e.h / 2
        return design.els.filter {
            it.id != e.id && it.visible && cx >= it.x && cx <= it.x + it.w && cy >= it.y && cy <= it.y + it.h &&
                it.w * it.h > e.w * e.h
        }.minByOrNull { it.w * it.h }
    }

    fun rectOf(target: El?): Rect = target?.let { Rect(it.x, it.y, it.x + it.w, it.y + it.h) } ?: Rect(0f, 0f, design.w, design.h)

    fun alignTo(target: El?, horizontal: Boolean, where: Int) {
        val s = selected ?: return
        val r = rectOf(target)
        updateEl(s.id) {
            if (horizontal) it.copy(x = when (where) { 0 -> r.left; 1 -> r.left + (r.width - it.w) / 2; else -> r.right - it.w })
            else it.copy(y = when (where) { 0 -> r.top; 1 -> r.top + (r.height - it.h) / 2; else -> r.bottom - it.h })
        }
    }

    fun centerIn(target: El?) {
        val s = selected ?: return
        val r = rectOf(target)
        updateEl(s.id) { it.copy(x = r.left + (r.width - it.w) / 2, y = r.top + (r.height - it.h) / 2) }
    }

    fun fillTarget(target: El?) {
        val s = selected ?: return
        val r = rectOf(target)
        updateEl(s.id) { it.copy(x = r.left, y = r.top, w = r.width, h = r.height) }
    }

    fun copyStyle() { copiedStyle = selected }

    fun pasteStyle() {
        val src = copiedStyle ?: return
        val s = selected ?: return
        updateEl(s.id) {
            it.copy(
                bg = src.bg, bg2 = src.bg2, gradDir = src.gradDir, fg = src.fg, fontSize = src.fontSize, bold = src.bold,
                italic = src.italic, underline = src.underline, font = src.font, radius = src.radius, borderW = src.borderW,
                borderColor = src.borderColor, shadow = src.shadow, opacity = src.opacity, padH = src.padH, padV = src.padV,
                lineHeight = src.lineHeight, letterSpacing = src.letterSpacing,
            )
        }
    }

    fun rename(n: String) = edit { it.copy(name = n) }
    fun canvas(key: String?, f: (Design) -> Design) = edit(key, f)
}

fun ElType.label(): L = when (this) {
    ElType.BOX -> L("شكل", "Shape")
    ElType.CARD -> L("بطاقة", "Card")
    ElType.TEXT -> L("نص", "Text")
    ElType.BUTTON -> L("زر", "Button")
    ElType.CHIP -> L("وسم", "Chip")
    ElType.INPUT -> L("حقل إدخال", "Input")
    ElType.SEARCH -> L("بحث", "Search")
    ElType.IMAGE -> L("صورة", "Image")
    ElType.AVATAR -> L("صورة شخصية", "Avatar")
    ElType.CIRCLE -> L("دائرة", "Circle")
    ElType.ICON -> L("أيقونة", "Icon")
    ElType.DIVIDER -> L("فاصل", "Divider")
    ElType.CHECKBOX -> L("مربع اختيار", "Checkbox")
    ElType.TOGGLE -> L("مفتاح", "Toggle")
    ElType.SLIDER -> L("منزلق", "Slider")
    ElType.PROGRESS -> L("شريط تقدّم", "Progress")
    ElType.RATING -> L("تقييم", "Rating")
    ElType.SHAPE -> L("شكل", "Shape")
    ElType.FRAME -> L("إطار مزخرف", "Decorative frame")
    ElType.PAYMENT -> L("وسيلة دفع", "Payment mark")
}

private fun ElType.icon(): ImageVector = when (this) {
    ElType.BOX -> Icons.Rounded.Crop169
    ElType.CARD -> Icons.Rounded.WebAsset
    ElType.TEXT -> Icons.Rounded.Title
    ElType.BUTTON -> Icons.Rounded.SmartButton
    ElType.CHIP -> Icons.Rounded.Label
    ElType.INPUT -> Icons.Rounded.Input
    ElType.SEARCH -> Icons.Rounded.Search
    ElType.IMAGE -> Icons.Rounded.Image
    ElType.AVATAR -> Icons.Rounded.AccountCircle
    ElType.CIRCLE -> Icons.Rounded.RadioButtonUnchecked
    ElType.ICON -> Icons.Rounded.Star
    ElType.DIVIDER -> Icons.Rounded.HorizontalRule
    ElType.CHECKBOX -> Icons.Rounded.CheckBox
    ElType.TOGGLE -> Icons.Rounded.ToggleOn
    ElType.SLIDER -> Icons.Rounded.Tune
    ElType.PROGRESS -> Icons.Rounded.LinearScale
    ElType.RATING -> Icons.Rounded.StarHalf
    ElType.SHAPE -> Icons.Rounded.Category
    ElType.FRAME -> Icons.Rounded.CropFree
    ElType.PAYMENT -> Icons.Rounded.CreditCard
}

/** Banner-like shapes start wide so a title fits inside. */
private val WIDE_SHAPES = setOf(6, 14, 15, 18, 20, 22, 25, 26, 27, 28, 29)

private val TEXT_TYPES = setOf(ElType.TEXT, ElType.BUTTON, ElType.INPUT, ElType.CIRCLE, ElType.CHIP, ElType.SEARCH, ElType.CHECKBOX, ElType.SHAPE)
private val VALUE_TYPES = setOf(ElType.CHECKBOX, ElType.TOGGLE, ElType.SLIDER, ElType.PROGRESS, ElType.RATING)
private val PHOTO_TYPES = setOf(ElType.IMAGE, ElType.AVATAR, ElType.BOX, ElType.CARD, ElType.CIRCLE, ElType.BUTTON, ElType.SHAPE)
private val ROUND = setOf(ElType.CIRCLE, ElType.AVATAR)
private val INNER_TYPES = setOf(ElType.SHAPE, ElType.BOX, ElType.CIRCLE, ElType.BUTTON, ElType.CARD, ElType.CHIP, ElType.IMAGE)

// ============================================================================ screen

private enum class PanelTab(val label: L, val icon: ImageVector) {
    ADD(L("إضافة", "Add"), Icons.Rounded.Add),
    STYLE(L("الألوان", "Style"), Icons.Rounded.Palette),
    TEXT(L("المحتوى", "Content"), Icons.Rounded.TextFields),
    LAYOUT(L("الحجم والموضع", "Layout"), Icons.Rounded.OpenWith),
    LAYERS(L("الطبقات", "Layers"), Icons.Rounded.Layers),
}

/** Who receives the next picked photo: an element id, or the canvas background. */
private const val CANVAS_TARGET = "__canvas__"
private const val BACK_PREFIX = "back:"

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CanvasEditorScreen(id: String) {
    val d = LocalData.current
    val vm = LocalVm.current
    val nav = LocalNav.current
    val ctx = LocalContext.current
    val c = Eyn.c
    val initial = remember { d.designs.firstOrNull { it.id == id } }
    if (initial == null) {
        LaunchedEffect(Unit) { nav.popBackStack() }
        return
    }
    val state = remember { EditorState(initial) }
    val lang = LocalLang.current
    var tab by remember { mutableStateOf(PanelTab.ADD) }
    var zoom by remember { mutableFloatStateOf(1f) }
    var snap by remember { mutableStateOf(true) }
    var renaming by remember { mutableStateOf(false) }
    var pickingIcon by remember { mutableStateOf(false) }
    var panelOpen by remember { mutableStateOf(true) }
    var reviewing by remember { mutableStateOf(false) }
    var savingBlock by remember { mutableStateOf(false) }
    var exporting by remember { mutableStateOf(false) }
    var exportScale by remember { mutableStateOf<Float?>(null) }
    var exportTransparent by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    val layer = rememberGraphicsLayer()
    val scope = rememberCoroutineScope()
    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    var photoTarget by remember { mutableStateOf<String?>(null) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        val target = photoTarget ?: return@rememberLauncherForActivityResult
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            val path = Photos.import(ctx, uri)
            if (path == null) Toast.makeText(ctx, "✗", Toast.LENGTH_SHORT).show()
            else if (target == CANVAS_TARGET) state.canvas(null) { it.copy(bgImage = path) }
            else if (target.startsWith(BACK_PREFIX)) state.updateEl(target.removePrefix(BACK_PREFIX)) { it.copy(backImage = path) }
            else state.updateEl(target) { it.copy(image = path) }
        }
    }
    val pickPhoto: (String) -> Unit = { target ->
        photoTarget = target
        picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    // Autosave shortly after every change, and once more on leave.
    LaunchedEffect(state.design) { delay(500); vm.saveDesign(state.design) }
    val latest by rememberUpdatedState(state.design)
    DisposableEffect(Unit) { onDispose { vm.saveDesign(latest) } }

    LaunchedEffect(state.selectedId) {
        if (state.selectedId != null && tab == PanelTab.ADD) tab = PanelTab.STYLE
        if (state.selectedId != null) panelOpen = true
    }

    /**
     * Renders the canvas at [widthPx] (0 = current screen size), optionally without background,
     * and saves or shares it. The canvas is temporarily re-laid out at export scale.
     */
    fun export(widthPx: Int, transparent: Boolean, jpeg: Boolean, gallery: Boolean) = scope.launch {
        if (busy) return@launch
        busy = true
        val sel = state.selectedId
        state.selectedId = null
        if (widthPx > 0) {
            val d = state.design
            // Cap at ~16 MP so very tall canvases stay within memory.
            val px = minOf(widthPx.toFloat(), kotlin.math.sqrt(16_000_000f * d.w / d.h))
            exportScale = px / (d.w * density.density)
        }
        exportTransparent = transparent && !jpeg
        delay(350)
        val bmp = runCatching { Exporter.capture(layer) }.getOrNull()
        exportScale = null; exportTransparent = false
        state.selectedId = sel
        busy = false
        if (bmp == null) { Toast.makeText(ctx, "✗", Toast.LENGTH_SHORT).show(); return@launch }
        val name = state.design.name
        if (gallery) {
            val ok = Exporter.saveToGallery(ctx, bmp, name, jpeg)
            Toast.makeText(ctx, if (ok) "✓ ${bmp.width}×${bmp.height}" else "Android 10+", Toast.LENGTH_SHORT).show()
            if (!ok) Exporter.share(ctx, listOf(if (jpeg) Exporter.jpg(ctx, bmp, name) else Exporter.png(ctx, bmp, name)), if (jpeg) "image/jpeg" else "image/png")
        } else Exporter.share(ctx, listOf(if (jpeg) Exporter.jpg(ctx, bmp, name) else Exporter.png(ctx, bmp, name)), if (jpeg) "image/jpeg" else "image/png")
    }

    fun removeBackground(id: String) = scope.launch {
        val e = state.design.els.firstOrNull { it.id == id } ?: return@launch
        busy = true
        Toast.makeText(ctx, if (lang == Lang.AR) "جارٍ إزالة الخلفية…" else "Removing background…", Toast.LENGTH_SHORT).show()
        when (val r = Photos.removeBackground(ctx, e.image)) {
            is Photos.CutResult.Done -> state.updateEl(id) { it.copy(image = r.path, fit = 1) }
            Photos.CutResult.Downloading -> Toast.makeText(
                ctx, if (lang == Lang.AR) "يتم تنزيل أداة إزالة الخلفية من Google Play، حاول بعد دقيقة" else "Downloading the background remover from Google Play — try again in a minute", Toast.LENGTH_LONG,
            ).show()
            is Photos.CutResult.Failed -> Toast.makeText(
                ctx, (if (lang == Lang.AR) "تعذّرت إزالة الخلفية. تحتاج خدمات Google Play." else "Couldn't remove the background. Google Play services required.") + (r.message?.let { "\n$it" } ?: ""), Toast.LENGTH_LONG,
            ).show()
        }
        busy = false
    }

    // imePadding keeps the properties panel above the keyboard while typing.
    Column(Modifier.fillMaxSize().background(c.bg).imePadding()) {
        // ------------------------------------------------------------- top bar
        Row(
            Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            SquareIconButton(Icons.AutoMirrored.Rounded.ArrowBack, { nav.popBackStack() })
            Column(Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).clickable { renaming = true }.padding(4.dp)) {
                Text(state.design.name, style = Type.h3, color = c.text, maxLines = 1)
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Text("${state.design.w.toInt()}×${state.design.h.toInt()}", style = Type.small, color = c.muted, maxLines = 1)
                }
            }
            ToolIcon(Icons.AutoMirrored.Rounded.Undo, state.canUndo, desc = tr("تراجع", "Undo")) { state.undo() }
            ToolIcon(Icons.AutoMirrored.Rounded.Redo, state.canRedo, desc = tr("إعادة", "Redo")) { state.redo() }
            ToolIcon(Icons.Rounded.Grid4x4, true, active = snap, desc = tr("الشبكة والالتقاط", "Grid & snap")) {
                snap = !snap
                Toast.makeText(
                    ctx,
                    if (lang == Lang.AR) (if (snap) "الشبكة والالتقاط: مفعّل — العناصر تلتصق بالشبكة وبحواف العناصر الأخرى" else "الشبكة والالتقاط: متوقف — تحريك حر")
                    else (if (snap) "Grid & snapping on — elements snap to the grid and to other elements" else "Grid & snapping off — free movement"),
                    Toast.LENGTH_SHORT,
                ).show()
            }
            ToolIcon(Icons.Rounded.FactCheck, true, desc = tr("تقييم التصميم", "Review design")) { state.selectedId = null; reviewing = true }
            ToolIcon(Icons.Rounded.IosShare, !busy, desc = tr("تصدير", "Export")) { exporting = true }
        }

        // ------------------------------------------------------------- canvas
        BoxWithConstraints(
            Modifier.weight(1f).fillMaxWidth().clipToBounds().dotPattern(c.muted.copy(alpha = 0.18f))
                .pointerInput(Unit) { detectTapGestures { state.selectedId = null } },
        ) {
            val fit = ((maxWidth.value - 40f) / state.design.w).coerceAtMost(2f)
            val s = exportScale ?: (fit * zoom)
            val vs = rememberScrollState()
            val hs = rememberScrollState()
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Box(
                    Modifier.fillMaxSize().verticalScroll(vs).horizontalScroll(hs).padding(20.dp),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    EditableCanvas(
                        state, s, snap, layer, onDoubleTap = { tab = PanelTab.TEXT; panelOpen = true },
                        showGrid = snap && exportScale == null && !busy, transparent = exportTransparent,
                    )
                }
            }
            Row(
                Modifier.align(Alignment.BottomEnd).padding(10.dp).clip(RoundedCornerShape(14.dp)).background(c.surface)
                    .border(1.dp, c.outline, RoundedCornerShape(14.dp)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ZoomBtn(Icons.Rounded.Remove) { zoom = (zoom - 0.25f).coerceAtLeast(0.5f) }
                Text("${(zoom * 100).roundToInt()}%", style = Type.small, color = c.text, modifier = Modifier.clickable { zoom = 1f }.padding(horizontal = 4.dp))
                ZoomBtn(Icons.Rounded.Add) { zoom = (zoom + 0.25f).coerceAtMost(4f) }
            }
        }

        // ------------------------------------------------------------- panel
        Column(
            Modifier.fillMaxWidth().shadow(16.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)).background(c.surface).navigationBarsPadding(),
        ) {
            // Drag handle: tap or swipe down to hide the panel and see the whole canvas.
            Box(
                Modifier.fillMaxWidth().height(22.dp)
                    .clickable(interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }, indication = null) { panelOpen = !panelOpen }
                    .pointerInput(Unit) {
                        var total = 0f
                        detectVerticalDragGestures(
                            onDragStart = { total = 0f },
                            onDragEnd = { if (total > 30) panelOpen = false else if (total < -30) panelOpen = true },
                        ) { change, dy -> change.consume(); total += dy }
                    },
                contentAlignment = Alignment.Center,
            ) { Box(Modifier.width(44.dp).height(5.dp).clip(CircleShape).background(c.muted.copy(alpha = 0.45f))) }
            SelectionHeader(state)
            AnimatedVisibility(panelOpen, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
            Column {
            if (!imeVisible) {
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    PanelTab.entries.forEach { t ->
                        val on = tab == t
                        val bg by animateColorAsState(if (on) c.lilac else Color.Transparent, label = "tab")
                        Row(
                            Modifier.clip(RoundedCornerShape(12.dp)).background(bg).clickable { tab = t }.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(t.icon, null, tint = if (on) c.onLilac else c.muted, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(t.label.t(), style = Type.label, color = if (on) c.onLilac else c.muted)
                        }
                    }
                }
            }
            AnimatedContent(tab, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "panel") { t ->
                Column(Modifier.fillMaxWidth().height(if (imeVisible) 150.dp else 280.dp).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 10.dp)) {
                    when (t) {
                        PanelTab.ADD -> AddPanel(state, onIcon = { pickingIcon = true })
                        PanelTab.STYLE -> StylePanel(state, pickPhoto, onRemoveBg = { removeBackground(it) }, busy = busy)
                        PanelTab.TEXT -> ContentPanel(state, onPickIcon = { pickingIcon = true })
                        PanelTab.LAYOUT -> LayoutPanel(state)
                        PanelTab.LAYERS -> LayersPanel(state)
                    }
                }
            }
            }
            }
        }
    }

    if (reviewing) ReviewSheet(state, onDismiss = { reviewing = false }, onShow = { id -> state.selectedId = id; reviewing = false })
    if (exporting) ExportSheet(state.design, onDismiss = { exporting = false }, onSaveBlock = { exporting = false; savingBlock = true }) { w, transparent, jpeg, gallery ->
        exporting = false
        export(w, transparent, jpeg, gallery)
    }
    if (savingBlock) SaveBlockDialog(state.design.name, BlockKind.CUSTOM.name, onDismiss = { savingBlock = false }) { n, cat ->
        vm.saveBlock(savedFromDesign(state.design, n, cat)); savingBlock = false
        Toast.makeText(ctx, if (lang == Lang.AR) "حُفظ في «القطع المحفوظة» بمكتبة القطع" else "Saved to “Saved blocks” in the block library", Toast.LENGTH_SHORT).show()
    }

    if (pickingIcon) {
        state.selected?.let { e ->
            IconPickerSheet(
                e.icon, onDismiss = { pickingIcon = false }, style = e.iconStyle,
                onStyle = { st -> state.updateEl(e.id) { it.copy(iconStyle = st) } },
            ) { name ->
                state.updateEl(e.id) { it.copy(icon = name) }
                pickingIcon = false
            }
        } ?: run { pickingIcon = false }
    }

    if (renaming) {
        var name by remember { mutableStateOf(state.design.name) }
        AlertDialog(
            onDismissRequest = { renaming = false },
            containerColor = c.surface,
            title = { Text(tr("اسم التصميم", "Design name"), style = Type.h3, color = c.text) },
            text = { OutlinedTextField(name, { name = it.take(40) }, singleLine = true, shape = RoundedCornerShape(12.dp)) },
            confirmButton = { TextButton({ state.rename(name.ifBlank { state.design.name }); renaming = false }) { Text(tr("حفظ", "Save"), color = c.primary) } },
            dismissButton = { TextButton({ renaming = false }) { Text(tr("إلغاء", "Cancel"), color = c.muted) } },
        )
    }
}

@Composable
private fun ToolIcon(icon: ImageVector, enabled: Boolean, active: Boolean = false, desc: String? = null, onClick: () -> Unit) {
    val c = Eyn.c
    Box(
        Modifier.size(36.dp).bounceClick(enabled, 0.88f, onClick).clip(RoundedCornerShape(12.dp))
            .background(if (active) c.lilac else c.surface).border(1.dp, c.outline, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
    ) { Icon(icon, desc, tint = if (!enabled) c.outline else if (active) c.onLilac else c.text, modifier = Modifier.size(18.dp)) }
}

@Composable
private fun ZoomBtn(icon: ImageVector, onClick: () -> Unit) {
    Box(Modifier.size(36.dp).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Icon(icon, null, tint = Eyn.c.text, modifier = Modifier.size(18.dp))
    }
}

// ============================================================================ canvas with gestures

/** Snaps one axis: any of the element's start/centre/end edges to the nearest target line. */
private fun snapAxis(pos: Float, size: Float, targets: List<Float>, threshold: Float): Pair<Float, Float?> {
    var best: Pair<Float, Float>? = null // (delta, guide)
    for (t in targets) {
        for (edge in floatArrayOf(pos, pos + size / 2, pos + size)) {
            val delta = t - edge
            if (abs(delta) <= threshold && (best == null || abs(delta) < abs(best.first))) best = delta to t
        }
    }
    return best?.let { pos + it.first to it.second } ?: (pos to null)
}

@Composable
private fun EditableCanvas(
    state: EditorState, s: Float, snap: Boolean, layer: GraphicsLayer, onDoubleTap: () -> Unit,
    showGrid: Boolean = false, transparent: Boolean = false,
) {
    val d = state.design
    val accent = Color(0xFF3B82F6)
    val guide = Color(0xFFEC4899)
    Box(
        Modifier.size((d.w * s).dp, (d.h * s).dp).shadow(10.dp, RoundedCornerShape(4.dp))
            .pointerInput(Unit) { detectTapGestures { state.selectedId = null } },
    ) {
        // Content (this is what gets exported)
        Box(
            Modifier.fillMaxSize().drawWithContent {
                layer.record { this@drawWithContent.drawContent() }
                drawLayer(layer)
            }.then(if (transparent) Modifier else Modifier.background(d.bg.c())).clipToBounds(),
        ) {
            if (d.bgImage.isNotEmpty() && !transparent) PhotoImage(d.bgImage, 0, Modifier.fillMaxSize())
            d.els.forEach { e ->
                RenderEl(
                    e, s,
                    Modifier
                        .pointerInput(e.id) {
                            detectTapGestures(
                                onTap = { state.selectedId = e.id },
                                onDoubleTap = { state.selectedId = e.id; onDoubleTap() },
                            )
                        }
                        .pointerInput(e.id, s, snap, e.locked) {
                            if (e.locked) return@pointerInput
                            var startX = 0f; var startY = 0f; var ax = 0f; var ay = 0f
                            detectDragGestures(
                                onDragStart = {
                                    val cur = state.design.els.first { it.id == e.id }
                                    state.selectedId = e.id; state.breakChain()
                                    startX = cur.x; startY = cur.y; ax = 0f; ay = 0f
                                },
                                onDragEnd = { state.guideV = null; state.guideH = null; state.breakChain() },
                                onDragCancel = { state.guideV = null; state.guideH = null; state.breakChain() },
                            ) { change, amount ->
                                change.consume()
                                val unit = s.dp.toPx()
                                ax += amount.x / unit; ay += amount.y / unit
                                val cur = state.design.els.firstOrNull { it.id == e.id } ?: return@detectDragGestures
                                var nx = startX + ax
                                var ny = startY + ay
                                if (snap) {
                                    // Snap to the canvas and to every other element's edges and centre.
                                    val others = state.design.els.filter { it.id != e.id && it.visible }
                                    val tx = listOf(0f, state.design.w / 2, state.design.w) + others.flatMap { listOf(it.x, it.x + it.w / 2, it.x + it.w) }
                                    val ty = listOf(0f, state.design.h / 2, state.design.h) + others.flatMap { listOf(it.y, it.y + it.h / 2, it.y + it.h) }
                                    val th = 5f / s.coerceAtLeast(0.5f)
                                    val (sx, gv) = snapAxis(nx, cur.w, tx, th)
                                    val (sy, gh) = snapAxis(ny, cur.h, ty, th)
                                    nx = if (gv != null) sx else nx.roundToInt().toFloat()
                                    ny = if (gh != null) sy else ny.roundToInt().toFloat()
                                    state.guideV = gv; state.guideH = gh
                                } else { nx = nx.roundToInt().toFloat(); ny = ny.roundToInt().toFloat() }
                                state.updateEl(e.id, "move") { it.copy(x = nx, y = ny) }
                            }
                        },
                )
            }
        }
        // Snap grid (an overlay, so it is never exported)
        if (showGrid) {
            val gridColor = Color(0xFF3B82F6).copy(alpha = 0.10f)
            androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
                val unitPx = s.dp.toPx()
                val step = if (8 * unitPx >= 10f) 8 else if (16 * unitPx >= 10f) 16 else 32
                var x = 0f
                while (x <= d.w) { drawLine(gridColor, androidx.compose.ui.geometry.Offset(x * unitPx, 0f), androidx.compose.ui.geometry.Offset(x * unitPx, size.height), 1f); x += step }
                var y = 0f
                while (y <= d.h) { drawLine(gridColor, androidx.compose.ui.geometry.Offset(0f, y * unitPx), androidx.compose.ui.geometry.Offset(size.width, y * unitPx), 1f); y += step }
            }
        }
        // Guides
        state.guideV?.let { g -> Box(Modifier.absoluteOffset(x = (g * s).dp).width(1.dp).fillMaxHeight().background(guide)) }
        state.guideH?.let { g -> Box(Modifier.absoluteOffset(y = (g * s).dp).height(1.dp).fillMaxWidth().background(guide)) }
        // Selection
        state.selected?.let { e ->
            state.containerOf(e)?.let { p ->
                Box(
                    Modifier.absoluteOffset((p.x * s).dp, (p.y * s).dp).size((p.w * s).dp, (p.h * s).dp)
                        .border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(2.dp)),
                )
            }
            Box(
                Modifier.absoluteOffset((e.x * s).dp - 2.dp, (e.y * s).dp - 2.dp)
                    .size((e.w * s).dp + 4.dp, (e.h * s).dp + 4.dp)
                    .graphicsLayer { rotationZ = e.rotation }
                    .border(1.5.dp, if (e.locked) Color(0xFF9CA3AF) else accent, RoundedCornerShape(3.dp)),
            )
            Text(
                "${e.w.roundToInt()} × ${e.h.roundToInt()}" + if (e.locked) "  🔒" else "",
                style = Type.small, color = Color.White,
                modifier = Modifier.absoluteOffset((e.x * s).dp, (e.y * s).dp - 22.dp)
                    .clip(RoundedCornerShape(4.dp)).background(if (e.locked) Color(0xFF6B7280) else accent).padding(horizontal = 5.dp),
            )
            if (!e.locked) Box(
                Modifier.absoluteOffset(((e.x + e.w) * s).dp - 14.dp, ((e.y + e.h) * s).dp - 14.dp)
                    .size(28.dp)
                    .pointerInput(e.id, s, snap) {
                        var sw = 0f; var sh = 0f; var ax = 0f; var ay = 0f
                        detectDragGestures(
                            onDragStart = {
                                val cur = state.design.els.first { it.id == e.id }
                                state.breakChain(); sw = cur.w; sh = cur.h; ax = 0f; ay = 0f
                            },
                            onDragEnd = { state.breakChain() },
                        ) { change, amount ->
                            change.consume()
                            val unit = s.dp.toPx()
                            ax += amount.x / unit; ay += amount.y / unit
                            var nw = (sw + ax).coerceAtLeast(4f)
                            var nh = (sh + ay).coerceAtLeast(1f)
                            if (snap) { nw = ((nw / 4f).roundToInt() * 4f).coerceAtLeast(4f); nh = ((nh / 4f).roundToInt() * 4f).coerceAtLeast(1f) }
                            else { nw = nw.roundToInt().toFloat(); nh = nh.roundToInt().toFloat() }
                            state.updateEl(e.id, "resize") { if (it.type in ROUND) it.copy(w = nw, h = nw) else it.copy(w = nw, h = nh) }
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                Box(Modifier.size(14.dp).clip(CircleShape).background(Color.White).border(2.dp, accent, CircleShape))
            }
        }
    }
}

// ============================================================================ controls

/** −/+ button that repeats (and speeds up) while held. */
@Composable
fun RepeatButton(icon: ImageVector, onStep: () -> Unit) {
    val c = Eyn.c
    val step by rememberUpdatedState(onStep)
    Box(
        Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(c.surfaceAlt)
            .pointerInput(Unit) {
                coroutineScope {
                    val sc = this
                    detectTapGestures(onPress = {
                        step()
                        val job = sc.launch {
                            delay(380)
                            var wait = 110L
                            while (isActive) { step(); delay(wait); wait = (wait * 0.85).toLong().coerceAtLeast(25L) }
                        }
                        tryAwaitRelease()
                        job.cancel()
                    })
                }
            },
        contentAlignment = Alignment.Center,
    ) { Icon(icon, null, tint = c.text, modifier = Modifier.size(18.dp)) }
}

/** Precise numeric control: label, −, editable value, +, and an optional slider for coarse moves. */
@Composable
fun NumStepper(
    label: String, value: Float, min: Float, max: Float, step: Float = 1f, suffix: String = "",
    slider: Boolean = true, onChange: (Float) -> Unit,
) {
    val c = Eyn.c
    val current by rememberUpdatedState(value)
    val change by rememberUpdatedState(onChange)
    fun set(v: Float) = change(((v / step).roundToInt() * step).coerceIn(min, max))
    Column(Modifier.padding(vertical = 3.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = Type.label, color = c.text, modifier = Modifier.weight(1f), maxLines = 1)
            var text by remember(value) { mutableStateOf(value.roundToInt().toString()) }
            // Numbers read left-to-right in both languages: − value +
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                RepeatButton(Icons.Rounded.Remove) { set(current - step) }
                Row(
                    Modifier.padding(horizontal = 6.dp).width(72.dp).clip(RoundedCornerShape(10.dp)).border(1.dp, c.outline, RoundedCornerShape(10.dp))
                        .padding(horizontal = 6.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BasicTextField(
                        text,
                        { t ->
                            text = t.filter { it.isDigit() || it == '-' }.take(5)
                            text.toIntOrNull()?.let { change(it.toFloat().coerceIn(min, max)) }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = Type.label.copy(color = c.text, textAlign = TextAlign.Center, fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.weight(1f),
                    )
                    if (suffix.isNotEmpty()) Text(suffix, style = Type.small.copy(fontSize = 10.sp), color = c.muted)
                }
                RepeatButton(Icons.Rounded.Add) { set(current + step) }
              }
            }
        }
        if (slider) CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Slider(
                value.coerceIn(min, max), { set(it) }, valueRange = min..max,
                colors = SliderDefaults.colors(thumbColor = c.primary, activeTrackColor = c.primary, inactiveTrackColor = c.lilac),
                modifier = Modifier.height(30.dp),
            )
        }
    }
}

@Composable
private fun SelectionHeader(state: EditorState) {
    val c = Eyn.c
    val e = state.selected
    Row(Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        if (e == null) {
            Text(tr("اضغط على عنصر لتحديده، واسحبه لتحريكه", "Tap an element to select, drag to move"), style = Type.small, color = c.muted, modifier = Modifier.weight(1f))
        } else {
            Icon(e.type.icon(), null, tint = c.primary, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(e.type.label().t(), style = Type.label, color = c.text, modifier = Modifier.weight(1f), maxLines = 1)
            MiniAction(if (e.locked) Icons.Rounded.Lock else Icons.Rounded.LockOpen, if (e.locked) c.accent else c.text) {
                state.updateEl(e.id) { it.copy(locked = !it.locked) }
            }
            MiniAction(Icons.Rounded.FlipToFront) { state.moveZ(e.id, 1) }
            MiniAction(Icons.Rounded.FlipToBack) { state.moveZ(e.id, -1) }
            MiniAction(Icons.Rounded.ContentCopy) { state.duplicate() }
            MiniAction(Icons.Rounded.Delete, c.danger) { state.delete() }
            MiniAction(Icons.Rounded.Close) { state.selectedId = null }
        }
    }
}

@Composable
private fun MiniAction(icon: ImageVector, tint: Color = Eyn.c.text, onClick: () -> Unit) {
    Box(Modifier.size(36.dp).clip(CircleShape).bounceClick(scaleDown = 0.85f, onClick = onClick), contentAlignment = Alignment.Center) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(19.dp))
    }
}

@Composable
private fun PanelLabel(text: String) {
    Text(text, style = Type.label, color = Eyn.c.muted, modifier = Modifier.padding(top = 8.dp, bottom = 8.dp))
}

@Composable
private fun Toggle(icon: ImageVector, on: Boolean, size: Int = 44, onClick: () -> Unit) {
    val c = Eyn.c
    val bg by animateColorAsState(if (on) c.lilac else c.surfaceAlt, label = "tg")
    Box(Modifier.size(size.dp).clip(RoundedCornerShape(12.dp)).background(bg).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Icon(icon, null, tint = if (on) c.onLilac else c.muted, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun ActionChip(text: String, icon: ImageVector, enabled: Boolean = true, selected: Boolean = false, onClick: () -> Unit) {
    val c = Eyn.c
    val border by animateColorAsState(if (selected) c.primary else Color.Transparent, label = "chipBorder")
    Row(
        Modifier.clip(RoundedCornerShape(12.dp)).background(if (enabled) c.lilac else c.surfaceAlt)
            .border(2.dp, border, RoundedCornerShape(12.dp)).clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(if (selected) Icons.Rounded.CheckCircle else icon, null, tint = if (selected) c.primary else if (enabled) c.onLilac else c.muted, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(text, style = Type.label, color = if (enabled) c.onLilac else c.muted)
    }
}

// ============================================================================ panels

@Composable
private fun AddPanel(state: EditorState, onIcon: () -> Unit) {
    val c = Eyn.c
    val lang = LocalLang.current
    PanelLabel(tr("أضف عنصراً إلى اللوحة", "Add an element to the canvas"))
    ElType.entries.filter { it !in GALLERY_TYPES }.chunked(4).forEach { row ->
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(bottom = 10.dp)) {
            row.forEach { t ->
                Column(
                    Modifier.weight(1f).bounceClick { state.add(t, lang); if (t == ElType.ICON) onIcon() }.clip(RoundedCornerShape(16.dp)).background(c.surfaceAlt).padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(t.icon(), null, tint = c.primary, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.height(6.dp))
                    Text(t.label().t(), style = Type.small, color = c.text, maxLines = 1)
                }
            }
            repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
        }
    }
    PanelLabel(tr("الأشكال", "Shapes"))
    ShapeGrid(-1) { i -> state.add(ElType.SHAPE, lang, variant = i) }
    PanelLabel(tr("إطارات وزخارف الصفحة", "Page frames & borders"))
    FrameGrid(-1) { i -> state.add(ElType.FRAME, lang, variant = i) }
    PanelLabel(tr("وسائل الدفع", "Payment marks"))
    PaymentGrid(null) { k -> state.add(ElType.PAYMENT, lang, kind = k) }
}

private val GALLERY_TYPES = setOf(ElType.SHAPE, ElType.FRAME, ElType.PAYMENT)

/** Thumbnails of every shape; [selected] is highlighted. */
@Composable
private fun ShapeGrid(selected: Int, onPick: (Int) -> Unit) {
    val c = Eyn.c
    Shapes.all.indices.chunked(6).forEach { row ->
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 8.dp)) {
            row.forEach { i ->
                Box(
                    Modifier.weight(1f).aspectRatio(1f).clip(RoundedCornerShape(12.dp)).background(c.surfaceAlt)
                        .border(2.dp, if (i == selected) c.primary else Color.Transparent, RoundedCornerShape(12.dp))
                        .bounceClick { onPick(i) }.padding(9.dp),
                    contentAlignment = Alignment.Center,
                ) { Box(Modifier.fillMaxSize().background(c.primary, Shapes.ShapeOf(i))) }
            }
            repeat(6 - row.size) { Spacer(Modifier.weight(1f)) }
        }
    }
}

@Composable
private fun FrameGrid(selected: Int, onPick: (Int) -> Unit) {
    val c = Eyn.c
    Shapes.frames.indices.chunked(4).forEach { row ->
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 8.dp)) {
            row.forEach { i ->
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier.fillMaxWidth().aspectRatio(0.78f).clip(RoundedCornerShape(10.dp)).background(Color.White)
                            .border(2.dp, if (i == selected) c.primary else c.outline, RoundedCornerShape(10.dp))
                            .bounceClick { onPick(i) }.padding(6.dp),
                    ) { Shapes.Frame(i, c.primary, 2f, 4f) }
                    Text(Shapes.frames[i].t(), style = Type.small, color = c.muted, maxLines = 1, modifier = Modifier.padding(top = 3.dp))
                }
            }
            repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
        }
    }
}

@Composable
private fun PaymentGrid(selected: String?, onPick: (String) -> Unit) {
    val c = Eyn.c
    Shapes.payments.chunked(4).forEach { row ->
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 8.dp)) {
            row.forEach { (k, _) ->
                Box(
                    Modifier.weight(1f).height(48.dp).clip(RoundedCornerShape(12.dp)).background(c.surfaceAlt)
                        .border(2.dp, if (k == selected) c.primary else Color.Transparent, RoundedCornerShape(12.dp))
                        .bounceClick { onPick(k) }.padding(6.dp),
                ) { Shapes.PaymentMark(k, Modifier.fillMaxSize()) }
            }
            repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
        }
    }
}

@Composable
private fun NoSelection() {
    Text(tr("حدّد عنصراً أولاً من اللوحة أو من الطبقات", "Select an element on the canvas or in Layers first"), style = Type.body, color = Eyn.c.muted)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StylePanel(state: EditorState, pickPhoto: (String) -> Unit, onRemoveBg: (String) -> Unit, busy: Boolean) {
    val e = state.selected
    if (e == null) {
        PanelLabel(tr("خلفية اللوحة", "Canvas background"))
        SwatchRow(state.design.bg.c(), { col -> state.canvas(null) { it.copy(bg = col.argb()) } })
        Spacer(Modifier.height(8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ActionChip(tr("صورة خلفية من الجوال", "Background photo"), Icons.Rounded.AddPhotoAlternate) { pickPhoto(CANVAS_TARGET) }
            if (state.design.bgImage.isNotEmpty()) ActionChip(tr("إزالة الصورة", "Remove photo"), Icons.Rounded.Close) { state.canvas(null) { it.copy(bgImage = "") } }
        }
        PanelLabel(tr("مقاس اللوحة", "Canvas size"))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(
                tr("جوال", "Phone") to (360f to 640f), tr("منشور", "Post") to (1080f to 1080f),
                tr("ستوري", "Story") to (1080f to 1920f), tr("غلاف", "Cover") to (1200f to 630f), "A4" to (595f to 842f),
            ).forEach { (n, size) ->
                val chosen = state.design.w == size.first && state.design.h == size.second
                ActionChip("$n ${size.first.toInt()}×${size.second.toInt()}", Icons.Rounded.FitScreen, selected = chosen) {
                    state.canvas(null) { it.copy(w = size.first, h = size.second) }
                }
            }
        }
        NumStepper(tr("العرض", "Width"), state.design.w, 60f, 2400f) { v -> state.canvas("cw") { it.copy(w = v) } }
        NumStepper(tr("الارتفاع", "Height"), state.design.h, 60f, 4000f) { v -> state.canvas("ch") { it.copy(h = v) } }
        return
    }
    when (e.type) {
        ElType.SHAPE -> { PanelLabel(tr("الشكل", "Shape")); ShapeGrid(e.shape) { i -> state.updateEl(e.id) { it.copy(shape = i) } } }
        ElType.FRAME -> { PanelLabel(tr("نمط الإطار", "Frame style")); FrameGrid(e.shape) { i -> state.updateEl(e.id) { it.copy(shape = i) } } }
        ElType.PAYMENT -> {
            PanelLabel(tr("وسيلة الدفع", "Payment method"))
            PaymentGrid(e.icon) { k -> state.updateEl(e.id) { it.copy(icon = k) } }
            NumStepper(tr("الشفافية", "Opacity"), e.opacity * 100, 5f, 100f, suffix = "%") { v -> state.updateEl(e.id, "op${e.id}") { it.copy(opacity = v / 100f) } }
            return
        }
        else -> {}
    }
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ActionChip(tr("نسخ التنسيق", "Copy style"), Icons.Rounded.ContentCopy) { state.copyStyle() }
        ActionChip(tr("لصق التنسيق", "Paste style"), Icons.Rounded.ContentPaste, enabled = state.copiedStyle != null) { state.pasteStyle() }
    }
    val fillLabel = when (e.type) {
        ElType.CHECKBOX, ElType.TOGGLE -> tr("لون التفعيل", "Active color")
        ElType.PROGRESS, ElType.SLIDER -> tr("لون المسار", "Track color")
        else -> tr("التعبئة", "Fill")
    }
    if (e.type != ElType.ICON && e.type != ElType.RATING) {
        PanelLabel(fillLabel)
        SwatchRow(e.bg.c(), { col -> state.updateEl(e.id) { it.copy(bg = col.argb()) } }, allowClear = true)
    }
    if (e.type !in VALUE_TYPES && e.type != ElType.ICON && e.type != ElType.DIVIDER) {
        Row(Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(tr("تدرّج لوني", "Gradient"), style = Type.label, color = Eyn.c.muted, modifier = Modifier.weight(1f))
            Segmented(
                listOf(tr("بدون", "None"), "↓", "→", "↘"),
                if (e.bg2 == null) 0 else e.gradDir + 1,
                { i -> state.updateEl(e.id) { if (i == 0) it.copy(bg2 = null) else it.copy(bg2 = it.bg2 ?: CORAL, gradDir = i - 1) } },
                Modifier.width(210.dp),
            )
        }
        AnimatedVisibility(e.bg2 != null, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
            Column {
                PanelLabel(tr("لون نهاية التدرّج", "Gradient end color"))
                SwatchRow((e.bg2 ?: CORAL).c(), { col -> state.updateEl(e.id) { it.copy(bg2 = col.argb()) } })
            }
        }
    }
    if (e.type in PHOTO_TYPES) {
        PanelLabel(tr("صورة من الجوال", "Photo from your phone"))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ActionChip(if (e.image.isEmpty()) tr("اختر صورة", "Pick photo") else tr("تغيير الصورة", "Change photo"), Icons.Rounded.AddPhotoAlternate) { pickPhoto(e.id) }
            if (e.image.isNotEmpty()) {
                ActionChip(tr("إزالة خلفية الصورة", "Remove photo background"), Icons.Rounded.AutoFixHigh, enabled = !busy) { onRemoveBg(e.id) }
                ActionChip(tr("حذف الصورة", "Delete photo"), Icons.Rounded.Close) { state.updateEl(e.id) { it.copy(image = "", backImage = "") } }
            }
        }
        if (e.image.isNotEmpty()) {
            Text(
                tr("بعد إزالة الخلفية اختر خلفية جديدة: لوناً من «التعبئة» أعلاه، أو تدرّجاً، أو صورة أخرى:", "After removing the background, pick a new one: a color from Fill above, a gradient, or another photo:"),
                style = Type.small, color = Eyn.c.muted, modifier = Modifier.padding(top = 8.dp, bottom = 6.dp),
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionChip(if (e.backImage.isEmpty()) tr("صورة خلفية للعنصر", "Background photo") else tr("تغيير صورة الخلفية", "Change background photo"), Icons.Rounded.Wallpaper) { pickPhoto(BACK_PREFIX + e.id) }
                if (e.backImage.isNotEmpty()) ActionChip(tr("إزالة صورة الخلفية", "Remove background photo"), Icons.Rounded.Close) { state.updateEl(e.id) { it.copy(backImage = "") } }
            }
        }
        if (e.image.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Segmented(listOf(tr("ملء وقص", "Fill & crop"), tr("احتواء كامل", "Fit whole")), e.fit, { i -> state.updateEl(e.id) { it.copy(fit = i) } }, Modifier.fillMaxWidth())
        }
    }
    if (e.type != ElType.BOX && e.type != ElType.CARD && e.type != ElType.DIVIDER && e.type != ElType.TOGGLE && e.type != ElType.FRAME) {
        PanelLabel(
            when (e.type) {
                ElType.ICON, ElType.RATING -> tr("لون الأيقونة", "Icon color")
                ElType.PROGRESS, ElType.SLIDER -> tr("لون التقدّم", "Fill color")
                else -> tr("لون النص", "Text color")
            },
        )
        SwatchRow(e.fg.c(), { col -> state.updateEl(e.id) { it.copy(fg = col.argb()) } })
    }
    if (e.type == ElType.TOGGLE) {
        PanelLabel(tr("لون الإيقاف", "Off color"))
        SwatchRow(e.borderColor.c(), { col -> state.updateEl(e.id) { it.copy(borderColor = col.argb()) } })
    }
    Spacer(Modifier.height(6.dp))
    if (e.type !in ROUND && e.type != ElType.ICON && e.type != ElType.TOGGLE && e.type != ElType.RATING && e.type != ElType.SHAPE) {
        NumStepper(tr("استدارة الزوايا", "Corner radius"), e.radius, 0f, 200f) { v -> state.updateEl(e.id, "r${e.id}") { it.copy(radius = v) } }
    }
    if (e.type !in VALUE_TYPES && e.type != ElType.ICON) {
        val frame = e.type == ElType.FRAME
        NumStepper(if (frame) tr("سماكة الزخرفة", "Line width") else tr("سماكة الإطار", "Border width"), e.borderW, 0f, 20f) { v -> state.updateEl(e.id, "bw${e.id}") { it.copy(borderW = v) } }
        if (e.borderW > 0f) {
            PanelLabel(if (frame) tr("لون الزخرفة", "Frame color") else tr("لون الإطار", "Border color"))
            SwatchRow(e.borderColor.c(), { col -> state.updateEl(e.id) { it.copy(borderColor = col.argb()) } })
        }
        if (e.type in INNER_TYPES) {
            PanelLabel(tr("خط داخلي مزخرف", "Decorative inner line"))
            Segmented(
                listOf(tr("بدون", "None"), tr("منقّط", "Dotted"), tr("متقطع", "Dashed"), tr("متصل", "Solid")), e.inner,
                { i -> state.updateEl(e.id) { it.copy(inner = i) } }, Modifier.fillMaxWidth(),
            )
            if (e.inner > 0) {
                Spacer(Modifier.height(8.dp))
                SwatchRow(e.innerColor.c(), { col -> state.updateEl(e.id) { it.copy(innerColor = col.argb()) } })
            }
        }
        NumStepper(tr("الظل", "Shadow"), e.shadow, 0f, 40f) { v -> state.updateEl(e.id, "sh${e.id}") { it.copy(shadow = v) } }
    }
    NumStepper(tr("الشفافية", "Opacity"), e.opacity * 100, 5f, 100f, suffix = "%") { v -> state.updateEl(e.id, "op${e.id}") { it.copy(opacity = v / 100f) } }
}

@Composable
private fun ContentPanel(state: EditorState, onPickIcon: () -> Unit) {
    val c = Eyn.c
    val e = state.selected ?: return NoSelection()
    if (e.type == ElType.ICON) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(56.dp).clip(RoundedCornerShape(14.dp)).background(c.surfaceAlt), contentAlignment = Alignment.Center) {
                FontIcon(e.icon, e.fg.c(), 32f)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(tr("الأيقونة الحالية", "Current icon"), style = Type.small, color = c.muted)
                Text(e.icon, style = Type.label, color = c.text)
            }
        }
        Spacer(Modifier.height(10.dp))
        ActionChip(tr("اختر من كل الأيقونات (+2000) مع البحث", "Browse all 2000+ icons with search"), Icons.Rounded.Search) { onPickIcon() }
        PanelLabel(tr("نمط الأيقونة", "Icon style"))
        IconStyleRow(e.iconStyle, sample = e.icon.ifBlank { "favorite" }) { i -> state.updateEl(e.id) { it.copy(iconStyle = i) } }
        return
    }
    if (e.type in VALUE_TYPES) {
        if (e.type == ElType.CHECKBOX || e.type == ElType.TOGGLE) {
            PanelLabel(tr("الحالة", "State"))
            Segmented(
                listOf(tr("مفعّل", "On"), tr("غير مفعّل", "Off")), if (e.value >= 0.5f) 0 else 1,
                { i -> state.updateEl(e.id) { it.copy(value = if (i == 0) 1f else 0f) } }, Modifier.fillMaxWidth(),
            )
        } else {
            NumStepper(
                if (e.type == ElType.RATING) tr("التقييم (من 5)", "Rating (of 5)") else tr("القيمة", "Value"),
                if (e.type == ElType.RATING) e.value * 5 else e.value * 100,
                0f, if (e.type == ElType.RATING) 5f else 100f,
                suffix = if (e.type == ElType.RATING) "★" else "%",
            ) { v -> state.updateEl(e.id, "v${e.id}") { it.copy(value = if (e.type == ElType.RATING) v / 5f else v / 100f) } }
        }
    }
    if (e.type !in TEXT_TYPES) {
        if (e.type !in VALUE_TYPES) Text(tr("هذا العنصر لا يحتوي نصاً", "This element has no text"), style = Type.body, color = c.muted)
        return
    }
    PanelLabel(tr("النص", "Text"))
    OutlinedTextField(
        e.text, { v -> state.updateEl(e.id, "t${e.id}") { it.copy(text = v) } },
        shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth(), maxLines = 3,
        textStyle = Type.body.copy(color = c.text),
    )
    PanelLabel(tr("الخط", "Font"))
    FontPickerRow(e.font, { i -> state.updateEl(e.id) { it.copy(font = i) } }, sample = e.text.take(5).ifBlank { "أبجد Aa" })
    Spacer(Modifier.height(6.dp))
    NumStepper(tr("حجم الخط", "Font size"), e.fontSize, 6f, 160f) { v -> state.updateEl(e.id, "fs${e.id}") { it.copy(fontSize = v) } }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 6.dp)) {
        Toggle(Icons.Rounded.FormatBold, e.bold) { state.updateEl(e.id) { it.copy(bold = !it.bold) } }
        Toggle(Icons.Rounded.FormatItalic, e.italic) { state.updateEl(e.id) { it.copy(italic = !it.italic) } }
        Toggle(Icons.Rounded.FormatUnderlined, e.underline) { state.updateEl(e.id) { it.copy(underline = !it.underline) } }
    }
    PanelLabel(tr("محاذاة النص", "Text alignment"))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(Icons.Rounded.FormatAlignLeft, Icons.Rounded.FormatAlignCenter, Icons.Rounded.FormatAlignRight).forEachIndexed { i, ic ->
            Toggle(ic, e.align == i) { state.updateEl(e.id) { it.copy(align = i) } }
        }
        Spacer(Modifier.width(8.dp))
        listOf(Icons.Rounded.VerticalAlignTop, Icons.Rounded.VerticalAlignCenter, Icons.Rounded.VerticalAlignBottom).forEachIndexed { i, ic ->
            Toggle(ic, e.vAlign == i) { state.updateEl(e.id) { it.copy(vAlign = i) } }
        }
    }
    Spacer(Modifier.height(6.dp))
    NumStepper(tr("المسافة الداخلية أفقياً", "Padding horizontal"), e.padH, 0f, 200f) { v -> state.updateEl(e.id, "ph${e.id}") { it.copy(padH = v) } }
    NumStepper(tr("المسافة الداخلية عمودياً", "Padding vertical"), e.padV, 0f, 200f) { v -> state.updateEl(e.id, "pv${e.id}") { it.copy(padV = v) } }
    NumStepper(tr("ارتفاع السطر", "Line height"), e.lineHeight * 100, 80f, 300f, step = 5f, suffix = "%") { v -> state.updateEl(e.id, "lh${e.id}") { it.copy(lineHeight = v / 100f) } }
    NumStepper(tr("تباعد الحروف", "Letter spacing"), e.letterSpacing, -5f, 30f) { v -> state.updateEl(e.id, "ls${e.id}") { it.copy(letterSpacing = v) } }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LayoutPanel(state: EditorState) {
    val c = Eyn.c
    val e = state.selected ?: return NoSelection()
    val d = state.design
    val container = state.containerOf(e)
    var useCanvas by remember(e.id) { mutableStateOf(container == null) }
    val target = if (useCanvas) null else container

    PanelLabel(tr("محاذاة داخل", "Align inside"))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        com.eyn.designtrainer.ui.components.ChipRow(
            listOfNotNull(tr("اللوحة", "Canvas"), container?.let { tr("العنصر تحته: ", "Element below: ") + (it.text.take(12).ifBlank { it.type.label().t() }) }),
            if (useCanvas || container == null) 0 else 1, { useCanvas = it == 0 },
            Modifier.padding(horizontal = 0.dp),
        )
    }
    Spacer(Modifier.height(8.dp))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Toggle(Icons.Rounded.AlignHorizontalLeft, false, 42) { state.alignTo(target, true, 0) }
        Toggle(Icons.Rounded.AlignHorizontalCenter, false, 42) { state.alignTo(target, true, 1) }
        Toggle(Icons.Rounded.AlignHorizontalRight, false, 42) { state.alignTo(target, true, 2) }
        Toggle(Icons.Rounded.AlignVerticalTop, false, 42) { state.alignTo(target, false, 0) }
        Toggle(Icons.Rounded.AlignVerticalCenter, false, 42) { state.alignTo(target, false, 1) }
        Toggle(Icons.Rounded.AlignVerticalBottom, false, 42) { state.alignTo(target, false, 2) }
    }
    Spacer(Modifier.height(8.dp))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ActionChip(tr("توسيط كامل", "Center both ways"), Icons.Rounded.CenterFocusStrong) { state.centerIn(target) }
        ActionChip(tr("ملء الحاوية", "Fill container"), Icons.Rounded.FitScreen) { state.fillTarget(target) }
    }
    PanelLabel(tr("الحجم", "Size"))
    NumStepper(tr("العرض", "Width"), e.w, 1f, maxOf(d.w * 2, e.w)) { v ->
        state.updateEl(e.id, "w${e.id}") { it.copy(w = v, h = if (it.type in ROUND) v else it.h) }
    }
    if (e.type !in ROUND) NumStepper(tr("الارتفاع", "Height"), e.h, 1f, maxOf(d.h * 2, e.h)) { v -> state.updateEl(e.id, "h${e.id}") { it.copy(h = v) } }
    PanelLabel(tr("الموضع", "Position"))
    NumStepper(tr("أفقي X", "X"), e.x, -e.w, d.w) { v -> state.updateEl(e.id, "x${e.id}") { it.copy(x = v) } }
    NumStepper(tr("عمودي Y", "Y"), e.y, -e.h, d.h) { v -> state.updateEl(e.id, "y${e.id}") { it.copy(y = v) } }
    NumStepper(tr("الدوران", "Rotation"), e.rotation, -180f, 180f, suffix = "°") { v -> state.updateEl(e.id, "rot${e.id}") { it.copy(rotation = v) } }
    PanelLabel(tr("الترتيب", "Order"))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ActionChip(tr("إلى الأمام", "Forward"), Icons.Rounded.FlipToFront) { state.moveZ(e.id, 1) }
        ActionChip(tr("إلى الخلف", "Backward"), Icons.Rounded.FlipToBack) { state.moveZ(e.id, -1) }
        ActionChip(tr("في المقدمة", "To front"), Icons.Rounded.FlipToFront) { state.moveZ(e.id, 999) }
        ActionChip(tr("في الخلفية", "To back"), Icons.Rounded.FlipToBack) { state.moveZ(e.id, -999) }
    }
    Spacer(Modifier.height(6.dp))
    Text(tr("نصيحة: اضغط مرتين على عنصر لتعديل محتواه", "Tip: double-tap an element to edit its content"), style = Type.small, color = c.muted)
}

@Composable
private fun LayersPanel(state: EditorState) {
    val c = Eyn.c
    if (state.design.els.isEmpty()) {
        Text(tr("لا توجد عناصر بعد", "No elements yet"), style = Type.body, color = c.muted)
        return
    }
    PanelLabel(tr("الأعلى يظهر فوق البقية", "Top of the list is in front"))
    state.design.els.reversed().forEach { e ->
        val on = e.id == state.selectedId
        val bg by animateColorAsState(if (on) c.lilac else c.surfaceAlt, label = "layer")
        Row(
            Modifier.fillMaxWidth().padding(bottom = 6.dp).clip(RoundedCornerShape(12.dp)).background(bg)
                .clickable { state.selectedId = e.id }.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(14.dp).clip(RoundedCornerShape(4.dp)).background(if (e.bg.c().alpha > 0f) e.bg.c() else e.fg.c()).border(1.dp, c.outline, RoundedCornerShape(4.dp)))
            Spacer(Modifier.width(8.dp))
            Icon(e.type.icon(), null, tint = c.muted, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(
                e.text.ifBlank { e.type.label().t() },
                style = Type.label.copy(fontWeight = if (on) FontWeight.SemiBold else FontWeight.Normal),
                color = if (e.visible) c.text else c.muted, maxLines = 1, modifier = Modifier.weight(1f),
            )
            MiniAction(if (e.locked) Icons.Rounded.Lock else Icons.Rounded.LockOpen, if (e.locked) c.accent else c.muted) {
                state.updateEl(e.id) { it.copy(locked = !it.locked) }
            }
            MiniAction(Icons.Rounded.FlipToFront) { state.moveZ(e.id, 1) }
            MiniAction(Icons.Rounded.FlipToBack) { state.moveZ(e.id, -1) }
            MiniAction(if (e.visible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff, if (e.visible) c.text else c.muted) {
                state.updateEl(e.id) { it.copy(visible = !it.visible) }
            }
        }
    }
}

// ============================================================================ icon picker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconPickerSheet(current: String, onDismiss: () -> Unit, style: Int = LocalIconStyle.current, onStyle: ((Int) -> Unit)? = null, onPick: (String) -> Unit) {
    val c = Eyn.c
    val ctx = LocalContext.current
    var query by remember { mutableStateOf("") }
    var shown by remember { mutableStateOf(style) }
    val results = remember(query, shown) { IconFont.search(ctx, query, shown) }
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), containerColor = c.surface) {
        Column(Modifier.fillMaxWidth().fillMaxHeight(0.85f).padding(horizontal = 16.dp).imePadding()) {
            Text(tr("كل الأيقونات", "All icons"), style = Type.h2, color = c.text)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                query, { query = it }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                leadingIcon = { Icon(Icons.Rounded.Search, null) },
                placeholder = { Text(tr("ابحث: بيت، قلب، home، cart…", "Search: home, heart, cart…")) },
            )
            Spacer(Modifier.height(8.dp))
            IconStyleRow(shown) { shown = it; onStyle?.invoke(it) }
            Text(tr("${results.size} أيقونة", "${results.size} icons"), style = Type.small, color = c.muted, modifier = Modifier.padding(vertical = 6.dp))
            if (results.isEmpty()) Text(
                tr("لا نتائج. جرّب كلمة أخرى أو بالإنجليزية (مثل hospital، cart، star).", "No results. Try another word, e.g. hospital, cart, star."),
                style = Type.small, color = c.muted,
            )
            LazyVerticalGrid(GridCells.Adaptive(56.dp), Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(results, key = { it.first }) { (name, glyph) ->
                    val on = name == current
                    Box(
                        Modifier.size(56.dp).clip(RoundedCornerShape(14.dp)).background(if (on) c.lilac else c.surfaceAlt).clickable { onPick(name) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(glyph, fontFamily = IconFont.family(shown), fontSize = 26.sp, color = if (on) c.onLilac else c.text)
                    }
                }
            }
        }
    }
}

/** Icon style chips: each previews the same icon in that style. */
@Composable
fun IconStyleRow(selected: Int, sample: String = "favorite", onSelect: (Int) -> Unit) {
    val c = Eyn.c
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        IconFont.styles.forEachIndexed { i, st ->
            val on = i == selected
            Column(
                Modifier.bounceClick { onSelect(i) }.clip(RoundedCornerShape(14.dp)).background(if (on) c.lilac else c.surfaceAlt)
                    .border(if (on) 2.dp else 0.dp, if (on) c.primary else Color.Transparent, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                FontIcon(sample, if (on) c.onLilac else c.text, 24f, style = i)
                Text(st.label.t(), style = Type.small.copy(fontSize = 11.sp), color = if (on) c.onLilac else c.muted)
            }
        }
    }
}
