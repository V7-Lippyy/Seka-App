package com.example.seka.data.repository

import com.example.seka.data.local.dao.ChatMessageDao
import com.example.seka.data.local.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val chatMessageDao: ChatMessageDao
) {
    fun getAllMessages(): Flow<List<ChatMessageEntity>> = chatMessageDao.getAllMessages()

    suspend fun saveMessage(message: ChatMessageEntity) = chatMessageDao.insert(message)

    suspend fun saveAllMessages(messages: List<ChatMessageEntity>) =
        chatMessageDao.insertAll(messages)

    suspend fun clearAllMessages() = chatMessageDao.deleteAllMessages()
}