package com.example.seka.util.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.seka.data.repository.AirMinumRepository
import com.example.seka.util.notification.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * Worker untuk mengirim pengingat minum air berkala
 */
@HiltWorker
class AirMinumReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val airMinumRepository: AirMinumRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "air_minum_reminder_worker"

        // Method to schedule the water reminder - run every 15 minutes to check
        // Reduced from 30 minutes to 15 minutes for more responsive reminders
        fun schedule(workManager: WorkManager) {
            val reminderRequest = PeriodicWorkRequestBuilder<AirMinumReminderWorker>(
                15, TimeUnit.MINUTES
            )
                .setInitialDelay(1, TimeUnit.MINUTES) // Start almost immediately after setting
                .build()

            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE, // Using REPLACE instead of CANCEL_AND_REENQUEUE
                reminderRequest
            )
        }

        // Method to cancel reminders
        fun cancel(workManager: WorkManager) {
            workManager.cancelUniqueWork(WORK_NAME)
        }
    }

    override suspend fun doWork(): Result {
        try {
            // Cek apakah perlu menampilkan pengingat berdasarkan interval kustom
            val perluPengingat = airMinumRepository.perluPengingat()

            if (perluPengingat) {
                val airMinumHariIni = airMinumRepository.getAirMinumHariIni().first()

                if (airMinumHariIni != null) {
                    val sisaGelas = airMinumHariIni.targetGelas - airMinumHariIni.jumlahGelas
                    val totalMl = sisaGelas * airMinumHariIni.ukuranGelas

                    // Tampilkan waktu berlalu dalam format yang tepat (jam atau menit)
                    val waktuSejak = airMinumHariIni.waktuGelasTerakhir?.let {
                        val diffMs = Date().time - it.time
                        val diffMinutes = diffMs / (1000 * 60)
                        if (diffMinutes < 60) {
                            "$diffMinutes menit"
                        } else {
                            val diffHours = diffMinutes / 60
                            "$diffHours jam"
                        }
                    } ?: "beberapa waktu"

                    // Tampilkan notifikasi dengan info waktu
                    val notificationContent = if (airMinumHariIni.waktuGelasTerakhir != null) {
                        "Sudah $waktuSejak sejak terakhir minum air. Kamu masih perlu meminum $sisaGelas gelas ($totalMl ml) lagi hari ini untuk mencapai target. Tetap terhidrasi!"
                    } else {
                        "Kamu belum minum air hari ini. Target harianmu adalah ${airMinumHariIni.targetGelas} gelas (${airMinumHariIni.targetGelas * airMinumHariIni.ukuranGelas} ml). Tetap terhidrasi!"
                    }

                    // Tampilkan notifikasi
                    notificationHelper.showGeneralNotification(
                        id = 9999, // ID unik untuk pengingat air
                        title = "Waktunya Minum Air!",
                        content = notificationContent,
                        route = "air_minum"
                    )

                    // Update waktu pengingat terakhir
                    airMinumRepository.updateWaktuPengingatTerakhir()
                } else {
                    // Jika belum ada record hari ini
                    notificationHelper.showGeneralNotification(
                        id = 9999,
                        title = "Ayo Mulai Minum Air!",
                        content = "Kamu belum mencatat minum air hari ini. Tetap terhidrasi untuk kesehatan!",
                        route = "air_minum"
                    )

                    // Buat record kosong agar tidak terus diingatkan dalam waktu dekat
                    airMinumRepository.updateWaktuPengingatTerakhir()
                }
            }

            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }
}