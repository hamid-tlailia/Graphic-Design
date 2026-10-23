package com.eyn.designtrainer.ui

import android.app.Activity
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.eyn.designtrainer.builder.BlockLibraryScreen
import com.eyn.designtrainer.builder.CanvasEditorScreen
import com.eyn.designtrainer.builder.ExportScreen
import com.eyn.designtrainer.builder.IdentityScreen
import com.eyn.designtrainer.builder.PageBuilderScreen
import com.eyn.designtrainer.data.AppData
import com.eyn.designtrainer.data.AppViewModel
import com.eyn.designtrainer.i18n.Lang
import com.eyn.designtrainer.i18n.tr
import com.eyn.designtrainer.ui.components.bounceClick
import com.eyn.designtrainer.ui.screens.ContrastScreen
import com.eyn.designtrainer.ui.screens.FontLabScreen
import com.eyn.designtrainer.ui.screens.HomeScreen
import com.eyn.designtrainer.ui.screens.LessonScreen
import com.eyn.designtrainer.ui.screens.LessonsScreen
import com.eyn.designtrainer.ui.screens.OnboardingScreen
import com.eyn.designtrainer.ui.screens.PracticeHubScreen
import com.eyn.designtrainer.ui.screens.ProfileScreen
import com.eyn.designtrainer.ui.screens.QuizScreen
import com.eyn.designtrainer.ui.screens.SettingsScreen
import com.eyn.designtrainer.ui.screens.SpacingPracticeScreen
import com.eyn.designtrainer.ui.screens.StudioScreen
import com.eyn.designtrainer.ui.theme.Eyn
import com.eyn.designtrainer.ui.theme.EynTheme
import com.eyn.designtrainer.ui.theme.Type

val LocalData = staticCompositionLocalOf { AppData() }
val LocalVm = staticCompositionLocalOf<AppViewModel> { error("no vm") }
val LocalNav = staticCompositionLocalOf<NavHostController> { error("no nav") }

/**
 * Switches to a bottom-bar tab, always showing that tab's main screen. Tab stacks are not saved
 * and restored: doing so brought back screens opened from elsewhere (e.g. the Studio opened from
 * a lesson reappeared when tapping "Lessons").
 */
fun NavHostController.goTab(route: String) {
    if (currentBackStackEntry?.destination?.route == route) return
    navigate(route) {
        popUpTo(Routes.HOME) { inclusive = route == Routes.HOME }
        launchSingleTop = true
    }
}

object Routes {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val LESSONS = "lessons"
    const val STUDIO = "studio"
    const val PRACTICE = "practice"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
    const val SPACING = "tool/spacing"
    const val CONTRAST = "tool/contrast"
    const val FONTS = "tool/fonts"
    fun lesson(id: String) = "lesson/$id"
    fun quiz(mode: String) = "quiz/$mode"
    fun canvas(id: String) = "canvas/$id"
    fun identity(id: String) = "app/$id/identity"
    fun build(id: String) = "app/$id/build"
    fun library(id: String, pageId: String) = "app/$id/library/$pageId"
    fun export(id: String) = "app/$id/export"
    val tabs = listOf(HOME, LESSONS, STUDIO, PRACTICE, PROFILE)
}

@Composable
fun EynRoot(vm: AppViewModel) {
    val data by vm.data.collectAsStateWithLifecycle()
    val d = data ?: return
    val dark = when (d.theme) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }
    val lang = if (d.lang == "en") Lang.EN else Lang.AR
    CompositionLocalProvider(LocalData provides d, LocalVm provides vm) {
        EynTheme(dark, lang) { AppNav(dark) }
    }
}

private data class Tab(val route: String, val icon: ImageVector, val ar: String, val en: String)

private fun NavBackStackEntry.isTab() = destination.route in Routes.tabs

@Composable
internal fun AppNav(dark: Boolean, startOverride: String? = null) {
    val nav = rememberNavController()
    val d = LocalData.current
    val start = remember { if (startOverride != null) Routes.HOME else if (d.onboarded) Routes.HOME else Routes.ONBOARDING }
    if (startOverride != null && startOverride != Routes.HOME) {
        LaunchedEffect(Unit) { nav.navigate(startOverride) }
    }
    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route
    val c = Eyn.c

    val view = LocalView.current
    val lightBars = !dark && route != Routes.ONBOARDING && route != null
    SideEffect {
        val window = (view.context as? Activity)?.window ?: return@SideEffect
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = lightBars
            isAppearanceLightNavigationBars = !dark
        }
    }

    val dur = 340
    CompositionLocalProvider(LocalNav provides nav) {
        // Status bar padding lives here, outside every scroll container, so content never scrolls
        // underneath the system status bar. Screens' own statusBarsPadding() then becomes a no-op.
        val top by animateColorAsState(if (route == Routes.ONBOARDING) c.hero else c.bg, tween(300), label = "statusbar")
        Column(Modifier.fillMaxSize().background(top).statusBarsPadding().background(c.bg)) {
            NavHost(
                navController = nav,
                startDestination = start,
                modifier = Modifier.weight(1f),
                enterTransition = {
                    if (initialState.isTab() && targetState.isTab()) fadeIn(tween(300)) + slideInVertically(tween(300, easing = FastOutSlowInEasing)) { it / 30 }
                    else slideIntoContainer(SlideDirection.Start, tween(dur, easing = FastOutSlowInEasing)) { it / 3 } + fadeIn(tween(dur)) +
                        scaleIn(tween(dur, easing = FastOutSlowInEasing), initialScale = 0.97f)
                },
                exitTransition = {
                    if (initialState.isTab() && targetState.isTab()) fadeOut(tween(180))
                    else slideOutOfContainer(SlideDirection.Start, tween(dur, easing = FastOutSlowInEasing)) { it / 5 } + fadeOut(tween(dur))
                },
                popEnterTransition = {
                    if (initialState.isTab() && targetState.isTab()) fadeIn(tween(260))
                    else slideIntoContainer(SlideDirection.End, tween(dur, easing = FastOutSlowInEasing)) { it / 5 } + fadeIn(tween(dur))
                },
                popExitTransition = {
                    if (initialState.isTab() && targetState.isTab()) fadeOut(tween(180))
                    else slideOutOfContainer(SlideDirection.End, tween(dur, easing = FastOutSlowInEasing)) { it / 3 } + fadeOut(tween(dur)) +
                        scaleOut(tween(dur, easing = FastOutSlowInEasing), targetScale = 0.97f)
                },
            ) {
                composable(Routes.ONBOARDING) { OnboardingScreen() }
                composable(Routes.HOME) { HomeScreen() }
                composable(Routes.LESSONS) { LessonsScreen() }
                composable(Routes.STUDIO) { StudioScreen() }
                composable(Routes.PRACTICE) { PracticeHubScreen() }
                composable(Routes.PROFILE) { ProfileScreen() }
                composable(Routes.SETTINGS) { SettingsScreen() }
                composable(Routes.SPACING) { SpacingPracticeScreen() }
                composable(Routes.CONTRAST) { ContrastScreen() }
                composable(Routes.FONTS) { FontLabScreen() }
                composable("lesson/{id}") { LessonScreen(it.arguments?.getString("id") ?: "") }
                composable("quiz/{mode}") { QuizScreen(it.arguments?.getString("mode") ?: "daily") }
                composable("canvas/{id}") { CanvasEditorScreen(it.arguments?.getString("id") ?: "") }
                composable("app/{id}/identity") { IdentityScreen(it.arguments?.getString("id") ?: "") }
                composable("app/{id}/build") { PageBuilderScreen(it.arguments?.getString("id") ?: "") }
                composable("app/{id}/library/{page}") {
                    BlockLibraryScreen(it.arguments?.getString("id") ?: "", it.arguments?.getString("page") ?: "")
                }
                composable("app/{id}/export") { ExportScreen(it.arguments?.getString("id") ?: "") }
            }
            AnimatedVisibility(
                visible = route in Routes.tabs,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut(),
            ) {
                BottomBar(route) { target -> nav.goTab(target) }
            }
        }
    }
}

@Composable
private fun BottomBar(current: String?, onSelect: (String) -> Unit) {
    val c = Eyn.c
    val tabs = listOf(
        Tab(Routes.HOME, Icons.Rounded.Home, "الرئيسية", "Home"),
        Tab(Routes.LESSONS, Icons.AutoMirrored.Rounded.MenuBook, "الدروس", "Lessons"),
        Tab(Routes.STUDIO, Icons.Rounded.Dashboard, "الاستوديو", "Studio"),
        Tab(Routes.PRACTICE, Icons.Rounded.Draw, "تمارين", "Practice"),
        Tab(Routes.PROFILE, Icons.Rounded.Person, "حسابي", "Profile"),
    )
    Column(
        Modifier
            .fillMaxWidth()
            .background(c.surface)
            .border(width = 1.dp, color = c.outline, shape = androidx.compose.ui.graphics.RectangleShape)
            .navigationBarsPadding()
            .padding(top = 8.dp, bottom = 6.dp),
    ) {
        Row(Modifier.fillMaxWidth()) {
            tabs.forEach { t ->
                val on = t.route == current
                val fg by animateColorAsState(if (on) c.primary else c.muted, tween(250), label = "tab")
                val pill by animateColorAsState(if (on) c.lilac else c.surface, tween(250), label = "pill")
                Column(
                    Modifier
                        .weight(1f)
                        .bounceClick(scaleDown = 0.9f) { onSelect(t.route) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        Modifier.clip(CircleShape).background(pill).padding(horizontal = 16.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center,
                    ) { Icon(t.icon, null, tint = fg, modifier = Modifier.size(22.dp)) }
                    Spacer(Modifier.height(2.dp))
                    Text(
                        tr(t.ar, t.en),
                        style = Type.small.copy(fontWeight = if (on) FontWeight.SemiBold else FontWeight.Normal),
                        color = fg,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    )
                }
            }
        }
        Spacer(Modifier.width(0.dp))
    }
}
