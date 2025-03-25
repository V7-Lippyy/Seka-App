package com.example.seka.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Enum class untuk unit waktu pengingat
 */
enum class IntervalUnit {
    MINUTES, HOURS
}

@Entity(tableName = "air_minum_items")
data class AirMinumItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val tanggal: Date = Date(),
    val jumlahGelas: Int = 0,
    val targetGelas: Int = 8,
    val ukuranGelas: Int = 250, // ml
    val pengingat: Boolean = true,
    val intervalPengingat: Int = 60, // Interval pengingat dalam menit (default 60 menit)
    val intervalUnit: IntervalUnit = IntervalUnit.MINUTES, // Unit waktu untuk interval (default menit)
    val waktuPengingatTerakhir: Date? = null, // Waktu terakhir kali notifikasi dikirim
    val waktuGelasTerakhir: Date? = null, // Waktu terakhir kali menambah gelas

    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)