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
import com.eyn.designtrainer.ui.components.EynCard
import com.eyn.designtrainer.ui.components.LessonIllustration
import com.eyn.designtrainer.ui.components.OutlineButton
import com.eyn.designtrainer.ui.components.PrimaryButton
import com.eyn.designtrainer.ui.components.ProgressBar
import com.eyn.designtrainer.ui.components.SquareIconButton
import com.eyn.designtrainer.ui.components.TopBar
import com.eyn.designtrainer.ui.theme.Eyn
import com.eyn.designtrainer.ui.theme.Type

/** A lesson unlocks once the previous one is done; the first four are always open. */
fun isUnlocked(index: Int, d: AppData) = index < 4 || Content.lessons[index - 1].id in d.completed

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
    val cats = listOf<Cat?>(null, Cat.BASICS, Cat.COLORS, Cat.TYPE, Cat.SPACING, Cat.ALIGN)
    val current = Content.lessons.indexOfFirst { it.id !in d.completed }

    Column(Modifier.fillMaxSize()) {
        TopBar(tr("الدروس", "Lessons"), big = true) {
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
        com.eyn.designtrainer.ui.components.ChipRow(
            cats.map { it?.label?.t() ?: tr("الكل", "All") }, filter, { filter = it },
            Modifier.padding(vertical = 8.dp),
        )
        val list = Content.lessons.withIndex().filter { (_, l) ->
            (cats[filter] == null || l.cat == cats[filter]) &&
                (query.isBlank() || l.title.get(lang).contains(query, true))
        }
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
                    else tr("يُفتح بعد الدرس $index", "Unlocks after lesson $index"),
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
            PrimaryButton(tr("جرّبه عملياً", "Try it in practice"), icon = Icons.Rounded.Draw) { nav.navigate(lesson.practice.route()) }
            OutlineButton(
                if (id in d.completed) tr("أعد الاختبار", "Retake the quiz") else tr("اختبر نفسك وأكمل الدرس", "Quiz yourself to finish"),
                icon = Icons.Rounded.Quiz,
            ) { nav.navigate(Routes.quiz("lesson:$id")) }
        }
    }
}
