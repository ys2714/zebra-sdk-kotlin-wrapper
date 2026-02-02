package com.zebra.zebrakotlindemo.emdk

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zebra.zebrakotlindemo.ui.components.RoundButton

class EMDKActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
           RootView(this)
        }
    }

    @Composable
    fun RootView(context: Context) {
        Column(
            Modifier
                .padding(horizontal = 16.dp)
        ) {
            Text("Control Device use Zebra MX API",
                modifier = Modifier
                    .padding()
            )
            RoundButton("Power Management Demo") {
                startActivity(Intent(context, EMDKPowerManagementActivity::class.java))
            }
            RoundButton("Power Button Menu Control Demo") {
                startActivity(Intent(context, EMDKPowerButtonMenuActivity::class.java))
            }
            RoundButton("System Clock Control Demo") {
                startActivity(Intent(context, EMDKSystemClockActivity::class.java))
            }
            RoundButton("Lock Screen Control Demo") {
                startActivity(Intent(context, EMDKLockScreenActivity::class.java))
            }
            RoundButton("USB Client Mode Control Demo") {
                startActivity(Intent(context, EMDKUSBManagerActivity::class.java))
            }
            RoundButton("Recovery Mode Access Control Demo", color = Color(0xFFD10000)) {
                startActivity(Intent(context, EMDKRecoveryModeActivity::class.java))
            }
        }
    }
}