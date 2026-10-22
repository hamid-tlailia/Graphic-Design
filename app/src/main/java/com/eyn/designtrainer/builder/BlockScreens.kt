package com.eyn.designtrainer.builder

import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.eyn.designtrainer.data.AppProject
import com.eyn.designtrainer.data.Block
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
import com.eyn.designtrainer.ui.components.SwatchRow
import com.eyn.designtrainer.ui.components.TopBar
import com.eyn.designtrainer.ui.components.bounceClick
import com.eyn.designtrainer.ui.screens.SliderRow
import com.eyn.designtrainer.ui.theme.Eyn
import com.eyn.designtrainer.ui.theme.Type
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
private fun OptionChip(label: String, on: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
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

    Column(Modifier.fillMaxSize()) {
        TopBar(tr("هوية تطبيقك", "Your app identity"), onBack = { nav.popBackStack() }) {
            if (isNew) Text("1 / 3", style = Type.label, color = c.muted)
        }
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            Label(tr("اسم التطبيق", "App name"))
            OutlinedTextField(
                p.name, { v -> set { it.copy(name = v.take(24)) } }, singleLine = true,
                shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(16.dp))
            val t = p.theme()
            Box(
                Modifier.fillMaxWidth().height(130.dp).clip(RoundedCornerShape(24.dp)).background(t.surface).animateContentSize(),
                contentAlignment = Alignment.Center,
            ) { LogoMark(p, 60.dp) }

            Label(tr("شكل الشعار", "Logo style"))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(tr("أيقونة + نص", "Icon + text"), tr("حرف", "Letter"), tr("نص فقط", "Text only")).forEachIndexed { i, l ->
                    OptionChip(l, p.logoStyle == i, Modifier.weight(1f)) { set { it.copy(logoStyle = i) } }
                }
            }
            AnimatedVisibility(p.logoStyle == 0, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
                Row(Modifier.padding(top = 12.dp).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LogoIcons.forEach { k ->
                        val on = p.logoIcon == k
                        Box(
                            Modifier.size(46.dp).bounceClick { set { it.copy(logoIcon = k) } }.clip(RoundedCornerShape(14.dp))
                                .background(if (on) t.primary else c.surface).border(1.dp, c.outline, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center,
                        ) { Icon(CanvasIcons.getValue(k), null, tint = if (on) t.onPrimary else c.muted, modifier = Modifier.size(22.dp)) }
                    }
                }
            }

            Label(tr("الألوان", "Colors"))
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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

            Label(tr("الخط", "Font"))
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
}

private fun tr0(lang: com.eyn.designtrainer.i18n.Lang, ar: String, en: String) = if (lang == com.eyn.designtrainer.i18n.Lang.AR) ar else en

// ============================================================================ 2 · block library

@Composable
fun BlockLibraryScreen(id: String, pageId: String) {
    val c = Eyn.c
    val vm = LocalVm.current
    val nav = LocalNav.current
    val lang = LocalLang.current
    val p = rememberProject(id) ?: return
    var kindIdx by rememberSaveable { mutableStateOf(0) }
    val kinds = BlockKind.entries
    val picked = remember { mutableStateListOf<Pair<BlockKind, Int>>() }

    Column(Modifier.fillMaxSize()) {
        TopBar(tr("مكتبة القطع", "Block library"), onBack = null) {
            SquareIconButton(Icons.Rounded.Close, { nav.popBackStack() })
        }
        ChipRow(kinds.map { it.label().t() }, kindIdx, { kindIdx = it }, Modifier.padding(bottom = 8.dp))
        val kind = kinds[kindIdx]
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            Variants.getValue(kind).withIndex().chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(bottom = 12.dp)) {
                    row.forEach { (v, name) ->
                        val key = kind to v
                        val on = key in picked
                        Column(Modifier.weight(1f)) {
                            Box(
                                Modifier.fillMaxWidth().height(118.dp).bounceClick { if (on) picked.remove(key) else picked.add(key) }
                                    .clip(RoundedCornerShape(18.dp)).background(c.surface)
                                    .border(if (on) 2.dp else 1.dp, if (on) c.primary else c.outline, RoundedCornerShape(18.dp)),
                                contentAlignment = Alignment.Center,
                            ) {
                                BoxWithConstraints(Modifier.fillMaxSize().padding(8.dp), contentAlignment = Alignment.Center) {
                                    val scale = (maxWidth.value / 360f)
                                    Box(Modifier.clip(RoundedCornerShape(8.dp)).background(p.theme().bg)) {
                                        Scaled(scale) { BlockView(defaultBlock(kind, v, p, lang), p) }
                                    }
                                }
                                androidx.compose.animation.AnimatedVisibility(on, Modifier.align(Alignment.BottomStart).padding(8.dp), enter = scaleIn() + fadeIn(), exit = scaleOut() + fadeOut()) {
                                    Box(Modifier.size(24.dp).clip(CircleShape).background(c.primary), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Rounded.Check, null, tint = c.onPrimary, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                            Text(name.t(), style = Type.small, color = c.text, modifier = Modifier.padding(top = 6.dp, start = 4.dp))
                        }
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(12.dp))
        }
        Box(Modifier.navigationBarsPadding().padding(20.dp)) {
            PrimaryButton(
                if (picked.isEmpty()) tr("اختر قطعة أو أكثر", "Pick one or more blocks") else tr("أضف إلى الصفحة (${picked.size})", "Add to page (${picked.size})"),
                icon = Icons.Rounded.Add, enabled = picked.isNotEmpty(),
            ) {
                val blocks = picked.map { (k, v) -> defaultBlock(k, v, p, lang) }
                vm.updateProject(id) { pr -> pr.copy(pages = pr.pages.map { if (it.id == pageId) it.copy(blocks = it.blocks + blocks) else it }) }
                nav.previousBackStackEntry?.savedStateHandle?.set("selectPage", pageId)
                nav.popBackStack()
            }
        }
    }
}

// ============================================================================ 3 · page builder

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PageBuilderScreen(id: String) {
    val c = Eyn.c
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
    var preview by rememberSaveable { mutableStateOf(false) }
    var pageDialog by remember { mutableStateOf<Page?>(null) }
    var addingPage by remember { mutableStateOf(false) }

    fun setBlocks(list: List<Block>) = vm.updateProject(id) { pr -> pr.copy(pages = pr.pages.map { if (it.id == page.id) it.copy(blocks = list) else it }) }

    // Drag-to-reorder state: a working copy while the finger is down.
    var working by remember { mutableStateOf<List<Block>?>(null) }
    var dragId by remember { mutableStateOf<String?>(null) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val heights = remember { mutableStateMapOf<String, Int>() }
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

        Column(Modifier.weight(1f).verticalScroll(rememberScrollState(), enabled = dragId == null).padding(horizontal = 16.dp, vertical = 8.dp)) {
            val t = p.theme()
            Column(
                Modifier.fillMaxWidth().heightIn(min = 420.dp).clip(RoundedCornerShape(28.dp)).background(t.bg)
                    .border(6.dp, c.text.copy(alpha = 0.85f), RoundedCornerShape(28.dp)).padding(vertical = 14.dp, horizontal = 6.dp),
            ) {
                if (blocks.isEmpty()) {
                    Text(
                        tr("الصفحة فارغة. أضف قطعاً من المكتبة.", "This page is empty. Add blocks from the library."),
                        style = Type.body, color = c.muted, modifier = Modifier.padding(24.dp).align(Alignment.CenterHorizontally),
                    )
                }
                blocks.forEach { b ->
                    val isSel = b.id == selected && !preview
                    val isDrag = b.id == dragId
                    Column(
                        Modifier.zIndex(if (isDrag) 1f else 0f).onSizeChanged { heights[b.id] = it.height }
                            .graphicsLayer {
                                translationY = if (isDrag) dragOffset else 0f
                                shadowElevation = if (isDrag) 18f else 0f
                                scaleX = if (isDrag) 1.02f else 1f; scaleY = scaleX
                            },
                    ) {
                        AnimatedVisibility(isSel, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
                            Row(
                                Modifier.padding(start = 8.dp, bottom = 4.dp).clip(RoundedCornerShape(10.dp)).background(c.text).padding(horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                BlockAction(tr("تعديل", "Edit"), Icons.Rounded.Edit) { editing = b.id }
                                BlockAction(tr("تكرار", "Duplicate"), Icons.Rounded.ContentCopy) {
                                    val i = blocks.indexOf(b); setBlocks(blocks.toMutableList().apply { add(i + 1, b.copy(id = newId())) })
                                }
                                BlockAction(null, Icons.Rounded.KeyboardArrowUp) {
                                    val i = blocks.indexOf(b); if (i > 0) setBlocks(blocks.toMutableList().apply { add(i - 1, removeAt(i)) })
                                }
                                BlockAction(null, Icons.Rounded.KeyboardArrowDown) {
                                    val i = blocks.indexOf(b); if (i < blocks.lastIndex) setBlocks(blocks.toMutableList().apply { add(i + 1, removeAt(i)) })
                                }
                                BlockAction(tr("حذف", "Delete"), Icons.Rounded.Delete) { setBlocks(blocks - b); selected = null }
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.weight(1f).clip(RoundedCornerShape(14.dp))
                                    .border(if (isSel) 2.dp else 0.dp, if (isSel) c.primary else Color.Transparent, RoundedCornerShape(14.dp))
                                    .clickable(enabled = !preview) { selected = if (selected == b.id) null else b.id },
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
                                        val i = list.indexOfFirst { it.id == dragId }
                                        if (dragOffset > 0 && i < list.lastIndex) {
                                            val h = heights[list[i + 1].id] ?: return@detectDragGestures
                                            if (dragOffset > h / 2f) { working = list.toMutableList().apply { add(i + 1, removeAt(i)) }; dragOffset -= h }
                                        } else if (dragOffset < 0 && i > 0) {
                                            val h = heights[list[i - 1].id] ?: return@detectDragGestures
                                            if (-dragOffset > h / 2f) { working = list.toMutableList().apply { add(i - 1, removeAt(i)) }; dragOffset += h }
                                        }
                                    }
                                },
                                contentAlignment = Alignment.Center,
                            ) { Icon(Icons.Rounded.DragIndicator, null, tint = c.muted.copy(alpha = 0.7f), modifier = Modifier.size(20.dp)) }
                        }
                    }
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
        else BlockEditSheet(b, p, onChange = { nb -> setBlocks(page.blocks.map { if (it.id == bid) nb else it }) }, onDismiss = { editing = null })
    }

    if (addingPage) {
        val suggestions = listOf(tr("القائمة", "Menu"), tr("السلة", "Cart"), tr("حسابي", "Account"), tr("تسجيل الدخول", "Sign in"))
        val fallback = tr("صفحة جديدة", "New page")
        var name by remember { mutableStateOf(suggestions.firstOrNull { s -> p.pages.none { it.name == s } } ?: fallback) }
        val lang = LocalLang.current
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
private fun BlockAction(label: String?, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Row(
        Modifier.clip(RoundedCornerShape(8.dp)).clickable(onClick = onClick).padding(horizontal = 8.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = Eyn.c.bg, modifier = Modifier.size(16.dp))
        if (label != null) {
            Spacer(Modifier.width(4.dp))
            Text(label, style = Type.small, color = Eyn.c.bg)
        }
    }
}

// ============================================================================ 4 · edit block

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BlockEditSheet(b: Block, p: AppProject, onChange: (Block) -> Unit, onDismiss: () -> Unit) {
    val c = Eyn.c
    val sheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val t = p.theme()
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheet, containerColor = c.surface) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).navigationBarsPadding()) {
            Text(tr("تعديل: ", "Edit: ") + b.kind.label().t(), style = Type.h2, color = c.text)
            Spacer(Modifier.height(12.dp))
            BoxWithConstraints(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(t.bg).border(1.dp, c.outline, RoundedCornerShape(16.dp))) {
                Scaled(maxWidth.value / 360f) { BlockView(b, p) }
            }

            Label(tr("الشكل", "Style"))
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Variants.getValue(b.kind).forEachIndexed { i, name -> OptionChip(name.t(), b.variant == i) { onChange(b.copy(variant = i)) } }
            }

            val fields = fieldsFor(b)
            fields.forEach { (label, which) ->
                Label(label)
                OutlinedTextField(
                    value = when (which) { 0 -> b.title; 1 -> b.subtitle; else -> b.button },
                    onValueChange = { v -> onChange(when (which) { 0 -> b.copy(title = v); 1 -> b.copy(subtitle = v); else -> b.copy(button = v) }) },
                    shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth(),
                    singleLine = !(b.kind == BlockKind.TEXT && which == 1),
                )
            }

            Label(tr("الخلفية", "Background"))
            val pal = Palettes[p.palette.coerceIn(Palettes.indices)]
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
            Spacer(Modifier.height(12.dp))
            PrimaryButton(tr("تم", "Done"), onClick = onDismiss)
            Spacer(Modifier.height(16.dp))
        }
    }
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
}
