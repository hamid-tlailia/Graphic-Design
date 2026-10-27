package com.eyn.designtrainer.builder

import com.eyn.designtrainer.data.AppProject
import com.eyn.designtrainer.data.Block
import com.eyn.designtrainer.data.BlockItem
import com.eyn.designtrainer.data.BlockKind
import com.eyn.designtrainer.data.Page
import com.eyn.designtrainer.i18n.L
import com.eyn.designtrainer.i18n.Lang

/** Complete starter apps built from the block library, ready to edit. */
object AppTemplates {
    class Tpl(val name: L, val desc: L, val build: (Lang) -> AppProject)

    /** Shortcuts for writing templates in both languages. */
    private class K(val p: AppProject, val lang: Lang) {
        fun s(ar: String, en: String) = if (lang == Lang.AR) ar else en
        fun b(kind: BlockKind, v: Int) = defaultBlock(kind, v, p, lang)
        fun c(v: Int) = CV.fresh(v, lang)
        fun i(ar: String, en: String, icon: String = "", value: String = "") = BlockItem(s(ar, en), icon, value)
        fun back(ar: String, en: String) = b(BlockKind.HEADER, 1).copy(title = s(ar, en), items = emptyList())
        fun tabs(active: Int, vararg items: BlockItem) = b(BlockKind.FOOTER, 0).copy(items = items.toList(), active = active)
        fun page(ar: String, en: String, height: Float = 900f, vararg blocks: Block) = Page(name = s(ar, en), blocks = blocks.toList(), height = height)
    }

    private fun make(name: String, palette: Int, icon: String, font: Int, pages: K.() -> List<Page>, lang: Lang): AppProject {
        val p = AppProject(name = name, palette = palette, logoIcon = icon, font = font)
        return p.copy(pages = K(p, lang).pages())
    }

    val all: List<Tpl> = listOf(
        Tpl(L("متجر إلكتروني", "Online store"), L("رئيسية ومنتج وسلة ودفع وحساب", "Home, product, cart, checkout, account")) { lang ->
            make(if (lang == Lang.AR) "سوقي" else "Souqi", 3, "shopping_bag", 1, {
                val tab = { a: Int -> tabs(a, i("الرئيسية", "Home", "home"), i("الأقسام", "Shop", "grid_view"), i("السلة", "Cart", "shopping_bag"), i("حسابي", "Account", "person")) }
                listOf(
                    page("الرئيسية", "Home", 1000f,
                        b(BlockKind.HEADER, 0).copy(items = listOf(i("", "", "favorite_border"), i("", "", "notifications"))),
                        b(BlockKind.SEARCH, 1).copy(title = s("ابحث عن منتج…", "Search products…")),
                        b(BlockKind.HERO, 2).copy(title = s("خصم 30٪ على الجديد", "30% off new arrivals"), subtitle = s("لفترة محدودة", "For a limited time"), button = s("تسوّق الآن", "Shop now")),
                        b(BlockKind.CATEGORIES, 1).copy(title = "", items = listOf(i("الكل", "All", "apps"), i("ملابس", "Clothes", "checkroom"), i("أحذية", "Shoes", "ice_skating"), i("إلكترونيات", "Tech", "headphones"))),
                        c(CV.SECTION).copy(title = s("وصل حديثاً", "New arrivals")),
                        b(BlockKind.CARDS, 0).copy(title = "", items = listOf(BlockItem(s("حقيبة جلدية", "Leather bag"), value = "249"), BlockItem(s("ساعة كلاسيك", "Classic watch"), value = "399"), BlockItem(s("حذاء رياضي", "Sneakers"), value = "189"), BlockItem(s("نظارة شمسية", "Sunglasses"), value = "129"))),
                        tab(0),
                    ),
                    page("المنتج", "Product", 1100f,
                        back("تفاصيل المنتج", "Product details"),
                        c(CV.PRODUCT).copy(title = s("حقيبة جلدية", "Leather bag"), price = "249", body = s("جلد طبيعي بخياطة يدوية وجيوب داخلية واسعة.", "Genuine leather, hand-stitched, with roomy inner pockets.")),
                        c(CV.REVIEWS),
                        c(CV.ADD_BAR).copy(price = "249"),
                    ),
                    page("السلة", "Cart", 1100f, back("السلة", "Cart"), c(CV.CART), c(CV.PROMO), c(CV.SUMMARY).copy(button = s("إتمام الشراء", "Checkout")), tab(2)),
                    page("الدفع", "Checkout", 1000f,
                        back("الدفع", "Checkout"), c(CV.STEPS).copy(active = 2), c(CV.PAY_METHODS), c(CV.PAY_LOGOS),
                        b(BlockKind.BUTTON, 0).copy(button = s("ادفع الآن", "Pay now")),
                    ),
                    page("الدخول", "Sign in", 800f, b(BlockKind.FORM, 0), c(CV.SOCIAL)),
                    page("حسابي", "Account", 900f, back("حسابي", "Account"), c(CV.PROFILE), b(BlockKind.LIST, 0), tab(3)),
                )
            }, lang)
        },
        Tpl(L("تطبيق دردشة", "Chat app"), L("ترحيب ومحادثات ورسائل وإشعارات", "Welcome, chats, messages, notifications")) { lang ->
            make(if (lang == Lang.AR) "تواصل" else "Talk", 5, "chat", 0, {
                val tab = { a: Int -> tabs(a, i("المحادثات", "Chats", "chat"), i("المكالمات", "Calls", "call"), i("المجموعات", "Groups", "groups"), i("الإعدادات", "Settings", "settings")) }
                listOf(
                    page("ترحيب", "Welcome", 900f,
                        c(CV.ONBOARD).copy(title = s("تحدّث مع من تحب", "Talk to the people you love"), body = s("رسائل ومكالمات سريعة وآمنة ومجانية.", "Fast, secure and free messages and calls."), button = s("ابدأ الآن", "Get started")),
                        c(CV.SOCIAL).copy(items = listOf("phone", "google", "apple").map { BlockItem(Shapes.socialLabel(it, lang == Lang.AR), it) }),
                    ),
                    page("المحادثات", "Chats", 900f,
                        b(BlockKind.HEADER, 3).copy(title = s("المحادثات", "Chats"), items = listOf(i("", "", "edit_square"))),
                        b(BlockKind.SEARCH, 0).copy(title = s("ابحث في المحادثات", "Search chats")),
                        c(CV.CHAT_LIST), tab(0),
                    ),
                    page("المحادثة", "Conversation", 900f,
                        b(BlockKind.HEADER, 2).copy(title = s("سارة", "Sara"), subtitle = s("متصلة الآن", "Online"), items = listOf(i("", "", "call"), i("", "", "videocam"))),
                        c(CV.DIVIDER).copy(title = s("اليوم", "Today")),
                        c(CV.CHAT),
                    ),
                    page("الإشعارات", "Notifications", 900f, back("الإشعارات", "Notifications"), c(CV.NOTIFS)),
                    page("الإعدادات", "Settings", 900f,
                        back("الإعدادات", "Settings"), c(CV.PROFILE).copy(title = s("أحمد علي", "Ahmed Ali"), subtitle = s("متاح", "Available")),
                        c(CV.TOGGLES).copy(title = s("الخصوصية", "Privacy")), tab(3),
                    ),
                )
            }, lang)
        },
        Tpl(L("نقل وتنافس أسعار", "Rides with price offers"), L("خريطة وطلب رحلة وعروض السائقين وتتبع", "Map, ride request, driver offers, tracking")) { lang ->
            make(if (lang == Lang.AR) "مشوار" else "Mishwar", 4, "local_taxi", 1, {
                val tab = { a: Int -> tabs(a, i("الرئيسية", "Home", "home"), i("رحلاتي", "Trips", "history"), i("المحفظة", "Wallet", "account_balance_wallet"), i("حسابي", "Account", "person")) }
                listOf(
                    page("الرئيسية", "Home", 900f,
                        b(BlockKind.HEADER, 0).copy(items = listOf(i("", "", "notifications"))),
                        c(CV.MAP).copy(itemHeight = 360f),
                        b(BlockKind.CATEGORIES, 1).copy(title = s("نوع الرحلة", "Ride type"), items = listOf(i("اقتصادي", "Economy", "directions_car"), i("مريح", "Comfort", "airline_seat_recline_extra"), i("عائلي", "Family", "airport_shuttle"), i("توصيل", "Delivery", "two_wheeler"))),
                        tab(0),
                    ),
                    page("طلب رحلة", "Request", 1000f,
                        back("اطلب رحلة", "Request a ride"),
                        c(CV.MAP).copy(itemHeight = 220f, hidden = setOf(CV.OPT_INPUT)),
                        c(CV.PROMO).copy(title = s("اقترح سعرك", "Offer your fare"), subtitle = s("مثال: 25 ر.س", "e.g. $25"), button = s("اطلب", "Request")),
                        c(CV.PAY_METHODS).copy(items = listOf(BlockItem(s("نقداً", "Cash"), "cash"), BlockItem(s("فيزا •••• 4242", "Visa •••• 4242"), "visa"))),
                    ),
                    page("العروض", "Offers", 1000f,
                        back("عروض السائقين", "Driver offers"),
                        c(CV.COUNTDOWN).copy(title = s("تنتهي العروض خلال", "Offers expire in"), items = listOf(i("دقيقة", "min", value = "01"), i("ثانية", "sec", value = "45"))),
                        c(CV.OFFERS).copy(title = ""),
                    ),
                    page("التتبع", "Tracking", 1000f,
                        c(CV.MAP).copy(itemHeight = 280f, hidden = setOf(CV.OPT_INPUT)),
                        c(CV.PROFILE).copy(title = s("خالد · تويوتا كامري", "Khaled · Toyota Camry"), subtitle = "ABC 1234 · ★ 4.9", button = s("اتصال", "Call")),
                        c(CV.TIMELINE).copy(title = s("حالة الرحلة", "Trip status"), active = 0, items = listOf(
                            i("السائق في الطريق", "Driver on the way", value = s("3 د", "3 min")), i("وصل السائق", "Driver arrived"), i("بدأت الرحلة", "Trip started"), i("وصلت", "Arrived"),
                        )),
                    ),
                    page("الدخول", "Sign in", 800f,
                        c(CV.ONBOARD).copy(title = s("مشاويرك بسعرك", "Rides at your price"), body = s("اقترح السعر واختر أفضل عرض من السائقين.", "Name your fare and pick the best driver offer."), button = "", hidden = setOf(CV.OPT_DOTS)),
                        c(CV.SOCIAL).copy(items = listOf("phone", "google", "apple").map { BlockItem(Shapes.socialLabel(it, lang == Lang.AR), it) }),
                    ),
                )
            }, lang)
        },
        Tpl(L("مدونة / موقع علمي", "Blog / knowledge site"), L("مقالات وتصنيفات وصفحة مقال وحساب", "Articles, categories, article page, account")) { lang ->
            make(if (lang == Lang.AR) "معرفة" else "Maarifa", 6, "menu_book", 2, {
                val tab = { a: Int -> tabs(a, i("الرئيسية", "Home", "home"), i("استكشف", "Explore", "explore"), i("المحفوظة", "Saved", "bookmark"), i("حسابي", "Account", "person")) }
                listOf(
                    page("الرئيسية", "Home", 1100f,
                        b(BlockKind.HEADER, 0).copy(items = listOf(i("", "", "search"))),
                        b(BlockKind.CATEGORIES, 1).copy(title = "", items = listOf(i("الكل", "All"), i("تصميم", "Design"), i("برمجة", "Code"), i("علوم", "Science"), i("صحة", "Health"))),
                        c(CV.ARTICLES), tab(0),
                    ),
                    page("المقال", "Article", 1200f,
                        back("المقال", "Article"),
                        c(CV.BANNER).copy(title = s("كيف تختار ألوان علامتك التجارية", "How to pick your brand colors")),
                        b(BlockKind.TEXT, 0).copy(title = s("لماذا الألوان مهمة؟", "Why colors matter"), subtitle = s("الألوان أول ما يلاحظه الناس، وتحمل مشاعر ومعاني تختلف حسب الثقافة. ابدأ بلون أساسي واحد ثم أضف لوناً ثانوياً ولون تمييز.", "Color is the first thing people notice and carries feelings that vary by culture. Start with one main color, then add a secondary and an accent.")),
                        b(BlockKind.TEXT, 1).copy(title = s("نصيحة", "Tip"), subtitle = s("استخدم قاعدة 60-30-10 لتوزيع الألوان.", "Use the 60-30-10 rule to balance colors.")),
                        c(CV.REVIEWS).copy(title = s("التعليقات", "Comments")),
                    ),
                    page("استكشف", "Explore", 1000f,
                        b(BlockKind.HEADER, 3).copy(title = s("استكشف", "Explore"), items = emptyList()),
                        b(BlockKind.SEARCH, 0).copy(title = s("ابحث عن موضوع", "Search topics")),
                        c(CV.ICON_GRID).copy(title = s("التصنيفات", "Categories"), items = listOf(
                            i("تصميم", "Design", "palette"), i("برمجة", "Code", "code"), i("علوم", "Science", "science"), i("صحة", "Health", "favorite"),
                            i("أعمال", "Business", "work"), i("تقنية", "Tech", "devices"), i("لغات", "Languages", "translate"), i("فنون", "Arts", "brush"),
                        )),
                        c(CV.ARTICLES).copy(title = s("الأكثر قراءة", "Most read"), hidden = setOf(CV.OPT_FEATURED)), tab(1),
                    ),
                    page("النشرة", "Newsletter", 900f,
                        b(BlockKind.HERO, 2).copy(title = s("اشترك في نشرتنا", "Join our newsletter"), subtitle = s("مقال جديد كل أسبوع في بريدك", "A new article in your inbox every week"), button = s("اشترك", "Subscribe")),
                        b(BlockKind.FORM, 1),
                    ),
                    page("حسابي", "Account", 900f,
                        back("حسابي", "Account"), c(CV.PROFILE),
                        c(CV.STATS).copy(items = listOf(i("مقالات", "articles", value = "24"), i("متابعون", "followers", value = "1.2k"), i("إعجابات", "likes", value = "860"))),
                        b(BlockKind.LIST, 1), tab(3),
                    ),
                )
            }, lang)
        },
    )
}
