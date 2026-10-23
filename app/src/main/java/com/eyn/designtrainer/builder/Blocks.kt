package com.eyn.designtrainer.builder

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eyn.designtrainer.data.AppProject
import com.eyn.designtrainer.data.Block
import com.eyn.designtrainer.data.BlockItem
import com.eyn.designtrainer.data.BlockKind
import com.eyn.designtrainer.data.Page
import com.eyn.designtrainer.data.c
import com.eyn.designtrainer.i18n.L
import com.eyn.designtrainer.i18n.Lang
import com.eyn.designtrainer.i18n.LocalLang
import com.eyn.designtrainer.ui.theme.Cairo
import com.eyn.designtrainer.ui.theme.fontAt
import com.eyn.designtrainer.ui.theme.Plex
import com.eyn.designtrainer.ui.theme.Tajawal
import kotlin.math.roundToInt

// ============================================================================ identity

data class AppPalette(val name: L, val primary: Long, val surface: Long, val accent: Long, val bg: Long, val ink: Long)

val Palettes = listOf(
    AppPalette(L("قهوة", "Coffee"), 0xFF5A3A22, 0xFFF3E9DD, 0xFFC98B3C, 0xFFFBF8F4, 0xFF2B1D12),
    AppPalette(L("عين", "Eyn"), 0xFF2A2470, 0xFFE8E5FA, 0xFFF2603F, 0xFFF7F6F2, 0xFF1D1A3F),
    AppPalette(L("نعناع", "Mint"), 0xFF0F766E, 0xFFDDF3EF, 0xFFF59E0B, 0xFFF6FBFA, 0xFF0B2E2B),
    AppPalette(L("وردي", "Rose"), 0xFF9D174D, 0xFFFCE7F0, 0xFF1E3A8A, 0xFFFFF8FA, 0xFF3B0A20),
)

val ProjectFonts = listOf("Tajawal" to Tajawal, "Cairo" to Cairo, "IBM Plex" to Plex)

val LogoIcons = listOf("coffee", "star", "heart", "bolt", "cart", "home", "eye", "school")

data class PTheme(
    val primary: Color, val surface: Color, val accent: Color, val bg: Color,
    val ink: Color, val muted: Color, val card: Color, val font: FontFamily,
) {
    val onPrimary: Color get() = if (primary.luminance() > 0.55f) ink else Color.White
}

/** The project's palette: a preset, or the colours the user built (palette = -1). */
fun AppProject.colors(): AppPalette =
    if (palette < 0 && customColors.size >= 5) AppPalette(L("مخصص", "Custom"), customColors[0], customColors[1], customColors[2], customColors[3], customColors[4])
    else Palettes[palette.coerceIn(Palettes.indices)]

fun AppProject.theme(): PTheme {
    val p = if (applyIdentity) colors() else Palettes[1]
    val font = if (applyIdentity) ProjectFonts[font.coerceIn(ProjectFonts.indices)].second else Plex
    return PTheme(p.primary.c(), p.surface.c(), p.accent.c(), p.bg.c(), p.ink.c(), p.ink.c().copy(alpha = 0.55f), Color.White, font)
}

@Composable
fun LogoMark(p: AppProject, size: Dp = 40.dp, showName: Boolean = true) {
    CompositionLocalProvider(LocalIconStyle provides p.iconStyle) { LogoMarkContent(p, size, showName) }
}

@Composable
private fun LogoMarkContent(p: AppProject, size: Dp, showName: Boolean) {
    val t = p.theme()
    val logoFamily = if (p.logoFont >= 0) fontAt(p.logoFont) else t.font
    val scale = p.logoScale
    val markShape = if (p.logoShape == 1) CircleShape else RoundedCornerShape(size * 0.3f)
    val onMark = if (p.logoShape == 2) t.primary else t.onPrimary
    val mark: @Composable () -> Unit = {
        if (p.logoStyle != 2) Box(
            Modifier.size(size).clip(markShape).background(if (p.logoShape == 2) Color.Transparent else t.primary),
            contentAlignment = Alignment.Center,
        ) {
            if (p.logoStyle == 0) FontIcon(p.logoIcon, onMark, size.value * (if (p.logoShape == 2) 0.9f else 0.58f))
            else Text(p.name.trim().take(1).ifEmpty { "?" }, color = onMark, fontFamily = logoFamily, fontWeight = FontWeight.Bold, fontSize = (size.value * 0.45f).sp)
        }
    }
    val stacked = p.logoLayout == 1
    val name: @Composable () -> Unit = {
        Column(horizontalAlignment = if (stacked) Alignment.CenterHorizontally else Alignment.Start) {
            Text(
                p.name, color = t.primary, fontFamily = logoFamily, fontWeight = FontWeight.Bold, maxLines = 1,
                fontSize = (size.value * (if (p.logoStyle == 2) 0.7f else 0.5f) * scale).sp, letterSpacing = p.logoSpacing.sp,
            )
            if (p.tagline.isNotBlank()) Text(p.tagline, color = t.muted, fontFamily = logoFamily, fontSize = (size.value * 0.2f * scale).sp, maxLines = 1)
        }
    }
    if (stacked) Column(horizontalAlignment = Alignment.CenterHorizontally) {
        mark()
        if (showName || p.logoStyle == 2) { if (p.logoStyle != 2) Spacer(Modifier.height(size * 0.2f)); name() }
    } else Row(verticalAlignment = Alignment.CenterVertically) {
        mark()
        if (showName || p.logoStyle == 2) { if (p.logoStyle != 2) Spacer(Modifier.width(size * 0.25f)); name() }
    }
}

// ============================================================================ catalog

fun BlockKind.label(): L = when (this) {
    BlockKind.HEADER -> L("الهيدر", "Header")
    BlockKind.HERO -> L("البطل", "Hero")
    BlockKind.SEARCH -> L("البحث", "Search")
    BlockKind.CATEGORIES -> L("الأقسام", "Categories")
    BlockKind.CARDS -> L("البطاقات", "Cards")
    BlockKind.LIST -> L("القوائم", "Lists")
    BlockKind.TEXT -> L("النصوص", "Text")
    BlockKind.FORM -> L("النماذج", "Forms")
    BlockKind.BUTTON -> L("الأزرار", "Buttons")
    BlockKind.FOOTER -> L("الفوتر", "Footer")
}

val Variants: Map<BlockKind, List<L>> = mapOf(
    BlockKind.HEADER to listOf(
        L("شعار + تنبيهات", "Logo + alerts"), L("عنوان + رجوع", "Title + back"), L("ترحيب + صورة", "Welcome + photo"),
        L("عنوان + بحث", "Title + search"), L("شعار في الوسط", "Centered logo"), L("عنوان + تبويبات", "Title + tabs"),
    ),
    BlockKind.HERO to listOf(L("بطاقة عرض", "Offer card"), L("صورة + نص", "Image + text"), L("خلفية ملونة", "Color banner")),
    BlockKind.SEARCH to listOf(L("شريط بحث", "Search bar"), L("بحث + فلتر", "Search + filter")),
    BlockKind.CATEGORIES to listOf(L("دوائر", "Circles"), L("شرائح", "Chips")),
    BlockKind.CARDS to listOf(L("شبكة منتجات", "Product grid"), L("قائمة أفقية", "Row list"), L("بطاقة مميزة", "Feature card")),
    BlockKind.LIST to listOf(L("قائمة روابط", "Link list"), L("قائمة إعدادات", "Settings list")),
    BlockKind.TEXT to listOf(L("عنوان وفقرة", "Title + paragraph"), L("ملاحظة", "Note box")),
    BlockKind.FORM to listOf(L("تسجيل دخول", "Sign in"), L("تواصل معنا", "Contact")),
    BlockKind.BUTTON to listOf(L("زر كامل العرض", "Full-width button"), L("زران", "Two buttons")),
    BlockKind.FOOTER to listOf(L("شريط تبويبات", "Tab bar"), L("فوتر نصي", "Text footer")),
)

/** A new block filled with sensible copy in the current language. */
fun defaultBlock(kind: BlockKind, variant: Int, p: AppProject, lang: Lang): Block {
    fun s(ar: String, en: String) = if (lang == Lang.AR) ar else en
    return when (kind) {
        BlockKind.HEADER -> Block(
            kind = kind, variant = variant,
            title = when (variant) {
                1 -> s("القائمة", "Menu"); 2 -> s("أهلاً، أحمد", "Hi, Ahmed"); 3, 5 -> s("اكتشف", "Discover"); else -> p.name
            },
            subtitle = if (variant == 2) s("صباح الخير", "Good morning") else "",
            padding = 16f,
        )
        BlockKind.HERO -> Block(
            kind = kind, variant = variant,
            title = s("عرض خاص بانتظارك", "A special offer awaits"),
            subtitle = s("اطلب الآن واستلم دون انتظار", "Order now and skip the wait"),
            button = s("اطلب الآن", "Order now"), align = if (variant == 2) 1 else 0, padding = 16f, radius = 20f,
        )
        BlockKind.SEARCH -> Block(kind = kind, variant = variant, title = s("ابحث…", "Search…"), padding = 16f, radius = 14f)
        BlockKind.CATEGORIES -> Block(kind = kind, variant = variant, title = s("الأقسام", "Categories"), padding = 16f)
        BlockKind.CARDS -> Block(kind = kind, variant = variant, title = s("الأكثر طلباً", "Most popular"), subtitle = s("ر.س", "$"), padding = 16f, radius = 16f)
        BlockKind.LIST -> Block(kind = kind, variant = variant, title = if (variant == 0) s("حسابي", "My account") else s("الإعدادات", "Settings"), padding = 16f, radius = 16f)
        BlockKind.TEXT -> Block(
            kind = kind, variant = variant, title = if (variant == 0) s("من نحن", "About us") else s("ملاحظة", "Note"),
            subtitle = s("نحضّر كل كوب بعناية من حبوب مختارة، لتصلك التجربة نفسها في كل مرة.", "We craft every cup with care from selected beans, so you get the same experience every time."),
            padding = 16f, radius = 16f,
        )
        BlockKind.FORM -> Block(
            kind = kind, variant = variant,
            title = if (variant == 0) s("تسجيل الدخول", "Sign in") else s("تواصل معنا", "Contact us"),
            button = if (variant == 0) s("دخول", "Sign in") else s("إرسال", "Send"), padding = 16f, radius = 14f,
        )
        BlockKind.BUTTON -> Block(
            kind = kind, variant = variant, button = s("اطلب الآن", "Order now"),
            subtitle = if (variant == 1) s("لاحقاً", "Later") else "", padding = 16f, radius = 14f,
        )
        BlockKind.FOOTER -> Block(
            kind = kind, variant = variant, title = p.name,
            subtitle = s("© 2026 جميع الحقوق محفوظة", "© 2026 All rights reserved"), padding = if (variant == 0) 8f else 16f,
            pinBottom = true,
        )
    }
}

fun starterPage(p: AppProject, lang: Lang, name: String) = Page(
    name = name,
    blocks = listOf(
        defaultBlock(BlockKind.HEADER, 0, p, lang),
        defaultBlock(BlockKind.HERO, 0, p, lang),
        defaultBlock(BlockKind.CATEGORIES, 0, p, lang),
        defaultBlock(BlockKind.CARDS, 0, p, lang),
        defaultBlock(BlockKind.FOOTER, 0, p, lang),
    ),
)

// ============================================================================ rendering

@Composable
private fun PText(
    text: String, t: PTheme, size: Int, color: Color = t.ink, bold: Boolean = false,
    align: TextAlign = TextAlign.Start, modifier: Modifier = Modifier, maxLines: Int = Int.MAX_VALUE,
) = Text(
    text, color = color, fontFamily = t.font, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
    fontSize = size.sp, lineHeight = (size * 1.45f).sp, textAlign = align, maxLines = maxLines,
    overflow = TextOverflow.Ellipsis, modifier = modifier,
)

@Composable
private fun Placeholder(t: PTheme, modifier: Modifier, radius: Dp = 12.dp, image: String = "") {
    Box(modifier.clip(RoundedCornerShape(radius)).background(t.surface), contentAlignment = Alignment.Center) {
        if (image.isNotEmpty()) PhotoImage(image, 0, Modifier.matchParentSize())
        else Icon(Icons.Rounded.Image, null, tint = t.primary.copy(alpha = 0.35f), modifier = Modifier.size(22.dp))
    }
}

private val sampleItems = listOf(L("كلاسيك", "Classic"), L("مميّز", "Signature"), L("بارد", "Iced"), L("موسمي", "Seasonal"))

/** Renders one block with the project's identity. Width comes from the caller. */
@Composable
fun BlockView(b: Block, p: AppProject, modifier: Modifier = Modifier) {
    val d = LocalDensity.current
    // Text size control: scale fonts inside this block only.
    CompositionLocalProvider(LocalDensity provides Density(d.density, d.fontScale * b.textScale), LocalIconStyle provides p.iconStyle) {
        Box(modifier.fillMaxWidth(), contentAlignment = if (b.align == 1) Alignment.TopCenter else Alignment.TopStart) {
            BlockContent(b, p, Modifier.fillMaxWidth(b.widthPct.coerceIn(0.2f, 1f)).heightIn(min = b.minHeight.dp))
        }
    }
}

/** Which kinds expose an editable list of items, and whether those items carry icons. */
fun Block.hasItems() = kind == BlockKind.FOOTER || kind == BlockKind.LIST || kind == BlockKind.CATEGORIES || (kind == BlockKind.HEADER && variant == 5)
fun Block.itemsHaveIcons() = (kind == BlockKind.FOOTER && variant == 0) || kind == BlockKind.LIST || kind == BlockKind.CATEGORIES
fun Block.hasActiveItem() = (kind == BlockKind.FOOTER && variant == 0) || kind == BlockKind.CATEGORIES || (kind == BlockKind.HEADER && variant == 5)

fun defaultItems(b: Block, lang: Lang): List<BlockItem> {
    fun i(ar: String, en: String, icon: String = "") = BlockItem(if (lang == Lang.AR) ar else en, icon)
    return when (b.kind) {
        BlockKind.FOOTER -> if (b.variant == 0) listOf(i("الرئيسية", "Home", "home"), i("بحث", "Search", "search"), i("السلة", "Cart", "shopping_bag"), i("حسابي", "Account", "person"))
        else listOf(i("من نحن", "About"), i("الشروط", "Terms"), i("تواصل", "Contact"))
        BlockKind.LIST -> if (b.variant == 0) listOf(i("طلباتي", "My orders", "receipt"), i("العناوين", "Addresses", "location_on"), i("الدفع", "Payment", "credit_card"), i("المساعدة", "Help", "help"))
        else listOf(i("الإشعارات", "Notifications", "notifications"), i("العروض", "Offers", "star"), i("الموقع", "Location", "location_on"))
        BlockKind.CATEGORIES -> listOf(i("قهوة", "Coffee", "local_cafe"), i("شاي", "Tea", "emoji_food_beverage"), i("حلى", "Sweets", "cake"), i("بارد", "Cold", "icecream"))
        BlockKind.HEADER -> listOf(i("الكل", "All"), i("جديد", "New"), i("عروض", "Deals"))
        else -> emptyList()
    }
}

fun Block.itemsOr(lang: Lang) = items.ifEmpty { defaultItems(this, lang) }

@Composable
private fun BlockContent(b: Block, p: AppProject, modifier: Modifier) {
    val t = p.theme()
    val lang = LocalLang.current
    val pad = b.padding.dp
    val r = b.radius.dp
    val bg = b.bg?.c()
    val center = b.align == 1
    val ta = if (center) TextAlign.Center else TextAlign.Start
    val hAlign = if (center) Alignment.CenterHorizontally else Alignment.Start
    val box = modifier.fillMaxWidth().then(if (bg != null && b.kind != BlockKind.HERO) Modifier.background(bg) else Modifier)

    when (b.kind) {
        BlockKind.HEADER -> Row(box.padding(horizontal = pad, vertical = pad * 0.75f), verticalAlignment = Alignment.CenterVertically) {
            when (b.variant) {
                0 -> {
                    if (p.applyIdentity) LogoMark(p, 34.dp) else PText(b.title, t, 18, bold = true)
                    Spacer(Modifier.weight(1f))
                    RoundIcon(Icons.Rounded.Notifications, t)
                }
                1 -> {
                    RoundIcon(Icons.AutoMirrored.Rounded.ArrowBack, t)
                    PText(b.title, t, 18, bold = true, align = TextAlign.Center, modifier = Modifier.weight(1f))
                    Spacer(Modifier.size(36.dp))
                }
                2 -> {
                    Box(Modifier.size(42.dp).clip(CircleShape).background(t.surface), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.Person, null, tint = t.primary, modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        PText(b.subtitle, t, 12, t.muted)
                        PText(b.title, t, 17, bold = true)
                    }
                    RoundIcon(Icons.Rounded.ShoppingBag, t)
                }
                3 -> {
                    PText(b.title, t, 22, bold = true, modifier = Modifier.weight(1f))
                    RoundIcon(Icons.Rounded.Search, t)
                }
                4 -> Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    if (p.applyIdentity) LogoMark(p, 34.dp) else PText(b.title, t, 18, bold = true)
                }
                else -> Column(Modifier.fillMaxWidth()) {
                    PText(b.title, t, 22, bold = true)
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                        b.itemsOr(lang).forEachIndexed { i, item ->
                            val on = i == b.active
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                PText(item.label, t, 14, if (on) t.primary else t.muted, bold = on)
                                Box(Modifier.padding(top = 4.dp).width(18.dp).height(3.dp).clip(CircleShape).background(if (on) t.accent else Color.Transparent))
                            }
                        }
                    }
                }
            }
        }

        BlockKind.HERO -> {
            val heroBg = bg ?: if (b.variant == 2) t.primary else t.surface
            val onHero = if (heroBg.luminance() > 0.5f) t.ink else Color.White
            Box(box.padding(horizontal = pad, vertical = pad * 0.5f)) {
                when (b.variant) {
                    1 -> Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(r)).background(heroBg).padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f), horizontalAlignment = hAlign) {
                            PText(b.title, t, 18, onHero, bold = true, align = ta)
                            Spacer(Modifier.height(4.dp))
                            PText(b.subtitle, t, 12, onHero.copy(alpha = 0.7f), align = ta)
                            Spacer(Modifier.height(12.dp))
                            HeroButton(b.button, t)
                        }
                        Spacer(Modifier.width(12.dp))
                        Placeholder(t.copy(surface = Color.White.copy(alpha = 0.6f)), Modifier.size(96.dp), r * 0.7f, b.image)
                    }
                    else -> Box(Modifier.fillMaxWidth().heightIn(min = b.itemHeight.dp).clip(RoundedCornerShape(r)).background(heroBg), contentAlignment = Alignment.Center) {
                        val onPhoto = if (b.image.isNotEmpty()) Color.White else onHero
                        if (b.image.isNotEmpty()) {
                            PhotoImage(b.image, 0, Modifier.matchParentSize())
                            Box(Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.38f)))
                        }
                        Column(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 22.dp), horizontalAlignment = hAlign) {
                            PText(b.title, t, 20, onPhoto, bold = true, align = ta, modifier = Modifier.fillMaxWidth())
                            Spacer(Modifier.height(6.dp))
                            PText(b.subtitle, t, 13, onPhoto.copy(alpha = 0.75f), align = ta, modifier = Modifier.fillMaxWidth())
                            if (b.button.isNotBlank()) {
                                Spacer(Modifier.height(14.dp))
                                HeroButton(b.button, t, inverted = b.variant == 2)
                            }
                        }
                    }
                }
            }
        }

        BlockKind.SEARCH -> Row(box.padding(horizontal = pad, vertical = pad * 0.5f), verticalAlignment = Alignment.CenterVertically) {
            Row(
                Modifier.weight(1f).height(if (b.itemHeight > 0) b.itemHeight.dp else 48.dp).clip(RoundedCornerShape(r)).background(Color.White)
                    .border(1.dp, t.ink.copy(alpha = 0.08f), RoundedCornerShape(r)).padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Rounded.Search, null, tint = t.muted, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                PText(b.title, t, 14, t.muted, maxLines = 1)
            }
            if (b.variant == 1) {
                Spacer(Modifier.width(10.dp))
                Box(Modifier.size(if (b.itemHeight > 0) b.itemHeight.dp else 48.dp).clip(RoundedCornerShape(r)).background(t.primary), contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Tune, null, tint = t.onPrimary, modifier = Modifier.size(20.dp))
                }
            }
        }

        BlockKind.CATEGORIES -> Column(box.padding(horizontal = pad, vertical = pad * 0.5f)) {
            if (b.title.isNotBlank()) {
                PText(b.title, t, 16, bold = true, align = ta, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
            }
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = if (b.variant == 0) Arrangement.spacedBy(14.dp) else Arrangement.spacedBy(8.dp),
            ) {
                b.itemsOr(lang).forEachIndexed { i, item ->
                    val on = i == b.active
                    if (b.variant == 0) Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val s = if (b.itemHeight > 0) b.itemHeight.dp else 54.dp
                        Box(Modifier.size(s).clip(CircleShape).background(if (on) t.primary else t.surface), contentAlignment = Alignment.Center) {
                            if (item.icon.isNotEmpty()) FontIcon(item.icon, if (on) t.onPrimary else t.primary, s.value * 0.45f)
                        }
                        Spacer(Modifier.height(6.dp))
                        PText(item.label, t, 12, t.ink, bold = on)
                    } else Row(
                        Modifier.clip(CircleShape).background(if (on) t.primary else t.surface).padding(horizontal = 14.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (item.icon.isNotEmpty()) { FontIcon(item.icon, if (on) t.onPrimary else t.primary, 16f); Spacer(Modifier.width(6.dp)) }
                        PText(item.label, t, 13, if (on) t.onPrimary else t.ink, bold = on)
                    }
                }
            }
        }

        BlockKind.CARDS -> Column(box.padding(horizontal = pad, vertical = pad * 0.5f)) {
            if (b.title.isNotBlank()) {
                PText(b.title, t, 16, bold = true, align = ta, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
            }
            when (b.variant) {
                0 -> Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf(0, 1).forEach { i ->
                        Column(Modifier.weight(1f).clip(RoundedCornerShape(r)).background(t.card).padding(10.dp)) {
                            Placeholder(t, if (b.itemHeight > 0) Modifier.fillMaxWidth().height(b.itemHeight.dp) else Modifier.fillMaxWidth().aspectRatio(1.3f), r * 0.7f, b.image)
                            Spacer(Modifier.height(8.dp))
                            PText(sampleItems[i].get(lang), t, 14, bold = true, maxLines = 1)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                PText("${18 + i * 2} ${b.subtitle}", t, 13, t.accent, bold = true, modifier = Modifier.weight(1f))
                                Box(Modifier.size(26.dp).clip(CircleShape).background(t.primary), contentAlignment = Alignment.Center) {
                                    PText("+", t, 15, t.onPrimary, bold = true)
                                }
                            }
                        }
                    }
                }
                1 -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(0, 1).forEach { i ->
                        Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(r)).background(t.card).padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Placeholder(t, Modifier.size(56.dp), r * 0.6f, b.image)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                PText(sampleItems[i + 2].get(lang), t, 14, bold = true)
                                PText("${22 + i * 3} ${b.subtitle}", t, 13, t.accent)
                            }
                            Icon(Icons.Rounded.Favorite, null, tint = t.accent, modifier = Modifier.size(18.dp))
                        }
                    }
                }
                else -> Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(r)).background(t.card)) {
                    Placeholder(t, Modifier.fillMaxWidth().height(if (b.itemHeight > 0) b.itemHeight.dp else 130.dp), 0.dp, b.image)
                    Column(Modifier.padding(14.dp), horizontalAlignment = hAlign) {
                        PText(sampleItems[1].get(lang), t, 16, bold = true, align = ta)
                        PText("4.9 ★ · 24 ${b.subtitle}", t, 13, t.muted, align = ta)
                    }
                }
            }
        }

        BlockKind.LIST -> Column(box.padding(horizontal = pad, vertical = pad * 0.5f)) {
            if (b.title.isNotBlank()) {
                PText(b.title, t, 16, bold = true, align = ta, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
            }
            val rows = b.itemsOr(lang)
            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(r)).background(t.card)) {
                rows.forEachIndexed { i, item ->
                    Row(
                        Modifier.fillMaxWidth().heightIn(min = b.itemHeight.dp).padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (item.icon.isNotEmpty()) {
                            Box(Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(t.surface), contentAlignment = Alignment.Center) {
                                FontIcon(item.icon, t.primary, 18f)
                            }
                            Spacer(Modifier.width(12.dp))
                        }
                        PText(item.label, t, 14, modifier = Modifier.weight(1f))
                        if (b.variant == 0) Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = t.muted, modifier = Modifier.size(20.dp))
                        else Box(Modifier.width(38.dp).height(22.dp).clip(CircleShape).background(if (i != 1) t.primary else t.surface), contentAlignment = if (i != 1) Alignment.CenterEnd else Alignment.CenterStart) {
                            Box(Modifier.padding(3.dp).size(16.dp).clip(CircleShape).background(Color.White))
                        }
                    }
                    if (i < rows.lastIndex) Box(Modifier.fillMaxWidth().padding(horizontal = 14.dp).height(1.dp).background(t.ink.copy(alpha = 0.06f)))
                }
            }
        }

        BlockKind.TEXT -> Column(box.padding(horizontal = pad, vertical = pad * 0.5f), horizontalAlignment = hAlign) {
            if (b.variant == 0) {
                PText(b.title, t, 18, bold = true, align = ta, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(6.dp))
                PText(b.subtitle, t, 14, t.muted, align = ta, modifier = Modifier.fillMaxWidth())
            } else Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(r)).background(t.surface).padding(14.dp), horizontalAlignment = hAlign) {
                PText(b.title, t, 14, t.primary, bold = true, align = ta, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(4.dp))
                PText(b.subtitle, t, 13, t.ink.copy(alpha = 0.75f), align = ta, modifier = Modifier.fillMaxWidth())
            }
        }

        BlockKind.FORM -> Column(box.padding(horizontal = pad, vertical = pad * 0.5f), horizontalAlignment = hAlign) {
            PText(b.title, t, 20, bold = true, align = ta, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            val fields = if (b.variant == 0) listOf(L("البريد الإلكتروني", "Email"), L("كلمة المرور", "Password"))
            else listOf(L("الاسم", "Name"), L("البريد الإلكتروني", "Email"), L("رسالتك", "Your message"))
            fields.forEachIndexed { i, f ->
                Box(
                    Modifier.fillMaxWidth().padding(bottom = 10.dp).height(if (b.variant == 1 && i == 2) 84.dp else if (b.itemHeight > 0) b.itemHeight.dp else 48.dp)
                        .clip(RoundedCornerShape(r)).background(Color.White).border(1.dp, t.ink.copy(alpha = 0.1f), RoundedCornerShape(r))
                        .padding(horizontal = 14.dp, vertical = 13.dp),
                ) { PText(f.get(lang), t, 13, t.muted) }
            }
            Spacer(Modifier.height(4.dp))
            Box(Modifier.fillMaxWidth().height(if (b.itemHeight > 0) b.itemHeight.dp else 50.dp).clip(RoundedCornerShape(r)).background(t.primary), contentAlignment = Alignment.Center) {
                PText(b.button, t, 15, t.onPrimary, bold = true)
            }
        }

        BlockKind.BUTTON -> Row(box.padding(horizontal = pad, vertical = pad * 0.5f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            val bh = if (b.itemHeight > 0) b.itemHeight.dp else 50.dp
            if (b.variant == 1) Box(
                Modifier.weight(1f).height(bh).clip(RoundedCornerShape(r)).border(1.5.dp, t.primary.copy(alpha = 0.4f), RoundedCornerShape(r)),
                contentAlignment = Alignment.Center,
            ) { PText(b.subtitle, t, 15, t.primary, bold = true) }
            Box(Modifier.weight(1f).height(bh).clip(RoundedCornerShape(r)).background(t.primary), contentAlignment = Alignment.Center) {
                PText(b.button, t, 15, t.onPrimary, bold = true)
            }
        }

        BlockKind.FOOTER -> if (b.variant == 0) Row(
            box.background(bg ?: Color.White).padding(vertical = pad + 2.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            b.itemsOr(lang).forEachIndexed { i, item ->
                val on = i == b.active
                // Every tab gets the same width so the bar stays balanced whatever the labels are.
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier.clip(CircleShape).background(if (on) t.surface else Color.Transparent).padding(horizontal = 12.dp, vertical = 3.dp),
                        contentAlignment = Alignment.Center,
                    ) { FontIcon(item.icon.ifEmpty { "circle" }, if (on) t.primary else t.muted, 22f) }
                    PText(item.label, t, 11, if (on) t.primary else t.muted, bold = on, maxLines = 1)
                }
            }
        } else Column(box.background(bg ?: t.primary).padding(pad), horizontalAlignment = hAlign) {
            val on = if ((bg ?: t.primary).luminance() > 0.5f) t.ink else Color.White
            PText(b.title, t, 18, on, bold = true, align = ta, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                b.itemsOr(lang).forEach { PText(it.label, t, 13, on.copy(alpha = 0.8f)) }
            }
            Spacer(Modifier.height(10.dp))
            PText(b.subtitle, t, 11, on.copy(alpha = 0.6f), align = ta, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun RoundIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, t: PTheme) {
    Box(Modifier.size(36.dp).clip(CircleShape).background(t.surface), contentAlignment = Alignment.Center) {
        Icon(icon, null, tint = t.primary, modifier = Modifier.size(19.dp))
    }
}

@Composable
private fun HeroButton(text: String, t: PTheme, inverted: Boolean = false) {
    if (text.isBlank()) return
    PText(
        text, t, 13, if (inverted) t.primary else t.onPrimary, bold = true,
        modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(if (inverted) Color.White else t.primary).padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

/** A full page: blocks stacked on the project background; pinned blocks sit at the bottom. */
@Composable
fun PageView(page: Page, p: AppProject, modifier: Modifier = Modifier, minHeight: Dp = page.height.dp) {
    val t = p.theme()
    PinnedColumn(
        minHeight, modifier.fillMaxWidth().background(t.bg),
        top = { page.blocks.filter { !it.pinBottom }.forEach { BlockView(it, p) } },
        bottom = { page.blocks.filter { it.pinBottom }.forEach { BlockView(it, p) } },
    )
}

/** Column whose [bottom] group is pushed to the bottom edge, with at least [minHeight] of height. */
@Composable
fun PinnedColumn(minHeight: Dp, modifier: Modifier, top: @Composable ColumnScope.() -> Unit, bottom: @Composable ColumnScope.() -> Unit) {
    Layout(contents = listOf({ Column(content = top) }, { Column(content = bottom) }), modifier = modifier) { (tm, bm), c ->
        val w = if (c.hasBoundedWidth) c.maxWidth else c.minWidth
        val child = Constraints(minWidth = w, maxWidth = w)
        val tp = tm.first().measure(child)
        val bp = bm.first().measure(child)
        val h = maxOf(minHeight.roundToPx(), tp.height + bp.height, c.minHeight)
        layout(w, h) {
            tp.place(0, 0)
            bp.place(0, h - bp.height)
        }
    }
}

/**
 * Lays out [content] at a fixed [designWidth] then scales it down to fit, so previews
 * look exactly like the real thing. Font scale is pinned to 1 for consistent previews.
 */
@Composable
fun Scaled(scale: Float, designWidth: Dp = 360.dp, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val density = LocalDensity.current
    CompositionLocalProvider(LocalDensity provides Density(density.density, 1f)) {
        Column(
            modifier.layout { measurable, _ ->
                val w = designWidth.roundToPx()
                val placeable = measurable.measure(Constraints(minWidth = w, maxWidth = w))
                layout((placeable.width * scale).roundToInt(), (placeable.height * scale).roundToInt()) {
                    placeable.placeWithLayer(0, 0) {
                        scaleX = scale; scaleY = scale; transformOrigin = TransformOrigin(0f, 0f)
                    }
                }
            },
            content = content,
        )
    }
}
