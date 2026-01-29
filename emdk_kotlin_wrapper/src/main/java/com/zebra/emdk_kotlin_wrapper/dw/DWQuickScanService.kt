package com.zebra.emdk_kotlin_wrapper.dw

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.zebra.emdk_kotlin_wrapper.emdk.EMDKHelper

/*
Device Boot () -> Top (switch profile) -> Scan (switch parameter)
1.Create Profile (Background Service) > 2.Switch Profile (UI change. MainActivity) > 3.Switch Parameter
*/

class DWQuickScanServiceConfig(
    val iconResId: Int,
    val title: String,
    val desc: String
)

/*
need following permissions in AndroidManifest.xml:
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_DATA_SYNC" />

<service
android:name=".DWQuickScanService"
android:enabled="true"
android:exported="true"
android:foregroundServiceType="dataSync" />
*/
class DWQuickScanService : Service() {

    enum class ScannerKey(val value: String) {
        KEY_SCANNER_WORKFLOW("KEY_SCANNER_WORKFLOW"),
        KEY_SCANNER_BARCODE("KEY_SCANNER_BARCODE"),
    }

    companion object Static {

        private var instance: DWQuickScanService? = null

        private val CHANNEL_ID = "QuickScanService_Channel"
        private val CHANNEL_NAME = "QuickScanService Channel"
        private val CHANNEL_IMPORTANCE = NotificationManager.IMPORTANCE_HIGH
        private val NOTIFICATION_PRIORITY = NotificationCompat.PRIORITY_LOW

        val shared: DWQuickScanService?
            get() = instance

        fun isRunning(): Boolean {
            return instance != null
        }

        fun isPrepared(): Boolean {
            return instance?._zebraDataWedgePrepared ?: false
        }

        fun<T> start(context: Context,
                     config: DWQuickScanServiceConfig,
                     activityClass: Class<T>) {
            val ctx = context.applicationContext
            val intent = Intent(ctx, activityClass)
            val pendingIntent = PendingIntent.getActivity(
                ctx,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE)

            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                CHANNEL_IMPORTANCE
            )
            val manager = ctx.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)

            val pendingNotification = NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(config.iconResId)
                .setContentTitle(config.title)
                .setContentText(config.desc)
                .setPriority(NOTIFICATION_PRIORITY)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build()

            val serviceIntent = Intent(ctx, DWQuickScanService::class.java)
            serviceIntent.putExtra("PENDING_NOTIFICATION", pendingNotification)
            ctx.startForegroundService(serviceIntent)
        }
    }

    private var _zebraDataWedgePrepared: Boolean = false

    private val scanners: MutableMap<String, DWVirtualScanner>
        = emptyMap<String, DWVirtualScanner>().toMutableMap()

    var currentScanner: DWVirtualScanner? = null

    fun findScanner(key: ScannerKey): DWVirtualScanner {
        return scanners[key.value]!!
    }

    fun selectScanner(key: ScannerKey): DWVirtualScanner {
        currentScanner = scanners[key.value]!!.select()
        return currentScanner!!
    }

    fun startScan() {
        currentScanner?.startScan()
    }

    fun stopScan() {
        currentScanner?.stopScan()
    }

    fun startListen(onData: (String) -> Unit) {
        currentScanner?.startListen(onData)
    }

    fun stopListen() {
        currentScanner?.stopListen()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelableExtra("PENDING_NOTIFICATION", Notification::class.java)?.let { notification ->
                    startForeground(1, notification)
                }
            } else {
                it.getParcelableExtra<Notification>("PENDING_NOTIFICATION")?.let { notification ->
                    startForeground(1, notification)
                }
            }
        }
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        prepareZebraDataWedge(this.applicationContext)
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            CHANNEL_IMPORTANCE
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(serviceChannel)
    }

    private fun prepareZebraDataWedge(context: Context) {
        EMDKHelper.shared.prepare(context) {
            DataWedgeHelper.prepare(context) {
                scanners[ScannerKey.KEY_SCANNER_WORKFLOW.value] = DWWorkflowFreeFormScanner(context).open()
                scanners[ScannerKey.KEY_SCANNER_BARCODE.value] = DWBarcodeScanner(context).open().select() {
                    _zebraDataWedgePrepared = true
                }
            }
        }
    }
}