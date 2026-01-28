# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# =========================================
# Kotlin and Coroutines
# =========================================
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**
-keepclassmembernames class kotlinx.coroutines.internal.MainDispatcherFactory { *; }
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler

# =========================================
# AndroidX and Google Libraries
# =========================================
-keep class androidx.** { *; }
-keep class com.google.** { *; }
-dontwarn androidx.**
-dontwarn com.google.**

# =========================================
# Lifecycle Components
# =========================================
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class * extends androidx.lifecycle.AndroidViewModel { *; }
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>();
}
-keepclassmembers class androidx.lifecycle.ViewModel {
    <init>();
}

# =========================================
# ViewBinding
# =========================================
-keep class * extends androidx.viewbinding.ViewBinding { *; }
-keepclasseswithmembernames class * {
    public static *** bind(android.view.View);
}
-keepclasseswithmembernames class * {
    public *** bind(android.view.View);
}

# =========================================
# Data and Serialization
# =========================================
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keep class com.google.gson.** { *; }
-keep class com.tencent.mmkv.** { *; }
-dontwarn com.tencent.mmkv.**

# =========================================
# OkHttp
# =========================================
-dontwarn okhttp3.**
-dontwarn okio.**
-keepattributes Signature
-keepattributes Exceptions

# =========================================
# Material Components
# =========================================
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**

# =========================================
# Third-party Libraries
# =========================================
-keep class com.blacksquircle.ui.** { *; }
-keep class com.github.GrenderG.** { *; }
-keep class com.google.zxing.** { *; }
-keep class com.github.T8RIN.QuickieExtended.** { *; }
-dontwarn com.blacksquircle.ui.**
-dontwarn com.github.GrenderG.**
-dontwarn com.github.T8RIN.QuickieExtended.**

# =========================================
# V2Ray Core and Native Libraries
# =========================================
-keep class libv2ray.** { *; }
-keep class go.** { *; }
-dontwarn libv2ray.**
-dontwarn go.**

# =========================================
# Reflection and Serialization
# =========================================
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keepclassmembers class * {
    public <init>(android.content.Context);
}

# =========================================
# Keep Parcelable and Serializable
# =========================================
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# =========================================
# Work Manager
# =========================================
-keep class androidx.work.** { *; }
-dontwarn androidx.work.**

# =========================================
# Remove logging in release builds
# =========================================
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# =========================================
# Keep line number information for debugging
# =========================================
-keepattributes SourceFile,LineNumberTable

# =========================================
# Optimization and Obfuscation
# =========================================
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# =========================================
# Keep enums
# =========================================
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# =========================================
# Native methods
# =========================================
-keepclasseswithmembernames class * {
    native <methods>;
}
