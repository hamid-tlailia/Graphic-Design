package com.eyn.designtrainer.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eyn.designtrainer.data.Ill
import com.eyn.designtrainer.data.Vis
import com.eyn.designtrainer.ui.theme.Eyn
import com.eyn.designtrainer.ui.theme.Plex
import com.eyn.designtrainer.ui.theme.Type

@Composable
private fun Bar(w: Dp, h: Dp, color: Color, modifier: Modifier = Modifier) =
    Box(modifier.width(w).height(h).clip(RoundedCornerShape(50)).background(color))

/** Animated growth factor 0→1 so illustrations draw themselves in. */
@Composable
private fun grow(key: Any): Float {
    val a = remember(key) { Animatable(0f) }
    LaunchedEffect(key) { a.animateTo(1f, tween(900, easing = FastOutSlowInEasing)) }
    return a.value
}

@Composable
fun LessonIllustration(ill: Ill, modifier: Modifier = Modifier) {
    val c = Eyn.c
    val g = grow(ill)
    Box(
        modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(c.lilac)
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        when (ill) {
            Ill.SPACING_BARS -> Column(verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.End) {
                listOf(8, 16, 24, 32, 48).forEach { v ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.width((v * 2.6f * g).dp).height(16.dp).clip(RoundedCornerShape(3.dp)).background(c.accent))
                        Spacer(Modifier.width(16.dp))
                        Text("$v", style = Type.label.copy(fontWeight = FontWeight.SemiBold), color = c.onLilac, modifier = Modifier.width(26.dp))
                    }
                }
            }
            Ill.HIERARCHY -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Bar((180 * g).dp, 22.dp, c.onLilac)
                Bar((140 * g).dp, 12.dp, c.onLilac.copy(alpha = 0.5f))
                Bar((200 * g).dp, 8.dp, c.onLilac.copy(alpha = 0.3f))
                Bar((170 * g).dp, 8.dp, c.onLilac.copy(alpha = 0.3f))
                Spacer(Modifier.height(4.dp))
                Box(Modifier.width((96 * g).dp).height(30.dp).clip(RoundedCornerShape(10.dp)).background(c.accent))
            }
            Ill.COLOR_RULE -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(Modifier.width(260.dp).height(64.dp).clip(RoundedCornerShape(16.dp))) {
                    Box(Modifier.weight(0.6f).fillMaxSize().background(Color(0xFFF7F6F2)))
                    Box(Modifier.weight(0.3f * g + 0.001f).fillMaxSize().background(Color(0xFF2A2470)))
                    Box(Modifier.weight(0.1f * g + 0.001f).fillMaxSize().background(Color(0xFFF2603F)))
                }
                Spacer(Modifier.height(12.dp))
                Row(Modifier.width(260.dp)) {
                    Text("60%", style = Type.label, color = c.onLilac, modifier = Modifier.weight(0.6f))
                    Text("30%", style = Type.label, color = c.onLilac, modifier = Modifier.weight(0.3f))
                    Text("10%", style = Type.label, color = c.onLilac, modifier = Modifier.weight(0.12f))
                }
            }
            Ill.ALIGN_GRID -> Box(Modifier.size(260.dp, 140.dp)) {
                Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    repeat(4) { Box(Modifier.weight(1f).fillMaxSize().background(c.accent.copy(alpha = 0.10f))) }
                }
                Column(Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Bar((190 * g).dp, 18.dp, c.onLilac)
                    Bar((124 * g).dp, 10.dp, c.onLilac.copy(alpha = 0.5f))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(Modifier.size(57.dp, (50 * g).dp).clip(RoundedCornerShape(8.dp)).background(c.surface))
                        Box(Modifier.size(57.dp, (50 * g).dp).clip(RoundedCornerShape(8.dp)).background(c.surface))
                    }
                }
            }
            Ill.ARABIC_TYPE -> Row(verticalAlignment = Alignment.Bottom) {
                Text("أب", fontFamily = Plex, fontWeight = FontWeight.Bold, fontSize = (70 * g + 1).sp, color = c.onLilac)
                Spacer(Modifier.width(16.dp))
                Text("Aa", fontFamily = Plex, fontWeight = FontWeight.Bold, fontSize = (48 * g + 1).sp, color = c.accent)
            }
            Ill.CONTRAST -> Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(Modifier.size(110.dp).clip(RoundedCornerShape(20.dp)).background(Color(0xFFF7F6F2)), contentAlignment = Alignment.Center) {
                    Text("Aa", fontFamily = Plex, fontWeight = FontWeight.Bold, fontSize = 40.sp, color = Color(0xFF2A2470).copy(alpha = g))
                }
                Box(Modifier.size(110.dp).clip(RoundedCornerShape(20.dp)).background(Color.White), contentAlignment = Alignment.Center) {
                    Text("Aa", fontFamily = Plex, fontWeight = FontWeight.Bold, fontSize = 40.sp, color = Color(0xFFCFCFCF).copy(alpha = g))
                }
            }
            Ill.PROXIMITY -> Row(horizontalArrangement = Arrangement.spacedBy((40 * g + 6).dp)) {
                repeat(2) { grp ->
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        repeat(3) { r ->
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                repeat(3) { Box(Modifier.size(18.dp).clip(CircleShape).background(if (grp == 0) c.onLilac else c.accent)) }
                            }
                        }
                    }
                }
            }
            Ill.WHITESPACE -> Box(Modifier.size(250.dp, 140.dp).clip(RoundedCornerShape(16.dp)).background(c.surface), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Bar(110.dp, 16.dp, c.onLilac)
                    Bar(70.dp, 8.dp, c.muted.copy(alpha = 0.5f))
                    Box(Modifier.size((70 * g).dp, 24.dp).clip(RoundedCornerShape(8.dp)).background(c.accent))
                }
            }
            Ill.TYPE_SCALE -> Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(12, 16, 20, 24, 32, 40).forEach { s ->
                    Text("أ", fontFamily = Plex, fontWeight = FontWeight.Bold, fontSize = (s * g + 1).sp, color = if (s == 40) c.accent else c.onLilac)
                }
            }
            Ill.BALANCE -> Box(Modifier.size(260.dp, 130.dp)) {
                Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(4.dp).clip(CircleShape).background(c.onLilac))
                Box(Modifier.align(Alignment.BottomStart).offset(x = 20.dp, y = (-4).dp).size((90 * g + 1).dp).clip(RoundedCornerShape(16.dp)).background(c.onLilac.copy(alpha = 0.25f)))
                Box(Modifier.align(Alignment.BottomEnd).offset(x = (-36).dp, y = (-4).dp).size((44 * g + 1).dp).clip(CircleShape).background(c.accent))
            }
        }
    }
}

/** Mini mock used inside a quiz answer card. */
@Composable
fun QuizVisual(vis: Vis, good: Boolean, modifier: Modifier = Modifier) {
    val c = Eyn.c
    val ink = c.onLilac
    val soft = c.muted.copy(alpha = 0.35f)
    Box(
        modifier
            .fillMaxWidth()
            .height(112.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(c.surfaceAlt),
        contentAlignment = Alignment.Center,
    ) {
        when (vis) {
            Vis.SPACING -> {
                val pad = if (good) 16.dp else 6.dp
                Row(
                    Modifier
                        .width(220.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(c.surface)
                        .padding(start = pad, end = if (good) pad else 22.dp, top = pad, bottom = if (good) pad else 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(if (good) 8.dp else 3.dp)) {
                        Bar(96.dp, 8.dp, ink)
                        Bar(120.dp, 6.dp, soft)
                        Bar(76.dp, 6.dp, soft)
                    }
                    Spacer(Modifier.width(if (good) 12.dp else 2.dp))
                    Box(Modifier.size(52.dp).clip(RoundedCornerShape(10.dp)).background(c.lilac))
                }
            }
            Vis.CONTRAST -> {
                val bg = if (good) Color(0xFFF7F6F2) else Color.White
                val fg = if (good) Color(0xFF2A2470) else Color(0xFFBDBDBD)
                Column(Modifier.width(220.dp).clip(RoundedCornerShape(12.dp)).background(bg).padding(14.dp)) {
                    Text("التصميم الجيد", fontFamily = Plex, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = fg)
                    Text("Good design is readable", fontFamily = Plex, fontSize = 12.sp, color = fg)
                }
            }
            Vis.ALIGN -> Column(Modifier.width(220.dp).clip(RoundedCornerShape(12.dp)).background(c.surface).padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val offs = if (good) listOf(0, 0, 0) else listOf(0, 18, 8)
                offs.forEachIndexed { i, o ->
                    Row(Modifier.padding(start = o.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(14.dp).clip(CircleShape).background(c.accent))
                        Spacer(Modifier.width(8.dp))
                        Bar(listOf(110, 80, 130)[i].dp, 7.dp, ink.copy(alpha = 0.6f))
                    }
                }
            }
            Vis.HIERARCHY -> Column(Modifier.width(220.dp).clip(RoundedCornerShape(12.dp)).background(c.surface).padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("نظام الـ 8", fontFamily = Plex, fontWeight = if (good) FontWeight.Bold else FontWeight.Normal, fontSize = if (good) 20.sp else 13.sp, color = ink)
                Text("اجعل المسافات من مضاعفات 8", fontFamily = Plex, fontSize = 13.sp, color = if (good) c.muted else ink)
                Text("5 دقائق", fontFamily = Plex, fontSize = if (good) 11.sp else 13.sp, color = if (good) c.muted else ink)
            }
            Vis.COLOR_RATIO -> Row(Modifier.width(220.dp).height(64.dp).clip(RoundedCornerShape(12.dp))) {
                if (good) {
                    Box(Modifier.weight(0.6f).fillMaxSize().background(Color(0xFFF7F6F2)))
                    Box(Modifier.weight(0.3f).fillMaxSize().background(Color(0xFF2A2470)))
                    Box(Modifier.weight(0.1f).fillMaxSize().background(Color(0xFFF2603F)))
                } else {
                    Box(Modifier.weight(1f).fillMaxSize().background(Color(0xFF22C55E)))
                    Box(Modifier.weight(1f).fillMaxSize().background(Color(0xFFF2603F)))
                    Box(Modifier.weight(1f).fillMaxSize().background(Color(0xFF8B5CF6)))
                }
            }
            Vis.BUTTONS -> Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(Modifier.size(100.dp, 40.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF2A2470)), contentAlignment = Alignment.Center) {
                    Text("متابعة", fontFamily = Plex, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color.White)
                }
                val m = Modifier.size(100.dp, 40.dp).clip(RoundedCornerShape(12.dp))
                Box(
                    if (good) m.border(1.5.dp, Color(0xFF2A2470).copy(alpha = 0.4f), RoundedCornerShape(12.dp)) else m.background(Color(0xFF2A2470)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("إلغاء", fontFamily = Plex, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = if (good) Color(0xFF2A2470) else Color.White)
                }
            }
            Vis.PROXIMITY -> Column(Modifier.width(220.dp).clip(RoundedCornerShape(12.dp)).background(c.surface).padding(10.dp)) {
                repeat(2) { i ->
                    if (i == 1) Spacer(Modifier.height(if (good) 12.dp else 6.dp))
                    Bar(50.dp, 6.dp, ink.copy(alpha = 0.6f))
                    Spacer(Modifier.height(if (good) 3.dp else 8.dp))
                    Box(Modifier.fillMaxWidth().height(20.dp).clip(RoundedCornerShape(6.dp)).border(1.dp, soft, RoundedCornerShape(6.dp)))
                }
            }
        }
    }
}
