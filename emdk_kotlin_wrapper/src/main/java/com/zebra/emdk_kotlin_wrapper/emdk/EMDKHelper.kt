package com.zebra.emdk_kotlin_wrapper.emdk

import android.content.Context
import com.symbol.emdk.EMDKBase
import com.symbol.emdk.EMDKManager
import com.symbol.emdk.ProfileManager
import com.symbol.emdk.barcode.BarcodeManager

class EMDKHelper private constructor() {

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

    private var _manager: EMDKManager? = null
    private var _barcodeManager: BarcodeManager? = null
    private var _profileManager: ProfileManager? = null

    val barcodeManager: BarcodeManager?
        get() {
            return _barcodeManager
        }

    val profileManager: ProfileManager?
        get() {
            return _profileManager
        }

    fun prepareBarcodeManager(context: Context, callback: (success: Boolean) -> Unit) {
        EMDKManager.getEMDKManager(
            context.applicationContext,
            object : EMDKManager.EMDKListener {
                override fun onOpened(manager: EMDKManager?) {
                    this@EMDKHelper._manager = manager?.also { emdkManager ->
                        emdkManager.getInstanceAsync(
                            EMDKManager.FEATURE_TYPE.BARCODE,
                            object : EMDKManager.StatusListener {
                            override fun onStatus(status: EMDKManager.StatusData?, base: EMDKBase?) {
                                status?.also {
                                    if (it.featureType == EMDKManager.FEATURE_TYPE.BARCODE) {
                                        _barcodeManager = base as? BarcodeManager
                                        callback(true)
                                    } else {
                                        callback(false)
                                    }
                                } ?: run {
                                    callback(false)
                                }
                            }
                        })
                    }
                }

                override fun onClosed() {
                    this@EMDKHelper.teardown()
                }
            })
    }

    fun prepareEMDKProfileManager(context: Context, callback: (success: Boolean) -> Unit) {
        EMDKManager.getEMDKManager(
            context.applicationContext,
            object : EMDKManager.EMDKListener {
                override fun onOpened(manager: EMDKManager?) {
                    this@EMDKHelper._manager = manager?.also { emdkManager ->
                        emdkManager.getInstanceAsync(
                            EMDKManager.FEATURE_TYPE.PROFILE,
                            object : EMDKManager.StatusListener {
                            override fun onStatus(status: EMDKManager.StatusData?, base: EMDKBase?) {
                                status?.also {
                                    if (it.featureType == EMDKManager.FEATURE_TYPE.PROFILE) {
                                        _profileManager = base as? ProfileManager
                                        callback(true)
                                    } else {
                                        callback(false)
                                    }
                                } ?: run {
                                    callback(false)
                                }
                            }
                        })
                    }
                }

                override fun onClosed() {
                    this@EMDKHelper.teardown()
                }
            })
    }

    fun teardown() {
        _manager?.release()
        _manager = null
        _barcodeManager = null
        _profileManager = null
    }
}