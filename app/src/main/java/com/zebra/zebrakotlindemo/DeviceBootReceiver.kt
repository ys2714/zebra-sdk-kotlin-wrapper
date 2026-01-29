package com.zebra.zebrakotlindemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.zebra.zebrakotlindemo.quickscan.DWQuickScanService
import com.zebra.zebrakotlindemo.quickscan.DWQuickScanServiceConfig
import com.zebra.zebrakotlindemo.quickscan.QuickScanActivity

/* manifest setup
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />

<application>
    <receiver
        android:name=".DeviceBootReceiver"
        android:enabled="true"
        android:exported="true"
        android:permission="android.permission.RECEIVE_BOOT_COMPLETED">
        <intent-filter>
            <action android:name="android.intent.action.BOOT_COMPLETED" />
            <action android:name="android.intent.action.QUICKBOOT_POWERON" />
            <category android:name="android.intent.category.DEFAULT" />
        </intent-filter>
    </receiver>
</application>
 */

class DeviceBootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            context?.let {
                val ctx = it.applicationContext
                Toast.makeText(ctx, "Boot Complete Start DWQuickScanService", Toast.LENGTH_LONG).show()
                DWQuickScanService.start(
                    ctx,
                    DWQuickScanServiceConfig(
                        R.drawable.local_shipping_24px,
                        "Scanner Ready",
                        "push scan button"
                    ),
                    QuickScanActivity::class.java)
            }
        }
    }
}