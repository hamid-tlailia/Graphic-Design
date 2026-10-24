package com.eyn.designtrainer.builder

import androidx.compose.runtime.CompositionLocalProvider

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DragIndicator
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.eyn.designtrainer.data.AppProject
import com.eyn.designtrainer.data.Block
import com.eyn.designtrainer.data.Design
import com.eyn.designtrainer.data.SavedBlock
import com.eyn.designtrainer.data.BlockItem
import com.eyn.designtrainer.data.BlockKind
import com.eyn.designtrainer.data.Page
import com.eyn.designtrainer.data.argb
import com.eyn.designtrainer.data.c
import com.eyn.designtrainer.data.newId
import com.eyn.designtrainer.i18n.LocalLang
import com.eyn.designtrainer.i18n.tr
import com.eyn.designtrainer.ui.LocalData
import com.eyn.designtrainer.ui.LocalNav
import com.eyn.designtrainer.ui.LocalVm
import com.eyn.designtrainer.ui.Routes
import com.eyn.designtrainer.ui.components.ChipRow
import com.eyn.designtrainer.ui.components.Pill
import com.eyn.designtrainer.ui.components.PrimaryButton
import com.eyn.designtrainer.ui.components.Segmented
import com.eyn.designtrainer.ui.components.SquareIconButton
import com.eyn.designtrainer.ui.components.ColorDialog
import com.eyn.designtrainer.ui.components.SwatchRow
import com.eyn.designtrainer.ui.components.hex
import com.eyn.designtrainer.ui.screens.contrastRatio
import com.eyn.designtrainer.ui.components.TopBar
import com.eyn.designtrainer.ui.components.bounceClick
import com.eyn.designtrainer.ui.screens.SliderRow
import com.eyn.designtrainer.ui.theme.Eyn
import com.eyn.designtrainer.ui.theme.Type
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
private fun rememberProject(id: String): AppProject? {
    val d = LocalData.current
    val nav = LocalNav.current
    val p = d.projects.firstOrNull { it.id == id }
    if (p == null) LaunchedEffect(Unit) { nav.popBackStack() }
    return p
}

@Composable
private fun Label(text: String) = Text(text, style = Type.label, color = Eyn.c.text, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))

@Composable
internal fun OptionChip(label: String, on: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val c = Eyn.c
    val bg by animateColorAsState(if (on) c.primary else c.surface, label = "oc")
    Box(
        modifier.bounceClick(onClick = onClick).clip(RoundedCornerShape(50)).background(bg)
            .border(1.dp, if (on) Color.Transparent else c.outline, RoundedCornerShape(50)).padding(horizontal = 16.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center,
    ) { Text(label, style = Type.label, color = if (on) c.onPrimary else c.muted, maxLines = 1) }
}

// ============================================================================ 1 · identity

@Composable
fun IdentityScreen(id: String) {
    val c = Eyn.c
    val vm = LocalVm.current
    val nav = LocalNav.current
    val lang = LocalLang.current
    val p = rememberProject(id) ?: return
    val isNew = p.pages.isEmpty()
    fun set(f: (AppProject) -> AppProject) = vm.updateProject(id, f)
    var pickingIcon by remember { mutableStateOf(false) }
    var editingColor by remember { mutableStateOf<Int?>(null) }

    Column(Modifier.fillMaxSize()) {
        TopBar(tr("هوية تطبيقك", "Your app identity"), onBack = { nav.popBackStack() }) {
            if (isNew) Text("1 / 3", style = Type.label, color = c.muted)
        }
        // The logo preview stays pinned so every change below is visible immediately.
        Box(
            Modifier.padding(horizontal = 20.dp).fillMaxWidth().heightIn(min = 120.dp).clip(RoundedCornerShape(24.dp))
                .background(p.theme().surface).border(1.dp, c.outline, RoundedCornerShape(24.dp)).padding(16.dp).animateContentSize(),
            contentAlignment = Alignment.Center,
        ) { LogoMark(p, 56.dp) }
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            Label(tr("اسم التطبيق", "App name"))
            OutlinedTextField(
                p.name, { v -> set { it.copy(name = v.take(24)) } }, singleLine = true,
                shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth(),
            )
            val t = p.theme()
            Spacer(Modifier.height(10.dp))
            OptionChip(tr("افتح في مصمم الشعار — تحكم كامل وتصدير HD شفاف", "Open in logo designer — full control & transparent HD export"), false, Modifier.fillMaxWidth()) {
                val design = logoDesign(p, lang)
                vm.saveDesign(design)
                nav.navigate(Routes.canvas(design.id))
            }

            Label(tr("شكل الشعار", "Logo style"))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(tr("أيقونة + نص", "Icon + text"), tr("حرف", "Letter"), tr("نص فقط", "Text only")).forEachIndexed { i, l ->
                    OptionChip(l, p.logoStyle == i, Modifier.weight(1f)) { set { it.copy(logoStyle = i) } }
                }
            }
            AnimatedVisibility(p.logoStyle == 0, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
                Row(Modifier.padding(top = 12.dp).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        Modifier.height(46.dp).bounceClick { pickingIcon = true }.clip(RoundedCornerShape(14.dp)).background(c.lilac).padding(horizontal = 14.dp),
                        contentAlignment = Alignment.Center,
                    ) { Text(tr("متجر الأيقونات +2000", "Icon store 2000+"), style = Type.label, color = c.onLilac) }
                    (listOf(p.logoIcon) + QuickLogoIcons).distinct().forEach { k ->
                        val on = p.logoIcon == k
                        Box(
                            Modifier.size(46.dp).bounceClick { set { it.copy(logoIcon = k) } }.clip(RoundedCornerShape(14.dp))
                                .background(if (on) t.primary else c.surface).border(1.dp, c.outline, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center,
                        ) { FontIcon(k, if (on) t.onPrimary else c.muted, 24f, style = p.iconStyle) }
                    }
                }
            }
            Label(tr("نمط الأيقونات في التطبيق", "App icon style"))
            IconStyleRow(p.iconStyle, sample = p.logoIcon) { i -> set { it.copy(iconStyle = i) } }
            AnimatedVisibility(p.logoStyle != 2, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
                Column {
                    Label(tr("خلفية الرمز", "Mark background"))
                    Segmented(
                        listOf(tr("مربع مستدير", "Rounded"), tr("دائرة", "Circle"), tr("بدون", "None")), p.logoShape,
                        { i -> set { it.copy(logoShape = i) } }, Modifier.fillMaxWidth(),
                    )
                }
            }
            Label(tr("ترتيب الشعار", "Layout"))
            Segmented(listOf(tr("جنباً إلى جنب", "Side by side"), tr("فوق بعض", "Stacked")), p.logoLayout, { i -> set { it.copy(logoLayout = i) } }, Modifier.fillMaxWidth())
            Label(tr("خط الشعار", "Logo font"))
            FontPickerRow(p.logoFont, { i -> set { it.copy(logoFont = i) } }, extraFirst = tr("خط التطبيق", "App font"), sample = p.name.take(6).ifBlank { "أبجد" })
            Spacer(Modifier.height(8.dp))
            NumStepper(tr("حجم الاسم", "Name size"), p.logoScale * 100, 50f, 200f, step = 5f, suffix = "%") { v -> set { it.copy(logoScale = v / 100f) } }
            NumStepper(tr("تباعد الحروف", "Letter spacing"), p.logoSpacing, -4f, 20f) { v -> set { it.copy(logoSpacing = v) } }
            Label(tr("شعار نصي قصير (اختياري)", "Tagline (optional)"))
            OutlinedTextField(
                p.tagline, { v -> set { it.copy(tagline = v.take(40)) } }, singleLine = true,
                placeholder = { Text(tr("مثال: قهوة مختصة", "e.g. Specialty coffee")) },
                shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth(),
            )

            Label(tr("الألوان", "Colors"))
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                val custom = p.palette < 0
                Column(
                    Modifier.bounceClick {
                        set { pr -> pr.copy(palette = -1, customColors = pr.customColors.ifEmpty { pr.colors().let { listOf(it.primary, it.surface, it.accent, it.bg, it.ink) } }) }
                    }.clip(RoundedCornerShape(18.dp)).background(c.surface)
                        .border(if (custom) 2.dp else 1.dp, if (custom) c.primary else c.outline, RoundedCornerShape(18.dp)).padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        Modifier.size(width = 64.dp, height = 24.dp).clip(CircleShape)
                            .background(Brush.horizontalGradient(listOf(Color(0xFFEC4899), Color(0xFFF59E0B), Color(0xFF10B981), Color(0xFF3B82F6)))),
                        contentAlignment = Alignment.Center,
                    ) { Icon(Icons.Rounded.Add, null, tint = Color.White, modifier = Modifier.size(16.dp)) }
                    Spacer(Modifier.height(6.dp))
                    Text(tr("ألواني", "My colors"), style = Type.small, color = c.text)
                }
                Palettes.forEachIndexed { i, pal ->
                    val on = p.palette == i
                    Column(
                        Modifier.bounceClick { set { it.copy(palette = i) } }.clip(RoundedCornerShape(18.dp)).background(c.surface)
                            .border(if (on) 2.dp else 1.dp, if (on) c.primary else c.outline, RoundedCornerShape(18.dp)).padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                            listOf(pal.surface, pal.accent, pal.primary).forEach {
                                Box(Modifier.size(24.dp).clip(CircleShape).background(it.c()).border(2.dp, c.surface, CircleShape))
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(pal.name.t(), style = Type.small, color = c.text)
                    }
                }
            }
            AnimatedVisibility(p.palette < 0, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
                Column(Modifier.padding(top = 12.dp).fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(c.surface).border(1.dp, c.outline, RoundedCornerShape(18.dp)).padding(14.dp)) {
                    Text(tr("صمّم لوحة ألوانك — اضغط على أي لون لتغييره", "Build your palette — tap a color to change it"), style = Type.small, color = c.muted)
                    Spacer(Modifier.height(10.dp))
                    val labels = listOf(tr("أساسي", "Primary"), tr("ثانوي", "Surface"), tr("تمييز", "Accent"), tr("خلفية", "Background"), tr("نص", "Text"))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        labels.forEachIndexed { i, label ->
                            val col = p.customColors.getOrNull(i)?.c() ?: Color.Gray
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.bounceClick { editingColor = i }) {
                                Box(Modifier.size(44.dp).clip(CircleShape).background(col).border(1.dp, c.outline, CircleShape))
                                Spacer(Modifier.height(4.dp))
                                Text(label, style = Type.small.copy(fontSize = 11.sp), color = c.text)
                                Text(col.hex(), style = Type.small.copy(fontSize = 9.sp, textDirection = androidx.compose.ui.text.style.TextDirection.Ltr), color = c.muted)
                            }
                        }
                    }
                    val contrast = contrastRatio(p.colors().primary.c(), p.colors().bg.c())
                    Spacer(Modifier.height(8.dp))
                    Text(
                        tr("تباين الأساسي مع الخلفية: %.1f:1 ", "Primary vs background contrast: %.1f:1 ").format(contrast) +
                            if (contrast >= 4.5) tr("✓ ممتاز", "✓ good") else tr("⚠ ضعيف للنصوص", "⚠ weak for text"),
                        style = Type.small, color = if (contrast >= 4.5) c.success else c.danger,
                    )
                }
            }

            Label(tr("خط التطبيق", "App font"))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ProjectFonts.forEachIndexed { i, (name, _) -> OptionChip(name, p.font == i, Modifier.weight(1f)) { set { it.copy(font = i) } } }
            }
            Spacer(Modifier.height(24.dp))
        }
        Column(Modifier.navigationBarsPadding().padding(20.dp)) {
            PrimaryButton(if (isNew) tr("التالي: اختر القطع", "Next: choose blocks") else tr("حفظ", "Save")) {
                if (isNew) {
                    val home = Page(name = tr0(lang, "الرئيسية", "Home"))
                    vm.updateProject(id) { it.copy(pages = listOf(home)) }
                    nav.navigate(Routes.build(id)) { popUpTo(Routes.identity(id)) { inclusive = true } }
                    nav.navigate(Routes.library(id, home.id))
                } else nav.popBackStack()
            }
        }
    }
    if (pickingIcon) IconPickerSheet(p.logoIcon, onDismiss = { pickingIcon = false }, style = p.iconStyle, onStyle = { st -> set { it.copy(iconStyle = st) } }) { name ->
        set { it.copy(logoIcon = name) }; pickingIcon = false
    }
    editingColor?.let { i ->
        ColorDialog(p.customColors.getOrNull(i)?.c() ?: Color.Gray, onDismiss = { editingColor = null }) { col ->
            set { pr -> pr.copy(customColors = pr.customColors.toMutableList().also { l -> if (i < l.size) l[i] = col.argb() }) }
            editingColor = null
        }
    }
}

/** Popular picks shown before opening the full icon store. */
private val QuickLogoIcons = listOf(
    "local_cafe", "star", "favorite", "bolt", "shopping_bag", "home", "brush", "school", "restaurant", "spa",
    "pets", "eco", "diamond", "rocket_launch", "palette", "camera_alt", "music_note", "fitness_center", "local_florist", "auto_awesome",
)

private fun tr0(lang: com.eyn.designtrainer.i18n.Lang, ar: String, en: String) = if (lang == com.eyn.designtrainer.i18n.Lang.AR) ar else en

// ============================================================================ 2 · block library

@Composable
fun BlockLibraryScreen(id: String, pageId: String) {
    val c = Eyn.c
    val vm = LocalVm.current
    val nav = LocalNav.current
    val lang = LocalLang.current
    val p = rememberProject(id) ?: return
    val data = LocalData.current
    val saved = data.savedBlocks
    // Tab 0 holds the user's saved blocks; open it first once there are some.
    var tab by rememberSaveable { mutableStateOf(if (saved.isNotEmpty()) 0 else 1) }
    val kinds = BlockKind.entries
    // Keys: "k:KIND:variant" for built-in blocks, "s:id" for saved ones.
    val picked = remember { mutableStateListOf<String>() }
    var savedFilter by rememberSaveable { mutableStateOf<String?>(null) }
    var deleting by remember { mutableStateOf<SavedBlock?>(null) }
    val previewData = remember(data) { data.withSavedDrawings() }

    @Composable
    fun Tile(key: String, label: String, block: Block, onLong: (() -> Unit)? = null, modifier: Modifier) {
        val on = key in picked
        Column(modifier) {
            Box(
                Modifier.fillMaxWidth().height(118.dp).clip(RoundedCornerShape(18.dp))
                    .combinedClickable(onClick = { if (on) picked.remove(key) else picked.add(key) }, onLongClick = onLong)
                    .background(c.surface)
                    .border(if (on) 2.dp else 1.dp, if (on) c.primary else c.outline, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center,
            ) {
                BoxWithConstraints(Modifier.fillMaxSize().padding(8.dp), contentAlignment = Alignment.Center) {
                    val scale = (maxWidth.value / 360f)
                    Box(Modifier.clip(RoundedCornerShape(8.dp)).background(p.theme().bg)) {
                        CompositionLocalProvider(LocalData provides previewData) { Scaled(scale) { BlockView(block, p) } }
                    }
                }
                androidx.compose.animation.AnimatedVisibility(on, Modifier.align(Alignment.BottomStart).padding(8.dp), enter = scaleIn() + fadeIn(), exit = scaleOut() + fadeOut()) {
                    Box(Modifier.size(24.dp).clip(CircleShape).background(c.primary), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.Check, null, tint = c.onPrimary, modifier = Modifier.size(16.dp))
                    }
                }
            }
            Text(label, style = Type.small, color = c.text, maxLines = 1, modifier = Modifier.padding(top = 6.dp, start = 4.dp))
        }
    }

    @Composable
    fun <T> Grid(items: List<T>, tile: @Composable (T, Modifier) -> Unit) {
        items.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(bottom = 12.dp)) {
                row.forEach { tile(it, Modifier.weight(1f)) }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }

    @Composable
    fun SavedGrid(list: List<SavedBlock>) = Grid(list) { sb, m -> Tile("s:${sb.id}", sb.name, sb.block, onLong = { deleting = sb }, modifier = m) }

    Column(Modifier.fillMaxSize()) {
        TopBar(tr("مكتبة القطع", "Block library"), onBack = null) {
            SquareIconButton(Icons.Rounded.Close, { nav.popBackStack() })
        }
        ChipRow(listOf(tr("⭐ المحفوظة", "⭐ Saved")) + kinds.map { it.label().t() }, tab, { tab = it }, Modifier.padding(bottom = 8.dp))
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            if (tab == 0) {
                if (saved.isEmpty()) Text(
                    tr("لا توجد قطع محفوظة بعد. في صفحة البناء اضغط أي قطعة ثم «حفظ»، أو احفظ تصميماً من الاستوديو كقطعة.",
                        "No saved blocks yet. In the page builder tap a block, then “Save”, or save a studio design as a block."),
                    style = Type.body, color = c.muted, modifier = Modifier.padding(vertical = 24.dp),
                ) else {
                    val cats = saved.map { it.category }.distinct()
                    Row(Modifier.horizontalScroll(rememberScrollState()).padding(bottom = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OptionChip(tr("الكل", "All"), savedFilter == null) { savedFilter = null }
                        cats.forEach { cat -> OptionChip(categoryLabel(cat), savedFilter == cat) { savedFilter = cat } }
                    }
                    SavedGrid(saved.filter { savedFilter == null || it.category == savedFilter })
                    Text(tr("اضغط مطولاً على قطعة لحذفها", "Long-press a block to delete it"), style = Type.small, color = c.muted)
                }
            } else {
                val kind = kinds[tab - 1]
                val mine = saved.filter { it.category == kind.name }
                if (mine.isNotEmpty()) {
                    Text(tr("قطعك المحفوظة", "Your saved blocks"), style = Type.label, color = c.muted, modifier = Modifier.padding(bottom = 8.dp))
                    SavedGrid(mine)
                    Text(tr("القطع الجاهزة", "Ready-made"), style = Type.label, color = c.muted, modifier = Modifier.padding(bottom = 8.dp))
                }
                val names = Variants.getValue(kind)
                val order = if (kind == BlockKind.CUSTOM) CV.libraryOrder else names.indices.toList()
                Grid(order) { v, m -> Tile("k:${kind.name}:$v", names[v].t(), defaultBlock(kind, v, p, lang), modifier = m) }
            }
            Spacer(Modifier.height(12.dp))
        }
        Box(Modifier.navigationBarsPadding().padding(20.dp)) {
            PrimaryButton(
                if (picked.isEmpty()) tr("اختر قطعة أو أكثر", "Pick one or more blocks") else tr("أضف إلى الصفحة (${picked.size})", "Add to page (${picked.size})"),
                icon = Icons.Rounded.Add, enabled = picked.isNotEmpty(),
            ) {
                val blocks = picked.mapNotNull { key ->
                    val parts = key.split(":")
                    if (parts[0] == "s") saved.firstOrNull { it.id == parts[1] }?.instantiate(vm)
                    else defaultBlock(BlockKind.valueOf(parts[1]), parts[2].toInt(), p, lang)
                }
                vm.updateProject(id) { pr -> pr.copy(pages = pr.pages.map { if (it.id == pageId) it.copy(blocks = it.blocks + blocks) else it }) }
                nav.previousBackStackEntry?.savedStateHandle?.set("selectPage", pageId)
                nav.popBackStack()
            }
        }
    }
    deleting?.let { sb ->
        AlertDialog(
            onDismissRequest = { deleting = null }, containerColor = c.surface,
            title = { Text(tr("حذف «${sb.name}»؟", "Delete “${sb.name}”?"), style = Type.h3, color = c.text) },
            text = { Text(tr("ستُحذف من القطع المحفوظة فقط، ولن تتأثر الصفحات التي استعملتها.", "It's removed from saved blocks only; pages that use it stay as they are."), color = c.muted) },
            confirmButton = { TextButton(onClick = { vm.deleteSavedBlock(sb.id); picked.remove("s:${sb.id}"); deleting = null }) { Text(tr("حذف", "Delete"), color = c.danger) } },
            dismissButton = { TextButton(onClick = { deleting = null }) { Text(tr("إلغاء", "Cancel")) } },
        )
    }
}

// ============================================================================ 3 · page builder

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PageBuilderScreen(id: String) {
    val c = Eyn.c
    val data = LocalData.current
    val lang = LocalLang.current
    val vm = LocalVm.current
    val nav = LocalNav.current
    val ctx = LocalContext.current
    val p = rememberProject(id) ?: return
    if (p.pages.isEmpty()) return
    var pageId by rememberSaveable { mutableStateOf(p.pages.first().id) }
    // Other screens (library, export) ask us to jump to a page via the saved-state handle.
    val handle = nav.currentBackStackEntry?.savedStateHandle
    val requested = remember(handle) { handle?.getStateFlow<String?>("selectPage", null) }?.collectAsState()?.value
    LaunchedEffect(requested) {
        if (requested != null) { pageId = requested; handle?.set<String?>("selectPage", null) }
    }
    val page = p.pages.firstOrNull { it.id == pageId } ?: p.pages.first()
    var selected by rememberSaveable { mutableStateOf<String?>(null) }
    var editing by remember { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf<Block?>(null) }
    var preview by rememberSaveable { mutableStateOf(false) }
    var pageDialog by remember { mutableStateOf<Page?>(null) }
    var addingPage by remember { mutableStateOf(false) }

    fun setBlocks(list: List<Block>) = vm.updateProject(id) { pr -> pr.copy(pages = pr.pages.map { if (it.id == page.id) it.copy(blocks = list) else it }) }

    // Drag-to-reorder state: a working copy while the finger is down.
    var working by remember { mutableStateOf<List<Block>?>(null) }
    var dragId by remember { mutableStateOf<String?>(null) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val heights = remember { mutableStateMapOf<String, Int>() }
    var resizing by remember { mutableStateOf(false) }
    val blocks = working ?: page.blocks

    Column(Modifier.fillMaxSize()) {
        TopBar(tr("بناء الصفحات", "Page builder"), onBack = { nav.popBackStack() }, subtitle = p.name) {
            SquareIconButton(if (preview) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility, { preview = !preview; selected = null }, tint = if (preview) c.accent else c.text)
            SquareIconButton(Icons.Rounded.Palette, { nav.navigate(Routes.identity(id)) })
            SquareIconButton(Icons.Rounded.IosShare, { nav.navigate(Routes.export(id)) })
        }
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically,
        ) {
            p.pages.forEach { pg ->
                val on = pg.id == page.id
                val bg by animateColorAsState(if (on) c.primary else c.surface, label = "pg")
                Text(
                    pg.name, style = Type.label, color = if (on) c.onPrimary else c.muted,
                    modifier = Modifier.clip(RoundedCornerShape(50)).background(bg).border(1.dp, if (on) Color.Transparent else c.outline, RoundedCornerShape(50))
                        .combinedClickable(onClick = { pageId = pg.id; selected = null }, onLongClick = { pageDialog = pg })
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
            Box(
                Modifier.size(36.dp).clip(CircleShape).border(1.dp, c.outline, CircleShape).clickable { addingPage = true },
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Rounded.Add, null, tint = c.muted, modifier = Modifier.size(18.dp)) }
        }

        Column(Modifier.weight(1f).verticalScroll(rememberScrollState(), enabled = dragId == null && !resizing).padding(horizontal = 16.dp, vertical = 8.dp)) {
            val t = p.theme()

            /** Moves [b] by one step inside its own group (top blocks or pinned-bottom blocks). */
            fun move(b: Block, dir: Int) {
                val group = blocks.filter { it.pinBottom == b.pinBottom }
                val gi = group.indexOfFirst { it.id == b.id }
                val other = group.getOrNull(gi + dir) ?: return
                val list = blocks.toMutableList()
                val a = list.indexOfFirst { it.id == b.id }; val o = list.indexOfFirst { it.id == other.id }
                list[a] = other; list[o] = b
                setBlocks(list)
            }

            // State is read here and passed down: reads inside this local function don't trigger recomposition
            // reliably when it is called from the PinnedColumn slots.
            val sel = selected
            val dragging = dragId
            val offset = dragOffset

            @Composable
            fun BuilderBlock(b: Block) {
                val isSel = b.id == sel && !preview
                val isDrag = b.id == dragging
                val group = blocks.filter { it.pinBottom == b.pinBottom }
                val gi = group.indexOfFirst { it.id == b.id }
                Column(
                    Modifier.zIndex(if (isDrag) 1f else 0f).onSizeChanged { heights[b.id] = it.height }
                        .graphicsLayer {
                            translationY = if (isDrag) offset else 0f
                            shadowElevation = if (isDrag) 24f else 0f
                            scaleX = if (isDrag) 1.02f else 1f; scaleY = scaleX
                        }
                        // Opaque while dragging so the block never shows through the ones beneath it.
                        .then(if (isDrag) Modifier.clip(RoundedCornerShape(14.dp)).background(t.bg) else Modifier),
                ) {
                    AnimatedVisibility(isSel, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
                        Row(
                            Modifier.padding(start = 8.dp, bottom = 4.dp).horizontalScroll(rememberScrollState())
                                .clip(RoundedCornerShape(10.dp)).background(c.text).padding(horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            BlockAction(tr("تعديل", "Edit"), Icons.Rounded.Edit) { editing = b.id }
                            BlockAction(tr("تكرار", "Duplicate"), Icons.Rounded.ContentCopy) {
                                // A free block's drawing is copied too, so the two can be edited separately.
                                val drawing = data.designs.firstOrNull { it.id == b.designId }
                                val copyId = if (drawing != null) newId().also { vm.saveDesign(drawing.copy(id = it)) } else b.designId
                                val i = blocks.indexOf(b); setBlocks(blocks.toMutableList().apply { add(i + 1, b.copy(id = newId(), designId = copyId)) })
                            }
                            BlockAction(tr("حفظ", "Save"), Icons.Rounded.BookmarkAdd) { saving = b }
                            BlockAction(tr("أعلى", "Up"), Icons.Rounded.KeyboardArrowUp, enabled = gi > 0) { move(b, -1) }
                            BlockAction(tr("أسفل", "Down"), Icons.Rounded.KeyboardArrowDown, enabled = gi < group.lastIndex) { move(b, 1) }
                            BlockAction(tr("حذف", "Delete"), Icons.Rounded.Delete) {
                                setBlocks(blocks - b); selected = null
                                if (b.designId.isNotEmpty()) vm.deleteDesign(b.designId)
                            }
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.weight(1f).clip(RoundedCornerShape(14.dp))
                                .border(if (isSel) 2.dp else 0.dp, if (isSel) c.primary else Color.Transparent, RoundedCornerShape(14.dp))
                                .clickable(enabled = !preview) { selected = if (sel == b.id) null else b.id },
                        ) { BlockView(b, p) }
                        if (!preview) Box(
                            Modifier.width(26.dp).height(48.dp).pointerInput(b.id) {
                                detectDragGestures(
                                    onDragStart = { working = page.blocks; dragId = b.id; dragOffset = 0f; selected = null },
                                    onDragEnd = { working?.let { setBlocks(it) }; working = null; dragId = null; dragOffset = 0f },
                                    onDragCancel = { working = null; dragId = null; dragOffset = 0f },
                                ) { change, amount ->
                                    change.consume()
                                    dragOffset += amount.y
                                    val list = working ?: return@detectDragGestures
                                    val me = list.firstOrNull { it.id == dragId } ?: return@detectDragGestures
                                    // Reorder only within the same group so pinned footers stay at the bottom.
                                    val grp = list.filter { it.pinBottom == me.pinBottom }
                                    val i = grp.indexOfFirst { it.id == me.id }
                                    val neighbour = when {
                                        dragOffset > 0 -> grp.getOrNull(i + 1)
                                        dragOffset < 0 -> grp.getOrNull(i - 1)
                                        else -> null
                                    } ?: return@detectDragGestures
                                    val h = heights[neighbour.id] ?: return@detectDragGestures
                                    if (abs(dragOffset) > h / 2f) {
                                        val l = list.toMutableList()
                                        val a = l.indexOfFirst { it.id == me.id }; val o = l.indexOfFirst { it.id == neighbour.id }
                                        l[a] = neighbour; l[o] = me
                                        working = l
                                        dragOffset -= if (dragOffset > 0) h else -h
                                    }
                                }
                            },
                            contentAlignment = Alignment.Center,
                        ) { Icon(Icons.Rounded.DragIndicator, null, tint = c.muted.copy(alpha = 0.7f), modifier = Modifier.size(20.dp)) }
                    }
                }
            }

            Box(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(t.bg)
                    .border(6.dp, c.text.copy(alpha = 0.85f), RoundedCornerShape(28.dp)).padding(vertical = 14.dp, horizontal = 6.dp),
            ) {
                PinnedColumn(
                    page.height.dp, Modifier.fillMaxWidth(),
                    top = {
                        if (blocks.isEmpty()) Text(
                            tr("الصفحة فارغة. أضف قطعاً من المكتبة.", "This page is empty. Add blocks from the library."),
                            style = Type.body, color = c.muted, modifier = Modifier.padding(24.dp).align(Alignment.CenterHorizontally),
                        )
                        blocks.filter { !it.pinBottom }.forEach { key(it.id) { BuilderBlock(it) } }
                    },
                    bottom = { blocks.filter { it.pinBottom }.forEach { key(it.id) { BuilderBlock(it) } } },
                )
            }
            // Page height handle: drag up/down to make the page shorter or longer.
            if (!preview) {
                val density = LocalDensity.current
                Row(
                    Modifier.fillMaxWidth().padding(top = 6.dp).pointerInput(page.id) {
                        detectDragGestures(
                            onDragStart = { resizing = true },
                            onDragEnd = { resizing = false },
                            onDragCancel = { resizing = false },
                        ) { change, amount ->
                            change.consume()
                            val dy = with(density) { amount.y.toDp().value }
                            vm.updateProject(id) { pr -> pr.copy(pages = pr.pages.map { if (it.id == page.id) it.copy(height = (it.height + dy).coerceIn(300f, 4000f)) else it }) }
                        }
                    }.padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.width(48.dp).height(5.dp).clip(CircleShape).background(if (resizing) c.primary else c.muted.copy(alpha = 0.5f)))
                    Spacer(Modifier.width(10.dp))
                    Text(tr("طول الصفحة: ${page.height.roundToInt()} — اسحب للتعديل", "Page height: ${page.height.roundToInt()} — drag to resize"), style = Type.small, color = c.muted)
                }
            }
            if (!preview) {
                Spacer(Modifier.height(12.dp))
                Row(
                    Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(16.dp))
                        .drawBehind {
                            drawRoundRect(c.muted.copy(alpha = 0.5f), style = Stroke(2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f))), cornerRadius = CornerRadius(16.dp.toPx()))
                        }
                        .clickable { nav.navigate(Routes.library(id, page.id)) },
                    horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Rounded.Add, null, tint = c.primary, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(tr("أضف قطعة", "Add block"), style = Type.label, color = c.primary)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
        Box(Modifier.navigationBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp)) {
            PrimaryButton(tr("احفظ الصفحة", "Save page")) {
                Toast.makeText(ctx, "✓", Toast.LENGTH_SHORT).show()
                nav.navigate(Routes.export(id))
            }
        }
    }

    editing?.let { bid ->
        val b = page.blocks.firstOrNull { it.id == bid }
        if (b == null) editing = null
        else BlockEditSheet(
            b, p, onChange = { nb -> setBlocks(page.blocks.map { if (it.id == bid) nb else it }) }, onDismiss = { editing = null },
            onProject = { f -> vm.updateProject(id, f) }, onIdentity = { editing = null; nav.navigate(Routes.identity(id)) },
            onDraw = {
                val did = b.designId.ifEmpty { newId() }
                if (data.designs.none { it.id == did }) vm.saveDesign(
                    Design(id = did, name = tr0(lang, "قطعة حرّة", "Free block"), kind = BLOCK_DESIGN, w = 360f, h = 200f, bg = 0x00000000, els = emptyList()),
                )
                if (b.designId != did) setBlocks(page.blocks.map { if (it.id == bid) it.copy(designId = did) else it })
                editing = null
                nav.navigate(Routes.canvas(did))
            },
        )
    }

    saving?.let { b ->
        val name = if (b.kind == BlockKind.CUSTOM) Variants.getValue(b.kind)[b.variant.coerceIn(Variants.getValue(b.kind).indices)].t() else b.kind.label().t()
        SaveBlockDialog(
            defaultName = b.title.ifBlank { name }, defaultCategory = b.kind.name, onDismiss = { saving = null },
        ) { n, cat ->
            vm.saveBlock(savedFrom(b, data, n, cat)); saving = null
            android.widget.Toast.makeText(ctx, tr0(lang, "حُفظت في «القطع المحفوظة» بالمكتبة", "Saved to “Saved blocks” in the library"), android.widget.Toast.LENGTH_SHORT).show()
        }
    }
    if (addingPage) {
        val suggestions = listOf(tr("القائمة", "Menu"), tr("السلة", "Cart"), tr("حسابي", "Account"), tr("تسجيل الدخول", "Sign in"))
        val fallback = tr("صفحة جديدة", "New page")
        var name by remember { mutableStateOf(suggestions.firstOrNull { s -> p.pages.none { it.name == s } } ?: fallback) }
        AlertDialog(
            onDismissRequest = { addingPage = false },
            containerColor = c.surface,
            title = { Text(tr("صفحة جديدة", "New page"), style = Type.h3, color = c.text) },
            text = {
                Column {
                    OutlinedTextField(name, { name = it.take(20) }, singleLine = true, shape = RoundedCornerShape(12.dp))
                    Spacer(Modifier.height(10.dp))
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        suggestions.forEach { s -> Pill(s, c.lilac, c.onLilac, Modifier.clickable { name = s }) }
                    }
                }
            },
            confirmButton = {
                TextButton({
                    val np = if (name == tr0(lang, "تسجيل الدخول", "Sign in"))
                        Page(name = name, blocks = listOf(defaultBlock(BlockKind.HEADER, 4, p, lang), defaultBlock(BlockKind.FORM, 0, p, lang)))
                    else Page(name = name.ifBlank { "Page" })
                    vm.updateProject(id) { it.copy(pages = it.pages + np) }
                    pageId = np.id; addingPage = false
                    if (np.blocks.isEmpty()) nav.navigate(Routes.library(id, np.id))
                }) { Text(tr("إنشاء", "Create"), color = c.primary) }
            },
            dismissButton = { TextButton({ addingPage = false }) { Text(tr("إلغاء", "Cancel"), color = c.muted) } },
        )
    }

    pageDialog?.let { pg ->
        var name by remember(pg.id) { mutableStateOf(pg.name) }
        AlertDialog(
            onDismissRequest = { pageDialog = null },
            containerColor = c.surface,
            title = { Text(tr("الصفحة", "Page"), style = Type.h3, color = c.text) },
            text = { OutlinedTextField(name, { name = it.take(20) }, singleLine = true, shape = RoundedCornerShape(12.dp)) },
            confirmButton = {
                TextButton({
                    vm.updateProject(id) { pr -> pr.copy(pages = pr.pages.map { if (it.id == pg.id) it.copy(name = name.ifBlank { pg.name }) else it }) }
                    pageDialog = null
                }) { Text(tr("حفظ", "Save"), color = c.primary) }
            },
            dismissButton = {
                if (p.pages.size > 1) TextButton({
                    vm.updateProject(id) { pr -> pr.copy(pages = pr.pages.filter { it.id != pg.id }) }
                    if (pageId == pg.id) pageId = p.pages.first { it.id != pg.id }.id
                    pageDialog = null
                }) { Text(tr("حذف الصفحة", "Delete page"), color = c.danger) }
            },
        )
    }
}

@Composable
private fun BlockAction(label: String?, icon: androidx.compose.ui.graphics.vector.ImageVector, enabled: Boolean = true, onClick: () -> Unit) {
    val tint = if (enabled) Eyn.c.bg else Eyn.c.bg.copy(alpha = 0.35f)
    Row(
        Modifier.clip(RoundedCornerShape(8.dp)).clickable(enabled = enabled, onClick = onClick).padding(horizontal = 8.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(16.dp))
        if (label != null) {
            Spacer(Modifier.width(4.dp))
            Text(label, style = Type.small, color = tint)
        }
    }
}

// ============================================================================ 4 · edit block

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BlockEditSheet(
    b: Block, p: AppProject, onChange: (Block) -> Unit, onDismiss: () -> Unit,
    onProject: ((AppProject) -> AppProject) -> Unit, onIdentity: () -> Unit, onDraw: () -> Unit,
) {
    val c = Eyn.c
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val latest by rememberUpdatedState(b)
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) scope.launch { Photos.import(ctx, uri)?.let { onChange(latest.copy(image = it)) } }
    }
    val sheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val t = p.theme()
    val lang = LocalLang.current
    var pendingItem by remember { mutableStateOf(-1) }
    val itemPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        val i = pendingItem
        if (uri != null && i >= 0) scope.launch {
            Photos.import(ctx, uri)?.let { path ->
                val items = latest.itemsOr(lang).toMutableList()
                if (i < items.size) { items[i] = items[i].copy(image = path); onChange(latest.copy(items = items)) }
            }
        }
    }
    var iconFor by remember { mutableStateOf<Int?>(null) } // item index, or BLOCK_ICON for the block's own icon
    var payFor by remember { mutableStateOf<Int?>(null) }
    // statusBarsPadding keeps the sheet (and its handle) below the phone's status bar.
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheet, containerColor = c.surface, modifier = Modifier.statusBarsPadding()) {
        // Title and live preview stay pinned while the controls below scroll.
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
            Text(tr("تعديل: ", "Edit: ") + b.kind.label().t(), style = Type.h2, color = c.text)
            Spacer(Modifier.height(10.dp))
            BoxWithConstraints(
                Modifier.fillMaxWidth().heightIn(max = 220.dp).clip(RoundedCornerShape(16.dp)).background(t.bg).border(1.dp, c.outline, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Scaled(maxWidth.value / 360f) { BlockView(b, p) }
            }
            Spacer(Modifier.height(4.dp))
        }
        Column(Modifier.fillMaxWidth().weight(1f, fill = false).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).navigationBarsPadding()) {
            Label(tr("الشكل", "Style"))
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Variants.getValue(b.kind).forEachIndexed { i, name -> OptionChip(name.t(), b.variant == i) { onChange(b.copy(variant = i)) } }
            }

            if (b.kind == BlockKind.CUSTOM && b.variant == CV.FREE) {
                Label(tr("التصميم", "Design"))
                Text(
                    tr("ارسم هذه القطعة بحرية في الاستوديو: نصوص وأشكال وصور وأيقونات وأزرار بأي ترتيب. ارجع بعدها وستظهر هنا.",
                        "Draw this block freely in the studio: text, shapes, photos, icons and buttons, placed anywhere. Come back and it shows up here."),
                    style = Type.small, color = c.muted, modifier = Modifier.padding(bottom = 8.dp),
                )
                PrimaryButton(if (b.designId.isEmpty()) tr("ارسم القطعة", "Draw this block") else tr("عدّل الرسم", "Edit the drawing"), onClick = onDraw)
            }
            if (b.kind == BlockKind.HEADER && (b.variant == 0 || b.variant == 4) && p.applyIdentity) {
                // The header shows the project logo; let it be changed right here.
                Label(tr("الشعار", "Logo"))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (p.logoStyle == 0) {
                        Box(
                            Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).background(c.lilac).clickable { iconFor = LOGO_ICON },
                            contentAlignment = Alignment.Center,
                        ) { FontIcon(p.logoIcon, c.onLilac, 26f, style = p.iconStyle) }
                        Spacer(Modifier.width(10.dp))
                        OptionChip(tr("تغيير أيقونة الشعار", "Change logo icon"), false) { iconFor = LOGO_ICON }
                        Spacer(Modifier.width(8.dp))
                    }
                    OptionChip(tr("كل إعدادات الهوية", "All identity settings"), false) { onIdentity() }
                }
            }
            val fields = fieldsFor(b)
            fields.forEach { (label, which) ->
                Label(label)
                OutlinedTextField(
                    value = when (which) { 0 -> b.title; 1 -> b.subtitle; 2 -> b.button; 3 -> b.body; else -> b.price },
                    onValueChange = { v ->
                        onChange(when (which) { 0 -> b.copy(title = v); 1 -> b.copy(subtitle = v); 2 -> b.copy(button = v); 3 -> b.copy(body = v); else -> b.copy(price = v.take(12)) })
                    },
                    shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth(),
                    singleLine = !((b.kind == BlockKind.TEXT && which == 1) || which == 3),
                    placeholder = if (which == 2 && b.kind == BlockKind.CUSTOM) ({ Text(tr("اتركه فارغاً لإخفائه", "Leave empty to hide")) }) else null,
                )
            }
            if (b.kind == BlockKind.CUSTOM && CV.blockIcon(b.variant)) {
                Label(tr("الأيقونة", "Icon"))
                Box(
                    Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).background(c.lilac).clickable { iconFor = BLOCK_ICON },
                    contentAlignment = Alignment.Center,
                ) { FontIcon(b.icon.ifEmpty { "info" }, c.onLilac, 24f, style = p.iconStyle) }
            }
            val options = if (b.kind == BlockKind.CUSTOM) CV.options(b.variant) else emptyList()
            if (options.isNotEmpty()) {
                Label(tr("إظهار", "Show"))
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    options.forEach { (key, name) ->
                        OptionChip((if (b.shows(key)) "✓ " else "") + name.t(), b.shows(key)) {
                            onChange(b.copy(hidden = if (b.shows(key)) b.hidden + key else b.hidden - key))
                        }
                    }
                }
            }

            if (b.hasItems()) {
                val items = b.itemsOr(lang)
                fun setItems(list: List<BlockItem>, active: Int = b.active) = onChange(
                    b.copy(
                        items = list, active = active.coerceIn(0, (list.size - 1).coerceAtLeast(0)),
                        hidden = if (list.isEmpty()) b.hidden + NO_ITEMS else b.hidden - NO_ITEMS,
                    ),
                )
                Label(
                    when (b.kind) {
                        BlockKind.FOOTER -> if (b.variant == 0) tr("أزرار شريط التبويبات", "Tab bar items") else tr("الروابط", "Links")
                        BlockKind.LIST -> tr("صفوف القائمة", "List rows")
                        BlockKind.CATEGORIES -> tr("الأقسام", "Categories")
                        BlockKind.CARDS -> tr("المنتجات (الاسم والسعر والصورة)", "Products (name, price, photo)")
                        BlockKind.CUSTOM -> tr("العناصر (أيقونة ونص وسعر)", "Items (icon, text, price)")
                        BlockKind.HEADER -> if (b.variant == 5) tr("التبويبات", "Tabs") else tr("أيقونات الهيدر", "Header icons")
                        else -> tr("العناصر", "Items")
                    },
                )
                if (b.hasActiveItem()) Text(
                    tr("اضغط الدائرة لتحديد العنصر النشط في هذه الصفحة", "Tap the circle to mark the active item on this page"),
                    style = Type.small, color = c.muted, modifier = Modifier.padding(bottom = 6.dp),
                )
                items.forEachIndexed { i, item ->
                    Column(
                        Modifier.fillMaxWidth().padding(bottom = 8.dp).clip(RoundedCornerShape(14.dp)).background(c.surfaceAlt).padding(8.dp),
                    ) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        if (b.hasActiveItem()) {
                            val on = i == b.active
                            Box(
                                Modifier.size(26.dp).clip(CircleShape).border(2.dp, if (on) c.primary else c.outline, CircleShape).clickable { setItems(items, i) },
                                contentAlignment = Alignment.Center,
                            ) { if (on) Box(Modifier.size(12.dp).clip(CircleShape).background(c.primary)) }
                            Spacer(Modifier.width(8.dp))
                        }
                        if (b.itemsArePayments()) {
                            Box(
                                Modifier.size(64.dp, 48.dp).clip(RoundedCornerShape(12.dp)).background(c.surface).clickable { payFor = i }.padding(6.dp),
                                contentAlignment = Alignment.Center,
                            ) { Shapes.PaymentMark(item.icon, Modifier.fillMaxSize()) }
                            Spacer(Modifier.width(8.dp))
                        }
                        if (b.itemsHaveIcons()) {
                            Box(
                                Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(c.lilac).clickable { iconFor = i },
                                contentAlignment = Alignment.Center,
                            ) { FontIcon(item.icon.ifEmpty { "add" }, c.onLilac, 22f, style = p.iconStyle) }
                            Spacer(Modifier.width(8.dp))
                        }
                        if (b.itemsHaveImage()) {
                            Box(
                                Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(c.surface).border(1.dp, c.outline, RoundedCornerShape(12.dp)).clickable {
                                    pendingItem = i; itemPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                },
                                contentAlignment = Alignment.Center,
                            ) {
                                if (item.image.isNotEmpty()) PhotoImage(item.image, 0, Modifier.matchParentSize())
                                else Icon(Icons.Rounded.AddPhotoAlternate, null, tint = c.muted, modifier = Modifier.size(22.dp))
                            }
                            Spacer(Modifier.width(8.dp))
                        }
                        if (b.itemsHaveLabels()) OutlinedTextField(
                            item.label, { v -> setItems(items.toMutableList().also { it[i] = item.copy(label = v.take(30)) }) },
                            singleLine = true, shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f),
                            placeholder = { Text(tr("النص", "Text")) },
                        ) else Text(if (b.itemsArePayments()) Shapes.paymentName(item.icon).t() else item.icon, style = Type.small, color = c.muted, modifier = Modifier.weight(1f))
                        if (b.kind == BlockKind.CUSTOM && CV.on(b.variant)) Text(CV.onLabel(b.variant).t(), style = Type.small, color = c.muted)
                        if (b.kind == BlockKind.CUSTOM && CV.on(b.variant)) Switch(
                            item.on, { v -> setItems(items.toMutableList().also { it[i] = item.copy(on = v) }) },
                            modifier = Modifier.padding(horizontal = 4.dp),
                        )
                        if (items.size > 1 || !b.itemsHaveLabels()) Box(Modifier.size(40.dp).clip(CircleShape).clickable {
                            setItems(items.toMutableList().also { it.removeAt(i) }, if (b.active >= i && b.active > 0) b.active - 1 else b.active)
                        }, contentAlignment = Alignment.Center) { Icon(Icons.Rounded.Delete, null, tint = c.danger, modifier = Modifier.size(20.dp)) }
                    }
                    if (b.itemsHaveValue()) {
                        Spacer(Modifier.height(6.dp))
                        OutlinedTextField(
                            item.value, { v -> setItems(items.toMutableList().also { it[i] = item.copy(value = v.take(20)) }) },
                            singleLine = true, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    when (b.kind) {
                                        BlockKind.LIST -> tr("قيمة اختيارية (مثل السعر)", "Optional value (e.g. price)")
                                        BlockKind.CUSTOM -> CV.valueHint(b.variant).t()
                                        else -> tr("السعر", "Price")
                                    },
                                )
                            },
                        )
                    }
                    if (b.kind == BlockKind.CUSTOM && CV.note(b.variant)) {
                        Spacer(Modifier.height(6.dp))
                        OutlinedTextField(
                            item.note, { v -> setItems(items.toMutableList().also { it[i] = item.copy(note = v.take(200)) }) },
                            shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(), maxLines = 3,
                            placeholder = { Text(CV.noteHint(b.variant).t()) },
                        )
                    }
                    if (b.kind == BlockKind.CUSTOM && CV.qty(b.variant)) {
                        NumStepper(tr("الكمية", "Quantity"), item.qty.toFloat(), 1f, 99f) { v ->
                            setItems(items.toMutableList().also { it[i] = item.copy(qty = v.roundToInt()) })
                        }
                    }
                    }
                }
                val max = if (b.kind == BlockKind.CARDS || b.kind == BlockKind.CUSTOM) 12 else 6
                if (items.size < max) OptionChip(
                    when {
                        !b.itemsHaveLabels() -> tr("+ إضافة أيقونة", "+ Add icon")
                        b.kind == BlockKind.CARDS -> tr("+ إضافة منتج", "+ Add product")
                        else -> tr("+ إضافة عنصر", "+ Add item")
                    }, false,
                ) {
                    val label = if (b.itemsHaveLabels()) tr0(lang, "عنصر جديد", "New item") else ""
                    val icon = when { b.itemsArePayments() -> "card"; b.itemsHaveIcons() -> "star"; else -> "" }
                    setItems(items + BlockItem(label, icon, value = if (b.itemsHaveValue() && b.kind != BlockKind.LIST) "10" else ""))
                }
            }

            if (b.kind == BlockKind.HERO || b.kind == BlockKind.CARDS || (b.kind == BlockKind.CUSTOM && CV.blockImage(b.variant))) {
                Label(tr("صورة", "Photo"))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OptionChip(if (b.image.isEmpty()) tr("اختر صورة من الجوال", "Pick a photo") else tr("تغيير الصورة", "Change photo"), false) {
                        picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                    if (b.image.isNotEmpty()) OptionChip(tr("إزالة", "Remove"), false) { onChange(b.copy(image = "")) }
                }
            }

            Label(tr("الخلفية", "Background"))
            val pal = p.colors()
            SwatchRow(
                b.bg?.c() ?: Color.Transparent,
                { col -> onChange(b.copy(bg = if (col.alpha == 0f) null else col.argb())) },
                allowClear = true, extra = listOf(pal.surface, pal.primary, pal.accent, pal.bg),
            )

            Label(tr("المحاذاة", "Alignment"))
            Segmented(listOf(tr("بداية", "Start"), tr("وسط", "Center")), b.align, { onChange(b.copy(align = it)) }, Modifier.fillMaxWidth())

            Spacer(Modifier.height(12.dp))
            SliderRow(tr("المسافة الداخلية", "Inner spacing"), b.padding, 0f..40f, steps = 9) { onChange(b.copy(padding = it.roundToInt().toFloat())) }
            SliderRow(tr("استدارة الزوايا", "Corner radius"), b.radius, 0f..32f, steps = 7) { onChange(b.copy(radius = it.roundToInt().toFloat())) }
            NumStepper(tr("حجم النص", "Text size"), b.textScale * 100, 60f, 200f, step = 5f, suffix = "%") { onChange(b.copy(textScale = it / 100f)) }
            NumStepper(tr("العرض", "Width"), b.widthPct * 100, 20f, 100f, step = 5f, suffix = "%") { onChange(b.copy(widthPct = it / 100f)) }
            itemHeightLabel(b)?.let { label ->
                NumStepper(label, if (b.itemHeight > 0) b.itemHeight else defaultItemHeight(b), 24f, 400f, step = 4f) { onChange(b.copy(itemHeight = it)) }
            }
            NumStepper(tr("الارتفاع الأدنى للقطعة", "Block minimum height"), b.minHeight, 0f, 800f, step = 4f) { onChange(b.copy(minHeight = it)) }
            Label(tr("الموضع في الصفحة", "Position on page"))
            Segmented(
                listOf(tr("مع المحتوى", "With content"), tr("مثبّت في الأسفل", "Pinned to bottom")), if (b.pinBottom) 1 else 0,
                { onChange(b.copy(pinBottom = it == 1)) }, Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            PrimaryButton(tr("تم", "Done"), onClick = onDismiss)
            Spacer(Modifier.height(16.dp))
        }
    }
    iconFor?.let { i ->
        val items = b.itemsOr(lang)
        val current = when (i) { BLOCK_ICON -> b.icon; LOGO_ICON -> p.logoIcon; else -> items.getOrNull(i)?.icon.orEmpty() }
        IconPickerSheet(current, onDismiss = { iconFor = null }, style = p.iconStyle) { name ->
            when (i) {
                LOGO_ICON -> onProject { it.copy(logoIcon = name) }
                BLOCK_ICON -> onChange(b.copy(icon = name))
                else -> onChange(b.copy(items = items.toMutableList().also { if (i < it.size) it[i] = it[i].copy(icon = name) }))
            }
            iconFor = null
        }
    }
    payFor?.let { i ->
        PaymentPickerSheet(onDismiss = { payFor = null }) { kind ->
            val items = b.itemsOr(lang)
            onChange(
                b.copy(
                    items = items.toMutableList().also {
                        if (i < it.size) {
                            val old = it[i]
                            // Rename the row too when it still carries the previous method's name.
                            val rename = old.label.isBlank() || Shapes.payments.any { (k, n) -> old.label == n.get(lang) || k == old.icon && old.label.startsWith(n.get(lang)) }
                            it[i] = old.copy(icon = kind, label = if (rename && b.itemsHaveLabels()) Shapes.paymentName(kind).get(lang) else old.label)
                        }
                    },
                ),
            )
            payFor = null
        }
    }
}

private const val BLOCK_ICON = -1
private const val LOGO_ICON = -2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaymentPickerSheet(onDismiss: () -> Unit, onPick: (String) -> Unit) {
    val c = Eyn.c
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = c.surface, modifier = Modifier.statusBarsPadding()) {
        Column(Modifier.padding(horizontal = 20.dp).navigationBarsPadding().padding(bottom = 16.dp)) {
            Text(tr("اختر وسيلة الدفع", "Pick a payment method"), style = Type.h2, color = c.text)
            Spacer(Modifier.height(12.dp))
            Shapes.payments.chunked(3).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(bottom = 10.dp)) {
                    row.forEach { (k, name) ->
                        Column(
                            Modifier.weight(1f).clip(RoundedCornerShape(14.dp)).background(c.surfaceAlt).clickable { onPick(k) }.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Shapes.PaymentMark(k, Modifier.size(70.dp, 42.dp))
                            Spacer(Modifier.height(6.dp))
                            Text(name.t(), style = Type.small, color = c.text, maxLines = 1)
                        }
                    }
                    repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

/** Label for the per-item height control, or null when the block has no sized items. */
@Composable
private fun itemHeightLabel(b: Block): String? = when (b.kind) {
    BlockKind.BUTTON -> tr("ارتفاع الزر", "Button height")
    BlockKind.SEARCH -> tr("ارتفاع شريط البحث", "Search bar height")
    BlockKind.FORM -> tr("ارتفاع الحقول والزر", "Field & button height")
    BlockKind.CARDS -> tr("ارتفاع صورة البطاقة", "Card image height")
    BlockKind.HERO -> tr("ارتفاع البطاقة", "Card height")
    BlockKind.CATEGORIES -> if (b.variant == 0) tr("حجم الدائرة", "Circle size") else null
    BlockKind.LIST -> tr("ارتفاع الصف", "Row height")
    BlockKind.CUSTOM -> when (b.variant) {
        CV.ICON_ROW, CV.ICON_GRID -> tr("حجم الأيقونة", "Icon size")
        CV.CARD_GRID -> tr("ارتفاع صورة البطاقة", "Card image height")
        CV.ITEM_LIST, CV.TOGGLES, CV.PAY_METHODS -> tr("ارتفاع الصف", "Row height")
        CV.CART -> tr("حجم صورة المنتج", "Product photo size")
        CV.PAY_LOGOS -> tr("ارتفاع الشعارات", "Logo height")
        CV.PROMO, CV.ADD_BAR -> tr("ارتفاع الحقل والزر", "Field & button height")
        CV.BANNER, CV.PRODUCT -> tr("ارتفاع الصورة", "Image height")
        CV.PROFILE -> tr("حجم الصورة الشخصية", "Avatar size")
        CV.EMPTY -> tr("حجم الأيقونة", "Icon size")
        CV.ONBOARD -> tr("ارتفاع الصورة", "Image height")
        else -> null
    }
    else -> null
}

private fun defaultItemHeight(b: Block) = when (b.kind) {
    BlockKind.BUTTON -> 50f; BlockKind.FORM -> 48f; BlockKind.SEARCH -> 48f; BlockKind.CARDS -> 120f
    BlockKind.HERO -> 160f; BlockKind.CATEGORIES -> 54f; BlockKind.LIST -> 56f
    BlockKind.CUSTOM -> when (b.variant) {
        CV.ICON_ROW -> 52f; CV.ICON_GRID -> 56f; CV.CARD_GRID -> 84f; CV.CART -> 64f; CV.PAY_LOGOS -> 30f; CV.ADD_BAR -> 52f
        CV.BANNER -> 150f; CV.PRODUCT -> 220f; CV.PROFILE -> 60f; CV.EMPTY -> 96f; CV.ONBOARD -> 260f; else -> 56f
    }
    else -> 48f
}

/** Which text fields a block exposes: 0 title, 1 subtitle, 2 button. */
@Composable
private fun fieldsFor(b: Block): List<Pair<String, Int>> = when (b.kind) {
    BlockKind.HEADER -> listOfNotNull(tr("العنوان", "Title") to 0, if (b.variant == 2) tr("التحية", "Greeting") to 1 else null)
    BlockKind.HERO -> listOf(tr("العنوان", "Headline") to 0, tr("الوصف", "Description") to 1, tr("نص الزر", "Button text") to 2)
    BlockKind.SEARCH -> listOf(tr("نص البحث", "Placeholder") to 0)
    BlockKind.CATEGORIES, BlockKind.LIST -> listOf(tr("العنوان", "Title") to 0)
    BlockKind.CARDS -> listOf(tr("العنوان", "Title") to 0, tr("العملة", "Currency") to 1)
    BlockKind.TEXT -> listOf(tr("العنوان", "Title") to 0, tr("الفقرة", "Paragraph") to 1)
    BlockKind.FORM -> listOf(tr("العنوان", "Title") to 0, tr("نص الزر", "Button text") to 2)
    BlockKind.BUTTON -> listOfNotNull(tr("نص الزر", "Button text") to 2, if (b.variant == 1) tr("الزر الثانوي", "Secondary button") to 1 else null)
    BlockKind.FOOTER -> if (b.variant == 1) listOf(tr("العنوان", "Title") to 0, tr("حقوق النشر", "Copyright") to 1) else emptyList()
    BlockKind.CUSTOM -> {
        val title = tr("العنوان", "Title") to 0
        val currency = tr("العملة (تُضاف بعد الأسعار)", "Currency (after prices)") to 1
        when (b.variant) {
            CV.SUMMARY -> listOf(title, currency, tr("نص الزر", "Button text") to 2)
            CV.PAY_METHODS, CV.PAY_LOGOS, CV.STEPS, CV.TOGGLES, CV.REVIEWS, CV.STATS, CV.ICON_GRID -> listOf(title)
            CV.SECTION -> listOf(title, tr("سطر ثانوي", "Subtitle") to 1, tr("نص الرابط", "Link text") to 2)
            CV.PROMO -> listOf(title, tr("نص الحقل", "Field placeholder") to 1, tr("نص الزر", "Button text") to 2)
            CV.DIVIDER -> listOf(tr("نص في الوسط", "Middle text") to 0)
            CV.BANNER -> listOf(tr("نص على الصورة", "Text on image") to 0)
            CV.PRODUCT -> listOf(tr("اسم المنتج", "Product name") to 0, tr("السعر", "Price") to 4, currency, tr("الوصف", "Description") to 3, tr("نص الزر", "Button text") to 2)
            CV.PROFILE -> listOf(tr("الاسم", "Name") to 0, tr("البريد أو الوصف", "Email or detail") to 1, tr("نص الزر", "Button text") to 2)
            CV.ALERT -> listOf(title, tr("التفاصيل", "Details") to 3)
            CV.ADD_BAR -> listOf(tr("نص الزر", "Button text") to 2, tr("السعر", "Price") to 4, currency)
            CV.FREE -> emptyList()
            CV.NOTIFS, CV.FAQ, CV.TIMELINE, CV.COUNTDOWN -> listOf(title)
            CV.CHAT -> listOf(tr("نص حقل الكتابة", "Message field text") to 1)
            CV.CALENDAR -> listOf(tr("الشهر", "Month") to 0)
            CV.EMPTY -> listOf(title, tr("الوصف", "Description") to 3, tr("نص الزر", "Button text") to 2)
            CV.ONBOARD -> listOf(title, tr("الوصف", "Description") to 3, tr("نص الزر", "Button text") to 2, tr("رابط التخطي", "Skip link") to 1)
            CV.PRICING -> listOf(title, currency, tr("المدة (مثل / شهرياً)", "Period (e.g. / month)") to 3, tr("نص الزر", "Button text") to 2)
            else -> listOf(title, currency)
        }
    }
}
