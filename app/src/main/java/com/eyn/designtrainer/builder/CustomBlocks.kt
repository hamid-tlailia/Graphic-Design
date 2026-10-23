package com.eyn.designtrainer.builder

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eyn.designtrainer.data.Block
import com.eyn.designtrainer.data.BlockItem
import com.eyn.designtrainer.data.BlockKind
import com.eyn.designtrainer.i18n.L
import com.eyn.designtrainer.i18n.Lang
import kotlin.math.roundToInt

/** The ready-made "Custom" blocks beyond the first three, by variant index. */
object CV {
    const val ICON_ROW = 0; const val ITEM_LIST = 1; const val CARD_GRID = 2
    const val CART = 3; const val SUMMARY = 4; const val PAY_METHODS = 5; const val PAY_LOGOS = 6
    const val SECTION = 7; const val PROMO = 8; const val STEPS = 9; const val TOGGLES = 10
    const val REVIEWS = 11; const val DIVIDER = 12; const val BANNER = 13; const val PRODUCT = 14
    const val STATS = 15; const val PROFILE = 16; const val ALERT = 17; const val ICON_GRID = 18
    const val ADD_BAR = 19; const val CHECKS = 20

    val names: List<L> = listOf(
        L("صف أيقونات ونصوص", "Icon row"), L("قائمة عناصر وأسعار", "Item list"), L("شبكة بطاقات", "Card grid"),
        L("سلة المشتريات", "Shopping cart"), L("ملخص الطلب", "Order summary"), L("طرق الدفع", "Payment methods"),
        L("شعارات الدفع", "Payment logos"), L("عنوان قسم", "Section title"), L("كود خصم", "Promo code"),
        L("خطوات الطلب", "Checkout steps"), L("قائمة مفاتيح", "Toggle list"), L("آراء العملاء", "Reviews"),
        L("فاصل", "Divider"), L("صورة / بانر", "Image banner"), L("تفاصيل منتج", "Product details"),
        L("إحصائيات", "Stats"), L("ملف شخصي", "Profile card"), L("تنبيه", "Alert"), L("شبكة أيقونات", "Icon grid"),
        L("شريط إضافة للسلة", "Add-to-cart bar"), L("خيارات إضافية", "Extras checklist"),
    )

    private val noItems = setOf(SECTION, PROMO, DIVIDER, BANNER, PRODUCT, PROFILE, ALERT, ADD_BAR)
    fun hasItems(v: Int) = v !in noItems
    fun icons(v: Int) = v in setOf(ICON_ROW, ITEM_LIST, CARD_GRID, TOGGLES, ICON_GRID)
    fun payments(v: Int) = v == PAY_METHODS || v == PAY_LOGOS
    fun labels(v: Int) = v != PAY_LOGOS
    fun value(v: Int) = v in setOf(ICON_ROW, ITEM_LIST, CARD_GRID, CART, SUMMARY, REVIEWS, STATS, CHECKS)
    fun image(v: Int) = v in setOf(CARD_GRID, CART, REVIEWS)
    fun active(v: Int) = v == PAY_METHODS || v == STEPS
    fun note(v: Int) = v == REVIEWS
    fun qty(v: Int) = v == CART
    fun on(v: Int) = v == TOGGLES || v == CHECKS
    fun blockImage(v: Int) = v in setOf(BANNER, PRODUCT, PROFILE)
    fun blockIcon(v: Int) = v == ALERT
    /** Long text (body) for these. */
    fun body(v: Int) = v == PRODUCT || v == ALERT
    fun price(v: Int) = v == PRODUCT || v == ADD_BAR

    /** Optional parts the user can switch off, keyed as stored in [Block.hidden]. */
    fun options(v: Int): List<Pair<String, L>> = when (v) {
        CART -> listOf(OPT_IMAGE to L("صورة المنتج", "Product photo"), OPT_STEPPER to L("أزرار + و −", "+ / − buttons"), OPT_DELETE to L("زر الحذف", "Delete button"))
        SUMMARY -> listOf(OPT_TOTAL to L("سطر الإجمالي", "Total row"))
        REVIEWS -> listOf(OPT_IMAGE to L("الصورة الشخصية", "Avatar"), OPT_RATING to L("النجوم", "Stars"))
        PRODUCT -> listOf(OPT_RATING to L("التقييم", "Rating"), OPT_STEPPER to L("أزرار + و −", "+ / − buttons"), OPT_FAV to L("زر المفضلة", "Favorite button"))
        ALERT -> listOf(OPT_CLOSE to L("زر الإغلاق", "Close button"))
        ADD_BAR -> listOf(OPT_STEPPER to L("أزرار + و −", "+ / − buttons"))
        STEPS -> listOf(OPT_LABELS to L("أسماء الخطوات", "Step names"))
        else -> emptyList()
    }

    const val OPT_IMAGE = "image"; const val OPT_STEPPER = "stepper"; const val OPT_DELETE = "delete"; const val OPT_TOTAL = "total"
    const val OPT_RATING = "rating"; const val OPT_FAV = "fav"; const val OPT_CLOSE = "close"; const val OPT_LABELS = "labels"

    fun valueHint(v: Int): L = when (v) {
        REVIEWS -> L("التقييم من 5", "Rating out of 5"); STATS -> L("الرقم", "Number"); SUMMARY -> L("المبلغ", "Amount")
        else -> L("السعر", "Price")
    }

    /** Title / subtitle / button texts and items for a fresh block. */
    fun fresh(v: Int, lang: Lang): Block {
        fun s(ar: String, en: String) = if (lang == Lang.AR) ar else en
        val cur = s("ر.س", "$")
        val b = Block(kind = BlockKind.CUSTOM, variant = v, subtitle = cur, padding = 16f, radius = 16f)
        return when (v) {
            ITEM_LIST -> b.copy(title = s("قائمة الأسعار", "Price list"))
            ICON_ROW, CARD_GRID -> b.copy(title = s("لماذا نحن؟", "Why us?"))
            CART -> b.copy(title = s("سلة المشتريات", "My cart"))
            SUMMARY -> b.copy(title = s("ملخص الطلب", "Order summary"), button = s("إتمام الطلب", "Checkout"))
            PAY_METHODS -> b.copy(title = s("طريقة الدفع", "Payment method"))
            PAY_LOGOS -> b.copy(title = s("نقبل الدفع عبر", "We accept"), align = 1)
            SECTION -> b.copy(title = s("الأكثر مبيعاً", "Best sellers"), subtitle = "", button = s("عرض الكل", "See all"))
            PROMO -> b.copy(title = s("كود الخصم", "Promo code"), subtitle = s("أدخل الكود هنا", "Enter your code"), button = s("تطبيق", "Apply"), radius = 14f)
            STEPS -> b.copy(title = "", active = 1)
            TOGGLES -> b.copy(title = s("الإعدادات", "Settings"))
            REVIEWS -> b.copy(title = s("آراء العملاء", "Reviews"))
            DIVIDER -> b.copy(title = s("أو", "or"), subtitle = "")
            BANNER -> b.copy(title = s("عروض نهاية الأسبوع", "Weekend deals"), subtitle = "", radius = 20f)
            PRODUCT -> b.copy(
                title = s("لاتيه بالكراميل", "Caramel latte"), price = "18",
                body = s("إسبريسو مزدوج مع حليب مخفوق وصوص الكراميل.", "Double espresso with steamed milk and caramel sauce."),
                button = s("أضف إلى السلة", "Add to cart"), radius = 20f,
            )
            STATS -> b.copy(title = "")
            PROFILE -> b.copy(title = s("سارة أحمد", "Sara Ahmed"), subtitle = "sara@example.com", button = s("تعديل الملف", "Edit profile"))
            ALERT -> b.copy(
                title = s("تمت إضافة المنتج إلى السلة", "Added to your cart"), subtitle = "",
                body = s("تابع التسوق أو أتمم طلبك الآن.", "Keep shopping or check out now."), icon = "check_circle", radius = 14f,
            )
            ICON_GRID -> b.copy(title = s("خدماتنا", "Our services"))
            ADD_BAR -> b.copy(title = "", price = "18", button = s("أضف إلى السلة", "Add to cart"), radius = 14f, pinBottom = true)
            CHECKS -> b.copy(title = s("إضافات", "Extras"))
            else -> b
        }
    }

    fun items(v: Int, lang: Lang): List<BlockItem> {
        fun s(ar: String, en: String) = if (lang == Lang.AR) ar else en
        fun i(ar: String, en: String, icon: String = "", value: String = "") = BlockItem(s(ar, en), icon, value)
        return when (v) {
            ITEM_LIST -> listOf(i("قهوة عربية", "Arabic coffee", "local_cafe", "12"), i("لاتيه", "Latte", "coffee", "16"), i("كرواسون", "Croissant", "bakery_dining", "9"))
            CARD_GRID -> listOf(i("توصيل سريع", "Fast delivery", "local_shipping"), i("دفع آمن", "Secure pay", "lock"), i("دعم 24/7", "24/7 support", "support_agent"), i("نقاط ولاء", "Rewards", "redeem"))
            ICON_ROW -> listOf(i("توصيل سريع", "Fast delivery", "local_shipping"), i("دفع آمن", "Secure pay", "lock"), i("دعم 24/7", "24/7 support", "support_agent"))
            CART -> listOf(i("لاتيه", "Latte", value = "16"), i("كرواسون", "Croissant", value = "9").copy(qty = 2))
            SUMMARY -> listOf(i("المجموع الفرعي", "Subtotal", value = "34"), i("التوصيل", "Delivery", value = "5"), i("الخصم", "Discount", value = "-4"))
            PAY_METHODS -> listOf(
                BlockItem(s("فيزا •••• 4242", "Visa •••• 4242"), "visa"), BlockItem(s("ماستركارد •••• 8810", "Mastercard •••• 8810"), "mastercard"),
                BlockItem("Apple Pay", "applepay"), BlockItem(s("الدفع عند الاستلام", "Cash on delivery"), "cash"),
            )
            PAY_LOGOS -> listOf("visa", "mastercard", "mada", "applepay", "paypal").map { BlockItem("", it) }
            STEPS -> listOf(i("السلة", "Cart"), i("العنوان", "Address"), i("الدفع", "Payment"), i("تأكيد", "Confirm"))
            TOGGLES -> listOf(i("الإشعارات", "Notifications", "notifications").copy(on = true), i("الوضع الداكن", "Dark mode", "dark_mode"), i("مشاركة الموقع", "Share location", "location_on").copy(on = true))
            REVIEWS -> listOf(
                i("أحمد", "Ahmed", value = "5").copy(note = s("قهوة رائعة وخدمة سريعة.", "Great coffee and fast service.")),
                i("نورة", "Noura", value = "4").copy(note = s("المكان هادئ والأسعار مناسبة.", "Calm place, fair prices.")),
            )
            STATS -> listOf(i("طلب يومياً", "orders a day", value = "120+"), i("تقييم", "rating", value = "4.9"), i("عميل", "customers", value = "2k"))
            ICON_GRID -> listOf(
                i("توصيل", "Delivery", "local_shipping"), i("استلام", "Pickup", "storefront"), i("هدايا", "Gifts", "card_giftcard"),
                i("حجز", "Booking", "event"), i("دعم", "Support", "support_agent"), i("عروض", "Offers", "local_offer"),
            )
            CHECKS -> listOf(i("حليب الشوفان", "Oat milk", value = "3"), i("شوت إضافي", "Extra shot", value = "4").copy(on = true), i("كريمة", "Whipped cream", value = "2"))
            else -> emptyList()
        }
    }

    /** Numbers in [v] ("-4", "12.5 ر.س") summed for the total row. */
    fun total(items: List<BlockItem>): String {
        val sum = items.sumOf { it.value.filter { ch -> ch.isDigit() || ch == '.' || ch == '-' }.toDoubleOrNull() ?: 0.0 }
        return if (sum == sum.roundToInt().toDouble()) sum.roundToInt().toString() else "%.2f".format(sum)
    }
}

fun Block.shows(option: String) = option !in hidden

// ============================================================================ rendering

@Composable
private fun Stepper(qty: Int, t: PTheme, size: Dp = 28.dp) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(size).clip(CircleShape).border(1.dp, t.ink.copy(alpha = 0.15f), CircleShape), contentAlignment = Alignment.Center) {
            FontIcon("remove", t.ink, size.value * 0.55f)
        }
        PText(qty.toString(), t, 14, bold = true, align = TextAlign.Center, modifier = Modifier.width(size + 4.dp))
        Box(Modifier.size(size).clip(CircleShape).background(t.primary), contentAlignment = Alignment.Center) {
            FontIcon("add", t.onPrimary, size.value * 0.55f)
        }
    }
}

@Composable
private fun Toggle(on: Boolean, t: PTheme) {
    Box(
        Modifier.size(44.dp, 26.dp).clip(CircleShape).background(if (on) t.primary else t.ink.copy(alpha = 0.15f)).padding(3.dp),
        contentAlignment = if (on) Alignment.CenterEnd else Alignment.CenterStart,
    ) { Box(Modifier.size(20.dp).clip(CircleShape).background(Color.White)) }
}

@Composable
private fun Stars(rating: Float, t: PTheme, size: Float = 14f) {
    Row {
        repeat(5) { i -> FontIcon(if (i < rating.roundToInt()) "star" else "star_border", Color(0xFFFFB400), size) }
    }
}

/** Variants from [CV.CART] on. Called inside the block's padded column. */
@Composable
internal fun ColumnScope.CustomExtra(b: Block, t: PTheme, items: List<BlockItem>, lang: Lang, price: (String) -> String) {
    val r = b.radius.dp
    val center = b.align == 1
    val ta = if (center) TextAlign.Center else TextAlign.Start
    val ih = b.itemHeight
    val divider = @Composable { Box(Modifier.fillMaxWidth().height(1.dp).background(t.ink.copy(alpha = 0.06f))) }
    when (b.variant) {
        CV.CART -> Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items.forEach { item ->
                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(r)).background(t.card).padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (b.shows(CV.OPT_IMAGE)) {
                        val s = if (ih > 0) ih.dp else 64.dp
                        Placeholder(t, Modifier.size(s), r * 0.6f, item.image)
                        Spacer(Modifier.width(12.dp))
                    }
                    Column(Modifier.weight(1f)) {
                        PText(item.label, t, 14, bold = true, maxLines = 1)
                        Spacer(Modifier.height(4.dp))
                        PText(price(item.value), t, 13, t.accent, bold = true)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        if (b.shows(CV.OPT_DELETE)) {
                            FontIcon("delete", t.muted, 20f)
                            if (b.shows(CV.OPT_STEPPER)) Spacer(Modifier.height(8.dp))
                        }
                        if (b.shows(CV.OPT_STEPPER)) Stepper(item.qty, t)
                        else PText("×${item.qty}", t, 13, t.muted)
                    }
                }
            }
        }
        CV.SUMMARY -> Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(r)).background(t.card).padding(14.dp)) {
            items.forEach { item ->
                Row(Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
                    PText(item.label, t, 14, t.muted, modifier = Modifier.weight(1f))
                    val negative = item.value.trim().startsWith("-")
                    // LRM keeps the minus sign in front of the number in right-to-left text.
                    PText((if (negative) "\u200E" else "") + price(item.value), t, 14, if (negative) Color(0xFF2E9E6A) else t.ink)
                }
            }
            if (b.shows(CV.OPT_TOTAL)) {
                Spacer(Modifier.height(6.dp)); divider(); Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth()) {
                    PText(if (lang == Lang.AR) "الإجمالي" else "Total", t, 16, bold = true, modifier = Modifier.weight(1f))
                    PText(price(CV.total(items)), t, 16, t.primary, bold = true)
                }
            }
            if (b.button.isNotBlank()) {
                Spacer(Modifier.height(14.dp))
                Box(Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(r * 0.8f)).background(t.primary), contentAlignment = Alignment.Center) {
                    PText(b.button, t, 15, t.onPrimary, bold = true)
                }
            }
        }
        CV.PAY_METHODS -> Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items.forEachIndexed { i, item ->
                val on = i == b.active
                Row(
                    Modifier.fillMaxWidth().heightIn(min = if (ih > 0) ih.dp else 56.dp).clip(RoundedCornerShape(r)).background(t.card)
                        .border(if (on) 2.dp else 1.dp, if (on) t.primary else t.ink.copy(alpha = 0.08f), RoundedCornerShape(r)).padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Shapes.PaymentMark(item.icon, Modifier.size(52.dp, 32.dp))
                    Spacer(Modifier.width(12.dp))
                    PText(item.label, t, 14, modifier = Modifier.weight(1f), maxLines = 1)
                    Box(Modifier.size(22.dp).clip(CircleShape).border(2.dp, if (on) t.primary else t.ink.copy(alpha = 0.25f), CircleShape), contentAlignment = Alignment.Center) {
                        if (on) Box(Modifier.size(11.dp).clip(CircleShape).background(t.primary))
                    }
                }
            }
        }
        CV.PAY_LOGOS -> Row(
            Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, if (center) Alignment.CenterHorizontally else Alignment.Start),
        ) {
            val h = if (ih > 0) ih.dp else 30.dp
            items.forEach { Shapes.PaymentMark(it.icon, Modifier.size(h * 1.6f, h)) }
        }
        CV.SECTION -> {} // the title row is drawn by the caller
        CV.PROMO -> Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val h = if (ih > 0) ih.dp else 48.dp
            Row(
                Modifier.weight(1f).height(h).clip(RoundedCornerShape(r)).background(Color.White).border(1.dp, t.ink.copy(alpha = 0.12f), RoundedCornerShape(r)).padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FontIcon("local_offer", t.muted, 18f); Spacer(Modifier.width(8.dp))
                PText(b.subtitle, t, 13, t.muted, maxLines = 1)
            }
            Box(Modifier.height(h).clip(RoundedCornerShape(r)).background(t.primary).padding(horizontal = 18.dp), contentAlignment = Alignment.Center) {
                PText(b.button, t, 14, t.onPrimary, bold = true)
            }
        }
        CV.STEPS -> Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            items.forEachIndexed { i, item ->
                val done = i < b.active; val now = i == b.active
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.weight(1f).height(2.dp).background(if (i == 0) Color.Transparent else if (i <= b.active) t.primary else t.ink.copy(alpha = 0.12f)))
                        Box(
                            Modifier.size(30.dp).clip(CircleShape).background(if (done || now) t.primary else t.surface),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (done) FontIcon("check", t.onPrimary, 16f) else PText("${i + 1}", t, 13, if (now) t.onPrimary else t.muted, bold = true)
                        }
                        Box(Modifier.weight(1f).height(2.dp).background(if (i == items.lastIndex) Color.Transparent else if (i < b.active) t.primary else t.ink.copy(alpha = 0.12f)))
                    }
                    if (b.shows(CV.OPT_LABELS)) {
                        Spacer(Modifier.height(6.dp))
                        PText(item.label, t, 11, if (now) t.primary else t.muted, bold = now, align = TextAlign.Center, maxLines = 1)
                    }
                }
            }
        }
        CV.TOGGLES -> Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(r)).background(t.card)) {
            items.forEachIndexed { i, item ->
                Row(Modifier.fillMaxWidth().heightIn(min = if (ih > 0) ih.dp else 56.dp).padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (item.icon.isNotEmpty()) { FontIcon(item.icon, t.primary, 22f); Spacer(Modifier.width(12.dp)) }
                    PText(item.label, t, 14, modifier = Modifier.weight(1f))
                    Toggle(item.on, t)
                }
                if (i < items.lastIndex) Box(Modifier.padding(horizontal = 14.dp)) { divider() }
            }
        }
        CV.REVIEWS -> Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items.forEach { item ->
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(r)).background(t.card).padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (b.shows(CV.OPT_IMAGE)) {
                            Box(Modifier.size(40.dp).clip(CircleShape).background(t.surface), contentAlignment = Alignment.Center) {
                                if (item.image.isNotEmpty()) PhotoImage(item.image, 0, Modifier.size(40.dp))
                                else PText(item.label.take(1), t, 16, t.primary, bold = true)
                            }
                            Spacer(Modifier.width(10.dp))
                        }
                        Column(Modifier.weight(1f)) {
                            PText(item.label, t, 14, bold = true, maxLines = 1)
                            if (b.shows(CV.OPT_RATING)) Stars(item.value.toFloatOrNull() ?: 5f, t)
                        }
                    }
                    if (item.note.isNotBlank()) { Spacer(Modifier.height(8.dp)); PText(item.note, t, 13, t.muted) }
                }
            }
        }
        CV.DIVIDER -> Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.weight(1f).height(1.dp).background(t.ink.copy(alpha = 0.12f)))
            if (b.title.isNotBlank()) PText(b.title, t, 13, t.muted, modifier = Modifier.padding(horizontal = 12.dp))
            Box(Modifier.weight(1f).height(1.dp).background(t.ink.copy(alpha = 0.12f)))
        }
        CV.BANNER -> Box(Modifier.fillMaxWidth().height(if (ih > 0) ih.dp else 150.dp).clip(RoundedCornerShape(r)).background(t.surface), contentAlignment = Alignment.BottomStart) {
            if (b.image.isNotEmpty()) PhotoImage(b.image, 0, Modifier.fillMaxWidth().fillMaxHeight())
            else Box(Modifier.fillMaxWidth().fillMaxHeight(), contentAlignment = Alignment.Center) { FontIcon("image", t.primary.copy(alpha = 0.35f), 34f) }
            if (b.title.isNotBlank()) Box(
                Modifier.fillMaxWidth().background(androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f)))).padding(14.dp),
            ) { PText(b.title, t, 18, Color.White, bold = true, align = ta, modifier = Modifier.fillMaxWidth()) }
        }
        CV.PRODUCT -> Column(Modifier.fillMaxWidth()) {
            Box(Modifier.fillMaxWidth()) {
                Placeholder(t, Modifier.fillMaxWidth().height(if (ih > 0) ih.dp else 220.dp), r, b.image)
                if (b.shows(CV.OPT_FAV)) Box(
                    Modifier.align(Alignment.TopEnd).padding(10.dp).size(38.dp).clip(CircleShape).background(Color.White),
                    contentAlignment = Alignment.Center,
                ) { FontIcon("favorite_border", t.accent, 20f) }
            }
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                PText(b.title, t, 20, bold = true, modifier = Modifier.weight(1f))
                PText(price(b.price), t, 18, t.accent, bold = true)
            }
            if (b.shows(CV.OPT_RATING)) Row(Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Stars(4.6f, t); Spacer(Modifier.width(6.dp)); PText("4.6", t, 12, t.muted)
            }
            if (b.body.isNotBlank()) { Spacer(Modifier.height(10.dp)); PText(b.body, t, 13, t.muted) }
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (b.shows(CV.OPT_STEPPER)) { Stepper(1, t, 34.dp); Spacer(Modifier.width(14.dp)) }
                if (b.button.isNotBlank()) Box(Modifier.weight(1f).height(50.dp).clip(RoundedCornerShape(r * 0.7f)).background(t.primary), contentAlignment = Alignment.Center) {
                    PText(b.button, t, 15, t.onPrimary, bold = true)
                }
            }
        }
        CV.STATS -> Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items.forEach { item ->
                Column(Modifier.weight(1f).clip(RoundedCornerShape(r)).background(t.card).padding(vertical = 14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    PText(item.value, t, 20, t.primary, bold = true)
                    PText(item.label, t, 12, t.muted, align = TextAlign.Center, maxLines = 2)
                }
            }
        }
        CV.PROFILE -> Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(r)).background(t.card).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(if (ih > 0) ih.dp else 60.dp).clip(CircleShape).background(t.surface), contentAlignment = Alignment.Center) {
                if (b.image.isNotEmpty()) PhotoImage(b.image, 0, Modifier.size(if (ih > 0) ih.dp else 60.dp)) else FontIcon("person", t.primary, 30f)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                PText(b.title, t, 16, bold = true, maxLines = 1)
                if (b.subtitle.isNotBlank()) PText(b.subtitle, t, 12, t.muted, maxLines = 1)
            }
            if (b.button.isNotBlank()) Box(Modifier.clip(CircleShape).background(t.surface).padding(horizontal = 12.dp, vertical = 7.dp)) {
                PText(b.button, t, 12, t.primary, bold = true)
            }
        }
        CV.ALERT -> Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(r)).background(t.primary.copy(alpha = 0.1f)).border(1.dp, t.primary.copy(alpha = 0.25f), RoundedCornerShape(r)).padding(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            FontIcon(b.icon.ifBlank { "info" }, t.primary, 24f)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                PText(b.title, t, 14, bold = true)
                if (b.body.isNotBlank()) PText(b.body, t, 12, t.muted)
            }
            if (b.shows(CV.OPT_CLOSE)) FontIcon("close", t.muted, 18f)
        }
        CV.ICON_GRID -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items.chunked(4).forEach { row ->
                Row(Modifier.fillMaxWidth()) {
                    row.forEach { item ->
                        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            val s = if (ih > 0) ih.dp else 56.dp
                            Box(Modifier.size(s).clip(RoundedCornerShape(r)).background(t.surface), contentAlignment = Alignment.Center) {
                                FontIcon(item.icon.ifEmpty { "circle" }, t.primary, s.value * 0.46f)
                            }
                            Spacer(Modifier.height(6.dp))
                            PText(item.label, t, 12, align = TextAlign.Center, maxLines = 1)
                        }
                    }
                    repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
        CV.ADD_BAR -> Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            if (b.shows(CV.OPT_STEPPER)) { Stepper(1, t, 36.dp); Spacer(Modifier.width(14.dp)) }
            Row(
                Modifier.weight(1f).height(if (ih > 0) ih.dp else 52.dp).clip(RoundedCornerShape(r)).background(t.primary).padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PText(b.button, t, 15, t.onPrimary, bold = true, modifier = Modifier.weight(1f))
                if (b.price.isNotBlank()) PText(price(b.price), t, 15, t.onPrimary, bold = true)
            }
        }
        CV.CHECKS -> Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(r)).background(t.card)) {
            items.forEachIndexed { i, item ->
                Row(Modifier.fillMaxWidth().heightIn(min = 52.dp).padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(22.dp).clip(RoundedCornerShape(6.dp)).background(if (item.on) t.primary else Color.Transparent)
                            .border(2.dp, if (item.on) t.primary else t.ink.copy(alpha = 0.25f), RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center,
                    ) { if (item.on) FontIcon("check", t.onPrimary, 16f) }
                    Spacer(Modifier.width(12.dp))
                    PText(item.label, t, 14, modifier = Modifier.weight(1f))
                    if (item.value.isNotBlank()) PText("+" + price(item.value.trimStart('+')), t, 13, t.muted)
                }
                if (i < items.lastIndex) Box(Modifier.padding(horizontal = 14.dp)) { divider() }
            }
        }
    }
}
