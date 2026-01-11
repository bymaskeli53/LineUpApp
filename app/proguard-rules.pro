# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep line numbers for better crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep annotations
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions

# ==================== KOTLIN ====================
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}

# ==================== HILT ====================
-keep,allowobfuscation,allowshrinking class dagger.hilt.android.internal.** { *; }
-keep,allowobfuscation,allowshrinking class javax.inject.** { *; }
-keep,allowobfuscation,allowshrinking class dagger.** { *; }
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ComponentSupplier { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Keep Hilt generated classes
-keep class **_HiltModules** { *; }
-keep class **_HiltComponents** { *; }
-keep class **_Factory { *; }
-keep class **_MembersInjector { *; }

# Keep classes annotated with @HiltViewModel
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }

# Keep classes annotated with @AndroidEntryPoint
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }

# ==================== ROOM DATABASE ====================
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
}

# Keep Room generated classes
-keep class **_Impl { *; }

# ==================== RETROFIT & OKHTTP ====================
# Retrofit
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keep class okio.** { *; }

# ==================== GSON ====================
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

# Keep data model classes for GSON serialization
-keep class com.gundogar.lineupapp.data.model.** { *; }
-keep class com.gundogar.lineupapp.data.remote.model.** { *; }
-keep class com.gundogar.lineupapp.data.local.entity.** { *; }

# ==================== JETPACK COMPOSE ====================
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep Compose compiler generated classes
-keep class **ComposableSingletons$* { *; }

# ==================== LIFECYCLE & VIEWMODEL ====================
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class * extends androidx.lifecycle.AndroidViewModel { *; }
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# ==================== NAVIGATION ====================
-keep class * extends androidx.navigation.Navigator { *; }

# ==================== COIL ====================
-dontwarn coil.**
-keep class coil.** { *; }

# ==================== OSMDROID (Maps) ====================
-dontwarn org.osmdroid.**
-keep class org.osmdroid.** { *; }

# ==================== PLAY SERVICES LOCATION ====================
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# ==================== LOTTIE ====================
-dontwarn com.airbnb.lottie.**
-keep class com.airbnb.lottie.** { *; }

# ==================== DATASTORE ====================
-keep class androidx.datastore.** { *; }
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite {
    <fields>;
}

# ==================== APP SPECIFIC ====================
# Keep your app's entry points
-keep class com.gundogar.lineupapp.LineUpApplication { *; }
-keep class com.gundogar.lineupapp.MainActivity { *; }

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep Serializable implementations
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ==================== REMOVE LOGGING IN RELEASE ====================
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# ==================== MISCELLANEOUS ====================
# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep custom views
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Suppress warnings for missing classes
-dontwarn java.lang.invoke.StringConcatFactory
-dontwarn org.bouncycastle.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**
