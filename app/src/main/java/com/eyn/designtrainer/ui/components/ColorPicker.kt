package com.eyn.designtrainer.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.eyn.designtrainer.i18n.tr
import com.eyn.designtrainer.ui.theme.Eyn
import com.eyn.designtrainer.ui.theme.Type

val Swatches = listOf(
    0xFFFFFFFF, 0xFFF7F6F2, 0xFFE8E5FA, 0xFF2A2470, 0xFFF2603F, 0xFF1D1A3F, 0xFF7C7A92, 0xFFEAE8F0,
    0xFF2E9E6A, 0xFFFFC845, 0xFF3B82F6, 0xFFEC4899, 0xFF8B5CF6, 0xFF14B8A6, 0xFF5A3A22, 0xFFF3E9DD, 0xFF111111,
)

fun Color.hex(): String = "#%06X".format(toArgb() and 0xFFFFFF)

fun parseHex(s: String): Color? {
    val t = s.trim().removePrefix("#")
    if (t.length != 6 && t.length != 8) return null
    return t.toLongOrNull(16)?.let { Color((if (t.length == 6) 0xFF000000 or it else it).toInt()) }
}

/** Horizontal swatch strip with an optional "transparent" chip and a custom-color button. */
@Composable
fun SwatchRow(
    selected: Color,
    onPick: (Color) -> Unit,
    modifier: Modifier = Modifier,
    allowClear: Boolean = false,
    extra: List<Long> = emptyList(),
) {
    var custom by remember { mutableStateOf(false) }
    val c = Eyn.c
    Row(
        modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (allowClear) {
            val on = selected.alpha == 0f
            Box(
                Modifier.size(34.dp).clip(CircleShape).background(c.surface)
                    .border(if (on) 2.5.dp else 1.dp, if (on) c.primary else c.outline, CircleShape)
                    .bounceClick { onPick(Color.Transparent) },
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Rounded.Block, null, tint = c.muted, modifier = Modifier.size(18.dp)) }
        }
        (extra + Swatches).distinct().forEach { v ->
            val col = Color(v.toInt())
            val on = col.toArgb() == selected.toArgb() && selected.alpha > 0f
            val ring by animateDpAsState(if (on) 3.dp else 0.dp, label = "ring")
            Box(
                Modifier
                    .size(34.dp)
                    .bounceClick(scaleDown = 0.85f) { onPick(col) }
                    .clip(CircleShape)
                    .border(1.dp, c.outline, CircleShape)
                    .padding(ring)
                    .border(if (on) 2.dp else 0.dp, if (on) c.primary else Color.Transparent, CircleShape)
                    .padding(if (on) 3.dp else 0.dp)
                    .clip(CircleShape)
                    .background(col),
            )
        }
        Box(
            Modifier.size(34.dp).clip(CircleShape)
                .background(Brush.sweepGradient(listOf(Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Blue, Color.Magenta, Color.Red)))
                .bounceClick { custom = true },
            contentAlignment = Alignment.Center,
        ) { Icon(Icons.Rounded.Add, null, tint = Color.White, modifier = Modifier.size(18.dp)) }
    }
    if (custom) ColorDialog(selected.takeIf { it.alpha > 0f } ?: Color(0xFF2A2470), onDismiss = { custom = false }) { onPick(it); custom = false }
}

@Composable
fun ColorDialog(initial: Color, onDismiss: () -> Unit, onPick: (Color) -> Unit) {
    val hsv = remember {
        FloatArray(3).also { android.graphics.Color.colorToHSV(initial.toArgb(), it) }
    }
    var h by remember { mutableFloatStateOf(hsv[0]) }
    var s by remember { mutableFloatStateOf(hsv[1]) }
    var v by remember { mutableFloatStateOf(hsv[2]) }
    val color = Color(android.graphics.Color.HSVToColor(floatArrayOf(h, s, v)))
    var hex by remember { mutableStateOf(initial.hex()) }
    val c = Eyn.c

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = c.surface,
        confirmButton = { TextButton({ onPick(color) }) { Text(tr("تم", "Done"), style = Type.label, color = c.primary) } },
        dismissButton = { TextButton(onDismiss) { Text(tr("إلغاء", "Cancel"), style = Type.label, color = c.muted) } },
        title = { Text(tr("لون مخصص", "Custom color"), style = Type.h3, color = c.text) },
        text = {
            Column {
                Box(
                    Modifier.fillMaxWidth().height(64.dp).clip(RoundedCornerShape(16.dp)).background(color),
                    contentAlignment = Alignment.Center,
                ) { Text(color.hex(), style = Type.h3, color = if (color.luminance() > 0.5f) Color.Black else Color.White) }
                Spacer(Modifier.height(12.dp))
                @Composable
                fun row(label: String, value: Float, range: ClosedFloatingPointRange<Float>, track: Brush, set: (Float) -> Unit) {
                    Text(label, style = Type.small, color = c.muted)
                    Box(contentAlignment = Alignment.Center) {
                        Box(Modifier.fillMaxWidth().padding(horizontal = 10.dp).height(10.dp).clip(CircleShape).background(track))
                        Slider(
                            value, { set(it); hex = Color(android.graphics.Color.HSVToColor(floatArrayOf(h, s, v))).hex() }, valueRange = range,
                            colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.Transparent, inactiveTrackColor = Color.Transparent),
                        )
                    }
                }
                row(tr("الصبغة", "Hue"), h, 0f..360f, Brush.horizontalGradient((0..6).map { Color.hsv(it * 60f % 360f, 1f, 1f) })) { h = it }
                row(tr("التشبع", "Saturation"), s, 0f..1f, Brush.horizontalGradient(listOf(Color.hsv(h, 0f, v), Color.hsv(h, 1f, v)))) { s = it }
                row(tr("السطوع", "Brightness"), v, 0f..1f, Brush.horizontalGradient(listOf(Color.Black, Color.hsv(h, s, 1f)))) { v = it }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    hex,
                    { t ->
                        hex = t
                        parseHex(t)?.let { p ->
                            val arr = FloatArray(3); android.graphics.Color.colorToHSV(p.toArgb(), arr)
                            h = arr[0]; s = arr[1]; v = arr[2]
                        }
                    },
                    singleLine = true,
                    label = { Text("HEX") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.width(0.dp))
            }
        },
    )
}
