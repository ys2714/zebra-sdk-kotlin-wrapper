package com.zebra.emdk_kotlin_wrapper.mx

fun MXProfileProcessor.callClockSetAuto(is24Hours: Boolean, ntpServer: String, syncInterval: String, callback: MXBase.ProcessProfileCallback) {
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
        MXConst.ClockSetXML,
        MXConst.ClockSet,
        map,
        callback
    )
}

fun MXProfileProcessor.callClockSet(is24Hours: Boolean, timeZone: String, date: String, time: String, callback: MXBase.ProcessProfileCallback) {
    val militaryTime = if (is24Hours) "1" else "2"
    val map = mapOf(
        MXConst.AutoTime to "false",
        MXConst.NTPServer to MXConst.ignoredValue,
        MXConst.SyncInterval to MXConst.ignoredValue,
        MXConst.TimeZone to timeZone,
        MXConst.Date to date,
        MXConst.Time to time,
        MXConst.MilitaryTime to militaryTime
    )
    processProfileWithCallback(
        MXConst.ClockSetXML,
        MXConst.ClockSet,
        map,
        callback
    )
}