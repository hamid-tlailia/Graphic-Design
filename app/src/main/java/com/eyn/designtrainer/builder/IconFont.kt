package com.eyn.designtrainer.builder

import android.content.Context
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.eyn.designtrainer.R

/** The full Material Icons (Round) set, rendered from a font so every icon is available. */
object IconFont {
    val family = FontFamily(Font(R.font.material_icons_round))

    @Volatile private var map: Map<String, String>? = null

    /** name → glyph, loaded once from assets. */
    fun all(ctx: Context): Map<String, String> = map ?: synchronized(this) {
        map ?: ctx.assets.open("icons.codepoints").bufferedReader().useLines { lines ->
            lines.mapNotNull { line ->
                val parts = line.trim().split(' ')
                if (parts.size != 2) null else parts[0] to String(Character.toChars(parts[1].toInt(16)))
            }.toMap(LinkedHashMap())
        }.also { map = it }
    }

    /** Names used by older designs before the full set existed. */
    private val legacy = mapOf(
        "heart" to "favorite", "cart" to "shopping_cart", "bell" to "notifications", "eye" to "visibility",
        "chart" to "show_chart", "arrow_fwd" to "arrow_forward",
    )

    fun glyph(ctx: Context, name: String): String? = all(ctx)[legacy[name] ?: name]

    /** Arabic words → English icon keywords, so Arabic search finds icons too. */
    private val arabic = mapOf(
        "بيت" to "home", "منزل" to "home", "رئيسية" to "home", "بحث" to "search", "قلب" to "favorite", "مفضلة" to "favorite",
        "نجمة" to "star", "تقييم" to "star", "سلة" to "shopping", "تسوق" to "shopping", "متجر" to "store", "رسالة" to "mail",
        "بريد" to "mail", "هاتف" to "phone", "اتصال" to "call", "إعدادات" to "settings", "مستخدم" to "person", "شخص" to "person",
        "حساب" to "account", "قفل" to "lock", "كاميرا" to "camera", "صورة" to "image", "صور" to "photo", "موقع" to "location",
        "خريطة" to "map", "وقت" to "schedule", "ساعة" to "schedule", "تقويم" to "calendar", "تاريخ" to "calendar", "سهم" to "arrow",
        "إغلاق" to "close", "اغلاق" to "close", "إضافة" to "add", "اضافة" to "add", "حذف" to "delete", "تعديل" to "edit",
        "قلم" to "edit", "مشاركة" to "share", "تنزيل" to "download", "تحميل" to "download", "رفع" to "upload", "إشعار" to "notifications",
        "جرس" to "notifications", "قهوة" to "coffee", "طعام" to "restaurant", "مطعم" to "restaurant", "سيارة" to "car",
        "دفع" to "payment", "بطاقة" to "credit", "عين" to "visibility", "واي" to "wifi", "شمس" to "sunny", "قمر" to "dark_mode",
        "موسيقى" to "music", "فيديو" to "videocam", "ملف" to "file", "مجلد" to "folder", "دردشة" to "chat", "محادثة" to "chat",
        "علم" to "flag", "هدية" to "gift", "كتاب" to "book", "مدرسة" to "school", "تعليم" to "school", "قائمة" to "menu",
        "تحقق" to "check", "صح" to "check", "خطأ" to "error", "تحذير" to "warning", "معلومات" to "info", "مساعدة" to "help",
        "سفر" to "flight", "طائرة" to "flight", "رياضة" to "sports", "صحة" to "health", "طقس" to "cloud", "لغة" to "language",
        "عالم" to "public", "أمان" to "security", "مفتاح" to "key", "فاتورة" to "receipt", "مال" to "payments", "محفظة" to "wallet",
        "عرض" to "visibility", "إخفاء" to "visibility_off", "صوت" to "volume", "ميكروفون" to "mic", "تشغيل" to "play",
        "إيقاف" to "pause", "رسم" to "draw", "لون" to "palette", "ألوان" to "palette", "نص" to "text", "خط" to "font",
    )

    fun search(ctx: Context, query: String): List<Pair<String, String>> {
        val q = query.trim().lowercase()
        val all = all(ctx)
        if (q.isEmpty()) return all.toList()
        val terms = buildSet {
            add(q.replace(' ', '_'))
            arabic.forEach { (ar, en) -> if (ar.contains(q) || q.contains(ar)) add(en) }
        }
        return all.filterKeys { name -> terms.any { name.contains(it) } }.toList()
            .sortedBy { (name, _) -> if (terms.any { name.startsWith(it) }) 0 else 1 }
    }
}
