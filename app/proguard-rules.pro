-optimizationpasses 30
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-mergeinterfacesaggressively
-optimizations !code/simplification/arithmetic
-dontusemixedcaseclassnames
-allowaccessmodification
-useuniqueclassmembernames
-keeppackagenames doNotKeepAThing


-keep public class com.android.installreferrer.** { *; }
-keep class com.appsflyer.** { *; }

 -keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
        public static final *** NULL;      }

    -keepnames @com.google.android.gms.common.annotation.KeepName class *
    -keepclassmembernames class * {
        @com.google.android.gms.common.annotation.KeepName *;
    }
-dontwarn com.google.android.gms.**
-keep class com.google.android.gms.**{ *; }
-keep interface com.google.android.gms.** { *; }
    -keepnames class * implements android.os.Parcelable {
        public static final ** CREATOR;
    }

    -dontwarn rx.**

    -dontwarn okio.**

    -dontwarn com.squareup.okhttp.**
    -keep class com.squareup.okhttp.** { *; }
    -keep interface com.squareup.okhttp.** { *; }

    -dontwarn retrofit.**
    -dontwarn retrofit.appengine.UrlFetchClient
    -keep class retrofit.** { *; }
    -keepclasseswithmembers class * {
        @retrofit.http.* <methods>;
    }

    -keepattributes Signature
    -keepattributes *Annotation*

-keep class com.firebase.** { *; }
-keep class org.apache.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }
-dontwarn org.w3c.dom.**
-dontwarn org.joda.time.**
-dontwarn org.shaded.apache.**
-dontwarn org.ietf.jgss.**
-dontwarn com.onesignal.**
-dontwarn com.appsflyer.**

# These 2 methods are called with reflection.
-keep class com.google.android.gms.common.api.GoogleApiClient {
    void connect();
    void disconnect();
}
-keep class com.onesignal.ActivityLifecycleListenerCompat** {*;}

# Observer backcall methods are called with reflection
-keep class com.onesignal.OSSubscriptionState {
    void changed(com.onesignal.OSPermissionState);
}

-keep class com.onesignal.OSPermissionChangedInternalObserver {
    void changed(com.onesignal.OSPermissionState);
}

-keep class com.onesignal.OSSubscriptionChangedInternalObserver {
    void changed(com.onesignal.OSSubscriptionState);
}

-keep class com.onesignal.OSEmailSubscriptionChangedInternalObserver {
    void changed(com.onesignal.OSEmailSubscriptionState);
}

-keep class ** implements com.onesignal.OSPermissionObserver {
    void onOSPermissionChanged(com.onesignal.OSPermissionStateChanges);
}

-dontwarn okhttp3.**
-dontwarn okio.**