package com.zebra.emdk_kotlin_wrapper.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

object ZebraSystemEventMonitor {

    fun registerScreenOFFListener(context: Context, callback: (Boolean) -> Unit) {
        context.applicationContext.registerReceiver(
            object : BroadcastReceiver() {
                override fun onReceive(
                    context: Context?,
                    intent: Intent?
                ) {
                    if (intent == null) {
                        throw RuntimeException("intent is null")
                    } else {
                        val action = intent.action
                        if (action == Intent.ACTION_SCREEN_OFF) {
                            callback(true)
                        } else if (action == Intent.ACTION_SCREEN_ON) {
                            callback(false)
                        }
                    }
                    context?.unregisterReceiver(this)
                }
            },
            IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_OFF)
                addAction(Intent.ACTION_SCREEN_ON)
            }
        )
    }
}