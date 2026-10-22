# عين · Eyn — Design Trainer

تطبيق أندرويد لتعليم التصميم الغرافيكي: «درّب عينك، صمّم بثقة».
An Android app that teaches graphic design, plus a builder for ready-made UI blocks and templates.

**Kotlin · Jetpack Compose · Material 3 · Arabic (RTL) & English · Auto / light / dark theme**

📦 Ready-to-install APK: [`release/eyn-1.1.0.apk`](release/eyn-1.1.0.apk)

| Home | Challenge | Lesson | Studio | Page builder | Canvas editor |
|---|---|---|---|---|---|
| ![](docs/screens/02_home.jpg) | ![](docs/screens/03_quiz.jpg) | ![](docs/screens/06_lesson.jpg) | ![](docs/screens/16_studio.jpg) | ![](docs/screens/13_build.jpg) | ![](docs/screens/17_canvas.jpg) |

| Identity | Block library | Export | Contrast | Dark | English |
|---|---|---|---|---|---|
| ![](docs/screens/11_identity.jpg) | ![](docs/screens/12_library.jpg) | ![](docs/screens/15_export.jpg) | ![](docs/screens/08_contrast.jpg) | ![](docs/screens/20_home_dark.jpg) | ![](docs/screens/22_canvas_en_dark.jpg) |

## الميزات · Features

### التعلّم · Learning (screens 01–10)
- **Onboarding**: three pages, with a language switch and an optional name.
- **Home**: greeting, day streak 🔥, a daily challenge of 5 questions that stays the same all day, and sections (spacing, colors, typography, alignment).
- **"Which is better?" challenges**: visual answers, an explanation after each one (why it's better, tags, a related lesson), +10 points, and a results screen.
- **Lessons**: 10 lessons with filters, search, progress, unlocking and bookmarks. Each lesson has an animated illustration, key points, "Try it" and "Quiz me".
- **Practice tools**: spacing trainer (padding/gap/alignment with checking), WCAG contrast checker, and a font lab (IBM Plex Sans Arabic, Cairo, Tajawal).
- **Profile**: level, points, accuracy, streak, weekly activity chart and badges. **Settings**: theme (auto/light/dark), language, name, reset progress.

### وضع البناء بالقطع · Block build mode (screens 11–15)
1. **Identity**: app name, logo style (icon + text / letter / text only), color palette, font.
2. **Block library**: header, hero, search, categories, cards, lists, text, forms, buttons and footer, with several variants each. Multi-select.
3. **Page builder**: multiple pages, drag-to-reorder blocks, duplicate, delete, move up/down, preview mode.
4. **Edit block**: text, variant, background color, alignment, inner spacing, corner radius, with a live preview.
5. **Export**: presentation board PNG + PDF, a PNG per page, HTML/CSS code and React Native code.

### القوالب الحرة · Freeform templates
Ready templates: buttons, cards, headers, footers, login/sign-up, landing pages and pricing. Or start from a blank canvas (phone, post, story, cover, A4).

| All elements | Layout | Style | Content |
|---|---|---|---|
| ![](docs/screens/24_canvas_all_types.jpg) | ![](docs/screens/25_canvas_layout.jpg) | ![](docs/screens/26_canvas_style.jpg) | ![](docs/screens/27_canvas_content.jpg) |

- **17 elements**: shape, card, text, button, chip, input, search bar, image, avatar, circle, icon, divider, checkbox, toggle, slider, progress bar, star rating.
- **Precise controls**: − / + steppers (hold to repeat) with an editable value for width, height, X, Y, rotation, font size, padding, radius, border, shadow, opacity and more, plus a slider for coarse moves.
- **Alignment**: drag snaps to the canvas and to other elements' edges and centers with pink guides. One tap aligns or **centers an element inside the element beneath it** (or the canvas), or makes it fill that container.
- **Content and text**: font (IBM Plex / Cairo / Tajawal), bold, italic, underline, horizontal and vertical alignment, horizontal/vertical padding, line height, letter spacing.
- **Style**: fill, gradient (3 directions), text/icon color, border, corner radius, shadow, opacity, copy/paste style.
- **Real photos** from the phone (system photo picker, no permission needed) inside images, avatars, shapes, cards, buttons and the canvas background, with fill/fit modes.
- **All 2,100+ Material icons** with search in Arabic and English.
- Rotation, lock, show/hide, layers, z-order (forward, backward, to front, to back), duplicate, undo/redo, zoom.
- The properties panel stays above the keyboard while you type.
- Export: save to the gallery or share as PNG.

Arabic templates are mirrored automatically for right-to-left reading.

## البناء · Build

Requires JDK 17+ and the Android SDK (compileSdk 35, minSdk 26).

```bash
./gradlew assembleDebug        # app/build/outputs/apk/debug/app-debug.apk
./gradlew assembleRelease      # shrunk with R8, signed with the debug key — use your own keystore for Play
./gradlew testDebugUnitTest    # renders every screen headlessly (Robolectric) to app/build/screens/
```

## البنية · Structure

```
app/src/main/java/com/eyn/designtrainer/
├─ data/        AppData model, ViewModel (in-memory state persisted to DataStore), lessons & questions
├─ i18n/        Arabic/English strings helper
├─ ui/          navigation, theme (colors/type/animated dark mode), components, learning screens
└─ builder/     freeform canvas (templates, renderer, editor), block builder (identity, library,
                page builder, export), code generation, PNG/PDF export
```

Fonts: IBM Plex Sans Arabic, Cairo, Tajawal — SIL Open Font License.
