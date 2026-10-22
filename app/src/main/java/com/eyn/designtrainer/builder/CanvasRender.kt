package com.eyn.designtrainer.builder

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Mail
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.RemoveRedEye
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.ShowChart
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.eyn.designtrainer.data.Design
import com.eyn.designtrainer.data.El
import com.eyn.designtrainer.data.ElType
import com.eyn.designtrainer.data.c
import com.eyn.designtrainer.ui.theme.Cairo
import com.eyn.designtrainer.ui.theme.Plex
import com.eyn.designtrainer.ui.theme.Tajawal
import kotlin.math.roundToInt

/** Small vector set used by the block builder's logo picker. The canvas uses the full [IconFont]. */
val CanvasIcons: Map<String, ImageVector> = linkedMapOf(
    "star" to Icons.Rounded.Star,
    "heart" to Icons.Rounded.Favorite,
    "home" to Icons.Rounded.Home,
    "search" to Icons.Rounded.Search,
    "menu" to Icons.Rounded.Menu,
    "person" to Icons.Rounded.Person,
    "cart" to Icons.Rounded.ShoppingCart,
    "mail" to Icons.Rounded.Mail,
    "lock" to Icons.Rounded.Lock,
    "bell" to Icons.Rounded.Notifications,
    "settings" to Icons.Rounded.Settings,
    "eye" to Icons.Rounded.RemoveRedEye,
    "check" to Icons.Rounded.Check,
    "chart" to Icons.Rounded.ShowChart,
    "bolt" to Icons.Rounded.Bolt,
    "school" to Icons.Rounded.School,
    "dashboard" to Icons.Rounded.Dashboard,
    "public" to Icons.Rounded.Public,
    "coffee" to Icons.Rounded.Coffee,
    "arrow_fwd" to Icons.AutoMirrored.Rounded.ArrowForward,
    "arrow_back" to Icons.AutoMirrored.Rounded.ArrowBack,
)

val CanvasFonts: List<Pair<String, FontFamily>> = listOf("IBM Plex" to Plex, "Cairo" to Cairo, "Tajawal" to Tajawal)

private val ROUND_TYPES = setOf(ElType.CIRCLE, ElType.AVATAR)

fun El.shape(scale: Float): Shape = if (type in ROUND_TYPES) CircleShape else RoundedCornerShape((radius * scale).dp)

fun El.fill(): Brush? {
    val a = bg.c()
    val b = bg2?.c() ?: return if (a.alpha == 0f) null else Brush.linearGradient(listOf(a, a))
    return when (gradDir) {
        1 -> Brush.horizontalGradient(listOf(a, b))
        2 -> Brush.linearGradient(listOf(a, b))
        else -> Brush.verticalGradient(listOf(a, b))
    }
}

/** Material icon glyph drawn from the icon font. */
@Composable
fun FontIcon(name: String, color: Color, sizeDp: Float, modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    val glyph = remember(name) { IconFont.glyph(ctx, name) } ?: return
    val fs = with(LocalDensity.current) { sizeDp.dp.toSp() }
    Text(
        glyph, modifier = modifier, color = color,
        style = TextStyle(fontFamily = IconFont.family, fontSize = fs, lineHeight = fs, textAlign = TextAlign.Center),
    )
}

/** Draws one element at its position. [scale] maps design units to dp. */
@Composable
fun RenderEl(e: El, scale: Float, modifier: Modifier = Modifier) {
    if (!e.visible) return
    val density = LocalDensity.current
    val shape = e.shape(scale)
    val sp = { v: Float -> with(density) { (v * scale).dp.toSp() } }
    val fontSize = sp(e.fontSize)
    val hBias = when (e.align) { 1 -> 0f; 2 -> 1f; else -> -1f }
    val vBias = when (e.vAlign) { 0 -> -1f; 2 -> 1f; else -> 0f }
    val textAlign = when (e.align) { 1 -> TextAlign.Center; 2 -> TextAlign.Right; else -> TextAlign.Left }
    val style = TextStyle(
        color = e.fg.c(), fontSize = fontSize, lineHeight = fontSize * e.lineHeight,
        fontFamily = CanvasFonts.getOrNull(e.font)?.second ?: Plex,
        fontWeight = if (e.bold) FontWeight.Bold else FontWeight.Normal,
        fontStyle = if (e.italic) FontStyle.Italic else FontStyle.Normal,
        textDecoration = if (e.underline) TextDecoration.Underline else null,
        letterSpacing = sp(e.letterSpacing), textAlign = textAlign,
    )
    val fill = e.fill()
    val hasBox = e.type !in setOf(ElType.TOGGLE, ElType.SLIDER, ElType.PROGRESS, ElType.RATING, ElType.CHECKBOX)

    Box(
        Modifier
            .absoluteOffset((e.x * scale).dp, (e.y * scale).dp)
            .size((e.w * scale).dp, (e.h * scale).dp)
            .then(modifier)
            .graphicsLayer { rotationZ = e.rotation; alpha = e.opacity }
            .then(if (e.shadow > 0f && hasBox) Modifier.shadow((e.shadow * scale).dp, shape, clip = false) else Modifier)
            .then(if (hasBox) Modifier.clip(shape) else Modifier)
            .then(if (hasBox && fill != null) Modifier.background(fill, shape) else Modifier)
            .then(if (hasBox && e.borderW > 0f) Modifier.border((e.borderW * scale).dp, e.borderColor.c(), shape) else Modifier),
    ) {
        if (e.image.isNotEmpty() && hasBox) PhotoImage(e.image, e.fit, Modifier.fillMaxSize())
        val inner = Modifier.fillMaxSize().padding(horizontal = (e.padH * scale).dp, vertical = (e.padV * scale).dp)
        when (e.type) {
            ElType.TEXT -> Box(inner, contentAlignment = BiasAlignment(hBias, vBias)) {
                Text(e.text, style = style, modifier = Modifier.fillMaxWidth())
            }
            ElType.BUTTON, ElType.CHIP, ElType.CIRCLE -> if (e.text.isNotEmpty()) Box(inner.padding(horizontal = (4 * scale).dp), contentAlignment = BiasAlignment(hBias, vBias)) {
                Text(e.text, style = style, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.fillMaxWidth())
            }
            ElType.INPUT -> Box(inner.padding(horizontal = (14 * scale).dp), contentAlignment = BiasAlignment(hBias, vBias)) {
                Text(e.text, style = style, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.fillMaxWidth())
            }
            ElType.SEARCH -> Row(inner.padding(horizontal = (14 * scale).dp), verticalAlignment = Alignment.CenterVertically) {
                val icon = @Composable { FontIcon("search", e.fg.c(), e.h * 0.45f * scale) }
                if (e.align != 2) { icon(); Spacer(Modifier.width((8 * scale).dp)) }
                Text(e.text, style = style, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                if (e.align == 2) { Spacer(Modifier.width((8 * scale).dp)); icon() }
            }
            ElType.IMAGE, ElType.AVATAR -> if (e.image.isEmpty()) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                FontIcon(if (e.type == ElType.AVATAR) "person" else "image", e.fg.c().copy(alpha = 0.5f), minOf(e.w, e.h) * (if (e.type == ElType.AVATAR) 0.55f else 0.3f) * scale)
            }
            ElType.ICON -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                FontIcon(e.icon, e.fg.c(), minOf(e.w, e.h) * scale)
            }
            ElType.CHECKBOX -> Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                val s = (e.h * 0.8f * scale).dp
                val on = e.value >= 0.5f
                val box = @Composable {
                    Box(
                        Modifier.size(s).clip(RoundedCornerShape(s * 0.25f))
                            .background(if (on) e.bg.c() else Color.Transparent)
                            .border((1.5f * scale).dp, if (on) e.bg.c() else e.fg.c().copy(alpha = 0.4f), RoundedCornerShape(s * 0.25f)),
                        contentAlignment = Alignment.Center,
                    ) { if (on) FontIcon("check", Color.White, e.h * 0.65f * scale) }
                }
                if (e.align == 2) {
                    Text(e.text, style = style, maxLines = 1, modifier = Modifier.weight(1f)); Spacer(Modifier.width((8 * scale).dp)); box()
                } else {
                    box(); Spacer(Modifier.width((8 * scale).dp)); Text(e.text, style = style, maxLines = 1, modifier = Modifier.weight(1f))
                }
            }
            ElType.TOGGLE -> {
                val on = e.value >= 0.5f
                Box(
                    Modifier.fillMaxSize().clip(CircleShape).background(if (on) e.bg.c() else e.borderColor.c()).padding((e.h * 0.12f * scale).dp),
                    contentAlignment = if (on) Alignment.CenterEnd else Alignment.CenterStart,
                ) { Box(Modifier.fillMaxHeight().aspectRatio(1f).shadow((2 * scale).dp, CircleShape).clip(CircleShape).background(Color.White)) }
            }
            ElType.PROGRESS -> Box(Modifier.fillMaxSize().clip(RoundedCornerShape((e.radius * scale).dp)).background(e.bg.c())) {
                Box(Modifier.fillMaxHeight().fillMaxWidth(e.value.coerceIn(0f, 1f)).clip(RoundedCornerShape((e.radius * scale).dp)).background(e.fg.c()))
            }
            ElType.SLIDER -> BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                val track = (maxOf(e.h * 0.2f, 2f) * scale).dp
                Box(Modifier.fillMaxWidth().height(track).clip(CircleShape).background(e.bg.c()))
                Box(Modifier.fillMaxWidth(e.value.coerceIn(0f, 1f)).height(track).clip(CircleShape).background(e.fg.c()))
                val knob = (e.h * scale).dp
                Box(
                    Modifier.offset(x = (maxWidth - knob) * e.value.coerceIn(0f, 1f)).size(knob)
                        .shadow((2 * scale).dp, CircleShape).clip(CircleShape).background(Color.White)
                        .border((2 * scale).dp, e.fg.c(), CircleShape),
                )
            }
            ElType.RATING -> Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                val filled = (e.value * 5).roundToInt()
                repeat(5) { i -> FontIcon(if (i < filled) "star" else "star_border", e.fg.c(), minOf(e.h, e.w / 5f) * scale) }
            }
            ElType.BOX, ElType.CARD, ElType.DIVIDER -> {}
        }
    }
}

/** The whole design at a fixed scale (no interaction). */
@Composable
fun DesignCanvas(design: Design, scale: Float, modifier: Modifier = Modifier) {
    Box(
        modifier
            .size((design.w * scale).dp, (design.h * scale).dp)
            .background(design.bg.c())
            .clipToBounds(),
    ) {
        if (design.bgImage.isNotEmpty()) PhotoImage(design.bgImage, 0, Modifier.fillMaxSize())
        // Canvas coordinates are absolute, so lay elements out left-to-right in every language.
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            design.els.forEach { RenderEl(it, scale) }
        }
    }
}

/** Fits the design's width into the available space, cropping tall designs from the top. */
@Composable
fun DesignThumb(design: Design, modifier: Modifier = Modifier, height: Int = 150) {
    BoxWithConstraints(
        modifier.fillMaxWidth().height(height.dp).clip(RoundedCornerShape(14.dp)).background(design.bg.c().copy(alpha = 0.6f)),
        contentAlignment = Alignment.TopCenter,
    ) {
        val scale = minOf(maxWidth.value / design.w, if (design.h < design.w * 1.2f) height / design.h else 10f)
        val top = ((height - design.h * scale) / 2).coerceAtLeast(0f)
        DesignCanvas(design, scale, Modifier.offset(y = top.dp))
    }
}
