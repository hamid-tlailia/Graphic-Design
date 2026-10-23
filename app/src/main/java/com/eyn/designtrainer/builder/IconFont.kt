package com.eyn.designtrainer.builder

import android.content.Context
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.eyn.designtrainer.R

/** The full Material Icons (Round) set, rendered from a font so every icon is available. */
/** Icon style for [FontIcon]s below; projects and canvas elements set it. */
val LocalIconStyle = androidx.compose.runtime.compositionLocalOf { 0 }

/** The full Material Icons set in five styles, rendered from fonts so every icon is available. */
object IconFont {
    data class Style(val label: com.eyn.designtrainer.i18n.L, val res: Int, val codepoints: String)

    val styles = listOf(
        Style(com.eyn.designtrainer.i18n.L("مستدير", "Rounded"), R.font.material_icons_round, "icons.codepoints"),
        Style(com.eyn.designtrainer.i18n.L("ممتلئ", "Solid"), R.font.material_icons_filled, "icons_filled.codepoints"),
        Style(com.eyn.designtrainer.i18n.L("محدد", "Outlined"), R.font.material_icons_outlined, "icons_outlined.codepoints"),
        Style(com.eyn.designtrainer.i18n.L("حاد", "Sharp"), R.font.material_icons_sharp, "icons_sharp.codepoints"),
        Style(com.eyn.designtrainer.i18n.L("لونين", "Two-tone"), R.font.material_icons_twotone, "icons_twotone.codepoints"),
    )

    private val families = styles.map { FontFamily(Font(it.res)) }

    fun family(style: Int = 0): FontFamily = families.getOrElse(style) { families[0] }

    val family: FontFamily get() = families[0]

    private val maps = arrayOfNulls<Map<String, String>>(styles.size)

    /** name → glyph for a style, loaded once from assets. */
    fun all(ctx: Context, style: Int = 0): Map<String, String> {
        val i = style.coerceIn(styles.indices)
        maps[i]?.let { return it }
        return synchronized(this) {
            maps[i] ?: ctx.assets.open(styles[i].codepoints).bufferedReader().useLines { lines ->
                lines.mapNotNull { line ->
                    val parts = line.trim().split(' ')
                    if (parts.size != 2) null else parts[0] to String(Character.toChars(parts[1].toInt(16)))
                }.toMap(LinkedHashMap())
            }.also { maps[i] = it }
        }
    }

    /** Names used by older designs before the full set existed. */
    private val legacy = mapOf(
        "heart" to "favorite", "cart" to "shopping_cart", "bell" to "notifications", "eye" to "visibility",
        "chart" to "show_chart", "arrow_fwd" to "arrow_forward",
    )

    fun glyph(ctx: Context, name: String, style: Int = 0): String? = all(ctx, style)[legacy[name] ?: name]

    /**
     * Arabic topic words → English icon-name keywords. Several keywords per topic so a word like
     * "عيادة" finds every medical icon, not just one.
     */
    private val arabic: Map<String, List<String>> = mapOf(
        // health
        "عياده" to listOf("hospital", "medical", "healing", "health", "vaccine", "medication", "monitor_heart", "emergency", "sick", "masks", "medical_services"),
        "مستشفي" to listOf("hospital", "medical", "emergency", "local_hospital"), "طبيب" to listOf("medical", "health", "healing", "person"),
        "دكتور" to listOf("medical", "health", "healing"), "صحه" to listOf("health", "medical", "favorite", "monitor_heart", "spa"),
        "دواء" to listOf("medication", "vaccines", "pill", "medical"), "صيدليه" to listOf("pharmacy", "medication", "local_pharmacy"),
        "اسنان" to listOf("dentistry", "medical"), "قلب" to listOf("favorite", "heart", "monitor_heart"), "مفضله" to listOf("favorite", "star", "bookmark"),
        "تجميل" to listOf("spa", "face", "brush", "auto_awesome", "content_cut"), "جمال" to listOf("spa", "face", "auto_awesome"), "صالون" to listOf("content_cut", "spa", "face"),
        "رياضه" to listOf("sports", "fitness", "directions_run", "pool"), "نادي" to listOf("fitness", "sports", "pool"), "يوغا" to listOf("self_improvement", "spa"),
        // food
        "قهوه" to listOf("coffee", "local_cafe", "emoji_food_beverage"), "مقهي" to listOf("local_cafe", "coffee"), "شاي" to listOf("emoji_food_beverage", "local_cafe"),
        "طعام" to listOf("restaurant", "fastfood", "lunch_dining", "dinner_dining", "food"), "اكل" to listOf("restaurant", "fastfood", "lunch_dining", "food"),
        "مطعم" to listOf("restaurant", "dinner_dining", "local_dining"), "بيتزا" to listOf("local_pizza"), "حلويات" to listOf("cake", "icecream", "bakery_dining"),
        "حلى" to listOf("cake", "icecream", "cookie"), "كعك" to listOf("cake", "bakery_dining"), "مخبز" to listOf("bakery_dining", "breakfast_dining"),
        "مشروب" to listOf("local_drink", "local_bar", "wine_bar", "coffee"), "ماء" to listOf("water_drop", "local_drink"), "فواكه" to listOf("nutrition", "eco"),
        // shopping
        "سله" to listOf("shopping_basket", "shopping_cart", "shopping_bag"), "تسوق" to listOf("shopping", "store", "local_mall", "sell"),
        "متجر" to listOf("store", "storefront", "shop", "local_mall"), "سوق" to listOf("store", "local_grocery_store", "storefront"),
        "عربه" to listOf("shopping_cart"), "شراء" to listOf("shopping_cart", "add_shopping_cart", "payments"), "خصم" to listOf("discount", "sell", "local_offer", "percent"),
        "عرض" to listOf("local_offer", "sell", "discount", "visibility"), "عروض" to listOf("local_offer", "sell", "discount"), "سعر" to listOf("sell", "attach_money", "price"),
        "هديه" to listOf("redeem", "card_giftcard", "gift"), "ملابس" to listOf("checkroom", "dry_cleaning"), "مجوهرات" to listOf("diamond"),
        // money
        "دفع" to listOf("payment", "payments", "credit_card", "account_balance_wallet"), "بطاقه" to listOf("credit_card", "card", "badge"),
        "مال" to listOf("payments", "attach_money", "savings", "account_balance"), "فلوس" to listOf("payments", "attach_money", "money"),
        "محفظه" to listOf("wallet", "account_balance_wallet"), "بنك" to listOf("account_balance", "savings"), "فاتوره" to listOf("receipt", "request_quote"),
        "طلب" to listOf("receipt", "shopping_bag", "order"), "طلبات" to listOf("receipt", "list_alt", "shopping_bag"),
        // transport & places
        "توصيل" to listOf("local_shipping", "delivery_dining", "moped"), "شحن" to listOf("local_shipping", "inventory"), "سياره" to listOf("directions_car", "car", "local_taxi"),
        "تاكسي" to listOf("local_taxi"), "طائره" to listOf("flight", "airplane"), "سفر" to listOf("flight", "luggage", "travel", "beach_access"),
        "فندق" to listOf("hotel", "bed", "apartment"), "موقع" to listOf("location", "place", "map", "pin_drop"), "خريطه" to listOf("map", "explore", "location"),
        "عنوان" to listOf("location", "home", "place"), "منزل" to listOf("home", "house"), "بيت" to listOf("home", "house", "cottage"), "رئيسيه" to listOf("home", "dashboard"),
        "مسجد" to listOf("mosque"), "مدرسه" to listOf("school", "menu_book"), "جامعه" to listOf("school", "account_balance"), "عمل" to listOf("work", "business_center"),
        "مكتب" to listOf("business", "work", "desk"), "شركه" to listOf("business", "apartment", "corporate"),
        // people & communication
        "مستخدم" to listOf("person", "account", "people"), "شخص" to listOf("person", "face"), "حساب" to listOf("account", "person", "manage_accounts"),
        "حسابي" to listOf("account_circle", "person"), "فريق" to listOf("groups", "people", "diversity"), "عائله" to listOf("family", "people"),
        "اطفال" to listOf("child", "toys", "stroller"), "رساله" to listOf("mail", "message", "chat", "sms"), "بريد" to listOf("mail", "email", "inbox"),
        "هاتف" to listOf("phone", "call", "smartphone"), "اتصال" to listOf("call", "phone", "contact"), "تواصل" to listOf("chat", "call", "mail", "contact", "forum"),
        "دردشه" to listOf("chat", "forum", "message"), "واتساب" to listOf("chat", "phone", "message"), "اشعار" to listOf("notifications"), "اشعارات" to listOf("notifications"),
        "جرس" to listOf("notifications", "doorbell"), "مشاركه" to listOf("share", "ios_share"), "دعم" to listOf("support", "support_agent", "help"),
        "مساعده" to listOf("help", "support", "info"),
        // ui & actions
        "بحث" to listOf("search", "manage_search", "find"), "اعدادات" to listOf("settings", "tune"), "قائمه" to listOf("menu", "list"), "قفل" to listOf("lock", "security"),
        "امان" to listOf("security", "lock", "shield", "verified"), "حمايه" to listOf("shield", "security"), "تحقق" to listOf("check", "verified", "done"),
        "صح" to listOf("check", "done"), "خطا" to listOf("error", "close"), "اغلاق" to listOf("close", "cancel"), "اضافه" to listOf("add"), "حذف" to listOf("delete", "remove"),
        "تعديل" to listOf("edit", "tune"), "قلم" to listOf("edit", "draw", "create"), "تنزيل" to listOf("download"), "تحميل" to listOf("download", "upload"),
        "رفع" to listOf("upload"), "سهم" to listOf("arrow"), "رجوع" to listOf("arrow_back", "undo"), "التالي" to listOf("arrow_forward", "navigate_next"),
        "تحديث" to listOf("refresh", "sync"), "فلتر" to listOf("filter", "tune"), "تصفيه" to listOf("filter", "tune"), "ترتيب" to listOf("sort", "reorder"),
        "عين" to listOf("visibility", "eye"), "اخفاء" to listOf("visibility_off"), "معلومات" to listOf("info"), "تحذير" to listOf("warning", "report"),
        "نجمه" to listOf("star", "grade"), "تقييم" to listOf("star", "grade", "thumb_up", "reviews"), "اعجاب" to listOf("thumb_up", "favorite"),
        "وقت" to listOf("schedule", "timer", "access_time"), "ساعه" to listOf("schedule", "watch", "alarm"), "تقويم" to listOf("calendar", "event", "date_range"),
        "موعد" to listOf("event", "calendar", "schedule"), "حجز" to listOf("event_available", "book_online", "calendar"), "منبه" to listOf("alarm"),
        // media & design
        "صوره" to listOf("image", "photo", "insert_photo"), "صور" to listOf("photo", "image", "collections"), "كاميرا" to listOf("camera", "photo_camera"),
        "فيديو" to listOf("videocam", "movie", "play_circle"), "موسيقي" to listOf("music", "headphones", "library_music"), "صوت" to listOf("volume", "mic"),
        "ميكروفون" to listOf("mic"), "تشغيل" to listOf("play"), "ايقاف" to listOf("pause", "stop"), "لون" to listOf("palette", "color", "format_paint"),
        "الوان" to listOf("palette", "color"), "رسم" to listOf("draw", "brush", "palette"), "تصميم" to listOf("design", "palette", "brush", "draw"), "نص" to listOf("text", "title", "font"),
        "خط" to listOf("font", "text_fields"), "ملف" to listOf("file", "description", "article"), "مجلد" to listOf("folder"), "كتاب" to listOf("book", "menu_book", "auto_stories"),
        "دروس" to listOf("school", "menu_book", "class"), "تعليم" to listOf("school", "menu_book", "cast_for_education"),
        // nature & misc
        "شمس" to listOf("sunny", "light_mode", "wb_sunny"), "قمر" to listOf("dark_mode", "nightlight"), "طقس" to listOf("cloud", "sunny", "thermostat"),
        "نبات" to listOf("eco", "park", "local_florist", "yard"), "ورد" to listOf("local_florist"), "حيوان" to listOf("pets"), "قطه" to listOf("pets"), "كلب" to listOf("pets"),
        "بحر" to listOf("beach_access", "pool", "sailing"), "انترنت" to listOf("wifi", "language", "public"), "واي" to listOf("wifi"), "لغه" to listOf("language", "translate"),
        "عالم" to listOf("public", "language"), "علم" to listOf("flag", "science"), "مفتاح" to listOf("key", "vpn_key"), "سياره كهربائيه" to listOf("electric_car"),
        "كهرباء" to listOf("bolt", "electric", "power"), "طاقه" to listOf("bolt", "battery", "power"), "بطاريه" to listOf("battery"), "جوال" to listOf("smartphone", "phone_iphone"),
        "حاسوب" to listOf("computer", "laptop"), "كمبيوتر" to listOf("computer", "laptop", "desktop"), "العاب" to listOf("sports_esports", "videogame"), "لعبه" to listOf("sports_esports", "toys"),
        "ساعه يد" to listOf("watch"), "تاج" to listOf("workspace_premium", "diamond"), "جائزه" to listOf("emoji_events", "military_tech", "workspace_premium"),
        "صاروخ" to listOf("rocket", "rocket_launch"), "فكره" to listOf("lightbulb", "tips_and_updates"), "ذكاء" to listOf("psychology", "auto_awesome", "smart_toy"),
    )

    /** English words people type that aren't icon names. */
    private val englishSynonyms = mapOf(
        "clinic" to listOf("hospital", "medical", "healing"), "doctor" to listOf("medical", "healing", "health"), "medicine" to listOf("medication", "pharmacy"),
        "money" to listOf("payments", "attach_money", "savings"), "cart" to listOf("shopping_cart"), "basket" to listOf("shopping_basket"),
        "food" to listOf("restaurant", "fastfood", "lunch_dining"), "cafe" to listOf("local_cafe", "coffee"), "user" to listOf("person", "account"),
        "profile" to listOf("person", "account_circle"), "message" to listOf("chat", "mail", "sms"), "gift" to listOf("redeem", "card_giftcard"),
        "delivery" to listOf("local_shipping", "delivery_dining"), "car" to listOf("directions_car", "local_taxi"), "beauty" to listOf("spa", "face"),
        "gym" to listOf("fitness_center"), "offer" to listOf("local_offer", "sell"), "price" to listOf("sell", "attach_money"), "booking" to listOf("event_available", "book_online"),
    )

    /** Loose Arabic normalisation so spelling variants still match. */
    private fun normalize(t: String): String {
        var s = t.trim()
            .replace(Regex("[\u064B-\u0652\u0640]"), "") // diacritics and tatweel
            .replace('أ', 'ا').replace('إ', 'ا').replace('آ', 'ا')
            .replace('ة', 'ه').replace('ى', 'ي').replace('ؤ', 'و').replace('ئ', 'ي')
        if (s.startsWith("ال") && s.length > 3) s = s.removePrefix("ال")
        return s.lowercase()
    }

    private val normalizedArabic by lazy { arabic.mapKeys { normalize(it.key) } }

    fun search(ctx: Context, query: String, style: Int = 0): List<Pair<String, String>> {
        val all = all(ctx, style)
        val q = normalize(query)
        if (q.isEmpty()) return all.toList()
        val terms = buildSet {
            // English: words of the query, joined or split on spaces
            add(q.replace(' ', '_'))
            q.split(' ').filter { it.length >= 2 }.forEach { add(it); englishSynonyms[it]?.let { syn -> addAll(syn) } }
            // Arabic: every topic whose word contains (or is contained in) the query
            q.split(' ').filter { it.isNotBlank() }.forEach { word ->
                normalizedArabic.forEach { (ar, en) -> if (ar.contains(word) || (word.length >= 3 && word.contains(ar))) addAll(en) }
            }
        }
        return all.filterKeys { name -> terms.any { name.contains(it) } }.toList()
            .sortedBy { (name, _) -> if (terms.any { name == it }) 0 else if (terms.any { name.startsWith(it) }) 1 else 2 }
    }
}
