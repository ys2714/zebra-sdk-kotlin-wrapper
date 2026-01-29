import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

open class ZebraBaseComponentActivity: ComponentActivity() {

    // methods for subclass overriding
    open fun handlePermissionGranted() {}
    open fun handlePermissionDenied() {}

    fun getAppVersion() : String? {
        try {
            val pInfo: PackageInfo = getPackageManager().getPackageInfo(getPackageName(), 0)
            val version = pInfo.versionName
            return version
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return null
        }
    }

    fun <T> startActivityWithClass(javaClass: Class<T>, flags: Int? = null) {
        val intent = Intent(this, javaClass)
        flags?.let {
            intent.addFlags(it)
        }
        startActivity(intent)
    }

    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, you can now display notifications
            handlePermissionGranted()
        } else {
            // Permission denied, handle appropriately
            handlePermissionDenied()
        }
    }

    // Manifest.permission.POST_NOTIFICATIONS
    fun requestPermissionWithName(permissionName: String) {
        when {
            ContextCompat.checkSelfPermission(
                this,
                permissionName
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission already granted, proceed with showing notifications
                handlePermissionGranted()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                permissionName
            ) -> {
                // Show rationale and request permission
                // This is where you can show a dialog explaining why the permission is needed
                requestPermissionLauncher.launch(permissionName)
            }
            else -> {
                // Directly request for permission
                requestPermissionLauncher.launch(permissionName)
            }
        }
    }
}