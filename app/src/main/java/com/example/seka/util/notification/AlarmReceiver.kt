package com.example.seka.util.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "AlarmReceiver"

/**
 * Receiver untuk menangani alarm yang dijadwalkan untuk pengingat tugas.
 */
@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    companion object {
        const val EXTRA_TODO_ID = "extra_todo_id"
        const val EXTRA_TODO_TITLE = "extra_todo_title"
        const val EXTRA_TODO_CONTENT = "extra_todo_content"
        const val EXTRA_NOTIFICATION_TYPE = "extra_notification_type"
        const val EXTRA_INTERVAL_MS = "extra_interval_ms"

        const val TYPE_DAILY_REMINDER = "daily_reminder"
        const val TYPE_DUE_DATE_REMINDER = "due_date_reminder"
        const val TYPE_DUE_TODAY = "due_today"
        const val TYPE_OVERDUE = "overdue"
        const val TYPE_INTERVAL_REMINDER = "interval_reminder"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val todoId = intent.getLongExtra(EXTRA_TODO_ID, -1L)
        if (todoId == -1L) {
            Log.e(TAG, "Invalid todoId received")
            return
        }

        val todoTitle = intent.getStringExtra(EXTRA_TODO_TITLE) ?: "Tugas"
        val todoContent = intent.getStringExtra(EXTRA_TODO_CONTENT)
        val notificationType = intent.getStringExtra(EXTRA_NOTIFICATION_TYPE)
            ?: TYPE_DAILY_REMINDER

        Log.d(TAG, "Alarm received for todo $todoId, type: $notificationType")

        // ID notifikasi unik berdasarkan tipe dan ID todo
        val notificationId = when (notificationType) {
            TYPE_DAILY_REMINDER -> todoId.toInt()
            TYPE_DUE_DATE_REMINDER -> todoId.toInt() + 1000
            TYPE_DUE_TODAY -> todoId.toInt() + 2000
            TYPE_OVERDUE -> todoId.toInt() + 3000
            TYPE_INTERVAL_REMINDER -> todoId.toInt() + 4000
            else -> todoId.toInt()
        }

        // Konten notifikasi berdasarkan tipe
        val notificationContent = when (notificationType) {
            TYPE_DAILY_REMINDER -> "Jangan lupa: $todoTitle"
            TYPE_DUE_DATE_REMINDER -> "Tugas '$todoTitle' akan jatuh tempo dalam beberapa hari"
            TYPE_DUE_TODAY -> "Tugas '$todoTitle' jatuh tempo hari ini!"
            TYPE_OVERDUE -> "Tugas '$todoTitle' sudah melewati tenggat waktu!"
            TYPE_INTERVAL_REMINDER -> "Pengingat untuk tugas: $todoTitle"
            else -> todoContent ?: "Jangan lupa tentang tugas ini"
        }

        // Judul notifikasi berdasarkan tipe
        val notificationTitle = when (notificationType) {
            TYPE_DAILY_REMINDER -> "Pengingat Tugas Harian"
            TYPE_DUE_DATE_REMINDER -> "Pengingat Tenggat Waktu"
            TYPE_DUE_TODAY -> "Tenggat Hari Ini!"
            TYPE_OVERDUE -> "Tenggat Terlewat!"
            TYPE_INTERVAL_REMINDER -> "Pengingat Interval"
            else -> "Pengingat Tugas"
        }

        // Tampilkan notifikasi
        notificationHelper.showTodoNotification(
            id = notificationId,
            title = notificationTitle,
            content = notificationContent,
            todoId = todoId
        )
    }
}