package com.example.seka.data.local.dao

import androidx.room.*
import com.example.seka.data.local.entity.TodoItem
import com.example.seka.data.local.entity.TodoUrgency
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo_items ORDER BY date DESC")
    fun getAllTodos(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE title LIKE '%' || :searchQuery || '%' OR content LIKE '%' || :searchQuery || '%'")
    fun searchTodos(searchQuery: String): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE urgency = :urgency")
    fun getTodosByUrgency(urgency: TodoUrgency): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE date BETWEEN :startDate AND :endDate")
    fun getTodosByDateRange(startDate: Date, endDate: Date): Flow<List<TodoItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todoItem: TodoItem): Long

    @Update
    suspend fun update(todoItem: TodoItem)

    @Delete
    suspend fun delete(todoItem: TodoItem)

    @Query("SELECT * FROM todo_items WHERE id = :id")
    suspend fun getTodoById(id: Long): TodoItem?
}