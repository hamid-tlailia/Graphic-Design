package com.eyn.designtrainer

import com.eyn.designtrainer.data.Content
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LessonQuizTest {
    @Test fun everyLessonHasItsOwnThreeQuestions() {
        val counts = Content.lessons.associate { l -> l.id to Content.questions.count { it.lessonId == l.id } }
        println("COUNTS " + counts)
        val short = counts.filterValues { it < 3 }
        assertTrue("Lessons with fewer than 3 own questions: $short", short.isEmpty())
    }

    @Test fun noTwoLessonsShareAQuiz() {
        val quizzes = Content.lessons.map { l -> Content.forLesson(l.id).map { it.id }.toSet() }
        assertEquals(quizzes.size, quizzes.toSet().size)
    }

    @Test fun questionIdsAreUnique() {
        val ids = Content.questions.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }
}
