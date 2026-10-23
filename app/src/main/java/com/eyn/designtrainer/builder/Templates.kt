package com.eyn.designtrainer.builder

import com.eyn.designtrainer.data.Design
import com.eyn.designtrainer.data.El
import com.eyn.designtrainer.data.ElType
import com.eyn.designtrainer.i18n.L
import com.eyn.designtrainer.i18n.Lang

enum class Kind(val label: L) {
    BUTTON(L("الأزرار", "Buttons")),
    CARD(L("البطاقات", "Cards")),
    HEADER(L("الهيدر", "Headers")),
    FOOTER(L("الفوتر", "Footers")),
    LOGIN(L("تسجيل الدخول", "Login")),
    LANDING(L("صفحات الهبوط", "Landing")),
    LOGO(L("الشعارات", "Logos")),
}

/** Template element: like [El] but with bilingual text. Coordinates are authored left-to-right. */
data class TEl(
    val type: ElType, val x: Float, val y: Float, val w: Float, val h: Float,
    val bg: Long = 0x00000000, val fg: Long = INK, val text: L? = null, val fontSize: Float = 14f,
    val bold: Boolean = false, val radius: Float = 0f, val borderW: Float = 0f, val borderColor: Long = LINE,
    val align: Int = 0, val icon: String = "",
)

data class Template(val id: String, val kind: Kind, val name: L, val w: Float, val h: Float, val bg: Long, val els: List<TEl>)

const val NAVY = 0xFF2A2470
const val CORAL = 0xFFF2603F
const val LILAC = 0xFFE8E5FA
const val CREAM = 0xFFF7F6F2
const val WHITE = 0xFFFFFFFF
const val INK = 0xFF1D1A3F
const val GRAY = 0xFF7C7A92
const val LINE = 0xFFEAE8F0
const val CLEAR = 0x00000000L
const val SOFT = 0xFFC9C5F5

private class TB {
    val els = mutableListOf<TEl>()
    fun box(x: Number, y: Number, w: Number, h: Number, bg: Long, r: Number = 0, bw: Number = 0, bc: Long = LINE) {
        els += TEl(ElType.BOX, x.f, y.f, w.f, h.f, bg = bg, radius = r.f, borderW = bw.f, borderColor = bc)
    }
    fun txt(x: Number, y: Number, w: Number, h: Number, t: L, size: Number, color: Long, bold: Boolean = false, align: Int = 0) {
        els += TEl(ElType.TEXT, x.f, y.f, w.f, h.f, fg = color, text = t, fontSize = size.f, bold = bold, align = align)
    }
    fun btn(x: Number, y: Number, w: Number, h: Number, bg: Long, fg: Long, t: L, size: Number = 15, r: Number = 14, bw: Number = 0, bc: Long = LINE) {
        els += TEl(ElType.BUTTON, x.f, y.f, w.f, h.f, bg = bg, fg = fg, text = t, fontSize = size.f, bold = true, radius = r.f, borderW = bw.f, borderColor = bc, align = 1)
    }
    fun input(x: Number, y: Number, w: Number, h: Number, bg: Long, t: L, r: Number = 14, bw: Number = 1) {
        els += TEl(ElType.INPUT, x.f, y.f, w.f, h.f, bg = bg, fg = GRAY, text = t, fontSize = 14f, radius = r.f, borderW = bw.f, borderColor = LINE)
    }
    fun img(x: Number, y: Number, w: Number, h: Number, r: Number = 16) {
        els += TEl(ElType.IMAGE, x.f, y.f, w.f, h.f, bg = LILAC, fg = NAVY, radius = r.f)
    }
    fun circle(x: Number, y: Number, s: Number, bg: Long, fg: Long = WHITE, t: L? = null, size: Number = 18) {
        els += TEl(ElType.CIRCLE, x.f, y.f, s.f, s.f, bg = bg, fg = fg, text = t, fontSize = size.f, bold = true, align = 1)
    }
    fun icon(x: Number, y: Number, s: Number, name: String, color: Long) {
        els += TEl(ElType.ICON, x.f, y.f, s.f, s.f, fg = color, icon = name)
    }
    fun line(x: Number, y: Number, w: Number, color: Long = LINE) {
        els += TEl(ElType.DIVIDER, x.f, y.f, w.f, 1f, bg = color)
    }
    private val Number.f get() = toFloat()
}

private fun t(id: String, kind: Kind, name: L, w: Int, h: Int, bg: Long, block: TB.() -> Unit) =
    Template(id, kind, name, w.toFloat(), h.toFloat(), bg, TB().apply(block).els)

private fun l(ar: String, en: String) = L(ar, en)

val Templates: List<Template> = listOf(
    // ------------------------------------------------------------------ buttons
    t("btn_primary", Kind.BUTTON, l("زر أساسي", "Primary button"), 320, 140, CREAM) {
        btn(60, 46, 200, 48, NAVY, WHITE, l("ابدأ الآن", "Get started"), 16)
    },
    t("btn_set", Kind.BUTTON, l("مجموعة أزرار", "Button set"), 320, 232, CREAM) {
        btn(40, 28, 240, 48, NAVY, WHITE, l("متابعة", "Continue"), 16)
        btn(40, 92, 240, 48, CLEAR, NAVY, l("اعرف المزيد", "Learn more"), 16, bw = 1.5, bc = NAVY)
        btn(40, 156, 240, 48, CORAL, WHITE, l("اشترِ الآن", "Buy now"), 16, r = 24)
    },
    t("btn_icon", Kind.BUTTON, l("زر مع أيقونة", "Icon button"), 320, 140, CREAM) {
        btn(70, 46, 180, 48, CORAL, WHITE, l("التالي", "Next"), 16, r = 24)
        icon(214, 58, 24, "arrow_fwd", WHITE)
    },
    // ------------------------------------------------------------------ cards
    t("card_product", Kind.CARD, l("بطاقة منتج", "Product card"), 300, 400, LILAC) {
        box(16, 16, 268, 368, WHITE, 20)
        img(32, 32, 236, 170, 14)
        txt(32, 218, 236, 26, l("كرسي بسيط", "Minimal chair"), 18, INK, true)
        txt(32, 248, 236, 40, l("قماش ناعم وأرجل من خشب البلوط.", "Soft fabric, solid oak legs."), 13, GRAY)
        txt(32, 300, 120, 30, l("129 \$", "\$129"), 20, CORAL, true)
        btn(172, 294, 96, 40, NAVY, WHITE, l("أضف", "Add"), 14, 12)
    },
    t("card_profile", Kind.CARD, l("بطاقة ملف شخصي", "Profile card"), 300, 380, CREAM) {
        box(16, 16, 268, 348, WHITE, 24)
        circle(110, 40, 80, NAVY, WHITE, l("ح", "H"), 30)
        txt(32, 134, 236, 26, l("حميد علي", "Hamid Ali"), 18, INK, true, 1)
        txt(32, 162, 236, 20, l("مصمم منتجات", "Product designer"), 13, GRAY, align = 1)
        box(32, 198, 236, 64, CREAM, 16)
        txt(40, 206, 70, 26, l("128", "128"), 18, NAVY, true, 1)
        txt(40, 232, 70, 20, l("مشروع", "Projects"), 11, GRAY, align = 1)
        txt(115, 206, 70, 26, l("4.9", "4.9"), 18, NAVY, true, 1)
        txt(115, 232, 70, 20, l("تقييم", "Rating"), 11, GRAY, align = 1)
        txt(190, 206, 70, 26, l("2.4k", "2.4k"), 18, NAVY, true, 1)
        txt(190, 232, 70, 20, l("متابع", "Followers"), 11, GRAY, align = 1)
        btn(32, 282, 112, 44, NAVY, WHITE, l("متابعة", "Follow"), 14, 12)
        btn(156, 282, 112, 44, CLEAR, NAVY, l("رسالة", "Message"), 14, 12, bw = 1.5, bc = NAVY)
    },
    t("card_stat", Kind.CARD, l("بطاقة إحصائية", "Stat card"), 320, 180, CREAM) {
        box(16, 16, 288, 148, NAVY, 20)
        txt(36, 36, 200, 20, l("الإيرادات الشهرية", "Monthly revenue"), 13, SOFT)
        txt(36, 60, 220, 40, l("24,500 \$", "\$24,500"), 30, WHITE, true)
        btn(36, 114, 84, 28, CORAL, WHITE, l("+12.5%", "+12.5%"), 12, 14)
        icon(256, 36, 28, "chart", CORAL)
    },
    // ------------------------------------------------------------------ headers
    t("hdr_appbar", Kind.HEADER, l("شريط تطبيق", "App bar"), 360, 88, CREAM) {
        box(0, 0, 360, 88, WHITE)
        circle(16, 24, 40, NAVY, WHITE, l("ع", "E"), 18)
        txt(66, 24, 160, 22, l("استوديو ريشة", "Risha Studio"), 16, INK, true)
        txt(66, 46, 160, 18, l("مساء الخير", "Good evening"), 12, GRAY)
        icon(276, 30, 28, "search", INK)
        icon(316, 30, 28, "menu", INK)
        line(0, 87, 360)
    },
    t("hdr_hero", Kind.HEADER, l("هيدر رئيسي", "Hero header"), 360, 300, NAVY) {
        txt(20, 24, 120, 28, l("ريشة", "Risha"), 20, WHITE, true)
        icon(312, 24, 28, "menu", WHITE)
        txt(20, 80, 320, 76, l("تصميم يتحدث عن نفسه", "Design that speaks for itself"), 28, WHITE, true)
        txt(20, 162, 300, 44, l("تعلّم قواعد التصميم الجيد في دقيقة كل يوم.", "Learn the rules of great design in one minute a day."), 14, SOFT)
        btn(20, 228, 150, 48, CORAL, WHITE, l("ابدأ مجاناً", "Start free"), 15)
        btn(182, 228, 140, 48, CLEAR, WHITE, l("شاهد العرض", "Watch demo"), 15, bw = 1.5, bc = WHITE)
    },
    t("hdr_search", Kind.HEADER, l("هيدر مع بحث", "Search header"), 360, 150, WHITE) {
        txt(20, 24, 260, 32, l("ابحث عن الإلهام", "Find inspiration"), 22, INK, true)
        circle(304, 22, 36, LILAC)
        icon(312, 30, 20, "bell", NAVY)
        input(20, 76, 320, 48, CREAM, l("ابحث في القوالب...", "Search templates..."), bw = 0)
        icon(304, 88, 24, "search", GRAY)
    },
    // ------------------------------------------------------------------ footers
    t("ftr_simple", Kind.FOOTER, l("فوتر بسيط", "Simple footer"), 360, 230, NAVY) {
        txt(24, 28, 200, 28, l("ريشة", "Risha"), 22, WHITE, true)
        txt(24, 60, 300, 40, l("تعلّم، تدرّب، صمّم بثقة.", "Learn, practice, design with confidence."), 13, SOFT)
        txt(24, 112, 76, 20, l("من نحن", "About"), 13, WHITE)
        txt(104, 112, 76, 20, l("الدروس", "Lessons"), 13, WHITE)
        txt(184, 112, 76, 20, l("الاستوديو", "Studio"), 13, WHITE)
        txt(264, 112, 76, 20, l("تواصل", "Contact"), 13, WHITE)
        line(24, 152, 312, 0x33FFFFFF)
        txt(24, 172, 230, 20, l("© 2026 ريشة. جميع الحقوق محفوظة.", "© 2026 Risha. All rights reserved."), 11, 0xFF9C99C8)
        icon(276, 170, 22, "mail", WHITE)
        icon(310, 170, 22, "public", WHITE)
    },
    t("ftr_news", Kind.FOOTER, l("فوتر مع اشتراك", "Newsletter footer"), 360, 280, CREAM) {
        box(16, 16, 328, 248, WHITE, 24)
        txt(36, 40, 288, 28, l("اشترك في نشرتنا", "Join our newsletter"), 20, INK, true, 1)
        txt(36, 72, 288, 40, l("نصائح تصميم أسبوعية في بريدك.", "Weekly design tips straight to your inbox."), 13, GRAY, align = 1)
        input(36, 128, 288, 48, CREAM, l("البريد الإلكتروني", "Email address"))
        btn(36, 188, 288, 48, CORAL, WHITE, l("اشترك", "Subscribe"), 15)
    },
    // ------------------------------------------------------------------ login
    t("login_classic", Kind.LOGIN, l("تسجيل دخول", "Sign in"), 360, 670, CREAM) {
        circle(148, 48, 64, NAVY)
        icon(164, 64, 32, "eye", WHITE)
        txt(24, 132, 312, 34, l("مرحباً بعودتك", "Welcome back"), 26, INK, true, 1)
        txt(24, 170, 312, 22, l("سجّل الدخول لمتابعة التعلم", "Sign in to continue learning"), 14, GRAY, align = 1)
        txt(24, 222, 312, 20, l("البريد الإلكتروني", "Email"), 13, INK, true)
        input(24, 246, 312, 52, WHITE, l("name@example.com", "name@example.com"))
        txt(24, 314, 312, 20, l("كلمة المرور", "Password"), 13, INK, true)
        input(24, 338, 312, 52, WHITE, l("••••••••", "••••••••"))
        txt(24, 400, 312, 20, l("نسيت كلمة المرور؟", "Forgot password?"), 13, CORAL, true, 2)
        btn(24, 440, 312, 52, NAVY, WHITE, l("تسجيل الدخول", "Sign in"), 16)
        line(24, 524, 130)
        txt(154, 514, 52, 20, l("أو", "or"), 12, GRAY, align = 1)
        line(206, 524, 130)
        btn(24, 552, 312, 52, WHITE, INK, l("المتابعة مع Google", "Continue with Google"), 15, bw = 1, bc = LINE)
        txt(24, 624, 312, 22, l("جديد هنا؟ أنشئ حساباً", "New here? Create an account"), 13, NAVY, align = 1)
    },
    t("login_signup", Kind.LOGIN, l("إنشاء حساب", "Sign up"), 360, 640, CREAM) {
        box(0, 0, 360, 230, NAVY)
        txt(24, 56, 312, 36, l("إنشاء حساب", "Create account"), 26, WHITE, true)
        txt(24, 98, 312, 40, l("ابدأ رحلتك في التصميم اليوم.", "Start your design journey today."), 14, SOFT)
        box(16, 170, 328, 440, WHITE, 24)
        input(36, 200, 288, 50, CREAM, l("الاسم الكامل", "Full name"), 12, 0)
        input(36, 264, 288, 50, CREAM, l("البريد الإلكتروني", "Email"), 12, 0)
        input(36, 328, 288, 50, CREAM, l("كلمة المرور", "Password"), 12, 0)
        btn(36, 404, 288, 52, CORAL, WHITE, l("إنشاء الحساب", "Sign up"), 16)
        txt(36, 474, 288, 40, l("بالتسجيل فإنك توافق على الشروط.", "By signing up you agree to our Terms."), 12, GRAY, align = 1)
        txt(36, 540, 288, 22, l("لديك حساب؟ سجّل الدخول", "Have an account? Sign in"), 13, NAVY, true, 1)
    },
    // ------------------------------------------------------------------ landing
    t("landing_app", Kind.LANDING, l("صفحة هبوط تطبيق", "App landing page"), 360, 1100, CREAM) {
        box(0, 0, 360, 64, WHITE)
        circle(16, 14, 36, NAVY)
        icon(24, 22, 20, "eye", WHITE)
        txt(60, 20, 120, 24, l("ريشة", "Risha"), 18, INK, true)
        btn(256, 14, 88, 36, NAVY, WHITE, l("انضم", "Join"), 13, 10)
        btn(24, 96, 160, 30, LILAC, NAVY, l("جديد · الإصدار 2", "New · v2.0"), 12, 15)
        txt(24, 140, 312, 84, l("تعلّم التصميم دقيقة كل يوم", "Learn design one minute a day"), 30, INK, true)
        txt(24, 228, 312, 48, l("دروس قصيرة وتحديات يومية وقوالب جاهزة للتطبيق.", "Short lessons, daily challenges, and ready templates to practice."), 14, GRAY)
        btn(24, 292, 150, 50, CORAL, WHITE, l("ابدأ الآن", "Get started"), 15)
        btn(186, 292, 150, 50, CLEAR, NAVY, l("استكشف", "Explore"), 15, bw = 1.5, bc = NAVY)
        img(24, 370, 312, 220, 24)
        txt(24, 624, 312, 30, l("لماذا ريشة؟", "Why Risha?"), 22, INK, true)
        val feats = listOf(
            Triple("bolt", l("تحديات يومية", "Daily challenges"), l("خمسة أسئلة سريعة كل يوم.", "Five quick questions every day.")),
            Triple("school", l("دروس قصيرة", "Short lessons"), l("تعلّم قاعدة في 5 دقائق.", "Learn a rule in 5 minutes.")),
            Triple("dashboard", l("استوديو القوالب", "Template studio"), l("عدّل تصاميم جاهزة.", "Customize ready layouts.")),
        )
        feats.forEachIndexed { i, (ic, title, desc) ->
            val y = 670 + i * 96
            box(24, y, 312, 84, WHITE, 18)
            circle(40, y + 18, 48, LILAC)
            icon(52, y + 30, 24, ic, NAVY)
            txt(104, y + 18, 216, 24, title, 15, INK, true)
            txt(104, y + 42, 216, 24, desc, 12, GRAY)
        }
        box(0, 972, 360, 128, NAVY)
        txt(24, 990, 312, 30, l("جاهز لتطوير مهارتك؟", "Ready to level up?"), 20, WHITE, true)
        btn(24, 1036, 150, 44, CORAL, WHITE, l("حمّل الآن", "Download"), 14, 12)
    },
    t("landing_pricing", Kind.LANDING, l("قسم الأسعار", "Pricing section"), 360, 620, CREAM) {
        txt(24, 32, 312, 32, l("أسعار بسيطة", "Simple pricing"), 24, INK, true, 1)
        txt(24, 68, 312, 20, l("اختر الخطة المناسبة لك", "Choose the plan that fits you"), 13, GRAY, align = 1)
        box(24, 112, 312, 212, WHITE, 20, 1, LINE)
        txt(44, 132, 200, 22, l("الأساسية", "Basic"), 15, GRAY, true)
        txt(44, 158, 200, 40, l("مجاناً", "\$0"), 30, INK, true)
        txt(44, 204, 272, 40, l("دروس وتحديات يومية", "Daily lessons and challenges"), 13, GRAY)
        btn(44, 256, 272, 48, CREAM, NAVY, l("خطتك الحالية", "Current plan"), 14, 12)
        box(24, 344, 312, 236, NAVY, 20)
        btn(228, 364, 88, 26, CORAL, WHITE, l("الأشهر", "Popular"), 11, 13)
        txt(44, 364, 170, 22, l("الاحترافية", "Pro"), 15, SOFT, true)
        txt(44, 390, 200, 40, l("4.99 \$", "\$4.99"), 30, WHITE, true)
        txt(44, 436, 272, 40, l("كل القوالب والتصدير والدروس المتقدمة", "All templates, export and advanced lessons"), 13, SOFT)
        btn(44, 508, 272, 48, WHITE, NAVY, l("ترقية", "Upgrade"), 14, 12)
    },
)

/** Logo starters (600×600) — refine them in the editor and export as transparent HD PNG. */
val LogoTemplates: List<Template> = listOf(
    t("logo_icon_text", Kind.LOGO, l("أيقونة + اسم", "Icon + name"), 600, 600, CREAM) {
        box(60, 220, 160, 160, NAVY, 48)
        icon(96, 256, 88, "visibility", WHITE)
        txt(250, 240, 320, 90, l("ريشة", "Risha"), 72, NAVY, true)
        txt(250, 330, 320, 40, l("درّب عينك", "Train your eye"), 24, GRAY)
    },
    t("logo_badge", Kind.LOGO, l("شارة دائرية", "Round badge"), 600, 600, WHITE) {
        circle(100, 100, 400, CORAL, WHITE, l("قهوتي", "Brew"), 72)
        txt(100, 400, 400, 40, l("منذ 2026", "SINCE 2026"), 22, WHITE, true, 1)
    },
    t("logo_monogram", Kind.LOGO, l("حرف أول", "Monogram"), 600, 600, CREAM) {
        box(150, 110, 300, 300, CLEAR, 150, 10, NAVY)
        txt(150, 110, 300, 300, l("ح", "H"), 170, NAVY, true, 1)
        txt(60, 440, 480, 60, l("حميد للتصميم", "HAMID STUDIO"), 34, NAVY, true, 1)
    },
    t("logo_stacked", Kind.LOGO, l("أيقونة فوق الاسم", "Stacked"), 600, 600, NAVY) {
        icon(225, 120, 150, "local_cafe", CORAL)
        txt(60, 300, 480, 100, l("بُنّ", "BEAN"), 84, WHITE, true, 1)
        line(220, 420, 160, CORAL)
        txt(60, 440, 480, 40, l("محمصة مختصة", "SPECIALTY ROASTERS"), 22, SOFT, align = 1)
    },
)

/** Builds an editable design. Arabic mirrors the layout horizontally so it reads right-to-left. */
fun Template.toDesign(lang: Lang): Design {
    val rtl = lang == Lang.AR
    return Design(
        name = name.get(lang),
        kind = kind.name,
        w = w, h = h, bg = bg,
        els = els.map { e ->
            El(
                type = e.type,
                x = if (rtl) w - e.x - e.w else e.x, y = e.y, w = e.w, h = e.h,
                bg = e.bg, fg = e.fg, text = e.text?.get(lang) ?: "",
                fontSize = e.fontSize, bold = e.bold, radius = e.radius,
                borderW = e.borderW, borderColor = e.borderColor,
                align = if (rtl) 2 - e.align else e.align,
                icon = if (rtl && e.icon == "arrow_fwd") "arrow_back" else e.icon,
            )
        },
    )
}

fun blankDesign(lang: Lang, w: Float = 360f, h: Float = 640f) = Design(
    name = if (lang == Lang.AR) "تصميم جديد" else "New design",
    kind = "BLANK", w = w, h = h, bg = WHITE, els = emptyList(),
)

/** Turns the project's identity into an editable canvas so the logo can be refined and exported in HD. */
fun logoDesign(p: com.eyn.designtrainer.data.AppProject, lang: Lang): Design {
    val pal = p.colors()
    val font = if (p.logoFont >= 0) p.logoFont else when (p.font) { 0 -> 2; 1 -> 1; else -> 0 }
    val stacked = p.logoLayout == 1 || p.logoStyle == 2
    val els = mutableListOf<El>()
    val mark = 160f
    val markX = if (stacked) 220f else 60f
    val markY = if (stacked) 110f else 220f
    if (p.logoStyle != 2) {
        els += El(
            type = if (p.logoShape == 1) ElType.CIRCLE else ElType.BOX, x = markX, y = markY, w = mark, h = mark,
            bg = if (p.logoShape == 2) CLEAR else pal.primary, radius = if (p.logoShape == 0) 48f else 0f,
        )
        val onMark = if (p.logoShape == 2) pal.primary else WHITE
        els += if (p.logoStyle == 0) El(type = ElType.ICON, x = markX + 36, y = markY + 36, w = 88f, h = 88f, fg = onMark, icon = p.logoIcon, iconStyle = p.iconStyle)
        else El(type = ElType.TEXT, x = markX, y = markY, w = mark, h = mark, text = p.name.trim().take(1), fg = onMark, fontSize = 80f, bold = true, align = 1, font = font)
    }
    val textX = if (stacked) 40f else markX + mark + 30f
    val textW = if (stacked) 520f else 600f - textX - 30f
    val textY = if (stacked) (if (p.logoStyle == 2) 220f else 300f) else 240f
    val align = if (stacked) 1 else if (lang == Lang.AR) 2 else 0
    els += El(
        type = ElType.TEXT, x = textX, y = textY, w = textW, h = 90f, text = p.name, fg = pal.primary,
        fontSize = 64f * p.logoScale, bold = true, align = align, font = font, letterSpacing = p.logoSpacing,
    )
    if (p.tagline.isNotBlank()) els += El(
        type = ElType.TEXT, x = textX, y = textY + 90f, w = textW, h = 40f, text = p.tagline, fg = pal.ink,
        fontSize = 24f, align = align, font = font, opacity = 0.7f,
    )
    return Design(name = (if (lang == Lang.AR) "شعار " else "Logo ") + p.name, kind = "LOGO", w = 600f, h = 600f, bg = pal.surface, els = els)
}
