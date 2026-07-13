import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.zebra.emdk_kotlin_wrapper.mx.MXHelper

class EMDKAppManagerViewModel: ViewModel() {

    fun disableApp(context: Context, packageName: String) {
        try {
            MXHelper.disableApp(context, packageName) { success ->
                val message = if (success) "SUCCESS" else "FAILED"
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "please input integer", Toast.LENGTH_LONG).show()
        }
    }
}