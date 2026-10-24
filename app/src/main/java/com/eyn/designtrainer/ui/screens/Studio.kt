package com.eyn.designtrainer.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.eyn.designtrainer.builder.DesignThumb
import com.eyn.designtrainer.builder.Kind
import com.eyn.designtrainer.builder.LogoMark
import com.eyn.designtrainer.builder.LogoTemplates
import com.eyn.designtrainer.builder.PageView
import com.eyn.designtrainer.builder.Scaled
import com.eyn.designtrainer.builder.Templates
import com.eyn.designtrainer.builder.blankDesign
import com.eyn.designtrainer.builder.theme
import com.eyn.designtrainer.builder.toDesign
import com.eyn.designtrainer.data.AppProject
import com.eyn.designtrainer.i18n.Lang
import com.eyn.designtrainer.i18n.LocalLang
import com.eyn.designtrainer.i18n.tr
import com.eyn.designtrainer.ui.LocalData
import com.eyn.designtrainer.ui.LocalNav
import com.eyn.designtrainer.ui.LocalVm
import com.eyn.designtrainer.ui.Routes
import com.eyn.designtrainer.ui.components.ChipRow
import com.eyn.designtrainer.ui.components.PrimaryButton
import com.eyn.designtrainer.ui.components.SectionTitle
import com.eyn.designtrainer.ui.components.TopBar
import com.eyn.designtrainer.ui.components.bounceClick
import com.eyn.designtrainer.ui.components.appear
import com.eyn.designtrainer.ui.components.dotPattern
import com.eyn.designtrainer.ui.theme.Eyn
import com.eyn.designtrainer.ui.theme.Type

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StudioScreen() {
    val c = Eyn.c
    val d = LocalData.current
    val vm = LocalVm.current
    val nav = LocalNav.current
    val lang = LocalLang.current
    var kind by rememberSaveable { mutableIntStateOf(0) }
    var deleting by remember { mutableStateOf<Pair<String, Boolean>?>(null) } // id, isProject

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        TopBar(tr("الاستوديو", "Studio"), big = true)

        // ------------------------------------------------------------ build mode
        Column(
            Modifier.padding(horizontal = 20.dp).fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(c.hero)
                .dotPattern(Color.White.copy(alpha = 0.06f)).padding(20.dp),
        ) {
            Text(tr("وضع البناء بالقطع", "Build mode with blocks"), style = Type.h2, color = c.onHero)
            Spacer(Modifier.height(4.dp))
            Text(
                tr("صمّم هوية تطبيقك وصفحاته الأساسية من جوالك بقطع جاهزة", "Design your app's identity and main pages from your phone with ready blocks"),
                style = Type.small, color = c.heroMuted,
            )
            Spacer(Modifier.height(14.dp))
            Row(Modifier.horizontalScroll(rememberScrollState()), verticalAlignment = Alignment.CenterVertically) {
                listOf(tr("الهوية", "Identity"), tr("القطع", "Blocks"), tr("بناء الصفحة", "Page"), tr("التعديل", "Edit"), tr("التصدير", "Export"))
                    .forEachIndexed { i, s ->
                        if (i > 0) Icon(Icons.AutoMirrored.Rounded.ArrowForward, null, tint = c.heroMuted, modifier = Modifier.padding(horizontal = 4.dp).size(14.dp))
                        Row(
                            Modifier.clip(RoundedCornerShape(50)).background(Color.White.copy(alpha = 0.12f)).padding(start = 4.dp, end = 10.dp, top = 4.dp, bottom = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(Modifier.size(20.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
                                Text("${i + 1}", style = Type.small, color = c.hero)
                            }
                            Spacer(Modifier.width(6.dp))
                            Text(s, style = Type.small, color = Color.White)
                        }
                    }
            }
            Spacer(Modifier.height(16.dp))
            PrimaryButton(tr("مشروع تطبيق جديد", "New app project"), icon = Icons.Rounded.Add, container = Color.White, content = c.hero) {
                val p = AppProject(name = if (lang == Lang.AR) "قهوتي" else "My Cafe")
                vm.saveProject(p)
                nav.navigate(Routes.identity(p.id))
            }
        }

        if (d.projects.isNotEmpty()) {
            Box(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) { SectionTitle(tr("مشاريعي", "My projects")) }
            Row(Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                d.projects.forEach { p ->
                    Column(
                        Modifier.width(150.dp).clip(RoundedCornerShape(20.dp)).background(c.surface).border(1.dp, c.outline, RoundedCornerShape(20.dp))
                            .combinedClickable(
                                onClick = { if (p.pages.isEmpty()) nav.navigate(Routes.identity(p.id)) else nav.navigate(Routes.build(p.id)) },
                                onLongClick = { deleting = p.id to true },
                            ).padding(10.dp),
                    ) {
                        Box(Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(14.dp)).background(p.theme().bg)) {
                            p.pages.firstOrNull()?.let { pg -> Scaled(130f / 360f) { PageView(pg, p) } }
                                ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { LogoMark(p, 30.dp, showName = false) }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(p.name, style = Type.label, color = c.text, maxLines = 1)
                        Text(tr("${p.pages.size} صفحات", "${p.pages.size} pages"), style = Type.small, color = c.muted)
                    }
                }
            }
        }

        // ------------------------------------------------------------ quick templates
        Box(Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp)) {
            Column {
                SectionTitle(tr("قوالب جاهزة للتعديل", "Ready-to-edit templates"))
                Text(
                    tr("اسحب العناصر لتحريكها وغيّر الألوان والنصوص والأحجام والترتيب", "Drag elements to move them, change colors, text, sizes and order"),
                    style = Type.small, color = c.muted, modifier = Modifier.padding(bottom = 12.dp),
                )
            }
        }
        val kinds = listOf<Kind?>(null) + Kind.entries
        ChipRow(kinds.map { it?.label?.t() ?: tr("الكل", "All") }, kind, { kind = it }, Modifier.padding(bottom = 12.dp))
        val list = (Templates + LogoTemplates).filter { kinds[kind] == null || it.kind == kinds[kind] }
        Column(Modifier.padding(horizontal = 20.dp)) {
            val tiles: List<Any?> = (if (kinds[kind] == null) listOf<Any?>(null) else emptyList()) + list
            tiles.chunked(2).forEachIndexed { ri, row ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.appear(ri).padding(bottom = 12.dp)) {
                    row.forEach { item ->
                        Column(
                            Modifier.weight(1f).bounceClick {
                                val design = if (item is com.eyn.designtrainer.builder.Template) item.toDesign(lang) else blankDesign(lang)
                                vm.saveDesign(design)
                                nav.navigate(Routes.canvas(design.id))
                            }.clip(RoundedCornerShape(20.dp)).background(c.surface).border(1.dp, c.outline, RoundedCornerShape(20.dp)).padding(10.dp),
                        ) {
                            if (item is com.eyn.designtrainer.builder.Template) {
                                val preview = remember(item.id, lang) { item.toDesign(lang) }
                                DesignThumb(preview, height = 140)
                                Spacer(Modifier.height(8.dp))
                                Text(item.name.t(), style = Type.label, color = c.text, maxLines = 1)
                                Text(item.kind.label.t(), style = Type.small, color = c.muted)
                            } else {
                                Box(
                                    Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(14.dp)).background(c.surfaceAlt),
                                    contentAlignment = Alignment.Center,
                                ) { Icon(Icons.Rounded.Add, null, tint = c.muted, modifier = Modifier.size(36.dp)) }
                                Spacer(Modifier.height(8.dp))
                                Text(tr("لوحة فارغة", "Blank canvas"), style = Type.label, color = c.text)
                                Text("360 × 640", style = Type.small, color = c.muted)
                            }
                        }
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }

        // Free blocks keep their drawings as designs too; they belong to their project, not to this list.
        val mine = d.designs.filter { it.kind != com.eyn.designtrainer.builder.BLOCK_DESIGN }
        if (mine.isNotEmpty()) {
            Box(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) { SectionTitle(tr("تصاميمي", "My designs")) }
            Row(Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                mine.forEach { ds ->
                    Column(
                        Modifier.width(150.dp).clip(RoundedCornerShape(20.dp)).background(c.surface).border(1.dp, c.outline, RoundedCornerShape(20.dp))
                            .combinedClickable(onClick = { nav.navigate(Routes.canvas(ds.id)) }, onLongClick = { deleting = ds.id to false })
                            .padding(10.dp),
                    ) {
                        BoxWithConstraints { DesignThumb(ds, height = 110) }
                        Spacer(Modifier.height(8.dp))
                        Text(ds.name, style = Type.label, color = c.text, maxLines = 1)
                        Text("${ds.els.size} " + tr("عناصر", "elements"), style = Type.small, color = c.muted)
                    }
                }
            }
            Text(
                tr("اضغط مطولاً على أي تصميم أو مشروع لحذفه", "Long-press a design or project to delete it"),
                style = Type.small, color = c.muted, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            )
        }
        Spacer(Modifier.height(24.dp))
    }

    deleting?.let { (id, isProject) ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            containerColor = c.surface,
            title = { Text(tr("حذف؟", "Delete?"), style = Type.h3, color = c.text) },
            text = { Text(tr("لا يمكن التراجع عن الحذف.", "This can't be undone."), style = Type.body, color = c.muted) },
            confirmButton = {
                TextButton({ if (isProject) vm.deleteProject(id) else vm.deleteDesign(id); deleting = null }) { Text(tr("حذف", "Delete"), color = c.danger) }
            },
            dismissButton = { TextButton({ deleting = null }) { Text(tr("إلغاء", "Cancel"), color = c.muted) } },
        )
    }
}
