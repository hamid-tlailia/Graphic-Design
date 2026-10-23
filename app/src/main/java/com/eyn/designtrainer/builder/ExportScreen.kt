package com.eyn.designtrainer.builder

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Collections
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.unit.dp
import com.eyn.designtrainer.data.AppProject
import com.eyn.designtrainer.data.Page
import com.eyn.designtrainer.i18n.LocalLang
import com.eyn.designtrainer.i18n.tr
import com.eyn.designtrainer.ui.LocalData
import com.eyn.designtrainer.ui.LocalNav
import com.eyn.designtrainer.ui.LocalVm
import com.eyn.designtrainer.ui.components.EynCard
import com.eyn.designtrainer.ui.components.IconTile
import com.eyn.designtrainer.ui.components.Pill
import com.eyn.designtrainer.ui.components.PrimaryButton
import com.eyn.designtrainer.ui.components.TopBar
import com.eyn.designtrainer.ui.components.bounceClick
import com.eyn.designtrainer.ui.theme.Eyn
import com.eyn.designtrainer.ui.theme.Type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun ExportScreen(id: String) {
    val c = Eyn.c
    val vm = LocalVm.current
    val nav = LocalNav.current
    val ctx = LocalContext.current
    val lang = LocalLang.current
    val p: AppProject = LocalData.current.projects.firstOrNull { it.id == id } ?: return
    val layers = remember { mutableMapOf<String, GraphicsLayer>() }
    val scope = rememberCoroutineScope()
    var busy by remember { mutableStateOf(false) }

    suspend fun shots(): List<Bitmap> = p.pages.mapNotNull { pg -> layers[pg.id]?.let { Exporter.capture(it) } }

    fun run(block: suspend () -> Unit) {
        if (busy) return
        busy = true
        scope.launch {
            runCatching { block() }.onFailure { Toast.makeText(ctx, it.message ?: "Error", Toast.LENGTH_LONG).show() }
            busy = false
        }
    }

    val boardLayer = rememberGraphicsLayer()
    suspend fun boardBitmap(): Bitmap = Exporter.capture(boardLayer)

    Column(Modifier.fillMaxSize()) {
        TopBar(tr("القوالب والتصدير", "Templates & export"), onBack = { nav.popBackStack() }) {
            Pill(p.name, c.accentSoft, c.accent)
        }
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            Text(tr("الصفحات", "Pages"), style = Type.h3, color = c.text)
            Spacer(Modifier.height(10.dp))
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(
                    Modifier.width(110.dp).height(210.dp).clip(RoundedCornerShape(18.dp))
                        .border(1.5.dp, c.outline, RoundedCornerShape(18.dp)).clickable { nav.popBackStack() },
                    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
                ) {
                    Icon(Icons.Rounded.Add, null, tint = c.muted, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.height(4.dp))
                    Text(tr("قالب جديد", "New page"), style = Type.small, color = c.muted)
                }
                p.pages.forEach { pg ->
                    PageThumb(pg, p, onLayer = { layers[pg.id] = it }, onGone = { layers.remove(pg.id) }) {
                        nav.previousBackStackEntry?.savedStateHandle?.set("selectPage", pg.id)
                        nav.popBackStack()
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(tr("لوحة العرض", "Presentation board"), style = Type.h3, color = c.text)
            Text(tr("صورة واحدة فيها الهوية والألوان والخط وكل الصفحات، 4 في كل صف", "One image with identity, colors, type and every page, 4 per row"), style = Type.small, color = c.muted)
            Spacer(Modifier.height(10.dp))
            BoxWithConstraints(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).border(1.dp, c.outline, RoundedCornerShape(18.dp))) {
                // Lay the board out at density 2 (a 3360px-wide export) and scale it down to fit here.
                val density = LocalDensity.current
                val boardPx = BOARD_WIDTH * 2f
                val scale = with(density) { maxWidth.toPx() } / boardPx
                Scaled(scale, designWidth = with(density) { boardPx.toDp() }) {
                    Box(Modifier.drawWithContent { boardLayer.record { this@drawWithContent.drawContent() }; drawLayer(boardLayer) }) {
                        CompositionLocalProvider(LocalDensity provides Density(2f, 1f)) { PresentationBoard(p) }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            EynCard(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(tr("طبّق الهوية على كل الصفحات", "Apply identity to all pages"), style = Type.label, color = c.text)
                        Text(tr("الألوان والخط والشعار", "Colors, font and logo"), style = Type.small, color = c.muted)
                    }
                    Switch(
                        p.applyIdentity, { v -> vm.updateProject(id) { it.copy(applyIdentity = v) } },
                        colors = SwitchDefaults.colors(checkedTrackColor = c.primary, checkedThumbColor = c.onPrimary),
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            Text(tr("التصدير", "Export"), style = Type.h3, color = c.text)
            Spacer(Modifier.height(10.dp))
            EynCard(Modifier.fillMaxWidth()) {
                ExportRow(Icons.Rounded.Dashboard, tr("لوحة عرض كاملة", "Full presentation board"), "PNG · PDF") {
                    run {
                        val b = boardBitmap()
                        val png = Exporter.png(ctx, b, p.name + "_board")
                        val pdf = Exporter.pdf(ctx, listOf(b), p.name + "_board")
                        Exporter.share(ctx, listOf(png, pdf), "*/*")
                    }
                }
                ExportRow(Icons.Rounded.Collections, tr("صورة لكل شاشة", "A screenshot of every page"), "PNG") {
                    run {
                        val files = shots().mapIndexed { i, b -> Exporter.png(ctx, b, "${p.name}_${i + 1}_${p.pages[i].name}") }
                        Exporter.share(ctx, files, "image/png")
                    }
                }
                ExportRow(Icons.Rounded.Code, tr("كود HTML / CSS", "HTML / CSS code"), tr("للمتقدمين", "Advanced")) {
                    run { Exporter.share(ctx, listOf(Exporter.text(ctx, CodeGen.html(p, lang), "index.html")), "text/html") }
                }
                ExportRow(Icons.Rounded.PhoneAndroid, tr("كود React Native", "React Native code"), tr("للمتقدمين", "Advanced"), last = true) {
                    run { Exporter.share(ctx, listOf(Exporter.text(ctx, CodeGen.reactNative(p, lang), "App.js")), "text/plain") }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
        Box(Modifier.navigationBarsPadding().padding(20.dp)) {
            PrimaryButton(if (busy) tr("جارٍ التصدير…", "Exporting…") else tr("صدّر المشروع", "Export project"), enabled = !busy) {
                run {
                    val b = boardBitmap()
                    val files = mutableListOf<File>(Exporter.png(ctx, b, p.name + "_board"))
                    files += Exporter.pdf(ctx, listOf(b) + shots(), p.name)
                    files += Exporter.text(ctx, CodeGen.html(p, lang), "index.html")
                    files += Exporter.text(ctx, CodeGen.reactNative(p, lang), "App.js")
                    Exporter.share(ctx, files, "*/*")
                }
            }
        }
    }
}

@Composable
private fun PageThumb(page: Page, p: AppProject, onLayer: (GraphicsLayer) -> Unit, onGone: () -> Unit, onClick: () -> Unit) {
    val c = Eyn.c
    val layer = rememberGraphicsLayer()
    DisposableEffect(layer) { onLayer(layer); onDispose { onGone() } }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier.width(110.dp).height(210.dp).bounceClick(onClick = onClick).clip(RoundedCornerShape(18.dp))
                .background(p.theme().bg).border(1.dp, c.outline, RoundedCornerShape(18.dp)),
        ) {
            // Full-size page is recorded for export, then drawn scaled down.
            Scaled(110f / 360f) {
                Box(
                    Modifier.drawWithContent {
                        layer.record { this@drawWithContent.drawContent() }
                        drawLayer(layer)
                    },
                ) { PageView(page, p, minHeight = 640.dp) }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(page.name, style = Type.small, color = c.text)
    }
}

@Composable
private fun ExportRow(icon: ImageVector, title: String, tag: String, last: Boolean = false, onClick: () -> Unit) {
    val c = Eyn.c
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable(onClick = onClick).padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconTile(icon, c.lilac, c.onLilac, size = 38.dp, radius = 12.dp, iconSize = 19.dp)
        Spacer(Modifier.width(12.dp))
        Text(title, style = Type.label, color = c.text, modifier = Modifier.weight(1f))
        Pill(tag, if (tag.contains("PNG")) c.surfaceAlt else c.successSoft, if (tag.contains("PNG")) c.muted else c.success, style = Type.small)
    }
    if (!last) Box(Modifier.fillMaxWidth().height(1.dp).background(c.outline))
}
