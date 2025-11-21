package com.zebra.emdk_kotlin_wrapper.mx

import android.util.Log
import org.xmlpull.v1.XmlPullParser

internal object XMLParser {

    private val TAG = XMLParser::class.java.simpleName

    // Method to parse the XML response using XML Pull Parser
    fun parseXML(myParser: XmlPullParser): Result<MXBase.ErrorInfo?> {
        try {
            var event = myParser.eventType
            while (event != XmlPullParser.END_DOCUMENT) {
                if (event == XmlPullParser.START_TAG) {
                    when (myParser.name) {
                        "parm-error" -> {
                            return Result.failure(MXBase.ErrorInfo().apply {
                                errorName = "characteristic-error"
                                errorType = "XMLParser"
                                errorDescription = myParser.getAttributeValue(null, "desc")
                            })
                        }
                        "characteristic-error" -> {
                            return Result.failure(MXBase.ErrorInfo().apply {
                                errorName = "characteristic-error"
                                errorType = "XMLParser"
                                errorDescription = myParser.getAttributeValue(null, "desc")
                            })
                        }
                    }
                }
                event = myParser.next()
            }
        } catch (e: Exception) { // Catching generic Exception to include both XmlPullParserException and IOException
            Log.e(TAG, "Failed to parse XML response", e)
            return Result.failure(MXBase.ErrorInfo().apply {
                errorName = "Generic Exception"
                errorType = "XmlPullParserException and IOException"
                errorDescription = e.message ?: "Failed to parse XML response"
            })
        }
        return Result.success(null)
    }
}
