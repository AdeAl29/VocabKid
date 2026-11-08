# Room Database & Entities
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-dontwarn androidx.room.paging.**

# Google Generative AI / Kotlinx Serialization / Ktor
-keepattributes *Annotation*, InnerClasses, EnclosingMethod, Signature
-dontwarn io.ktor.**
-dontwarn kotlinx.serialization.**
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}

# Kotlin Coroutines
-dontwarn kotlinx.coroutines.**

# Firebase Auth
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
