package com.eyn.designtrainer.data

import com.eyn.designtrainer.i18n.L

enum class Cat(val label: L) {
    BASICS(L("الأساسيات", "Basics")),
    SPACING(L("المسافات", "Spacing")),
    COLORS(L("الألوان", "Colors")),
    TYPE(L("الخطوط", "Typography")),
    ALIGN(L("المحاذاة", "Alignment")),
    IMAGERY(L("الصور", "Imagery")),
    BRANDING(L("الهوية", "Branding")),
    UX(L("الواجهات", "UI/UX")),
    PRINT(L("الطباعة", "Print")),
}

enum class Ill {
    SPACING_BARS, HIERARCHY, COLOR_RULE, ALIGN_GRID, ARABIC_TYPE, CONTRAST, PROXIMITY, WHITESPACE, TYPE_SCALE, BALANCE,
    ELEMENTS, COLUMNS, WHEEL, UI_KIT, VECTOR, LOGO_TYPES, PRINT_MARKS,
}

/** Where "Try it" sends the learner. */
enum class Practice { SPACING, CONTRAST, FONTS, STUDIO }

data class Lesson(
    val id: String,
    val cat: Cat,
    val minutes: Int,
    val exercises: Int,
    val ill: Ill,
    val title: L,
    val body: L,
    val points: List<L>,
    val practice: Practice,
)

/** Mini visual rendered inside a quiz option. `good` = the well-designed variant. */
enum class Vis { SPACING, CONTRAST, ALIGN, HIERARCHY, COLOR_RATIO, BUTTONS, PROXIMITY }

data class Option(val text: L? = null, val vis: Vis? = null, val good: Boolean = false)

data class Question(
    val id: String,
    val cat: Cat,
    val q: L,
    val hint: L,
    val options: List<Option>,
    val correct: Int,
    val why: L,
    val tags: List<L> = emptyList(),
    val lessonId: String,
)

object Content {
    private val baseLessons = listOf(
        Lesson(
            "grid8", Cat.SPACING, 5, 4, Ill.SPACING_BARS,
            L("نظام الـ 8 بكسل", "The 8px grid"),
            L(
                "اجعل كل المسافات والأحجام من مضاعفات الرقم 8. هذا يمنح التصميم إيقاعاً ثابتاً ويختصر عليك قرارات المسافة، فبدلاً من التردد بين 13 و15 و17 تختار من سلّم واضح: 8، 16، 24، 32، 48.",
                "Make every spacing and size a multiple of 8. It gives your design a steady rhythm and removes guesswork: instead of hesitating between 13, 15 and 17 you pick from a clear scale: 8, 16, 24, 32, 48.",
            ),
            listOf(
                L("4 للتفاصيل الدقيقة فقط", "4 for fine details only"),
                L("16 حشوة البطاقات الافتراضية", "16 is the default card padding"),
                L("24 إلى 32 بين الأقسام", "24 to 32 between sections"),
            ),
            Practice.SPACING,
        ),
        Lesson(
            "hierarchy", Cat.BASICS, 7, 5, Ill.HIERARCHY,
            L("التسلسل البصري", "Visual hierarchy"),
            L(
                "التسلسل البصري يخبر العين بما تقرأه أولاً. استخدم الحجم والوزن واللون والمسافة لتمييز العنصر الأهم، ثم الأقل أهمية. إذا كان كل شيء بارزاً فلا شيء بارز.",
                "Hierarchy tells the eye what to read first. Use size, weight, color and spacing to make the most important element stand out, then the next. If everything is loud, nothing is.",
            ),
            listOf(
                L("عنوان واحد واضح لكل شاشة", "One clear headline per screen"),
                L("فرق حجم ملحوظ بين المستويات (1.25× على الأقل)", "A noticeable size step between levels (at least 1.25×)"),
                L("زر أساسي واحد ممتلئ، والبقية ثانوية", "One filled primary button, the rest secondary"),
            ),
            Practice.STUDIO,
        ),
        Lesson(
            "rule603010", Cat.COLORS, 7, 5, Ill.COLOR_RULE,
            L("قاعدة 60-30-10 للألوان", "The 60-30-10 color rule"),
            L(
                "وزّع ألوانك بنسبة 60% للون المحايد الأساسي (الخلفيات)، و30% للون الثانوي (البطاقات والأقسام)، و10% للون التمييز الذي يجذب الانتباه للأزرار والتنبيهات.",
                "Split your colors 60% neutral base (backgrounds), 30% secondary (cards and sections), and 10% accent that pulls attention to buttons and highlights.",
            ),
            listOf(
                L("60% خلفية هادئة", "60% calm background"),
                L("30% لون العلامة الثانوي", "30% secondary brand color"),
                L("10% لون التمييز للأفعال المهمة", "10% accent for key actions"),
            ),
            Practice.CONTRAST,
        ),
        Lesson(
            "alignment", Cat.ALIGN, 6, 4, Ill.ALIGN_GRID,
            L("المحاذاة والشبكة", "Alignment & grids"),
            L(
                "كل عنصر يجب أن يرتبط بصرياً بعنصر آخر. المحاذاة على خطوط مشتركة تجعل الواجهة مرتبة وسهلة المسح بالعين، والشبكة تمنحك هذه الخطوط مجاناً.",
                "Every element should visually connect to another. Aligning to shared edges makes an interface tidy and easy to scan, and a grid gives you those edges for free.",
            ),
            listOf(
                L("اختر حافة محاذاة واحدة لكل مجموعة", "Pick one alignment edge per group"),
                L("في العربية: المحاذاة لليمين للنصوص", "In Arabic: right-align body text"),
                L("استخدم 4 أعمدة على الجوال", "Use a 4-column grid on mobile"),
            ),
            Practice.STUDIO,
        ),
        Lesson(
            "arabictype", Cat.TYPE, 6, 4, Ill.ARABIC_TYPE,
            L("الخطوط العربية", "Arabic typography"),
            L(
                "الحروف العربية تحتاج ارتفاع سطر أكبر من اللاتينية بسبب الصواعد والنوازل والتشكيل. اختر خطاً مصمماً للشاشات مثل IBM Plex Sans Arabic أو Cairo أو Tajawal.",
                "Arabic letters need more line height than Latin because of ascenders, descenders and diacritics. Pick a typeface designed for screens like IBM Plex Sans Arabic, Cairo or Tajawal.",
            ),
            listOf(
                L("ارتفاع سطر 1.6 إلى 1.8", "Line height 1.6 to 1.8"),
                L("لا تقل عن 14 للنص الأساسي", "Never below 14 for body text"),
                L("تجنب الخطوط الزخرفية في الواجهات", "Avoid decorative fonts in UI"),
            ),
            Practice.FONTS,
        ),
        Lesson(
            "contrast", Cat.COLORS, 5, 3, Ill.CONTRAST,
            L("التباين وسهولة القراءة", "Contrast & readability"),
            L(
                "التباين هو الفرق في السطوع بين النص وخلفيته. معيار WCAG يطلب 4.5:1 على الأقل للنص العادي و3:1 للنص الكبير. التباين الجيد يخدم الجميع، وليس فقط ضعاف البصر.",
                "Contrast is the brightness difference between text and its background. WCAG asks for at least 4.5:1 for normal text and 3:1 for large text. Good contrast serves everyone, not only people with low vision.",
            ),
            listOf(
                L("4.5:1 للنص العادي (AA)", "4.5:1 for normal text (AA)"),
                L("7:1 للمستوى الأعلى (AAA)", "7:1 for the highest level (AAA)"),
                L("الرمادي الفاتح على الأبيض نادراً ما ينجح", "Light gray on white rarely passes"),
            ),
            Practice.CONTRAST,
        ),
        Lesson(
            "proximity", Cat.BASICS, 4, 3, Ill.PROXIMITY,
            L("قانون التقارب", "The law of proximity"),
            L(
                "العناصر المتقاربة تُفهم كمجموعة واحدة. ضع العنوان قريباً من الحقل الذي يصفه، وابعد المجموعات المختلفة عن بعضها لتظهر العلاقات دون خطوط أو صناديق.",
                "Items placed close together are read as one group. Put a label close to the field it describes and push unrelated groups apart so relationships show without lines or boxes.",
            ),
            listOf(
                L("المسافة داخل المجموعة أصغر من خارجها", "Space inside a group is smaller than between groups"),
                L("العنوان أقرب لحقله من الحقل السابق", "A label sits closer to its field than to the previous one"),
                L("قلّل الخطوط الفاصلة واستخدم المسافة", "Use space instead of dividers"),
            ),
            Practice.SPACING,
        ),
        Lesson(
            "whitespace", Cat.SPACING, 5, 3, Ill.WHITESPACE,
            L("المساحات البيضاء", "White space"),
            L(
                "المساحة الفارغة ليست مساحة ضائعة. إنها تمنح المحتوى تنفساً وتوجّه الانتباه وتعطي إحساساً بالفخامة. التصميم المزدحم يرهق العين ويضيّع الرسالة.",
                "Empty space is not wasted space. It lets content breathe, directs attention and feels premium. A crowded layout tires the eye and buries the message.",
            ),
            listOf(
                L("هوامش الشاشة 16 إلى 24", "Screen margins 16 to 24"),
                L("مساحة أكبر حول العنصر الأهم", "More space around the key element"),
                L("احذف قبل أن تضيف", "Remove before you add"),
            ),
            Practice.SPACING,
        ),
        Lesson(
            "typescale", Cat.TYPE, 6, 4, Ill.TYPE_SCALE,
            L("مقياس الخطوط", "Type scale"),
            L(
                "اختر مجموعة صغيرة من الأحجام مرتبطة بنسبة ثابتة (مثل 1.25) بدلاً من أحجام عشوائية. مثلاً: 12، 14، 16، 20، 24، 32. هذا يصنع تسلسلاً متناسقاً.",
                "Choose a small set of sizes tied by a fixed ratio (like 1.25) instead of random sizes. For example: 12, 14, 16, 20, 24, 32. It creates a consistent hierarchy.",
            ),
            listOf(
                L("عائلة أو عائلتا خطوط فقط", "One or two font families only"),
                L("3 إلى 5 أحجام تكفي لمعظم الشاشات", "3 to 5 sizes cover most screens"),
                L("الوزن يصنع فرقاً دون تغيير الحجم", "Weight creates contrast without changing size"),
            ),
            Practice.FONTS,
        ),
        Lesson(
            "balance", Cat.ALIGN, 5, 3, Ill.BALANCE,
            L("التوازن البصري", "Visual balance"),
            L(
                "التوازن هو توزيع الوزن البصري في المساحة. العنصر الكبير أو الداكن أثقل من الصغير أو الفاتح. وازن بينهما بالتماثل أو بتوزيع غير متماثل مدروس.",
                "Balance is how visual weight is spread across the space. Large or dark items weigh more than small or light ones. Balance them with symmetry or a deliberate asymmetric layout.",
            ),
            listOf(
                L("التماثل يعطي هدوءاً ورسمية", "Symmetry feels calm and formal"),
                L("اللاتماثل يعطي حيوية وحركة", "Asymmetry feels lively"),
                L("عنصر صغير مشبع يوازن عنصراً كبيراً هادئاً", "A small saturated item balances a large calm one"),
            ),
            Practice.STUDIO,
        ),
    )

    /** Every lesson in learning-path order (unit by unit). */
    val lessons: List<Lesson> by lazy {
        val all = baseLessons + Path.extraLessons
        Path.units.flatMap { u -> u.lessonIds.map { id -> all.first { it.id == id } } }
    }

    fun lesson(id: String) = lessons.first { it.id == id }

    private fun two(goodFirst: Boolean, vis: Vis) =
        if (goodFirst) listOf(Option(vis = vis, good = true), Option(vis = vis, good = false))
        else listOf(Option(vis = vis, good = false), Option(vis = vis, good = true))

    private fun texts(vararg t: L) = t.map { Option(text = it) }

    private val baseQuestions = listOf(
        // ---------------------------------------------------- spacing
        Question(
            "s1", Cat.SPACING,
            L("أي بطاقة مسافاتها أكثر توازناً؟", "Which card has more balanced spacing?"),
            L("لاحظ الحشوة الداخلية والفجوات بين العناصر", "Notice the padding and gaps between elements"),
            two(true, Vis.SPACING), 0,
            L(
                "المسافات في (أ) مبنية على نظام 8: حشوة متساوية وفجوة ثابتة بين الصورة والنص، فتبدو العناصر مترابطة ومريحة. في (ب) الحشوة غير متساوية والعناصر ملتصقة.",
                "Spacing in (A) follows the 8px system: even padding and a fixed gap between image and text, so elements feel connected and calm. In (B) padding is uneven and items are cramped.",
            ),
            listOf(L("16 حشوة", "16 padding"), L("12 فجوة", "12 gap"), L("8 بين السطور", "8 line gap")),
            "grid8",
        ),
        Question(
            "s2", Cat.SPACING,
            L("أي قيمة تناسب نظام الـ 8 بكسل؟", "Which value fits the 8px grid?"),
            L("ابحث عن مضاعفات الرقم 8", "Look for a multiple of 8"),
            texts(L("13px", "13px"), L("24px", "24px"), L("18px", "18px")), 1,
            L("24 = 8 × 3، لذا تنسجم مع بقية المسافات. القيم 13 و18 تكسر الإيقاع.", "24 = 8 × 3, so it fits with every other spacing. 13 and 18 break the rhythm."),
            listOf(L("8 × 3", "8 × 3")), "grid8",
        ),
        Question(
            "s3", Cat.SPACING,
            L("ما المسافة المناسبة بين الأقسام الكبيرة؟", "What's a good gap between major sections?"),
            L("الأقسام تحتاج فصلاً أوضح من العناصر", "Sections need clearer separation than items"),
            texts(L("4px", "4px"), L("8px", "8px"), L("32px", "32px")), 2,
            L("المسافات الكبيرة (24–32) بين الأقسام تصنع مجموعات واضحة دون الحاجة لخطوط فاصلة.", "Large gaps (24–32) between sections create clear groups without needing dividers."),
            listOf(L("24–32 للأقسام", "24–32 for sections")), "whitespace",
        ),
        Question(
            "s4", Cat.SPACING,
            L("الحشوة الداخلية للبطاقة يجب أن تكون…", "A card's inner padding should be…"),
            L("فكّر في قانون التقارب", "Think about proximity"),
            texts(
                L("أكبر من الفجوة بين عناصرها", "Larger than the gaps between its items"),
                L("أصغر من الفجوة بين عناصرها", "Smaller than the gaps between its items"),
                L("لا علاقة بينهما", "Unrelated"),
            ), 0,
            L("عندما تكون الحشوة أكبر من الفجوات الداخلية تبدو العناصر مجموعة واحدة محمية داخل البطاقة.", "When padding is larger than inner gaps, the items read as one group held inside the card."),
            listOf(L("حشوة > فجوة", "padding > gap")), "proximity",
        ),
        // ---------------------------------------------------- colors
        Question(
            "c1", Cat.COLORS,
            L("أي نص أسهل في القراءة؟", "Which text is easier to read?"),
            L("قارن الفرق بين لون النص والخلفية", "Compare text color against background"),
            two(true, Vis.CONTRAST), 0,
            L("الحبري على الخلفية الدافئة يحقق تبايناً يتجاوز 12:1، بينما الرمادي الفاتح على الأبيض أقل من 2.5:1 ولا يجتاز المعيار.", "Navy on the warm background reaches over 12:1, while light gray on white is under 2.5:1 and fails."),
            listOf(L("12.3 : 1", "12.3 : 1"), L("AAA", "AAA")), "contrast",
        ),
        Question(
            "c2", Cat.COLORS,
            L("أي توزيع ألوان أكثر انسجاماً؟", "Which color split feels more harmonious?"),
            L("تذكّر قاعدة 60-30-10", "Remember 60-30-10"),
            two(false, Vis.COLOR_RATIO), 1,
            L("(ب) يتبع 60-30-10: لون هادئ يسيطر، ولون ثانوي يدعم، ولمسة تمييز صغيرة. (أ) يقسم الألوان بالتساوي فتتنافس.", "(B) follows 60-30-10: a calm dominant color, a supporting secondary and a small accent. (A) splits colors evenly so they compete."),
            listOf(L("60%", "60%"), L("30%", "30%"), L("10%", "10%")), "rule603010",
        ),
        Question(
            "c3", Cat.COLORS,
            L("ما الحد الأدنى للتباين للنص العادي (AA)؟", "Minimum contrast for normal text (AA)?"),
            L("معيار WCAG", "The WCAG standard"),
            texts(L("3 : 1", "3 : 1"), L("4.5 : 1", "4.5 : 1"), L("7 : 1", "7 : 1")), 1,
            L("4.5:1 للنص العادي، و3:1 للنص الكبير (18+ أو 14+ عريض)، و7:1 للمستوى AAA.", "4.5:1 for normal text, 3:1 for large text (18+ or 14+ bold), and 7:1 for AAA."),
            listOf(L("AA", "AA")), "contrast",
        ),
        Question(
            "c4", Cat.COLORS,
            L("في قاعدة 60-30-10، لمن نخصص 10%؟", "In 60-30-10, what gets the 10%?"),
            L("اللون الذي يجب أن يلفت الانتباه", "The color that should grab attention"),
            texts(
                L("الخلفية", "The background"),
                L("اللون الثانوي", "The secondary color"),
                L("لون التمييز للأزرار والتنبيهات", "The accent for buttons and highlights"),
            ), 2,
            L("لون التمييز يعمل لأنه نادر. إذا استُخدم بكثرة فقد قدرته على جذب العين.", "The accent works because it's rare. Overuse it and it stops drawing the eye."),
            listOf(L("10% تمييز", "10% accent")), "rule603010",
        ),
        // ---------------------------------------------------- typography
        Question(
            "t1", Cat.TYPE,
            L("أي بطاقة لديها تسلسل خطوط أوضح؟", "Which card has clearer type hierarchy?"),
            L("هل تعرف ماذا تقرأ أولاً؟", "Can you tell what to read first?"),
            two(true, Vis.HIERARCHY), 0,
            L("في (أ) العنوان أكبر وأثقل والنص الوصفي أصغر وأفتح، فتعرف العين الترتيب فوراً.", "In (A) the title is bigger and bolder while the description is smaller and lighter, so the eye knows the order instantly."),
            listOf(L("24 عنوان", "24 title"), L("14 نص", "14 body")), "typescale",
        ),
        Question(
            "t2", Cat.TYPE,
            L("ما الطول المريح لسطر النص؟", "What's a comfortable line length?"),
            L("عدد الحروف في السطر الواحد", "Characters per line"),
            texts(L("20–30 حرفاً", "20–30 characters"), L("45–75 حرفاً", "45–75 characters"), L("أكثر من 120 حرفاً", "120+ characters")), 1,
            L("الأسطر القصيرة جداً تقطع القراءة، والطويلة جداً تُتعب العين عند العودة لبداية السطر التالي.", "Very short lines break reading; very long ones tire the eye when jumping back to the next line."),
            listOf(L("45–75", "45–75")), "typescale",
        ),
        Question(
            "t3", Cat.TYPE,
            L("ارتفاع السطر المناسب للنصوص العربية الطويلة؟", "Good line height for long Arabic text?"),
            L("الحروف العربية لها صواعد ونوازل", "Arabic has tall ascenders and descenders"),
            texts(L("1.0", "1.0"), L("1.6–1.8", "1.6–1.8"), L("3.0", "3.0")), 1,
            L("ارتفاع 1.6–1.8 يمنح التشكيل والنقاط مساحة كافية دون أن تتباعد الأسطر.", "1.6–1.8 gives dots and diacritics room without lines drifting apart."),
            listOf(L("1.7×", "1.7×")), "arabictype",
        ),
        Question(
            "t4", Cat.TYPE,
            L("كم عائلة خطوط تكفي عادةً في الواجهة؟", "How many font families usually suffice?"),
            L("الاتساق أهم من التنوع", "Consistency beats variety"),
            texts(L("1–2", "1–2"), L("4–5", "4–5"), L("6+", "6+")), 0,
            L("عائلة واحدة بأوزان متعددة أو عائلتان متكاملتان تكفي. المزيد يشتت ويبدو عشوائياً.", "One family with several weights, or two complementary ones, is enough. More looks random."),
            listOf(L("عائلة + أوزان", "family + weights")), "typescale",
        ),
        // ---------------------------------------------------- alignment
        Question(
            "a1", Cat.ALIGN,
            L("أي قائمة محاذاتها أنظف؟", "Which list is aligned more cleanly?"),
            L("تتبّع الحافة التي تبدأ منها العناصر", "Follow the edge items start from"),
            two(false, Vis.ALIGN), 1,
            L("في (ب) كل العناصر تبدأ من الحافة نفسها، فتمسحها العين بخط مستقيم. في (أ) البدايات مبعثرة.", "In (B) every item starts from the same edge so the eye scans in a straight line. In (A) starts are scattered."),
            listOf(L("حافة واحدة", "one edge")), "alignment",
        ),
        Question(
            "a2", Cat.ALIGN,
            L("في واجهة عربية، يُحاذى النص الأساسي إلى…", "In an Arabic UI, body text aligns to…"),
            L("اتجاه القراءة", "Reading direction"),
            texts(L("اليسار", "Left"), L("اليمين", "Right"), L("الوسط", "Center")), 1,
            L("العربية تُقرأ من اليمين، لذا تبدأ الأسطر من الحافة اليمنى. التوسيط يناسب العناوين القصيرة فقط.", "Arabic reads right to left so lines start at the right edge. Centering suits short headings only."),
            listOf(L("RTL", "RTL")), "alignment",
        ),
        Question(
            "a3", Cat.ALIGN,
            L("ما فائدة نظام الشبكة؟", "What does a grid give you?"),
            L("فكّر في الإيقاع", "Think rhythm"),
            texts(
                L("الاتساق والإيقاع بين العناصر", "Consistency and rhythm between elements"),
                L("ألوان أكثر", "More colors"),
                L("خطوط أكبر", "Bigger fonts"),
            ), 0,
            L("الشبكة توفر خطوط محاذاة مشتركة، فتتوزع العناصر بإيقاع ثابت وتسهل القرارات.", "A grid gives shared alignment lines so elements fall into a steady rhythm and decisions get easier."),
            listOf(L("4 أعمدة", "4 columns")), "balance",
        ),
        // ---------------------------------------------------- basics
        Question(
            "b1", Cat.BASICS,
            L("أي زر أساسي أوضح؟", "Which primary action is clearer?"),
            L("هل تعرف أي زر هو الأهم؟", "Can you tell which button matters most?"),
            two(true, Vis.BUTTONS), 0,
            L("في (أ) زر واحد ممتلئ للفعل الأساسي وزر ثانوي بحدود فقط. في (ب) الزران متطابقان فيتردد المستخدم.", "(A) has one filled primary and an outlined secondary. In (B) both look identical so users hesitate."),
            listOf(L("زر أساسي واحد", "one primary")), "hierarchy",
        ),
        Question(
            "b2", Cat.BASICS,
            L("أي نموذج يربط العناوين بحقولها بوضوح؟", "Which form links labels to fields clearly?"),
            L("قانون التقارب", "The law of proximity"),
            two(false, Vis.PROXIMITY), 1,
            L("في (ب) العنوان قريب من حقله وبعيد عن الحقل السابق، فتظهر كل مجموعة بوضوح.", "In (B) each label sits close to its field and far from the previous one, so groups are obvious."),
            listOf(L("4 عنوان↔حقل", "4 label↔field"), L("20 بين المجموعات", "20 between groups")), "proximity",
        ),
        Question(
            "b3", Cat.BASICS,
            L("ما أول ما يلفت العين في التصميم عادةً؟", "What usually catches the eye first?"),
            L("الوزن البصري", "Visual weight"),
            texts(
                L("العنصر الأكبر والأعلى تبايناً", "The largest, highest-contrast element"),
                L("النص الصغير", "Small text"),
                L("الحواف", "The borders"),
            ), 0,
            L("الحجم والتباين أقوى أدوات جذب الانتباه، استخدمهما للعنصر الأهم فقط.", "Size and contrast are the strongest attention tools. Save them for what matters most."),
            listOf(L("حجم + تباين", "size + contrast")), "hierarchy",
        ),
    )

    val questions: List<Question> by lazy { baseQuestions + Path.extraQuestions }

    fun byCat(cat: Cat) = questions.filter { it.cat == cat }

    fun forLesson(id: String) = questions.filter { it.lessonId == id }.ifEmpty { byCat(lesson(id).cat) }.take(3)

    /** Five questions that stay the same for the whole day. */
    fun daily(day: Long): List<Question> = questions.shuffled(java.util.Random(day * 7919)).take(5)
}
