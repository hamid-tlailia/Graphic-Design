-keepattributes *Annotation*, InnerClasses
-keepclassmembers class com.eyn.designtrainer.** {
    *** Companion;
}
-keepclasseswithmembers class com.eyn.designtrainer.** {
    kotlinx.serialization.KSerializer serializer(...);
}
