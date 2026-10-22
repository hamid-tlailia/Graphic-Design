package com.eyn.designtrainer.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Contrast
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SettingsEthernet
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.VerticalAlignBottom
import androidx.compose.material.icons.rounded.VerticalAlignCenter
import androidx.compose.material.icons.rounded.VerticalAlignTop
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eyn.designtrainer.data.ColorPair
import com.eyn.designtrainer.data.argb
import com.eyn.designtrainer.data.c
import com.eyn.designtrainer.i18n.L
import com.eyn.designtrainer.i18n.tr
import com.eyn.designtrainer.ui.LocalData
import com.eyn.designtrainer.ui.LocalNav
import com.eyn.designtrainer.ui.LocalVm
import com.eyn.designtrainer.ui.Routes
import com.eyn.designtrainer.ui.components.ColorDialog
import com.eyn.designtrainer.ui.components.EynCard
import com.eyn.designtrainer.ui.components.IconTile
import com.eyn.designtrainer.ui.components.Pill
import com.eyn.designtrainer.ui.components.PrimaryButton
import com.eyn.designtrainer.ui.components.SquareIconButton
import com.eyn.designtrainer.ui.components.TopBar
import com.eyn.designtrainer.ui.components.bounceClick
import com.eyn.designtrainer.ui.components.dotPattern
import com.eyn.designtrainer.ui.components.hex
import com.eyn.designtrainer.ui.theme.Cairo
import com.eyn.designtrainer.ui.theme.Eyn
import com.eyn.designtrainer.ui.theme.Plex
import com.eyn.designtrainer.ui.theme.Tajawal
import com.eyn.designtrainer.ui.theme.Type
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

// ===================================================================== hub

@Composable
fun PracticeHubScreen() {
    val c = Eyn.c
    val nav = LocalNav.current
    val d = LocalData.current
    data class Tool(val icon: ImageVector, val tint: Color, val bg: Color, val title: String, val desc: String, val route: String)
    val tools = listOf(
        Tool(Icons.Rounded.SettingsEthernet, c.primary, c.lilac, tr("مدرّب المسافات", "Spacing trainer"), tr("عدّل الحشوة والفجوة حتى تتوازن البطاقة", "Tune padding and gaps until the card balances"), Routes.SPACING),
        Tool(Icons.Rounded.Contrast, c.accent, c.accentSoft, tr("فاحص التباين", "Contrast checker"), tr("اختبر مقروئية أي لونين حسب WCAG", "Test any two colors against WCAG"), Routes.CONTRAST),
        Tool(Icons.Rounded.TextFields, c.success, c.successSoft, tr("مختبر الخطوط", "Font lab"), tr("قارن الخطوط العربية على نصك", "Compare Arabic fonts on your own text"), Routes.FONTS),
        Tool(Icons.Rounded.Dashboard, c.primary, c.lilac, tr("استوديو البناء", "Build studio"), tr("قوالب جاهزة: هيدر، فوتر، دخول، بطاقات وأزرار", "Ready templates: headers, footers, login, cards, buttons"), Routes.STUDIO),
    )
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        TopBar(tr("تطبيق عملي", "Practice"), big = true)
        Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                tr("نجحت في ${d.practiceWins} تمارين عملية", "You've passed ${d.practiceWins} practice rounds"),
                style = Type.body, color = c.muted,
            )
            tools.forEach { t ->
                EynCard(Modifier.fillMaxWidth(), onClick = {
                    if (t.route == Routes.STUDIO) nav.navigate(t.route) { launchSingleTop = true; popUpTo(Routes.HOME) { saveState = true }; restoreState = true }
                    else nav.navigate(t.route)
                }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconTile(t.icon, t.bg, t.tint, size = 52.dp, radius = 16.dp)
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(t.title, style = Type.h3, color = c.text)
                            Text(t.desc, style = Type.small, color = c.muted)
                        }
                        Icon(Icons.AutoMirrored.Rounded.ArrowForward, null, tint = c.muted, modifier = Modifier.size(18.dp))
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

// ===================================================================== spacing trainer

@Composable
private fun MeasureTag(v: Int, modifier: Modifier = Modifier) {
    Text(
        "$v", style = Type.small.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), color = Color.White,
        modifier = modifier.clip(RoundedCornerShape(4.dp)).background(Color(0xFFE5484D)).padding(horizontal = 5.dp, vertical = 1.dp),
    )
}

@Composable
fun SpacingPracticeScreen() {
    val c = Eyn.c
    val vm = LocalVm.current
    val nav = LocalNav.current
    var round by rememberSaveable { mutableIntStateOf(1) }
    var pad by rememberSaveable(round) { mutableFloatStateOf(listOf(2f, 6f, 30f, 10f).random()) }
    var gap by rememberSaveable(round) { mutableFloatStateOf(listOf(2f, 22f, 30f, 14f).random()) }
    var align by rememberSaveable(round) { mutableIntStateOf(Random.nextInt(0, 3).let { if (it == 1) 0 else it }) }
    var result by remember(round) { mutableStateOf<List<L>?>(null) }
    val p = pad.toInt()
    val g = gap.toInt()
    val animPad by animateDpAsState(p.dp, label = "pad")
    val animGap by animateDpAsState(g.dp, label = "gap")

    Column(Modifier.fillMaxSize()) {
        TopBar(tr("تطبيق عملي", "Practice"), onBack = { nav.popBackStack() }) {
            Text("$round / 6", style = Type.label, color = c.muted)
        }
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            Text(tr("عدّل المسافات حتى تتوازن البطاقة", "Adjust spacing until the card balances"), style = Type.h3, color = c.text)
            Spacer(Modifier.height(16.dp))
            Box(
                Modifier.fillMaxWidth().height(230.dp).clip(RoundedCornerShape(20.dp)).background(c.surface)
                    .border(1.dp, c.outline, RoundedCornerShape(20.dp)).dotPattern(c.muted.copy(alpha = 0.25f), 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box {
                    Row(
                        Modifier.width(250.dp).height(120.dp).clip(RoundedCornerShape(14.dp)).background(c.surfaceAlt)
                            .border(1.5.dp, Color(0xFF3B82F6), RoundedCornerShape(14.dp)).padding(animPad),
                        verticalAlignment = when (align) { 0 -> Alignment.Top; 1 -> Alignment.CenterVertically; else -> Alignment.Bottom },
                    ) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                            Box(Modifier.fillMaxWidth(0.8f).height(9.dp).clip(CircleShape).background(c.primary))
                            Box(Modifier.fillMaxWidth().height(7.dp).clip(CircleShape).background(c.muted.copy(alpha = 0.4f)))
                            Box(Modifier.fillMaxWidth(0.6f).height(7.dp).clip(CircleShape).background(c.muted.copy(alpha = 0.4f)))
                        }
                        Box(Modifier.width(animGap).height(10.dp).wrapContentSize(unbounded = true)) { MeasureTag(g) }
                        Box(Modifier.size(56.dp).clip(RoundedCornerShape(10.dp)).background(c.lilac))
                    }
                    MeasureTag(p, Modifier.align(Alignment.TopCenter).offset(y = (-10).dp))
                }
            }
            Spacer(Modifier.height(16.dp))
            EynCard(Modifier.fillMaxWidth()) {
                SliderRow(tr("الحشوة الداخلية", "Inner padding"), pad, 0f..32f, 15) { pad = it; result = null }
                SliderRow(tr("الفجوة", "Gap"), gap, 0f..32f, 15) { gap = it; result = null }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(tr("المحاذاة", "Alignment"), style = Type.label, color = c.text, modifier = Modifier.weight(1f))
                    listOf(Icons.Rounded.VerticalAlignTop, Icons.Rounded.VerticalAlignCenter, Icons.Rounded.VerticalAlignBottom).forEachIndexed { i, ic ->
                        val on = align == i
                        val bg by animateColorAsState(if (on) c.lilac else c.surfaceAlt, label = "al")
                        Box(
                            Modifier.padding(start = 6.dp).size(38.dp).clip(RoundedCornerShape(10.dp)).background(bg)
                                .bounceClick { align = i; result = null },
                            contentAlignment = Alignment.Center,
                        ) { Icon(ic, null, tint = if (on) c.onLilac else c.muted, modifier = Modifier.size(20.dp)) }
                    }
                }
            }
            AnimatedVisibility(result != null, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
                val issues = result.orEmpty()
                val ok = issues.isEmpty()
                Row(
                    Modifier.padding(top = 12.dp).fillMaxWidth().clip(RoundedCornerShape(16.dp))
                        .background(if (ok) c.successSoft else c.dangerSoft).padding(14.dp),
                ) {
                    Icon(if (ok) Icons.Rounded.Check else Icons.Rounded.Close, null, tint = if (ok) c.success else c.danger)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        if (ok) Text(tr("ممتاز! تصميم متوازن (+10 نقاط)", "Excellent! A balanced design (+10 points)"), style = Type.label, color = c.success)
                        issues.forEach { Text("• " + it.t(), style = Type.small, color = c.danger) }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
        Box(Modifier.navigationBarsPadding().padding(20.dp)) {
            if (result?.isEmpty() == true) {
                PrimaryButton(tr("الجولة التالية", "Next round"), icon = Icons.Rounded.Refresh) { round = round % 6 + 1 }
            } else {
                PrimaryButton(tr("تحقّق من التصميم", "Check design")) {
                    val issues = buildList {
                        if (p % 8 != 0 && p % 4 != 0) add(L("الحشوة ليست من مضاعفات 4 أو 8", "Padding isn't a multiple of 4 or 8"))
                        if (g % 4 != 0) add(L("الفجوة ليست من مضاعفات 4", "Gap isn't a multiple of 4"))
                        if (p !in 12..24) add(L("الحشوة المريحة للبطاقات بين 12 و24", "Comfortable card padding is 12–24"))
                        if (g >= p) add(L("الفجوة يجب أن تكون أصغر من الحشوة", "The gap should be smaller than the padding"))
                        if (g < 8) add(L("العناصر ملتصقة: الفجوة أقل من 8", "Items are cramped: gap below 8"))
                        if (align != 1) add(L("وسّط العناصر عمودياً لتوازن الصورة مع النص", "Center items vertically to balance image and text"))
                    }
                    result = issues
                    if (issues.isEmpty()) vm.practiceWin()
                }
            }
        }
    }
}

@Composable
fun SliderRow(label: String, value: Float, range: ClosedFloatingPointRange<Float>, steps: Int = 0, suffix: String = "px", onChange: (Float) -> Unit) {
    val c = Eyn.c
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = Type.label, color = c.text, modifier = Modifier.weight(1f))
            Text("${value.toInt()}$suffix", style = Type.label.copy(fontWeight = FontWeight.SemiBold), color = c.primary)
        }
        Slider(
            value, onChange, valueRange = range, steps = steps,
            colors = SliderDefaults.colors(thumbColor = c.primary, activeTrackColor = c.primary, inactiveTrackColor = c.lilac, activeTickColor = Color.Transparent, inactiveTickColor = Color.Transparent),
        )
    }
}

// ===================================================================== contrast

fun contrastRatio(a: Color, b: Color): Double {
    val la = a.luminance() + 0.05
    val lb = b.luminance() + 0.05
    return max(la, lb) / min(la, lb)
}

@Composable
fun ContrastScreen() {
    val c = Eyn.c
    val vm = LocalVm.current
    val nav = LocalNav.current
    val d = LocalData.current
    var fg by rememberSaveable { mutableStateOf(0xFF2A2470L) }
    var bg by rememberSaveable { mutableStateOf(0xFFF7F6F2L) }
    var editing by remember { mutableStateOf<Int?>(null) }
    var saved by remember { mutableStateOf(false) }
    val ratio = contrastRatio(fg.c(), bg.c())
    val shown by animateFloatAsState(ratio.toFloat(), label = "ratio")

    Column(Modifier.fillMaxSize()) {
        TopBar(tr("فاحص التباين", "Contrast checker"), onBack = { nav.popBackStack() }) {
            SquareIconButton(Icons.Rounded.SwapVert, { val t = fg; fg = bg; bg = t; saved = false })
        }
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val previewBg by animateColorAsState(bg.c(), label = "pbg")
            val previewFg by animateColorAsState(fg.c(), label = "pfg")
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(previewBg)
                    .border(1.dp, c.outline, RoundedCornerShape(20.dp)).padding(20.dp),
            ) {
                Text(tr("التصميم الجيد مقروء للجميع", "Good design is readable for everyone"), style = Type.h2, color = previewFg)
                Spacer(Modifier.height(6.dp))
                Text(tr("نص عادي بحجم 12 لاختبار الوضوح", "Regular 12pt text to test clarity"), style = Type.small, color = previewFg)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(tr("النص", "Text") to fg, tr("الخلفية", "Background") to bg).forEachIndexed { i, (label, v) ->
                    EynCard(Modifier.weight(1f), onClick = { editing = i }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(label, style = Type.small, color = c.muted)
                                Text(v.c().hex(), style = Type.label.copy(fontWeight = FontWeight.Bold), color = c.text)
                            }
                            Box(Modifier.size(32.dp).clip(RoundedCornerShape(10.dp)).background(v.c()).border(1.dp, c.outline, RoundedCornerShape(10.dp)))
                        }
                    }
                }
            }
            EynCard(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("1 : " + "%.1f".format(shown), style = Type.h1.copy(fontSize = 38.sp, lineHeight = 46.sp), color = c.primary, modifier = Modifier.weight(1f))
                    val (verdict, vc, vbg) = when {
                        ratio >= 7 -> Triple(tr("ممتاز", "Excellent"), c.success, c.successSoft)
                        ratio >= 4.5 -> Triple(tr("جيد", "Good"), c.success, c.successSoft)
                        ratio >= 3 -> Triple(tr("للنص الكبير فقط", "Large text only"), c.accent, c.accentSoft)
                        else -> Triple(tr("ضعيف", "Fails"), c.danger, c.dangerSoft)
                    }
                    Pill(verdict, vbg, vc)
                }
                Spacer(Modifier.height(12.dp))
                val checks = listOf(
                    tr("AA نص عادي", "AA normal") to (ratio >= 4.5), tr("AAA نص عادي", "AAA normal") to (ratio >= 7),
                    tr("AA نص كبير", "AA large") to (ratio >= 3), tr("AAA نص كبير", "AAA large") to (ratio >= 4.5),
                )
                checks.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 8.dp)) {
                        row.forEach { (label, pass) ->
                            Row(
                                Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(c.surfaceAlt).padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(if (pass) Icons.Rounded.Check else Icons.Rounded.Close, null, tint = if (pass) c.success else c.danger, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(label, style = Type.small, color = c.text)
                            }
                        }
                    }
                }
            }
            AnimatedVisibility(ratio < 4.5, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(c.dangerSoft).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Close, null, tint = c.danger, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        tr("التباين %.1f : 1 لا يجتاز معيار النص العادي. اختر لوناً أغمق للنص أو أفتح للخلفية.", "Contrast %.1f : 1 fails for normal text. Pick a darker text or lighter background.").format(ratio),
                        style = Type.small, color = c.danger,
                    )
                }
            }
            Text(tr("جرّب أمثلة", "Try examples"), style = Type.label, color = c.muted)
            val examples = listOf(0xFF2A2470L to 0xFFF7F6F2L, 0xFFAAAAAAL to 0xFFFFFFFFL, 0xFFFFFFFFL to 0xFFF2603FL, 0xFFFFFFFFL to 0xFF2A2470L, 0xFF2E9E6AL to 0xFFFFFFFFL) +
                d.savedPairs.map { it.fg to it.bg }
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                examples.distinct().forEach { (f, b) ->
                    Box(
                        Modifier.size(56.dp).bounceClick { fg = f; bg = b; saved = false }.clip(RoundedCornerShape(14.dp)).background(b.c())
                            .border(1.dp, c.outline, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center,
                    ) { Text("Aa", style = Type.h3, color = f.c()) }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
        Box(Modifier.navigationBarsPadding().padding(20.dp)) {
            PrimaryButton(if (saved) tr("تم الحفظ ✓", "Saved ✓") else tr("احفظ في لوحتي", "Save to my palette")) {
                vm.savePair(ColorPair(fg, bg)); saved = true
            }
        }
    }
    editing?.let { which ->
        ColorDialog((if (which == 0) fg else bg).c(), onDismiss = { editing = null }) {
            if (which == 0) fg = it.argb() else bg = it.argb()
            editing = null; saved = false
        }
    }
}

// ===================================================================== font lab

data class FontInfo(val name: String, val family: FontFamily, val desc: L)

val LabFonts = listOf(
    FontInfo("IBM Plex Sans Arabic", Plex, L("عصري وواضح للواجهات", "Modern and clear for interfaces")),
    FontInfo("Cairo", Cairo, L("قوي ومناسب للعناوين", "Strong, great for headings")),
    FontInfo("Tajawal", Tajawal, L("خفيف ومريح للنصوص الطويلة", "Light and comfortable for long text")),
)

@Composable
fun FontLabScreen() {
    val c = Eyn.c
    val nav = LocalNav.current
    val defaultText = tr("التصميم الجيد غير مرئي", "Good design is invisible")
    var text by rememberSaveable { mutableStateOf("") }
    var chosen by rememberSaveable { mutableIntStateOf(0) }
    var size by rememberSaveable { mutableFloatStateOf(26f) }
    var bold by rememberSaveable { mutableStateOf(true) }
    var tune by rememberSaveable { mutableStateOf(false) }
    val sample = text.ifBlank { defaultText }

    Column(Modifier.fillMaxSize()) {
        TopBar(tr("مختبر الخطوط", "Font lab"), onBack = { nav.popBackStack() }) {
            SquareIconButton(Icons.Rounded.Tune, { tune = !tune }, tint = if (tune) c.accent else c.text)
        }
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                text, { text = it }, singleLine = true,
                label = { Text(tr("النص", "Text")) }, placeholder = { Text(defaultText) },
                shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth(),
            )
            AnimatedVisibility(tune, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
                EynCard(Modifier.fillMaxWidth()) {
                    SliderRow(tr("الحجم", "Size"), size, 12f..48f, suffix = "sp") { size = it }
                    com.eyn.designtrainer.ui.components.Segmented(
                        listOf(tr("عادي", "Regular"), tr("عريض", "Bold")), if (bold) 1 else 0, { bold = it == 1 }, Modifier.fillMaxWidth(),
                    )
                }
            }
            LabFonts.forEachIndexed { i, f ->
                val on = i == chosen
                EynCard(
                    Modifier.fillMaxWidth(), onClick = { chosen = i },
                    border = BorderStroke(if (on) 2.dp else 1.dp, if (on) c.primary else c.outline),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(f.name, style = Type.label, color = c.text, modifier = Modifier.weight(1f))
                        if (on) Pill(tr("مختار", "Chosen"), c.lilac, c.onLilac, style = Type.small)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        sample, fontFamily = f.family, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
                        fontSize = size.sp, lineHeight = (size * 1.5f).sp, color = c.text,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(f.desc.t(), style = Type.small, color = c.muted)
                }
            }
            EynCard(Modifier.fillMaxWidth(), color = c.lilac, border = null) {
                Text(tr("نصيحة الاقتران", "Pairing tip"), style = Type.label, color = c.onLilac)
                Spacer(Modifier.height(4.dp))
                Text(
                    tr("استخدم Cairo العريض للعناوين مع Tajawal أو IBM Plex للنصوص. خطان يكفيان دائماً.", "Use bold Cairo for headings with Tajawal or IBM Plex for body text. Two fonts are always enough."),
                    style = Type.small, color = c.onLilac.copy(alpha = 0.8f), textAlign = TextAlign.Start,
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
