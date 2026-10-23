package com.eyn.designtrainer.builder

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eyn.designtrainer.ui.components.bounceClick
import com.eyn.designtrainer.ui.theme.Eyn
import com.eyn.designtrainer.ui.theme.FontCatalog
import com.eyn.designtrainer.ui.theme.Type

/**
 * Scrollable font picker; each chip previews the typeface itself.
 * [extraFirst] adds a leading option (e.g. "same as app font") reported as index -1.
 */
@Composable
fun FontPickerRow(selected: Int, onSelect: (Int) -> Unit, extraFirst: String? = null, sample: String = "أبجد Aa") {
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        if (extraFirst != null) FontChip(extraFirst, sample, null, selected == -1) { onSelect(-1) }
        FontCatalog.forEachIndexed { i, f -> FontChip(f.name, sample, f.family, selected == i) { onSelect(i) } }
    }
}

@Composable
private fun FontChip(name: String, sample: String, family: androidx.compose.ui.text.font.FontFamily?, on: Boolean, onClick: () -> Unit) {
    val c = Eyn.c
    val bg by animateColorAsState(if (on) c.lilac else c.surface, label = "font")
    Column(
        Modifier.widthIn(min = 84.dp).bounceClick(onClick = onClick).clip(RoundedCornerShape(14.dp)).background(bg)
            .border(if (on) 2.dp else 1.dp, if (on) c.primary else c.outline, RoundedCornerShape(14.dp)).padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(sample, fontFamily = family, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = if (on) c.onLilac else c.text, maxLines = 1)
        Text(name, style = Type.small.copy(fontSize = 11.sp), color = if (on) c.onLilac else c.muted, maxLines = 1)
    }
}
