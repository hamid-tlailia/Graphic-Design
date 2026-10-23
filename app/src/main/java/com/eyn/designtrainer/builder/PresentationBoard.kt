package com.eyn.designtrainer.builder

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eyn.designtrainer.data.AppProject
import com.eyn.designtrainer.data.Page
import com.eyn.designtrainer.data.c
import com.eyn.designtrainer.i18n.LocalLang
import com.eyn.designtrainer.i18n.tr

/** Board width in dp; pages are laid out four per row. */
const val BOARD_WIDTH = 1680
private const val PHONE_W = 360
private const val SCREEN_H = 740

/**
 * A single presentation image of the whole app: identity header (logo, app icon, palette,
 * typography, icon style) followed by every page in a phone frame, four per row.
 */
@Composable
fun PresentationBoard(p: AppProject) {
    val t = p.theme()
    val pal = p.colors()
    val lang = LocalLang.current
    val label = { size: Int, color: Color -> androidx.compose.ui.text.TextStyle(fontFamily = t.font, fontSize = size.sp, color = color) }

    Column(
        Modifier.width(BOARD_WIDTH.dp)
            .background(Brush.verticalGradient(listOf(t.surface, Color.White, t.surface.copy(alpha = 0.6f))))
            .padding(60.dp),
    ) {
        // ------------------------------------------------------------ identity header
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(40.dp)).background(Color.White.copy(alpha = 0.92f)).padding(horizontal = 48.dp, vertical = 40.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                LogoMark(p, 72.dp)
                Spacer(Modifier.height(14.dp))
                Box(Modifier.width(260.dp).height(2.dp).background(t.primary.copy(alpha = 0.2f)))
                Spacer(Modifier.height(10.dp))
                Text(
                    tr("هوية بصرية · ${p.pages.size} شاشات", "Visual identity · ${p.pages.size} screens"),
                    style = label(22, t.muted),
                )
            }
            BoardDivider(t.primary)
            // App icon
            Box(
                Modifier.size(160.dp).clip(RoundedCornerShape(40.dp)).background(t.primary),
                contentAlignment = Alignment.Center,
            ) {
                if (p.logoStyle == 0) FontIcon(p.logoIcon, t.onPrimary, 96f)
                else Text(p.name.trim().take(1), fontFamily = if (p.logoFont >= 0) com.eyn.designtrainer.ui.theme.fontAt(p.logoFont) else t.font, fontWeight = FontWeight.Bold, fontSize = 84.sp, color = t.onPrimary)
            }
            BoardDivider(t.primary)
            Column(Modifier.width(460.dp)) {
                Text(tr("لوحة الألوان", "Color palette"), style = label(24, t.ink))
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    val roles = listOf(
                        pal.primary to tr("أساسي", "Primary"), pal.surface to tr("ثانوي", "Surface"),
                        pal.accent to tr("تمييز", "Accent"), pal.bg to tr("خلفية", "Background"), pal.ink to tr("نص", "Text"),
                    )
                    roles.forEach { (col, name) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(84.dp)) {
                            Box(Modifier.size(64.dp).clip(CircleShape).background(col.c()).border(2.dp, t.ink.copy(alpha = 0.08f), CircleShape))
                            Spacer(Modifier.height(8.dp))
                            Text("#%06X".format(col and 0xFFFFFF), style = label(16, t.ink).copy(fontWeight = FontWeight.Bold, textDirection = TextDirection.Ltr), maxLines = 1)
                            Text(name, style = label(15, t.muted))
                        }
                    }
                }
            }
            BoardDivider(t.primary)
            Column(Modifier.width(190.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(tr("الخط", "Typeface"), style = label(24, t.ink))
                Text("Aa أب", fontFamily = t.font, fontWeight = FontWeight.Bold, fontSize = 56.sp, color = t.primary, maxLines = 1, softWrap = false)
                Text(ProjectFonts.getOrNull(p.font)?.first ?: "", style = label(18, t.muted))
            }
            BoardDivider(t.primary)
            Column(Modifier.width(190.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(tr("نمط الأيقونات", "Icon style"), style = label(24, t.ink))
                Spacer(Modifier.height(16.dp))
                iconsOf(p, lang).chunked(3).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(22.dp), modifier = Modifier.padding(bottom = 18.dp)) {
                        row.forEach { FontIcon(it, t.primary, 44f) }
                    }
                }
            }
        }

        Spacer(Modifier.height(56.dp))

        // ------------------------------------------------------------ screens, 4 per row
        p.pages.chunked(4).forEachIndexed { r, row ->
            Row(horizontalArrangement = Arrangement.spacedBy(40.dp)) {
                row.forEachIndexed { i, page -> BoardPhone(page, p, r * 4 + i + 1) }
            }
            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
private fun BoardDivider(color: Color) {
    Box(Modifier.padding(horizontal = 20.dp).width(2.dp).height(160.dp).background(color.copy(alpha = 0.12f)))
}

/** Icons the project actually uses (logo, tab bars, lists), topped up with neutral defaults. */
private fun iconsOf(p: AppProject, lang: com.eyn.designtrainer.i18n.Lang): List<String> {
    val used = buildList {
        if (p.logoStyle == 0) add(p.logoIcon)
        p.pages.flatMap { it.blocks }.filter { it.itemsHaveIcons() }.forEach { b -> addAll(b.itemsOr(lang).map { it.icon }) }
    }.filter { it.isNotBlank() }.distinct()
    return (used + listOf("home", "search", "favorite", "person", "shopping_bag", "notifications")).distinct().take(6)
}

@Composable
private fun BoardPhone(page: Page, p: AppProject, number: Int) {
    val t = p.theme()
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier.width(PHONE_W.dp).clip(RoundedCornerShape(52.dp)).background(Color(0xFF15131F)).padding(9.dp),
        ) {
            Column(Modifier.fillMaxWidth().height(SCREEN_H.dp).clip(RoundedCornerShape(44.dp)).background(t.bg).clipToBounds()) {
                // Fake status bar with the notch
                Box(Modifier.fillMaxWidth().height(34.dp).padding(horizontal = 26.dp)) {
                    Text("9:41", fontFamily = t.font, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = t.ink, modifier = Modifier.align(Alignment.CenterStart))
                    Box(Modifier.align(Alignment.Center).width(96.dp).height(24.dp).clip(CircleShape).background(Color(0xFF15131F)))
                    Row(Modifier.align(Alignment.CenterEnd), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FontIcon("signal_cellular_alt", t.ink, 14f); FontIcon("wifi", t.ink, 14f); FontIcon("battery_full", t.ink, 14f)
                    }
                }
                PageView(page, p, minHeight = (SCREEN_H - 34).dp)
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "%02d · %s".format(number, page.name),
            fontFamily = t.font, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = t.ink.copy(alpha = 0.7f),
        )
    }
}
