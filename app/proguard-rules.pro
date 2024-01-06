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

# Add ProGuard rules for com.google.android.material:material library

# Keep the Material Components library classes
-keep class com.google.android.material.** { *; }

# Keep the Material Components themes
-keep class com.google.android.material.theme.** { *; }

# Keep the Material Components transition classes
-keep class com.google.android.material.transition.** { *; }

# Keep the Material Components internal classes
-keep class com.google.android.material.internal.** { *; }

# Keep the Material Components experimental classes
-keep class com.google.android.material.experimental.** { *; }

# Keep the Material Components animator classes
-keep class com.google.android.material.animation.** { *; }

# Keep the Material Components resource classes
-keep class com.google.android.material.R$* { *; }

# Keep the Material Components constants
-keepclassmembers class com.google.android.material.internal.** { *; }

# Keep the Parcelable implementation of internal classes
-keepclassmembers class com.google.android.material.internal.ParcelableSparseArray { *; }

# Keep the entry point to avoid removing classes with @Keep annotation
-keepclassmembers class * {
    #noinspection ShrinkerUnresolvedReference
    @com.google.android.material.internal.Keep *;
}

# Add ProGuard rules for com.google.android.gms:play-services-location library

# Keep the Location Services classes
-keep class com.google.android.gms.location.** { *; }

# Keep the fused location provider classes
-keep class com.google.android.gms.location.FusedLocationProviderClient { *; }

# Keep the Geofencing classes
-keep class com.google.android.gms.location.GeofencingClient { *; }

# Keep the settings API classes
-keep class com.google.android.gms.location.SettingsClient { *; }

# Keep the ActivityRecognition classes
-keep class com.google.android.gms.location.ActivityRecognitionClient { *; }

# Keep the LocationRequest and LocationSettingsRequest classes
-keep class com.google.android.gms.location.LocationRequest { *; }
-keep class com.google.android.gms.location.LocationSettingsRequest { *; }

# Keep the result classes
-keep class com.google.android.gms.location.LocationResult { *; }
-keep class com.google.android.gms.location.LocationAvailability { *; }

# Keep the exceptions
#noinspection ShrinkerUnresolvedReference
-keep class com.google.android.gms.location.LocationException { *; }

# Keep the API interfaces
-keep class com.google.android.gms.location.LocationServices { *; }
-keep class com.google.android.gms.location.SettingsApi { *; }
-keep class com.google.android.gms.location.ActivityRecognitionApi { *; }

# Keep the constants
-keepclassmembers class com.google.android.gms.location.* {
    public static final *;
}

# Keep the Parcelable implementation of internal classes
-keepclassmembers class com.google.android.gms.location.LocationRequest {
    <fields>;
}

# Keep the R class for Google Play Services
-keep class com.google.android.gms.R$* { *; }

# Optional: If you are using Play Services Auth as well, you might need to add the following rule
# -keep class com.google.android.gms.auth.api.** { *; }


# Add ProGuard rules for com.android.support:multidex library

# Keep the MultiDexApplication class
-keep class android.support.multidex.MultiDexApplication

# Keep the MultiDex installation methods
-keep class android.support.multidex.MultiDex

# Keep all classes in the support.multidex package
-keep class android.support.multidex.** { *; }

# Keep the classes that are part of the MultiDex library
-keep class androidx.multidex.** { *; }

# Keep the application classes
-keep class your.application.package.** { *; }

# Keep the entry point to avoid removing classes with @Keep annotation
-keepclassmembers class * {
    #noinspection ShrinkerUnresolvedReference
    @android.support.annotation.Keep *;
}

# Add ProGuard rules for com.google.code.gson:gson library

# Gson uses field names to match JSON properties, so fields should not be obfuscated
-keepattributes Signature

# Gson specific classes
-keep class com.google.gson.stream.** { *; }
-keep class sun.misc.Unsafe { *; }

# Keep public classes and methods used for serialization and deserialization
-keep class com.google.gson.** {
    public <methods>;
    public <fields>;
}

# If you are using Gson to convert between JSON and Java objects, you may need to
# add additional rules to keep your model classes. For example:
# -keep class com.example.myapp.models.** { *; }

# Keep the names of the fields used by serialization/deserialization
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep the entry point to avoid removing classes with @Keep annotation
-keepclassmembers class * {
    #noinspection ShrinkerUnresolvedReference
    @com.google.gson.annotations.Keep *;
}


# Add ProGuard rules for org.apache.httpcomponents:httpclient-android library

# Apache HttpClient classes
-keep class org.apache.http.** { *; }
-keep interface org.apache.http.** { *; }

# Apache Commons Logging classes
-keep class org.apache.commons.logging.** { *; }

# Keep all classes in the httpclient package and its subpackages
-keep class org.apache.httpcomponents.httpclient.** { *; }
-keep class org.apache.httpcomponents.httpcore.** { *; }

# If you are using custom socket factories, you might need to add rules to keep them
-keep class my.package.CustomSocketFactory { *; }

# If you are using custom connection managers, you might need to add rules to keep them
-keep class my.package.CustomConnectionManager { *; }

# If you are using custom authentication handlers, you might need to add rules to keep them
-keep class my.package.CustomAuthenticationHandler { *; }

# Keep the entry point to avoid removing classes with @Keep annotation
-keepclassmembers class * {
    #noinspection ShrinkerUnresolvedReference
    @org.apache.http.annotation.Keep *;
}

# Add ProGuard rules for com.squareup.retrofit2:retrofit and OkHttp libraries

# Retrofit
-keep class retrofit2.** { *; }
-keepclassmembers class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-dontwarn retrofit2.**

# OkHttp
-keep class okhttp3.** { *; }
-keepclassmembers class okhttp3.** { *; }
-dontwarn okhttp3.**

# If you are using OkHttp with Retrofit, you might need to keep additional classes
-keep class okhttp3.logging.** { *; }
-keep class okhttp3.internal.** { *; }
-keep class okio.** { *; }

# Keep the entry point to avoid removing classes with @Keep annotation
-keepclassmembers class * {
    #noinspection ShrinkerUnresolvedReference
    @retrofit2.http.Keep *;
    @okhttp3.internal.annotations.Keep *;
}

# Add ProGuard rules for com.squareup.retrofit2:retrofit library

# Retrofit
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# OkHttp
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**

# Okio
-keep class okio.** { *; }

# Gson
-keep class com.google.gson.** { *; }

# RxJava
-keep class io.reactivex.** { *; }

# Logging interceptor
-keep class okhttp3.logging.** { *; }

# Add ProGuard rules for com.squareup.retrofit2:converter-gson library

# Gson
-keep class com.google.gson.** { *; }
-keep class com.squareup.retrofit2.converter.gson.** { *; }
-keep class com.squareup.retrofit2.converter.gson.stream.** { *; }

# If you are using custom model classes, keep them
-keep class your.package.models.** { *; }

# Keep the names of the fields used by Gson serialization/deserialization
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# If your models are using @SerializedName annotation, keep it
-keepattributes *Annotation*

# If your models have a default constructor, keep it
-keepclasseswithmembers class * {
    public <init>(...);
}

# Keep the entry point to avoid removing classes with @Keep annotation
-keepclassmembers class * {
    #noinspection ShrinkerUnresolvedReference
    @com.squareup.retrofit2.http.Keep *;
}

# Add ProGuard rules for com.squareup.okhttp3:okhttp library

# OkHttp
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }

# If you are using OkHttp with Retrofit, you might need to add additional rules
-keep class okhttp3.logging.** { *; }

# Okio
-dontwarn okio.**
-keep class okio.** { *; }

# Add ProGuard rules for com.squareup.okhttp3:logging-interceptor library

# OkHttp Logging Interceptor
-dontwarn okhttp3.logging.**
-keep class okhttp3.logging.** { *; }

# Jackson Databind
-keep class com.fasterxml.jackson.** { *; }
-dontwarn com.fasterxml.jackson.databind.**
-dontwarn com.fasterxml.jackson.core.**

# Other Jackson modules
-keep class com.fasterxml.jackson.module.** { *; }
-dontwarn com.fasterxml.jackson.module.**

# Keep all fields with the @JsonProperty annotation
-keepclassmembers,allowobfuscation class * {
    @com.fasterxml.jackson.annotation.JsonProperty *;
}

# Keep all fields with the @JsonCreator annotation
-keepclassmembers,allowobfuscation class * {
    @com.fasterxml.jackson.annotation.JsonCreator *;
}

# Keep serverkey.key and server.crt in the raw directory
-keep class **.R$raw
-keep class **.R$raw$serverkey
-keep class **.R$raw$server

# Keep classes for networking
-keep class com.yourpackage.network.** { *; }

# Keep classes for serialization (if using Gson or other serialization libraries)
-keep class com.google.gson.** { *; }

# Keep the CapturedGPSPojo class
-keep class com.simtech.app1.pojo.layout.CapturedGPSPojo {
    *;
}
# Keep the DAPPojo class
-keep class com.simtech.app1.pojo.dap.DAPPojo {
    *;
}

# Keep the HarvestDetailPojo class
-keep class com.simtech.app1.pojo.harvest.HarvestDetailPojo {
    *;
}

# Keep the HarvestDataPojo class
-keep class com.simtech.app1.pojo.harvest.HarvestDataPojo {
    *;
}


# Keep the PlantatingDetailsPojo class
-keep class com.simtech.app1.pojo.planting.PlantatingDetailsPojo {
    *;
}

# Keep the PlantatingVarietyDataPojo class
-keep class com.simtech.app1.pojo.planting.PlantatingVarietyDataPojo {
    *;
}

#Keep the PlantingPojo class
-keep class com.simtech.app1.pojo.planting.PlantingPojo {
    *;
}