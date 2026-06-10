package com.zebra.zebrakotlindemo.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import java.net.URISyntaxException

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun IntentSensitiveWebView(url: String, modifier: Modifier = Modifier) {
    // Keep a reference to the WebView instance to handle back navigation
    var webViewReference by remember { mutableStateOf<WebView?>(null) }

    // Intercept system back button presses
    BackHandler(enabled = webViewReference?.canGoBack() == true) {
        webViewReference?.goBack()
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                // Configure WebView settings
                settings.apply {
                    javaScriptEnabled = true // Enable JS if needed (requires @SuppressLint("SetJavaScriptEnabled"))
                    domStorageEnabled = true // Support modern rich-media websites
                    loadWithOverviewMode = true
                    useWideViewPort = true
                }

                // Force links to open inside this WebView rather than the system browser
                webViewClient = CustomWebViewClient(context)

                // Load initial URL
                loadUrl(url)

                // Save reference
                webViewReference = this
            }
        },
        update = { webView ->
            // If the URL parameter changes dynamically, update the webView
            if (webView.url != url) {
                webView.loadUrl(url)
            }
            webViewReference = webView
        },
        onRelease = { webView ->
            // Clean up the WebView resource when it leaves the Composition
            webView.stopLoading()
            webView.destroy()
        }
    )
}

// Custom WebViewClient to capture and parse Android intents
class CustomWebViewClient(private val context: Context) : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val url = request?.url?.toString() ?: return false

        // 1. Intercept URLs starting with "intent://"
        if (url.startsWith("intent://") || url.startsWith("intent:#")) {

            launchSettingsFromUri(context, url)

            return true // Prevents WebView from loading the raw "intent://" URL (avoiding ERR_UNKNOWN_URL_SCHEME)
        }

        // Return false for normal web protocols (http, https) so they load inside the webview as usual
        return false
    }

    private fun launchSettingsFromUri(context: Context, intentUriString: String) {
        try {
            // 2. Parse the string into a native Android Intent object
            // The Intent.URI_INTENT_SCHEME flag tells Android to decode the "intent:" syntax
            val intent = Intent.parseUri(intentUriString, Intent.URI_INTENT_SCHEME)

            // 3. Since we are calling this from outside an Activity context (or to ensure safety),
            // add the NEW_TASK flag.
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            // 4. Verify that there is an app (Settings app) on the device that can handle this Intent
            if (intent.resolveActivity(context.packageManager) != null) {
                // 5. Fire the intent to open the Mobile Network settings screen
                context.startActivity(intent)
            } else {
                // Fallback: Show a message to the user if the settings page isn't available
                Toast.makeText(context, "Settings screen not found on this device.", Toast.LENGTH_SHORT).show()
            }

        } catch (e: URISyntaxException) {
            // Handle malformed URI string
            e.printStackTrace()
            Toast.makeText(context, "Invalid URI scheme format.", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            // Handle potential permission/sandboxing restrictions
            e.printStackTrace()
            Toast.makeText(context, "Access Denied: Cannot open this screen.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }
}