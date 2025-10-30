package com.zebra.emdk_kotlin_wrapper.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import android.util.Base64
import android.util.Log

/**
 * A utility object for retrieving package information, such as the application's signature.
 */
object PackageManagerHelper {

    private val TAG = PackageManagerHelper::class.java.simpleName

    /**
     * Retrieves the Base64-encoded signature of the application package.
     *
     * @param context The context to use for accessing the package manager.
     * @return The Base64-encoded signature as a String, or an empty string if an error occurs.
     */
    fun getPackageSignature(context: Context): String {
        return try {
            val signatures = getSigningCertificates(context)
            // A package must be signed, so the signature array should not be empty.
            if (signatures.isNotEmpty()) {
                // We are interested in the first signature.
                val signature = signatures[0]
                Base64.encodeToString(signature.toByteArray(), Base64.NO_WRAP)
            } else {
                Log.w(TAG, "No signatures found for package: " + context.packageName)
                ""
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "Failed to get package signature. Package not found.", e)
            ""
        }
    }

    /**
     * Retrieves the signing certificates for the current application package.
     * This method handles the differences between modern (API 28+) and legacy Android versions.
     *
     * @param context The context to use for accessing the package manager.
     * @return An array of [Signature] objects.
     * @throws PackageManager.NameNotFoundException if the package name is not found.
     */
    @SuppressLint("PackageManagerGetSignatures")
    @Throws(PackageManager.NameNotFoundException::class)
    private fun getSigningCertificates(context: Context): Array<Signature> {
        val packageName = context.packageName
        val pm = context.packageManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // For Android P (API 28) and above, use GET_SIGNING_CERTIFICATES
            val packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            packageInfo.signingInfo?.apkContentsSigners ?: emptyArray()
        } else {
            // For older versions, use the deprecated GET_SIGNATURES
            @Suppress("DEPRECATION")
            val packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            @Suppress("DEPRECATION")
            packageInfo.signatures ?: emptyArray()
        }
    }
}
