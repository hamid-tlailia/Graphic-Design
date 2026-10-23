package com.eyn.designtrainer.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.MilitaryTech
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SettingsEthernet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.eyn.designtrainer.data.Cat
import com.eyn.designtrainer.data.Content
import com.eyn.designtrainer.i18n.tr
import com.eyn.designtrainer.ui.LocalData
import com.eyn.designtrainer.ui.LocalNav
import com.eyn.designtrainer.ui.LocalVm
import com.eyn.designtrainer.ui.Routes
import com.eyn.designtrainer.ui.components.EynCard
import com.eyn.designtrainer.ui.components.ProgressBar
import com.eyn.designtrainer.ui.components.Segmented
import com.eyn.designtrainer.ui.components.SquareIconButton
import com.eyn.designtrainer.ui.components.StatCard
import com.eyn.designtrainer.ui.components.TopBar
import com.eyn.designtrainer.ui.theme.Eyn
import com.eyn.designtrainer.ui.theme.Type
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
fun ProfileScreen() {
    val c = Eyn.c
    val d = LocalData.current
    val nav = LocalNav.current
    val level = d.points / 100 + 1
    val levelName = when {
        level < 3 -> tr("مصمم مبتدئ", "Beginner designer")
        level < 6 -> tr("مصمم متدرّب", "Trained designer")
        level < 10 -> tr("مصمم محترف", "Pro designer")
        else -> tr("مصمم خبير", "Expert designer")
    }
    val accuracy = if (d.answered == 0) 0 else d.correct * 100 / d.answered

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        TopBar(tr("حسابي", "Profile"), big = true) {
            SquareIconButton(Icons.Rounded.Settings, { nav.navigate(Routes.SETTINGS) })
        }
        Column(Modifier.padding(horizontal = 20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(68.dp).clip(CircleShape).background(c.hero), contentAlignment = Alignment.Center) {
                    Text(d.name.trim().take(1).ifEmpty { tr("ع", "E") }, style = Type.h1.copy(fontSize = 30.sp), color = c.onHero)
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(d.name.ifBlank { tr("مصمم المستقبل", "Future designer") }, style = Type.h2, color = c.text)
                    Text(tr("المستوى $level · $levelName", "Level $level · $levelName"), style = Type.small, color = c.muted)
                    Spacer(Modifier.height(8.dp))
                    ProgressBar((d.points % 100) / 100f, Modifier.fillMaxWidth())
                }
            }
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("${d.points}", tr("نقطة", "Points"), Modifier.weight(1f))
                StatCard("$accuracy%", tr("دقة الإجابات", "Accuracy"), Modifier.weight(1f))
                StatCard("${d.streak}", tr("أيام متواصلة", "Day streak"), Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
            WeekChart()
            Spacer(Modifier.height(20.dp))
            Text(tr("دروسي المحفوظة", "Saved lessons"), style = Type.h3, color = c.text)
            Spacer(Modifier.height(10.dp))
            val saved = Content.lessons.filter { it.id in d.bookmarks }
            if (saved.isEmpty()) Text(
                tr("اضغط أيقونة الإشارة 🔖 في أعلى أي درس لحفظه هنا.", "Tap the bookmark 🔖 at the top of any lesson to save it here."),
                style = Type.small, color = c.muted,
            )
            saved.forEach { l ->
                EynCard(Modifier.fillMaxWidth().padding(bottom = 8.dp), onClick = { nav.navigate(Routes.lesson(l.id)) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Bookmark, null, tint = c.accent, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(l.title.t(), style = Type.label, color = c.text)
                            Text(l.cat.label.t() + tr(" · ${l.minutes} دقائق", " · ${l.minutes} min"), style = Type.small, color = c.muted)
                        }
                        if (l.id in d.completed) Icon(Icons.Rounded.CheckCircle, null, tint = c.success, modifier = Modifier.size(18.dp))
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            Text(tr("الأوسمة", "Badges"), style = Type.h3, color = c.text)
            Spacer(Modifier.height(12.dp))
            val spacingDone = Content.lessons.filter { it.cat == Cat.SPACING }.all { it.id in d.completed }
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Badge(Icons.Rounded.LocalFireDepartment, tr("أسبوع كامل", "Full week"), d.streak >= 7, c.accent, c.accentSoft)
                Badge(Icons.Rounded.SettingsEthernet, tr("سيد المسافات", "Spacing master"), spacingDone, c.primary, c.lilac)
                Badge(Icons.Rounded.MilitaryTech, tr("أول 50 تحدياً", "First 50"), d.answered >= 50, c.success, c.successSoft)
                Badge(Icons.Rounded.Brush, tr("مصمم بنّاء", "Builder"), d.designs.size + d.projects.size >= 3, c.accent, c.accentSoft)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun WeekChart() {
    val c = Eyn.c
    val d = LocalData.current
    val today = LocalDate.now()
    val days = (6 downTo 0).map { today.minusDays(it.toLong()) }
    val max = (days.maxOf { d.activity[it.toEpochDay()] ?: 0 }).coerceAtLeast(3)
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { shown = true }
    EynCard(Modifier.fillMaxWidth()) {
        Text(tr("نشاط الأسبوع", "This week"), style = Type.h3, color = c.text)
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth().height(110.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
            days.forEach { day ->
                val v = d.activity[day.toEpochDay()] ?: 0
                val isToday = day == today
                val h by animateDpAsState(if (shown) (8 + 70f * v / max).dp else 8.dp, tween(800), label = "bar")
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Box(Modifier.width(18.dp).height(h).clip(RoundedCornerShape(6.dp)).background(if (isToday) c.primary else c.lilac))
                    Spacer(Modifier.height(8.dp))
                    Text(
                        dayLabel(day.dayOfWeek), style = Type.small.copy(fontSize = 10.sp, fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal),
                        color = if (isToday) c.primary else c.muted, maxLines = 1, softWrap = false,
                    )
                }
            }
        }
    }
}

@Composable
private fun dayLabel(d: DayOfWeek) = when (d) {
    DayOfWeek.SATURDAY -> tr("السبت", "Sat")
    DayOfWeek.SUNDAY -> tr("الأحد", "Sun")
    DayOfWeek.MONDAY -> tr("الإثنين", "Mon")
    DayOfWeek.TUESDAY -> tr("الثلاثاء", "Tue")
    DayOfWeek.WEDNESDAY -> tr("الأربعاء", "Wed")
    DayOfWeek.THURSDAY -> tr("الخميس", "Thu")
    DayOfWeek.FRIDAY -> tr("الجمعة", "Fri")
}

@Composable
private fun Badge(icon: ImageVector, label: String, earned: Boolean, tint: androidx.compose.ui.graphics.Color, bg: androidx.compose.ui.graphics.Color) {
    val c = Eyn.c
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(78.dp)) {
        Box(
            Modifier.size(52.dp).clip(CircleShape).background(if (earned) bg else c.surfaceAlt),
            contentAlignment = Alignment.Center,
        ) { Icon(icon, null, tint = if (earned) tint else c.muted.copy(alpha = 0.5f), modifier = Modifier.size(24.dp)) }
        Spacer(Modifier.height(6.dp))
        Text(label, style = Type.small, color = if (earned) c.text else c.muted, maxLines = 1)
    }
}

@Composable
fun SettingsScreen() {
    val c = Eyn.c
    val d = LocalData.current
    val vm = LocalVm.current
    val nav = LocalNav.current
    var name by rememberSaveable { mutableStateOf(d.name) }
    var confirmReset by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        TopBar(tr("الإعدادات", "Settings"), onBack = { nav.popBackStack() })
        Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            EynCard(Modifier.fillMaxWidth()) {
                Text(tr("الاسم", "Name"), style = Type.label, color = c.text)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    name, { name = it.take(24); vm.setName(name) }, singleLine = true,
                    shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth(),
                )
            }
            EynCard(Modifier.fillMaxWidth()) {
                Text(tr("المظهر", "Appearance"), style = Type.label, color = c.text)
                Spacer(Modifier.height(10.dp))
                val opts = listOf("system", "light", "dark")
                Segmented(
                    listOf(tr("تلقائي", "Auto"), tr("نهاري", "Light"), tr("ليلي", "Dark")),
                    opts.indexOf(d.theme).coerceAtLeast(0), { vm.setTheme(opts[it]) }, Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(6.dp))
                Text(tr("التلقائي يتبع إعدادات جهازك", "Auto follows your device setting"), style = Type.small, color = c.muted)
            }
            EynCard(Modifier.fillMaxWidth()) {
                Text(tr("اللغة", "Language"), style = Type.label, color = c.text)
                Spacer(Modifier.height(10.dp))
                Segmented(listOf("العربية", "English"), if (d.lang == "en") 1 else 0, { vm.setLang(if (it == 1) "en" else "ar") }, Modifier.fillMaxWidth())
            }
            EynCard(Modifier.fillMaxWidth()) {
                Text(tr("عن التطبيق", "About"), style = Type.label, color = c.text)
                Spacer(Modifier.height(6.dp))
                Text(
                    tr("ريشة · مدرّب التصميم. تعلّم، تدرّب، صمّم بثقة.\nالإصدار 1.6.1", "Risha · Design Trainer. Learn, practice, design with confidence.\nVersion 1.6.1"),
                    style = Type.small, color = c.muted,
                )
            }
            TextButton({ confirmReset = true }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(tr("إعادة ضبط التقدّم", "Reset progress"), style = Type.label, color = c.danger)
            }
            Spacer(Modifier.height(12.dp))
        }
    }
    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            containerColor = c.surface,
            title = { Text(tr("إعادة ضبط التقدّم؟", "Reset progress?"), style = Type.h3, color = c.text) },
            text = { Text(tr("ستُحذف النقاط والدروس المكتملة. تصاميمك ومشاريعك ستبقى.", "Points and completed lessons will be cleared. Your designs and projects stay."), style = Type.body, color = c.muted) },
            confirmButton = { TextButton({ vm.resetProgress(); confirmReset = false }) { Text(tr("إعادة الضبط", "Reset"), color = c.danger) } },
            dismissButton = { TextButton({ confirmReset = false }) { Text(tr("إلغاء", "Cancel"), color = c.muted) } },
        )
    }
}
