package com.eyn.designtrainer.builder

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Redo
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AlignHorizontalCenter
import androidx.compose.material.icons.rounded.AlignHorizontalLeft
import androidx.compose.material.icons.rounded.AlignHorizontalRight
import androidx.compose.material.icons.rounded.AlignVerticalBottom
import androidx.compose.material.icons.rounded.AlignVerticalCenter
import androidx.compose.material.icons.rounded.AlignVerticalTop
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Crop169
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.FlipToBack
import androidx.compose.material.icons.rounded.FlipToFront
import androidx.compose.material.icons.rounded.FormatAlignCenter
import androidx.compose.material.icons.rounded.FormatAlignLeft
import androidx.compose.material.icons.rounded.FormatAlignRight
import androidx.compose.material.icons.rounded.FormatBold
import androidx.compose.material.icons.rounded.Grid4x4
import androidx.compose.material.icons.rounded.HorizontalRule
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.SmartButton
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.Title
import androidx.compose.material.icons.rounded.OpenWith
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Input
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
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
import com.eyn.designtrainer.ui.components.SquareIconButton
import com.eyn.designtrainer.ui.components.SwatchRow
import com.eyn.designtrainer.ui.components.bounceClick
import com.eyn.designtrainer.ui.components.dotPattern
import com.eyn.designtrainer.ui.screens.SliderRow
import com.eyn.designtrainer.ui.theme.Eyn
import com.eyn.designtrainer.ui.theme.Type
import kotlinx.coroutines.delay
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

    private val undoStack = ArrayDeque<Design>()
    private val redoStack = ArrayDeque<Design>()
    private var lastKey: String? = null

    val selected: El? get() = design.els.firstOrNull { it.id == selectedId }

    /** Consecutive edits with the same [key] (a drag, a slider) collapse into one undo step. */
    fun edit(key: String? = null, f: (Design) -> Design) {
        if (key == null || key != lastKey) {
            undoStack.addLast(design)
            if (undoStack.size > 80) undoStack.removeFirst()
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

    fun add(type: ElType, lang: Lang) {
        val ar = lang == Lang.AR
        val (w, h) = when (type) {
            ElType.BOX -> 160f to 100f
            ElType.TEXT -> 220f to 32f
            ElType.BUTTON -> 160f to 48f
            ElType.INPUT -> 260f to 48f
            ElType.IMAGE -> 200f to 140f
            ElType.CIRCLE -> 64f to 64f
            ElType.ICON -> 32f to 32f
            ElType.DIVIDER -> 240f to 1f
        }
        val el = El(
            type = type,
            x = ((design.w - w) / 2).roundToInt().toFloat(),
            y = ((minOf(design.h, 640f) - h) / 2).roundToInt().toFloat(),
            w = w, h = h,
            bg = when (type) {
                ElType.BOX, ElType.IMAGE -> LILAC
                ElType.BUTTON -> NAVY
                ElType.INPUT -> WHITE
                ElType.CIRCLE -> CORAL
                ElType.DIVIDER -> LINE
                else -> CLEAR
            },
            fg = when (type) {
                ElType.BUTTON, ElType.CIRCLE -> WHITE
                ElType.INPUT -> GRAY
                ElType.ICON, ElType.IMAGE -> NAVY
                else -> INK
            },
            text = when (type) {
                ElType.TEXT -> if (ar) "نص جديد" else "New text"
                ElType.BUTTON -> if (ar) "زر" else "Button"
                ElType.INPUT -> if (ar) "اكتب هنا…" else "Type here…"
                else -> ""
            },
            fontSize = if (type == ElType.TEXT) 18f else 15f,
            bold = type == ElType.TEXT || type == ElType.BUTTON,
            radius = when (type) { ElType.BOX -> 12f; ElType.BUTTON, ElType.INPUT -> 14f; ElType.IMAGE -> 16f; else -> 0f },
            borderW = if (type == ElType.INPUT) 1f else 0f,
            align = when (type) { ElType.BUTTON, ElType.CIRCLE -> 1; else -> if (ar) 2 else 0 },
            icon = if (type == ElType.ICON) "star" else "",
        )
        edit { it.copy(els = it.els + el) }
        selectedId = el.id
    }

    fun duplicate() {
        val s = selected ?: return
        val copy = s.copy(id = newId(), x = s.x + 12, y = s.y + 12)
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

    fun alignInFrame(horizontal: Boolean, where: Int) {
        val s = selected ?: return
        updateEl(s.id) {
            if (horizontal) it.copy(x = when (where) { 0 -> 0f; 1 -> (design.w - it.w) / 2; else -> design.w - it.w })
            else it.copy(y = when (where) { 0 -> 0f; 1 -> (design.h - it.h) / 2; else -> design.h - it.h })
        }
    }

    fun rename(n: String) = edit { it.copy(name = n) }
    fun canvas(key: String?, f: (Design) -> Design) = edit(key, f)
}

private fun ElType.label(): L = when (this) {
    ElType.BOX -> L("شكل", "Shape")
    ElType.TEXT -> L("نص", "Text")
    ElType.BUTTON -> L("زر", "Button")
    ElType.INPUT -> L("حقل إدخال", "Input")
    ElType.IMAGE -> L("صورة", "Image")
    ElType.CIRCLE -> L("دائرة", "Circle")
    ElType.ICON -> L("أيقونة", "Icon")
    ElType.DIVIDER -> L("فاصل", "Divider")
}

private fun ElType.icon(): ImageVector = when (this) {
    ElType.BOX -> Icons.Rounded.Crop169
    ElType.TEXT -> Icons.Rounded.Title
    ElType.BUTTON -> Icons.Rounded.SmartButton
    ElType.INPUT -> Icons.Rounded.Input
    ElType.IMAGE -> Icons.Rounded.Image
    ElType.CIRCLE -> Icons.Rounded.RadioButtonUnchecked
    ElType.ICON -> Icons.Rounded.Star
    ElType.DIVIDER -> Icons.Rounded.HorizontalRule
}

private val ElType.hasText get() = this == ElType.TEXT || this == ElType.BUTTON || this == ElType.INPUT || this == ElType.CIRCLE

// ============================================================================ screen

private enum class PanelTab(val label: L, val icon: ImageVector) {
    ADD(L("إضافة", "Add"), Icons.Rounded.Add),
    STYLE(L("الألوان", "Style"), Icons.Rounded.Palette),
    TEXT(L("النص", "Text"), Icons.Rounded.TextFields),
    LAYOUT(L("الحجم والموضع", "Layout"), Icons.Rounded.OpenWith),
    LAYERS(L("الطبقات", "Layers"), Icons.Rounded.Layers),
}

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
    var tab by remember { mutableStateOf(PanelTab.ADD) }
    var zoom by remember { mutableFloatStateOf(1f) }
    var snap by remember { mutableStateOf(true) }
    var renaming by remember { mutableStateOf(false) }
    val layer = rememberGraphicsLayer()
    val scope = rememberCoroutineScope()

    // Autosave shortly after every change, and once more on leave.
    LaunchedEffect(state.design) { delay(500); vm.saveDesign(state.design) }
    val latest by rememberUpdatedState(state.design)
    DisposableEffect(Unit) { onDispose { vm.saveDesign(latest) } }

    LaunchedEffect(state.selectedId) {
        if (state.selectedId != null && tab == PanelTab.ADD) tab = PanelTab.STYLE
    }

    fun export(gallery: Boolean) = scope.launch {
        val sel = state.selectedId
        state.selectedId = null
        delay(80)
        val bmp = Exporter.capture(layer)
        state.selectedId = sel
        if (gallery) {
            val ok = Exporter.saveToGallery(ctx, bmp, state.design.name)
            Toast.makeText(ctx, if (ok) "✓" else "Android 10+", Toast.LENGTH_SHORT).show()
            if (!ok) Exporter.share(ctx, listOf(Exporter.png(ctx, bmp, state.design.name)), "image/png")
        } else Exporter.share(ctx, listOf(Exporter.png(ctx, bmp, state.design.name)), "image/png")
    }

    Column(Modifier.fillMaxSize().background(c.bg)) {
        // ------------------------------------------------------------- top bar
        Row(
            Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SquareIconButton(Icons.AutoMirrored.Rounded.ArrowBack, { nav.popBackStack() })
            Column(Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).clickable { renaming = true }.padding(4.dp)) {
                Text(state.design.name, style = Type.h3, color = c.text, maxLines = 1)
                Text("${state.design.w.toInt()} × ${state.design.h.toInt()}", style = Type.small, color = c.muted)
            }
            ToolIcon(Icons.AutoMirrored.Rounded.Undo, state.canUndo) { state.undo() }
            ToolIcon(Icons.AutoMirrored.Rounded.Redo, state.canRedo) { state.redo() }
            ToolIcon(Icons.Rounded.Grid4x4, true, active = snap) { snap = !snap }
            ToolIcon(Icons.Rounded.Download, true) { export(true) }
            ToolIcon(Icons.Rounded.Share, true) { export(false) }
        }

        // ------------------------------------------------------------- canvas
        BoxWithConstraints(
            Modifier.weight(1f).fillMaxWidth().clipToBounds().dotPattern(c.muted.copy(alpha = 0.18f))
                .pointerInput(Unit) { detectTapGestures { state.selectedId = null } },
        ) {
            val fit = ((maxWidth.value - 40f) / state.design.w).coerceAtMost(2f)
            val s = fit * zoom
            val vs = rememberScrollState()
            val hs = rememberScrollState()
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Box(
                    Modifier.fillMaxSize().verticalScroll(vs).horizontalScroll(hs).padding(20.dp),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    EditableCanvas(state, s, snap, layer)
                }
            }
            Row(
                Modifier.align(Alignment.BottomEnd).padding(12.dp).clip(RoundedCornerShape(14.dp)).background(c.surface)
                    .border(1.dp, c.outline, RoundedCornerShape(14.dp)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ZoomBtn(Icons.Rounded.Remove) { zoom = (zoom - 0.25f).coerceAtLeast(0.5f) }
                Text("${(zoom * 100).roundToInt()}%", style = Type.small, color = c.text, modifier = Modifier.clickable { zoom = 1f }.padding(horizontal = 4.dp))
                ZoomBtn(Icons.Rounded.Add) { zoom = (zoom + 0.25f).coerceAtMost(3f) }
            }
        }

        // ------------------------------------------------------------- panel
        Column(
            Modifier.fillMaxWidth().shadow(16.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)).background(c.surface).navigationBarsPadding(),
        ) {
            SelectionHeader(state)
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
            AnimatedContent(tab, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "panel") { t ->
                Column(Modifier.fillMaxWidth().height(250.dp).verticalScroll(rememberScrollState()).padding(16.dp)) {
                    when (t) {
                        PanelTab.ADD -> AddPanel(state)
                        PanelTab.STYLE -> StylePanel(state)
                        PanelTab.TEXT -> TextPanel(state)
                        PanelTab.LAYOUT -> LayoutPanel(state)
                        PanelTab.LAYERS -> LayersPanel(state)
                    }
                }
            }
        }
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
private fun ToolIcon(icon: ImageVector, enabled: Boolean, active: Boolean = false, onClick: () -> Unit) {
    val c = Eyn.c
    Box(
        Modifier.size(38.dp).bounceClick(enabled, 0.88f, onClick).clip(RoundedCornerShape(12.dp))
            .background(if (active) c.lilac else c.surface).border(1.dp, c.outline, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
    ) { Icon(icon, null, tint = if (!enabled) c.outline else if (active) c.onLilac else c.text, modifier = Modifier.size(19.dp)) }
}

@Composable
private fun ZoomBtn(icon: ImageVector, onClick: () -> Unit) {
    Box(Modifier.size(36.dp).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Icon(icon, null, tint = Eyn.c.text, modifier = Modifier.size(18.dp))
    }
}

// ============================================================================ canvas with gestures

@Composable
private fun EditableCanvas(state: EditorState, s: Float, snap: Boolean, layer: androidx.compose.ui.graphics.layer.GraphicsLayer) {
    val d = state.design
    val accent = Color(0xFF3B82F6)
    Box(
        Modifier.size((d.w * s).dp, (d.h * s).dp).shadow(10.dp, RoundedCornerShape(4.dp))
            .pointerInput(Unit) { detectTapGestures { state.selectedId = null } },
    ) {
        // Content (this is what gets exported)
        Box(
            Modifier.fillMaxSize().drawWithContent {
                layer.record { this@drawWithContent.drawContent() }
                drawLayer(layer)
            }.background(d.bg.c()).clipToBounds(),
        ) {
            d.els.forEach { e ->
                RenderEl(
                    e, s,
                    Modifier
                        .pointerInput(e.id) { detectTapGestures { state.selectedId = e.id } }
                        .pointerInput(e.id, s, snap) {
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
                                if (snap) { nx = (nx / 4f).roundToInt() * 4f; ny = (ny / 4f).roundToInt() * 4f }
                                val fw = state.design.w; val fh = state.design.h
                                state.guideV = if (abs(nx + cur.w / 2 - fw / 2) < 5f) { nx = fw / 2 - cur.w / 2; fw / 2 } else null
                                state.guideH = if (abs(ny + cur.h / 2 - fh / 2) < 5f) { ny = fh / 2 - cur.h / 2; fh / 2 } else null
                                state.updateEl(e.id, "move") { it.copy(x = nx, y = ny) }
                            }
                        },
                )
            }
        }
        // Guides
        state.guideV?.let { g -> Box(Modifier.absoluteOffset(x = (g * s).dp).width(1.dp).fillMaxHeight().background(Color(0xFFEC4899))) }
        state.guideH?.let { g -> Box(Modifier.absoluteOffset(y = (g * s).dp).height(1.dp).fillMaxWidth().background(Color(0xFFEC4899))) }
        // Selection
        state.selected?.let { e ->
            Box(
                Modifier.absoluteOffset((e.x * s).dp - 2.dp, (e.y * s).dp - 2.dp)
                    .size((e.w * s).dp + 4.dp, (e.h * s).dp + 4.dp)
                    .border(1.5.dp, accent, RoundedCornerShape(3.dp)),
            )
            Text(
                "${e.w.roundToInt()} × ${e.h.roundToInt()}",
                style = Type.small, color = Color.White,
                modifier = Modifier.absoluteOffset((e.x * s).dp, (e.y * s).dp - 22.dp)
                    .clip(RoundedCornerShape(4.dp)).background(accent).padding(horizontal = 5.dp),
            )
            // Resize handle (bottom-right corner)
            Box(
                Modifier.absoluteOffset(((e.x + e.w) * s).dp - 12.dp, ((e.y + e.h) * s).dp - 12.dp)
                    .size(24.dp)
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
                            state.updateEl(e.id, "resize") { if (it.type == ElType.CIRCLE) it.copy(w = nw, h = nw) else it.copy(w = nw, h = nh) }
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                Box(Modifier.size(14.dp).clip(CircleShape).background(Color.White).border(2.dp, accent, CircleShape))
            }
        }
    }
}

// ============================================================================ panels

@Composable
private fun SelectionHeader(state: EditorState) {
    val c = Eyn.c
    val e = state.selected
    Row(Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp, top = 10.dp, bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        if (e == null) {
            Text(tr("اضغط على عنصر لتحديده، واسحبه لتحريكه", "Tap an element to select, drag to move"), style = Type.small, color = c.muted, modifier = Modifier.weight(1f))
        } else {
            Icon(e.type.icon(), null, tint = c.primary, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(e.type.label().t(), style = Type.label, color = c.text, modifier = Modifier.weight(1f))
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
    Box(Modifier.size(38.dp).clip(CircleShape).bounceClick(scaleDown = 0.85f, onClick = onClick), contentAlignment = Alignment.Center) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(19.dp))
    }
}

@Composable
private fun PanelLabel(text: String) {
    Text(text, style = Type.label, color = Eyn.c.muted, modifier = Modifier.padding(top = 4.dp, bottom = 8.dp))
}

@Composable
private fun AddPanel(state: EditorState) {
    val c = Eyn.c
    val lang = LocalLang.current
    PanelLabel(tr("أضف عنصراً إلى اللوحة", "Add an element to the canvas"))
    ElType.entries.chunked(4).forEach { row ->
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(bottom = 10.dp)) {
            row.forEach { t ->
                Column(
                    Modifier.weight(1f).bounceClick { state.add(t, lang) }.clip(RoundedCornerShape(16.dp)).background(c.surfaceAlt).padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(t.icon(), null, tint = c.primary, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.height(6.dp))
                    Text(t.label().t(), style = Type.small, color = c.text, maxLines = 1)
                }
            }
        }
    }
}

@Composable
private fun NoSelection() {
    Text(tr("حدّد عنصراً أولاً من اللوحة أو من الطبقات", "Select an element on the canvas or in Layers first"), style = Type.body, color = Eyn.c.muted)
}

@Composable
private fun StylePanel(state: EditorState) {
    val e = state.selected
    if (e == null) {
        PanelLabel(tr("خلفية اللوحة", "Canvas background"))
        SwatchRow(state.design.bg.c(), { col -> state.canvas(null) { it.copy(bg = col.argb()) } })
        Spacer(Modifier.height(12.dp))
        SliderRow(tr("عرض اللوحة", "Canvas width"), state.design.w, 120f..1200f) { v -> state.canvas("cw") { it.copy(w = v.roundToInt().toFloat()) } }
        SliderRow(tr("ارتفاع اللوحة", "Canvas height"), state.design.h, 60f..2000f) { v -> state.canvas("ch") { it.copy(h = v.roundToInt().toFloat()) } }
        return
    }
    PanelLabel(tr("التعبئة", "Fill"))
    SwatchRow(e.bg.c(), { col -> state.updateEl(e.id) { it.copy(bg = col.argb()) } }, allowClear = true)
    if (e.type != ElType.BOX && e.type != ElType.DIVIDER) {
        PanelLabel(if (e.type == ElType.ICON) tr("لون الأيقونة", "Icon color") else tr("لون النص", "Text color"))
        SwatchRow(e.fg.c(), { col -> state.updateEl(e.id) { it.copy(fg = col.argb()) } })
    }
    Spacer(Modifier.height(8.dp))
    if (e.type != ElType.CIRCLE && e.type != ElType.DIVIDER && e.type != ElType.ICON) {
        SliderRow(tr("استدارة الزوايا", "Corner radius"), e.radius, 0f..60f) { v -> state.updateEl(e.id, "r${e.id}") { it.copy(radius = v.roundToInt().toFloat()) } }
    }
    SliderRow(tr("سماكة الإطار", "Border width"), e.borderW, 0f..8f, steps = 15) { v -> state.updateEl(e.id, "bw${e.id}") { it.copy(borderW = v) } }
    if (e.borderW > 0f) {
        PanelLabel(tr("لون الإطار", "Border color"))
        SwatchRow(e.borderColor.c(), { col -> state.updateEl(e.id) { it.copy(borderColor = col.argb()) } })
    }
    SliderRow(tr("الشفافية", "Opacity"), e.opacity * 100, 10f..100f, suffix = "%") { v -> state.updateEl(e.id, "op${e.id}") { it.copy(opacity = v / 100f) } }
}

@Composable
private fun TextPanel(state: EditorState) {
    val c = Eyn.c
    val e = state.selected ?: return NoSelection()
    if (e.type == ElType.ICON) {
        PanelLabel(tr("اختر أيقونة", "Pick an icon"))
        CanvasIcons.entries.chunked(7).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 8.dp)) {
                row.forEach { (k, v) ->
                    val on = e.icon == k
                    Box(
                        Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(if (on) c.lilac else c.surfaceAlt)
                            .clickable { state.updateEl(e.id) { it.copy(icon = k) } },
                        contentAlignment = Alignment.Center,
                    ) { Icon(v, null, tint = if (on) c.onLilac else c.text, modifier = Modifier.size(20.dp)) }
                }
            }
        }
        return
    }
    if (!e.type.hasText) {
        Text(tr("هذا العنصر لا يحتوي نصاً", "This element has no text"), style = Type.body, color = c.muted)
        return
    }
    OutlinedTextField(
        e.text, { v -> state.updateEl(e.id, "t${e.id}") { it.copy(text = v) } },
        label = { Text(tr("النص", "Text")) }, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(8.dp))
    SliderRow(tr("حجم الخط", "Font size"), e.fontSize, 8f..72f) { v -> state.updateEl(e.id, "fs${e.id}") { it.copy(fontSize = v.roundToInt().toFloat()) } }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        val opts = listOf(Icons.Rounded.FormatAlignLeft, Icons.Rounded.FormatAlignCenter, Icons.Rounded.FormatAlignRight)
        opts.forEachIndexed { i, ic ->
            Toggle(ic, e.align == i) { state.updateEl(e.id) { it.copy(align = i) } }
        }
        Spacer(Modifier.width(8.dp))
        Toggle(Icons.Rounded.FormatBold, e.bold) { state.updateEl(e.id) { it.copy(bold = !it.bold) } }
    }
}

@Composable
private fun Toggle(icon: ImageVector, on: Boolean, onClick: () -> Unit) {
    val c = Eyn.c
    val bg by animateColorAsState(if (on) c.lilac else c.surfaceAlt, label = "tg")
    Box(Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(bg).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Icon(icon, null, tint = if (on) c.onLilac else c.muted, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun LayoutPanel(state: EditorState) {
    val e = state.selected ?: return NoSelection()
    val d = state.design
    PanelLabel(tr("محاذاة داخل اللوحة", "Align in canvas"))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Toggle(Icons.Rounded.AlignHorizontalLeft, false) { state.alignInFrame(true, 0) }
        Toggle(Icons.Rounded.AlignHorizontalCenter, false) { state.alignInFrame(true, 1) }
        Toggle(Icons.Rounded.AlignHorizontalRight, false) { state.alignInFrame(true, 2) }
        Toggle(Icons.Rounded.AlignVerticalTop, false) { state.alignInFrame(false, 0) }
        Toggle(Icons.Rounded.AlignVerticalCenter, false) { state.alignInFrame(false, 1) }
        Toggle(Icons.Rounded.AlignVerticalBottom, false) { state.alignInFrame(false, 2) }
    }
    Spacer(Modifier.height(10.dp))
    SliderRow(tr("العرض", "Width"), e.w, 4f..maxOf(d.w, e.w)) { v -> state.updateEl(e.id, "w${e.id}") { it.copy(w = v.roundToInt().toFloat(), h = if (it.type == ElType.CIRCLE) v.roundToInt().toFloat() else it.h) } }
    if (e.type != ElType.CIRCLE) SliderRow(tr("الارتفاع", "Height"), e.h, 1f..maxOf(d.h, e.h)) { v -> state.updateEl(e.id, "h${e.id}") { it.copy(h = v.roundToInt().toFloat()) } }
    SliderRow(tr("الموضع الأفقي X", "Position X"), e.x.coerceIn(-e.w, d.w), -e.w..d.w) { v -> state.updateEl(e.id, "x${e.id}") { it.copy(x = v.roundToInt().toFloat()) } }
    SliderRow(tr("الموضع العمودي Y", "Position Y"), e.y.coerceIn(-e.h, d.h), -e.h..d.h) { v -> state.updateEl(e.id, "y${e.id}") { it.copy(y = v.roundToInt().toFloat()) } }
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
                .clickable { state.selectedId = e.id }.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(14.dp).clip(RoundedCornerShape(4.dp)).background(if (e.bg.c().alpha > 0f) e.bg.c() else e.fg.c()).border(1.dp, c.outline, RoundedCornerShape(4.dp)))
            Spacer(Modifier.width(10.dp))
            Icon(e.type.icon(), null, tint = c.muted, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(
                e.text.ifBlank { e.type.label().t() },
                style = Type.label.copy(fontWeight = if (on) FontWeight.SemiBold else FontWeight.Normal),
                color = if (e.visible) c.text else c.muted, maxLines = 1, modifier = Modifier.weight(1f),
            )
            MiniAction(Icons.Rounded.FlipToFront) { state.moveZ(e.id, 1) }
            MiniAction(Icons.Rounded.FlipToBack) { state.moveZ(e.id, -1) }
            MiniAction(if (e.visible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff, if (e.visible) c.text else c.muted) {
                state.updateEl(e.id) { it.copy(visible = !it.visible) }
            }
        }
    }
}
