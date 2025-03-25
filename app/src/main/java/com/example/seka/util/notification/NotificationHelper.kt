package com.example.seka.util.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.seka.MainActivity
import com.example.seka.R
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class untuk mengelola notifikasi sistem Android.
 * Kelas ini bertanggung jawab untuk membuat channel notifikasi dan menampilkan notifikasi.
 */
@Singleton
class NotificationHelper @Inject constructor(private val context: Context) {

    companion object {
        // Channel ID
        const val CHANNEL_GENERAL_ID = "seka_general_channel"
        const val CHANNEL_TODO_ID = "seka_todo_channel"
        const val CHANNEL_TABUNGAN_ID = "seka_tabungan_channel"
        const val CHANNEL_KEUANGAN_ID = "seka_keuangan_channel"

        // Notification ID ranges
        const val TODO_NOTIFICATION_ID_START = 1000
        const val TABUNGAN_NOTIFICATION_ID_START = 2000
        const val KEUANGAN_NOTIFICATION_ID_START = 3000
    }

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    /**
     * Membuat channel notifikasi yang diperlukan (untuk Android O dan yang lebih baru)
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_GENERAL_ID,
                    "Notifikasi Umum",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Notifikasi umum dari aplikasi SEKA"
                    enableLights(true)
                    lightColor = Color.BLUE
                },

                NotificationChannel(
                    CHANNEL_TODO_ID,
                    "Notifikasi To Do",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Pengingat tugas dan tenggat waktu"
                    enableLights(true)
                    lightColor = Color.RED
                    enableVibration(true)
                },

                NotificationChannel(
                    CHANNEL_TABUNGAN_ID,
                    "Notifikasi Tabungan",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Pengingat progress tabungan"
                    enableLights(true)
                    lightColor = Color.YELLOW
                },

                NotificationChannel(
                    CHANNEL_KEUANGAN_ID,
                    "Notifikasi Keuangan",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Pengingat dan info transaksi keuangan"
                    enableLights(true)
                    lightColor = Color.GREEN
                }
            )

            channels.forEach { channel ->
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    /**
     * Menampilkan notifikasi To Do
     */
    fun showTodoNotification(id: Int, title: String, content: String, todoId: Long? = null) {
        showNotification(
            id + TODO_NOTIFICATION_ID_START,
            CHANNEL_TODO_ID,
            title,
            content,
            createPendingIntent("todo_detail/$todoId")
        )
    }

    /**
     * Menampilkan notifikasi Tabungan
     */
    fun showTabunganNotification(id: Int, title: String, content: String, tabunganId: Long? = null) {
        showNotification(
            id + TABUNGAN_NOTIFICATION_ID_START,
            CHANNEL_TABUNGAN_ID,
            title,
            content,
            createPendingIntent("tabungan_detail/$tabunganId")
        )
    }

    /**
     * Menampilkan notifikasi Keuangan
     */
    fun showKeuanganNotification(id: Int, title: String, content: String) {
        showNotification(
            id + KEUANGAN_NOTIFICATION_ID_START,
            CHANNEL_KEUANGAN_ID,
            title,
            content,
            createPendingIntent("keuangan")
        )
    }

    /**
     * Menampilkan notifikasi umum
     */
    fun showGeneralNotification(id: Int, title: String, content: String, route: String? = null) {
        showNotification(
            id,
            CHANNEL_GENERAL_ID,
            title,
            content,
            route?.let { createPendingIntent(it) }
        )
    }

    /**
     * Fungsi dasar untuk menampilkan notifikasi
     */
    private fun showNotification(
        id: Int,
        channelId: String,
        title: String,
        content: String,
        pendingIntent: PendingIntent? = null
    ) {
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification) // Pastikan icon ini ada di resources
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSound(defaultSound)
            .setAutoCancel(true)

        // Tampilkan content yang lebih panjang jika diperlukan
        if (content.length > 40) {
            notificationBuilder.setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(content)
            )
        }

        // Tambahkan intent jika ada
        pendingIntent?.let {
            notificationBuilder.setContentIntent(it)
        }

        notificationManager.notify(id, notificationBuilder.build())
    }

    /**
     * Buat pending intent untuk deep linking ke layar tertentu
     */
    private fun createPendingIntent(route: String): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("NAVIGATE_TO", route)
        }

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        return PendingIntent.getActivity(context, route.hashCode(), intent, flags)
    }

    /**
     * Membatalkan notifikasi berdasarkan ID
     */
    fun cancelNotification(id: Int) {
        notificationManager.cancel(id)
    }

    /**
     * Membatalkan semua notifikasi
     */
    fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }
}