package com.eyn.designtrainer

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.CompositionLocalProvider
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import android.graphics.Canvas
import androidx.test.core.app.ApplicationProvider
import com.eyn.designtrainer.builder.Templates
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
            completed = setOf("grid8"), progress = mapOf("hierarchy" to 60),
            dailyDay = today, dailyDone = 3,
            designs = listOf(Templates.first { it.id == "login_classic" }.toDesign(lang).copy(id = "d1")),
            projects = listOf(withPages),
        )
    }

    private fun shot(name: String, route: String, lang: Lang = Lang.AR, dark: Boolean = false, d: AppData = data(lang)) {
        val vm = AppViewModel(ApplicationProvider.getApplicationContext<Application>())
        rule.setContent {
            CompositionLocalProvider(LocalData provides d, LocalVm provides vm) {
                EynTheme(dark, lang) { AppNav(dark, startOverride = route) }
            }
        }
        rule.mainClock.autoAdvance = false
        repeat(25) { rule.mainClock.advanceTimeBy(100) }
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
    @Test fun s23_build_dark() = shot("23_build_dark", Routes.build("p1"), dark = true)
}
