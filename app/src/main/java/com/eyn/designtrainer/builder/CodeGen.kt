package com.eyn.designtrainer.builder

import androidx.compose.ui.graphics.luminance
import com.eyn.designtrainer.data.AppProject
import com.eyn.designtrainer.data.Block
import com.eyn.designtrainer.data.BlockItem
import com.eyn.designtrainer.data.BlockKind
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
    }

    /** k: 'b' box, 't' text, 'i' icon (text = icon name), 'm' image (text = data URI). */
    private class N(val k: Char, val s: S, val kids: List<N> = emptyList(), val text: String = "")

    private fun box(style: S.() -> Unit = {}, vararg kids: N?) = N('b', S().apply(style), kids.filterNotNull())
    private fun boxL(style: S.() -> Unit, kids: List<N?>) = N('b', S().apply(style), kids.filterNotNull())
    private fun spacer(h: Float = 0f, w: Float = 0f) = N('b', S().apply { if (h > 0) this.h = h; if (w > 0) this.w = w })
    private fun flexSpacer() = N('b', S().apply { flex = 1f })

    // ======================================================================== theme

    private class T(p: AppProject, val lang: Lang, val image: (String) -> String?) {
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
                if (b.title.isNotBlank()) tx(b.title, 16f, bold = true, c = center, fill = true) else null,
                if (b.title.isNotBlank()) spacer(10f) else null,
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
                if (b.title.isNotBlank()) tx(b.title, 16f, bold = true, c = center, fill = true) else null,
                if (b.title.isNotBlank()) spacer(10f) else null,
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
                    else -> boxL({ gap = 10f; wPct = 100f }, its.chunked(2).map { pair ->
                        boxL({ row = true; gap = 10f; wPct = 100f }, pair.map { item ->
                            box({ flex = 1f; this.r = rad; bg = card; ph = 12f; pv = 12f; this.items = if (center) "center" else "start" },
                                if (item.image.isNotEmpty()) placeholder({ wPct = 100f; h = if (b.itemHeight > 0) b.itemHeight else 84f; this.r = rad * 0.6f }, item.image)
                                else circleIcon(42f, surface, item.icon.ifEmpty { "circle" }, primary, 22f),
                                spacer(8f), tx(item.label, 13f, bold = true, c = center),
                                if (item.value.isNotBlank()) tx(price(item.value, b), 12f, accent, c = center) else null)
                        } + if (pair.size == 1) listOf(flexSpacer()) else emptyList())
                    })
                },
            ))
        }
        // Width %, alignment and minimum height wrap every block, like BlockView.
        return box({ wPct = 100f; this.items = if (center) "center" else "start" },
            box({ wPct = b.widthPct.coerceIn(0.2f, 1f) * 100f; minH = b.minHeight.takeIf { it > 0 } }, content))
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
        if (n.k == 't') {
            append("font-size:${px(s.fs ?: 14f)};line-height:1.45;color:${s.color};font-weight:${if (s.bold) 700 else 400};text-align:${s.ta ?: "start"};")
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

    fun html(p: AppProject, lang: Lang, image: (String) -> String? = { null }): String {
        val t = T(p, lang, image)
        val fonts = listOf(t.font, t.logoFont).distinct().joinToString("&") { fontLink(it) }
        val screens = p.pages.mapIndexed { i, page ->
            val (top, bottom) = t.page(page, p)
            """    <figure class="phone">
      <main class="screen" style="height:${(page.height + 36).roundToInt()}px">
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
<!-- Generated by Eyn · Design Trainer -->
<link href="https://fonts.googleapis.com/css2?$fonts&display=swap" rel="stylesheet">
<link href="https://fonts.googleapis.com/icon?family=${iconFamily[t.iconStyle.coerceIn(iconFamily.indices)]}" rel="stylesheet">
<style>
  * { box-sizing: border-box; margin: 0; }
  html { background: ${t.surface}; }
  body { font-family: '${t.font}', system-ui, sans-serif; color: ${t.ink}; padding: 40px 20px;
         background: linear-gradient(${t.surface}, #fff 40%, ${t.surface}); min-height: 100vh; }
  .board { max-width: 1680px; margin: 0 auto; display: flex; flex-direction: column; gap: 48px; }
  .phones { display: flex; flex-wrap: wrap; gap: 40px; justify-content: center; align-items: flex-start; }
  .phone { margin: 0; display: flex; flex-direction: column; align-items: center; gap: 14px; }
  .screen { width: 378px; background: ${t.bg}; border: 9px solid #15131F; border-radius: 52px; overflow: hidden;
            display: flex; flex-direction: column; padding-top: 18px; box-shadow: 0 20px 40px rgba(0,0,0,.12); }
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
        if (n.k == 't') {
            val fs = s.fs ?: 14f
            out += "fontSize: ${fs.fmt()}"; out += "lineHeight: ${(fs * 1.45f).fmt()}"; s.color?.let { out += "color: '$it'" }
            if (s.bold) out += "fontWeight: '700'"
            out += "textAlign: '${if (s.ta == "center") "center" else if (t.rtl) "right" else "left"}'"
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

    fun reactNative(p: AppProject, lang: Lang, image: (String) -> String? = { null }): String {
        val t = T(p, lang, image)
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
        return """// Generated by Eyn · Design Trainer
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
