package com.eyn.designtrainer.ui.theme

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.eyn.designtrainer.R

data class FontInfoItem(val name: String, val family: FontFamily, val arabic: Boolean)

@OptIn(ExperimentalTextApi::class)
private fun variable(res: Int) = FontFamily(
    Font(res, FontWeight.Normal, variationSettings = FontVariation.Settings(FontVariation.weight(400))),
    Font(res, FontWeight.Bold, variationSettings = FontVariation.Settings(FontVariation.weight(700))),
)

private fun single(res: Int) = FontFamily(Font(res, FontWeight.Normal))

private fun pair(regular: Int, bold: Int) = FontFamily(Font(regular, FontWeight.Normal), Font(bold, FontWeight.Bold))

/**
 * Every bundled typeface (SIL Open Font License). Indices are stored in designs, so only append.
 */
val FontCatalog: List<FontInfoItem> = listOf(
    FontInfoItem("IBM Plex", Plex, true),
    FontInfoItem("Cairo", Cairo, true),
    FontInfoItem("Tajawal", Tajawal, true),
    FontInfoItem("Almarai", pair(R.font.almarai_regular, R.font.almarai_bold), true),
    FontInfoItem("Amiri", pair(R.font.amiri_regular, R.font.amiri_bold), true),
    FontInfoItem("Aref Ruqaa", pair(R.font.arefruqaa_regular, R.font.arefruqaa_bold), true),
    FontInfoItem("Changa", variable(R.font.changa), true),
    FontInfoItem("El Messiri", variable(R.font.elmessiri), true),
    FontInfoItem("Lalezar", single(R.font.lalezar), true),
    FontInfoItem("Lemonada", variable(R.font.lemonada), true),
    FontInfoItem("Marhey", variable(R.font.marhey), true),
    FontInfoItem("Rakkas", single(R.font.rakkas), true),
    FontInfoItem("Reem Kufi", variable(R.font.reemkufi), true),
    FontInfoItem("Poppins", pair(R.font.poppins_regular, R.font.poppins_bold), false),
    FontInfoItem("Montserrat", variable(R.font.montserrat), false),
    FontInfoItem("Playfair", variable(R.font.playfair), false),
    FontInfoItem("Bebas Neue", single(R.font.bebas), false),
    FontInfoItem("Pacifico", single(R.font.pacifico), false),
    FontInfoItem("Lobster", single(R.font.lobster), false),
)

fun fontAt(i: Int): FontFamily = FontCatalog.getOrNull(i)?.family ?: Plex
