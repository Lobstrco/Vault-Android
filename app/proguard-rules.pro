# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-keepattributes SourceFile,LineNumberTable
-allowaccessmodification

-dontwarn android.support.**
-dontwarn com.futurice.project.models.pojo.**
-dontwarn sun.misc.Unsafe
-dontwarn com.google.gson.**
-dontwarn com.google.**
-dontwarn com.facebook.**
-dontwarn com.hudomju.**
-dontwarn com.google.j2objc.annotations.Weak
-dontwarn com.theartofdev.**
-dontwarn com.directions.**
-dontwarn com.google.guava.**
-dontwarn net.hockeyapp.android.**
-dontwarn android.webkit.**
-dontwarn java.lang.invoke**
-dontwarn rx.internal.util.**

-dontwarn okio.**
-dontwarn retrofit2.**
-dontwarn com.squareup.picasso.**
-dontwarn io.realm.**


-dontwarn afu.org.checkerframework.**
-dontwarn org.checkerframework.**

-dontwarn com.fasterxml.jackson.databind.**
-dontwarn org.springframework.**

-dontwarn java.lang.ClassValue
-dontwarn com.google.j2objc.annotations.Weak

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions

-keepattributes *Annotation*

# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform

# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Retain service method parameters when optimizing.
-keepclassmembers, allowshrinking, allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

-keepclassmembers class ** {
    @com.google.common.eventbus.Subscribe public *;
}

-keep class com.futurice.project.models.pojo.** { *; }
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }
-keep class com.google.** { *; }
-keep class com.facebook.** { *; }
-keep class com.theartofdev.** { *; }
-keep class com.directions.** { *; }
-keep class com.google.guava.** { *; }
-keep class com.theartofdev.** { *; }
-keep class com.fasterxml.jackson.annotation.** { *; }
-keep class retrofit2.** { *; }
-keep class io.realm.** { *; }

-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

-keep class android.support.** { *; }
-keep class android.support.v4.** { *; }
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }
-keep interface android.support.v4.** { *; }

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgent
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.support.v4.app.DialogFragment
-keep public class * extends com.actionbarsherlock.app.SherlockListFragment
-keep public class * extends com.actionbarsherlock.app.SherlockFragment
-keep public class * extends com.actionbarsherlock.app.SherlockFragmentActivity
-keep public class * extends android.app.Fragment
-keep public class com.android.vending.licensing.ILicensingService

# Mnemonics
-keep class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**
-keep class org.spongycastle.**

-keep class shadow.** { *; }
-dontwarn shadow.**

-keepclasseswithmembernames class * {
 native <methods>;
}

-keep public class * extends android.view.View {
 public <init>(android.content.Context);
 public <init>(android.content.Context, android.util.AttributeSet);
 public <init>(android.content.Context, android.util.AttributeSet, int);
 public void set*(...);
}

-keepclasseswithmembers class * {
 public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
 public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
 public void *(android.view.View);
}

-keepclassmembers enum * {
 public static **[] values();
 public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
 public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class ** {
   @com.google.common.eventbus.Subscribe public *;
}

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# Get deobfuscated crash reports
-keep public class * extends java.lang.Exception
-keep class com.google.firebase.crashlytics.** { *; }
-dontwarn com.google.firebase.crashlytics.**

# Keeping Stellar SDK
-keep class org.stellar.sdk.** { *; }

# R8 compatibility
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Moxy
# Do not obfuscate classes with Injected Presenters
-keepclasseswithmembernames class * { @moxy.presenter.InjectPresenter <fields>; }
# Do not obfuscate classes with Injected View States
-keepnames @moxy.InjectViewState class *
# Do not obfuscate PresentersBinder autogenerated classes
-keep class **$$PresentersBinder { *; }
# Do not obfuscate ViewStateProvider autogenerated classes
-keep class **$$ViewStateProvider { *; }
# Keep Moxy classes
-keep class moxy.** { *; }

# Okhttp
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*
# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform

# Tangem
-keep class com.tangem.** { *; }
-dontwarn com.tangem.**

# PinLockView
-keep class com.andrognito.** { *; }
-dontwarn com.andrognito.**

# AppInitializer
-keep class androidx.startup.AppInitializer
-keep class * extends androidx.startup.Initializer
-keepnames class * extends androidx.startup.Initializer
# These Proguard rules ensures that ComponentInitializers are are neither shrunk nor obfuscated,
# and are a part of the primary dex file. This is because they are discovered and instantiated
# during application startup.
-keep class * extends androidx.startup.Initializer {
    # Keep the public no-argument constructor while allowing other methods to be optimized.
    <init>();
}

-dontwarn javax.naming.NamingEnumeration
-dontwarn javax.naming.NamingException
-dontwarn javax.naming.directory.Attribute
-dontwarn javax.naming.directory.Attributes
-dontwarn javax.naming.directory.DirContext
-dontwarn javax.naming.directory.InitialDirContext
-dontwarn javax.naming.directory.SearchControls
-dontwarn javax.naming.directory.SearchResult
-dontwarn lombok.NonNull
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE
-dontwarn org.threeten.bp.zone.TzdbZoneRulesProvider
-dontwarn sun.security.x509.X509Key
-dontwarn org.threeten.bp.zone.ZoneRulesProvider
