package com.eyn.designtrainer.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eyn.designtrainer.data.Cat
import com.eyn.designtrainer.data.Content
import com.eyn.designtrainer.data.Question
import com.eyn.designtrainer.i18n.LocalLang
import com.eyn.designtrainer.i18n.optionLetter
import com.eyn.designtrainer.i18n.tr
import com.eyn.designtrainer.ui.LocalData
import com.eyn.designtrainer.ui.LocalNav
import com.eyn.designtrainer.ui.LocalVm
import com.eyn.designtrainer.ui.Routes
import com.eyn.designtrainer.ui.components.EynCard
import com.eyn.designtrainer.ui.components.OutlineButton
import com.eyn.designtrainer.ui.components.Pill
import com.eyn.designtrainer.ui.components.PrimaryButton
import com.eyn.designtrainer.ui.components.QuizVisual
import com.eyn.designtrainer.ui.components.SegmentedProgress
import com.eyn.designtrainer.ui.components.SquareIconButton
import com.eyn.designtrainer.ui.components.bounceClick
import com.eyn.designtrainer.ui.theme.Eyn
import com.eyn.designtrainer.ui.theme.Type
import java.time.LocalDate

private enum class Phase { ASK, EXPLAIN, RESULT }

@Composable
fun QuizScreen(mode: String) {
    val d = LocalData.current
    val vm = LocalVm.current
    val nav = LocalNav.current
    val haptic = LocalHapticFeedback.current

    val alreadyDone = remember { vm.dailyDoneToday(d) }
    val countsForDaily = mode == "daily" && alreadyDone < 5
    val questions: List<Question> = remember(mode) {
        when {
            mode == "daily" -> Content.daily(LocalDate.now().toEpochDay()).let { if (alreadyDone in 1..4) it.drop(alreadyDone) else it }
            mode.startsWith("cat:") -> Content.byCat(Cat.valueOf(mode.removePrefix("cat:"))).shuffled()
            mode.startsWith("lesson:") -> Content.forLesson(mode.removePrefix("lesson:"))
            else -> Content.questions.take(5)
        }
    }
    val offset = if (countsForDaily) alreadyDone else 0
    val total = if (countsForDaily) 5 else questions.size

    var index by rememberSaveable { mutableIntStateOf(0) }
    var selected by rememberSaveable { mutableIntStateOf(-1) }
    var phase by rememberSaveable { mutableStateOf(Phase.ASK) }
    var right by rememberSaveable { mutableIntStateOf(0) }
    var run by rememberSaveable { mutableIntStateOf(0) }

    AnimatedContent(
        targetState = phase to index,
        transitionSpec = {
            (slideInHorizontally(tween(320)) { it / 4 } + fadeIn(tween(320)))
                .togetherWith(slideOutHorizontally(tween(260)) { -it / 4 } + fadeOut(tween(200)))
        },
        label = "quiz",
    ) { (ph, i) ->
        val qq = questions[i.coerceAtMost(questions.lastIndex)]
        when (ph) {
            Phase.ASK -> AskView(
                q = qq, number = offset + i + 1, total = total, done = offset + i, selected = selected,
                onSelect = { selected = it; haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) },
                onBack = { nav.popBackStack() },
                onConfirm = {
                    val ok = selected == qq.correct
                    if (ok) { right++; run++ } else run = 0
                    vm.answer(ok, run, countsForDaily)
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    phase = Phase.EXPLAIN
                },
            )
            Phase.EXPLAIN -> ExplainView(
                q = qq, chosen = selected, run = run, last = i == questions.lastIndex,
                onLesson = { nav.navigate(Routes.lesson(qq.lessonId)) },
                onNext = {
                    if (i == questions.lastIndex) {
                        phase = Phase.RESULT
                        if (mode.startsWith("lesson:") && right * 2 >= questions.size) vm.completeLesson(mode.removePrefix("lesson:"))
                    } else {
                        index = i + 1; selected = -1; phase = Phase.ASK
                    }
                },
            )
            Phase.RESULT -> ResultView(
                right = right, total = questions.size, lessonMode = mode.startsWith("lesson:"),
                nextLesson = mode.removePrefix("lesson:").let { id ->
                    val i = Content.lessons.indexOfFirst { it.id == id }
                    if (mode.startsWith("lesson:") && i >= 0) Content.lessons.getOrNull(i + 1) else null
                },
                onNext = { next ->
                    // Leave the quiz and the finished lesson, then open the next one.
                    nav.popBackStack()
                    if (nav.currentBackStackEntry?.destination?.route == "lesson/{id}") nav.popBackStack()
                    nav.navigate(Routes.lesson(next.id))
                },
                onHome = {
                    nav.popBackStack()
                    if (mode.startsWith("lesson:") && right * 2 >= questions.size && nav.currentBackStackEntry?.destination?.route == "lesson/{id}") nav.popBackStack()
                },
                onRetry = { index = 0; selected = -1; right = 0; run = 0; phase = Phase.ASK },
            )
        }
    }
}

@Composable
private fun AskView(
    q: Question, number: Int, total: Int, done: Int, selected: Int,
    onSelect: (Int) -> Unit, onBack: () -> Unit, onConfirm: () -> Unit,
) {
    val c = Eyn.c
    val lang = LocalLang.current
    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        Row(Modifier.padding(horizontal = 20.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            SquareIconButton(Icons.AutoMirrored.Rounded.ArrowBack, onBack)
            Text(
                tr("تحدي $number من $total", "Challenge $number of $total"),
                style = Type.h3, color = c.text, textAlign = TextAlign.Center, modifier = Modifier.weight(1f),
            )
            Pill("+10", c.accentSoft, c.accent)
        }
        SegmentedProgress(total, done, Modifier.padding(horizontal = 20.dp))
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp),
        ) {
            Text(q.q.t(), style = Type.h2, color = c.text)
            Spacer(Modifier.height(4.dp))
            Text(q.hint.t(), style = Type.small, color = c.muted)
            Spacer(Modifier.height(20.dp))
            q.options.forEachIndexed { i, opt ->
                val on = i == selected
                val border by animateColorAsState(if (on) c.primary else c.outline, label = "opt")
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .bounceClick { onSelect(i) }
                        .clip(RoundedCornerShape(20.dp))
                        .background(c.surface)
                        .border(if (on) 2.dp else 1.dp, border, RoundedCornerShape(20.dp))
                        .padding(14.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(22.dp).clip(CircleShape).border(2.dp, if (on) c.primary else c.outline, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (on) Box(Modifier.size(10.dp).clip(CircleShape).background(c.primary))
                        }
                        Spacer(Modifier.width(12.dp))
                        if (opt.text != null) Text(opt.text.t(), style = Type.h3, color = c.text, modifier = Modifier.weight(1f))
                        else Spacer(Modifier.weight(1f))
                        Box(
                            Modifier.size(28.dp).clip(CircleShape).background(if (on) c.primary else c.lilac),
                            contentAlignment = Alignment.Center,
                        ) { Text(optionLetter(i, lang), style = Type.label, color = if (on) c.onPrimary else c.onLilac) }
                    }
                    if (opt.vis != null) {
                        Spacer(Modifier.height(12.dp))
                        QuizVisual(opt.vis, opt.good)
                    }
                }
            }
        }
        Box(Modifier.navigationBarsPadding().padding(20.dp)) {
            PrimaryButton(tr("تأكيد الإجابة", "Confirm answer"), enabled = selected >= 0, onClick = onConfirm)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExplainView(q: Question, chosen: Int, run: Int, last: Boolean, onLesson: () -> Unit, onNext: () -> Unit) {
    val c = Eyn.c
    val lang = LocalLang.current
    val ok = chosen == q.correct
    val pop = remember { Animatable(0.4f) }
    LaunchedEffect(Unit) { pop.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)) }
    val letter = optionLetter(q.correct, lang)

    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(20.dp))
            Box(
                Modifier
                    .graphicsLayer { scaleX = pop.value; scaleY = pop.value }
                    .size(104.dp)
                    .clip(CircleShape)
                    .background(if (ok) c.successSoft else c.dangerSoft),
                contentAlignment = Alignment.Center,
            ) {
                Icon(if (ok) Icons.Rounded.Check else Icons.Rounded.Close, null, tint = if (ok) c.success else c.danger, modifier = Modifier.size(52.dp))
            }
            Spacer(Modifier.height(20.dp))
            Text(if (ok) tr("إجابة صحيحة!", "Correct!") else tr("ليس تماماً", "Not quite"), style = Type.h1, color = c.text)
            Spacer(Modifier.height(10.dp))
            Pill(
                if (ok) tr("+10 نقاط · سلسلة $run إجابات", "+10 points · $run in a row")
                else tr("الإجابة الصحيحة: ($letter)", "Correct answer: ($letter)"),
                if (ok) c.accentSoft else c.dangerSoft, if (ok) c.accent else c.danger,
            )
            Spacer(Modifier.height(24.dp))
            EynCard(Modifier.fillMaxWidth()) {
                Text(tr("لماذا ($letter) أفضل؟", "Why is ($letter) better?"), style = Type.h3, color = c.text)
                Spacer(Modifier.height(8.dp))
                Text(q.why.t(), style = Type.body, color = c.muted)
                if (q.tags.isNotEmpty()) {
                    Spacer(Modifier.height(14.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        q.tags.forEach { Pill(it.t(), c.lilac, c.onLilac) }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            val lesson = Content.lesson(q.lessonId)
            EynCard(Modifier.fillMaxWidth(), onClick = onLesson) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(c.lilac), contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Rounded.MenuBook, null, tint = c.onLilac, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(tr("درس مرتبط", "Related lesson"), style = Type.label, color = c.text)
                        Text(
                            lesson.title.t() + tr(" · ${lesson.minutes} دقائق", " · ${lesson.minutes} min"),
                            style = Type.small, color = c.muted,
                        )
                    }
                    Icon(Icons.AutoMirrored.Rounded.ArrowForward, null, tint = c.muted, modifier = Modifier.size(18.dp))
                }
            }
        }
        Box(Modifier.navigationBarsPadding().padding(20.dp)) {
            PrimaryButton(if (last) tr("عرض النتيجة", "See results") else tr("التالي", "Next"), onClick = onNext)
        }
    }
}

@Composable
private fun ResultView(
    right: Int, total: Int, lessonMode: Boolean, nextLesson: com.eyn.designtrainer.data.Lesson?,
    onNext: (com.eyn.designtrainer.data.Lesson) -> Unit, onHome: () -> Unit, onRetry: () -> Unit,
) {
    val c = Eyn.c
    val pop = remember { Animatable(0.3f) }
    LaunchedEffect(Unit) { pop.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)) }
    val passed = right * 2 >= total
    Column(
        Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1f))
        Box(
            Modifier.graphicsLayer { scaleX = pop.value; scaleY = pop.value; rotationZ = (1 - pop.value) * -30 }
                .size(128.dp).clip(CircleShape).background(c.accentSoft),
            contentAlignment = Alignment.Center,
        ) { Icon(Icons.Rounded.EmojiEvents, null, tint = c.accent, modifier = Modifier.size(64.dp)) }
        Spacer(Modifier.height(24.dp))
        Text("$right / $total", style = Type.h1.copy(fontSize = 44.sp, lineHeight = 52.sp), color = c.primary)
        Spacer(Modifier.height(8.dp))
        Text(
            when {
                right == total -> tr("لمسة مصمم حقيقية!", "A true designer's touch!")
                passed -> tr("عمل رائع، استمر!", "Great work, keep going!")
                else -> tr("تحتاج تدريباً أكثر قليلاً", "A little more practice needed")
            },
            style = Type.h2, color = c.text, textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            tr("ربحت ${right * 10} نقطة", "You earned ${right * 10} points") +
                if (lessonMode && passed) tr(" + 20 لإكمال الدرس", " + 20 for finishing the lesson") else "",
            style = Type.body, color = c.muted, textAlign = TextAlign.Center,
        )
        Spacer(Modifier.weight(1f))
        when {
            lessonMode && passed && nextLesson != null -> {
                PrimaryButton(tr("الدرس التالي: ", "Next lesson: ") + nextLesson.title.t(), icon = Icons.AutoMirrored.Rounded.ArrowForward) { onNext(nextLesson) }
                Spacer(Modifier.height(10.dp))
                OutlineButton(tr("العودة إلى الدروس", "Back to lessons"), onClick = onHome)
            }
            lessonMode && !passed -> {
                PrimaryButton(tr("أعد المحاولة", "Try again"), onClick = onRetry)
                Spacer(Modifier.height(10.dp))
                OutlineButton(tr("راجع الدرس", "Review the lesson"), onClick = onHome)
            }
            else -> {
                PrimaryButton(if (lessonMode) tr("🎉 أنهيت المسار! العودة للدروس", "🎉 Path complete! Back to lessons") else tr("متابعة", "Continue"), onClick = onHome)
                Spacer(Modifier.height(10.dp))
                OutlineButton(tr("أعد المحاولة", "Try again"), onClick = onRetry)
            }
        }
    }
}
