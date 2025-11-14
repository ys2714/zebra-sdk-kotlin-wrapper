package com.zebra.emdk_kotlin_wrapper.mx

import android.util.Log
import org.xmlpull.v1.XmlPullParser

object XMLParser {

    private val TAG = XMLParser::class.java.simpleName

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
