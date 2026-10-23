package com.eyn.designtrainer

import androidx.test.core.app.ApplicationProvider
import com.eyn.designtrainer.builder.IconFont
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class IconSearchTest {
    private val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()

    @Test fun arabicAndEnglishQueriesFindIcons() {
        for (q in listOf("عيادة", "العيادة", "قهوة", "سلة", "توصيل", "مطعم", "حجز", "clinic", "cart", "star", "home")) {
            for (style in 0..4) {
                val n = IconFont.search(ctx, q, style).size
                println("ICONSEARCH '$q' style=$style -> $n")
                assertTrue("'$q' style $style found nothing", n > 0)
            }
        }
    }
}
