package com.eyn.designtrainer.builder

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eyn.designtrainer.i18n.L
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

/** Word-style shapes, decorative page frames and payment marks for the canvas and the block builder. */
object Shapes {
    class Def(val name: L, val build: Path.(Float, Float) -> Unit)

    private fun Path.poly(vararg pts: Pair<Float, Float>) {
        pts.forEachIndexed { i, (x, y) -> if (i == 0) moveTo(x, y) else lineTo(x, y) }
        close()
    }

    /** A regular polygon or star inscribed in the box; [inner] < 1 makes a star with that inner radius ratio. */
    private fun Path.radial(w: Float, h: Float, points: Int, inner: Float = 1f, start: Double = -PI / 2) {
        val n = if (inner < 1f) points * 2 else points
        for (i in 0 until n) {
            val r = if (inner < 1f && i % 2 == 1) inner else 1f
            val a = start + 2 * PI * i / n
            val x = w / 2 + (w / 2) * r * cos(a).toFloat()
            val y = h / 2 + (h / 2) * r * sin(a).toFloat()
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    private fun Path.set(other: Path) { reset(); addPath(other) }

    private fun combine(op: PathOperation, a: Path, b: Path) = Path().apply { op(a, b, op) }

    private fun oval(l: Float, t: Float, r: Float, b: Float) = Path().apply { addOval(Rect(l, t, r, b)) }

    val all: List<Def> = listOf(
        Def(L("مثلث", "Triangle")) { w, h -> poly(w / 2 to 0f, w to h, 0f to h) },
        Def(L("مثلث قائم", "Right triangle")) { w, h -> poly(0f to 0f, w to h, 0f to h) },
        Def(L("معيّن", "Diamond")) { w, h -> poly(w / 2 to 0f, w to h / 2, w / 2 to h, 0f to h / 2) },
        Def(L("خماسي", "Pentagon")) { w, h -> radial(w, h, 5) },
        Def(L("سداسي", "Hexagon")) { w, h -> poly(w * .25f to 0f, w * .75f to 0f, w to h / 2, w * .75f to h, w * .25f to h, 0f to h / 2) },
        Def(L("ثماني", "Octagon")) { w, h ->
            val x = w * .29f; val y = h * .29f
            poly(x to 0f, w - x to 0f, w to y, w to h - y, w - x to h, x to h, 0f to h - y, 0f to y)
        },
        Def(L("متوازي أضلاع", "Parallelogram")) { w, h -> poly(w * .25f to 0f, w to 0f, w * .75f to h, 0f to h) },
        Def(L("شبه منحرف", "Trapezoid")) { w, h -> poly(w * .2f to 0f, w * .8f to 0f, w to h, 0f to h) },
        Def(L("نجمة خماسية", "5-point star")) { w, h -> radial(w, h, 5, .4f) },
        Def(L("نجمة سداسية", "6-point star")) { w, h -> radial(w, h, 6, .58f) },
        Def(L("نجمة ثمانية", "8-point star")) { w, h -> radial(w, h, 8, .72f) },
        Def(L("ختم مسنّن", "Seal")) { w, h -> radial(w, h, 16, .86f) },
        Def(L("بريق", "Sparkle")) { w, h -> radial(w, h, 4, .28f) },
        Def(L("قلب", "Heart")) { w, h ->
            moveTo(w / 2, h * .28f)
            cubicTo(w * .2f, -h * .05f, -w * .12f, h * .38f, w / 2, h)
            cubicTo(w * 1.12f, h * .38f, w * .8f, -h * .05f, w / 2, h * .28f)
            close()
        },
        Def(L("سهم يمين", "Right arrow")) { w, h -> poly(0f to h * .3f, w * .6f to h * .3f, w * .6f to 0f, w to h / 2, w * .6f to h, w * .6f to h * .7f, 0f to h * .7f) },
        Def(L("سهم يسار", "Left arrow")) { w, h -> poly(w to h * .3f, w * .4f to h * .3f, w * .4f to 0f, 0f to h / 2, w * .4f to h, w * .4f to h * .7f, w to h * .7f) },
        Def(L("سهم أعلى", "Up arrow")) { w, h -> poly(w * .3f to h, w * .3f to h * .4f, 0f to h * .4f, w / 2 to 0f, w to h * .4f, w * .7f to h * .4f, w * .7f to h) },
        Def(L("سهم أسفل", "Down arrow")) { w, h -> poly(w * .3f to 0f, w * .7f to 0f, w * .7f to h * .6f, w to h * .6f, w / 2 to h, 0f to h * .6f, w * .3f to h * .6f) },
        Def(L("سهم مزدوج", "Double arrow")) { w, h ->
            poly(0f to h / 2, w * .25f to 0f, w * .25f to h * .3f, w * .75f to h * .3f, w * .75f to 0f, w to h / 2, w * .75f to h, w * .75f to h * .7f, w * .25f to h * .7f, w * .25f to h)
        },
        Def(L("شيفرون", "Chevron")) { w, h -> poly(0f to 0f, w * .7f to 0f, w to h / 2, w * .7f to h, 0f to h, w * .3f to h / 2) },
        Def(L("سهم وسم", "Pentagon arrow")) { w, h -> poly(0f to 0f, w * .78f to 0f, w to h / 2, w * .78f to h, 0f to h) },
        Def(L("زائد", "Plus")) { w, h ->
            val a = w / 3; val b = h / 3
            poly(a to 0f, 2 * a to 0f, 2 * a to b, w to b, w to 2 * b, 2 * a to 2 * b, 2 * a to h, a to h, a to 2 * b, 0f to 2 * b, 0f to b, a to b)
        },
        Def(L("فقاعة كلام", "Speech bubble")) { w, h ->
            val body = Path().apply { addRoundRect(RoundRect(0f, 0f, w, h * .78f, CornerRadius(min(w, h) * .14f))) }
            val tail = Path().apply { poly(w * .2f to h * .7f, w * .42f to h * .7f, w * .16f to h) }
            set(combine(PathOperation.Union, body, tail))
        },
        Def(L("فقاعة بيضاوية", "Oval bubble")) { w, h ->
            val tail = Path().apply { poly(w * .22f to h * .62f, w * .4f to h * .7f, w * .1f to h) }
            set(combine(PathOperation.Union, oval(0f, 0f, w, h * .82f), tail))
        },
        Def(L("سحابة", "Cloud")) { w, h ->
            var p = oval(w * .05f, h * .35f, w * .45f, h * .95f)
            listOf(
                Rect(w * .2f, h * .1f, w * .6f, h * .7f), Rect(w * .45f, h * .05f, w * .85f, h * .6f),
                Rect(w * .6f, h * .3f, w, h * .9f), Rect(w * .3f, h * .4f, w * .75f, h),
            ).forEach { r -> p = combine(PathOperation.Union, p, Path().apply { addOval(r) }) }
            set(p)
        },
        Def(L("شريط عنوان", "Ribbon banner")) { w, h -> poly(0f to 0f, w to 0f, w * .9f to h / 2, w to h, 0f to h, w * .1f to h / 2) },
        Def(L("شريط بطيّات", "Folded ribbon")) { w, h ->
            poly(
                w * .12f to 0f, w * .88f to 0f, w * .88f to h * .3f, w to h * .3f, w * .92f to h * .65f, w to h,
                w * .8f to h, w * .8f to h * .7f, w * .2f to h * .7f, w * .2f to h, 0f to h, w * .08f to h * .65f, 0f to h * .3f, w * .12f to h * .3f,
            )
        },
        Def(L("راية متموجة", "Wave banner")) { w, h ->
            moveTo(0f, h * .15f)
            cubicTo(w * .3f, -h * .1f, w * .6f, h * .4f, w, h * .1f)
            lineTo(w, h * .85f)
            cubicTo(w * .6f, h * 1.1f, w * .3f, h * .6f, 0f, h * .9f)
            close()
        },
        Def(L("تذكرة", "Ticket")) { w, h ->
            val r = min(w, h) * .14f
            var p = Path().apply { addRoundRect(RoundRect(0f, 0f, w, h, CornerRadius(r * .7f))) }
            p = combine(PathOperation.Difference, p, oval(-r, h / 2 - r, r, h / 2 + r))
            set(combine(PathOperation.Difference, p, oval(w - r, h / 2 - r, w + r, h / 2 + r)))
        },
        Def(L("لوحة بزوايا مقعّرة", "Plaque")) { w, h ->
            val r = min(w, h) * .18f
            var p = Path().apply { addRect(Rect(0f, 0f, w, h)) }
            listOf(0f to 0f, w to 0f, 0f to h, w to h).forEach { (x, y) -> p = combine(PathOperation.Difference, p, oval(x - r, y - r, x + r, y + r)) }
            set(p)
        },
        Def(L("نصف دائرة", "Half circle")) { w, h ->
            moveTo(0f, h); arcTo(Rect(0f, 0f, w, h * 2), 180f, 180f, false); close()
        },
        Def(L("ربع دائرة", "Quarter circle")) { w, h ->
            moveTo(0f, h); lineTo(0f, 0f); arcTo(Rect(-w, 0f, w, h * 2), 270f, 90f, false); close()
        },
        Def(L("حلقة", "Ring")) { w, h ->
            addOval(Rect(0f, 0f, w, h)); addOval(Rect(w * .25f, h * .25f, w * .75f, h * .75f))
            fillType = PathFillType.EvenOdd
        },
        Def(L("هلال", "Crescent")) { w, h ->
            set(combine(PathOperation.Difference, oval(0f, 0f, w, h), oval(w * .28f, -h * .06f, w * 1.12f, h * .9f)))
        },
        Def(L("برق", "Lightning")) { w, h -> poly(w * .55f to 0f, w * .1f to h * .58f, w * .45f to h * .58f, w * .35f to h, w * .9f to h * .38f, w * .55f to h * .38f, w * .7f to 0f) },
        Def(L("قطرة", "Teardrop")) { w, h ->
            addRoundRect(RoundRect(Rect(0f, 0f, w, h), CornerRadius(w / 2, h / 2), CornerRadius.Zero, CornerRadius(w / 2, h / 2), CornerRadius(w / 2, h / 2)))
        },
        Def(L("درع", "Shield")) { w, h ->
            moveTo(w / 2, 0f); lineTo(w, h * .15f); lineTo(w, h * .45f)
            cubicTo(w, h * .75f, w * .7f, h * .9f, w / 2, h)
            cubicTo(w * .3f, h * .9f, 0f, h * .75f, 0f, h * .45f)
            lineTo(0f, h * .15f); close()
        },
        Def(L("تبويب", "Tab")) { w, h ->
            val r = min(w, h) * .3f
            addRoundRect(RoundRect(Rect(0f, 0f, w, h), CornerRadius(r), CornerRadius(r), CornerRadius.Zero, CornerRadius.Zero))
        },
        Def(L("زهرة", "Flower")) { w, h ->
            var p = oval(w * .3f, h * .3f, w * .7f, h * .7f)
            for (i in 0 until 6) {
                val a = 2 * PI * i / 6
                val cx = w / 2 + w * .27f * cos(a).toFloat(); val cy = h / 2 + h * .27f * sin(a).toFloat()
                p = combine(PathOperation.Union, p, oval(cx - w * .2f, cy - h * .2f, cx + w * .2f, cy + h * .2f))
            }
            set(p)
        },
        Def(L("موجة", "Wave")) { w, h ->
            moveTo(0f, h * .35f)
            cubicTo(w * .25f, 0f, w * .5f, h * .7f, w * .75f, h * .3f)
            cubicTo(w * .85f, h * .15f, w * .95f, h * .2f, w, h * .3f)
            lineTo(w, h); lineTo(0f, h); close()
        },
    )

    fun path(index: Int, w: Float, h: Float): Path = Path().apply { all[index.coerceIn(all.indices)].build(this, w, h) }

    class ShapeOf(private val index: Int) : Shape {
        override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density) =
            Outline.Generic(path(index, size.width, size.height))
    }

    // ------------------------------------------------------------------------ page frames

    val frames: List<L> = listOf(
        L("خط بسيط", "Simple line"), L("خط مزدوج", "Double line"), L("سميك ورفيع", "Thick & thin"), L("متقطع", "Dashed"),
        L("منقّط", "Dotted"), L("مزدوج مستدير", "Rounded double"), L("أقواس الزوايا", "Corner brackets"), L("زوايا مزخرفة", "Ornate corners"),
        L("زوايا متدرّجة", "Stepped corners"), L("حافة مخرّمة", "Scalloped edge"), L("متعرّج", "Zigzag"), L("متموّج", "Wavy"),
        L("سلسلة نقاط", "Dot chain"), L("سلسلة معيّنات", "Diamond chain"), L("نجوم", "Stars"), L("قلوب", "Hearts"), L("خياطة", "Stitched"),
    )

    /** Points along the rectangle's edges spaced roughly [step] apart, corners included. */
    private fun perimeter(l: Float, t: Float, r: Float, b: Float, step: Float): List<Offset> {
        val out = mutableListOf<Offset>()
        fun edge(a: Offset, z: Offset) {
            val len = (z - a).getDistance()
            val n = (len / step).roundToInt().coerceAtLeast(1)
            for (i in 0 until n) out += a + (z - a) * (i.toFloat() / n)
        }
        edge(Offset(l, t), Offset(r, t)); edge(Offset(r, t), Offset(r, b)); edge(Offset(r, b), Offset(l, b)); edge(Offset(l, b), Offset(l, t))
        return out
    }

    /** Draws frame [style] inside the whole draw area. [sw] is the line width in px, [radius] the corner radius in px. */
    fun DrawScope.drawFrame(style: Int, color: Color, sw: Float, radius: Float) {
        val w = size.width; val h = size.height; val i = sw / 2
        val cr = CornerRadius(radius)
        fun rect(inset: Float, width: Float, effect: PathEffect? = null, r: CornerRadius = cr, cap: StrokeCap = StrokeCap.Butt) = drawRoundRect(
            color, Offset(inset, inset), Size(w - 2 * inset, h - 2 * inset), r, style = Stroke(width, cap = cap, pathEffect = effect),
        )
        when (style.coerceIn(frames.indices)) {
            0 -> rect(i, sw)
            1 -> { rect(i, sw); rect(sw * 3.5f, sw * .6f) }
            2 -> { rect(sw * 1.2f, sw * 2.4f); rect(sw * 4.4f, sw * .6f) }
            3 -> rect(i, sw, PathEffect.dashPathEffect(floatArrayOf(sw * 4, sw * 2.5f)))
            4 -> rect(i, sw, PathEffect.dashPathEffect(floatArrayOf(0.1f, sw * 2.4f)), cap = StrokeCap.Round)
            5 -> { val r = CornerRadius(maxOf(radius, min(w, h) * .06f)); rect(i, sw, r = r); rect(sw * 3.5f, sw * .6f, r = r) }
            6 -> {
                val len = min(w, h) * .18f
                listOf(Offset(i, i) to Offset(1f, 1f), Offset(w - i, i) to Offset(-1f, 1f), Offset(i, h - i) to Offset(1f, -1f), Offset(w - i, h - i) to Offset(-1f, -1f)).forEach { (o, d) ->
                    drawLine(color, o, o + Offset(len * d.x, 0f), sw, StrokeCap.Square)
                    drawLine(color, o, o + Offset(0f, len * d.y), sw, StrokeCap.Square)
                }
            }
            7 -> {
                rect(sw * 2.5f, sw * .7f, r = CornerRadius.Zero)
                val d = sw * 3.2f
                listOf(Offset(sw * 2.5f, sw * 2.5f), Offset(w - sw * 2.5f, sw * 2.5f), Offset(sw * 2.5f, h - sw * 2.5f), Offset(w - sw * 2.5f, h - sw * 2.5f)).forEach { c ->
                    drawPath(Path().apply { poly(c.x to c.y - d, c.x + d to c.y, c.x to c.y + d, c.x - d to c.y) }, color)
                    drawCircle(color, sw * 1.1f, c + Offset(if (c.x < w / 2) d * 1.9f else -d * 1.9f, 0f))
                    drawCircle(color, sw * 1.1f, c + Offset(0f, if (c.y < h / 2) d * 1.9f else -d * 1.9f))
                }
            }
            8 -> {
                val s = min(w, h) * .07f
                val p = Path().apply {
                    poly(
                        i + s to i, w - i - s to i, w - i - s to i + s, w - i to i + s, w - i to h - i - s, w - i - s to h - i - s, w - i - s to h - i,
                        i + s to h - i, i + s to h - i - s, i to h - i - s, i to i + s, i + s to i + s,
                    )
                }
                drawPath(p, color, style = Stroke(sw))
                rect(sw * 3.5f + s, sw * .6f, r = CornerRadius.Zero)
            }
            9, 10, 11 -> {
                // Edge patterns: bumps, teeth or waves running clockwise around the frame.
                val amp = sw * 2.2f
                val step = sw * 6f
                val inset = amp + i
                val p = Path()
                val corners = listOf(Offset(inset, inset), Offset(w - inset, inset), Offset(w - inset, h - inset), Offset(inset, h - inset))
                p.moveTo(corners[0].x, corners[0].y)
                for (k in 0 until 4) {
                    val a = corners[k]; val z = corners[(k + 1) % 4]
                    val dir = (z - a); val len = dir.getDistance(); val u = dir / len
                    val out = Offset(u.y, -u.x) // left of travel = outside for a clockwise loop
                    val n = (len / step).roundToInt().coerceAtLeast(1)
                    val seg = len / n
                    for (j in 0 until n) {
                        val s0 = a + u * (seg * j); val s1 = a + u * (seg * (j + 1)); val mid = (s0 + s1) / 2f
                        when (style) {
                            9 -> p.quadraticTo(mid.x + out.x * amp * 2, mid.y + out.y * amp * 2, s1.x, s1.y)
                            10 -> { p.lineTo(mid.x + out.x * amp, mid.y + out.y * amp); p.lineTo(s1.x, s1.y) }
                            else -> {
                                val q1 = s0 + u * (seg / 4) + out * amp; val q2 = s0 + u * (seg * 3 / 4) - out * amp
                                p.cubicTo(q1.x, q1.y, q2.x, q2.y, s1.x, s1.y)
                            }
                        }
                    }
                }
                p.close()
                drawPath(p, color, style = Stroke(sw * .8f))
            }
            12 -> perimeter(sw * 1.5f, sw * 1.5f, w - sw * 1.5f, h - sw * 1.5f, sw * 4f).forEach { drawCircle(color, sw * 1.1f, it) }
            13 -> perimeter(sw * 2f, sw * 2f, w - sw * 2f, h - sw * 2f, sw * 5f).forEach { c ->
                val d = sw * 1.7f
                drawPath(Path().apply { poly(c.x to c.y - d, c.x + d to c.y, c.x to c.y + d, c.x - d to c.y) }, color)
            }
            14, 15 -> {
                val s = sw * 4f
                perimeter(s / 2 + i, s / 2 + i, w - s / 2 - i, h - s / 2 - i, s * 1.6f).forEach { c ->
                    val shape = Path().apply { (if (style == 14) all[8] else all[13]).build(this, s, s) }
                    shape.translate(c - Offset(s / 2, s / 2))
                    drawPath(shape, color)
                }
            }
            else -> {
                val r = CornerRadius(maxOf(radius, min(w, h) * .05f))
                rect(i, sw, r = r)
                rect(sw * 3f, sw * .6f, PathEffect.dashPathEffect(floatArrayOf(sw * 2.2f, sw * 1.6f)), r = r)
            }
        }
    }

    @Composable
    fun Frame(style: Int, color: Color, widthDp: Float, radiusDp: Float, modifier: Modifier = Modifier) {
        Canvas(modifier.fillMaxSize()) { drawFrame(style, color, widthDp.dp.toPx().coerceAtLeast(1f), radiusDp.dp.toPx()) }
    }

    // ------------------------------------------------------------------------ payment marks

    val payments: List<Pair<String, L>> = listOf(
        "visa" to L("فيزا", "Visa"), "mastercard" to L("ماستركارد", "Mastercard"), "amex" to L("أمريكان إكسبريس", "Amex"),
        "paypal" to L("باي بال", "PayPal"), "applepay" to L("Apple Pay", "Apple Pay"), "googlepay" to L("Google Pay", "Google Pay"),
        "mada" to L("مدى", "mada"), "stcpay" to L("STC Pay", "STC Pay"), "cash" to L("الدفع عند الاستلام", "Cash on delivery"),
        "card" to L("بطاقة بنكية", "Bank card"), "wallet" to L("محفظة", "Wallet"), "bank" to L("تحويل بنكي", "Bank transfer"),
    )

    fun paymentName(kind: String): L = payments.firstOrNull { it.first == kind }?.second ?: L(kind, kind)

    /** A payment brand mark drawn to fit its box (height-driven). Simplified marks for mock-ups. */
    @Composable
    fun PaymentMark(kind: String, modifier: Modifier = Modifier, framed: Boolean = true) =
        // Brand marks read the same way in every language.
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) { PaymentMarkLtr(kind, modifier, framed) }

    @Composable
    private fun PaymentMarkLtr(kind: String, modifier: Modifier, framed: Boolean) {
        BoxWithConstraints(
            modifier
                .then(if (framed) Modifier.clip(RoundedCornerShape(6.dp)).background(if (kind == "amex") Color(0xFF2E77BC) else Color.White).border(1.dp, Color(0x1A000000), RoundedCornerShape(6.dp)) else Modifier),
            contentAlignment = Alignment.Center,
        ) {
            val hgt = maxHeight.value.takeIf { it.isFinite() } ?: 32f
            val fs = (hgt * .42f).sp
            val bold = FontFamily.SansSerif
            when (kind) {
                "visa" -> Text("VISA", color = Color(0xFF1A1F71), fontSize = (hgt * .46f).sp, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic, fontFamily = bold, letterSpacing = (hgt * .02f).sp)
                "mastercard" -> Row(Modifier.offset(x = (hgt * .1f).dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size((hgt * .56f).dp).clip(CircleShape).background(Color(0xFFEB001B)))
                    // Overlap the circles like the real mark.
                    Box(Modifier.offset(x = (-hgt * .2f).dp).size((hgt * .56f).dp).clip(CircleShape).background(Color(0xCCF79E1B)))
                }
                "amex" -> Text("AMEX", color = Color.White, fontSize = fs, fontWeight = FontWeight.Black, fontFamily = bold)
                "paypal" -> Text(buildAnnotatedString {
                    withStyle(SpanStyle(color = Color(0xFF003087))) { append("Pay") }
                    withStyle(SpanStyle(color = Color(0xFF009CDE))) { append("Pal") }
                }, fontSize = fs, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic, fontFamily = bold)
                "applepay" -> Text("Apple Pay", color = Color.Black, fontSize = (hgt * .34f).sp, fontWeight = FontWeight.Bold, fontFamily = bold)
                "googlepay" -> Text(buildAnnotatedString {
                    withStyle(SpanStyle(color = Color(0xFF4285F4))) { append("G") }
                    withStyle(SpanStyle(color = Color(0xFF5F6368))) { append(" Pay") }
                }, fontSize = fs, fontWeight = FontWeight.Bold, fontFamily = bold)
                "mada" -> Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size((hgt * .1f).dp, (hgt * .34f).dp).background(Color(0xFF84B740)))
                    Text(" mada", color = Color(0xFF259BD6), fontSize = fs, fontWeight = FontWeight.Bold, fontFamily = bold)
                }
                "stcpay" -> Text(buildAnnotatedString {
                    withStyle(SpanStyle(color = Color(0xFF4F008C), fontWeight = FontWeight.Black)) { append("stc") }
                    withStyle(SpanStyle(color = Color(0xFF4F008C))) { append(" pay") }
                }, fontSize = fs, fontFamily = bold)
                else -> FontIcon(
                    when (kind) { "cash" -> "payments"; "wallet" -> "account_balance_wallet"; "bank" -> "account_balance"; else -> "credit_card" },
                    when (kind) { "cash" -> Color(0xFF2E9E6A); else -> Color(0xFF3D3A66) }, hgt * .55f, style = 0,
                )
            }
        }
    }

    /** Same marks as plain colors + text, for exported code. */
    fun paymentStyle(kind: String): Triple<String, String, String> = when (kind) { // text, color, background
        "visa" -> Triple("VISA", "#1A1F71", "#FFFFFF")
        "mastercard" -> Triple("●●", "#EB001B", "#FFFFFF")
        "amex" -> Triple("AMEX", "#FFFFFF", "#2E77BC")
        "paypal" -> Triple("PayPal", "#003087", "#FFFFFF")
        "applepay" -> Triple("Apple Pay", "#000000", "#FFFFFF")
        "googlepay" -> Triple("G Pay", "#4285F4", "#FFFFFF")
        "mada" -> Triple("mada", "#259BD6", "#FFFFFF")
        "stcpay" -> Triple("stc pay", "#4F008C", "#FFFFFF")
        else -> Triple("", "#3D3A66", "#FFFFFF")
    }

    fun paymentIcon(kind: String): String? = when (kind) {
        "cash" -> "payments"; "wallet" -> "account_balance_wallet"; "bank" -> "account_balance"; "card" -> "credit_card"; else -> null
    }
}
