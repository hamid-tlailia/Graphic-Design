package com.eyn.designtrainer.builder

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Image
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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.eyn.designtrainer.data.Design
import com.eyn.designtrainer.data.El
import com.eyn.designtrainer.data.ElType
import com.eyn.designtrainer.data.c
import com.eyn.designtrainer.ui.theme.Plex

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

fun El.shape(scale: Float): Shape = if (type == ElType.CIRCLE) CircleShape else RoundedCornerShape((radius * scale).dp)

/** Draws one element at its position. [scale] maps design units to dp. */
@Composable
fun RenderEl(e: El, scale: Float, modifier: Modifier = Modifier) {
    if (!e.visible) return
    val density = LocalDensity.current
    val shape = e.shape(scale)
    val fontSize = with(density) { (e.fontSize * scale).dp.toSp() }
    val align = when (e.align) { 1 -> TextAlign.Center; 2 -> TextAlign.Right; else -> TextAlign.Left }
    val boxAlign = when (e.align) { 1 -> Alignment.Center; 2 -> Alignment.CenterEnd; else -> Alignment.CenterStart }
    Box(
        Modifier
            .absoluteOffset((e.x * scale).dp, (e.y * scale).dp)
            .size((e.w * scale).dp, (e.h * scale).dp)
            .then(modifier)
            .alpha(e.opacity)
            .clip(shape)
            .background(e.bg.c(), shape)
            .then(if (e.borderW > 0f) Modifier.border((e.borderW * scale).dp, e.borderColor.c(), shape) else Modifier),
        contentAlignment = when (e.type) {
            ElType.BUTTON, ElType.CIRCLE, ElType.IMAGE, ElType.ICON -> Alignment.Center
            ElType.INPUT -> Alignment.CenterStart
            else -> boxAlign
        },
    ) {
        when (e.type) {
            ElType.TEXT, ElType.BUTTON, ElType.CIRCLE -> if (e.text.isNotEmpty()) Text(
                e.text, color = e.fg.c(), fontSize = fontSize, lineHeight = fontSize * 1.35f, fontFamily = Plex,
                fontWeight = if (e.bold) FontWeight.Bold else FontWeight.Normal,
                textAlign = if (e.type == ElType.TEXT) align else TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = if (e.type == ElType.TEXT) 0.dp else (6 * scale).dp),
            )
            ElType.INPUT -> Text(
                e.text, color = e.fg.c(), fontSize = fontSize, fontFamily = Plex, maxLines = 1,
                textAlign = align, modifier = Modifier.fillMaxWidth().padding(horizontal = (14 * scale).dp),
            )
            ElType.IMAGE -> Icon(Icons.Rounded.Image, null, tint = e.fg.c().copy(alpha = 0.45f), modifier = Modifier.size((minOf(e.w, e.h) * 0.3f * scale).dp))
            ElType.ICON -> Icon(CanvasIcons[e.icon] ?: Icons.Rounded.Star, null, tint = e.fg.c(), modifier = Modifier.fillMaxSize())
            ElType.BOX, ElType.DIVIDER -> {}
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
