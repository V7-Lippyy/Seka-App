package com.example.seka.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "note_items")
data class NoteItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)