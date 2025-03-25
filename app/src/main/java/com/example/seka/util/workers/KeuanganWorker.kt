package com.example.seka.util.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.seka.data.repository.KeuanganRepository
import com.example.seka.util.notification.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.Calendar

/**
 * Worker yang memeriksa aktivitas keuangan dan memberikan notifikasi
 * jika tidak ada transaksi yang dicatat dalam 24 jam terakhir.
 */
@HiltWorker
class KeuanganWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val keuanganRepository: KeuanganRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "keuangan_reminder_worker"
    }

    override suspend fun doWork(): Result {
        try {
            // Tanggal 24 jam yang lalu
            val yesterday = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -1)
            }.time

            // Tanggal saat ini
            val today = Calendar.getInstance().time

            // Cek transaksi dalam 24 jam terakhir
            val recentTransactions = keuanganRepository.getTransactionsByDateRange(
                yesterday,
                today
            ).first()

            // Jika tidak ada transaksi, kirim notifikasi
            if (recentTransactions.isEmpty()) {
                notificationHelper.showKeuanganNotification(
                    id = 1,
                    title = "Pengingat Keuangan",
                    content = "Anda belum mencatat transaksi keuangan apapun dalam 24 jam terakhir. " +
                            "Jangan lupa untuk melacak pengeluaran dan pemasukan Anda."
                )

                return Result.success()
            }

            // Jika ada transaksi, tidak perlu notifikasi
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }
}