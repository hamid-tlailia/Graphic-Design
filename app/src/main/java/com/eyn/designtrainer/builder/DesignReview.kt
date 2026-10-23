package com.eyn.designtrainer.builder

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.eyn.designtrainer.data.Design
import com.eyn.designtrainer.data.El
import com.eyn.designtrainer.data.ElType
import com.eyn.designtrainer.data.argb
import com.eyn.designtrainer.data.c
import com.eyn.designtrainer.i18n.L
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/** Heuristic critique of a freeform design: is it ready to approve, or does it need work? */
object DesignReview {
    enum class Area(val label: L) {
        COLOR(L("الألوان والتباين", "Color & contrast")),
        TYPE(L("الخطوط", "Typography")),
        SPACE(L("الأبعاد والمسافات", "Sizes & spacing")),
        ALIGN(L("المحاذاة والترتيب", "Alignment & order")),
    }

    data class Issue(
        val area: Area,
        val weight: Int, // points removed from the score
        val title: L,
        val detail: L,
        val elementId: String? = null,
        val fix: ((Design) -> Design)? = null,
    )

    data class Result(val score: Int, val issues: List<Issue>, val passed: List<L>) {
        val verdict: L get() = when {
            score >= 85 -> L("جاهز للاعتماد ✓", "Ready to approve ✓")
            score >= 65 -> L("جيد — يحتاج لمسات بسيطة", "Good — needs small touches")
            else -> L("يحتاج تعديلات في الألوان أو الأبعاد", "Needs work on colors or sizes")
        }
    }

    private val TEXTUAL = setOf(ElType.TEXT, ElType.BUTTON, ElType.CHIP, ElType.INPUT, ElType.SEARCH, ElType.CHECKBOX, ElType.CIRCLE)
    private val TOUCH = setOf(ElType.BUTTON, ElType.INPUT, ElType.SEARCH)

    private fun contrast(a: Color, b: Color): Double {
        val la = a.luminance() + 0.05; val lb = b.luminance() + 0.05
        return max(la, lb) / min(la, lb)
    }

    /** The colour actually behind [e]: its own fill, the topmost filled element under it, or the canvas. */
    private fun backdrop(d: Design, e: El): Color {
        if (e.bg.c().alpha > 0.5f && e.type != ElType.TEXT) return e.bg.c()
        if (e.type == ElType.TEXT && e.bg.c().alpha > 0.5f) return e.bg.c()
        val idx = d.els.indexOfFirst { it.id == e.id }
        val cx = e.x + e.w / 2; val cy = e.y + e.h / 2
        for (i in idx - 1 downTo 0) {
            val o = d.els[i]
            if (!o.visible || o.bg.c().alpha < 0.5f) continue
            if (cx in o.x..(o.x + o.w) && cy in o.y..(o.y + o.h)) return o.bg.c()
        }
        return d.bg.c()
    }

    private fun Design.map(id: String, f: (El) -> El) = copy(els = els.map { if (it.id == id) f(it) else it })

    private fun name(e: El) = e.text.take(18).ifBlank { e.type.label().en }
    private fun nameL(e: El) = L(e.text.take(18).ifBlank { e.type.label().ar }, name(e))

    fun review(d: Design): Result {
        val issues = mutableListOf<Issue>()
        val passed = mutableListOf<L>()
        val els = d.els.filter { it.visible }
        val unit = d.w / 360f // sizes are judged relative to a 360-wide phone

        if (els.isEmpty()) return Result(0, listOf(Issue(Area.SPACE, 100, L("اللوحة فارغة", "The canvas is empty"), L("أضف عناصر لتقييم التصميم.", "Add elements to get a review."))), emptyList())

        // --------------------------------------------------------------- contrast
        var lowContrast = 0
        els.filter { it.type in TEXTUAL && it.text.isNotBlank() }.forEach { e ->
            val bg = backdrop(d, e)
            val ratio = contrast(e.fg.c(), bg)
            val large = e.fontSize >= 24 * unit || (e.bold && e.fontSize >= 18 * unit)
            val need = if (large) 3.0 else 4.5
            if (ratio < need) {
                lowContrast++
                val better = if (contrast(Color(0xFF1D1A3F), bg) >= contrast(Color.White, bg)) Color(0xFF1D1A3F) else Color.White
                issues += Issue(
                    Area.COLOR, if (ratio < 3) 10 else 6,
                    L("تباين ضعيف: «${nameL(e).ar}»", "Low contrast: \"${name(e)}\""),
                    L("التباين %.1f:1 والمطلوب %.1f:1 على الأقل. النص صعب القراءة.".format(ratio, need), "Contrast is %.1f:1, needs at least %.1f:1. Hard to read.".format(ratio, need)),
                    e.id,
                ) { it.map(e.id) { el -> el.copy(fg = better.argb()) } }
            }
        }
        if (lowContrast == 0) passed += L("كل النصوص مقروءة بتباين كافٍ", "All text has enough contrast")

        // --------------------------------------------------------------- colour count
        val palette = els.flatMap { listOf(it.bg, it.fg) }.map { it.c() }.filter { it.alpha > 0.3f }
            .map { col -> "%02X%02X%02X".format((col.red * 15).roundToInt(), (col.green * 15).roundToInt(), (col.blue * 15).roundToInt()) }
            .toSet()
        if (palette.size > 6) issues += Issue(
            Area.COLOR, 6, L("ألوان كثيرة (${palette.size})", "Too many colors (${palette.size})"),
            L("استخدم 3 إلى 5 ألوان: محايد للخلفية، لون أساسي، ولون تمييز (قاعدة 60-30-10).", "Stick to 3–5 colors: a neutral, a primary and one accent (60-30-10 rule)."),
        ) else passed += L("عدد الألوان متوازن (${palette.size})", "Balanced number of colors (${palette.size})")

        // --------------------------------------------------------------- typography
        val textEls = els.filter { it.type in TEXTUAL && it.text.isNotBlank() }
        textEls.filter { it.fontSize < 12 * unit }.forEach { e ->
            issues += Issue(
                Area.TYPE, 5, L("خط صغير جداً: «${nameL(e).ar}»", "Text too small: \"${name(e)}\""),
                L("الحجم ${e.fontSize.roundToInt()} — اجعل النص 12 على الأقل (14 للنص الأساسي).", "Size ${e.fontSize.roundToInt()} — use at least 12 (14 for body text)."), e.id,
            ) { it.map(e.id) { el -> el.copy(fontSize = (14 * unit).roundToInt().toFloat()) } }
        }
        val fonts = textEls.groupingBy { it.font }.eachCount()
        if (fonts.size > 2) {
            val main = fonts.maxBy { it.value }.key
            issues += Issue(
                Area.TYPE, 6, L("خطوط كثيرة (${fonts.size})", "Too many fonts (${fonts.size})"),
                L("خط أو خطان يكفيان. استخدم الأوزان والأحجام للتمييز بدلاً من تغيير الخط.", "One or two typefaces are enough; use weight and size for contrast."),
            ) { dd -> dd.copy(els = dd.els.map { if (it.type in TEXTUAL) it.copy(font = main) else it }) }
        } else passed += L("عدد الخطوط مناسب", "Font count is fine")
        val sizes = textEls.map { it.fontSize.roundToInt() }.toSet()
        if (textEls.size >= 3 && sizes.size == 1) issues += Issue(
            Area.TYPE, 5, L("لا يوجد تسلسل بصري", "No visual hierarchy"),
            L("كل النصوص بالحجم نفسه. كبّر العنوان (1.25× على الأقل) ليعرف القارئ ما يقرأ أولاً.", "All text is the same size. Make the headline at least 1.25× bigger."),
        )
        if (sizes.size > 5) issues += Issue(
            Area.TYPE, 3, L("أحجام خطوط كثيرة (${sizes.size})", "Too many font sizes (${sizes.size})"),
            L("استخدم مقياساً ثابتاً من 3 إلى 5 أحجام مثل 12، 14، 18، 24، 32.", "Use a fixed scale of 3–5 sizes such as 12, 14, 18, 24, 32."),
        )

        // --------------------------------------------------------------- spacing grid
        val values = els.flatMap { listOf(it.x, it.y, it.w, if (it.type == ElType.DIVIDER) 4f else it.h) }
        val onGrid = values.count { abs(it - (it / 4).roundToInt() * 4) < 0.5f }.toFloat() / values.size
        if (onGrid < 0.7f) issues += Issue(
            Area.SPACE, 6, L("الأبعاد خارج شبكة 4/8", "Sizes are off the 4/8 grid"),
            L("${(onGrid * 100).roundToInt()}% فقط من المواضع والأحجام من مضاعفات 4. الشبكة تمنح إيقاعاً ثابتاً.", "Only ${(onGrid * 100).roundToInt()}% of positions/sizes are multiples of 4. A grid gives steady rhythm."),
        ) { dd ->
            fun r(v: Float) = (v / 4).roundToInt() * 4f
            dd.copy(els = dd.els.map { it.copy(x = r(it.x), y = r(it.y), w = max(4f, r(it.w)), h = if (it.type == ElType.DIVIDER) it.h else max(4f, r(it.h))) })
        } else passed += L("الأبعاد على شبكة 4/8", "Sizes follow the 4/8 grid")

        // --------------------------------------------------------------- touch targets
        els.filter { it.type in TOUCH && it.h < 40 * unit }.forEach { e ->
            issues += Issue(
                Area.SPACE, 4, L("هدف لمس صغير: «${nameL(e).ar}»", "Small touch target: \"${name(e)}\""),
                L("الارتفاع ${e.h.roundToInt()} — الأزرار والحقول تحتاج 44 على الأقل ليسهل الضغط عليها.", "Height ${e.h.roundToInt()} — buttons and fields need at least 44 to be easy to tap."), e.id,
            ) { it.map(e.id) { el -> val nh = 44 * unit; el.copy(h = nh, y = el.y - (nh - el.h) / 2) } }
        }

        // --------------------------------------------------------------- margins & bounds
        val edge = 16 * unit
        els.forEach { e ->
            val outside = e.x < -0.5f || e.y < -0.5f || e.x + e.w > d.w + 0.5f || e.y + e.h > d.h + 0.5f
            val fullBleed = e.w >= d.w - 1 || e.x <= 0.5f && e.type in setOf(ElType.BOX, ElType.IMAGE, ElType.DIVIDER, ElType.CARD)
            if (outside && !fullBleed) issues += Issue(
                Area.SPACE, 4, L("عنصر يخرج من اللوحة: «${nameL(e).ar}»", "Element leaves the canvas: \"${name(e)}\""),
                L("جزء منه مقصوص. أعده داخل حدود التصميم.", "Part of it is cut off. Move it back inside."), e.id,
            ) { it.map(e.id) { el -> el.copy(x = el.x.coerceIn(0f, max(0f, d.w - el.w)), y = el.y.coerceIn(0f, max(0f, d.h - el.h))) } }
            else if (!fullBleed && e.type in TEXTUAL && (e.x in 0.5f..edge - 1 || (d.w - e.x - e.w) in 0.5f..edge - 1)) issues += Issue(
                Area.SPACE, 3, L("قريب جداً من الحافة: «${nameL(e).ar}»", "Too close to the edge: \"${name(e)}\""),
                L("اترك هامشاً 16 على الأقل بين النص وحافة الشاشة.", "Leave at least a 16 margin between text and the screen edge."), e.id,
            ) { it.map(e.id) { el -> el.copy(x = el.x.coerceIn(edge, max(edge, d.w - edge - el.w))) } }
        }

        // --------------------------------------------------------------- alignment near-misses
        val lefts = els.filter { it.w < d.w - 1 }
        val nearMiss = mutableSetOf<String>()
        for (a in lefts) for (b in lefts) {
            if (a.id >= b.id) continue
            val dx = abs(a.x - b.x)
            val dc = abs((a.x + a.w / 2) - (b.x + b.w / 2))
            val dr = abs((a.x + a.w) - (b.x + b.w))
            if ((dx in 0.5f..4f) || (dc in 0.5f..4f) || (dr in 0.5f..4f)) { nearMiss += a.id; nearMiss += b.id }
        }
        if (nearMiss.isNotEmpty()) issues += Issue(
            Area.ALIGN, 5, L("محاذاة شبه صحيحة (${nearMiss.size} عناصر)", "Almost-aligned elements (${nearMiss.size})"),
            L("بعض العناصر تبعد 1–4 وحدات عن خط مشترك. هذا يبدو كخطأ؛ حاذِها تماماً.", "Some elements are 1–4 units off a shared edge. It reads as a mistake; align them exactly."),
            nearMiss.first(),
        ) { dd ->
            // Snap each near-miss element's left edge to the most common nearby left edge.
            val xs = dd.els.filter { it.id in nearMiss }.map { it.x }
            dd.copy(els = dd.els.map { e ->
                if (e.id !in nearMiss) e else {
                    val target = xs.filter { abs(it - e.x) <= 4f }.groupingBy { it.roundToInt() }.eachCount().maxByOrNull { it.value }?.key?.toFloat() ?: e.x
                    e.copy(x = target)
                }
            })
        } else passed += L("لا توجد محاذاة مرتبكة", "No sloppy alignment")

        // --------------------------------------------------------------- overlapping text
        for (i in textEls.indices) for (j in i + 1 until textEls.size) {
            val a = textEls[i]; val b = textEls[j]
            val ox = min(a.x + a.w, b.x + b.w) - max(a.x, b.x)
            val oy = min(a.y + a.h, b.y + b.h) - max(a.y, b.y)
            if (ox > 4 && oy > 4 && a.type == ElType.TEXT && b.type == ElType.TEXT) {
                issues += Issue(
                    Area.ALIGN, 6, L("نصوص متداخلة", "Overlapping text"),
                    L("«${nameL(a).ar}» و«${nameL(b).ar}» فوق بعضهما. باعد بينهما.", "\"${name(a)}\" and \"${name(b)}\" overlap. Give them space."), b.id,
                ) { it.map(b.id) { el -> el.copy(y = a.y + a.h + 8 * unit) } }
            }
        }

        val score = (100 - issues.sumOf { it.weight }).coerceIn(0, 100)
        return Result(score, issues.sortedByDescending { it.weight }, passed)
    }
}
