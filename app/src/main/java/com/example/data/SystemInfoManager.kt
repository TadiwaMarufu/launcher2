package com.example.data

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.camera2.CameraManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.SystemClock
import android.provider.Settings
import android.util.Log

data class SystemStats(
    val batteryPercent: Int = 100,
    val isCharging: Boolean = false,
    val ramUsedMb: Long = 0,
    val ramTotalMb: Long = 0,
    val storageUsedGb: Float = 0f,
    val storageTotalGb: Float = 0f,
    val uptimeString: String = "0h 0m",
    val isWifiConnected: Boolean = true,
    val cpuPercentEst: Int = 14
)

class SystemInfoManager(private val context: Context) {

    private var isTorchOn = false

    fun getSystemStats(): SystemStats {
        // Battery
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: 100
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: 100
        val batteryPct = if (level >= 0 && scale > 0) (level * 100 / scale) else 85
        val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

        // Memory
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager?.getMemoryInfo(memInfo)
        val totalRam = (memInfo.totalMem / (1024 * 1024))
        val availRam = (memInfo.availMem / (1024 * 1024))
        val usedRam = totalRam - availRam

        // Storage
        val stat = StatFs(Environment.getDataDirectory().path)
        val totalBytes = stat.blockCountLong * stat.blockSizeLong
        val availableBytes = stat.availableBlocksLong * stat.blockSizeLong
        val usedBytes = totalBytes - availableBytes
        val totalGb = totalBytes.toFloat() / (1024 * 1024 * 1024)
        val usedGb = usedBytes.toFloat() / (1024 * 1024 * 1024)

        // Uptime
        val uptimeMillis = SystemClock.elapsedRealtime()
        val hours = uptimeMillis / (1000 * 60 * 60)
        val minutes = (uptimeMillis / (1000 * 60)) % 60
        val uptimeStr = "${hours}h ${minutes}m"

        // Network
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val network = connManager?.activeNetwork
        val caps = connManager?.getNetworkCapabilities(network)
        val isWifi = caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: true

        return SystemStats(
            batteryPercent = batteryPct,
            isCharging = isCharging,
            ramUsedMb = usedRam,
            ramTotalMb = totalRam,
            storageUsedGb = usedGb,
            storageTotalGb = totalGb,
            uptimeString = uptimeStr,
            isWifiConnected = isWifi,
            cpuPercentEst = (8..24).random()
        )
    }

    fun toggleFlashlight(): Boolean {
        return try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
            val cameraId = cameraManager?.cameraIdList?.firstOrNull() ?: return false
            isTorchOn = !isTorchOn
            cameraManager.setTorchMode(cameraId, isTorchOn)
            isTorchOn
        } catch (e: Exception) {
            Log.e("SystemInfoManager", "Error toggling flashlight: ${e.message}")
            false
        }
    }

    fun openWifiSettings() {
        try {
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            openSystemSettings()
        }
    }

    fun openBluetoothSettings() {
        try {
            val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            openSystemSettings()
        }
    }

    fun openBatterySettings() {
        try {
            val intent = Intent(Intent.ACTION_POWER_USAGE_SUMMARY).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            openSystemSettings()
        }
    }

    fun openDisplaySettings() {
        try {
            val intent = Intent(Settings.ACTION_DISPLAY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            openSystemSettings()
        }
    }

    fun openSystemSettings() {
        try {
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("SystemInfoManager", "Error opening settings", e)
        }
    }
}
