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

-dontwarn kotlinx.parcelize.Parcelize
-dontwarn javax.servlet.ServletContainerInitializer
-dontwarn com.squareup.anvil.annotations.ContributesBinding
-dontwarn com.squareup.anvil.annotations.ContributesMultibinding$Container
-dontwarn com.squareup.anvil.annotations.ContributesTo
-dontwarn com.squareup.anvil.annotations.internal.InternalMergedTypeMarker
-dontwarn com.squareup.anvil.annotations.ContributesMultibinding
-dontwarn com.squareup.anvil.annotations.MergeComponent
-dontwarn org.apiguardian.api.API
-keep class com.squareup.anvil.annotations.** { *; }
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}
-dontwarn java.applet.Applet
-dontwarn java.awt.BorderLayout
-dontwarn java.awt.Canvas
-dontwarn java.awt.Color
-dontwarn java.awt.Component
-dontwarn java.awt.Container
-dontwarn java.awt.Cursor
-dontwarn java.awt.Dimension
-dontwarn java.awt.Frame
-dontwarn java.awt.Image
-dontwarn java.awt.Insets
-dontwarn java.awt.LayoutManager
-dontwarn java.awt.Point
-dontwarn java.awt.Scrollbar
-dontwarn java.awt.Toolkit
-dontwarn java.awt.event.KeyAdapter
-dontwarn java.awt.event.KeyListener
-dontwarn java.awt.event.MouseAdapter
-dontwarn java.awt.event.MouseListener
-dontwarn java.awt.event.MouseMotionListener
-dontwarn java.awt.event.WindowAdapter
-dontwarn java.awt.event.WindowListener
-dontwarn java.awt.image.ColorModel
-dontwarn java.awt.image.ImageObserver
-dontwarn java.awt.image.ImageProducer
-dontwarn java.lang.management.ManagementFactory
-dontwarn org.apiguardian.api.API$Status