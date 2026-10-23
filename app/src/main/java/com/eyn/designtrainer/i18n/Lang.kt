package com.eyn.designtrainer.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.serialization.Serializable

enum class Lang { AR, EN }

val LocalLang = staticCompositionLocalOf { Lang.AR }

/** Picks the Arabic or English string for the current UI language. */
@Composable
@ReadOnlyComposable
fun tr(ar: String, en: String): String = if (LocalLang.current == Lang.AR) ar else en

/** A bilingual text pair used by static content (lessons, templates, blocks). */
@Serializable
data class L(val ar: String, val en: String) {
    fun get(lang: Lang) = if (lang == Lang.AR) ar else en

    @Composable
    @ReadOnlyComposable
    fun t() = get(LocalLang.current)
}

val isArabic: Boolean
    @Composable @ReadOnlyComposable get() = LocalLang.current == Lang.AR

/** Option letters: أ ب ج د / A B C D */
fun optionLetter(i: Int, lang: Lang) =
    if (lang == Lang.AR) listOf("أ", "ب", "ج", "د", "هـ")[i] else ('A' + i).toString()

/** "N days" with correct Arabic number agreement (يوم واحد، يومان، 3 أيام، 11 يوماً). */
fun daysText(n: Int, lang: Lang): String = if (lang == Lang.EN) (if (n == 1) "1 day" else "$n days") else when {
    n == 0 -> "0 يوم"
    n == 1 -> "يوم واحد"
    n == 2 -> "يومان"
    n % 100 in 3..10 -> "$n أيام"
    else -> "$n يوماً"
}
