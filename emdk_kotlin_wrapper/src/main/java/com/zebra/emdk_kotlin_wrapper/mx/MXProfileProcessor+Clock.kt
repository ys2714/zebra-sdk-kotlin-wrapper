package com.zebra.emdk_kotlin_wrapper.mx

import android.content.Context
import com.symbol.emdk.ProfileManager

/**
 *
 * https://techdocs.zebra.com/mx/clock/
 *
 *
 * */
fun MXProfileProcessor.callClockSetAuto(
    context: Context,
    profileManager: ProfileManager,
    is24Hours: Boolean,
    ntpServer: String,
    syncInterval: String,
    callback: MXBase.ProcessProfileCallback) {
    val militaryTime = if (is24Hours) "1" else "2"
    val map = mapOf(
        MXConst.AutoTime to "true",
        MXConst.NTPServer to ntpServer,
        MXConst.SyncInterval to syncInterval,
        MXConst.TimeZone to MXConst.ignoredValue,
        MXConst.Date to MXConst.ignoredValue,
        MXConst.Time to MXConst.ignoredValue,
        MXConst.MilitaryTime to militaryTime
    )
    processProfileWithCallback(
        context,
        profileManager,
        MXBase.ProfileXML.ClockSet,
        MXBase.ProfileName.ClockSet,
        map,
        callback
    )
}

fun MXProfileProcessor.callClockSet(
    context: Context,
    profileManager: ProfileManager,
    is24Hours: Boolean,
    timeZone: String,
    date: String,
    time: String,
    callback: MXBase.ProcessProfileCallback) {
    val militaryTime = if (is24Hours) "1" else "2"
    val map = mapOf(
        MXConst.AutoTime to "false",
//        MXConst.NTPServer to MXConst.ignoredValue,
//        MXConst.SyncInterval to MXConst.ignoredValue,
        MXConst.TimeZone to timeZone,
        MXConst.Date to date,
        MXConst.Time to time,
        MXConst.MilitaryTime to militaryTime
    )
    processProfileWithCallback(
        context,
        profileManager,
        MXBase.ProfileXML.ClockSet,
        MXBase.ProfileName.ClockSet,
        map,
        callback
    )
}