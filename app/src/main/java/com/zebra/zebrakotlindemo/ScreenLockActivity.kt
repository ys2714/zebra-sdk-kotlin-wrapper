package com.zebra.zebrakotlindemo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.layout.boundsInWindow

class ScreenLockActivity : ComponentActivity() {

    companion object {
        fun start(fromActivity: Activity) {
            fromActivity.startActivity(
                Intent(fromActivity, ScreenLockActivity::class.java).apply {
                    setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setFullscreen(this)
    }

    override fun onResume() {
        super.onResume()
        setContent {
            RootView(this)
        }
        setFullscreen(this)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        setFullscreen(this)
    }

    fun setFullscreen(activity: Activity) {
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        activity.window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }

        onBackPressedDispatcher.addCallback {
            // do nothing
        }
    }

    @Composable
    fun RootView(context: Context) {
        val buttonModifier = Modifier.padding(16.dp)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .consumeAllTouchBut(buttonModifier)
            ,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text("----Lock Screen----",
                modifier = Modifier
                    .padding()
            )
            RoundButton("Unlock", modifier = buttonModifier) {
                finish()
            }
        }
    }

    fun Offset.isInside(modifier: Modifier): Boolean {
        var bounds = androidx.compose.ui.geometry.Rect.Zero
        modifier.onGloballyPositioned { coordinates ->
            bounds = coordinates.boundsInWindow()
        }
        return bounds.contains(this)
    }

    fun Modifier.consumeAllTouchBut(
        buttonModifier: Modifier, // The modifier of the button to keep enabled
        onDown: () -> Unit = {}
    ): Modifier = this.then(
        Modifier.pointerInput(Unit) {
            awaitEachGesture {
                val down = awaitFirstDown(pass = PointerEventPass.Main)
                // Only consume if the touch is not within the bounds of the enabled button
                if (!down.isConsumed && !down.position.isInside(buttonModifier)) { // You'll need to define isInside
                    down.consume()
                    onDown()
                }
            }
        }
    )
}