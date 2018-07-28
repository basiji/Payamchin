# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#OKHTTP3 Rules
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keep class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

#Picasso
-keep class com.squareup.picasso.PicassoProvider

#Models and Adapters
#-keep class com.euro.live.eurolive.adapters.** {*;}
#-keep class com.euro.live.eurolive.models.** {*;}

# AVILoading
-keep class com.wang.avi.** { *; }
-keep class com.wang.avi.indicators.** { *; }

#Support Design
-keep class android.support.** {*;}

# Retrofit Rules
-dontnote retrofit2.Platform
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature
-keepattributes Exceptions

# Advanced webview
-keep class * extends android.webkit.WebChromeClient { *; }
-dontwarn im.delight.android.webview.**

# Provider
-keepclass me.everything.** { *; }