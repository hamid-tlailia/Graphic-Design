package com.eyn.designtrainer.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eyn.designtrainer.ui.theme.Eyn
import com.eyn.designtrainer.ui.theme.Type

/** Fades and slides content up on first appearance; [index] staggers items in a list. */
fun Modifier.appear(index: Int = 0): Modifier = composed {
    val a = remember { androidx.compose.animation.core.Animatable(0f) }
    androidx.compose.runtime.LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index.coerceAtMost(10) * 45L)
        a.animateTo(1f, tween(420, easing = androidx.compose.animation.core.FastOutSlowInEasing))
    }
    this.graphicsLayer { alpha = a.value; translationY = (1f - a.value) * 24.dp.toPx() }
}

/** Click with a soft spring scale instead of a ripple. */
fun Modifier.bounceClick(enabled: Boolean = true, scaleDown: Float = 0.97f, onClick: () -> Unit): Modifier = composed {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        if (pressed && enabled) scaleDown else 1f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "press",
    )
    this
        .graphicsLayer { scaleX = scale; scaleY = scale }
        .clickable(interaction, indication = null, enabled = enabled, onClick = onClick)
}

@Composable
fun EynCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    color: Color = Eyn.c.surface,
    border: BorderStroke? = BorderStroke(1.dp, Eyn.c.outline),
    shape: Shape = RoundedCornerShape(20.dp),
    padding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    val base = if (onClick != null) modifier.bounceClick(onClick = onClick) else modifier
    Column(
        base
            .clip(shape)
            .background(color)
            .then(if (border != null) Modifier.border(border, shape) else Modifier)
            .padding(padding),
        content = content,
    )
}

@Composable
fun PrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    container: Color = Eyn.c.primary,
    content: Color = Eyn.c.onPrimary,
    onClick: () -> Unit,
) {
    val bg by animateColorAsState(if (enabled) container else Eyn.c.outline, label = "btn")
    val fg by animateColorAsState(if (enabled) content else Eyn.c.muted, label = "btnfg")
    Row(
        modifier
            .fillMaxWidth()
            .height(54.dp)
            .bounceClick(enabled, onClick = onClick)
            .clip(RoundedCornerShape(16.dp))
            .background(bg),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text, style = Type.button, color = fg)
        if (icon != null) {
            Spacer(Modifier.width(8.dp))
            Icon(icon, null, tint = fg, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun OutlineButton(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    color: Color = Eyn.c.primary,
    onClick: () -> Unit,
) {
    Row(
        modifier
            .fillMaxWidth()
            .height(54.dp)
            .bounceClick(onClick = onClick)
            .clip(RoundedCornerShape(16.dp))
            .border(1.5.dp, color.copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, style = Type.button, color = color)
    }
}

@Composable
fun Pill(text: String, bg: Color, fg: Color, modifier: Modifier = Modifier, icon: ImageVector? = null, style: TextStyle = Type.label) {
    Row(
        modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(icon, null, tint = fg, modifier = Modifier.size(15.dp))
            Spacer(Modifier.width(4.dp))
        }
        Text(text, style = style.copy(fontWeight = FontWeight.SemiBold), color = fg, maxLines = 1)
    }
}

@Composable
fun IconTile(icon: ImageVector, bg: Color, tint: Color, size: Dp = 44.dp, radius: Dp = 14.dp, iconSize: Dp = 22.dp) {
    Box(
        Modifier
            .size(size)
            .clip(RoundedCornerShape(radius))
            .background(bg),
        contentAlignment = Alignment.Center,
    ) { Icon(icon, null, tint = tint, modifier = Modifier.size(iconSize)) }
}

@Composable
fun SquareIconButton(icon: ImageVector, onClick: () -> Unit, tint: Color = Eyn.c.text, bg: Color = Eyn.c.surface) {
    Box(
        Modifier
            .size(42.dp)
            .bounceClick(scaleDown = 0.9f, onClick = onClick)
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .border(1.dp, Eyn.c.outline, RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center,
    ) { Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp)) }
}

@Composable
fun TopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    subtitle: String? = null,
    big: Boolean = false,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Row(
        Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            SquareIconButton(Icons.AutoMirrored.Rounded.ArrowBack, onBack)
            Spacer(Modifier.width(12.dp))
        }
        Column(Modifier.weight(1f)) {
            if (subtitle != null) Text(subtitle, style = Type.small, color = Eyn.c.muted)
            Text(
                title,
                style = if (big) Type.h1 else Type.h3,
                color = Eyn.c.text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically, content = actions)
    }
}

@Composable
fun SectionTitle(title: String, action: String? = null, onAction: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = Type.h3, color = Eyn.c.text, modifier = Modifier.weight(1f))
        if (action != null && onAction != null) {
            Text(
                action,
                style = Type.label,
                color = Eyn.c.primary,
                modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable(onClick = onAction).padding(4.dp),
            )
        }
    }
}

@Composable
fun ProgressBar(progress: Float, modifier: Modifier = Modifier, color: Color = Eyn.c.accent, track: Color = Eyn.c.lilac, height: Dp = 6.dp) {
    val p by animateFloatAsState(progress.coerceIn(0f, 1f), tween(700), label = "progress")
    Box(modifier.height(height).clip(CircleShape).background(track)) {
        Box(Modifier.fillMaxHeight().fillMaxWidth(p).clip(CircleShape).background(color))
    }
}

@Composable
fun SegmentedProgress(total: Int, done: Int, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(total) { i ->
            val color by animateColorAsState(if (i < done) Eyn.c.primary else Eyn.c.outline, tween(400), label = "seg")
            Box(Modifier.weight(1f).height(5.dp).clip(CircleShape).background(color))
        }
    }
}

@Composable
fun ChipRow(options: List<String>, selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEachIndexed { i, label ->
            val on = i == selected
            val bg by animateColorAsState(if (on) Eyn.c.primary else Eyn.c.surface, label = "chip")
            val fg by animateColorAsState(if (on) Eyn.c.onPrimary else Eyn.c.muted, label = "chipfg")
            Text(
                label,
                style = Type.label,
                color = fg,
                modifier = Modifier
                    .bounceClick { onSelect(i) }
                    .clip(RoundedCornerShape(50))
                    .background(bg)
                    .border(1.dp, if (on) Color.Transparent else Eyn.c.outline, RoundedCornerShape(50))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
    }
}

/** Two-to-four way segmented control. */
@Composable
fun Segmented(options: List<String>, selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Eyn.c.surfaceAlt)
            .padding(4.dp),
    ) {
        options.forEachIndexed { i, label ->
            val on = i == selected
            val bg by animateColorAsState(if (on) Eyn.c.surface else Color.Transparent, label = "seg")
            val fg by animateColorAsState(if (on) Eyn.c.primary else Eyn.c.muted, label = "segfg")
            Box(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(bg)
                    .clickable { onSelect(i) }
                    .padding(vertical = 9.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(label, style = Type.label.copy(fontWeight = FontWeight.SemiBold), color = fg, textAlign = TextAlign.Center, maxLines = 1)
            }
        }
    }
}

/** The brand mark: a pen nib drawing a curve. Drawn on a 120-unit square grid. */
@Composable
fun BrandLogo(modifier: Modifier = Modifier, nib: Color = Eyn.c.hero, curve: Color = Eyn.c.accent, cut: Color = Color.White) {
    Canvas(modifier) {
        val k = size.minDimension / 120f
        withTransform({ scale(k, k, pivot = Offset.Zero) }) {
            val stroke = Path().apply { moveTo(18f, 92f); cubicTo(34f, 40f, 86f, 80f, 102f, 28f) }
            drawPath(stroke, curve, style = Stroke(width = 5f, cap = StrokeCap.Round))
            withTransform({
                translate(60f, 60f); rotate(-35f, pivot = Offset.Zero)
                translate(-50f, -56f); scale(0.82f, 0.82f, pivot = Offset.Zero); translate(11f, 6f)
            }) {
                val body = Path().apply { moveTo(30f, 36f); lineTo(70f, 36f); lineTo(70f, 54f); lineTo(50f, 92f); lineTo(30f, 54f); close() }
                drawPath(body, nib)
                drawPath(body, nib, style = Stroke(width = 4f, join = StrokeJoin.Round))
                drawRoundRect(nib, topLeft = Offset(35f, 16f), size = Size(30f, 14f), cornerRadius = CornerRadius(5f))
                drawLine(cut, Offset(50f, 64f), Offset(50f, 90f), strokeWidth = 3.5f, cap = StrokeCap.Round)
                drawCircle(cut, 7f, Offset(50f, 58f))
            }
        }
    }
}

/** Grey-and-white checks that show where a canvas is transparent. */
fun Modifier.checkerboard(cell: Dp = 10.dp) = drawBehind {
    val c = cell.toPx()
    drawRect(Color.White)
    var y = 0f; var row = 0
    while (y < size.height) {
        var x = if (row % 2 == 0) 0f else c
        while (x < size.width) { drawRect(Color(0xFFE5E5EA), Offset(x, y), Size(c, c)); x += 2 * c }
        y += c; row++
    }
}

/** Faint dot grid, used on hero screens and canvases. */
fun Modifier.dotPattern(color: Color, step: Dp = 18.dp, radius: Dp = 1.dp) = drawBehind {
    val s = step.toPx()
    val r = radius.toPx()
    var y = s / 2
    while (y < size.height) {
        var x = s / 2
        while (x < size.width) {
            drawCircle(color, r, Offset(x, y)); x += s
        }
        y += s
    }
}

@Composable
fun AnimatedDots(count: Int, current: Int, active: Color, inactive: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        repeat(count) { i ->
            val w by animateDpAsState(if (i == current) 22.dp else 7.dp, spring(stiffness = Spring.StiffnessLow), label = "dot")
            val c by animateColorAsState(if (i == current) active else inactive, label = "dotc")
            Box(Modifier.height(7.dp).width(w).clip(CircleShape).background(c))
        }
    }
}

@Composable
fun BoxScope.EmptyHint(text: String) {
    Text(text, style = Type.body, color = Eyn.c.muted, textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.Center).padding(32.dp))
}

@Composable
fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    EynCard(modifier, padding = PaddingValues(vertical = 14.dp, horizontal = 8.dp)) {
        Text(value, style = Type.h2.copy(fontSize = 22.sp), color = Eyn.c.primary, modifier = Modifier.align(Alignment.CenterHorizontally))
        Text(label, style = Type.small, color = Eyn.c.muted, modifier = Modifier.align(Alignment.CenterHorizontally), maxLines = 1)
    }
}
