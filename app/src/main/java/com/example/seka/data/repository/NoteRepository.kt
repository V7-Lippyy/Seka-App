package com.example.seka.data.repository

import com.example.seka.data.local.dao.NoteDao
import com.example.seka.data.local.entity.NoteItem
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val noteDao: NoteDao
) {
    fun getAllNotes(): Flow<List<NoteItem>> = noteDao.getAllNotes()

    fun searchNotes(query: String): Flow<List<NoteItem>> = noteDao.searchNotes(query)

    fun getNotesByDateRange(startDate: Date, endDate: Date): Flow<List<NoteItem>> =
        noteDao.getNotesByDateRange(startDate, endDate)

    suspend fun insertNote(noteItem: NoteItem): Long = noteDao.insert(noteItem)

    suspend fun updateNote(noteItem: NoteItem) = noteDao.update(noteItem)

    suspend fun deleteNote(noteItem: NoteItem) = noteDao.delete(noteItem)

    suspend fun getNoteById(id: Long): NoteItem? = noteDao.getNoteById(id)
}