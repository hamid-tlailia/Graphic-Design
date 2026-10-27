package com.eyn.designtrainer

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import android.graphics.Canvas
import androidx.test.core.app.ApplicationProvider
import com.eyn.designtrainer.builder.EditorState
import com.eyn.designtrainer.builder.Templates
import com.eyn.designtrainer.builder.blankDesign
import com.eyn.designtrainer.data.Design
import com.eyn.designtrainer.data.ElType
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.onNodeWithContentDescription
import com.eyn.designtrainer.builder.starterPage
import com.eyn.designtrainer.builder.toDesign
import com.eyn.designtrainer.builder.itemsOr
import com.eyn.designtrainer.data.AppData
import com.eyn.designtrainer.data.AppProject
import com.eyn.designtrainer.data.AppViewModel
import com.eyn.designtrainer.data.Page
import com.eyn.designtrainer.i18n.Lang
import com.eyn.designtrainer.ui.AppNav
import com.eyn.designtrainer.ui.LocalData
import com.eyn.designtrainer.ui.LocalVm
import com.eyn.designtrainer.ui.Routes
import com.eyn.designtrainer.ui.theme.EynTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.time.LocalDate

/** Renders each screen headlessly and writes PNGs to build/screens for visual review. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w400dp-h860dp-xxhdpi")
class ScreenshotTest {
    @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()

    private fun data(lang: Lang): AppData {
        val today = LocalDate.now().toEpochDay()
        val proj = AppProject(id = "p1", name = if (lang == Lang.AR) "قهوتي" else "My Cafe")
        val withPages = proj.copy(
            pages = listOf(
                starterPage(proj, lang, if (lang == Lang.AR) "الرئيسية" else "Home").copy(id = "pg1"),
                Page(
                    id = "pg2", name = if (lang == Lang.AR) "القائمة" else "Menu",
                    blocks = listOf(
                        com.eyn.designtrainer.builder.defaultBlock(com.eyn.designtrainer.data.BlockKind.HEADER, 3, proj, lang)
                            .copy(items = listOf(com.eyn.designtrainer.data.BlockItem("", "search"), com.eyn.designtrainer.data.BlockItem("", "shopping_cart"))),
                        com.eyn.designtrainer.builder.defaultBlock(com.eyn.designtrainer.data.BlockKind.CUSTOM, 0, proj, lang),
                        com.eyn.designtrainer.builder.defaultBlock(com.eyn.designtrainer.data.BlockKind.CUSTOM, 1, proj, lang),
                        com.eyn.designtrainer.builder.defaultBlock(com.eyn.designtrainer.data.BlockKind.CUSTOM, 2, proj, lang),
                    ),
                ),
            ),
        )
        return AppData(
            lang = if (lang == Lang.AR) "ar" else "en", onboarded = true, name = if (lang == Lang.AR) "حميد" else "Hamid",
            points = 340, answered = 40, correct = 34, streak = 12, lastDay = today,
            activity = (0..6).associate { today - it to listOf(2, 5, 1, 4, 3, 6, 0)[it] },
            completed = setOf("grid8"), progress = mapOf("hierarchy" to 60), bookmarks = setOf("grid8", "colorwheel"),
            dailyDay = today, dailyDone = 3,
            designs = listOf(Templates.first { it.id == "login_classic" }.toDesign(lang).copy(id = "d1"), allTypes(lang)),
            projects = listOf(withPages),
        )
    }

    /** A canvas holding one of every element type, laid out in a grid. */
    private fun allTypes(lang: Lang): Design {
        val st = EditorState(blankDesign(lang).copy(id = "d2", h = 700f))
        ElType.entries.forEachIndexed { i, t ->
            st.add(t, lang)
            val id = st.selectedId!!
            val col = i % 2; val row = i / 2
            st.updateEl(id) { it.copy(x = 16f + col * 172f, y = 16f + row * 76f, w = minOf(it.w, 156f), h = if (t == ElType.CIRCLE || t == ElType.AVATAR) 56f else minOf(it.h, 60f)) }
        }
        return st.design
    }

    private fun shot(name: String, route: String, lang: Lang = Lang.AR, dark: Boolean = false, d: AppData = data(lang), actions: () -> Unit = {}) {
        val vm = AppViewModel(ApplicationProvider.getApplicationContext<Application>())
        // Wait for the ViewModel's initial load, then seed it so edits made in the UI are visible.
        repeat(200) {
            if (vm.data.value == null) { org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle(); Thread.sleep(10) }
        }
        vm.update { d }
        rule.setContent {
            val live by vm.data.collectAsState()
            CompositionLocalProvider(LocalData provides (live ?: d), LocalVm provides vm) {
                EynTheme(dark, lang) { AppNav(dark, startOverride = route) }
            }
        }
        rule.mainClock.autoAdvance = false
        repeat(25) { rule.mainClock.advanceTimeBy(100) }
        actions()
        repeat(15) { rule.mainClock.advanceTimeBy(100) }
        val view = rule.activity.window.decorView
        val bmp = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        view.draw(Canvas(bmp))
        val dir = File("build/screens").apply { mkdirs() }
        File(dir, "$name.png").outputStream().use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    @Test fun s01_onboarding() = shot("01_onboarding", Routes.ONBOARDING)
    @Test fun s02_home() = shot("02_home", Routes.HOME)
    @Test fun s03_quiz() = shot("03_quiz", Routes.quiz("cat:SPACING"))
    @Test fun s05_lessons() = shot("05_lessons", Routes.LESSONS)
    @Test fun s06_lesson() = shot("06_lesson", Routes.lesson("grid8"))
    @Test fun s07_spacing() = shot("07_spacing", Routes.SPACING)
    @Test fun s08_contrast() = shot("08_contrast", Routes.CONTRAST)
    @Test fun s09_fonts() = shot("09_fonts", Routes.FONTS)
    @Test fun s10_profile() = shot("10_profile", Routes.PROFILE)
    @Test fun s11_identity() = shot("11_identity", Routes.identity("p1"))
    @Test fun s12_library() = shot("12_library", Routes.library("p1", "pg1"))
    @Test fun s13_build() = shot("13_build", Routes.build("p1"))
    @Test fun s15_export() = shot("15_export", Routes.export("p1"))
    @Test fun s16_studio() = shot("16_studio", Routes.STUDIO)
    @Test fun s17_canvas() = shot("17_canvas", Routes.canvas("d1"))
    @Test fun s18_practice() = shot("18_practice", Routes.PRACTICE)
    @Test fun s19_settings() = shot("19_settings", Routes.SETTINGS)
    @Test fun s20_home_dark() = shot("20_home_dark", Routes.HOME, dark = true)
    @Test fun s21_home_en() = shot("21_home_en", Routes.HOME, lang = Lang.EN)
    @Test fun s22_canvas_en_dark() = shot("22_canvas_en_dark", Routes.canvas("d1"), lang = Lang.EN, dark = true)
    /** With the clock paused, force a layout pass so semantics reflect the latest composition. */
    private fun settle() {
        repeat(6) { rule.mainClock.advanceTimeBy(100) }
        rule.runOnUiThread {
            val v = rule.activity.window.decorView
            v.measure(android.view.View.MeasureSpec.makeMeasureSpec(v.width, android.view.View.MeasureSpec.EXACTLY), android.view.View.MeasureSpec.makeMeasureSpec(v.height, android.view.View.MeasureSpec.EXACTLY))
            v.layout(0, 0, v.width, v.height)
        }
        repeat(2) { rule.mainClock.advanceTimeBy(100) }
    }

    private fun click(text: String) {
        rule.onAllNodesWithText(text, substring = true)[0].performClick()
        settle()
    }

    @Test fun s24_canvas_all_types() = shot("24_canvas_all_types", Routes.canvas("d2"))
    @Test fun s25_canvas_layout() = shot("25_canvas_layout", Routes.canvas("d1")) { click("مرحباً بعودتك"); click("الحجم والموضع") }
    @Test fun s26_canvas_style() = shot("26_canvas_style", Routes.canvas("d2")) { click("بطاقة"); click("الألوان") }
    @Test fun s27_canvas_content() = shot("27_canvas_content", Routes.canvas("d1")) { click("مرحباً بعودتك"); click("المحتوى") }
    @Test fun s28_canvas_value() = shot("28_canvas_value", Routes.canvas("d2")) { click("تذكرني"); click("المحتوى") }
    @Test fun s29_path() = shot("29_path", Routes.LESSONS)
    @Test fun s30_drill() = shot("30_drill", Routes.SPACING) { click("تحقّق من التصميم") }
    @Test fun s31_review() = shot("31_review", Routes.canvas("d1")) {
        rule.onNodeWithContentDescription("تقييم التصميم").performClick(); repeat(10) { rule.mainClock.advanceTimeBy(100) }
    }
    @Test fun s32_export() = shot("32_export", Routes.canvas("d1")) {
        rule.onNodeWithContentDescription("تصدير").performClick(); repeat(10) { rule.mainClock.advanceTimeBy(100) }
    }
    @Test fun s33_build_moved() = shot("33_build_moved", Routes.build("p1")) {
        click("عرض خاص")
        rule.onAllNodesWithText("أسفل", useUnmergedTree = true)[0].performClick()
        settle()
    }
    @Test fun s34_identity_custom() = shot("34_identity_custom", Routes.identity("p1")) { click("ألواني") }
    @Test fun s35_studio_logos() = shot("35_studio_logos", Routes.STUDIO) { click("الشعارات") }
    @Test fun s36_canvas_size() = shot("36_canvas_size", Routes.canvas("d2")) { click("الألوان"); click("جوال 360") }
    @Test fun s37_identity_icons() = shot("37_identity_icons", Routes.identity("p1")) { click("محدد") }
    @Test fun s38_saved() = shot("38_saved", Routes.LESSONS) { click("كل الدروس"); click("المحفوظة") }
    @Test fun s41_launcher_icon() {
        val ctx = ApplicationProvider.getApplicationContext<Application>()
        val bmp = Bitmap.createBitmap(432, 432, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(0xFF2A2470.toInt())
        ctx.getDrawable(R.drawable.ic_launcher_foreground)!!.apply { setBounds(0, 0, 432, 432); draw(canvas) }
        File(File("build/screens").apply { mkdirs() }, "41_launcher_icon.png").outputStream().use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }
    @Test fun s40_code() {
        val dir = File("build/screens").apply { mkdirs() }
        for (lang in Lang.entries) {
            val p = data(lang).projects.first()
            val tag = lang.name.lowercase()
            File(dir, "export_$tag.html").writeText(com.eyn.designtrainer.builder.CodeGen.html(p, lang))
            File(dir, "App_$tag.js").writeText(com.eyn.designtrainer.builder.CodeGen.reactNative(p, lang))
        }
    }
    /** A shop project using every new custom block. */
    private fun shop(lang: Lang): AppProject {
        val p = AppProject(id = "p2", name = if (lang == Lang.AR) "قهوتي" else "My Cafe")
        fun c(v: Int) = com.eyn.designtrainer.builder.defaultBlock(com.eyn.designtrainer.data.BlockKind.CUSTOM, v, p, lang)
        val header = com.eyn.designtrainer.builder.defaultBlock(com.eyn.designtrainer.data.BlockKind.HEADER, 1, p, lang)
        val cv = com.eyn.designtrainer.builder.CV
        return p.copy(pages = listOf(
            Page(id = "s1", name = if (lang == Lang.AR) "السلة" else "Cart", height = 1100f, blocks = listOf(
                header.copy(title = if (lang == Lang.AR) "السلة" else "Cart"), c(cv.STEPS), c(cv.CART), c(cv.PROMO), c(cv.SUMMARY), c(cv.PAY_METHODS), c(cv.PAY_LOGOS),
            )),
            Page(id = "s2", name = if (lang == Lang.AR) "المنتج" else "Product", height = 1000f, blocks = listOf(
                c(cv.PRODUCT), c(cv.CHECKS), c(cv.SECTION), c(cv.REVIEWS), c(cv.ADD_BAR),
            )),
            Page(id = "s3", name = if (lang == Lang.AR) "عناصر" else "Extras", height = 1200f, blocks = listOf(
                c(cv.PROFILE), c(cv.STATS), c(cv.ALERT), c(cv.BANNER), c(cv.ICON_GRID), c(cv.DIVIDER), c(cv.TOGGLES),
            )),
            Page(id = "s4", name = if (lang == Lang.AR) "تواصل" else "Contact", height = 1300f, blocks = listOf(
                c(cv.FREE).copy(designId = "free1"), c(cv.NOTIFS), c(cv.CHAT), c(cv.FAQ),
            )),
            Page(id = "s5", name = if (lang == Lang.AR) "الطلب" else "Order", height = 1300f, blocks = listOf(
                c(cv.TIMELINE), c(cv.CALENDAR), c(cv.COUNTDOWN), c(cv.PRICING),
            )),
            Page(id = "s6", name = if (lang == Lang.AR) "ترحيب" else "Welcome", height = 1100f, blocks = listOf(
                c(cv.ONBOARD), c(cv.EMPTY),
            )),
        ))
    }

    /** A game-style header drawn freely, used by the free block in [shop]. */
    private fun freeDesign(): Design {
        val st = EditorState(blankDesign(Lang.AR).copy(id = "free1", w = 360f, h = 150f, bg = 0xFF2A2470, kind = com.eyn.designtrainer.builder.BLOCK_DESIGN))
        fun add(t: ElType, f: (com.eyn.designtrainer.data.El) -> com.eyn.designtrainer.data.El, variant: Int = 0) { st.add(t, Lang.AR, variant = variant); st.updateEl(st.selectedId!!, null, f) }
        add(ElType.AVATAR, { it.copy(x = 286f, y = 20f, w = 56f, h = 56f, bg = 0xFFF2603F, fg = 0xFFFFFFFF) })
        add(ElType.TEXT, { it.copy(x = 120f, y = 24f, w = 156f, h = 26f, text = "المستوى 5", fg = 0xFFFFFFFF, fontSize = 20f, align = 2) })
        add(ElType.PROGRESS, { it.copy(x = 120f, y = 60f, w = 156f, h = 10f, value = 0.7f, bg = 0x33FFFFFF, fg = 0xFFFFC845, radius = 5f) })
        add(ElType.SHAPE, { it.copy(x = 18f, y = 20f, w = 90f, h = 40f, text = "★ 1250", fontSize = 15f, bold = true, bg = 0xFFFFC845, fg = 0xFF2A2470) }, variant = 25)
        listOf(0, 1, 2).forEach { i -> add(ElType.ICON, { it.copy(x = 20f + i * 30f, y = 96f, w = 26f, h = 26f, icon = "favorite", fg = 0xFFF2603F) }) }
        add(ElType.BUTTON, { it.copy(x = 200f, y = 94f, w = 142f, h = 40f, text = "العب الآن", bg = 0xFFF2603F, radius = 20f) })
        return st.design
    }

    /** Renders [content] standalone (no navigation) and saves it. */
    private fun still(name: String, lang: Lang = Lang.AR, content: @androidx.compose.runtime.Composable () -> Unit) {
        rule.setContent { EynTheme(false, lang) { content() } }
        rule.mainClock.autoAdvance = false
        repeat(10) { rule.mainClock.advanceTimeBy(100) }
        val view = rule.activity.window.decorView
        val bmp = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        view.draw(Canvas(bmp))
        File(File("build/screens").apply { mkdirs() }, "$name.png").outputStream().use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    @Test fun s42_shop_pages() = shopStill("42_shop_pages", 0..2)
    @Test fun s47_shop_pages_more() = shopStill("47_shop_pages_more", 3..5)

    private fun shopStill(name: String, range: IntRange) {
        val p = shop(Lang.AR).let { it.copy(pages = it.pages.slice(range)) }
        val d = data(Lang.AR).copy(designs = listOf(freeDesign()))
        still(name) { CompositionLocalProvider(LocalData provides d) { shopRow(p) } }
    }

    @androidx.compose.runtime.Composable
    private fun shopRow(p: AppProject) {
        androidx.compose.foundation.layout.Row(
            androidx.compose.ui.Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Color(0xFFE9E7F2)),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp),
        ) {
            p.pages.forEach { page ->
                androidx.compose.foundation.layout.Box(androidx.compose.ui.Modifier.width(128.dp).background(androidx.compose.ui.graphics.Color.White)) {
                    com.eyn.designtrainer.builder.Scaled(128f / 360f) { com.eyn.designtrainer.builder.PageView(page, p) }
                }
            }
        }
    }

    @Test fun s48_board() {
        val p = shop(Lang.AR)
        val d = data(Lang.AR).copy(designs = listOf(freeDesign()))
        still("48_board") {
            CompositionLocalProvider(LocalData provides d) {
                com.eyn.designtrainer.builder.Scaled(400f / 1680f, designWidth = 1680.dp) { com.eyn.designtrainer.builder.PresentationBoard(p) }
            }
        }
    }

    @Test fun s50_template_shop() = templateBoard(0)
    @Test fun s50_template_chat() = templateBoard(1)
    @Test fun s50_template_ride() = templateBoard(2)
    @Test fun s50_template_blog() = templateBoard(3)

    private fun templateBoard(i: Int) {
        val p = com.eyn.designtrainer.builder.AppTemplates.all[i].build(Lang.AR)
        still("50_template_$i") {
            com.eyn.designtrainer.builder.Scaled(400f / 1680f, designWidth = 1680.dp) { com.eyn.designtrainer.builder.PresentationBoard(p) }
        }
    }

    @Test fun distributeGivesEqualGaps() {
        val st = EditorState(blankDesign(Lang.AR).copy(w = 360f, h = 200f))
        st.add(ElType.BOX, Lang.AR); val bar = st.selectedId!!
        st.updateEl(bar) { it.copy(x = 0f, y = 100f, w = 360f, h = 60f) }
        listOf(10f, 60f, 300f).forEach { x -> st.add(ElType.CIRCLE, Lang.AR); st.updateEl(st.selectedId!!) { it.copy(x = x, y = 110f, w = 40f, h = 40f) } }
        st.distribute(st.design.els.first { it.id == bar }, true)
        val xs = st.design.els.filter { it.type == ElType.CIRCLE }.map { it.x }.sorted()
        // (360 - 3 × 40) / 4 = 60 → 60, 160, 260
        org.junit.Assert.assertEquals(listOf(60f, 160f, 260f), xs)
    }

    @Test fun distributeKeepsStackedButtonsTogether() {
        val st = EditorState(blankDesign(Lang.AR).copy(w = 360f, h = 163f))
        st.add(ElType.BOX, Lang.AR); val bar = st.selectedId!!
        st.updateEl(bar) { it.copy(x = 5f, y = 110f, w = 350f, h = 51f) }
        // Four tab buttons at uneven spots, each sticking out above the bar.
        listOf(18f, 100f, 190f, 290f).forEachIndexed { i, x ->
            st.add(ElType.CIRCLE, Lang.AR); st.updateEl(st.selectedId!!) { it.copy(x = x, y = 85f, w = 56f, h = 56f, text = "w$i") }
            st.add(ElType.CIRCLE, Lang.AR); st.updateEl(st.selectedId!!) { it.copy(x = x + 6f, y = 91f, w = 44f, h = 44f, text = "d$i") }
            st.add(ElType.ICON, Lang.AR); st.updateEl(st.selectedId!!) { it.copy(x = x + 16f, y = 101f, w = 24f, h = 24f, text = "i$i") }
            st.add(ElType.TEXT, Lang.AR); st.updateEl(st.selectedId!!) { it.copy(x = x - 2f, y = 140f, w = 60f, h = 18f, text = "t$i") }
        }
        org.junit.Assert.assertEquals(4, st.unitsIn(st.design.els.first { it.id == bar }, true).size)
        st.distribute(st.design.els.first { it.id == bar }, true)
        val els = st.design.els
        fun x(t: String) = els.first { it.text == t }.x
        // Units are 60 wide (label sticks out 2 on each side): gap = (350 - 240) / 5 = 22.
        val starts = (0..3).map { x("t$it") }
        org.junit.Assert.assertEquals(listOf(27f, 109f, 191f, 273f), starts)
        (0..3).forEach { i ->
            org.junit.Assert.assertEquals(x("t$i") + 2f, x("w$i"))
            org.junit.Assert.assertEquals(x("w$i") + 6f, x("d$i"))
            org.junit.Assert.assertEquals(x("w$i") + 16f, x("i$i"))
        }
    }

    @Test fun s51_social_canvas() {
        val st = EditorState(blankDesign(Lang.AR).copy(w = 360f, h = 420f))
        com.eyn.designtrainer.builder.Shapes.socials.forEachIndexed { i, (k, _) ->
            st.add(ElType.PAYMENT, Lang.AR, kind = com.eyn.designtrainer.builder.SOCIAL + k)
            st.updateEl(st.selectedId!!) { it.copy(x = 40f, y = 20f + i * 56f) }
        }
        still("51_social_canvas") { com.eyn.designtrainer.builder.DesignCanvas(st.design, 1.1f) }
    }

    @Test fun s43_canvas_shapes() {
        val st = EditorState(blankDesign(Lang.AR).copy(id = "d3", w = 360f, h = 780f))
        val colors = listOf(0xFFB5A33A, 0xFF5A4E44, 0xFFE09A1E, 0xFFB21FA0, 0xFF1D2D6B, 0xFF1EA5DE, 0xFFC0141F, 0xFFB3174F)
        com.eyn.designtrainer.builder.Shapes.all.indices.forEach { i ->
            st.add(ElType.SHAPE, Lang.AR, variant = i)
            // The label shapes added last get the stitched inner line from the reference sheet.
            val label = i >= 40
            st.updateEl(st.selectedId!!) {
                it.copy(x = 12f + (i % 7) * 49f, y = 12f + (i / 7) * 49f, w = 44f, h = if (label) 32f else 44f,
                    bg = if (label) colors[i % colors.size] else it.bg, inner = if (label) 1 else 0, borderW = 0f)
            }
        }
        com.eyn.designtrainer.builder.Shapes.frames.indices.forEach { i ->
            st.add(ElType.FRAME, Lang.AR, variant = i)
            st.updateEl(st.selectedId!!) { it.copy(x = 20f + (i % 6) * 54f, y = 420f + (i / 6) * 70f, w = 48f, h = 62f, borderW = 2f) }
        }
        com.eyn.designtrainer.builder.Shapes.payments.forEachIndexed { i, (k, _) ->
            st.add(ElType.PAYMENT, Lang.AR, kind = k)
            st.updateEl(st.selectedId!!) { it.copy(x = 20f + (i % 6) * 54f, y = 640f + (i / 6) * 40f, w = 50f, h = 32f) }
        }
        st.add(ElType.SHAPE, Lang.AR, variant = 44)
        st.updateEl(st.selectedId!!) { it.copy(x = 50f, y = 730f, w = 260f, h = 44f, text = "عنوان على لافتة", bg = 0xFFC0141F, inner = 1) }
        still("43_canvas_shapes") { com.eyn.designtrainer.builder.DesignCanvas(st.design, 1.1f) }
    }

    @Test fun s46_text_emoji() {
        val st = EditorState(blankDesign(Lang.AR).copy(id = "d4", w = 360f, h = 200f))
        st.add(ElType.TEXT, Lang.AR)
        st.updateEl(st.selectedId!!) { it.copy(x = 20f, y = 40f, w = 320f, h = 50f, text = "حبيبتي🌹🌹🌹❤️", fontSize = 28f) }
        st.add(ElType.TEXT, Lang.AR)
        st.updateEl(st.selectedId!!) { it.copy(x = 20f, y = 110f, w = 320f, h = 50f, text = "Hello 🌹 مرحبا", fontSize = 24f, align = 0) }
        still("46_text_emoji") { com.eyn.designtrainer.builder.DesignCanvas(st.design, 1.1f) }
    }

    @Test fun deletedHeaderIconsStayDeleted() {
        val p = AppProject(name = "x")
        val b = com.eyn.designtrainer.builder.defaultBlock(com.eyn.designtrainer.data.BlockKind.HEADER, 3, p, Lang.AR)
        org.junit.Assert.assertEquals(1, b.itemsOr(Lang.AR).size)
        val cleared = b.copy(items = emptyList(), hidden = b.hidden + com.eyn.designtrainer.builder.NO_ITEMS)
        org.junit.Assert.assertTrue(cleared.itemsOr(Lang.AR).isEmpty())
    }

    @Test fun logoIconCanBeChangedAgain() {
        val vm = AppViewModel(ApplicationProvider.getApplicationContext<Application>())
        repeat(200) { if (vm.data.value == null) { org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle(); Thread.sleep(10) } }
        val d = data(Lang.AR).let { it.copy(projects = it.projects.map { p -> p.copy(logoIcon = "menu") }) }
        vm.update { d }
        rule.setContent {
            val live by vm.data.collectAsState()
            CompositionLocalProvider(LocalData provides (live ?: d), LocalVm provides vm) { EynTheme(false, Lang.AR) { AppNav(false, startOverride = Routes.identity("p1")) } }
        }
        rule.mainClock.autoAdvance = false
        repeat(20) { rule.mainClock.advanceTimeBy(100) }
        val ctx = ApplicationProvider.getApplicationContext<Application>()
        val star = com.eyn.designtrainer.builder.IconFont.glyph(ctx, "star", 0)!!
        rule.onAllNodesWithText(star)[0].performClick()
        settle()
        println("LOGO after quick pick: " + vm.data.value!!.projects.first().logoIcon)
        org.junit.Assert.assertEquals("star", vm.data.value!!.projects.first().logoIcon)
    }

    @Test fun s49_library_saved() {
        val base = data(Lang.AR)
        val p = base.projects.first()
        val header = com.eyn.designtrainer.builder.defaultBlock(com.eyn.designtrainer.data.BlockKind.HEADER, 3, p, Lang.AR)
        val saved = listOf(
            com.eyn.designtrainer.builder.savedFromDesign(freeDesign(), "شريط اللعبة", "ألعاب"),
            com.eyn.designtrainer.builder.savedFrom(header, base, "هيدر البحث", "HEADER"),
            com.eyn.designtrainer.builder.savedFrom(com.eyn.designtrainer.builder.defaultBlock(com.eyn.designtrainer.data.BlockKind.CUSTOM, 22, p, Lang.AR), base, "إشعاراتي", "ألعاب"),
        )
        shot("49_library_saved", Routes.library("p1", "pg1"), d = base.copy(savedBlocks = saved))
    }

    @Test fun s45_shop_code() {
        val dir = File("build/screens").apply { mkdirs() }
        val free = freeDesign()
        File(dir, "shop_ar.html").writeText(com.eyn.designtrainer.builder.CodeGen.html(shop(Lang.AR), Lang.AR, designs = { if (it == free.id) free else null }))
        File(dir, "shop_en.html").writeText(com.eyn.designtrainer.builder.CodeGen.html(shop(Lang.EN), Lang.EN))
        File(dir, "Shop_ar.js").writeText(com.eyn.designtrainer.builder.CodeGen.reactNative(shop(Lang.AR), Lang.AR))
    }

    @Test fun s39_custom_blocks() = shot("39_custom_blocks", Routes.build("p1")) { click("القائمة") }
    @Test fun s23_build_dark() = shot("23_build_dark", Routes.build("p1"), dark = true)
}
