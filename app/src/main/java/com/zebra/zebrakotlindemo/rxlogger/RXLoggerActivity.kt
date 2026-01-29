package com.zebra.zebrakotlindemo.rxlogger

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zebra.emdk_kotlin_wrapper.rxlogger.RXLoggerHelper
import com.zebra.zebrakotlindemo.bridge.CBridge
import com.zebra.zebrakotlindemo.ui.components.RoundButton

class RXLoggerActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RootView(this)
        }
    }

    @Composable
    fun RootView(context: Context) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
            ,
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally) {
            RoundButton("Crash App to get tombstone log", color = Color.Red) {
                RXLoggerHelper.startRXLogger(context)
                CBridge.makeACrash()
                RXLoggerHelper.stopRXLogger(context)
                // throw RuntimeException("Crash App to get tombstone log")
            }
            RoundButton("Save logs to zip file", color = Color.Red) {
                RXLoggerHelper.startRXLogger(context)
                RXLoggerHelper.dumpRXLogger(context)
                RXLoggerHelper.stopRXLogger(context)
            }
        }
    }
}