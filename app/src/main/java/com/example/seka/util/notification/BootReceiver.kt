package com.example.seka.util.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.seka.util.workers.WorkerScheduler

/**
 * Receiver yang menerima broadcast ketika perangkat selesai boot,
 * untuk memulai ulang semua worker yang terjadwal.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Jadwalkan ulang semua worker
            WorkerScheduler.scheduleAllWorkersStatic(context)
        }
    }
}