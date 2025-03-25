package com.example.seka.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Utility functions for date operations
 */
object DateUtils {

    /**
     * Get the start of the day for a given date
     */
    fun getStartOfDay(date: Date = Date()): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    /**
     * Format date as readable string
     */
    fun formatDate(date: Date, pattern: String = "dd/MM/yyyy"): String {
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.format(date)
    }

    /**
     * Get a date that is specified number of days before today
     */
    fun getDaysAgo(days: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        return calendar.time
    }

    /**
     * Check if two dates are the same day
     */
    fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = date1
        cal2.time = date2
        return (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR))
    }
}