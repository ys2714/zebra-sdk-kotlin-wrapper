#-dontwarn com.symbol.emdk.**

-keep class com.symbol.** { *; }
-keep interface com.symbol.** { *; }

-keep class com.zebra.emdk.** { *; }
-keep interface com.zebra.emdk.** { *; }

-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod

-keep class com.zebra.emdk_kotlin_wrapper.** implements com.symbol.** { *; }
