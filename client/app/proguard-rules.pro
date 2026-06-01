# Add project specific ProGuard rules here.

# Retrofit & OkHttp
-keepattributes Signature, InnerClasses, EnclosingMethod
-keep class retrofit2.** { *; }
-keepclassmembers class * extends retrofit2.Retrofit { *; }
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

# DTOs / Network Models (Gson/Retrofit)
-keep class com.example.pulse.network.** { *; }
-keepclassmembers class com.example.pulse.network.** { *; }

# Room Database Entities
-keep class com.example.pulse.database.** { *; }
-keepclassmembers class com.example.pulse.database.** { *; }

# Jetpack Compose
-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** { *; }

# General
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile