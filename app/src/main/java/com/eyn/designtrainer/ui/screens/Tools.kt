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
import androidx.compose.material.icons.rounded.FormatAlignCenter
import androidx.compose.material.icons.rounded.FormatAlignLeft
import androidx.compose.material.icons.rounded.FormatAlignRight
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
import androidx.compose.ui.unit.Dp
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
import com.eyn.designtrainer.ui.goTab
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
                    if (t.route == Routes.STUDIO) nav.goTab(t.route)
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

/** One spacing exercise: what the two sliders and the alignment control mean, and what passes. */
private data class Drill(
    val title: L,
    val goal: L,
    val padLabel: L,
    val gapLabel: L,
    val vertical: Boolean,        // alignment control: vertical (top/center/bottom) or horizontal (start/center/end)
    val padOk: IntRange,
    val gapOk: IntRange,
    val alignOk: Int,
    val start: Triple<Int, Int, Int>,
    val tip: L,
)

private val Drills = listOf(
    Drill(
        L("بطاقة منتج", "Product card"), L("حشوة 16 حول المحتوى، فجوة 12 بين الصورة والنص، والعناصر في المنتصف عمودياً.", "16 padding around the content, 12 gap between image and text, items centered vertically."),
        L("الحشوة الداخلية", "Inner padding"), L("الفجوة", "Gap"), true, 16..16, 12..12, 1, Triple(4, 26, 0),
        L("الحشوة أكبر من الفجوة فتبدو العناصر مجموعة واحدة داخل البطاقة.", "Padding larger than the gap keeps items grouped inside the card."),
    ),
    Drill(
        L("زر مع أيقونة", "Icon button"), L("حشوة أفقية 24 داخل الزر، فجوة 8 بين الأيقونة والنص، والمحتوى في الوسط.", "24 horizontal padding inside the button, 8 between icon and label, content centered."),
        L("الحشوة الأفقية", "Horizontal padding"), L("فجوة الأيقونة", "Icon gap"), false, 20..24, 8..8, 1, Triple(6, 20, 0),
        L("الأيقونة والنص وحدة واحدة: فجوة صغيرة بينهما وهواء أكبر حولهما.", "Icon and label are one unit: a small gap between, more air around."),
    ),
    Drill(
        L("بطاقة عمودية", "Vertical card"), L("حشوة 16، فجوة 8 بين الصورة والعنوان، والنص محاذى لبداية السطر.", "16 padding, 8 between image and title, text aligned to the start."),
        L("الحشوة الداخلية", "Inner padding"), L("فجوة الصورة", "Image gap"), false, 16..16, 8..8, 0, Triple(30, 2, 1),
        L("النصوص الطويلة تُحاذى للبداية؛ التوسيط يناسب العناوين القصيرة فقط.", "Long text aligns to the start; centering suits short headings only."),
    ),
    Drill(
        L("قائمة أشخاص", "People list"), L("مسافة عمودية 12 لكل صف، فجوة 12 بين الصورة والاسم، والعناصر في منتصف الصف.", "12 vertical padding per row, 12 between avatar and name, items centered in the row."),
        L("حشوة الصف", "Row padding"), L("فجوة الصورة", "Avatar gap"), true, 12..12, 12..16, 1, Triple(2, 4, 0),
        L("الصفوف المتكررة تحتاج إيقاعاً ثابتاً؛ حشوة متساوية تجعل القائمة سهلة المسح.", "Repeated rows need a steady rhythm; equal padding makes lists easy to scan."),
    ),
    Drill(
        L("حقل نموذج", "Form field"), L("فجوة 4 إلى 8 بين العنوان وحقله (قانون التقارب)، حشوة 16 داخل الحقل، ومحاذاة للبداية.", "4–8 between label and its field (proximity), 16 padding inside the field, start aligned."),
        L("حشوة الحقل", "Field padding"), L("عنوان ↔ حقل", "Label ↔ field"), false, 16..16, 4..8, 0, Triple(4, 24, 1),
        L("العنوان الملتصق بحقله يُفهم فوراً أنه يصفه.", "A label close to its field is instantly read as describing it."),
    ),
    Drill(
        L("قسم رئيسي", "Hero section"), L("هوامش واسعة 24 إلى 32، فجوة 16 بين العنوان والزر، والمحتوى في الوسط.", "Generous 24–32 margins, 16 between headline and button, content centered."),
        L("الهوامش", "Margins"), L("فجوة الزر", "Button gap"), false, 24..32, 16..16, 1, Triple(8, 4, 0),
        L("المساحة البيضاء حول العنصر الأهم تمنحه حضوراً وفخامة.", "White space around the key element gives it presence."),
    ),
)

@Composable
private fun DrillPreview(index: Int, pad: Dp, gap: Dp, align: Int, p: Int, g: Int) {
    val c = Eyn.c
    val line = @Composable { frac: Float, h: Dp, col: Color -> Box(Modifier.fillMaxWidth(frac).height(h).clip(CircleShape).background(col)) }
    val hAlign = when (align) { 0 -> Alignment.Start; 1 -> Alignment.CenterHorizontally; else -> Alignment.End }
    val frame = Modifier.clip(RoundedCornerShape(14.dp)).background(c.surfaceAlt).border(1.5.dp, Color(0xFF3B82F6), RoundedCornerShape(14.dp))
    Box {
        when (index) {
            0 -> Row(
                frame.width(250.dp).height(120.dp).padding(pad),
                verticalAlignment = when (align) { 0 -> Alignment.Top; 1 -> Alignment.CenterVertically; else -> Alignment.Bottom },
            ) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    line(0.8f, 9.dp, c.primary); line(1f, 7.dp, c.muted.copy(alpha = 0.4f)); line(0.6f, 7.dp, c.muted.copy(alpha = 0.4f))
                }
                Box(Modifier.width(gap).height(10.dp).wrapContentSize(unbounded = true)) { MeasureTag(g) }
                Box(Modifier.size(56.dp).clip(RoundedCornerShape(10.dp)).background(c.lilac))
            }
            1 -> Row(
                Modifier.width(240.dp).height(56.dp).clip(RoundedCornerShape(16.dp)).background(c.primary).padding(horizontal = pad),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = when (align) { 0 -> Arrangement.Start; 1 -> Arrangement.Center; else -> Arrangement.End },
            ) {
                Icon(Icons.Rounded.Refresh, null, tint = c.onPrimary, modifier = Modifier.size(20.dp))
                Box(Modifier.width(gap).height(10.dp).wrapContentSize(unbounded = true)) { MeasureTag(g) }
                Text(tr("أضف إلى السلة", "Add to cart"), style = Type.button, color = c.onPrimary, maxLines = 1)
            }
            2 -> Column(frame.width(190.dp).padding(pad), horizontalAlignment = hAlign) {
                Box(Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(10.dp)).background(c.lilac))
                Box(Modifier.height(gap).width(10.dp).wrapContentSize(unbounded = true)) { MeasureTag(g) }
                Text(tr("كرسي بسيط", "Minimal chair"), style = Type.h3, color = c.text)
                Text(tr("قماش ناعم وخشب بلوط", "Soft fabric, oak legs"), style = Type.small, color = c.muted)
            }
            3 -> Column(frame.width(250.dp)) {
                repeat(3) { r ->
                    Row(
                        Modifier.fillMaxWidth().height(pad * 2 + 40.dp).padding(horizontal = 12.dp),
                        verticalAlignment = when (align) { 0 -> Alignment.Top; 1 -> Alignment.CenterVertically; else -> Alignment.Bottom },
                    ) {
                        Box(Modifier.size(40.dp).clip(CircleShape).background(listOf(c.lilac, c.accentSoft, c.successSoft)[r]))
                        Box(Modifier.width(gap).height(10.dp).wrapContentSize(unbounded = true)) { if (r == 0) MeasureTag(g) }
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) { line(0.6f, 8.dp, c.text.copy(alpha = 0.7f)); line(0.4f, 6.dp, c.muted.copy(alpha = 0.4f)) }
                    }
                    if (r < 2) Box(Modifier.fillMaxWidth().height(1.dp).background(c.outline))
                }
            }
            4 -> Column(Modifier.width(250.dp), horizontalAlignment = hAlign) {
                Text(tr("البريد الإلكتروني", "Email"), style = Type.label, color = c.text)
                Box(Modifier.height(gap).width(10.dp).wrapContentSize(unbounded = true)) { MeasureTag(g) }
                Box(
                    Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(12.dp)).background(c.surface).border(1.dp, c.outline, RoundedCornerShape(12.dp)).padding(horizontal = pad),
                    contentAlignment = when (align) { 0 -> Alignment.CenterStart; 1 -> Alignment.Center; else -> Alignment.CenterEnd },
                ) { Text("name@mail.com", style = Type.body, color = c.muted) }
                Spacer(Modifier.height(20.dp))
                Text(tr("كلمة المرور", "Password"), style = Type.label, color = c.text)
                Spacer(Modifier.height(gap))
                Box(Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(12.dp)).background(c.surface).border(1.dp, c.outline, RoundedCornerShape(12.dp)))
            }
            else -> Column(frame.width(260.dp).padding(pad), horizontalAlignment = hAlign) {
                Text(tr("تصميم يتحدث عن نفسه", "Design that speaks"), style = Type.h2, color = c.text, textAlign = when (align) { 1 -> TextAlign.Center; 2 -> TextAlign.End; else -> TextAlign.Start })
                Box(Modifier.height(gap).width(10.dp).wrapContentSize(unbounded = true)) { MeasureTag(g) }
                Box(Modifier.clip(RoundedCornerShape(12.dp)).background(c.accent).padding(horizontal = 18.dp, vertical = 10.dp)) {
                    Text(tr("ابدأ الآن", "Start now"), style = Type.label, color = Color.White)
                }
            }
        }
        if (index != 1 && index != 3) MeasureTag(p, Modifier.align(Alignment.TopCenter).offset(y = (-10).dp))
        else MeasureTag(p, Modifier.align(Alignment.CenterStart).offset(x = (-10).dp))
    }
}

@Composable
fun SpacingPracticeScreen() {
    val c = Eyn.c
    val vm = LocalVm.current
    val nav = LocalNav.current
    var round by rememberSaveable { mutableIntStateOf(0) }
    var finished by rememberSaveable { mutableStateOf(false) }
    val drill = Drills[round]
    var pad by rememberSaveable(round) { mutableFloatStateOf(drill.start.first.toFloat()) }
    var gap by rememberSaveable(round) { mutableFloatStateOf(drill.start.second.toFloat()) }
    var align by rememberSaveable(round) { mutableIntStateOf(drill.start.third) }
    var result by remember(round) { mutableStateOf<List<L>?>(null) }
    val p = pad.toInt()
    val g = gap.toInt()
    val animPad by animateDpAsState(p.dp, label = "pad")
    val animGap by animateDpAsState(g.dp, label = "gap")

    Column(Modifier.fillMaxSize()) {
        TopBar(tr("تطبيق عملي", "Practice"), onBack = { nav.popBackStack() }) {
            Text("${round + 1} / ${Drills.size}", style = Type.label, color = c.muted)
        }
        com.eyn.designtrainer.ui.components.SegmentedProgress(Drills.size, round + if (result?.isEmpty() == true) 1 else 0, Modifier.padding(horizontal = 20.dp))
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(12.dp))
            Text(drill.title.t(), style = Type.h2, color = c.text)
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(c.lilac).padding(12.dp)) {
                Text(tr("الهدف: ", "Goal: ") + drill.goal.t(), style = Type.small, color = c.onLilac)
            }
            Spacer(Modifier.height(14.dp))
            Box(
                Modifier.fillMaxWidth().height(260.dp).clip(RoundedCornerShape(20.dp)).background(c.surface)
                    .border(1.dp, c.outline, RoundedCornerShape(20.dp)).dotPattern(c.muted.copy(alpha = 0.25f), 14.dp),
                contentAlignment = Alignment.Center,
            ) { DrillPreview(round, animPad, animGap, align, p, g) }
            Spacer(Modifier.height(16.dp))
            EynCard(Modifier.fillMaxWidth()) {
                SliderRow(drill.padLabel.t(), pad, 0f..40f, 19) { pad = it; result = null }
                SliderRow(drill.gapLabel.t(), gap, 0f..40f, 19) { gap = it; result = null }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(tr("المحاذاة", "Alignment"), style = Type.label, color = c.text, modifier = Modifier.weight(1f))
                    val icons = if (drill.vertical) listOf(Icons.Rounded.VerticalAlignTop, Icons.Rounded.VerticalAlignCenter, Icons.Rounded.VerticalAlignBottom)
                    else listOf(Icons.Rounded.FormatAlignLeft, Icons.Rounded.FormatAlignCenter, Icons.Rounded.FormatAlignRight)
                    icons.forEachIndexed { i, ic ->
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
                        if (ok) {
                            Text(tr("ممتاز! (+10 نقاط)", "Excellent! (+10 points)"), style = Type.label, color = c.success)
                            Text(drill.tip.t(), style = Type.small, color = c.success)
                        }
                        issues.forEach { Text("• " + it.t(), style = Type.small, color = c.danger) }
                    }
                }
            }
            if (finished) {
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(c.accentSoft).padding(14.dp)) {
                    Text(tr("🎉 أنهيت التمارين الستة! يمكنك إعادتها أو تجربة الاستوديو.", "🎉 You finished all six drills! Replay them or try the Studio."), style = Type.label, color = c.accent)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
        Box(Modifier.navigationBarsPadding().padding(20.dp)) {
            if (result?.isEmpty() == true) {
                val last = round == Drills.lastIndex
                PrimaryButton(if (last) tr("ابدأ من جديد", "Start over") else tr("التمرين التالي", "Next drill"), icon = Icons.Rounded.Refresh) {
                    if (last) finished = true
                    round = (round + 1) % Drills.size
                }
            } else {
                PrimaryButton(tr("تحقّق من التصميم", "Check design")) {
                    val issues = buildList {
                        if (p !in drill.padOk) add(L(
                            "${drill.padLabel.ar}: $p — المطلوب ${drill.padOk.first}${if (drill.padOk.last != drill.padOk.first) "–${drill.padOk.last}" else ""}",
                            "${drill.padLabel.en}: $p — target ${drill.padOk.first}${if (drill.padOk.last != drill.padOk.first) "–${drill.padOk.last}" else ""}",
                        ))
                        if (g !in drill.gapOk) add(L(
                            "${drill.gapLabel.ar}: $g — المطلوب ${drill.gapOk.first}${if (drill.gapOk.last != drill.gapOk.first) "–${drill.gapOk.last}" else ""}",
                            "${drill.gapLabel.en}: $g — target ${drill.gapOk.first}${if (drill.gapOk.last != drill.gapOk.first) "–${drill.gapOk.last}" else ""}",
                        ))
                        if (align != drill.alignOk) add(
                            if (drill.vertical) L("غيّر المحاذاة العمودية كما في الهدف", "Change the vertical alignment to match the goal")
                            else L("غيّر المحاذاة الأفقية كما في الهدف", "Change the horizontal alignment to match the goal"),
                        )
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
