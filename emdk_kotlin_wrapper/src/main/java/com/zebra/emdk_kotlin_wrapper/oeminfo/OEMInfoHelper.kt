package com.zebra.emdk_kotlin_wrapper.oeminfo

import android.content.Context
import android.net.Uri
import android.util.Log

/**
 * A utility object for retrieving OEM information from the device.
 */
object OEMInfoHelper {

    private val TAG = OEMInfoHelper::class.java.simpleName

    /**
     * Retrieves OEM information, such as serial number or IMEI, from a content provider.
     * This method should be called from a background thread to avoid blocking the UI.
     *
     * @param ctx       The context to use for accessing the content resolver.
     * @param serviceId The URI of the content provider service.
     * @return The requested OEM information as a String, or null if it cannot be retrieved.
     */
    fun getOEMInfo(ctx: Context, serviceId: String): String? {
        // Use the 'use' extension function to ensure the cursor is automatically closed.
        try {
            ctx.contentResolver.query(Uri.parse(serviceId), null, null, null, null)?.use { cursor ->
                // Check if the cursor is valid and contains at least one row.
                if (cursor.moveToFirst() && cursor.columnCount > 0) {
                    return cursor.getString(0)
                }
            }
        } catch (e: Exception) {
            // Log any exceptions that occur during the query.
            Log.e(TAG, "Error querying OEM info for service: $serviceId", e)
        }
        // Return null if the query fails or no data is found.
        return null
    }
}
