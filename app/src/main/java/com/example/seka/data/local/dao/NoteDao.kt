package com.example.seka.data.local.dao

import androidx.room.*
import com.example.seka.data.local.entity.NoteItem
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface NoteDao {
    @Query("SELECT * FROM note_items ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteItem>>

    @Query("SELECT * FROM note_items WHERE title LIKE '%' || :searchQuery || '%' OR content LIKE '%' || :searchQuery || '%'")
    fun searchNotes(searchQuery: String): Flow<List<NoteItem>>

    @Query("SELECT * FROM note_items WHERE updatedAt BETWEEN :startDate AND :endDate")
    fun getNotesByDateRange(startDate: Date, endDate: Date): Flow<List<NoteItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(noteItem: NoteItem): Long

    @Update
    suspend fun update(noteItem: NoteItem)

    @Delete
    suspend fun delete(noteItem: NoteItem)

    @Query("SELECT * FROM note_items WHERE id = :id")
    suspend fun getNoteById(id: Long): NoteItem?
}