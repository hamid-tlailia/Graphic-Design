package com.eyn.designtrainer.builder

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eyn.designtrainer.data.Design
import com.eyn.designtrainer.i18n.tr
import com.eyn.designtrainer.ui.components.OutlineButton
import com.eyn.designtrainer.ui.components.Pill
import com.eyn.designtrainer.ui.components.PrimaryButton
import com.eyn.designtrainer.ui.components.Segmented
import com.eyn.designtrainer.ui.theme.Eyn
import com.eyn.designtrainer.ui.theme.Type
import kotlin.math.roundToInt

/** "Rate my design": score, verdict, issues grouped by area, with one-tap fixes. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewSheet(state: EditorState, onDismiss: () -> Unit, onShow: (String) -> Unit) {
    val c = Eyn.c
    val result = remember(state.design) { DesignReview.review(state.design) }
    val ring = remember { Animatable(0f) }
    LaunchedEffect(result.score) { ring.animateTo(result.score / 100f, tween(900)) }
    val tone = when {
        result.score >= 85 -> c.success
        result.score >= 65 -> c.accent
        else -> c.danger
    }
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), containerColor = c.surface) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).navigationBarsPadding()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(96.dp), contentAlignment = Alignment.Center) {
                    Canvas(Modifier.size(96.dp)) {
                        drawArc(c.outline, -90f, 360f, false, style = Stroke(10.dp.toPx(), cap = StrokeCap.Round))
                        drawArc(tone, -90f, 360f * ring.value, false, style = Stroke(10.dp.toPx(), cap = StrokeCap.Round))
                    }
                    Text("${(ring.value * 100).roundToInt()}", style = Type.h1.copy(fontSize = 30.sp), color = tone)
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(tr("تقييم التصميم", "Design review"), style = Type.small, color = c.muted)
                    Text(result.verdict.t(), style = Type.h2, color = c.text)
                    Text(
                        if (result.issues.isEmpty()) tr("لا توجد ملاحظات. يمكنك اعتماده.", "No notes. You can approve it.")
                        else tr("${result.issues.size} ملاحظات للتحسين", "${result.issues.size} things to improve"),
                        style = Type.small, color = c.muted,
                    )
                }
            }
            val fixable = result.issues.filter { it.fix != null }
            if (fixable.isNotEmpty()) {
                Spacer(Modifier.height(14.dp))
                PrimaryButton(tr("إصلاح تلقائي للكل (${fixable.size})", "Auto-fix all (${fixable.size})"), icon = Icons.Rounded.AutoFixHigh) {
                    state.edit { d -> fixable.fold(d) { acc, issue -> issue.fix!!.invoke(acc) } }
                }
            }
            DesignReview.Area.entries.forEach { area ->
                val list = result.issues.filter { it.area == area }
                if (list.isEmpty()) return@forEach
                Text(area.label.t(), style = Type.h3, color = c.text, modifier = Modifier.padding(top = 18.dp, bottom = 8.dp))
                list.forEach { issue ->
                    Column(
                        Modifier.fillMaxWidth().padding(bottom = 8.dp).clip(RoundedCornerShape(16.dp)).background(c.surfaceAlt).padding(14.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(issue.title.t(), style = Type.label, color = c.text, modifier = Modifier.weight(1f))
                            Pill("−${issue.weight}", if (issue.weight >= 6) c.dangerSoft else c.accentSoft, if (issue.weight >= 6) c.danger else c.accent, style = Type.small)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(issue.detail.t(), style = Type.small, color = c.muted)
                        Row(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            issue.elementId?.let { id ->
                                Text(
                                    tr("عرض العنصر", "Show element"), style = Type.label, color = c.primary,
                                    modifier = Modifier.clip(RoundedCornerShape(10.dp)).border(1.dp, c.outline, RoundedCornerShape(10.dp))
                                        .clickable { onShow(id) }.padding(horizontal = 12.dp, vertical = 7.dp),
                                )
                            }
                            issue.fix?.let { fix ->
                                Text(
                                    tr("إصلاح", "Fix"), style = Type.label, color = c.onLilac,
                                    modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(c.lilac)
                                        .clickable { state.edit { d -> fix(d) } }.padding(horizontal = 14.dp, vertical = 7.dp),
                                )
                            }
                        }
                    }
                }
            }
            if (result.passed.isNotEmpty()) {
                Text(tr("نقاط القوة", "What works"), style = Type.h3, color = c.text, modifier = Modifier.padding(top = 18.dp, bottom = 8.dp))
                result.passed.forEach {
                    Row(Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Check, null, tint = c.success, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(it.t(), style = Type.body, color = c.text)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

/** Export options: format, resolution (HD up to 4K) and transparent background. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportSheet(design: Design, onDismiss: () -> Unit, onSaveBlock: () -> Unit, onExport: (widthPx: Int, transparent: Boolean, jpeg: Boolean, gallery: Boolean) -> Unit) {
    val c = Eyn.c
    var size by remember { mutableIntStateOf(1) }
    var format by remember { mutableIntStateOf(0) }
    var transparent by remember { mutableStateOf(design.kind == "LOGO") }
    val widths = listOf(0, 2048, 4096)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), containerColor = c.surface) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp).navigationBarsPadding()) {
            Text(tr("تصدير التصميم", "Export design"), style = Type.h2, color = c.text)
            Spacer(Modifier.height(16.dp))
            Text(tr("الدقة", "Resolution"), style = Type.label, color = c.muted)
            Spacer(Modifier.height(8.dp))
            Segmented(listOf(tr("عادية", "Standard"), "HD 2K", "4K"), size, { size = it }, Modifier.fillMaxWidth())
            val h = if (widths[size] == 0) 0 else (widths[size] * design.h / design.w).roundToInt()
            Text(
                if (widths[size] == 0) tr("بدقة الشاشة", "At screen resolution") else "${widths[size]} × $h px",
                style = Type.small, color = c.muted, modifier = Modifier.padding(top = 6.dp),
            )
            Spacer(Modifier.height(14.dp))
            Text(tr("الصيغة", "Format"), style = Type.label, color = c.muted)
            Spacer(Modifier.height(8.dp))
            Segmented(listOf("PNG", "JPG"), format, { format = it; if (it == 1) transparent = false }, Modifier.fillMaxWidth())
            Spacer(Modifier.height(14.dp))
            Text(tr("الخلفية", "Background"), style = Type.label, color = c.muted)
            Spacer(Modifier.height(8.dp))
            Segmented(
                listOf(tr("مع الخلفية", "With background"), tr("شفافة (للشعارات)", "Transparent (logos)")), if (transparent) 1 else 0,
                { transparent = it == 1 && format == 0 }, Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(20.dp))
            PrimaryButton(tr("حفظ في المعرض", "Save to gallery"), icon = Icons.Rounded.Download) { onExport(widths[size], transparent, format == 1, true) }
            Spacer(Modifier.height(10.dp))
            OutlineButton(tr("مشاركة", "Share"), icon = Icons.Rounded.Share) { onExport(widths[size], transparent, format == 1, false) }
            Spacer(Modifier.height(10.dp))
            OutlineButton(tr("احفظه كقطعة لمشاريعي", "Save as a block for my projects"), icon = Icons.Rounded.BookmarkAdd) { onSaveBlock() }
            Spacer(Modifier.height(20.dp))
        }
    }
}
