package com.zebra.zebrakotlindemo

import android.content.Context
import android.widget.Toast
import com.zebra.emdk_kotlin_wrapper.emdk.EMDKHelper
import com.zebra.emdk_kotlin_wrapper.mx.MXBase
import com.zebra.emdk_kotlin_wrapper.mx.MXProfileProcessor
import com.zebra.emdk_kotlin_wrapper.mx.getCallServicePermission
import com.zebra.emdk_kotlin_wrapper.zdm.ZDMAuthHelper
import com.zebra.emdk_kotlin_wrapper.zdm.ZDMConst
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel {

    private var profileProcessor: MXProfileProcessor? = null

    var appAuthenticated: Boolean = false

    fun prepare(context: Context) {
        EMDKHelper.shared.prepare(context) {
            profileProcessor = MXProfileProcessor(context)
            authenticateApp(context)
        }
    }

    fun authenticateApp(context: Context) {
        whiteListApproveApp(context) { success ->
            if (success) {
                // Token saved in ZDMTokenStore
                getDWToken(context)
                appAuthenticated = true
            } else {
                appAuthenticated = false
            }
        }
    }

    fun whiteListApproveApp(context: Context, callback: (Boolean) -> Unit) {
        profileProcessor?.getCallServicePermission(
            context,
            ZDMConst.DelegationScope.SCOPE_DW_CONFIG_API.value,
            object : MXBase.ProcessProfileCallback {
                override fun onSuccess(profileName: String) {
                    showDebugToast(context, "Approve App Success", profileName)
                    callback(true)
                }

                override fun onError(errorInfo: MXBase.ErrorInfo) {
                    showDebugToast(context, "Approve App Error", errorInfo.errorDescription)
                    callback(false)
                }
            }
        )
    }

    fun getDWToken(context: Context) {
        val token = ZDMAuthHelper.acquireToken(context, ZDMConst.DelegationScope.SCOPE_DW_CONFIG_API) ?: "UNKNOWN"
        showDebugToast(context, "Token", token)
    }

    fun showDebugToast(context: Context, type: String, data: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context,
                "$type\n$data",
                Toast.LENGTH_LONG).show()
        }
    }
}