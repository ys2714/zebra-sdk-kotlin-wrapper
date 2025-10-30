package com.zebra.zebrakotlindemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.zebra.emdk_kotlin_wrapper.dw.DWAPI

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RootView(this)
        }
    }

    override fun onResume() {
        super.onResume()
        DWAPI.disableDW(this.applicationContext)
    }

    override fun onDestroy() {
        super.onDestroy()
        DWAPI.enableDW(this.applicationContext)
    }

    @Composable
    fun RootView(context: Context) {
        Column {
            RoundButton("EMDK") {
                startActivity(Intent(context, EMDKActivity::class.java))
            }
            RoundButton("DataWedge") {
                startActivity(Intent(context, DataWedgeActivity::class.java))
            }
        }
    }
}