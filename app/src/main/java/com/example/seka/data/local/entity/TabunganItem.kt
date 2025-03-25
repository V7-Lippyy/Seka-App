package com.example.seka.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "tabungan_items")
data class TabunganItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nama: String,
    val hargaTarget: Double,
    val tabunganTerkumpul: Double = 0.0,
    val cicilanJumlah: Double = 0.0,
    val kategori: String = "",
    val targetDate: Date? = null,
    val imagePath: String? = null, // Path ke gambar lokal
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)