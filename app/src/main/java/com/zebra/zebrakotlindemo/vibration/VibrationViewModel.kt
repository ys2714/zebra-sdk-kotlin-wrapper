package com.zebra.zebrakotlindemo.vibration

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.ViewModel

class VibrationViewModel: ViewModel() {

    // Retrieve the system Vibrator service safely based on the Android Version
    private fun vibrator(context: Context): Vibrator? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API 31+ (Android 12)
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            return vibratorManager?.defaultVibrator
        } else { // Legacy retrieval for older APIs
            @Suppress("DEPRECATION")
            return context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun triggerDefault(context: Context) {
        try {
            val effect = VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator(context)?.vibrate(effect)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun triggerClick(context: Context) {
        try {
            val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
            vibrator(context)?.vibrate(effect)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun triggerDoubleClick(context: Context) {
        try {
            val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
            vibrator(context)?.vibrate(effect)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun triggerHeavyClick(context: Context) {
        try {
            val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
            vibrator(context)?.vibrate(effect)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun triggerTick(context: Context) {
        try {
            val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
            vibrator(context)?.vibrate(effect)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}