package com.zebra.emdk_kotlin_wrapper.dw

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Parcelable
import android.util.Log
import androidx.core.content.ContextCompat
import com.zebra.emdk_kotlin_wrapper.dw.DWIntentFactory.TAG
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object DWTestFixture {

    val handlerThread = HandlerThread("DWIntentFactory_background_thread").apply { start() }
    val handler = Handler(handlerThread.looper)

    fun test(context: Context) {
        DWIntentFactory.callDWAPI(context, DWAPI.ActionExtraKeys.ENABLE_DATAWEDGE, true) { result ->

        }
    }

}