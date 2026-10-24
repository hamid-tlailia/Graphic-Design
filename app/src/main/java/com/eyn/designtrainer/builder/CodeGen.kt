package com.eyn.designtrainer.builder

import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.luminance
import com.eyn.designtrainer.data.AppProject
import com.eyn.designtrainer.data.Block
import com.eyn.designtrainer.data.BlockItem
import com.eyn.designtrainer.data.BlockKind
import com.eyn.designtrainer.data.Design
import com.eyn.designtrainer.data.El
import com.eyn.designtrainer.data.ElType
import com.eyn.designtrainer.data.Page
import com.eyn.designtrainer.data.c
import com.eyn.designtrainer.i18n.Lang
import com.eyn.designtrainer.ui.theme.FontCatalog
import kotlin.math.roundToInt

/**
 * Turns a block project into code that renders like the app. Every block is described once as a
 * small layout tree ([N]) that mirrors the Compose layout in Blocks.kt, then printed as HTML/CSS
 * or React Native, so both outputs stay in step with the app.
 */
object CodeGen {
    // ======================================================================== layout tree

    /** Style of a node, in dp/sp (1:1 with CSS px and React Native points). */
    private class S {
        var row = false; var gap = 0f; var flex = 0f
        var w: Float? = null; var h: Float? = null; var wPct: Float? = null; var minH: Float? = null; var aspect: Float? = null
        var ph = 0f; var pv = 0f; var pt: Float? = null; var pb: Float? = null
        var r = 0f; var circle = false; var bg: String? = null; var bw = 0f; var bc: String? = null
        var items: String? = null; var justify: String? = null; var self: String? = null; var clip = false
        var color: String? = null; var fs: Float? = null; var bold = false; var ta: String? = null; var lines = 0; var ls = 0f; var font: String? = null
        var scrollX = false; var photo: String? = null; var overlay = false; var mirror = false
        // Free blocks: absolutely placed children inside a relative box.
        var rel = false; var abs = false; var x = 0f; var y = 0f; var rot = 0f; var alpha = 1f
        var clipPath: String? = null; var borderStyle: String? = null; var shadow = 0f
    }

    /** k: 'b' box, 't' text, 'i' icon (text = icon name), 'm' image (text = data URI). */
    private class N(val k: Char, val s: S, val kids: List<N> = emptyList(), val text: String = "")

    private fun box(style: S.() -> Unit = {}, vararg kids: N?) = N('b', S().apply(style), kids.filterNotNull())
    private fun boxL(style: S.() -> Unit, kids: List<N?>) = N('b', S().apply(style), kids.filterNotNull())
    private fun spacer(h: Float = 0f, w: Float = 0f) = N('b', S().apply { if (h > 0) this.h = h; if (w > 0) this.w = w })
    private fun flexSpacer() = N('b', S().apply { flex = 1f })

    // ======================================================================== theme

    private class T(p: AppProject, val lang: Lang, val image: (String) -> String?, val designs: (String) -> Design? = { null }) {
        val pal = if (p.applyIdentity) p.colors() else Palettes[1]
        val primary = hex(pal.primary); val surface = hex(pal.surface); val accent = hex(pal.accent)
        val bg = hex(pal.bg); val ink = hex(pal.ink); val muted = rgba(pal.ink, 0.55f); val card = "#FFFFFF"
        val onPrimary = if (pal.primary.c().luminance() > 0.55f) ink else "#FFFFFF"
        val font = appFont(p)
        val logoFont = if (p.logoFont >= 0) googleName(FontCatalog.getOrNull(p.logoFont)?.name ?: "IBM Plex") else font
        val iconStyle = p.iconStyle
        val rtl = lang == Lang.AR
        fun ink(a: Float) = rgba(pal.ink, a)
    }

    private fun hex(v: Long) = "#%06X".format(v and 0xFFFFFF)
    private fun rgba(v: Long, a: Float) = "rgba(${(v shr 16) and 0xFF},${(v shr 8) and 0xFF},${v and 0xFF},${"%.2f".format(a)})"
    private fun onColor(v: Long, t: T) = if (v.c().luminance() > 0.5f) t.ink else "#FFFFFF"

    private fun appFont(p: AppProject) = if (!p.applyIdentity) "IBM Plex Sans Arabic" else when (p.font) {
        0 -> "Tajawal"; 1 -> "Cairo"; else -> "IBM Plex Sans Arabic"
    }

    private fun googleName(n: String) = when (n) { "IBM Plex" -> "IBM Plex Sans Arabic"; "Playfair" -> "Playfair Display"; else -> n }

    private val singleWeight = setOf("Lalezar", "Rakkas", "Bebas Neue", "Pacifico", "Lobster")

    private val legacyIcons = mapOf("heart" to "favorite", "cart" to "shopping_cart", "bell" to "notifications", "eye" to "visibility", "chart" to "show_chart", "arrow_fwd" to "arrow_forward")
    private fun iconName(n: String) = legacyIcons[n] ?: n.ifBlank { "circle" }

    // ======================================================================== building blocks

    private fun T.text(v: String, size: Float, color: String = ink, bold: Boolean = false, center: Boolean = false, fill: Boolean = false, lines: Int = 0, ts: Float = 1f) =
        N('t', S().apply { fs = size * ts; this.color = color; this.bold = bold; ta = if (center) "center" else "start"; if (fill || center) wPct = 100f; this.lines = lines }, text = v)

    private fun T.icon(name: String, color: String, size: Float, mirror: Boolean = false) =
        N('i', S().apply { fs = size; this.color = color; this.mirror = mirror && rtl }, text = iconName(name))

    /** Photo or a placeholder with an image icon, filling a sized box. */
    private fun T.placeholder(style: S.() -> Unit, path: String) = boxL({
        style(); bg = surface; clip = true; items = "center"; justify = "center"
    }, listOf(image(path)?.takeIf { path.isNotEmpty() }?.let { N('m', S(), text = it) } ?: icon("image", rgba(pal.primary, 0.35f), 22f)))

    private fun T.circleIcon(size: Float, bgc: String, iconName: String, iconColor: String, iconSize: Float) =
        box({ w = size; h = size; circle = true; bg = bgc; items = "center"; justify = "center" }, icon(iconName, iconColor, iconSize))

    private fun T.logo(p: AppProject, size: Float): N {
        val shapeCircle = p.logoShape == 1
        val onMark = if (p.logoShape == 2) primary else onPrimary
        val mark = if (p.logoStyle == 2) null else box({
            w = size; h = size; if (shapeCircle) circle = true else r = size * 0.3f
            bg = if (p.logoShape == 2) null else primary; items = "center"; justify = "center"
        }, if (p.logoStyle == 0) icon(p.logoIcon, onMark, size * (if (p.logoShape == 2) 0.9f else 0.58f))
        else N('t', S().apply { fs = size * 0.45f; color = onMark; bold = true; font = logoFont }, text = p.name.trim().take(1)))
        val stacked = p.logoLayout == 1
        val name = boxL({ items = if (stacked) "center" else "start" }, listOf(
            N('t', S().apply { fs = size * (if (p.logoStyle == 2) 0.7f else 0.5f) * p.logoScale; color = primary; bold = true; font = logoFont; ls = p.logoSpacing; lines = 1 }, text = p.name),
            if (p.tagline.isNotBlank()) N('t', S().apply { fs = size * 0.2f * p.logoScale; color = muted; font = logoFont; lines = 1 }, text = p.tagline) else null,
        ))
        return if (stacked) boxL({ items = "center"; gap = if (mark != null) size * 0.2f else 0f }, listOf(mark, name))
        else boxL({ row = true; items = "center"; gap = if (mark != null) size * 0.25f else 0f }, listOf(mark, name))
    }

    private fun price(v: String, b: Block) = listOf(v, if (v.isNotBlank()) b.subtitle else "").filter { it.isNotBlank() }.joinToString(" ")

    // ======================================================================== blocks (mirror Blocks.kt)

    private fun T.block(b: Block, p: AppProject): N {
        val pad = b.padding; val rad = b.radius; val ts = b.textScale
        val center = b.align == 1
        val bgc = b.bg?.let { hex(it) }
        val its = b.itemsOr(lang)
        fun tx(v: String, size: Float, color: String = ink, bold: Boolean = false, c: Boolean = false, fill: Boolean = false, lines: Int = 0) = text(v, size, color, bold, c, fill, lines, ts)
        val content: N = when (b.kind) {
            BlockKind.HEADER -> {
                val actions = boxL({ row = true; gap = 8f }, its.map { circleIcon(36f, surface, it.icon, primary, 19f) })
                boxL({ row = true; items = "center"; ph = pad; pv = pad * 0.75f; wPct = 100f; bg = bgc }, when (b.variant) {
                    0 -> listOf(if (p.applyIdentity) logo(p, 34f) else tx(b.title, 18f, bold = true), flexSpacer(), actions)
                    1 -> listOf(circleIcon(36f, surface, "arrow_back", primary, 19f).also { it.kids.first().s.mirror = rtl },
                        boxL({ flex = 1f }, listOf(tx(b.title, 18f, bold = true, c = true))), if (its.isEmpty()) spacer(w = 36f) else actions)
                    2 -> listOf(circleIcon(42f, surface, "person", primary, 22f), spacer(w = 10f),
                        boxL({ flex = 1f }, listOf(tx(b.subtitle, 12f, muted), tx(b.title, 17f, bold = true))), actions)
                    3 -> listOf(boxL({ flex = 1f }, listOf(tx(b.title, 22f, bold = true))), actions)
                    4 -> listOf(flexSpacer(), if (p.applyIdentity) logo(p, 34f) else tx(b.title, 18f, bold = true), boxL({ flex = 1f; row = true; justify = "end" }, listOf(actions)))
                    else -> listOf(boxL({ wPct = 100f }, listOf(
                        tx(b.title, 22f, bold = true), spacer(10f),
                        boxL({ row = true; gap = 18f }, its.mapIndexed { i, it ->
                            val on = i == b.active
                            boxL({ items = "center" }, listOf(tx(it.label, 14f, if (on) primary else muted, bold = on), spacer(4f),
                                box({ w = 18f; h = 3f; r = 2f; bg = if (on) accent else null })))
                        }),
                    )))
                })
            }
            BlockKind.HERO -> {
                val heroBgL = b.bg ?: if (b.variant == 2) pal.primary else pal.surface
                val heroBg = hex(heroBgL)
                val onHero = onColor(heroBgL, this)
                fun heroButton(inverted: Boolean) = if (b.button.isBlank()) null else box({
                    pv = 8f; ph = 16f; r = 10f; bg = if (inverted) "#FFFFFF" else primary
                }, tx(b.button, 13f, if (inverted) primary else onPrimary, bold = true))
                boxL({ ph = pad; pv = pad * 0.5f; wPct = 100f }, listOf(when (b.variant) {
                    1 -> boxL({ row = true; items = "center"; this.r = rad; bg = heroBg; ph = 16f; pv = 16f; wPct = 100f }, listOf(
                        boxL({ flex = 1f; this.items = if (center) "center" else "start" }, listOf(
                            tx(b.title, 18f, onHero, bold = true, c = center), spacer(4f), tx(b.subtitle, 12f, onHero.withAlpha(0.7f), c = center), spacer(12f), heroButton(false))),
                        spacer(w = 12f),
                        placeholder({ w = 96f; h = 96f; this.r = rad * 0.7f }, b.image).also { it.s.bg = "rgba(255,255,255,0.6)" },
                    ))
                    else -> {
                        val photo = b.image.takeIf { it.isNotEmpty() }?.let { image(it) }
                        val on = if (photo != null) "#FFFFFF" else onHero
                        boxL({ wPct = 100f; minH = b.itemHeight.takeIf { it > 0 }; this.r = rad; bg = heroBg; clip = true; justify = "center"; this.photo = photo; overlay = photo != null }, listOf(
                            boxL({ wPct = 100f; ph = 18f; pv = 22f; this.items = if (center) "center" else "start" }, listOf(
                                tx(b.title, 20f, on, bold = true, c = center, fill = true), spacer(6f),
                                tx(b.subtitle, 13f, on.withAlpha(0.75f), c = center, fill = true),
                                if (b.button.isNotBlank()) spacer(14f) else null, heroButton(b.variant == 2),
                            )),
                        ))
                    }
                }))
            }
            BlockKind.SEARCH -> {
                val bh = if (b.itemHeight > 0) b.itemHeight else 48f
                boxL({ row = true; items = "center"; ph = pad; pv = pad * 0.5f; wPct = 100f; bg = bgc; gap = if (b.variant == 1) 10f else 0f }, listOf(
                    box({ flex = 1f; row = true; items = "center"; h = bh; this.r = rad; bg = "#FFFFFF"; bw = 1f; bc = ink(0.08f); ph = 14f; gap = 8f },
                        icon("search", muted, 20f), tx(b.title, 14f, muted, lines = 1)),
                    if (b.variant == 1) box({ w = bh; h = bh; this.r = rad; bg = primary; this.items = "center"; justify = "center" }, icon("tune", onPrimary, 20f)) else null,
                ))
            }
            BlockKind.CATEGORIES -> boxL({ ph = pad; pv = pad * 0.5f; wPct = 100f; bg = bgc }, listOf(
                if (b.title.isNotBlank()) tx(b.title, 16f, bold = true, c = center, fill = true) else null,
                if (b.title.isNotBlank()) spacer(10f) else null,
                boxL({ row = true; scrollX = true; wPct = 100f; gap = if (b.variant == 0) 14f else 8f }, its.mapIndexed { i, it ->
                    val on = i == b.active
                    if (b.variant == 0) {
                        val s = if (b.itemHeight > 0) b.itemHeight else 54f
                        box({ this.items = "center" },
                            box({ w = s; h = s; circle = true; bg = if (on) primary else surface; this.items = "center"; justify = "center" },
                                if (it.icon.isNotEmpty()) icon(it.icon, if (on) onPrimary else primary, s * 0.45f) else null),
                            spacer(6f), tx(it.label, 12f, ink, bold = on))
                    } else box({ row = true; this.items = "center"; circle = true; bg = if (on) primary else surface; ph = 14f; pv = 7f; gap = if (it.icon.isNotEmpty()) 6f else 0f },
                        if (it.icon.isNotEmpty()) icon(it.icon, if (on) onPrimary else primary, 16f) else null,
                        tx(it.label, 13f, if (on) onPrimary else ink, bold = on))
                }),
            ))
            BlockKind.CARDS -> boxL({ ph = pad; pv = pad * 0.5f; wPct = 100f; bg = bgc }, listOf(
                if (b.variant == CV.SECTION) box({ row = true; this.items = "center"; wPct = 100f },
                    box({ flex = 1f }, tx(b.title, 18f, bold = true), if (b.subtitle.isNotBlank()) tx(b.subtitle, 12f, muted) else null),
                    if (b.button.isNotBlank()) tx(b.button, 13f, primary, bold = true) else null,
                ) else null,
                if (b.title.isNotBlank() && b.variant !in ownTitle) tx(b.title, 16f, bold = true, c = center, fill = true) else null,
                if (b.title.isNotBlank() && b.variant !in ownTitle) spacer(10f) else null,
                when (b.variant) {
                    0 -> boxL({ gap = 12f; wPct = 100f }, its.chunked(2).map { pair ->
                        boxL({ row = true; gap = 12f; wPct = 100f }, pair.map { item ->
                            box({ flex = 1f; this.r = rad; bg = card; ph = 10f; pv = 10f },
                                placeholder({ wPct = 100f; if (b.itemHeight > 0) h = b.itemHeight else aspect = 1.3f; this.r = rad * 0.7f }, item.image.ifEmpty { b.image }),
                                spacer(8f), tx(item.label, 14f, bold = true, lines = 1),
                                box({ row = true; this.items = "center"; wPct = 100f },
                                    boxL({ flex = 1f }, listOf(tx(price(item.value, b), 13f, accent, bold = true))),
                                    box({ w = 26f; h = 26f; circle = true; bg = primary; this.items = "center"; justify = "center" }, tx("+", 15f, onPrimary, bold = true))),
                            )
                        } + if (pair.size == 1) listOf(flexSpacer()) else emptyList())
                    })
                    1 -> boxL({ gap = 10f; wPct = 100f }, its.map { item ->
                        box({ row = true; this.items = "center"; wPct = 100f; this.r = rad; bg = card; ph = 10f; pv = 10f; gap = 12f },
                            placeholder({ val s = if (b.itemHeight > 0) b.itemHeight else 56f; w = s; h = s; this.r = rad * 0.6f }, item.image.ifEmpty { b.image }),
                            box({ flex = 1f }, tx(item.label, 14f, bold = true), tx(price(item.value, b), 13f, accent)),
                            icon("favorite", accent, 18f))
                    })
                    else -> {
                        val item = its.firstOrNull() ?: BlockItem("")
                        box({ wPct = 100f; this.r = rad; bg = card; clip = true },
                            placeholder({ wPct = 100f; h = if (b.itemHeight > 0) b.itemHeight else 130f }, item.image.ifEmpty { b.image }),
                            box({ ph = 14f; pv = 14f; wPct = 100f; this.items = if (center) "center" else "start" },
                                tx(item.label, 16f, bold = true, c = center), tx(price(item.value, b), 13f, muted, c = center)))
                    }
                },
            ))
            BlockKind.LIST -> boxL({ ph = pad; pv = pad * 0.5f; wPct = 100f; bg = bgc }, listOf(
                if (b.title.isNotBlank()) tx(b.title, 16f, bold = true, c = center, fill = true) else null,
                if (b.title.isNotBlank()) spacer(10f) else null,
                boxL({ wPct = 100f; this.r = rad; bg = card; clip = true }, its.flatMapIndexed { i, item ->
                    listOfNotNull(
                        boxL({ row = true; this.items = "center"; wPct = 100f; ph = 14f; pv = 12f; minH = b.itemHeight.takeIf { it > 0 }; gap = 12f }, listOf(
                            if (item.icon.isNotEmpty()) box({ w = 34f; h = 34f; this.r = 10f; bg = surface; this.items = "center"; justify = "center" }, icon(item.icon, primary, 18f)) else null,
                            boxL({ flex = 1f }, listOf(tx(item.label, 14f))),
                            if (item.value.isNotBlank()) tx(item.value, 13f, accent, bold = true) else null,
                            if (b.variant == 0) icon("chevron_right", muted, 20f, mirror = true)
                            else box({ w = 38f; h = 22f; circle = true; bg = if (i != 1) primary else surface; row = true; this.items = "center"; justify = if (i != 1) "end" else "start"; ph = 3f },
                                box({ w = 16f; h = 16f; circle = true; bg = "#FFFFFF" })),
                        )),
                        if (i < its.lastIndex) box({ h = 1f; wPct = 100f; bg = ink(0.06f) }) else null,
                    )
                }),
            ))
            BlockKind.TEXT -> boxL({ ph = pad; pv = pad * 0.5f; wPct = 100f; bg = bgc; this.items = if (center) "center" else "start" }, if (b.variant == 0) listOf(
                tx(b.title, 18f, bold = true, c = center, fill = true), spacer(6f), tx(b.subtitle, 14f, muted, c = center, fill = true),
            ) else listOf(box({ wPct = 100f; this.r = rad; bg = surface; ph = 14f; pv = 14f },
                tx(b.title, 14f, primary, bold = true, c = center, fill = true), spacer(4f), tx(b.subtitle, 13f, ink(0.75f), c = center, fill = true))))
            BlockKind.FORM -> {
                val fields = if (b.variant == 0) listOf("البريد الإلكتروني" to "Email", "كلمة المرور" to "Password")
                else listOf("الاسم" to "Name", "البريد الإلكتروني" to "Email", "رسالتك" to "Your message")
                boxL({ ph = pad; pv = pad * 0.5f; wPct = 100f; bg = bgc }, listOf(tx(b.title, 20f, bold = true, c = center, fill = true), spacer(12f)) +
                    fields.mapIndexed { i, (ar, en) ->
                        box({ wPct = 100f; h = if (b.variant == 1 && i == 2) 84f else if (b.itemHeight > 0) b.itemHeight else 48f; this.r = rad; bg = "#FFFFFF"; bw = 1f; bc = ink(0.1f); ph = 14f; pv = 13f },
                            tx(if (rtl) ar else en, 13f, muted))
                    }.flatMap { listOf(it, spacer(10f)) } + listOf(spacer(4f),
                    box({ wPct = 100f; h = if (b.itemHeight > 0) b.itemHeight else 50f; this.r = rad; bg = primary; this.items = "center"; justify = "center" }, tx(b.button, 15f, onPrimary, bold = true))))
            }
            BlockKind.BUTTON -> {
                val bh = if (b.itemHeight > 0) b.itemHeight else 50f
                box({ row = true; gap = 10f; ph = pad; pv = pad * 0.5f; wPct = 100f; bg = bgc },
                    if (b.variant == 1) box({ flex = 1f; h = bh; this.r = rad; bw = 1.5f; bc = rgba(pal.primary, 0.4f); this.items = "center"; justify = "center" }, tx(b.subtitle, 15f, primary, bold = true)) else null,
                    box({ flex = 1f; h = bh; this.r = rad; bg = primary; this.items = "center"; justify = "center" }, tx(b.button, 15f, onPrimary, bold = true)))
            }
            BlockKind.FOOTER -> if (b.variant == 0) boxL({ row = true; wPct = 100f; bg = bgc ?: "#FFFFFF"; pv = pad + 2f }, its.mapIndexed { i, item ->
                val on = i == b.active
                box({ flex = 1f; this.items = "center" },
                    box({ circle = true; bg = if (on) surface else null; ph = 12f; pv = 3f }, icon(item.icon.ifEmpty { "circle" }, if (on) primary else muted, 22f)),
                    tx(item.label, 11f, if (on) primary else muted, bold = on, lines = 1))
            }) else {
                val fbg = b.bg ?: pal.primary
                val on = onColor(fbg, this)
                boxL({ wPct = 100f; bg = hex(fbg); ph = pad; pv = pad; this.items = if (center) "center" else "start" }, listOf(
                    tx(b.title, 18f, on, bold = true, c = center, fill = true), spacer(8f),
                    boxL({ row = true; gap = 16f }, its.map { tx(it.label, 13f, on.withAlpha(0.8f)) }), spacer(10f),
                    tx(b.subtitle, 11f, on.withAlpha(0.6f), c = center, fill = true),
                ))
            }
            BlockKind.CUSTOM -> boxL({ ph = pad; pv = pad * 0.5f; wPct = 100f; bg = bgc; this.items = if (center) "center" else "start" }, listOf(
                if (b.variant == CV.SECTION) box({ row = true; this.items = "center"; wPct = 100f },
                    box({ flex = 1f }, tx(b.title, 18f, bold = true), if (b.subtitle.isNotBlank()) tx(b.subtitle, 12f, muted) else null),
                    if (b.button.isNotBlank()) tx(b.button, 13f, primary, bold = true) else null,
                ) else null,
                if (b.title.isNotBlank() && b.variant !in ownTitle) tx(b.title, 16f, bold = true, c = center, fill = true) else null,
                if (b.title.isNotBlank() && b.variant !in ownTitle) spacer(10f) else null,
                when (b.variant) {
                    0 -> boxL({ row = true; wPct = 100f }, its.map { item ->
                        val s = if (b.itemHeight > 0) b.itemHeight else 52f
                        box({ flex = 1f; ph = 4f; this.items = "center" },
                            box({ w = s; h = s; this.r = rad; bg = surface; this.items = "center"; justify = "center" }, if (item.icon.isNotEmpty()) icon(item.icon, primary, s * 0.48f) else null),
                            spacer(6f), tx(item.label, 12f, bold = true, c = true, lines = 2),
                            if (item.value.isNotBlank()) tx(price(item.value, b), 11f, accent, c = true) else null)
                    })
                    1 -> boxL({ wPct = 100f; this.r = rad; bg = card; clip = true }, its.flatMapIndexed { i, item ->
                        listOfNotNull(
                            box({ row = true; this.items = "center"; wPct = 100f; ph = 14f; pv = 12f; minH = b.itemHeight.takeIf { it > 0 }; gap = 12f },
                                if (item.icon.isNotEmpty()) box({ w = 36f; h = 36f; this.r = 10f; bg = surface; this.items = "center"; justify = "center" }, icon(item.icon, primary, 19f)) else null,
                                box({ flex = 1f }, tx(item.label, 14f)), tx(price(item.value, b), 14f, accent, bold = true)),
                            if (i < its.lastIndex) box({ h = 1f; wPct = 100f; bg = ink(0.06f) }) else null,
                        )
                    })
                    CV.CARD_GRID -> boxL({ gap = 10f; wPct = 100f }, its.chunked(2).map { pair ->
                        boxL({ row = true; gap = 10f; wPct = 100f }, pair.map { item ->
                            box({ flex = 1f; this.r = rad; bg = card; ph = 12f; pv = 12f; this.items = if (center) "center" else "start" },
                                if (item.image.isNotEmpty()) placeholder({ wPct = 100f; h = if (b.itemHeight > 0) b.itemHeight else 84f; this.r = rad * 0.6f }, item.image)
                                else circleIcon(42f, surface, item.icon.ifEmpty { "circle" }, primary, 22f),
                                spacer(8f), tx(item.label, 13f, bold = true, c = center),
                                if (item.value.isNotBlank()) tx(price(item.value, b), 12f, accent, c = center) else null)
                        } + if (pair.size == 1) listOf(flexSpacer()) else emptyList())
                    })
                    else -> customExtra(b, its, rad, center)
                },
            ))
        }
        // Width %, alignment and minimum height wrap every block, like BlockView.
        return box({ wPct = 100f; this.items = if (center) "center" else "start" },
            box({ wPct = b.widthPct.coerceIn(0.2f, 1f) * 100f; minH = b.minHeight.takeIf { it > 0 } }, content))
    }

    private val ownTitle = setOf(CV.SECTION, CV.DIVIDER, CV.BANNER, CV.PRODUCT, CV.PROFILE, CV.ALERT, CV.ADD_BAR, CV.FREE, CV.CALENDAR, CV.EMPTY, CV.ONBOARD, CV.COUNTDOWN)

    private fun argb(v: Long): String {
        val a = ((v shr 24) and 0xFF) / 255f
        return if (a >= 0.999f) hex(v) else rgba(v, a)
    }

    /** A Compose shape outline as a CSS polygon (outer contour, sampled). */
    private fun polygon(index: Int, w: Float, h: Float): String {
        val path = Shapes.path(index, w, h).asAndroidPath()
        val pm = android.graphics.PathMeasure(path, true)
        val pts = mutableListOf<String>()
        val pos = FloatArray(2)
        val n = 72
        for (i in 0 until n) {
            pm.getPosTan(pm.length * i / n, pos, null)
            pts += "${"%.1f".format(pos[0] / w * 100)}% ${"%.1f".format(pos[1] / h * 100)}%"
        }
        return "polygon(${pts.joinToString(",")})"
    }

    /** One canvas element of a free block, absolutely placed. */
    private fun T.freeEl(e: El): N? {
        if (!e.visible) return null
        val textAlign = when (e.align) { 1 -> "center"; 2 -> "right"; else -> "left" }
        fun label(size: Float = e.fontSize) = N('t', S().apply {
            fs = size; color = argb(e.fg); bold = e.bold; ta = textAlign; wPct = 100f; ls = e.letterSpacing
            font = FontCatalog.getOrNull(e.font)?.let { googleName(it.name) }
        }, text = e.text)
        val kids: List<N?> = when (e.type) {
            ElType.ICON -> listOf(icon(e.icon, argb(e.fg), minOf(e.w, e.h)))
            ElType.IMAGE, ElType.AVATAR -> listOf(
                e.image.takeIf { it.isNotEmpty() }?.let { image(it) }?.let { N('m', S(), text = it) }
                    ?: icon(if (e.type == ElType.AVATAR) "person" else "image", rgba(e.fg, 0.5f), minOf(e.w, e.h) * 0.4f),
            )
            ElType.PAYMENT -> listOf(pay(e.icon, e.h))
            ElType.RATING -> listOf(boxL({ row = true; justify = "between"; wPct = 100f }, (0 until 5).map { i ->
                icon(if (i < (e.value * 5).roundToInt()) "star" else "star_border", argb(e.fg), minOf(e.h, e.w / 5f))
            }))
            ElType.PROGRESS, ElType.SLIDER -> listOf(box({ h = e.h; wPct = e.value.coerceIn(0f, 1f) * 100f; r = e.radius; bg = argb(e.fg) }))
            ElType.TOGGLE -> listOf(box({ w = e.h * 0.76f; h = e.h * 0.76f; circle = true; bg = "#FFFFFF" }))
            ElType.CHECKBOX -> listOf(
                box({ w = e.h * 0.8f; h = e.h * 0.8f; r = e.h * 0.2f; bg = if (e.value >= 0.5f) argb(e.bg) else null; bw = 1.5f; bc = argb(e.bg); items = "center"; justify = "center" },
                    if (e.value >= 0.5f) icon("check", "#FFFFFF", e.h * 0.65f) else null),
                box({ flex = 1f }, label()),
            )
            ElType.SEARCH -> listOf(icon("search", argb(e.fg), e.h * 0.45f), box({ flex = 1f }, label()))
            else -> if (e.text.isNotEmpty()) listOf(label()) else emptyList()
        }
        val hasBox = e.type !in setOf(ElType.TOGGLE, ElType.RATING, ElType.CHECKBOX, ElType.PAYMENT)
        return boxL({
            abs = true; x = e.x; y = e.y; w = e.w; h = e.h; rot = e.rotation; alpha = e.opacity
            row = e.type in setOf(ElType.CHECKBOX, ElType.SEARCH, ElType.TOGGLE, ElType.PROGRESS, ElType.SLIDER, ElType.RATING)
            gap = if (e.type == ElType.CHECKBOX || e.type == ElType.SEARCH) 8f else 0f
            items = when (e.vAlign) { 0 -> "start"; 2 -> "end"; else -> "center" }.takeIf { row } ?: when (e.align) { 1 -> "center"; 2 -> "end"; else -> "start" }
            justify = if (row) (if (e.type == ElType.TOGGLE) (if (e.value >= 0.5f) "end" else "start") else null) else when (e.vAlign) { 0 -> "start"; 2 -> "end"; else -> "center" }
            ph = e.padH + if (e.type == ElType.INPUT || e.type == ElType.SEARCH) 14f else 0f; pv = e.padV
            if (e.type == ElType.TOGGLE) { circle = true; bg = argb(if (e.value >= 0.5f) e.bg else e.borderColor); ph = e.h * 0.12f }
            if (hasBox) {
                if (e.type == ElType.CIRCLE || e.type == ElType.AVATAR) circle = true else r = e.radius
                if (e.type == ElType.PROGRESS || e.type == ElType.SLIDER) { r = e.radius; bg = argb(e.bg) } else bg = argb(e.bg).takeIf { (e.bg shr 24) and 0xFF != 0L }
                if (e.type == ElType.FRAME) { bw = maxOf(e.borderW, 1f); bc = argb(e.borderColor); borderStyle = when (e.shape) { 1, 2, 5 -> "double"; 3 -> "dashed"; 4 -> "dotted"; else -> null }; if (e.shape == 1 || e.shape == 2 || e.shape == 5) bw = maxOf(e.borderW * 2, 3f) }
                else if (e.borderW > 0f) { bw = e.borderW; bc = argb(e.borderColor) }
                if (e.type == ElType.SHAPE) { r = 0f; clipPath = polygon(e.shape, e.w, e.h) }
                clip = true; shadow = e.shadow
            }
        }, kids)
    }

    /** A free block: its canvas drawing at design size, children placed absolutely. */
    private fun T.free(b: Block): N {
        val d = designs(b.designId) ?: return box({ wPct = 100f; h = 120f; r = 16f; bw = 2f; bc = rgba(pal.primary, 0.3f); items = "center"; justify = "center" },
            icon("draw", primary, 28f))
        return boxL({ rel = true; w = d.w; h = d.h; clip = true; bg = argb(d.bg).takeIf { (d.bg shr 24) and 0xFF != 0L }
            d.bgImage.takeIf { it.isNotEmpty() }?.let { photo = image(it) } }, d.els.map { freeEl(it) })
    }

    /** Payment brand mark as plain boxes and text. */
    private fun T.pay(kind: String, height: Float): N {
        val (label, color, back) = Shapes.paymentStyle(kind)
        val dot = height * 0.56f
        val content: N = Shapes.paymentIcon(kind)?.let { icon(it, if (kind == "cash") "#2E9E6A" else color, height * 0.55f) }
            ?: if (kind == "mastercard") {
                // Rows run right-to-left in Arabic; keep red on the left like the real mark.
                val circles = listOf(box({ w = dot; h = dot; circle = true; bg = "#EB001B" }), box({ w = dot; h = dot; circle = true; bg = "rgba(247,158,27,0.85)" }))
                boxL({ row = true }, if (rtl) circles.reversed() else circles)
            } else N('t', S().apply { fs = height * (if (label.length > 6) 0.3f else 0.4f); this.color = color; bold = true; lines = 1 }, text = label)
        return box({ w = height * 1.6f; h = height; r = 6f; bg = back; bw = 1f; bc = "rgba(0,0,0,0.10)"; items = "center"; justify = "center" }, content)
    }

    private fun T.stepper(qty: Int, size: Float = 28f) = box({ row = true; items = "center"; gap = 4f },
        box({ w = size; h = size; circle = true; bw = 1f; bc = ink(0.15f); items = "center"; justify = "center" }, icon("remove", ink, size * 0.55f)),
        box({ w = size + 4f; items = "center" }, text(qty.toString(), 14f, bold = true, center = true)),
        box({ w = size; h = size; circle = true; bg = primary; items = "center"; justify = "center" }, icon("add", onPrimary, size * 0.55f)))

    private fun T.stars(rating: Float, size: Float = 14f) = boxL({ row = true }, (0 until 5).map { i -> icon(if (i < rating.roundToInt()) "star" else "star_border", "#FFB400", size) })

    private fun T.button(label: String, height: Float, radius: Float, flex: Boolean = true) =
        box({ if (flex) this.flex = 1f; h = height; r = radius; bg = primary; items = "center"; justify = "center"; ph = 16f }, text(label, 15f, onPrimary, bold = true))

    /** Custom variants from [CV.CART] on, mirroring CustomBlocks.kt. */
    private fun T.customExtra(b: Block, its: List<BlockItem>, rad: Float, center: Boolean): N {
        val ih = b.itemHeight
        fun pr(v: String) = price(v, b)
        val line = { box({ h = 1f; wPct = 100f; bg = ink(0.06f) }) }
        return when (b.variant) {
            CV.CART -> boxL({ gap = 10f; wPct = 100f }, its.map { item ->
                box({ row = true; items = "center"; wPct = 100f; r = rad; bg = card; ph = 10f; pv = 10f; gap = 12f },
                    if (b.shows(CV.OPT_IMAGE)) placeholder({ w = if (ih > 0) ih else 64f; h = if (ih > 0) ih else 64f; r = rad * 0.6f }, item.image) else null,
                    box({ flex = 1f; gap = 4f }, text(item.label, 14f, bold = true, lines = 1), text(pr(item.value), 13f, accent, bold = true)),
                    box({ items = "end"; gap = 8f },
                        if (b.shows(CV.OPT_DELETE)) icon("delete", muted, 20f) else null,
                        if (b.shows(CV.OPT_STEPPER)) stepper(item.qty) else text("×${item.qty}", 13f, muted)))
            })
            CV.SUMMARY -> boxL({ wPct = 100f; r = rad; bg = card; ph = 14f; pv = 14f }, its.map { item ->
                box({ row = true; wPct = 100f; pv = 5f }, box({ flex = 1f }, text(item.label, 14f, muted)),
                    text(pr(item.value), 14f, if (item.value.trim().startsWith("-")) "#2E9E6A" else ink))
            } + (if (b.shows(CV.OPT_TOTAL)) listOf(spacer(6f), line(), spacer(8f),
                box({ row = true; wPct = 100f }, box({ flex = 1f }, text(if (rtl) "الإجمالي" else "Total", 16f, bold = true)), text(pr(CV.total(its)), 16f, primary, bold = true)))
            else emptyList()) + (if (b.button.isNotBlank()) listOf(spacer(14f), box({ row = true; wPct = 100f }, button(b.button, 48f, rad * 0.8f))) else emptyList()))
            CV.PAY_METHODS -> boxL({ gap = 8f; wPct = 100f }, its.mapIndexed { i, item ->
                val on = i == b.active
                box({ row = true; items = "center"; wPct = 100f; minH = if (ih > 0) ih else 56f; r = rad; bg = card; bw = if (on) 2f else 1f; bc = if (on) primary else ink(0.08f); ph = 12f; gap = 12f },
                    pay(item.icon, 32f), box({ flex = 1f }, text(item.label, 14f, lines = 1)),
                    box({ w = 22f; h = 22f; circle = true; bw = 2f; bc = if (on) primary else ink(0.25f); items = "center"; justify = "center" },
                        if (on) box({ w = 11f; h = 11f; circle = true; bg = primary }) else null))
            })
            CV.PAY_LOGOS -> boxL({ row = true; gap = 8f; wPct = 100f; justify = if (center) "center" else "start" }, its.map { pay(it.icon, if (ih > 0) ih else 30f) })
            CV.SECTION -> spacer()
            CV.PROMO -> box({ row = true; gap = 8f; wPct = 100f },
                box({ flex = 1f; row = true; items = "center"; h = if (ih > 0) ih else 48f; r = rad; bg = "#FFFFFF"; bw = 1f; bc = ink(0.12f); ph = 12f; gap = 8f },
                    icon("local_offer", muted, 18f), text(b.subtitle, 13f, muted, lines = 1)),
                button(b.button, if (ih > 0) ih else 48f, rad, flex = false))
            CV.STEPS -> boxL({ row = true; wPct = 100f }, its.mapIndexed { i, item ->
                val done = i < b.active; val now = i == b.active
                box({ flex = 1f; items = "center" },
                    box({ row = true; items = "center"; wPct = 100f },
                        box({ flex = 1f; h = 2f; bg = if (i == 0) null else if (i <= b.active) primary else ink(0.12f) }),
                        box({ w = 30f; h = 30f; circle = true; bg = if (done || now) primary else surface; items = "center"; justify = "center" },
                            if (done) icon("check", onPrimary, 16f) else text("${i + 1}", 13f, if (now) onPrimary else muted, bold = true)),
                        box({ flex = 1f; h = 2f; bg = if (i == its.lastIndex) null else if (i < b.active) primary else ink(0.12f) })),
                    if (b.shows(CV.OPT_LABELS)) spacer(6f) else null,
                    if (b.shows(CV.OPT_LABELS)) text(item.label, 11f, if (now) primary else muted, bold = now, center = true, lines = 1) else null)
            })
            CV.TOGGLES -> boxL({ wPct = 100f; r = rad; bg = card; clip = true }, its.flatMapIndexed { i, item ->
                listOfNotNull(
                    box({ row = true; items = "center"; wPct = 100f; minH = if (ih > 0) ih else 56f; ph = 14f; gap = 12f },
                        if (item.icon.isNotEmpty()) icon(item.icon, primary, 22f) else null, box({ flex = 1f }, text(item.label, 14f)),
                        box({ row = true; w = 44f; h = 26f; circle = true; bg = if (item.on) primary else ink(0.15f); ph = 3f; items = "center"; justify = if (item.on) "end" else "start" },
                            box({ w = 20f; h = 20f; circle = true; bg = "#FFFFFF" }))),
                    if (i < its.lastIndex) line() else null,
                )
            })
            CV.REVIEWS -> boxL({ gap = 10f; wPct = 100f }, its.map { item ->
                box({ wPct = 100f; r = rad; bg = card; ph = 14f; pv = 14f; gap = 8f },
                    box({ row = true; items = "center"; gap = 10f },
                        if (b.shows(CV.OPT_IMAGE)) (if (item.image.isNotEmpty()) placeholder({ w = 40f; h = 40f; circle = true }, item.image)
                        else box({ w = 40f; h = 40f; circle = true; bg = surface; items = "center"; justify = "center" }, text(item.label.take(1), 16f, primary, bold = true))) else null,
                        box({ flex = 1f }, text(item.label, 14f, bold = true, lines = 1), if (b.shows(CV.OPT_RATING)) stars(item.value.toFloatOrNull() ?: 5f) else null)),
                    if (item.note.isNotBlank()) text(item.note, 13f, muted) else null)
            })
            CV.DIVIDER -> box({ row = true; items = "center"; wPct = 100f; pv = 6f; gap = 12f },
                box({ flex = 1f; h = 1f; bg = ink(0.12f) }), if (b.title.isNotBlank()) text(b.title, 13f, muted) else null, box({ flex = 1f; h = 1f; bg = ink(0.12f) }))
            CV.BANNER -> {
                val photo = b.image.takeIf { it.isNotEmpty() }?.let { image(it) }
                box({ wPct = 100f; h = if (ih > 0) ih else 150f; r = rad; bg = surface; clip = true; justify = "end"; this.photo = photo; overlay = photo != null; items = if (photo == null) "center" else "start" },
                    if (photo == null && b.title.isBlank()) icon("image", rgba(pal.primary, 0.35f), 34f) else null,
                    if (b.title.isNotBlank()) box({ wPct = 100f; ph = 14f; pv = 14f; bg = if (photo == null) "rgba(0,0,0,0.45)" else null }, text(b.title, 18f, "#FFFFFF", bold = true, center = center, fill = true)) else null)
            }
            CV.PRODUCT -> box({ wPct = 100f },
                placeholder({ wPct = 100f; h = if (ih > 0) ih else 220f; r = rad }, b.image), spacer(14f),
                box({ row = true; items = "center"; wPct = 100f; gap = 8f }, box({ flex = 1f }, text(b.title, 20f, bold = true)),
                    if (b.shows(CV.OPT_FAV)) icon("favorite_border", accent, 22f) else null, text(pr(b.price), 18f, accent, bold = true)),
                if (b.shows(CV.OPT_RATING)) box({ row = true; items = "center"; gap = 6f; pt = 4f }, stars(4.6f), text("4.6", 12f, muted)) else null,
                if (b.body.isNotBlank()) spacer(10f) else null, if (b.body.isNotBlank()) text(b.body, 13f, muted) else null,
                spacer(16f),
                box({ row = true; items = "center"; wPct = 100f; gap = 14f },
                    if (b.shows(CV.OPT_STEPPER)) stepper(1, 34f) else null,
                    if (b.button.isNotBlank()) button(b.button, 50f, rad * 0.7f) else null))
            CV.STATS -> boxL({ row = true; gap = 10f; wPct = 100f }, its.map { item ->
                box({ flex = 1f; r = rad; bg = card; pv = 14f; items = "center" }, text(item.value, 20f, primary, bold = true), text(item.label, 12f, muted, center = true, lines = 2))
            })
            CV.PROFILE -> {
                val s = if (ih > 0) ih else 60f
                box({ row = true; items = "center"; wPct = 100f; r = rad; bg = card; ph = 14f; pv = 14f; gap = 12f },
                    if (b.image.isNotEmpty()) placeholder({ w = s; h = s; circle = true }, b.image) else circleIcon(s, surface, "person", primary, 30f),
                    box({ flex = 1f }, text(b.title, 16f, bold = true, lines = 1), if (b.subtitle.isNotBlank()) text(b.subtitle, 12f, muted, lines = 1) else null),
                    if (b.button.isNotBlank()) box({ circle = true; bg = surface; ph = 12f; pv = 7f }, text(b.button, 12f, primary, bold = true)) else null)
            }
            CV.ALERT -> box({ row = true; wPct = 100f; r = rad; bg = rgba(pal.primary, 0.10f); bw = 1f; bc = rgba(pal.primary, 0.25f); ph = 12f; pv = 12f; gap = 10f },
                icon(b.icon.ifBlank { "info" }, primary, 24f),
                box({ flex = 1f }, text(b.title, 14f, bold = true), if (b.body.isNotBlank()) text(b.body, 12f, muted) else null),
                if (b.shows(CV.OPT_CLOSE)) icon("close", muted, 18f) else null)
            CV.ICON_GRID -> boxL({ gap = 12f; wPct = 100f }, its.chunked(4).map { group ->
                boxL({ row = true; wPct = 100f }, group.map { item ->
                    val s = if (ih > 0) ih else 56f
                    box({ flex = 1f; items = "center"; gap = 6f },
                        box({ w = s; h = s; r = rad; bg = surface; items = "center"; justify = "center" }, icon(item.icon.ifEmpty { "circle" }, primary, s * 0.46f)),
                        text(item.label, 12f, center = true, lines = 1))
                } + List(4 - group.size) { flexSpacer() })
            })
            CV.ADD_BAR -> box({ row = true; items = "center"; wPct = 100f; gap = 14f },
                if (b.shows(CV.OPT_STEPPER)) stepper(1, 36f) else null,
                box({ flex = 1f; row = true; items = "center"; h = if (ih > 0) ih else 52f; r = rad; bg = primary; ph = 16f },
                    box({ flex = 1f }, text(b.button, 15f, onPrimary, bold = true)), if (b.price.isNotBlank()) text(pr(b.price), 15f, onPrimary, bold = true) else null))
            CV.FREE -> free(b)
            CV.NOTIFS -> boxL({ gap = 8f; wPct = 100f }, its.map { item ->
                box({ row = true; wPct = 100f; r = rad; bg = if (item.on) rgba(pal.primary, 0.06f) else card; ph = 12f; pv = 12f; gap = 12f },
                    if (b.shows(CV.OPT_ICON)) circleIcon(40f, surface, item.icon.ifEmpty { "notifications" }, primary, 20f) else null,
                    box({ flex = 1f },
                        box({ row = true; items = "center"; wPct = 100f }, box({ flex = 1f }, text(item.label, 14f, bold = true, lines = 1)),
                            if (item.value.isNotBlank()) text(item.value, 11f, muted) else null),
                        if (item.note.isNotBlank()) text(item.note, 12f, muted, lines = 2) else null),
                    if (b.shows(CV.OPT_DOT) && item.on) box({ w = 8f; h = 8f; circle = true; bg = accent; pt = 6f }) else null)
            })
            CV.CHAT -> boxL({ gap = 8f; wPct = 100f }, its.map { item ->
                box({ row = true; wPct = 100f; justify = if (item.on) "end" else "start"; items = "end"; gap = 6f },
                    if (!item.on && b.shows(CV.OPT_IMAGE)) circleIcon(30f, surface, "support_agent", primary, 16f) else null,
                    box({ r = rad; bg = if (item.on) primary else card; ph = 12f; pv = 8f },
                        text(item.label, 14f, if (item.on) onPrimary else ink),
                        if (item.value.isNotBlank()) text(item.value, 10f, if (item.on) onPrimary.withAlpha(0.7f) else muted) else null))
            } + if (b.shows(CV.OPT_INPUT)) listOf(box({ row = true; items = "center"; wPct = 100f; gap = 8f; pt = 4f },
                box({ flex = 1f; h = 46f; circle = true; bg = "#FFFFFF"; bw = 1f; bc = ink(0.1f); ph = 16f; justify = "center" }, text(b.subtitle, 13f, muted, lines = 1)),
                box({ w = 46f; h = 46f; circle = true; bg = primary; items = "center"; justify = "center" }, icon("send", onPrimary, 20f, mirror = true)))) else emptyList())
            CV.FAQ -> boxL({ wPct = 100f; r = rad; bg = card; clip = true }, its.flatMapIndexed { i, item ->
                listOfNotNull(
                    box({ wPct = 100f; ph = 14f; pv = 14f; gap = 6f },
                        box({ row = true; items = "center"; wPct = 100f }, box({ flex = 1f }, text(item.label, 14f, bold = true)), icon(if (item.on) "expand_less" else "expand_more", muted, 22f)),
                        if (item.on && item.note.isNotBlank()) text(item.note, 13f, muted) else null),
                    if (i < its.lastIndex) line() else null,
                )
            })
            CV.TIMELINE -> boxL({ wPct = 100f; r = rad; bg = card; ph = 14f; pv = 14f }, its.mapIndexed { i, item ->
                val done = i <= b.active
                box({ row = true; wPct = 100f; gap = 12f },
                    box({ items = "center"; minH = if (i < its.lastIndex) 56f else null },
                        box({ w = 24f; h = 24f; circle = true; bg = if (done) primary else surface; items = "center"; justify = "center" },
                            if (done) icon(if (i == b.active) "radio_button_checked" else "check", onPrimary, 14f) else null),
                        if (i < its.lastIndex) box({ w = 2f; flex = 1f; bg = if (i < b.active) primary else ink(0.12f) }) else null),
                    box({ flex = 1f },
                        box({ row = true; wPct = 100f }, box({ flex = 1f }, text(item.label, 14f, if (done) ink else muted, bold = i == b.active)), if (item.value.isNotBlank()) text(item.value, 12f, muted) else null),
                        if (item.note.isNotBlank()) text(item.note, 12f, muted) else null))
            })
            CV.CALENDAR -> box({ wPct = 100f; r = rad; bg = card; ph = 12f; pv = 12f; gap = 4f },
                box({ row = true; items = "center"; wPct = 100f }, icon("chevron_left", muted, 22f), box({ flex = 1f }, text(b.title, 15f, bold = true, center = true)), icon("chevron_right", muted, 22f)),
                boxL({ row = true; wPct = 100f; pt = 6f }, weekDays(lang).map { box({ flex = 1f }, text(it, 10f, muted, center = true, lines = 1)) }),
                boxL({ wPct = 100f; gap = 4f }, monthGrid().chunked(7).map { week ->
                    boxL({ row = true; wPct = 100f }, week.map { day ->
                        box({ flex = 1f; h = 34f; items = "center"; justify = "center" },
                            if (day > 0) box({ w = 32f; h = 32f; circle = true; bg = if (day == b.active) primary else null; items = "center"; justify = "center" },
                                text("$day", 13f, if (day == b.active) onPrimary else ink, bold = day == b.active)) else null)
                    } + List(7 - week.size) { flexSpacer() })
                }))
            CV.EMPTY -> {
                val sz = if (ih > 0) ih else 96f
                box({ wPct = 100f; pv = 20f; items = "center"; gap = 6f },
                    circleIcon(sz, surface, b.icon.ifBlank { "inbox" }, primary, sz * 0.45f), spacer(10f),
                    text(b.title, 18f, bold = true, center = true), if (b.body.isNotBlank()) text(b.body, 13f, muted, center = true) else null,
                    if (b.button.isNotBlank()) box({ r = rad; bg = primary; ph = 28f; pv = 12f; pt = 12f }, text(b.button, 14f, onPrimary, bold = true)) else null)
            }
            CV.ONBOARD -> box({ wPct = 100f; items = "center" },
                if (b.subtitle.isNotBlank()) box({ wPct = 100f; items = "end" }, text(b.subtitle, 13f, muted)) else null, spacer(10f),
                placeholder({ wPct = 100f; h = if (ih > 0) ih else 260f; r = rad }, b.image), spacer(22f),
                text(b.title, 22f, bold = true, center = true), if (b.body.isNotBlank()) text(b.body, 14f, muted, center = true) else null,
                if (b.shows(CV.OPT_DOTS)) boxL({ row = true; gap = 6f; pt = 18f }, (0 until 3).map { i -> box({ h = 8f; w = if (i == 0) 22f else 8f; circle = true; bg = if (i == 0) primary else ink(0.15f) }) }) else null,
                if (b.button.isNotBlank()) spacer(20f) else null,
                if (b.button.isNotBlank()) box({ row = true; wPct = 100f }, button(b.button, 52f, rad * 0.6f)) else null)
            CV.PRICING -> boxL({ gap = 10f; wPct = 100f }, its.map { item ->
                val on = if (item.on) onPrimary else ink
                boxL({ wPct = 100f; r = rad; bg = if (item.on) primary else card; bw = 1f; bc = if (item.on) primary else ink(0.08f); ph = 16f; pv = 16f; gap = 4f }, listOf(
                    box({ row = true; items = "center"; wPct = 100f }, box({ flex = 1f }, text(item.label, 16f, on, bold = true)),
                        if (item.on) box({ circle = true; bg = accent; ph = 10f; pv = 3f }, text(if (rtl) "الأكثر طلباً" else "Popular", 11f, "#FFFFFF", bold = true)) else null),
                    box({ row = true; items = "end"; gap = 4f; pv = 6f }, text(pr(item.value), 24f, on, bold = true), if (b.body.isNotBlank()) text(b.body, 12f, on.withAlpha(0.7f)) else null),
                ) + item.note.lines().filter { it.isNotBlank() }.map { f ->
                    box({ row = true; items = "center"; gap = 6f; pv = 2f }, icon("check", if (item.on) onPrimary else primary, 16f), text(f, 13f, on.withAlpha(0.85f)))
                } + if (b.button.isNotBlank()) listOf(spacer(12f), box({ row = true; wPct = 100f },
                    box({ flex = 1f; h = 44f; r = rad * 0.7f; bg = if (item.on) "#FFFFFF" else primary; items = "center"; justify = "center" },
                        text(b.button, 14f, if (item.on) primary else onPrimary, bold = true)))) else emptyList())
            })
            CV.COUNTDOWN -> box({ wPct = 100f; r = rad; bg = primary; ph = 16f; pv = 16f; items = "center"; gap = 10f },
                if (b.title.isNotBlank()) text(b.title, 14f, onPrimary.withAlpha(0.85f), center = true) else null,
                boxL({ row = true; gap = 8f }, its.map { item ->
                    box({ w = 60f; r = 12f; bg = "rgba(255,255,255,0.15)"; pv = 8f; items = "center" }, text(item.value, 22f, onPrimary, bold = true), text(item.label, 11f, onPrimary.withAlpha(0.8f), lines = 1))
                }))
            CV.CHECKS -> boxL({ wPct = 100f; r = rad; bg = card; clip = true }, its.flatMapIndexed { i, item ->
                listOfNotNull(
                    box({ row = true; items = "center"; wPct = 100f; minH = 52f; ph = 14f; gap = 12f },
                        box({ w = 22f; h = 22f; r = 6f; bg = if (item.on) primary else null; bw = 2f; bc = if (item.on) primary else ink(0.25f); items = "center"; justify = "center" },
                            if (item.on) icon("check", onPrimary, 16f) else null),
                        box({ flex = 1f }, text(item.label, 14f)),
                        if (item.value.isNotBlank()) text("+" + pr(item.value.trimStart('+')), 13f, muted) else null),
                    if (i < its.lastIndex) line() else null,
                )
            })
            else -> spacer()
        }
    }

    private fun String.withAlpha(a: Float): String = if (this == "#FFFFFF") "rgba(255,255,255,$a)" else if (startsWith("#")) {
        val v = substring(1).toLong(16); rgba(v, a)
    } else this

    /** A page: top blocks, then pinned blocks pushed to the bottom of a min-height screen. */
    private fun T.page(page: Page, p: AppProject): Pair<N, N> =
        boxL({ wPct = 100f }, page.blocks.filter { !it.pinBottom }.map { block(it, p) }) to
            boxL({ wPct = 100f }, page.blocks.filter { it.pinBottom }.map { block(it, p) })

    // ======================================================================== HTML

    private fun esc(s: String) = s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")

    private fun css(n: N, t: T): String = buildString {
        val s = n.s
        if (n.k == 'b') {
            append("display:flex;flex-direction:${if (s.row) "row" else "column"};")
            append("align-items:${align(s.items ?: "start")};")
            s.justify?.let { append("justify-content:${justify(it)};") }
        }
        if (s.gap > 0) append("gap:${px(s.gap)};")
        if (s.flex > 0) append("flex:${s.flex} 1 0;min-width:0;")
        s.w?.let { append("width:${px(it)};flex-shrink:0;") }
        s.h?.let { append("height:${px(it)};flex-shrink:0;") }
        s.wPct?.let { append("width:${"%.1f".format(it)}%;") }
        s.minH?.let { append("min-height:${px(it)};") }
        s.aspect?.let { append("aspect-ratio:$it;") }
        val pt = s.pt ?: s.pv; val pb = s.pb ?: s.pv
        if (pt > 0 || pb > 0 || s.ph > 0) append("padding:${px(pt)} ${px(s.ph)} ${px(pb)};")
        if (s.circle) append("border-radius:999px;") else if (s.r > 0) append("border-radius:${px(s.r)};")
        if (s.photo != null) append("background:${if (s.overlay) "linear-gradient(rgba(0,0,0,.38),rgba(0,0,0,.38))," else ""}url('${s.photo}') center/cover;")
        else s.bg?.let { append("background:$it;") }
        if (s.bw > 0) append("border:${px(s.bw)} solid ${s.bc};")
        if (s.clip) append("overflow:hidden;")
        if (s.scrollX) append("overflow-x:auto;scrollbar-width:none;")
        if (s.rel) append("position:relative;")
        if (s.abs) append("position:absolute;left:${px(s.x)};top:${px(s.y)};")
        if (s.rot != 0f) append("transform:rotate(${s.rot}deg);")
        if (s.alpha < 1f) append("opacity:${"%.2f".format(s.alpha)};")
        s.clipPath?.let { append("clip-path:$it;") }
        s.borderStyle?.let { append("border-style:$it;") }
        if (s.shadow > 0f) append("box-shadow:0 ${px(s.shadow / 2)} ${px(s.shadow)} rgba(0,0,0,.18);")
        if (n.k == 't') {
            append("font-size:${px(s.fs ?: 14f)};line-height:1.45;color:${s.color};font-weight:${if (s.bold) 700 else 400};text-align:${s.ta ?: "start"};")
            if (s.ta == "left" || s.ta == "right") append("unicode-bidi:plaintext;")
            if (s.ls != 0f) append("letter-spacing:${px(s.ls)};")
            s.font?.let { append("font-family:'$it';") }
            if (s.lines == 1) append("white-space:nowrap;overflow:hidden;text-overflow:ellipsis;max-width:100%;")
            else if (s.lines > 1) append("display:-webkit-box;-webkit-line-clamp:${s.lines};-webkit-box-orient:vertical;overflow:hidden;")
        }
        if (n.k == 'i') {
            append("font-size:${px(s.fs ?: 24f)};color:${s.color};line-height:1;width:${px(s.fs ?: 24f)};height:${px(s.fs ?: 24f)};flex-shrink:0;")
            if (s.mirror) append("transform:scaleX(-1);")
        }
    }

    private fun px(v: Float) = if (v == v.roundToInt().toFloat()) "${v.roundToInt()}px" else "${"%.1f".format(v)}px"
    private fun align(a: String) = when (a) { "center" -> "center"; "end" -> "flex-end"; "stretch" -> "stretch"; else -> "flex-start" }
    private fun justify(j: String) = when (j) { "center" -> "center"; "end" -> "flex-end"; "between" -> "space-between"; "evenly" -> "space-evenly"; else -> "flex-start" }

    private val iconClass = listOf("material-icons-round", "material-icons", "material-icons-outlined", "material-icons-sharp", "material-icons-two-tone")
    private val iconFamily = listOf("Material+Icons+Round", "Material+Icons", "Material+Icons+Outlined", "Material+Icons+Sharp", "Material+Icons+Two+Tone")

    private fun html(n: N, t: T, indent: String = ""): String = when (n.k) {
        't' -> "$indent<div style=\"${css(n, t)}\">${esc(n.text)}</div>"
        'i' -> "$indent<span class=\"${iconClass[t.iconStyle.coerceIn(iconClass.indices)]}\" style=\"${css(n, t)}\">${esc(n.text)}</span>"
        'm' -> "$indent<img src=\"${n.text}\" alt=\"\" style=\"width:100%;height:100%;object-fit:cover;display:block\">"
        else -> if (n.kids.isEmpty()) "$indent<div style=\"${css(n, t)}\"></div>"
        else "$indent<div style=\"${css(n, t)}\">\n" + n.kids.joinToString("\n") { html(it, t, "$indent  ") } + "\n$indent</div>"
    }

    private fun fontLink(name: String) = "family=" + name.replace(' ', '+') + if (name in singleWeight) "" else ":wght@400;700"

    /** The presentation board header: logo, app icon, palette, typeface and icon style. */
    private fun T.boardHeader(p: AppProject): N {
        val roles = listOf(pal.primary to (if (rtl) "أساسي" else "Primary"), pal.surface to (if (rtl) "ثانوي" else "Surface"),
            pal.accent to (if (rtl) "تمييز" else "Accent"), pal.bg to (if (rtl) "خلفية" else "Background"), pal.ink to (if (rtl) "نص" else "Text"))
        val icons = (listOfNotNull(if (p.logoStyle == 0) p.logoIcon else null) +
            p.pages.flatMap { it.blocks }.filter { it.itemsHaveIcons() }.flatMap { b -> b.itemsOr(lang).map { it.icon } } +
            listOf("home", "search", "favorite", "person", "shopping_bag", "notifications")).filter { it.isNotBlank() }.distinct().take(6)
        return box({ row = true; items = "center"; wPct = 100f; r = 32f; bg = "rgba(255,255,255,.92)"; ph = 40f; pv = 32f; gap = 36f },
            box({ flex = 1f; gap = 10f }, logo(p, 64f), text(if (rtl) "هوية بصرية · ${p.pages.size} شاشات" else "Visual identity · ${p.pages.size} screens", 16f, muted)),
            box({ w = 130f; h = 130f; r = 32f; bg = primary; items = "center"; justify = "center" },
                if (p.logoStyle == 0) icon(p.logoIcon, onPrimary, 76f) else N('t', S().apply { fs = 64f; bold = true; color = onPrimary; font = logoFont }, text = p.name.trim().take(1))),
            boxL({ gap = 12f }, listOf(text(if (rtl) "لوحة الألوان" else "Color palette", 18f), boxL({ row = true; gap = 12f }, roles.map { (col, name) ->
                box({ items = "center"; w = 76f }, box({ w = 54f; h = 54f; circle = true; bg = hex(col); bw = 2f; bc = ink(0.08f) }), spacer(6f),
                    N('t', S().apply { fs = 12f; bold = true; color = ink; ta = "center"; wPct = 100f }, text = hex(col)), text(name, 11f, muted, center = true))
            }))),
            box({ items = "center"; w = 150f }, text(if (rtl) "الخط" else "Typeface", 18f), N('t', S().apply { fs = 44f; bold = true; color = primary; font = this@boardHeader.font }, text = "Aa أب"), text(font, 13f, muted)),
            boxL({ items = "center"; w = 150f; gap = 12f }, listOf(text(if (rtl) "نمط الأيقونات" else "Icon style", 18f)) +
                icons.chunked(3).map { row -> boxL({ this.row = true; gap = 16f }, row.map { icon(it, primary, 32f) }) }),
        )
    }

    fun html(p: AppProject, lang: Lang, image: (String) -> String? = { null }, designs: (String) -> Design? = { null }): String {
        val t = T(p, lang, image, designs)
        val fonts = listOf(t.font, t.logoFont).distinct().joinToString("&") { fontLink(it) }
        // Every phone has the same, real-phone height, like the presentation board.
        val screenH = BOARD_SCREEN_H
        val screens = p.pages.mapIndexed { i, page ->
            val (top, bottom) = t.page(page, p)
            """    <figure class="phone">
      <main class="screen" style="height:${screenH}px">
        <div class="scroll">
${html(top, t, "          ")}
        </div>
${html(bottom, t, "        ")}
      </main>
      <figcaption>${"%02d".format(i + 1)} · ${esc(page.name)}</figcaption>
    </figure>"""
        }.joinToString("\n")
        return """<!doctype html>
<html lang="${if (t.rtl) "ar" else "en"}" dir="${if (t.rtl) "rtl" else "ltr"}">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>${esc(p.name)}</title>
<!-- Generated by Risha · Design Trainer -->
<link href="https://fonts.googleapis.com/css2?$fonts&display=swap" rel="stylesheet">
<link href="https://fonts.googleapis.com/icon?family=${iconFamily[t.iconStyle.coerceIn(iconFamily.indices)]}" rel="stylesheet">
<style>
  * { box-sizing: border-box; margin: 0; }
  html { background: color-mix(in srgb, #2B2622 55%, ${t.surface}); }
  body { font-family: '${t.font}', system-ui, sans-serif; color: ${t.ink}; padding: 40px 20px;
         background: linear-gradient(${t.surface}, #fff 30%, ${t.surface} 62%, color-mix(in srgb, #2B2622 55%, ${t.surface})); min-height: 100vh; }
  .board { max-width: 1680px; margin: 0 auto; display: flex; flex-direction: column; gap: 48px; }
  .phones { display: flex; flex-wrap: wrap; gap: 40px; justify-content: center; align-items: flex-start; }
  .phone { margin: 0; display: flex; flex-direction: column; align-items: center; gap: 14px; }
  .screen { width: 378px; background: ${t.bg}; border: 9px solid #15131F; border-radius: 52px; overflow: hidden;
            display: flex; flex-direction: column; padding-top: 18px; box-shadow: 0 24px 40px -12px rgba(0,0,0,.45); }
  .scroll { flex: 1; min-height: 0; overflow-y: auto; scrollbar-width: none; }
  .scroll::-webkit-scrollbar { display: none; }
  figcaption { font-weight: 700; font-size: 18px; color: ${t.ink(0.7f)}; }
  [style*="overflow-x:auto"]::-webkit-scrollbar { display: none; }
  @media (max-width: 900px) { .board > div:first-child { flex-wrap: wrap; } }
</style>
</head>
<body>
<div class="board">
${html(t.boardHeader(p), t, "  ")}
  <section class="phones">
$screens
  </section>
</div>
</body>
</html>
"""
    }

    // ======================================================================== React Native

    private fun js(s: String) = s.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n")

    private fun rnStyle(n: N, t: T): String {
        val s = n.s
        val out = mutableListOf<String>()
        fun cross(a: String) = when (a) { "center" -> "'center'"; "stretch" -> "'stretch'"; "end" -> if (!s.row && t.rtl) "'flex-start'" else "'flex-end'"; else -> if (!s.row && t.rtl) "'flex-end'" else "'flex-start'" }
        if (n.k == 'b') {
            if (s.row) out += "flexDirection: '${if (t.rtl) "row-reverse" else "row"}'"
            out += "alignItems: ${cross(s.items ?: "start")}"
            s.justify?.let { out += "justifyContent: '${justify(it)}'" }
        }
        if (s.gap > 0) out += "gap: ${s.gap.fmt()}"
        if (s.flex > 0) { out += "flex: ${s.flex.fmt()}"; out += "minWidth: 0" }
        s.w?.let { out += "width: ${it.fmt()}" }
        s.h?.let { out += "height: ${it.fmt()}" }
        s.wPct?.let { out += "width: '${"%.1f".format(it)}%'" }
        s.minH?.let { out += "minHeight: ${it.fmt()}" }
        s.aspect?.let { out += "aspectRatio: $it" }
        if (s.ph > 0) out += "paddingHorizontal: ${s.ph.fmt()}"
        val pt = s.pt ?: s.pv; val pb = s.pb ?: s.pv
        if (pt > 0) out += "paddingTop: ${pt.fmt()}"
        if (pb > 0) out += "paddingBottom: ${pb.fmt()}"
        if (s.circle) out += "borderRadius: 999" else if (s.r > 0) out += "borderRadius: ${s.r.fmt()}"
        s.bg?.let { out += "backgroundColor: '$it'" }
        if (s.bw > 0) { out += "borderWidth: ${s.bw.fmt()}"; out += "borderColor: '${s.bc}'" }
        if (s.clip) out += "overflow: 'hidden'"
        if (s.abs) { out += "position: 'absolute'"; out += "left: ${s.x.fmt()}"; out += "top: ${s.y.fmt()}" }
        if (s.alpha < 1f) out += "opacity: ${"%.2f".format(s.alpha)}"
        s.borderStyle?.let { if (it == "dashed" || it == "dotted") out += "borderStyle: '$it'" }
        if (s.rot != 0f) out += "transform: [{ rotate: '${s.rot.fmt()}deg' }]"
        if (n.k == 't') {
            val fs = s.fs ?: 14f
            out += "fontSize: ${fs.fmt()}"; out += "lineHeight: ${(fs * 1.45f).fmt()}"; s.color?.let { out += "color: '$it'" }
            if (s.bold) out += "fontWeight: '700'"
            out += "textAlign: '${when (s.ta) { "center" -> "center"; "left", "right" -> s.ta; else -> if (t.rtl) "right" else "left" }}'"
            if (s.ls != 0f) out += "letterSpacing: ${s.ls.fmt()}"
            out += "fontFamily: '${(s.font ?: t.font).replace(" ", "")}'"
        }
        if (s.mirror) out += "transform: [{ scaleX: -1 }]"
        return out.joinToString(", ")
    }

    private fun Float.fmt() = if (this == roundToInt().toFloat()) roundToInt().toString() else "%.1f".format(this)

    private fun rn(n: N, t: T, indent: String): String = when (n.k) {
        't' -> "$indent<Text style={{ ${rnStyle(n, t)} }}${if (n.s.lines > 0) " numberOfLines={${n.s.lines}}" else ""}>{'${js(n.text)}'}</Text>"
        'i' -> "$indent<MaterialIcons name=\"${n.text}\" size={${(n.s.fs ?: 24f).fmt()}} color=\"${n.s.color}\"${if (n.s.mirror) " style={{ transform: [{ scaleX: -1 }] }}" else ""} />"
        'm' -> "$indent<Image source={{ uri: '${n.text}' }} style={{ width: '100%', height: '100%' }} resizeMode=\"cover\" />"
        else -> {
            val tag = if (n.s.scrollX) "ScrollView" else if (n.s.photo != null) "ImageBackground" else "View"
            val extra = when {
                n.s.scrollX -> " horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={{ ${rnStyle(n, t)} }}"
                n.s.photo != null -> " source={{ uri: '${n.s.photo}' }} imageStyle={{ borderRadius: ${n.s.r.fmt()} }} style={{ ${rnStyle(n, t)} }}"
                else -> " style={{ ${rnStyle(n, t)} }}"
            }
            val overlay = if (n.s.overlay) "\n$indent  <View style={{ position: 'absolute', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.38)' }} />" else ""
            if (n.kids.isEmpty() && overlay.isEmpty()) "$indent<$tag$extra />"
            else "$indent<$tag$extra>$overlay\n" + n.kids.joinToString("\n") { rn(it, t, "$indent  ") } + "\n$indent</$tag>"
        }
    }

    fun reactNative(p: AppProject, lang: Lang, image: (String) -> String? = { null }, designs: (String) -> Design? = { null }): String {
        val t = T(p, lang, image, designs)
        val screens = p.pages.mapIndexed { i, page ->
            val (top, bottom) = t.page(page, p)
            """// ${page.name}
export function Screen${i + 1}() {
  return (
    <View style={{ flex: 1, backgroundColor: '${t.bg}' }}>
      <ScrollView style={{ flex: 1 }} showsVerticalScrollIndicator={false}>
${rn(top, t, "        ")}
      </ScrollView>
${rn(bottom, t, "      ")}
    </View>
  );
}"""
        }.joinToString("\n\n")
        val fontNote = listOf(t.font, t.logoFont).distinct().joinToString(", ")
        return """// Generated by Risha · Design Trainer
// Expo / React Native. Icons: @expo/vector-icons (MaterialIcons).
// Fonts used: $fontNote — load them with expo-google-fonts, e.g. useFonts({ ${t.font.replace(" ", "")}: ... }).
// Layout mirrors the app${if (t.rtl) " in right-to-left (rows use row-reverse, text aligns right)" else ""}.
import React from 'react';
import { SafeAreaView, ScrollView, View, Text, Image, ImageBackground } from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';

$screens

export default function App() {
  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: '${t.bg}' }}>
      <Screen1 />
    </SafeAreaView>
  );
}
"""
    }
}
