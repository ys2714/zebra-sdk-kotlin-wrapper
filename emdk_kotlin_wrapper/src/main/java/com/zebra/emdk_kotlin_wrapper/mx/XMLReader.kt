package com.zebra.emdk_kotlin_wrapper.mx

import android.content.Context
import android.util.Log
import org.xmlpull.v1.XmlPullParser
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object XMLReader {

    private val TAG = XMLReader::class.java.simpleName

    fun readXmlFileToStringWithParams(context: Context, resourceId: String, params: Map<String, String>?): String {
        var command = readXmlFileToString(context, resourceId).trim()
        params?.forEach { (key, value) ->
            val placeholder = "\${$key}"
            command = command.replace(placeholder, value)
        }
        return command
    }

    @JvmOverloads
    fun readXmlFileToString(context: Context, resourceId: String, newline: Boolean = false): String {
        val stringBuilder = StringBuilder()
        val assetManager = context.assets

        try {
            assetManager.open(resourceId).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        stringBuilder.append(line?.trim())
                        if (newline) {
                            stringBuilder.append("\n")
                        }
                    }
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error reading XML file from assets: $resourceId", e)
        }
        return stringBuilder.toString()
    }

    // Method to parse the XML response using XML Pull Parser
    fun parseXML(myParser: XmlPullParser): MXBase.ErrorInfo? {
        try {
            var event = myParser.eventType
            while (event != XmlPullParser.END_DOCUMENT) {
                if (event == XmlPullParser.START_TAG) {
                    when (myParser.name) {
                        "parm-error" -> {
                            return MXBase.ErrorInfo().apply {
                                errorName = myParser.getAttributeValue(null, "name")
                                errorDescription = myParser.getAttributeValue(null, "desc")
                            }
                        }
                        "characteristic-error" -> {
                            return MXBase.ErrorInfo().apply {
                                errorType = myParser.getAttributeValue(null, "type")
                                errorDescription = myParser.getAttributeValue(null, "desc")
                            }
                        }
                    }
                }
                event = myParser.next()
            }
        } catch (e: Exception) { // Catching generic Exception to include both XmlPullParserException and IOException
            Log.e(TAG, "Failed to parse XML response", e)
            return MXBase.ErrorInfo().apply {
                errorName = "Generic Exception"
                errorType = "XmlPullParserException and IOException"
                errorDescription = e.message ?: "Failed to parse XML response"
            }
        }
        return null
    }
}
