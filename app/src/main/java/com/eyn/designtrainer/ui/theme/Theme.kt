package com.eyn.designtrainer.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import com.eyn.designtrainer.R
import com.eyn.designtrainer.i18n.Lang
import com.eyn.designtrainer.i18n.LocalLang

// Brand palette (from the identity board)
val Navy = Color(0xFF2A2470)
val Coral = Color(0xFFF2603F)
val Lilac = Color(0xFFE8E5FA)
val Cream = Color(0xFFF7F6F2)

@Immutable
data class EynColors(
    val bg: Color,
    val surface: Color,
    val surfaceAlt: Color,
    val primary: Color,
    val onPrimary: Color,
    val hero: Color,
    val onHero: Color,
    val heroMuted: Color,
    val accent: Color,
    val accentSoft: Color,
    val lilac: Color,
    val onLilac: Color,
    val text: Color,
    val muted: Color,
    val outline: Color,
    val success: Color,
    val successSoft: Color,
    val danger: Color,
    val dangerSoft: Color,
    val isDark: Boolean,
)

val LightColors = EynColors(
    bg = Cream, surface = Color.White, surfaceAlt = Color(0xFFF1EFEA),
    primary = Navy, onPrimary = Color.White,
    hero = Navy, onHero = Color.White, heroMuted = Color(0xFFC9C5F5),
    accent = Coral, accentSoft = Color(0xFFFDE7E1),
    lilac = Lilac, onLilac = Navy,
    text = Color(0xFF1D1A3F), muted = Color(0xFF7C7A92), outline = Color(0xFFEAE8F0),
    success = Color(0xFF2E9E6A), successSoft = Color(0xFFE3F5EC),
    danger = Color(0xFFD9443A), dangerSoft = Color(0xFFFCE8E6),
    isDark = false,
)

val DarkColors = EynColors(
    bg = Color(0xFF110F22), surface = Color(0xFF1B1934), surfaceAlt = Color(0xFF24213F),
    primary = Color(0xFF8F86F5), onPrimary = Color(0xFF120F2E),
    hero = Color(0xFF2E2880), onHero = Color.White, heroMuted = Color(0xFFC9C5F5),
    accent = Color(0xFFFF7556), accentSoft = Color(0xFF3A2026),
    lilac = Color(0xFF2E2A55), onLilac = Color(0xFFD9D5FF),
    text = Color(0xFFF1EFFA), muted = Color(0xFF9C99B6), outline = Color(0xFF2D2A4A),
    success = Color(0xFF4CC38A), successSoft = Color(0xFF173527),
    danger = Color(0xFFFF6B60), dangerSoft = Color(0xFF3A1D22),
    isDark = true,
)

val LocalEyn = staticCompositionLocalOf { LightColors }

object Eyn {
    val c: EynColors @Composable get() = LocalEyn.current
}

val Plex = FontFamily(
    Font(R.font.plex_regular, FontWeight.Normal),
    Font(R.font.plex_medium, FontWeight.Medium),
    Font(R.font.plex_semibold, FontWeight.SemiBold),
    Font(R.font.plex_bold, FontWeight.Bold),
)
val Tajawal = FontFamily(
    Font(R.font.tajawal_regular, FontWeight.Normal),
    Font(R.font.tajawal_bold, FontWeight.Bold),
)
@OptIn(ExperimentalTextApi::class)
val Cairo = FontFamily(
    Font(R.font.cairo, FontWeight.Normal, variationSettings = FontVariation.Settings(FontVariation.weight(400))),
    Font(R.font.cairo, FontWeight.SemiBold, variationSettings = FontVariation.Settings(FontVariation.weight(600))),
    Font(R.font.cairo, FontWeight.Bold, variationSettings = FontVariation.Settings(FontVariation.weight(700))),
)

object Type {
    val h1 = TextStyle(fontFamily = Plex, fontWeight = FontWeight.Bold, fontSize = 26.sp, lineHeight = 36.sp)
    val h2 = TextStyle(fontFamily = Plex, fontWeight = FontWeight.Bold, fontSize = 21.sp, lineHeight = 30.sp)
    val h3 = TextStyle(fontFamily = Plex, fontWeight = FontWeight.SemiBold, fontSize = 17.sp, lineHeight = 26.sp)
    val body = TextStyle(fontFamily = Plex, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 25.sp)
    val label = TextStyle(fontFamily = Plex, fontWeight = FontWeight.Medium, fontSize = 13.sp, lineHeight = 19.sp)
    val small = TextStyle(fontFamily = Plex, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 17.sp)
    val button = TextStyle(fontFamily = Plex, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp)
}

@Composable
private fun animated(target: EynColors): EynColors {
    @Composable
    fun a(c: Color): Color = animateColorAsState(c, tween(450), label = "theme").value
    return EynColors(
        a(target.bg), a(target.surface), a(target.surfaceAlt), a(target.primary), a(target.onPrimary),
        a(target.hero), a(target.onHero), a(target.heroMuted), a(target.accent), a(target.accentSoft),
        a(target.lilac), a(target.onLilac), a(target.text), a(target.muted), a(target.outline),
        a(target.success), a(target.successSoft), a(target.danger), a(target.dangerSoft), target.isDark,
    )
}

@Composable
fun EynTheme(dark: Boolean, lang: Lang, content: @Composable () -> Unit) {
    val c = animated(if (dark) DarkColors else LightColors)
    val scheme = if (dark) darkColorScheme(
        primary = c.primary, onPrimary = c.onPrimary, secondary = c.accent, background = c.bg,
        surface = c.surface, onSurface = c.text, onBackground = c.text, surfaceVariant = c.surfaceAlt,
        onSurfaceVariant = c.muted, outline = c.outline, surfaceContainerHigh = c.surface,
        surfaceContainerLow = c.surface, surfaceContainer = c.surface, error = c.danger,
    ) else lightColorScheme(
        primary = c.primary, onPrimary = c.onPrimary, secondary = c.accent, background = c.bg,
        surface = c.surface, onSurface = c.text, onBackground = c.text, surfaceVariant = c.surfaceAlt,
        onSurfaceVariant = c.muted, outline = c.outline, surfaceContainerHigh = c.surface,
        surfaceContainerLow = c.surface, surfaceContainer = c.surface, error = c.danger,
    )
    val base = Typography()
    val typography = Typography(
        displayLarge = base.displayLarge.copy(fontFamily = Plex),
        headlineMedium = base.headlineMedium.copy(fontFamily = Plex),
        titleLarge = Type.h2, titleMedium = Type.h3, titleSmall = Type.label,
        bodyLarge = Type.body, bodyMedium = Type.body.copy(fontSize = 14.sp), bodySmall = Type.small,
        labelLarge = Type.label, labelMedium = Type.small, labelSmall = Type.small.copy(fontSize = 11.sp),
    )
    CompositionLocalProvider(
        LocalEyn provides c,
        LocalLang provides lang,
        LocalLayoutDirection provides if (lang == Lang.AR) LayoutDirection.Rtl else LayoutDirection.Ltr,
    ) {
        MaterialTheme(colorScheme = scheme, typography = typography, content = content)
    }
}
