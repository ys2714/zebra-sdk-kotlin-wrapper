package com.zebra.emdk_kotlin_wrapper

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.zebra.emdk_kotlin_wrapper.mx.MXBase
import com.zebra.emdk_kotlin_wrapper.mx.MXConst
import com.zebra.emdk_kotlin_wrapper.utils.AssetsReader
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MXClockTest {

    /*
    const val isAutoTime = "isAutoTime"
    const val ntpServer = "ntpServer"
    const val syncInterval = "syncInterval"
    const val timeZone = "timeZone"
    const val date = "date"
    const val time = "time"
    const val militaryTime = "militaryTime" // 0: do not change
    * */
    @Test
    fun checkClockSet() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        try {
            // Define the values to replace the placeholders in the XML
            val dateValue = "2024-01-01"
            val timeValue = "12:00:00"
            val isAutoTimeValue = "false"
            val ntpServerValue = "pool.ntp.org"
            val syncIntervalValue = "1440"
            val timeZoneValue = "America/New_York"
            val militaryTimeValue = "1"

            val xmlString = AssetsReader.readFileToStringWithParams(
                appContext,
                MXBase.ProfileXML.ClockSet.string,
                mapOf(
                    Pair(MXConst.Date, dateValue),
                    Pair(MXConst.Time, timeValue),
                    Pair(MXConst.AutoTime, isAutoTimeValue),
                    Pair(MXConst.NTPServer, ntpServerValue),
                    Pair(MXConst.SyncInterval, syncIntervalValue),
                    Pair(MXConst.TimeZone, timeZoneValue),
                    Pair(MXConst.MilitaryTime, militaryTimeValue)
                )
            )
            if (xmlString.contains("=[")) {
                fail("profile XML params replacement error")
            }

            // Verify that the placeholders were successfully replaced
            if (xmlString.contains(dateValue) &&
                xmlString.contains(timeValue) &&
                xmlString.contains(isAutoTimeValue) &&
                xmlString.contains(timeZoneValue) // Just checking a few more for sanity
            ) {
                // work as expected
            } else {
                fail("ClockSetXML XML params replacement error: $xmlString")
            }
        } catch (e: Exception) {
            // If any exception occurs, fail the test and report the error
            fail(e.message)
        }
    }
}
