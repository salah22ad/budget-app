# ──────────────────────────────────────────────────────────────────────────────
# 1. إعدادات R8 القصوى للتمويه وتقليص الحجم (Full Mode + Repackaging)
# ──────────────────────────────────────────────────────────────────────────────
-optimizationpasses 5
-allowaccessmodification
-repackageclasses 'c.h.d'
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
-keepattributes Signature,InnerClasses,EnclosingMethod,*Annotation*

# حذف سجلات Log من نسخة Release لتقليص الحجم وإخفاء المنطق البرمجي
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
    public static int wtf(...);
}
# ──────────────────────────────────────────────────────────────────────────────
# 2. حماية قواعد بيانات Room فقط (دون منع تمويه الجداول أو دوال الـ DAO)
# ──────────────────────────────────────────────────────────────────────────────
# Room يستدعي كلاسات *_Impl بالانعكاس عبر اسم كلاس القاعدة فقط
-keep class * extends androidx.room.RoomDatabase {
    <init>();
}
-keep class com.smartwallet.database.*_Impl {
    <init>(...);
}

# ──────────────────────────────────────────────────────────────────────────────
# 3. حماية حقول النماذج (Models) فقط لضمان عمل Gson دون تعطيل تمويه الكلاسات والدوال
# ──────────────────────────────────────────────────────────────────────────────
-keepclassmembers class cl.** {
    <fields>;
    <init>(...);
}

# ──────────────────────────────────────────────────────────────────────────────
# 4. حماية البانيات (Constructors) المطلوبة بالانعكاس فقط
# ──────────────────────────────────────────────────────────────────────────────
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    public <init>(...);
}
-keepclassmembers class * extends androidx.lifecycle.AndroidViewModel {
    public <init>(android.app.Application);
}
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
-keepclassmembers class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# ──────────────────────────────────────────────────────────────────────────────
# 5. حماية Parcelable و Serializable
# ──────────────────────────────────────────────────────────────────────────────
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}








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
