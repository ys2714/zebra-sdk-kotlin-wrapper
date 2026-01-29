package com.zebra.emdk_kotlin_wrapper.dw

import android.content.Context
import android.os.Bundle
import android.util.Log

abstract class DWVirtualScanner(open val context: Context) {

    val TAG = "DWVirtualScanner"

    abstract val configJSONFileName: String
    abstract val parameters: Map<String, String>

    val profileName: String?
        get() = parameters["PROFILE_NAME"]

    private var dataListener: DataWedgeHelper.ScanDataListener? = null

    open fun open(completion: ((DWVirtualScanner) -> Unit)? = null): DWVirtualScanner {
        DataWedgeHelper.configWithJSON(
            context,
            configJSONFileName,
            parameters
        ) { success ->
            if (success) {
                if (profileName != null) {
                    completion?.invoke(this)
                } else {
                    throw RuntimeException("$TAG profileName is null")
                }
            } else {
                throw RuntimeException("$TAG open failed: $configJSONFileName")
            }
        }
        return this
    }

    open fun select(completion: ((DWVirtualScanner) -> Unit)? = null): DWVirtualScanner {
        profileName?.let {
            DataWedgeHelper.switchProfile(context, it) {
                completion?.invoke(this)
            }
        }
        return this
    }

    open fun switchParamenter(key: String, value: String): DWVirtualScanner {
        DataWedgeHelper.switchScannerParams(
            context,
            Bundle().apply {
                putString(key, value)
            }
        )
        return this
    }

    open fun startListen(onData: (String) -> Unit) {
        if (dataListener != null) {
            return
        }
        dataListener = object : DataWedgeHelper.ScanDataListener {
            override fun onData(
                type: String,
                value: String,
                timestamp: String
            ) {
                onData(value)
            }

            override fun getID(): String {
                return hashCode().toString()
            }

            override fun onDisposal() {
                dataListener = null
            }
        }
        DataWedgeHelper.addScanDataListener(dataListener!!)
    }

    open fun stopListen() {
        if (dataListener != null) {
            DataWedgeHelper.removeScanDataListener(dataListener!!)
            dataListener = null
        }
    }

    open fun startScan(): DWVirtualScanner {
        DataWedgeHelper.softScanTrigger(
            context,
            DWAPI.SoftScanTriggerOptions.START_SCANNING
        )
        return this
    }

    open fun stopScan(): DWVirtualScanner {
        DataWedgeHelper.softScanTrigger(
            context,
            DWAPI.SoftScanTriggerOptions.STOP_SCANNING
        )
        return this
    }

    open fun close(): DWVirtualScanner {
        profileName?.let {
            dataListener?.onDisposal()
            DataWedgeHelper.deleteProfile(context, it) {
                Log.d(TAG, "close DWVirtualScanner success: $it")
            }
        }
        return this
    }
}
