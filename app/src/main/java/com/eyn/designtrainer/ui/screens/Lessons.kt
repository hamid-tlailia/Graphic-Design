package com.eyn.designtrainer.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.clickable
import androidx.compose.ui.unit.dp
import com.eyn.designtrainer.data.AppData
import com.eyn.designtrainer.data.Cat
import com.eyn.designtrainer.data.Content
import com.eyn.designtrainer.data.Lesson
import com.eyn.designtrainer.data.Practice
import com.eyn.designtrainer.i18n.LocalLang
import com.eyn.designtrainer.i18n.tr
import com.eyn.designtrainer.ui.LocalData
import com.eyn.designtrainer.ui.LocalNav
import com.eyn.designtrainer.ui.LocalVm
import com.eyn.designtrainer.ui.Routes
import com.eyn.designtrainer.ui.goTab
import com.eyn.designtrainer.ui.components.EynCard
import com.eyn.designtrainer.ui.components.LessonIllustration
import com.eyn.designtrainer.ui.components.OutlineButton
import com.eyn.designtrainer.ui.components.Pill
import com.eyn.designtrainer.ui.components.appear
import com.eyn.designtrainer.ui.components.PrimaryButton
import com.eyn.designtrainer.ui.components.ProgressBar
import com.eyn.designtrainer.ui.components.SquareIconButton
import com.eyn.designtrainer.ui.components.TopBar
import com.eyn.designtrainer.ui.theme.Eyn
import com.eyn.designtrainer.ui.theme.Type

/** Unit 1 is open; each later unit opens once a lesson of the previous unit is finished. */
fun isUnlocked(index: Int, d: AppData) = com.eyn.designtrainer.data.Path.isUnlocked(Content.lessons[index].id, d.completed)

fun Practice.route() = when (this) {
    Practice.SPACING -> Routes.SPACING
    Practice.CONTRAST -> Routes.CONTRAST
    Practice.FONTS -> Routes.FONTS
    Practice.STUDIO -> Routes.STUDIO
}

@Composable
fun LessonsScreen() {
    val c = Eyn.c
    val d = LocalData.current
    val nav = LocalNav.current
    val lang = LocalLang.current
    var filter by rememberSaveable { mutableIntStateOf(0) }
    var searching by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    val cats = listOf<Cat?>(null, null) + Cat.entries // 0 all, 1 saved, then categories
    val current = Content.lessons.indexOfFirst { it.id !in d.completed }
    var pathView by rememberSaveable { mutableStateOf(true) }

    Column(Modifier.fillMaxSize()) {
        TopBar(tr("الدروس", "Lessons"), big = true) {
            SquareIconButton(Icons.Rounded.Bookmark, { pathView = false; searching = false; filter = 1 }, tint = if (!pathView && filter == 1) c.accent else c.text)
            SquareIconButton(if (searching) Icons.Rounded.Close else Icons.Rounded.Search, {
                searching = !searching; if (!searching) query = ""
            })
        }
        AnimatedVisibility(searching, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
            OutlinedTextField(
                query, { query = it },
                singleLine = true,
                placeholder = { Text(tr("ابحث عن درس…", "Search lessons…")) },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp),
            )
        }
        com.eyn.designtrainer.ui.components.Segmented(
            listOf(tr("المسار", "Path"), tr("كل الدروس", "All lessons")), if (pathView && !searching) 0 else 1,
            { pathView = it == 0; if (pathView) searching = false }, Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp),
        )
        if (pathView && !searching) {
            PathView(d, current) { nav.navigate(Routes.lesson(it)) }
            return@Column
        }
        com.eyn.designtrainer.ui.components.ChipRow(
            cats.mapIndexed { i, cat -> cat?.label?.t() ?: if (i == 0) tr("الكل", "All") else tr("🔖 المحفوظة (${d.bookmarks.size})", "🔖 Saved (${d.bookmarks.size})") }, filter, { filter = it },
            Modifier.padding(vertical = 8.dp),
        )
        val list = Content.lessons.withIndex().filter { (_, l) ->
            (if (filter == 1) l.id in d.bookmarks else filter == 0 || l.cat == cats[filter]) &&
                (query.isBlank() || l.title.get(lang).contains(query, true))
        }
        if (filter == 1 && list.isEmpty()) Text(
            tr("لا توجد دروس محفوظة بعد. افتح أي درس واضغط أيقونة الإشارة 🔖 في الأعلى.", "No saved lessons yet. Open a lesson and tap the bookmark 🔖 at the top."),
            style = Type.body, color = c.muted, modifier = Modifier.padding(24.dp),
        )
        LazyColumn(
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            itemsIndexed(list, key = { _, it -> it.value.id }) { _, (i, lesson) ->
                LessonRow(i, lesson, d, isCurrent = i == current, modifier = Modifier.animateItem()) {
                    nav.navigate(Routes.lesson(lesson.id))
                }
            }
        }
    }
}

/** The curriculum as units of connected lesson nodes. */
@Composable
private fun PathView(d: AppData, current: Int, onOpen: (String) -> Unit) {
    val c = Eyn.c
    val total = Content.lessons.size
    val doneAll = Content.lessons.count { it.id in d.completed }
    LazyColumn(contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            EynCard(Modifier.fillMaxWidth(), color = c.hero, border = null) {
                Text(tr("مسار مصمم الغرافيك", "Graphic designer path"), style = Type.h3, color = c.onHero)
                Text(
                    tr("${com.eyn.designtrainer.data.Path.units.size} وحدات · $total درساً — من الأساسيات إلى ملف الأعمال", "${com.eyn.designtrainer.data.Path.units.size} units · $total lessons — from basics to portfolio"),
                    style = Type.small, color = c.heroMuted,
                )
                Spacer(Modifier.height(12.dp))
                ProgressBar(doneAll / total.toFloat(), Modifier.fillMaxWidth(), track = Color.White.copy(alpha = 0.18f))
                Spacer(Modifier.height(6.dp))
                Text(tr("أنهيت $doneAll من $total", "$doneAll of $total done"), style = Type.small, color = c.heroMuted)
            }
        }
        com.eyn.designtrainer.data.Path.units.forEachIndexed { ui, unit ->
            item(key = unit.id) {
                val lessons = unit.lessonIds.map { Content.lesson(it) }
                val done = lessons.count { it.id in d.completed }
                val open = com.eyn.designtrainer.data.Path.isUnlocked(unit.lessonIds.first(), d.completed)
                EynCard(Modifier.appear(ui + 1).fillMaxWidth(), color = if (open) c.surface else c.surfaceAlt) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(48.dp).clip(RoundedCornerShape(16.dp)).background(if (done == lessons.size) c.successSoft else if (open) c.lilac else c.outline),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (done == lessons.size) Icon(Icons.Rounded.Check, null, tint = c.success)
                            else if (!open) Icon(Icons.Rounded.Lock, null, tint = c.muted, modifier = Modifier.size(20.dp))
                            else com.eyn.designtrainer.builder.FontIcon(unit.icon, c.onLilac, 24f)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(tr("الوحدة ${ui + 1}", "Unit ${ui + 1}"), style = Type.small, color = c.muted)
                            Text(unit.title.t(), style = Type.h3, color = if (open) c.text else c.muted)
                            Text(unit.desc.t(), style = Type.small, color = c.muted)
                        }
                        Text("$done/${lessons.size}", style = Type.label, color = if (done == lessons.size) c.success else c.primary)
                    }
                    Spacer(Modifier.height(10.dp))
                    ProgressBar(done / lessons.size.toFloat(), Modifier.fillMaxWidth(), height = 4.dp)
                    Spacer(Modifier.height(8.dp))
                    lessons.forEachIndexed { li, l ->
                        val gi = Content.lessons.indexOf(l)
                        val isDone = l.id in d.completed
                        val isCur = gi == current && open
                        Row(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                                .then(if (open) Modifier.clickable { onOpen(l.id) } else Modifier)
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(32.dp)) {
                                Box(
                                    Modifier.size(28.dp).clip(CircleShape).background(
                                        when { isDone -> c.success; isCur -> c.primary; open -> c.lilac; else -> c.outline },
                                    ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (isDone) Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    else Text("${gi + 1}", style = Type.small.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold), color = if (isCur) c.onPrimary else c.onLilac)
                                }
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(l.title.t(), style = Type.label, color = if (open) c.text else c.muted)
                                Text(tr("${l.minutes} دقائق", "${l.minutes} min"), style = Type.small, color = c.muted)
                            }
                            if (isCur) Pill(tr("التالي", "Next"), c.accentSoft, c.accent, style = Type.small)
                        }
                        if (li < lessons.lastIndex) Box(Modifier.padding(start = 15.dp).width(2.dp).height(8.dp).background(c.outline))
                    }
                    if (!open) Text(
                        tr("تُفتح بعد إنهاء درس من الوحدة ${ui}", "Unlocks after finishing a lesson from unit $ui"),
                        style = Type.small, color = c.muted, modifier = Modifier.padding(top = 6.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun LessonRow(index: Int, lesson: Lesson, d: AppData, isCurrent: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val c = Eyn.c
    val unlocked = isUnlocked(index, d)
    val done = lesson.id in d.completed
    val prog = d.progress[lesson.id] ?: 0
    EynCard(
        modifier.fillMaxWidth(),
        onClick = if (unlocked) onClick else null,
        color = if (unlocked) c.surface else c.surfaceAlt,
        border = BorderStroke(if (isCurrent && unlocked) 2.dp else 1.dp, if (isCurrent && unlocked) c.primary else c.outline),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            done -> c.successSoft
                            !unlocked -> c.outline
                            isCurrent -> c.primary
                            else -> c.lilac
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    done -> Icon(Icons.Rounded.Check, null, tint = c.success, modifier = Modifier.size(20.dp))
                    !unlocked -> Icon(Icons.Rounded.Lock, null, tint = c.muted, modifier = Modifier.size(18.dp))
                    else -> Text("${index + 1}", style = Type.h3, color = if (isCurrent) c.onPrimary else c.onLilac)
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(lesson.title.t(), style = Type.h3, color = if (unlocked) c.text else c.muted)
                Text(
                    if (unlocked) tr("${lesson.minutes} دقائق · ${lesson.exercises} تمارين", "${lesson.minutes} min · ${lesson.exercises} exercises")
                    else tr("يُفتح بعد إنهاء درس من الوحدة ${com.eyn.designtrainer.data.Path.unitOf(lesson.id)}", "Unlocks after a lesson from unit ${com.eyn.designtrainer.data.Path.unitOf(lesson.id)}"),
                    style = Type.small, color = c.muted,
                )
                if (!done && prog > 0) {
                    Spacer(Modifier.height(8.dp))
                    ProgressBar(prog / 100f, Modifier.fillMaxWidth(0.7f), height = 4.dp)
                }
            }
            when {
                done -> Text(tr("مكتمل", "Done"), style = Type.label, color = c.success)
                prog > 0 -> Text("$prog%", style = Type.label, color = c.primary)
            }
        }
    }
}

@Composable
fun LessonScreen(id: String) {
    val c = Eyn.c
    val d = LocalData.current
    val vm = LocalVm.current
    val nav = LocalNav.current
    val lesson = Content.lessons.firstOrNull { it.id == id } ?: return
    val index = Content.lessons.indexOf(lesson)
    LaunchedEffect(id) { vm.openLesson(id) }

    Column(Modifier.fillMaxSize()) {
        TopBar(
            tr("الدرس ${index + 1} من ${Content.lessons.size}", "Lesson ${index + 1} of ${Content.lessons.size}"),
            onBack = { nav.popBackStack() },
        ) {
            SquareIconButton(
                if (id in d.bookmarks) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                { vm.toggleBookmark(id) },
                tint = if (id in d.bookmarks) c.accent else c.text,
            )
        }
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .animateContentSize(),
        ) {
            LessonIllustration(lesson.ill)
            Spacer(Modifier.height(24.dp))
            Text(lesson.cat.label.t(), style = Type.label, color = c.accent)
            Text(lesson.title.t(), style = Type.h1, color = c.text)
            Spacer(Modifier.height(10.dp))
            Text(lesson.body.t(), style = Type.body.copy(lineHeight = Type.body.lineHeight * 1.08f), color = c.muted)
            Spacer(Modifier.height(18.dp))
            lesson.points.forEach { p ->
                Row(Modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(24.dp).clip(CircleShape).background(c.successSoft), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.Check, null, tint = c.success, modifier = Modifier.size(15.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(p.t(), style = Type.body, color = c.text)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
        Column(
            Modifier.navigationBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            PrimaryButton(tr("جرّبه عملياً", "Try it in practice"), icon = Icons.Rounded.Draw) {
                val r = lesson.practice.route()
                if (r in Routes.tabs) nav.goTab(r) else nav.navigate(r)
            }
            OutlineButton(
                if (id in d.completed) tr("أعد الاختبار", "Retake the quiz") else tr("اختبر نفسك وأكمل الدرس", "Quiz yourself to finish"),
                icon = Icons.Rounded.Quiz,
            ) { nav.navigate(Routes.quiz("lesson:$id")) }
        }
    }
}
