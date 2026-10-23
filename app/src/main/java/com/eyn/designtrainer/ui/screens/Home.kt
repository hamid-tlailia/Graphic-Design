package com.eyn.designtrainer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.FormatAlignLeft
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Print
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.SettingsEthernet
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.eyn.designtrainer.data.Cat
import com.eyn.designtrainer.data.Content
import com.eyn.designtrainer.i18n.tr
import com.eyn.designtrainer.ui.LocalData
import com.eyn.designtrainer.ui.LocalNav
import com.eyn.designtrainer.ui.LocalVm
import com.eyn.designtrainer.ui.Routes
import com.eyn.designtrainer.ui.goTab
import com.eyn.designtrainer.ui.components.EynCard
import com.eyn.designtrainer.ui.components.IconTile
import com.eyn.designtrainer.ui.components.Pill
import com.eyn.designtrainer.ui.components.ProgressBar
import com.eyn.designtrainer.ui.components.SectionTitle
import com.eyn.designtrainer.ui.components.bounceClick
import com.eyn.designtrainer.ui.components.dotPattern
import com.eyn.designtrainer.ui.theme.Eyn
import com.eyn.designtrainer.ui.theme.Type
import java.time.LocalDate
import java.time.LocalTime

data class CatStyle(val icon: ImageVector, val tint: Color, val bg: Color)

@Composable
fun catStyle(cat: Cat): CatStyle {
    val c = Eyn.c
    return when (cat) {
        Cat.SPACING -> CatStyle(Icons.Rounded.SettingsEthernet, c.primary, c.lilac)
        Cat.COLORS -> CatStyle(Icons.Rounded.Palette, c.accent, c.accentSoft)
        Cat.TYPE -> CatStyle(Icons.Rounded.TextFields, c.success, c.successSoft)
        Cat.ALIGN -> CatStyle(Icons.AutoMirrored.Rounded.FormatAlignLeft, c.primary, c.lilac)
        Cat.BASICS -> CatStyle(Icons.Rounded.Widgets, c.accent, c.accentSoft)
        Cat.IMAGERY -> CatStyle(Icons.Rounded.Image, c.success, c.successSoft)
        Cat.BRANDING -> CatStyle(Icons.Rounded.Verified, c.accent, c.accentSoft)
        Cat.UX -> CatStyle(Icons.Rounded.Smartphone, c.primary, c.lilac)
        Cat.PRINT -> CatStyle(Icons.Rounded.Print, c.primary, c.lilac)
    }
}

@Composable
fun HomeScreen() {
    val c = Eyn.c
    val d = LocalData.current
    val vm = LocalVm.current
    val nav = LocalNav.current
    val done = vm.dailyDoneToday(d)
    val daily = Content.daily(LocalDate.now().toEpochDay())
    val nextQ = daily.getOrNull(done) ?: daily.last()
    val morning = LocalTime.now().hour < 12

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(if (morning) tr("صباح الخير", "Good morning") else tr("مساء الخير", "Good evening"), style = Type.small, color = c.muted)
                Text(d.name.ifBlank { tr("مصمم المستقبل", "Future designer") }, style = Type.h1, color = c.text)
            }
            Pill(
                tr("${d.streak} يوماً", "${d.streak} days"),
                bg = c.accentSoft, fg = c.accent, icon = Icons.Rounded.LocalFireDepartment,
            )
        }
        Spacer(Modifier.height(20.dp))

        // Daily challenge
        Column(
            Modifier
                .fillMaxWidth()
                .bounceClick { nav.navigate(Routes.quiz("daily")) }
                .clip(RoundedCornerShape(24.dp))
                .background(c.hero)
                .dotPattern(Color.White.copy(alpha = 0.06f))
                .padding(20.dp),
        ) {
            Text(tr("تحدي اليوم", "Today's challenge"), style = Type.small, color = c.heroMuted)
            Spacer(Modifier.height(6.dp))
            Text(
                if (done >= 5) tr("أحسنت! أكملت تحدي اليوم", "Well done! Today's challenge is complete")
                else tr("أيهما أفضل؟ ", "Which is better? ") + nextQ.cat.label.t(),
                style = Type.h2, color = c.onHero,
            )
            Spacer(Modifier.height(14.dp))
            ProgressBar(done / 5f, color = c.accent, track = Color.White.copy(alpha = 0.18f))
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(tr("$done من 5 تحديات", "$done of 5 challenges"), style = Type.label, color = c.heroMuted, modifier = Modifier.weight(1f))
                Text(
                    if (done == 0) tr("ابدأ", "Start") else if (done >= 5) tr("مراجعة", "Review") else tr("أكمل", "Continue"),
                    style = Type.label, color = c.hero,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(Color.White).padding(horizontal = 18.dp, vertical = 8.dp),
                )
            }
        }

        val next = Content.lessons.firstOrNull { it.id !in d.completed && com.eyn.designtrainer.data.Path.isUnlocked(it.id, d.completed) }
        if (next != null) {
            Spacer(Modifier.height(14.dp))
            val doneAll = Content.lessons.count { it.id in d.completed }
            EynCard(Modifier.fillMaxWidth(), onClick = { nav.navigate(Routes.lesson(next.id)) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val st = catStyle(next.cat)
                    IconTile(st.icon, st.bg, st.tint)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(tr("تابع المسار · ${doneAll}/${Content.lessons.size}", "Continue the path · ${doneAll}/${Content.lessons.size}"), style = Type.small, color = c.muted)
                        Text(next.title.t(), style = Type.h3, color = c.text)
                    }
                    Pill(tr("ابدأ", "Start"), c.lilac, c.onLilac)
                }
                Spacer(Modifier.height(10.dp))
                ProgressBar(doneAll / Content.lessons.size.toFloat(), Modifier.fillMaxWidth(), height = 4.dp)
            }
        }
        Spacer(Modifier.height(24.dp))
        SectionTitle(tr("الأقسام", "Sections"), tr("عرض الكل", "View all")) { nav.goTab(Routes.LESSONS) }
        val cats = listOf(Cat.SPACING, Cat.COLORS, Cat.TYPE, Cat.ALIGN)
        cats.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { cat ->
                    val s = catStyle(cat)
                    EynCard(Modifier.weight(1f), onClick = { nav.navigate(Routes.quiz("cat:${cat.name}")) }) {
                        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            IconTile(s.icon, s.bg, s.tint, size = 52.dp, radius = 16.dp, iconSize = 26.dp)
                            Spacer(Modifier.height(14.dp))
                            Text(cat.label.t(), style = Type.h3, color = c.text)
                            Text(tr("${Content.byCat(cat).size} تحديات", "${Content.byCat(cat).size} challenges"), style = Type.small, color = c.muted)
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.height(12.dp))
        SectionTitle(tr("وضع البناء", "Build mode"))
        EynCard(onClick = { nav.goTab(Routes.STUDIO) }, color = c.lilac, border = null, padding = PaddingValues(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(tr("صمّم تطبيقك بالقطع", "Design your app with blocks"), style = Type.h3, color = c.onLilac)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        tr("هيدر، فوتر، صفحة هبوط، تسجيل دخول، بطاقات وأزرار جاهزة للتعديل", "Headers, footers, landing pages, login, cards and buttons ready to customize"),
                        style = Type.small, color = c.onLilac.copy(alpha = 0.75f),
                    )
                }
                Spacer(Modifier.width(12.dp))
                Box(Modifier.size(52.dp).clip(RoundedCornerShape(16.dp)).background(c.surface), contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Dashboard, null, tint = c.accent, modifier = Modifier.size(26.dp))
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
