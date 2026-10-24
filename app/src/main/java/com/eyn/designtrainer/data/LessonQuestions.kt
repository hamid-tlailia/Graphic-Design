package com.eyn.designtrainer.data

import com.eyn.designtrainer.i18n.L

/** Questions written for each lesson, so no two lessons end with the same quiz. */
object LessonQuestions {
    private class B(val lesson: String, val cat: Cat) {
        val list = mutableListOf<Question>()
        /** [opts] are (Arabic, English) pairs; [correct] indexes them. */
        fun q(ar: String, en: String, correct: Int, whyAr: String, whyEn: String, vararg opts: Pair<String, String>) {
            list += Question(
                id = "$lesson-${list.size + 1}", cat = cat, q = L(ar, en), hint = L("", ""),
                options = opts.map { Option(text = L(it.first, it.second)) }, correct = correct,
                why = L(whyAr, whyEn), lessonId = lesson,
            )
        }
    }

    private fun lesson(id: String, cat: Cat, block: B.() -> Unit) = B(id, cat).apply(block).list

    val all: List<Question> = listOf(
        lesson("grid8", Cat.SPACING) {
            q("زر ارتفاعه 46px. ما الارتفاع الأقرب لنظام الـ 8؟", "A button is 46px tall. What's the closest 8px-grid height?", 1,
                "48 = 8 × 6، وهو أيضاً حجم مريح للمس.", "48 = 8 × 6, and it's a comfortable touch size too.",
                "44px" to "44px", "48px" to "48px", "50px" to "50px")
        },
        lesson("hierarchy", Cat.BASICS) {
            q("أي أداة لا تصنع تسلسلاً بصرياً بحد ذاتها؟", "Which tool doesn't create hierarchy on its own?", 2,
                "الحجم والوزن واللون تميّز العناصر، أما استخدام خط واحد فلا يرتّب شيئاً بمفرده.", "Size, weight and color set elements apart; using one typeface doesn't rank anything by itself.",
                "حجم الخط" to "Font size", "وزن الخط" to "Font weight", "استخدام خط واحد" to "Using one typeface")
        },
        lesson("rule603010", Cat.COLORS) {
            q("في قاعدة 60-30-10، أين يذهب لون الـ 10٪ عادةً؟", "In 60-30-10, where does the 10% color usually go?", 0,
                "اللون الأقل استخداماً يُحجز للأزرار والروابط ونقاط الجذب.", "The rarest color is kept for buttons, links and focal points.",
                "الأزرار والعناصر المهمة" to "Buttons and key elements", "خلفية الصفحة" to "The page background", "كل النصوص" to "All body text")
        },
        lesson("alignment", Cat.ALIGN) {
            q("لماذا تبدو الواجهة مرتبة عند محاذاة عناصرها على خط واحد؟", "Why does a layout look tidy when items share one edge?", 1,
                "الخط المشترك يربط العناصر بصرياً ويجعل العين تمسحها بسهولة.", "A shared edge links items visually and makes them easy to scan.",
                "لأنها تصبح أكبر" to "They become bigger", "لأن العين تتبع خطاً واحداً" to "The eye follows one line", "لأن الألوان تتغير" to "The colors change")
        },
        lesson("arabictype", Cat.TYPE) {
            q("ما المحاذاة الطبيعية لفقرة عربية؟", "What's the natural alignment for an Arabic paragraph?", 1,
                "العربية تُقرأ من اليمين، فبداية السطر الثابتة على اليمين تريح العين.", "Arabic reads right-to-left, so a steady right edge is easiest to follow.",
                "يسار" to "Left", "يمين" to "Right", "وسط دائماً" to "Always centered")
            q("لماذا يحتاج النص العربي غالباً ارتفاع سطر أكبر من اللاتيني؟", "Why does Arabic often need more line height than Latin?", 0,
                "النقاط والحركات والحروف النازلة تحتاج مساحة عمودية حتى لا تتصادم الأسطر.", "Dots, diacritics and descenders need vertical room so lines don't collide.",
                "بسبب النقاط والحروف النازلة" to "Because of dots and descenders", "لأن الحروف أعرض" to "Letters are wider", "لا يحتاج ذلك" to "It doesn't")
        },
        lesson("contrast", Cat.COLORS) {
            q("ما الحد الأدنى لنسبة تباين النص العادي حسب WCAG؟", "What's the minimum contrast for body text under WCAG?", 1,
                "النسبة 4.5:1 تضمن قراءة النص لمعظم الناس، و3:1 تكفي للنصوص الكبيرة.", "4.5:1 keeps text readable for most people; 3:1 is enough for large text.",
                "2:1" to "2:1", "4.5:1" to "4.5:1", "10:1" to "10:1")
        },
        lesson("proximity", Cat.SPACING) {
            q("عنوان حقل بعيد عن حقله وقريب من الحقل الذي فوقه. ما المشكلة؟", "A field label sits far from its field and close to the one above. What's wrong?", 0,
                "قانون القرب: العين تربط العنوان بالحقل الأقرب، فيبدو تابعاً للحقل الخطأ.", "Proximity: the eye ties the label to the nearest field, so it reads as the wrong one's.",
                "يبدو تابعاً للحقل الخطأ" to "It seems to belong to the wrong field", "اللون غير مناسب" to "The color is off", "الخط صغير" to "The font is small")
        },
        lesson("whitespace", Cat.SPACING) {
            q("ماذا تفعل المساحة الفارغة حول منتج فاخر؟", "What does generous white space around a luxury product do?", 2,
                "الفراغ يعطي إحساساً بالهدوء والقيمة ويوجّه النظر للمنتج.", "Space feels calm and premium and steers the eye to the product.",
                "تضيّع المساحة" to "Wastes space", "تقلل الوضوح" to "Reduces clarity", "توحي بالفخامة وتركّز النظر" to "Signals quality and focuses attention")
            q("واجهة مزدحمة بالعناصر. ما أول ما تجرّبه؟", "A screen feels crowded. What do you try first?", 1,
                "زيادة المسافات وحذف غير الضروري يريح الواجهة قبل تغيير أي شيء آخر.", "More space and fewer elements calm a screen before anything else.",
                "تصغير كل الخطوط" to "Shrink all fonts", "زيادة المسافات وحذف الزائد" to "Add space and remove extras", "إضافة ألوان" to "Add colors")
        },
        lesson("balance", Cat.ALIGN) {
            q("ما الفرق بين التوازن المتماثل وغير المتماثل؟", "Symmetric vs asymmetric balance — what's the difference?", 0,
                "المتماثل يعكس العناصر حول محور، وغير المتماثل يوازن أوزاناً مختلفة بالحجم واللون والموضع.", "Symmetric mirrors around an axis; asymmetric balances different weights by size, color and position.",
                "الأول مرآة، والثاني توزيع أوزان مختلفة" to "One mirrors, the other balances different weights", "لا فرق" to "No difference", "الثاني بلا عناصر" to "The second has no elements")
            q("عنصر داكن كبير على اليسار. كيف توازنه على اليمين؟", "A big dark element sits on the left. How do you balance the right?", 2,
                "عدة عناصر أصغر أو لون قوي يمكنها معادلة وزن عنصر كبير.", "Several smaller items or a strong color can match one heavy element.",
                "تتركه فارغاً" to "Leave it empty", "تصغّر الصفحة" to "Shrink the page", "بعناصر أصغر أو لون لافت" to "With smaller items or a bold color")
        },
        lesson("elements", Cat.BASICS) {
            q("أي مما يلي ليس من عناصر التصميم الأساسية؟", "Which is not a basic element of design?", 2,
                "العناصر: الخط والشكل واللون والملمس والمساحة. أما الشعار فمنتج يُبنى منها.", "Elements are line, shape, color, texture and space; a logo is something built from them.",
                "الخط" to "Line", "الملمس" to "Texture", "الشعار" to "Logo")
            q("ماذا يوحي الشكل الدائري غالباً؟", "What does a round shape usually suggest?", 0,
                "الدوائر بلا زوايا فتوحي بالود والأمان، والزوايا الحادة توحي بالقوة.", "Circles have no corners, so they feel friendly and safe; sharp angles feel strong.",
                "الود والليونة" to "Friendliness and softness", "الخطر" to "Danger", "الصرامة" to "Rigidity")
            q("ما وظيفة الخط (Line) في التصميم؟", "What does a line do in a design?", 1,
                "الخطوط تقسم المساحة وتقود العين في اتجاه معين.", "Lines divide space and lead the eye in a direction.",
                "يضيف ألواناً" to "Adds color", "يوجّه العين ويقسّم المساحة" to "Guides the eye and divides space", "يكبّر الصور" to "Enlarges images")
        },
        lesson("principles", Cat.BASICS) {
            q("تكرار نفس شكل الزر في كل الصفحات مثال على أي مبدأ؟", "Using the same button style on every page shows which principle?", 0,
                "التكرار يوحّد الواجهة ويجعل المستخدم يتعلّمها بسرعة.", "Repetition unifies the interface and makes it quick to learn.",
                "التكرار" to "Repetition", "التباين" to "Contrast", "الحركة" to "Movement")
            q("ما المبدأ الذي يجعل كل الأجزاء تبدو كأنها تنتمي لتصميم واحد؟", "Which principle makes every part feel like one design?", 2,
                "الوحدة تأتي من ألوان وخطوط ومسافات متسقة.", "Unity comes from consistent colors, type and spacing.",
                "التنوع" to "Variety", "الإيقاع" to "Rhythm", "الوحدة" to "Unity")
        },
        lesson("gestalt", Cat.BASICS) {
            q("ترى دائرة رغم أن خطها متقطع. أي قانون هذا؟", "You see a circle even though its outline has gaps. Which law?", 1,
                "الإغلاق: العقل يكمل الأجزاء الناقصة ليرى شكلاً كاملاً.", "Closure: the mind fills gaps to see a whole shape.",
                "القرب" to "Proximity", "الإغلاق" to "Closure", "التشابه" to "Similarity")
            q("عناصر داخل بطاقة واحدة تُرى كمجموعة. أي قانون؟", "Items inside one card are seen as a group. Which law?", 0,
                "المنطقة المشتركة: الإطار أو الخلفية المشتركة تجمع العناصر.", "Common region: a shared frame or background groups items.",
                "المنطقة المشتركة" to "Common region", "الاستمرارية" to "Continuity", "الشكل والأرضية" to "Figure/ground")
        },
        lesson("grids", Cat.ALIGN) {
            q("كم عموداً تستخدم عادةً شبكة تصميم الجوال؟", "How many columns does a mobile grid usually use?", 0,
                "شبكة الجوال غالباً 4 أعمدة، والويب 12 عموداً.", "Mobile grids are usually 4 columns; web grids 12.",
                "4 أعمدة" to "4 columns", "12 عموداً" to "12 columns", "عمود واحد دائماً" to "Always 1")
            q("ما «الفاصل» (Gutter) في الشبكة؟", "What is the gutter in a grid?", 2,
                "الفاصل هو المسافة الثابتة بين الأعمدة.", "The gutter is the fixed space between columns.",
                "هامش الصفحة الخارجي" to "The outer page margin", "عرض العمود" to "The column width", "المسافة بين الأعمدة" to "The space between columns")
            q("لماذا تُستخدم 12 عموداً في الويب كثيراً؟", "Why are 12-column web grids so common?", 1,
                "12 تقبل القسمة على 2 و3 و4 و6، فتسمح بتقسيمات كثيرة.", "12 divides by 2, 3, 4 and 6, allowing many layouts.",
                "لأنه رقم كبير" to "It's a big number", "لأنه يقبل القسمة على 2 و3 و4 و6" to "It divides by 2, 3, 4 and 6", "لأنه معيار إجباري" to "It's mandatory")
        },
        lesson("composition", Cat.ALIGN) {
            q("في قاعدة الأثلاث، أين تضع العنصر الأهم؟", "In the rule of thirds, where does the key subject go?", 1,
                "نقاط تقاطع خطوط الأثلاث تجذب العين أكثر من المنتصف تماماً.", "Where the third lines cross draws the eye more than the dead center.",
                "في المنتصف تماماً" to "Dead center", "على أحد تقاطعات الخطوط" to "On a line intersection", "في زاوية الصورة" to "In a corner")
            q("كم مربعاً تقسم الصورة في قاعدة الأثلاث؟", "Into how many boxes does the rule of thirds divide a frame?", 2,
                "خطان أفقيان وخطان عموديان يقسمان الإطار إلى 9 أجزاء متساوية.", "Two horizontal and two vertical lines split the frame into 9 equal parts.",
                "4" to "4", "6" to "6", "9" to "9")
            q("ما «الخطوط القائدة» في التكوين؟", "What are leading lines in a composition?", 0,
                "خطوط مثل طريق أو حافة تقود عين المشاهد نحو العنصر الرئيسي.", "Lines such as a road or edge lead the viewer's eye to the subject.",
                "خطوط تقود العين نحو العنصر الرئيسي" to "Lines that lead the eye to the subject", "إطار حول الصورة" to "A frame around the image", "خطوط الشبكة المخفية" to "Hidden grid lines")
        },
        lesson("colorwheel", Cat.COLORS) {
            q("ما الألوان المتجاورة (Analogous)؟", "What are analogous colors?", 0,
                "ألوان متجاورة في العجلة مثل الأزرق والأزرق المخضر، وتعطي تناسقاً هادئاً.", "Neighbors on the wheel, like blue and blue-green, giving calm harmony.",
                "ألوان متجاورة في العجلة" to "Neighbors on the wheel", "ألوان متقابلة" to "Opposites", "الأبيض والأسود" to "Black and white")
            q("ما الألوان الأساسية في عجلة الألوان التقليدية؟", "What are the primaries on the traditional color wheel?", 1,
                "الأحمر والأصفر والأزرق تُمزج منها بقية الألوان في العجلة التقليدية.", "Red, yellow and blue mix into the other colors on the traditional wheel.",
                "الأخضر والبرتقالي والبنفسجي" to "Green, orange, purple", "الأحمر والأصفر والأزرق" to "Red, yellow, blue", "الأسود والرمادي والأبيض" to "Black, gray, white")
        },
        lesson("colorpsych", Cat.COLORS) {
            q("أي لون يرتبط غالباً بالثقة والهدوء ويكثر في البنوك؟", "Which color often means trust and calm, common in banks?", 0,
                "الأزرق يوحي بالثقة والاستقرار.", "Blue suggests trust and stability.",
                "الأزرق" to "Blue", "الأحمر" to "Red", "الأصفر" to "Yellow")
            q("لماذا يكثر الأحمر في أزرار التخفيضات والتنبيهات؟", "Why is red common on sale and alert buttons?", 2,
                "الأحمر لون طاقة وعجلة يلفت الانتباه فوراً.", "Red signals energy and urgency and grabs attention instantly.",
                "لأنه هادئ" to "It's calm", "لأنه رخيص الطباعة" to "It's cheap to print", "لأنه يوحي بالعجلة ويلفت الانتباه" to "It signals urgency and grabs attention")
            q("هل معاني الألوان واحدة في كل الثقافات؟", "Do colors mean the same in every culture?", 1,
                "تختلف المعاني؛ الأبيض مثلاً للأفراح في ثقافات وللحداد في أخرى، فاعرف جمهورك.", "Meanings vary — white is for weddings in some cultures and mourning in others — so know your audience.",
                "نعم دائماً" to "Always", "لا، تختلف حسب الثقافة" to "No, they vary by culture", "فقط الأسود يختلف" to "Only black differs")
        },
        lesson("colormodels", Cat.COLORS) {
            q("أي نموذج ألوان تستخدمه للشاشات؟", "Which color model do screens use?", 0,
                "الشاشات تمزج الضوء الأحمر والأخضر والأزرق (RGB).", "Screens mix red, green and blue light (RGB).",
                "RGB" to "RGB", "CMYK" to "CMYK", "Pantone فقط" to "Pantone only")
            q("ماذا يمثل الرمز #FFFFFF؟", "What does #FFFFFF represent?", 2,
                "أعلى قيمة لكل من الأحمر والأخضر والأزرق تعطي الأبيض.", "The maximum of red, green and blue gives white.",
                "الأسود" to "Black", "الأحمر" to "Red", "الأبيض" to "White")
        },
        lesson("anatomy", Cat.TYPE) {
            q("ما «خط الأساس» (Baseline)؟", "What is the baseline?", 1,
                "الخط الوهمي الذي تجلس عليه معظم الحروف.", "The invisible line most letters sit on.",
                "أعلى الحروف" to "The top of letters", "الخط الذي تجلس عليه الحروف" to "The line letters sit on", "المسافة بين الكلمات" to "The space between words")
            q("ما الفرق بين الخط ذي الزوائد (Serif) وغيره؟", "What sets a serif typeface apart from a sans-serif?", 0,
                "الخطوط ذات الزوائد لها نتوءات صغيرة في أطراف الحروف.", "Serif faces have small strokes at the ends of letters.",
                "نتوءات صغيرة في أطراف الحروف" to "Small strokes at letter ends", "أنها أعرض دائماً" to "They're always wider", "أنها للعناوين فقط" to "They're for headings only")
            q("ماذا يسمى ارتفاع الحرف الصغير x في اللاتينية؟", "What's the height of a lowercase x called?", 2,
                "ارتفاع x يحدد حجم الخط الظاهري ومقروئيته.", "The x-height sets how big and readable a font looks.",
                "ارتفاع الكاب" to "Cap height", "النازل" to "Descender", "ارتفاع x" to "x-height")
        },
        lesson("typescale", Cat.TYPE) {
            q("ما فائدة سلّم أحجام الخطوط (Type scale)؟", "What does a type scale give you?", 0,
                "أحجام محددة بنسبة ثابتة تجعل الفروق واضحة ومتناسقة.", "Set sizes in a fixed ratio make differences clear and consistent.",
                "أحجام متناسقة بفروق واضحة" to "Consistent sizes with clear steps", "خطوط أكثر" to "More fonts", "ألوان للنص" to "Text colors")
        },
        lesson("pairing", Cat.TYPE) {
            q("ما القاعدة الآمنة لاقتران خطين؟", "What's a safe rule for pairing two fonts?", 1,
                "خط مميز للعناوين وخط بسيط مقروء للنصوص يكمل كل منهما الآخر.", "A distinctive heading face with a plain readable body face complement each other.",
                "خطان متشابهان جداً" to "Two near-identical fonts", "خط مميز للعناوين وبسيط للنص" to "Distinctive headings, plain body", "أربعة خطوط على الأقل" to "At least four fonts")
            q("كم خطاً يكفي في معظم التصاميم؟", "How many typefaces do most designs need?", 0,
                "خطان كافيان غالباً؛ الأكثر يشتّت ويضعف الهوية.", "Two is usually enough; more distracts and weakens the identity.",
                "خطان" to "Two", "خمسة" to "Five", "خط لكل قسم" to "One per section")
            q("كيف تميّز العنوان عن النص إن استخدمت خطاً واحداً؟", "How do you set headings apart with a single font?", 2,
                "عائلة واحدة بأوزان وأحجام مختلفة تكفي لتسلسل واضح.", "One family in different weights and sizes is enough for clear hierarchy.",
                "لا يمكن" to "You can't", "بتغيير اللون فقط" to "Color only", "بالوزن والحجم" to "With weight and size")
        },
        lesson("readability", Cat.TYPE) {
            q("كم حرفاً تقريباً هو الطول المريح للسطر؟", "Roughly how many characters make a comfortable line?", 1,
                "بين 45 و75 حرفاً تقريباً؛ أطول من ذلك يتعب العين.", "About 45–75 characters; longer lines tire the eye.",
                "15–20" to "15–20", "45–75" to "45–75", "150 أو أكثر" to "150 or more")
            q("نص رمادي فاتح على خلفية بيضاء. ما المشكلة؟", "Light gray text on white. What's wrong?", 0,
                "التباين المنخفض يجعل القراءة صعبة خاصة على الشاشات في الضوء.", "Low contrast makes reading hard, especially on screens in daylight.",
                "تباين ضعيف" to "Low contrast", "الخط كبير" to "Font too big", "لا مشكلة" to "Nothing")
        },
        lesson("photos", Cat.IMAGERY) {
            q("صورة شخص ينظر إلى خارج الإطار. أين تضع النص؟", "A person in the photo looks off one side. Where should text go?", 1,
                "نظرة الشخص تقود عين المشاهد، فضع النص في الاتجاه الذي ينظر إليه.", "Their gaze leads the viewer's eye, so put the text where they're looking.",
                "خلفه" to "Behind them", "في الاتجاه الذي ينظر إليه" to "Where they're looking", "فوق وجهه" to "Over their face")
            q("ماذا تفعل عند قص صورة شخص؟", "What should you avoid when cropping a person?", 2,
                "القص عند المفاصل كالركبة أو الرسغ يبدو مبتوراً؛ اقطع بين المفاصل.", "Cropping at joints like knees or wrists looks amputated; crop between joints.",
                "ترك مساحة فوق الرأس" to "Leaving room above the head", "القص من الجوانب" to "Cropping the sides", "القص عند المفاصل" to "Cropping at the joints")
        },
        lesson("icons", Cat.IMAGERY) {
            q("ما أهم قاعدة عند استخدام أيقونات من مجموعات مختلفة؟", "Most important rule when mixing icons from different sets?", 0,
                "يجب أن تتوحد السماكة والنمط والحجم وإلا بدت الواجهة غير متناسقة.", "Stroke, style and size must match or the interface looks messy.",
                "توحيد السماكة والنمط" to "Match stroke and style", "ألوان مختلفة لكل أيقونة" to "Different colors for each", "أحجام عشوائية" to "Random sizes")
            q("متى تضيف نصاً بجانب الأيقونة؟", "When should an icon get a text label?", 1,
                "إذا لم يكن معنى الأيقونة معروفاً للجميع، النص يزيل الغموض.", "When the icon isn't universally understood, a label removes doubt.",
                "أبداً" to "Never", "عندما لا يكون معناها واضحاً للجميع" to "When its meaning isn't obvious to everyone", "دائماً بخط كبير" to "Always in big type")
            q("أي أيقونة يفهمها الجميع تقريباً؟", "Which icon is understood almost everywhere?", 2,
                "العدسة للبحث من أكثر الأيقونات شيوعاً ووضوحاً.", "A magnifier for search is one of the clearest, most common icons.",
                "أيقونة مجردة جديدة" to "A new abstract icon", "شعار الشركة" to "The company logo", "العدسة للبحث" to "A magnifier for search")
        },
        lesson("vector", Cat.IMAGERY) {
            q("أي نوع يناسب الصور الفوتوغرافية؟", "Which type suits photographs?", 0,
                "الصور الفوتوغرافية نقطية (بكسلات) لأنها مليئة بالتدرجات والتفاصيل.", "Photos are raster (pixels) because they're full of gradients and detail.",
                "نقطي (Raster)" to "Raster", "متجه (Vector)" to "Vector", "نص" to "Text")
            q("ماذا يحدث لصورة نقطية عند تكبيرها كثيراً؟", "What happens to a raster image when enlarged a lot?", 1,
                "البكسلات تظهر فتصبح الصورة مشوشة ومربعة.", "Pixels show, so it turns blurry and blocky.",
                "تبقى حادة" to "Stays sharp", "تصبح مشوشة" to "Turns blurry", "تتحول لمتجه" to "Becomes vector")
        },
        lesson("logotypes", Cat.BRANDING) {
            q("شعار يجمع رمزاً واسماً معاً. ما نوعه؟", "A logo that combines a symbol and a name is a…", 2,
                "الشعار المركّب يجمع الرمز والنص ويمكن فصلهما لاحقاً.", "A combination mark pairs symbol and text, which can later be used apart.",
                "شعار نصي" to "Wordmark", "شعار حرفي" to "Lettermark", "شعار مركّب" to "Combination mark")
            q("شعار مكوّن من الأحرف الأولى فقط مثل IBM. ما نوعه؟", "A logo of initials only, like IBM, is a…", 0,
                "الشعار الحرفي يستخدم الأحرف الأولى لاسم طويل.", "A lettermark uses the initials of a long name.",
                "شعار حرفي (Lettermark)" to "Lettermark", "شعار تجريدي" to "Abstract mark", "شعار شخصية" to "Mascot")
        },
        lesson("logoprocess", Cat.BRANDING) {
            q("ما أول خطوة في تصميم شعار؟", "What's the first step in designing a logo?", 1,
                "فهم العلامة وجمهورها ومنافسيها يأتي قبل أي رسم.", "Understanding the brand, its audience and competitors comes before drawing.",
                "اختيار الألوان" to "Picking colors", "البحث وفهم العلامة" to "Research and understanding the brand", "تصميم بطاقة العمل" to "Designing business cards")
            q("لماذا ترسم اسكتشات كثيرة قبل الحاسوب؟", "Why sketch many ideas before going digital?", 0,
                "الاسكتشات سريعة وتسمح باستكشاف أفكار كثيرة قبل التعلق بواحدة.", "Sketches are quick and let you explore many ideas before committing.",
                "لاستكشاف أفكار كثيرة بسرعة" to "To explore many ideas fast", "لأن الحاسوب ممنوع" to "Computers aren't allowed", "لتوفير الحبر" to "To save ink")
        },
        lesson("brandguide", Cat.BRANDING) {
            q("ما الغرض من دليل الهوية البصرية؟", "What is a brand guideline for?", 0,
                "يضمن استخدام الشعار والألوان والخطوط بنفس الطريقة في كل مكان.", "It keeps the logo, colors and type used the same way everywhere.",
                "توحيد استخدام الهوية" to "Keeping the identity consistent", "بيع الشعار" to "Selling the logo", "تصميم شعار جديد كل شهر" to "A new logo every month")
            q("ما «المساحة الآمنة» حول الشعار؟", "What is the clear space around a logo?", 2,
                "مساحة فارغة إلزامية تمنع العناصر الأخرى من الالتصاق بالشعار.", "A required empty margin that keeps other elements away from the logo.",
                "مكان كلمة المرور" to "Where the password goes", "إطار ملون" to "A colored frame", "فراغ يحمي الشعار من العناصر الأخرى" to "Space that protects it from other elements")
            q("أي مما يلي لا يوجد عادةً في دليل الهوية؟", "Which usually isn't in a brand guideline?", 1,
                "الدليل يحدد الشعار والألوان والخطوط والاستخدامات الخاطئة، لا رواتب الموظفين.", "Guides cover logo, colors, type and misuse — not staff salaries.",
                "رموز الألوان" to "Color codes", "رواتب الموظفين" to "Staff salaries", "الاستخدامات الخاطئة للشعار" to "Logo misuse")
        },
        lesson("uxbasics", Cat.UX) {
            q("ما الفرق بين UX وUI؟", "What's the difference between UX and UI?", 0,
                "UX هي التجربة وسهولة الوصول للهدف، وUI هي الشكل المرئي للواجهة.", "UX is the experience and ease of reaching a goal; UI is the visual interface.",
                "UX التجربة وUI الشكل" to "UX is experience, UI is visuals", "هما نفس الشيء" to "They're the same", "UI للطباعة فقط" to "UI is only for print")
            q("كم خطوة يجب أن يحتاجها المستخدم لإنجاز مهمة متكررة؟", "How many steps should a frequent task take?", 2,
                "كل خطوة زائدة تزيد احتمال ترك المستخدم للمهمة؛ اختصر قدر الإمكان.", "Every extra step raises drop-off; keep it as short as possible.",
                "أكبر عدد ممكن" to "As many as possible", "10 على الأقل" to "At least 10", "أقل عدد ممكن" to "As few as possible")
            q("ما أفضل طريقة لمعرفة إن كان التصميم سهلاً؟", "Best way to know if a design is easy to use?", 1,
                "اختبار مستخدمين حقيقيين يكشف ما لا تراه أنت.", "Testing with real users reveals what you can't see yourself.",
                "سؤال المصمم نفسه" to "Ask the designer", "اختباره مع مستخدمين حقيقيين" to "Test it with real users", "إضافة ألوان أكثر" to "Add more colors")
        },
        lesson("uicomponents", Cat.UX) {
            q("ما حالات الزر التي يجب تصميمها؟", "Which button states should you design?", 2,
                "الزر يحتاج حالات: عادي، مضغوط، معطّل، وجارٍ التحميل.", "A button needs normal, pressed, disabled and loading states.",
                "العادية فقط" to "Normal only", "الملونة فقط" to "Colored only", "عادي ومضغوط ومعطّل وتحميل" to "Normal, pressed, disabled, loading")
            q("متى تستخدم مفتاح التشغيل (Toggle) بدل مربع الاختيار؟", "When do you use a toggle instead of a checkbox?", 0,
                "المفتاح لإعداد يُطبّق فوراً، ومربع الاختيار لاختيارات تُحفظ لاحقاً.", "A toggle applies a setting instantly; a checkbox marks choices saved later.",
                "لإعداد يعمل فوراً" to "For a setting that applies instantly", "في كل النماذج" to "In every form", "لا فرق أبداً" to "There's no difference")
        },
        lesson("accessibility", Cat.UX) {
            q("لماذا تضيف وصفاً نصياً (Alt) للصور؟", "Why add alt text to images?", 1,
                "قارئات الشاشة تقرأ الوصف للمكفوفين، ويظهر إن لم تُحمّل الصورة.", "Screen readers read it to blind users, and it shows if the image fails to load.",
                "لتحسين الألوان" to "To improve colors", "ليفهمها مستخدمو قارئ الشاشة" to "So screen-reader users understand it", "لتكبير الصورة" to "To enlarge the image")
            q("ما أصغر حجم مقبول لنص الفقرات على الجوال تقريباً؟", "Roughly the smallest acceptable body text on mobile?", 0,
                "نحو 14–16 يضمن القراءة المريحة لمعظم الناس.", "About 14–16 keeps reading comfortable for most people.",
                "14–16" to "14–16", "8" to "8", "10" to "10")
        },
        lesson("printprep", Cat.PRINT) {
            q("ما «الهامش الآمن» في التصميم المطبوع؟", "What is the safe margin in print design?", 2,
                "منطقة داخل خط القص تُبقي النصوص المهمة بعيدة عن خطر القص.", "An area inside the trim line keeping important text away from the cut.",
                "المساحة خارج الورقة" to "Space outside the sheet", "مكان الباركود" to "Where the barcode goes", "منطقة تحمي النص من القص" to "A zone protecting text from the cut")
        },
        lesson("formats", Cat.PRINT) {
            q("أي صيغة تختار لشعار بخلفية شفافة على موقع؟", "Which format for a logo with a transparent background on a website?", 1,
                "PNG وSVG يدعمان الشفافية، أما JPG فلا.", "PNG and SVG support transparency; JPG doesn't.",
                "JPG" to "JPG", "PNG أو SVG" to "PNG or SVG", "BMP" to "BMP")
            q("ما مقاس ورقة A4؟", "What size is an A4 sheet?", 0,
                "A4 مقاسها 210 × 297 ملم وهي الأكثر استخداماً للمطبوعات.", "A4 is 210 × 297 mm, the most common print size.",
                "210 × 297 ملم" to "210 × 297 mm", "100 × 150 ملم" to "100 × 150 mm", "500 × 700 ملم" to "500 × 700 mm")
            q("أي صيغة تناسب إرسال تصميم للمطبعة؟", "Which format suits sending a design to the printer?", 2,
                "ملف PDF عالي الجودة يحفظ الخطوط والألوان والنزف.", "A high-quality PDF keeps fonts, colors and bleed intact.",
                "GIF" to "GIF", "لقطة شاشة" to "A screenshot", "PDF للطباعة" to "Print-ready PDF")
        },
        lesson("process", Cat.BASICS) {
            q("ما الترتيب الصحيح لعملية التصميم؟", "What's the right order for the design process?", 0,
                "تفهم المشكلة، ثم تولّد أفكاراً، ثم تصمم، ثم تختبر وتحسّن.", "Understand the problem, generate ideas, design, then test and refine.",
                "فهم ← أفكار ← تصميم ← اختبار" to "Understand → ideas → design → test", "تصميم ← فهم ← اختبار" to "Design → understand → test", "اختبار ← تصميم فقط" to "Test → design only")
            q("ما «الموجز» (Brief) في مشروع التصميم؟", "What is a design brief?", 1,
                "وثيقة تحدد الهدف والجمهور والقيود والمواعيد قبل البدء.", "A document setting goals, audience, constraints and deadlines before starting.",
                "الفاتورة النهائية" to "The final invoice", "وثيقة الهدف والجمهور والقيود" to "Goals, audience and constraints", "ملف الخطوط" to "The font file")
            q("ماذا تفعل بعد تلقي ملاحظات العميل؟", "What do you do after client feedback?", 2,
                "التصميم عملية متكررة: تعدّل ثم تعرض النسخة المحسّنة.", "Design is iterative: revise, then present the improved version.",
                "تبدأ من الصفر دائماً" to "Always start from scratch", "تتجاهلها" to "Ignore it", "تعدّل وتعرض نسخة محسّنة" to "Revise and present an improved version")
        },
        lesson("portfolio", Cat.BASICS) {
            q("كم مشروعاً يكفي في ملف الأعمال؟", "How many projects should a portfolio show?", 1,
                "6 إلى 10 مشاريع قوية أفضل من عشرات المشاريع المتوسطة.", "6–10 strong projects beat dozens of average ones.",
                "مشروع واحد" to "One", "6 إلى 10 مشاريع قوية" to "6–10 strong projects", "كل ما صممته" to "Everything you ever made")
            q("ماذا تعرض مع كل مشروع غير الصورة النهائية؟", "Besides the final image, what should each project show?", 0,
                "المشكلة والحل والنتيجة وطريقة تفكيرك تُظهر قيمتك كمصمم.", "The problem, solution, result and your thinking show your value.",
                "المشكلة والحل والنتيجة" to "Problem, solution and result", "سعر البرنامج" to "The software price", "لا شيء" to "Nothing")
            q("بأي مشروع تبدأ ملف أعمالك؟", "Which project should open your portfolio?", 2,
                "الانطباع الأول مهم، فابدأ بأقوى مشروع يناسب العمل الذي تريده.", "First impressions matter, so lead with your strongest, most relevant work.",
                "الأقدم" to "The oldest", "الأصغر" to "The smallest", "الأقوى والأقرب لما تريده" to "Your strongest and most relevant")
        },
    ).flatten()
}
