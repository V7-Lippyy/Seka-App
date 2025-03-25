package com.example.seka.util.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.seka.data.local.entity.TimeUnit
import com.example.seka.data.repository.TodoRepository
import com.example.seka.util.notification.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit as JavaTimeUnit

/**
 * Worker yang memeriksa tugas-tugas dan mengirimkan notifikasi pengingat
 * berdasarkan pengaturan pengingat harian dan tenggat waktu.
 */
@HiltWorker
class TodoReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val todoRepository: TodoRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "todo_reminder_worker"
    }

    override suspend fun doWork(): Result {
        try {
            // Dapatkan semua tugas yang belum selesai
            val activeTodos = todoRepository.getAllTodos().first().filter { !it.isCompleted }

            // Tanggal hari ini
            val today = Calendar.getInstance()
            today.set(Calendar.HOUR_OF_DAY, 0)
            today.set(Calendar.MINUTE, 0)
            today.set(Calendar.SECOND, 0)
            today.set(Calendar.MILLISECOND, 0)
            val todayDate = today.time

            // Periksa tugas dengan pengingat harian
            val dailyReminders = activeTodos.filter { it.dailyReminder }
            dailyReminders.forEachIndexed { index, todo ->
                notificationHelper.showTodoNotification(
                    id = index,
                    title = "Pengingat Tugas Harian",
                    content = "Jangan lupa: ${todo.title}",
                    todoId = todo.id
                )
            }

            // Periksa tugas dengan tenggat waktu
            val dueDateReminders = activeTodos.filter {
                it.dueDateReminder && it.dueDate != null
            }

            dueDateReminders.forEachIndexed { index, todo ->
                todo.dueDate?.let { dueDate ->
                    // Hitung berapa hari tersisa sampai tenggat
                    val daysUntilDue = JavaTimeUnit.MILLISECONDS.toDays(dueDate.time - todayDate.time)

                    // Jika hari tersisa sama dengan pengaturan reminder, kirim notifikasi
                    if (daysUntilDue == todo.dueDateReminderDays.toLong()) {
                        notificationHelper.showTodoNotification(
                            id = 1000 + index, // ID berbeda untuk menghindari konflik dengan pengingat harian
                            title = "Pengingat Tenggat Waktu",
                            content = "Tugas '${todo.title}' akan jatuh tempo dalam ${todo.dueDateReminderDays} hari",
                            todoId = todo.id
                        )
                    }

                    // Jika tenggat hari ini
                    if (daysUntilDue == 0L) {
                        notificationHelper.showTodoNotification(
                            id = 2000 + index,
                            title = "Tenggat Hari Ini!",
                            content = "Tugas '${todo.title}' jatuh tempo hari ini!",
                            todoId = todo.id
                        )
                    }

                    // Jika sudah melewati tenggat
                    if (daysUntilDue < 0) {
                        notificationHelper.showTodoNotification(
                            id = 3000 + index,
                            title = "Tenggat Terlewat!",
                            content = "Tugas '${todo.title}' sudah melewati tenggat waktu!",
                            todoId = todo.id
                        )
                    }
                }
            }

            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }
}