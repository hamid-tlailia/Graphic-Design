package com.eyn.designtrainer

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.CompositionLocalProvider
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
                Page(id = "pg2", name = if (lang == Lang.AR) "القائمة" else "Menu"),
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
    @Test fun s23_build_dark() = shot("23_build_dark", Routes.build("p1"), dark = true)
}
