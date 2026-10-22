package com.eyn.designtrainer.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eyn.designtrainer.i18n.isArabic
import com.eyn.designtrainer.i18n.tr
import com.eyn.designtrainer.ui.LocalData
import com.eyn.designtrainer.ui.LocalNav
import com.eyn.designtrainer.ui.LocalVm
import com.eyn.designtrainer.ui.Routes
import com.eyn.designtrainer.ui.components.AnimatedDots
import com.eyn.designtrainer.ui.components.EyeLogo
import com.eyn.designtrainer.ui.components.PrimaryButton
import com.eyn.designtrainer.ui.components.bounceClick
import com.eyn.designtrainer.ui.components.dotPattern
import com.eyn.designtrainer.ui.theme.Eyn
import com.eyn.designtrainer.ui.theme.Navy
import com.eyn.designtrainer.ui.theme.Type
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen() {
    val c = Eyn.c
    val vm = LocalVm.current
    val nav = LocalNav.current
    val d = LocalData.current
    val pager = rememberPagerState { 3 }
    val scope = rememberCoroutineScope()
    var name by rememberSaveable { mutableStateOf(d.name) }
    val ar = isArabic

    val float = rememberInfiniteTransition(label = "float")
    val bob by float.animateFloat(-6f, 6f, infiniteRepeatable(tween(1800), RepeatMode.Reverse), label = "bob")

    Box(
        Modifier
            .fillMaxSize()
            .background(c.hero)
            .dotPattern(Color.White.copy(alpha = 0.07f))
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
    ) {
        Row(
            Modifier
                .align(Alignment.TopEnd)
                .padding(20.dp)
                .bounceClick { vm.setLang(if (ar) "en" else "ar") }
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.12f))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Rounded.Language, null, tint = Color.White, modifier = Modifier.size(16.dp))
            Spacer(Modifier.size(6.dp))
            Text(if (ar) "English" else "العربية", style = Type.label, color = Color.White)
        }

        Column(Modifier.fillMaxSize()) {
            HorizontalPager(pager, Modifier.weight(1f)) { page ->
                Column(
                    Modifier.fillMaxSize().padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Box(
                        Modifier
                            .graphicsLayer { translationY = bob * density }
                            .size(124.dp)
                            .shadow(24.dp, RoundedCornerShape(32.dp))
                            .clip(RoundedCornerShape(32.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center,
                    ) {
                        when (page) {
                            0 -> EyeLogo(Modifier.size(78.dp, 52.dp), stroke = Navy)
                            1 -> Icon(Icons.AutoMirrored.Rounded.MenuBook, null, tint = Navy, modifier = Modifier.size(56.dp))
                            else -> Icon(Icons.Rounded.Dashboard, null, tint = Navy, modifier = Modifier.size(56.dp))
                        }
                    }
                    Spacer(Modifier.height(36.dp))
                    Text(
                        when (page) {
                            0 -> tr("عين", "Eyn")
                            1 -> tr("تعلّم بالممارسة", "Learn by doing")
                            else -> tr("ابنِ تصاميمك", "Build your designs")
                        },
                        style = Type.h1.copy(fontSize = if (page == 0) 56.sp else 30.sp, lineHeight = if (page == 0) 64.sp else 40.sp),
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        when (page) {
                            0 -> tr("درّب عين المصمم فيك\nدقيقة واحدة كل يوم", "Train the designer's eye in you,\none minute a day")
                            1 -> tr("دروس قصيرة وتحديات يومية: أيهما أفضل؟ ولماذا؟", "Short lessons and daily challenges: which is better, and why?")
                            else -> tr("اختر قطعاً جاهزة مثل الهيدر والفوتر وصفحات الدخول، ثم عدّل الألوان والنصوص والأحجام والمواضع.", "Pick ready blocks like headers, footers and login pages, then tweak colors, text, sizes and positions.")
                        },
                        style = Type.body.copy(fontSize = 16.sp, lineHeight = 28.sp),
                        color = c.heroMuted,
                        textAlign = TextAlign.Center,
                    )
                    if (page == 2) {
                        Spacer(Modifier.height(24.dp))
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it.take(24) },
                            singleLine = true,
                            placeholder = { Text(tr("ما اسمك؟ (اختياري)", "Your name (optional)"), color = Color.White.copy(alpha = 0.6f)) },
                            shape = RoundedCornerShape(16.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                focusedBorderColor = Color.White, unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                cursorColor = Color.White,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
            Column(Modifier.padding(horizontal = 24.dp, vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                val last = pager.currentPage == 2
                PrimaryButton(
                    text = if (last) tr("ابدأ الآن", "Start now") else tr("التالي", "Next"),
                    container = Color.White,
                    content = Navy,
                ) {
                    if (last) {
                        vm.finishOnboarding(name)
                        nav.navigate(Routes.HOME) { popUpTo(Routes.ONBOARDING) { inclusive = true } }
                    } else scope.launch { pager.animateScrollToPage(pager.currentPage + 1) }
                }
                Spacer(Modifier.height(18.dp))
                AnimatedDots(3, pager.currentPage, Color.White, Color.White.copy(alpha = 0.3f))
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}
