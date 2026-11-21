# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# ==========================================
# Basic & Debugging
# ==========================================
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# ==========================================
# Firebase Core & Components
# ==========================================
-keep class com.google.firebase.** { *; }
#-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
#-dontwarn com.google.android.gms.**

# ==========================================
# Firebase Firestore (Critical for Serialization)
# ==========================================
-keep class com.google.firebase.firestore.** { *; }
-keepclassmembers class com.google.firebase.firestore.** { *; }
-keep class com.google.firebase.firestore.remote.** { *; }
-keep class com.google.firebase.firestore.local.** { *; }
-keep class com.google.firebase.firestore.core.** { *; }
-keep class com.google.firebase.firestore.model.** { *; }

# Keep your data models (Critical for Firestore toObject / toObject)
-keep class com.smartwallet.data.model.** { *; }
# ==========================================
# Firebase Crashlytics
# ==========================================
-keep class com.google.firebase.crashlytics.** { *; }
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception


# ==========================================
# Room Database
# ==========================================
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# ==========================================
# ViewBinding / DataBinding
# ==========================================
-keep class com.smartwallet.databinding.** { *; }
-keepclassmembers class * extends androidx.databinding.ViewDataBinding {
    <init>(...);
}
-keepclasseswithmembers class * {
    public static ** inflate(...);
}

# ==========================================
# Gson / Serialization (if used)
# ==========================================
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

# ==========================================
# General Protection
# ==========================================
-ignorewarnings
-keep class com.google.protobuf.** { *; }
-dontwarn com.google.protobuf.**
