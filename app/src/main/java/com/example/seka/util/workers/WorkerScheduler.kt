package com.example.seka.util.workers

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.seka.data.local.entity.TodoItem
import com.example.seka.data.repository.TodoRepository
import com.example.seka.util.notification.IntervalReminderScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Kelas untuk menjadwalkan worker dan notifikasi
 */
@Singleton
class WorkerScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val todoRepository: TodoRepository,
    private val intervalReminderScheduler: IntervalReminderScheduler
) {
    private val workManager = WorkManager.getInstance(context)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    companion object {
        // Metode static untuk digunakan oleh BootReceiver
        fun scheduleAllWorkersStatic(context: Context) {
            val workManager = WorkManager.getInstance(context)

            // Todo Reminder Worker (daily check)
            val todoConstraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            val todoReminderRequest = PeriodicWorkRequestBuilder<TodoReminderWorker>(
                1, TimeUnit.HOURS
            )
                .setConstraints(todoConstraints)
                .build()

            workManager.enqueueUniquePeriodicWork(
                TodoReminderWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                todoReminderRequest
            )
        }
    }

    /**
     * Jadwalkan semua worker yang diperlukan aplikasi
     */
    fun scheduleAllWorkers() {
        // Todo Reminder Worker (daily check)
        val todoConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val todoReminderRequest = PeriodicWorkRequestBuilder<TodoReminderWorker>(
            1, TimeUnit.HOURS
        )
            .setConstraints(todoConstraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            TodoReminderWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            todoReminderRequest
        )

        // Scheduler untuk interval reminder
        scheduleIntervalReminders()
    }

    /**
     * Jadwalkan pengingat interval untuk semua tugas yang memiliki pengaturan interval
     */
    fun scheduleIntervalReminders() {
        coroutineScope.launch {
            try {
                val todos = todoRepository.getAllTodos().first()
                val intervalTodos = todos.filter {
                    it.intervalReminder && !it.isCompleted
                }

                intervalTodos.forEach { todo ->
                    intervalReminderScheduler.scheduleIntervalReminder(todo)
                }
            } catch (e: Exception) {
                // Log error
            }
        }
    }

    /**
     * Jadwalkan pengingat interval untuk tugas tertentu
     */
    fun scheduleIntervalReminder(todoItem: TodoItem) {
        intervalReminderScheduler.scheduleIntervalReminder(todoItem)
    }

    /**
     * Batalkan pengingat interval untuk tugas tertentu
     */
    fun cancelIntervalReminder(todoId: Long) {
        intervalReminderScheduler.cancelIntervalReminder(todoId)
    }
}