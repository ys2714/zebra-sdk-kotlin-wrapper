package com.zebra.zebrakotlindemo.deeplink

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.ViewModel

/*
https://developer.android.com/training/app-links

there is no official, direct, and universally supported deep link that opens only the nested "Preferred Network Type" dialog itself
This limitation exists due to security policies, OS version changes, and carrier-specific customizations (some carriers explicitly hide this option)
However, you can jump directly to its immediate parent screen (the Mobile Network / SIM Settings page)
*/

class DeeplinkViewModel: ViewModel() {

    /***
     * intent:#Intent;action=android.settings.NETWORK_OPERATOR_SETTINGS;package=com.android.settings;end
     *
     * This launches the standard SIM and Mobile Network settings page, where the user can click "Preferred network type"
     * by intent. Standard & Safe way
     * */
    fun openMobileNetworkSettings(context: Context, simSubscriptionId: String = "") {
        val intent = Intent(android.provider.Settings.ACTION_NETWORK_OPERATOR_SETTINGS)
        // Optional (for Android 10+): If you want to specify a particular SIM slot
        if (!simSubscriptionId.isEmpty()) {
            intent.putExtra("android.provider.extra.SUB_ID", simSubscriptionId)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    /***
     * intent:#Intent;action=android.settings.NETWORK_OPERATOR_SETTINGS;package=com.android.settings;component=com.android.settings/.Settings$MobileNetworkActivity;end
     *
     * This bypasses generic routing and launches the concrete settings class directly
     * */
    fun openMobileNetworkSettingsByActivity(context: Context) {
        val intent = Intent()
        intent.setClassName(
            "com.android.settings",
            "com.android.settings.Settings\$MobileNetworkActivity"
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /***
     * intent:#Intent;action=android.intent.action.MAIN;package=com.android.settings;component=com.android.settings/.RadioInfo;end
     *
     * This opens the system's hidden testing menu (traditionally accessed via *#*#4636#*#*)
     * This screen contains the Set Preferred Network Type dropdown and works even if your carrier has hidden the setting from standard menu screens
     * */
    fun openHiddenRadioInfoMenu(context: Context) {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.setClassName(
            "com.android.settings",
            "com.android.settings.RadioInfo")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}