package com.example.seka.util.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.seka.data.local.entity.TimeUnit
import com.example.seka.data.local.entity.TodoItem
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit as JavaTimeUnit
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "IntervalReminderSched"

/**
 * Kelas untuk menjadwalkan notifikasi pengingat berdasarkan interval waktu.
 * Notifikasi akan dijalankan secara berulang sesuai dengan interval waktu yang ditentukan.
 */
@Singleton
class IntervalReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * Jadwalkan notifikasi interval berdasarkan TodoItem.
     * @param todoItem Item tugas dengan pengaturan interval
     */
    fun scheduleIntervalReminder(todoItem: TodoItem) {
        if (!todoItem.intervalReminder || todoItem.isCompleted) {
            cancelIntervalReminder(todoItem.id)
            return
        }

        // Konversi nilai interval ke milidetik
        val intervalMs = when (todoItem.intervalUnit) {
            TimeUnit.SECONDS -> JavaTimeUnit.SECONDS.toMillis(todoItem.intervalValue.toLong())
            TimeUnit.MINUTES -> JavaTimeUnit.MINUTES.toMillis(todoItem.intervalValue.toLong())
            TimeUnit.HOURS -> JavaTimeUnit.HOURS.toMillis(todoItem.intervalValue.toLong())
        }

        if (intervalMs <= 0) return // Validasi interval

        Log.d(TAG, "Scheduling interval reminder for todo ${todoItem.id} every $intervalMs ms")

        // Buat intent untuk AlarmReceiver
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_TODO_ID, todoItem.id)
            putExtra(AlarmReceiver.EXTRA_TODO_TITLE, todoItem.title)
            putExtra(AlarmReceiver.EXTRA_TODO_CONTENT, todoItem.content)
            putExtra(AlarmReceiver.EXTRA_NOTIFICATION_TYPE, AlarmReceiver.TYPE_INTERVAL_REMINDER)
            // Tambahkan interval sebagai extra untuk digunakan saat rescheduling
            putExtra("EXTRA_INTERVAL_MS", intervalMs)
        }

        // Buat pending intent (unique request code berdasarkan ID tugas)
        val requestCode = (todoItem.id + 4000).toInt()
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, flags)

        // Setel alarm berulang
        try {
            // Untuk semua versi Android, gunakan setRepeating untuk konsistensi
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + intervalMs,
                intervalMs,
                pendingIntent
            )

            Log.d(TAG, "Successfully scheduled repeating alarm for todo ${todoItem.id}")

            // Tampilkan notifikasi konfirmasi
            val notificationHelper = NotificationHelper(context)
            val unitText = when (todoItem.intervalUnit) {
                TimeUnit.SECONDS -> "detik"
                TimeUnit.MINUTES -> "menit"
                TimeUnit.HOURS -> "jam"
            }
            notificationHelper.showTodoNotification(
                id = (todoItem.id + 4500).toInt(),
                title = "Pengingat interval telah diatur!",
                content = "Anda akan diingatkan setiap ${todoItem.intervalValue} $unitText tentang tugas '${todoItem.title}'",
                todoId = todoItem.id
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error scheduling interval reminder: ${e.message}", e)
        }
    }

    /**
     * Batalkan pengingat interval untuk ID tugas tertentu
     */
    fun cancelIntervalReminder(todoId: Long) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val requestCode = (todoId + 4000).toInt()
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_NO_CREATE
        }
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, flags)

        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
            Log.d(TAG, "Cancelled interval reminder for todo $todoId")
        }
    }

    /**
     * Batalkan semua pengingat interval (digunakan ketika aplikasi di-reset)
     */
    fun cancelAllIntervalReminders(todoIds: List<Long>) {
        todoIds.forEach { cancelIntervalReminder(it) }
    }
}