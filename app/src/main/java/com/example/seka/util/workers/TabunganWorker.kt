package com.example.seka.util.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.seka.data.repository.TabunganRepository
import com.example.seka.util.notification.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Worker yang memeriksa progress tabungan dan memberikan notifikasi jika tidak ada progress dalam 24 jam.
 */
@HiltWorker
class TabunganWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val tabunganRepository: TabunganRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "tabungan_reminder_worker"
    }

    override suspend fun doWork(): Result {
        try {
            // Dapatkan tanggal 24 jam yang lalu
            val yesterday = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -1)
            }.time

            // Dapatkan semua tabungan
            val allTabungan = tabunganRepository.getAllTabungan().first()

            // Filter tabungan yang belum selesai (belum mencapai target) dan tidak ada progress dalam 24 jam
            val inactiveTabungan = allTabungan.filter { tabungan ->
                tabungan.tabunganTerkumpul < tabungan.hargaTarget &&
                        tabungan.updatedAt.before(yesterday)
            }

            // Kirim notifikasi untuk tabungan yang tidak aktif
            inactiveTabungan.forEachIndexed { index, tabungan ->
                val daysSinceUpdate = TimeUnit.MILLISECONDS.toDays(
                    System.currentTimeMillis() - tabungan.updatedAt.time
                )

                notificationHelper.showTabunganNotification(
                    id = index,
                    title = "Tabungan: ${tabungan.nama}",
                    content = "Sudah $daysSinceUpdate hari tidak ada progress pada tabungan ${tabungan.nama}. " +
                            "Target: ${formatCurrency(tabungan.hargaTarget)}, " +
                            "Terkumpul: ${formatCurrency(tabungan.tabunganTerkumpul)}",
                    tabunganId = tabungan.id
                )
            }

            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }

    private fun formatCurrency(amount: Double): String {
        return java.text.NumberFormat.getCurrencyInstance(
            java.util.Locale("id", "ID")
        ).format(amount)
    }
}