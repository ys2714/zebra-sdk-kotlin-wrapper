package com.zebra.emdk_kotlin_wrapper.emdk

import android.content.Context
import com.symbol.emdk.EMDKManager
import com.symbol.emdk.EMDKResults
import com.symbol.emdk.ProfileManager
import com.symbol.emdk.barcode.BarcodeManager
import com.symbol.emdk.barcode.ScanDataCollection
import com.symbol.emdk.barcode.Scanner
import com.symbol.emdk.barcode.ScannerConfig
import com.symbol.emdk.barcode.ScannerException
import com.symbol.emdk.barcode.StatusData
import com.zebra.emdk_kotlin_wrapper.mx.MXProfileProcessor

class EMDKHelper() {

    companion object {
        private var instance: EMDKHelper? = null

        public val shared: EMDKHelper
            get() {
                if (instance == null) {
                    instance = EMDKHelper()
                }
                return instance!!
            }
    }

    private var manager: EMDKManager? = null
    var barcodeManager: BarcodeManager? = null
    var profileManager: ProfileManager? = null

    private lateinit var callback: (success: Boolean) -> Unit


    private inner class EMDKEventHandler: EMDKManager.EMDKListener {
        override fun onOpened(p0: EMDKManager?) {
            manager = p0
            barcodeManager = manager?.getInstance(EMDKManager.FEATURE_TYPE.BARCODE) as? BarcodeManager
            profileManager = manager?.getInstance(EMDKManager.FEATURE_TYPE.PROFILE) as? ProfileManager

            if (manager != null) {
                callback(true)
            } else {
                callback(false)
            }
        }

        override fun onClosed() {
            manager?.release()
            manager = null
            barcodeManager = null
            profileManager = null
        }
    }

    fun prepare(context: Context, callback: (success: Boolean) -> Unit) {
        this.callback = callback
        if (manager != null) {
            callback(true)
            return
        }
        val emdkEventHandler = EMDKEventHandler()
        EMDKManager.getEMDKManager(context, emdkEventHandler)
    }

    fun teardown() {
        manager?.release()
        manager = null
        barcodeManager = null
        profileManager = null
    }
}