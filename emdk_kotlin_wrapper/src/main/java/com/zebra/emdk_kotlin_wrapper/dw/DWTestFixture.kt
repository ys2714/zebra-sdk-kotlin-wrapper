package com.zebra.emdk_kotlin_wrapper.dw

import android.content.Context
import android.os.Handler
import android.os.HandlerThread

object DWTestFixture {

    val handlerThread = HandlerThread("DWIntentFactory_background_thread").apply { start() }
    val handler = Handler(handlerThread.looper)

    fun test(context: Context) {
        DWIntentFactory.callDWAPI(context, DWAPI.ActionExtraKeys.ENABLE_DATAWEDGE, true) { result ->

        }
    }

}