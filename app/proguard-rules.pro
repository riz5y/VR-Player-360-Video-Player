# Libraries (Firebase, Play services, AdMob, Media3, Glide, Billing, Room, WorkManager,
# Lottie, Gson/Retrofit, Meta Ads) ship their own consumer rules. Blanket keeps on them
# stop R8 from obfuscating, so they were removed to raise the obfuscation percentage.

# ── App data classes read/written by Gson (field names must survive) ─────────
-keep class com.videoplayer.hdvideos.vrplayer1.Reels.model.** { *; }
-keep class com.videoplayer.hdvideos.vrplayer1.Main.VideoItem { *; }

# ── Attributes ───────────────────────────────────────────────────────────────
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ── Crashlytics readable exceptions ──────────────────────────────────────────
-keep public class * extends java.lang.Exception

# ── WorkManager + Room (Room loads *_Impl classes by name; startup crashes without these) ──
-keep class androidx.work.** { *; }
-keep class androidx.work.impl.** { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public static ** INSTANCE;
}
-keep class androidx.room.** { *; }
-keep class androidx.startup.** { *; }
-keep class * implements androidx.startup.Initializer { *; }
-keepnames class * implements androidx.startup.Initializer

# ── Meta Ads ─────────────────────────────────────────────────────────────────
-dontwarn com.facebook.infer.annotation.**

# ── More aggressive renaming ─────────────────────────────────────────────────
-repackageclasses ''
-allowaccessmodification
