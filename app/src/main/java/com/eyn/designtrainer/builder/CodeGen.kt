package com.eyn.designtrainer.builder

import com.eyn.designtrainer.data.AppProject
import com.eyn.designtrainer.data.Block
import com.eyn.designtrainer.data.BlockKind
import com.eyn.designtrainer.i18n.Lang

/** Turns a block project into starter code a developer can pick up. */
object CodeGen {
    private fun Long.hex() = "#%06X".format(this and 0xFFFFFF)
    private fun esc(s: String) = s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
    private fun js(s: String) = s.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n")

    private val sample = mapOf(
        Lang.AR to Sample(listOf("قهوة", "شاي", "حلى", "بارد"), listOf("كلاسيك", "مميّز"), listOf("الرئيسية", "بحث", "السلة", "حسابي"), listOf("البريد الإلكتروني", "كلمة المرور"), listOf("الاسم", "البريد الإلكتروني", "رسالتك"), listOf("طلباتي", "العناوين", "الدفع", "المساعدة")),
        Lang.EN to Sample(listOf("Coffee", "Tea", "Sweets", "Cold"), listOf("Classic", "Signature"), listOf("Home", "Search", "Cart", "Account"), listOf("Email", "Password"), listOf("Name", "Email", "Your message"), listOf("My orders", "Addresses", "Payment", "Help")),
    )

    private data class Sample(val cats: List<String>, val items: List<String>, val tabs: List<String>, val login: List<String>, val contact: List<String>, val rows: List<String>)

    private fun palette(p: AppProject) = if (p.applyIdentity) p.colors() else Palettes[1]

    private fun fontCss(p: AppProject): Pair<String, String> = when (if (p.applyIdentity) p.font else 2) {
        0 -> "Tajawal" to "Tajawal:wght@400;700"
        1 -> "Cairo" to "Cairo:wght@400;700"
        else -> "IBM Plex Sans Arabic" to "IBM+Plex+Sans+Arabic:wght@400;700"
    }

    // ------------------------------------------------------------------ HTML

    fun html(p: AppProject, lang: Lang): String {
        val pal = palette(p)
        val (font, gf) = fontCss(p)
        val s = sample.getValue(lang)
        val dir = if (lang == Lang.AR) "rtl" else "ltr"
        val screens = p.pages.joinToString("\n") { page ->
            """  <div class="wrap"><div class="page-name">${esc(page.name)}</div>
    <main class="screen">
${page.blocks.joinToString("\n") { "      " + htmlBlock(it, p, s) }}
    </main></div>"""
        }
        return """<!doctype html>
<html lang="${if (lang == Lang.AR) "ar" else "en"}" dir="$dir">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>${esc(p.name)}</title>
<link href="https://fonts.googleapis.com/css2?family=$gf&display=swap" rel="stylesheet">
<style>
  :root { --primary: ${pal.primary.hex()}; --surface: ${pal.surface.hex()}; --accent: ${pal.accent.hex()}; --bg: ${pal.bg.hex()}; --ink: ${pal.ink.hex()}; }
  * { box-sizing: border-box; }
  body { margin: 0; padding: 32px 16px; background: #ECEAF2; font-family: '$font', system-ui, sans-serif; color: var(--ink);
         display: flex; flex-wrap: wrap; gap: 32px; justify-content: center; }
  .page-name { font-weight: 700; margin: 0 8px 8px; color: #555; }
  .screen { width: 360px; min-height: 640px; background: var(--bg); border-radius: 32px; overflow: hidden; box-shadow: 0 12px 32px rgba(0,0,0,.12); padding: 8px 0; }
  .b { display: block; }
  .row { display: flex; align-items: center; gap: 12px; }
  .grow { flex: 1; }
  .muted { opacity: .6; }
  h1, h2, h3, p { margin: 0; }
  .icon { width: 36px; height: 36px; border-radius: 50%; background: var(--surface); display: grid; place-items: center; color: var(--primary); }
  .logo { display: flex; align-items: center; gap: 8px; font-weight: 700; color: var(--primary); font-size: 18px; }
  .logo i { width: 34px; height: 34px; border-radius: 10px; background: var(--primary); color: #fff; display: grid; place-items: center; font-style: normal; }
  .card { background: #fff; padding: 10px; }
  .ph { background: var(--surface); border-radius: 10px; aspect-ratio: 1.3; }
  .btn { display: inline-block; background: var(--primary); color: #fff; border: 0; padding: 12px 18px; font: inherit; font-weight: 700; text-align: center; }
  .btn.ghost { background: transparent; color: var(--primary); border: 1.5px solid var(--primary); }
  .field { width: 100%; height: 48px; border: 1px solid rgba(0,0,0,.1); background: #fff; padding: 0 14px; font: inherit; margin-bottom: 10px; }
  .chips { display: flex; gap: 8px; }
  .chip { padding: 7px 14px; border-radius: 999px; background: var(--surface); font-size: 13px; }
  .chip.on { background: var(--primary); color: #fff; }
  .tabs { display: flex; justify-content: space-around; background: #fff; padding: 10px 0; font-size: 11px; }
  .tabs .on { color: var(--primary); font-weight: 700; }
  .list > div { padding: 12px 14px; border-bottom: 1px solid rgba(0,0,0,.06); }
</style>
</head>
<body>
$screens
</body>
</html>
"""
    }

    private fun htmlBlock(b: Block, p: AppProject, s: Sample): String {
        val pal = palette(p)
        val r = "${b.radius.toInt()}px"
        val bg = b.bg?.let { "background:${it.hex()};" } ?: ""
        val align = if (b.align == 1) "text-align:center;" else ""
        val wrap = "class=\"b\" style=\"padding:${(b.padding * 0.6f).toInt()}px ${b.padding.toInt()}px;$bg$align\""
        val t = esc(b.title); val sub = esc(b.subtitle); val btn = esc(b.button)
        return when (b.kind) {
            BlockKind.HEADER -> when (b.variant) {
                1 -> "<header $wrap><div class=\"row\"><span class=\"icon\">←</span><h3 class=\"grow\" style=\"text-align:center\">$t</h3><span style=\"width:36px\"></span></div></header>"
                2 -> "<header $wrap><div class=\"row\"><span class=\"icon\">●</span><div class=\"grow\"><p class=\"muted\">$sub</p><h3>$t</h3></div><span class=\"icon\">🛍</span></div></header>"
                3, 5 -> "<header $wrap><div class=\"row\"><h2 class=\"grow\">$t</h2><span class=\"icon\">⌕</span></div></header>"
                4 -> "<header $wrap><div class=\"logo\" style=\"justify-content:center\"><i>${esc(p.name.take(1))}</i>${esc(p.name)}</div></header>"
                else -> "<header $wrap><div class=\"row\"><div class=\"logo grow\"><i>${esc(p.name.take(1))}</i>${esc(p.name)}</div><span class=\"icon\">🔔</span></div></header>"
            }
            BlockKind.HERO -> {
                val heroBg = b.bg?.hex() ?: if (b.variant == 2) pal.primary.hex() else pal.surface.hex()
                val fg = if (b.variant == 2 && b.bg == null) "#fff" else "inherit"
                "<section class=\"b\" style=\"padding:${(b.padding * 0.5f).toInt()}px ${b.padding.toInt()}px;$align\"><div style=\"background:$heroBg;color:$fg;border-radius:$r;padding:22px 18px\"><h2>$t</h2><p class=\"muted\" style=\"margin:6px 0 14px\">$sub</p>" +
                    (if (btn.isNotBlank()) "<a class=\"btn\" style=\"border-radius:10px\">$btn</a>" else "") + "</div></section>"
            }
            BlockKind.SEARCH -> "<div $wrap><div class=\"row\"><input class=\"field grow\" style=\"border-radius:$r;margin:0\" placeholder=\"$t\">" +
                (if (b.variant == 1) "<button class=\"btn\" style=\"border-radius:$r\">⚙</button>" else "") + "</div></div>"
            BlockKind.CATEGORIES -> "<section $wrap><h3 style=\"margin-bottom:10px\">$t</h3><div class=\"chips\">" +
                s.cats.mapIndexed { i, c -> "<span class=\"chip${if (i == 0) " on" else ""}\">${esc(c)}</span>" }.joinToString("") + "</div></section>"
            BlockKind.CARDS -> "<section $wrap><h3 style=\"margin-bottom:10px\">$t</h3><div class=\"row\" style=\"align-items:stretch\">" +
                s.items.mapIndexed { i, n -> "<div class=\"card grow\" style=\"border-radius:$r\"><div class=\"ph\"></div><b>${esc(n)}</b><p style=\"color:var(--accent);font-weight:700\">${18 + i * 2} $sub</p></div>" }.joinToString("") + "</div></section>"
            BlockKind.LIST -> "<section $wrap><h3 style=\"margin-bottom:10px\">$t</h3><div class=\"list card\" style=\"border-radius:$r;padding:0\">" +
                s.rows.joinToString("") { "<div>${esc(it)}</div>" } + "</div></section>"
            BlockKind.TEXT -> if (b.variant == 0) "<section $wrap><h3>$t</h3><p class=\"muted\" style=\"margin-top:6px\">$sub</p></section>"
            else "<section $wrap><div style=\"background:var(--surface);border-radius:$r;padding:14px\"><b style=\"color:var(--primary)\">$t</b><p>$sub</p></div></section>"
            BlockKind.FORM -> "<form $wrap><h2 style=\"margin-bottom:12px\">$t</h2>" +
                (if (b.variant == 0) s.login else s.contact).joinToString("") { "<input class=\"field\" style=\"border-radius:$r\" placeholder=\"${esc(it)}\">" } +
                "<button class=\"btn\" style=\"width:100%;border-radius:$r\">$btn</button></form>"
            BlockKind.BUTTON -> "<div $wrap><div class=\"row\">" + (if (b.variant == 1) "<a class=\"btn ghost grow\" style=\"border-radius:$r\">$sub</a>" else "") +
                "<a class=\"btn grow\" style=\"border-radius:$r\">$btn</a></div></div>"
            BlockKind.FOOTER -> if (b.variant == 0) "<nav class=\"tabs\" style=\"$bg\">" + s.tabs.mapIndexed { i, n -> "<span${if (i == 0) " class=\"on\"" else ""}>${esc(n)}</span>" }.joinToString("") + "</nav>"
            else "<footer $wrap style=\"background:${b.bg?.hex() ?: pal.primary.hex()};color:#fff;padding:${b.padding.toInt()}px\"><h3>$t</h3><p class=\"muted\" style=\"margin-top:8px;font-size:11px\">$sub</p></footer>"
        }
    }

    // ------------------------------------------------------------------ React Native

    fun reactNative(p: AppProject, lang: Lang): String {
        val pal = palette(p)
        val s = sample.getValue(lang)
        val rtl = lang == Lang.AR
        val screens = p.pages.mapIndexed { i, page ->
            val name = "Screen${i + 1}"
            """// ${page.name}
export function $name() {
  return (
    <ScrollView style={{ flex: 1, backgroundColor: C.bg }} contentContainerStyle={{ paddingVertical: 8 }}>
${page.blocks.joinToString("\n") { rnBlock(it, s).prependIndent("      ") }}
    </ScrollView>
  );
}"""
        }.joinToString("\n\n")
        return """// Generated by Eyn · Design Trainer
import React from 'react';
import { SafeAreaView, ScrollView, View, Text, TextInput, TouchableOpacity, StyleSheet } from 'react-native';

const C = {
  primary: '${pal.primary.hex()}',
  surface: '${pal.surface.hex()}',
  accent: '${pal.accent.hex()}',
  bg: '${pal.bg.hex()}',
  ink: '${pal.ink.hex()}',
};
const ALIGN = '${if (rtl) "right" else "left"}';

$screens

export default function App() {
  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: C.bg }}>
      <Screen1 />
    </SafeAreaView>
  );
}

const s = StyleSheet.create({
  row: { flexDirection: 'row', alignItems: 'center', gap: 12 },
  h1: { fontSize: 22, fontWeight: '700', color: C.ink, textAlign: ALIGN },
  h2: { fontSize: 18, fontWeight: '700', color: C.ink, textAlign: ALIGN },
  p: { fontSize: 14, color: C.ink, opacity: 0.6, textAlign: ALIGN },
  icon: { width: 36, height: 36, borderRadius: 18, backgroundColor: C.surface },
  btn: { backgroundColor: C.primary, paddingVertical: 14, alignItems: 'center' },
  btnText: { color: '#fff', fontWeight: '700', fontSize: 15 },
  field: { height: 48, borderWidth: 1, borderColor: 'rgba(0,0,0,0.1)', backgroundColor: '#fff', paddingHorizontal: 14, marginBottom: 10, textAlign: ALIGN },
  card: { flex: 1, backgroundColor: '#fff', padding: 10 },
  ph: { backgroundColor: C.surface, borderRadius: 10, aspectRatio: 1.3 },
  chip: { paddingHorizontal: 14, paddingVertical: 7, borderRadius: 999, backgroundColor: C.surface },
});
"""
    }

    private fun rnBlock(b: Block, s: Sample): String {
        val pad = "paddingHorizontal: ${b.padding.toInt()}, paddingVertical: ${(b.padding * 0.5f).toInt()}"
        val bg = b.bg?.let { ", backgroundColor: '${it.hex()}'" } ?: ""
        val r = b.radius.toInt()
        val center = if (b.align == 1) ", textAlign: 'center'" else ""
        val ta = if (b.align == 1) ", { textAlign: 'center' }" else ""
        val t = js(b.title); val sub = js(b.subtitle); val btn = js(b.button)
        val view = "<View style={{ $pad$bg }}>"
        return when (b.kind) {
            BlockKind.HEADER -> "$view<View style={s.row}><Text style={[s.h1, { flex: 1$center }]}>{'$t'}</Text><View style={s.icon} /></View></View>"
            BlockKind.HERO -> "$view<View style={{ backgroundColor: ${b.bg?.let { "'${it.hex()}'" } ?: if (b.variant == 2) "C.primary" else "C.surface"}, borderRadius: $r, padding: 18 }}>" +
                "<Text style={[s.h1$ta]}>{'$t'}</Text><Text style={[s.p, { marginVertical: 8 }]}>{'$sub'}</Text>" +
                "<TouchableOpacity style={[s.btn, { borderRadius: 10 }]}><Text style={s.btnText}>{'$btn'}</Text></TouchableOpacity></View></View>"
            BlockKind.SEARCH -> "$view<TextInput style={[s.field, { borderRadius: $r, marginBottom: 0 }]} placeholder={'$t'} /></View>"
            BlockKind.CATEGORIES -> "$view<Text style={[s.h2, { marginBottom: 10 }]}>{'$t'}</Text><View style={s.row}>" +
                s.cats.joinToString("") { "<Text style={s.chip}>{'${js(it)}'}</Text>" } + "</View></View>"
            BlockKind.CARDS -> "$view<Text style={[s.h2, { marginBottom: 10 }]}>{'$t'}</Text><View style={s.row}>" +
                s.items.mapIndexed { i, n -> "<View style={[s.card, { borderRadius: $r }]}><View style={s.ph} /><Text style={s.h2}>{'${js(n)}'}</Text><Text style={{ color: C.accent, fontWeight: '700' }}>{'${18 + i * 2} $sub'}</Text></View>" }.joinToString("") +
                "</View></View>"
            BlockKind.LIST -> "$view<Text style={[s.h2, { marginBottom: 10 }]}>{'$t'}</Text><View style={{ backgroundColor: '#fff', borderRadius: $r }}>" +
                s.rows.joinToString("") { "<Text style={[s.p, { opacity: 1, padding: 14 }]}>{'${js(it)}'}</Text>" } + "</View></View>"
            BlockKind.TEXT -> "$view<Text style={[s.h2$ta]}>{'$t'}</Text><Text style={s.p}>{'$sub'}</Text></View>"
            BlockKind.FORM -> "$view<Text style={[s.h1, { marginBottom: 12 }]}>{'$t'}</Text>" +
                (if (b.variant == 0) s.login else s.contact).joinToString("") { "<TextInput style={[s.field, { borderRadius: $r }]} placeholder={'${js(it)}'} />" } +
                "<TouchableOpacity style={[s.btn, { borderRadius: $r }]}><Text style={s.btnText}>{'$btn'}</Text></TouchableOpacity></View>"
            BlockKind.BUTTON -> "$view<TouchableOpacity style={[s.btn, { borderRadius: $r }]}><Text style={s.btnText}>{'$btn'}</Text></TouchableOpacity></View>"
            BlockKind.FOOTER -> if (b.variant == 0) "<View style={[s.row, { justifyContent: 'space-around', backgroundColor: '#fff', paddingVertical: 12 }]}>" +
                s.tabs.joinToString("") { "<Text style={{ fontSize: 11, color: C.ink }}>{'${js(it)}'}</Text>" } + "</View>"
            else "<View style={{ backgroundColor: ${b.bg?.let { "'${it.hex()}'" } ?: "C.primary"}, padding: ${b.padding.toInt()} }}><Text style={[s.h2, { color: '#fff' }]}>{'$t'}</Text><Text style={{ color: '#fff', opacity: 0.6, fontSize: 11 }}>{'$sub'}</Text></View>"
        }
    }
}
