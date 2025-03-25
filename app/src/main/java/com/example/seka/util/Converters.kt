package com.example.seka.util

import androidx.room.TypeConverter
import com.example.seka.data.local.entity.TodoUrgency
import com.example.seka.data.local.entity.TransactionType
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromString(value: String?): List<String>? {
        return value?.split(",")?.map { it.trim() }
    }

    @TypeConverter
    fun fromList(list: List<String>?): String? {
        return list?.joinToString(",")
    }

    @TypeConverter
    fun urgencyToInt(urgency: TodoUrgency): Int {
        return when (urgency) {
            TodoUrgency.HIGH -> 0
            TodoUrgency.MEDIUM -> 1
            TodoUrgency.LOW -> 2
        }
    }

    @TypeConverter
    fun intToUrgency(value: Int): TodoUrgency {
        return when (value) {
            0 -> TodoUrgency.HIGH
            1 -> TodoUrgency.MEDIUM
            else -> TodoUrgency.LOW
        }
    }

    @TypeConverter
    fun transactionTypeToInt(type: TransactionType): Int {
        return when (type) {
            TransactionType.INCOME -> 0
            TransactionType.EXPENSE -> 1
        }
    }

    @TypeConverter
    fun intToTransactionType(value: Int): TransactionType {
        return when (value) {
            0 -> TransactionType.INCOME
            else -> TransactionType.EXPENSE
        }
    }
}