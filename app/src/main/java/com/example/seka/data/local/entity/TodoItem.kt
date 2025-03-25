package com.example.seka.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

enum class TodoUrgency {
    HIGH, MEDIUM, LOW
}

enum class TimeUnit {
    SECONDS, MINUTES, HOURS
}

@Entity(tableName = "todo_items")
data class TodoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String = "",
    val date: Date = Date(),
    val dueDate: Date? = null,
    val urgency: TodoUrgency = TodoUrgency.MEDIUM,
    val isCompleted: Boolean = false,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),

    // Notification settings
    val dailyReminder: Boolean = false,
    val dueDateReminder: Boolean = false,
    val dueDateReminderDays: Int = 1,

    // Interval reminder settings
    val intervalReminder: Boolean = false,
    val intervalValue: Int = 0,
    val intervalUnit: TimeUnit = TimeUnit.MINUTES
)