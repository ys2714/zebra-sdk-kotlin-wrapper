package com.zebra.emdk_kotlin_wrapper

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.zebra.emdk_kotlin_wrapper.mx.MXConst
import com.zebra.emdk_kotlin_wrapper.utils.AssetsReader
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppManagerTest {

    @Test
    fun checkAppManagerInstallAndStart() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        try {
            val apkPathValue = "/sdcard/path/to/your/app.apk"
            val appPackageNameValue = "com.zebra.demo"
            val mainActivityClass = "com.zebra.demo.MainActivity"

            val xmlString = AssetsReader.readFileToStringWithParams(
                appContext,
                MXConst.AppManagerInstallAndStartXML,
                mapOf(
                    Pair(MXConst.APK, apkPathValue),
                    Pair(MXConst.Package, appPackageNameValue),
                    Pair(MXConst.Class, mainActivityClass)
                )
            )

            if (xmlString.contains(apkPathValue) &&
                xmlString.contains(appPackageNameValue) &&
                xmlString.contains(mainActivityClass)
            ) {
                // "work as expected"
            } else {
                fail("profile XML params replacement error")
            }
        } catch (e: Exception) {
            fail(e.message)
        }
    }
}
