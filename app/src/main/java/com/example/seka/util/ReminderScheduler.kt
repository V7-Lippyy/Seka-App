package com.example.seka.util

import androidx.work.WorkManager
import com.example.seka.util.workers.AirMinumReminderWorker
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class to manage scheduling of all reminders in the app
 */
@Singleton
class ReminderScheduler @Inject constructor(
    private val workManager: WorkManager
) {
    /**
     * Schedule the water reminder based on user preference
     * @param enable Whether to enable or disable the reminder
     */
    fun scheduleWaterReminder(enable: Boolean) {
        if (enable) {
            AirMinumReminderWorker.schedule(workManager)
        } else {
            AirMinumReminderWorker.cancel(workManager)
        }
    }

    /**
     * Cancel all reminders
     */
    fun cancelAllReminders() {
        workManager.cancelAllWork()
    }
}