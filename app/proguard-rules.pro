# Basic R8/ProGuard rules
-keepattributes SourceFile,LineNumberTable,*Annotation*,Signature,Exceptions,InnerClasses
-renamesourcefileattribute SourceFile
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

-keep class **.R
-keep class **.R$* {
    <fields>;
}

# CandyBar specific rules
-keep class com.candybar.** { *; }
-keep class com.danimahardhika.** { *; }
-keep interface com.candybar.** { *; }
-keep class candybar.lib.** { *; }
-keepclassmembers class * {
    @com.candybar.annotations.** *; 
}

# Amazon SDK rules
-keep class com.amazon.** { *; }
-keep interface com.amazon.** { *; }
-keepclassmembers class com.amazon.** { *; }
-dontwarn com.amazon.**

# Keep all interfaces and their implementations
-keep interface * { *; }
-keep class * implements java.io.Serializable { *; }
-keep class * implements java.lang.annotation.Annotation { *; }

# Keep library utils
-keep class candybar.lib.utils.** { *; }
-keep class candybar.lib.preferences.** { *; }

# Keep Activities, Fragments, and their methods
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}
-keepclassmembers class * extends androidx.fragment.app.Fragment {
    public void *(android.view.View);
}

# Keep enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelables
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Apache Commons
-keep class org.apache.commons.** { *; }
-keep interface org.apache.commons.** { *; }
-dontwarn org.apache.commons.**

# Specific rule for ToStringStyle
-keepclassmembers class org.apache.commons.lang3.builder.ToStringStyle {
    public static final org.apache.commons.lang3.builder.ToStringStyle *;
}

# Lombok
-keep class lombok.** { *; }
-dontwarn lombok.**
-keepclassmembers class * {
    @lombok.* *;
}

# Amazon SDK with Lombok
-keep class com.amazon.device.** { *; }
-keepclassmembers class com.amazon.device.** {
    @lombok.* *;
}
-dontwarn com.amazon.device.**

# Handle missing stack map table warnings
-keepattributes StackMapTable
-keep class com.amazon.appstore.** { *; }
-dontwarn com.amazon.appstore.**