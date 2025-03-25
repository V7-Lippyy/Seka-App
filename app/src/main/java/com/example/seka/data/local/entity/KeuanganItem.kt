package com.example.seka.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

enum class TransactionType {
    INCOME, EXPENSE
}

@Entity(tableName = "keuangan_items")
data class KeuanganItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tanggal: Date,
    val judul: String,
    val deskripsi: String,
    val jumlah: Double,
    val tipe: TransactionType,
    val kategori: String,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)