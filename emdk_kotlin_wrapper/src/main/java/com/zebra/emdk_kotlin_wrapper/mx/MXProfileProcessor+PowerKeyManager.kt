package com.zebra.emdk_kotlin_wrapper.mx

/**
 * https://techdocs.zebra.com/emdk-for-android/14-0/mx/powerkeymgr/
 *
 * Power-off Button Show/Hide
 * Parm Name: PowerOffState
 *
 * Options:
 * 0 - Do not change: This value (or the absence of this parm from the XML) causes no change to the Power-off Menu; any previously selected setting is retained.
 * 1 - Show: Enables the Power-off button to be shown after the device power key is long-pressed.
 * 2 - Hide: Prevents the Power-off button from being shown after the device power key is long-pressed.
 * */

fun MXProfileProcessor.powerKeyMenuEnablePowerOffButton() {

}

fun MXProfileProcessor.powerKeyMenuDisablePowerOffButton() {

}

// also check DisplayManager
fun MXProfileProcessor.powerKeyMenuDisableScreenshotButton() {

}

fun MXProfileProcessor.powerKeyMenuDisableRebootButton() {

}
