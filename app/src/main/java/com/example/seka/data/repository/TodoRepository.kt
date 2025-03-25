package com.example.seka.data.repository

import com.example.seka.data.local.dao.TodoDao
import com.example.seka.data.local.entity.TodoItem
import com.example.seka.data.local.entity.TodoUrgency
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class TodoRepository @Inject constructor(
    private val todoDao: TodoDao
) {
    fun getAllTodos(): Flow<List<TodoItem>> = todoDao.getAllTodos()

    fun searchTodos(query: String): Flow<List<TodoItem>> = todoDao.searchTodos(query)

    fun getTodosByUrgency(urgency: TodoUrgency): Flow<List<TodoItem>> =
        todoDao.getTodosByUrgency(urgency)

    fun getTodosByDateRange(startDate: Date, endDate: Date): Flow<List<TodoItem>> =
        todoDao.getTodosByDateRange(startDate, endDate)

    suspend fun insertTodo(todoItem: TodoItem): Long = todoDao.insert(todoItem)

    suspend fun updateTodo(todoItem: TodoItem) = todoDao.update(todoItem)

    suspend fun deleteTodo(todoItem: TodoItem) = todoDao.delete(todoItem)

    suspend fun getTodoById(id: Long): TodoItem? = todoDao.getTodoById(id)
}