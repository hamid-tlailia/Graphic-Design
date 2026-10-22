package com.eyn.designtrainer.data

import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.util.Locale

private val android.content.Context.store by preferencesDataStore("eyn")
private val KEY = stringPreferencesKey("data")

/**
 * Single source of truth. State lives in memory (so sliders and drags update instantly)
 * and is persisted to DataStore shortly after each change.
 */
@OptIn(FlowPreview::class)
class AppViewModel(app: Application) : AndroidViewModel(app) {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    private val _data = MutableStateFlow<AppData?>(null)
    val data: StateFlow<AppData?> = _data

    init {
        viewModelScope.launch {
            val raw = app.store.data.first()[KEY]
            val loaded = raw?.let { runCatching { json.decodeFromString<AppData>(it) }.getOrNull() } ?: AppData()
            _data.value = if (loaded.lang.isEmpty())
                loaded.copy(lang = if (Locale.getDefault().language == "ar") "ar" else "en")
            else loaded
            _data.filterNotNull().debounce(350).collect { d ->
                app.store.edit { it[KEY] = json.encodeToString(AppData.serializer(), d) }
            }
        }
    }

    fun update(f: (AppData) -> AppData) = _data.update { it?.let(f) }

    // ------------------------------------------------------------ settings
    fun setTheme(t: String) = update { it.copy(theme = t) }
    fun setLang(l: String) = update { it.copy(lang = l) }
    fun setName(n: String) = update { it.copy(name = n) }
    fun finishOnboarding(name: String) = update { it.copy(onboarded = true, name = name.trim().ifEmpty { it.name }) }
    fun resetProgress() = update {
        AppData(theme = it.theme, lang = it.lang, onboarded = true, name = it.name, designs = it.designs, projects = it.projects)
    }

    // ------------------------------------------------------------ learning
    private fun today() = LocalDate.now().toEpochDay()

    private fun AppData.touch(): AppData {
        val t = today()
        val newStreak = when (lastDay) {
            t -> streak.coerceAtLeast(1)
            t - 1 -> streak + 1
            else -> 1
        }
        val act = (activity + (t to (activity[t] ?: 0) + 1)).filterKeys { it > t - 14 }
        return copy(streak = newStreak, lastDay = t, activity = act)
    }

    fun answer(correct: Boolean, run: Int, daily: Boolean) = update { d ->
        val t = today()
        val done = if (d.dailyDay == t) d.dailyDone else 0
        d.touch().copy(
            points = d.points + if (correct) 10 else 0,
            answered = d.answered + 1,
            correct = d.correct + if (correct) 1 else 0,
            bestRun = maxOf(d.bestRun, run),
            dailyDay = t,
            dailyDone = if (daily) (done + 1).coerceAtMost(5) else done,
        )
    }

    fun dailyDoneToday(d: AppData) = if (d.dailyDay == today()) d.dailyDone else 0

    fun openLesson(id: String) = update { d ->
        if (id in d.completed) d else d.copy(progress = d.progress + (id to maxOf(d.progress[id] ?: 0, 60)))
    }

    fun completeLesson(id: String) = update { d ->
        d.touch().copy(completed = d.completed + id, progress = d.progress + (id to 100), points = d.points + 20)
    }

    fun toggleBookmark(id: String) = update { d ->
        d.copy(bookmarks = if (id in d.bookmarks) d.bookmarks - id else d.bookmarks + id)
    }

    fun practiceWin() = update { d -> d.touch().copy(practiceWins = d.practiceWins + 1, points = d.points + 10) }

    fun savePair(p: ColorPair) = update { d -> d.copy(savedPairs = (listOf(p) + d.savedPairs.filter { it != p }).take(12)) }

    // ------------------------------------------------------------ freeform designs
    fun saveDesign(design: Design) = update { d ->
        val u = design.copy(updated = System.currentTimeMillis())
        d.copy(designs = listOf(u) + d.designs.filter { it.id != design.id })
    }

    fun deleteDesign(id: String) = update { d -> d.copy(designs = d.designs.filter { it.id != id }) }

    // ------------------------------------------------------------ block projects
    fun saveProject(p: AppProject) = update { d ->
        val u = p.copy(updated = System.currentTimeMillis())
        val exists = d.projects.any { it.id == p.id }
        d.copy(projects = if (exists) d.projects.map { if (it.id == p.id) u else it } else listOf(u) + d.projects)
    }

    fun updateProject(id: String, f: (AppProject) -> AppProject) = update { d ->
        d.copy(projects = d.projects.map { if (it.id == id) f(it).copy(updated = System.currentTimeMillis()) else it })
    }

    fun deleteProject(id: String) = update { d -> d.copy(projects = d.projects.filter { it.id != id }) }
}
