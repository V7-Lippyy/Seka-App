package com.example.seka.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Date = Date()
)