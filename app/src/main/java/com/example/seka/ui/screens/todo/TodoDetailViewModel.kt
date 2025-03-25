package com.example.seka.ui.screens.todo

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seka.data.local.entity.TimeUnit
import com.example.seka.data.local.entity.TodoItem
import com.example.seka.data.local.entity.TodoUrgency
import com.example.seka.data.repository.TodoRepository
import com.example.seka.util.notification.NotificationHelper
import com.example.seka.util.workers.WorkerScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

private const val TAG = "TodoDetailViewModel"

data class TodoDetailUiState(
    val todoItem: TodoItem? = null,
    val title: String = "",
    val content: String = "",
    val date: Date = Date(),
    val dueDate: Date? = null,
    val urgency: TodoUrgency = TodoUrgency.MEDIUM,
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false,
    val dailyReminder: Boolean = false,
    val dueDateReminder: Boolean = false,
    val dueDateReminderDays: Int = 1,

    // Interval reminder state
    val intervalReminder: Boolean = false,
    val intervalValue: Int = 5,  // Default 5 minutes
    val intervalUnit: TimeUnit = TimeUnit.MINUTES,

    val error: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class TodoDetailViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val notificationHelper: NotificationHelper,
    private val workerScheduler: WorkerScheduler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val todoId: Long = checkNotNull(savedStateHandle["todoId"])

    private val _uiState = MutableStateFlow(TodoDetailUiState(isLoading = true))
    val uiState: StateFlow<TodoDetailUiState> = _uiState.asStateFlow()

    init {
        if (todoId != -1L) {
            loadTodo()
        } else {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun loadTodo() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val todo = todoRepository.getTodoById(todoId)
                if (todo != null) {
                    _uiState.update { state ->
                        state.copy(
                            todoItem = todo,
                            title = todo.title,
                            content = todo.content,
                            date = todo.date,
                            dueDate = todo.dueDate,
                            urgency = todo.urgency,
                            isCompleted = todo.isCompleted,
                            dailyReminder = todo.dailyReminder,
                            dueDateReminder = todo.dueDateReminder,
                            dueDateReminderDays = todo.dueDateReminderDays,
                            intervalReminder = todo.intervalReminder,
                            intervalValue = todo.intervalValue,
                            intervalUnit = todo.intervalUnit,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            error = "Todo tidak ditemukan",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading todo: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Terjadi kesalahan",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun updateContent(content: String) {
        _uiState.update { it.copy(content = content) }
    }

    fun updateDate(date: Date) {
        _uiState.update { it.copy(date = date) }
    }

    fun updateDueDate(dueDate: Date?) {
        _uiState.update { it.copy(dueDate = dueDate) }
    }

    fun updateUrgency(urgency: TodoUrgency) {
        _uiState.update { it.copy(urgency = urgency) }
    }

    fun toggleCompletion() {
        _uiState.update { it.copy(isCompleted = !it.isCompleted) }
    }

    fun toggleDailyReminder() {
        _uiState.update { it.copy(dailyReminder = !it.dailyReminder) }
    }

    fun toggleDueDateReminder() {
        _uiState.update { it.copy(dueDateReminder = !it.dueDateReminder) }
    }

    fun toggleIntervalReminder() {
        _uiState.update { it.copy(intervalReminder = !it.intervalReminder) }
    }

    fun updateDueDateReminderDays(daysStr: String) {
        try {
            val days = daysStr.toIntOrNull() ?: 1
            _uiState.update { it.copy(dueDateReminderDays = days.coerceAtLeast(1)) }
        } catch (e: Exception) {
            // Ignore invalid input
        }
    }

    fun updateIntervalValue(valueStr: String) {
        try {
            val value = valueStr.toIntOrNull() ?: 5
            _uiState.update { it.copy(intervalValue = value.coerceAtLeast(1)) }
        } catch (e: Exception) {
            // Ignore invalid input
        }
    }

    fun updateIntervalUnit(unit: TimeUnit) {
        _uiState.update { it.copy(intervalUnit = unit) }
    }

    fun saveTodo() {
        val currentState = _uiState.value

        if (currentState.title.isBlank()) {
            _uiState.update { it.copy(error = "Judul tidak boleh kosong") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            Log.d(TAG, "Starting save todo process...")

            try {
                // Buat objek todoToSave
                val todoToSave = if (currentState.todoItem != null) {
                    Log.d(TAG, "Updating existing todo with ID: ${currentState.todoItem.id}")
                    currentState.todoItem.copy(
                        title = currentState.title,
                        content = currentState.content,
                        date = currentState.date,
                        dueDate = currentState.dueDate,
                        urgency = currentState.urgency,
                        isCompleted = currentState.isCompleted,
                        dailyReminder = currentState.dailyReminder,
                        dueDateReminder = currentState.dueDateReminder,
                        dueDateReminderDays = currentState.dueDateReminderDays,
                        intervalReminder = currentState.intervalReminder,
                        intervalValue = currentState.intervalValue,
                        intervalUnit = currentState.intervalUnit,
                        updatedAt = Date()
                    )
                } else {
                    Log.d(TAG, "Creating new todo")
                    TodoItem(
                        title = currentState.title,
                        content = currentState.content,
                        date = currentState.date,
                        dueDate = currentState.dueDate,
                        urgency = currentState.urgency,
                        isCompleted = currentState.isCompleted,
                        dailyReminder = currentState.dailyReminder,
                        dueDateReminder = currentState.dueDateReminder,
                        dueDateReminderDays = currentState.dueDateReminderDays,
                        intervalReminder = currentState.intervalReminder,
                        intervalValue = currentState.intervalValue,
                        intervalUnit = currentState.intervalUnit
                    )
                }

                // Simpan ke database
                val savedId = try {
                    if (todoId == -1L) {
                        Log.d(TAG, "Inserting new todo to database")
                        todoRepository.insertTodo(todoToSave)
                    } else {
                        Log.d(TAG, "Updating todo in database")
                        todoRepository.updateTodo(todoToSave)
                        todoId
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error saving todo to database: ${e.message}", e)
                    throw e
                }

                Log.d(TAG, "Todo saved with ID: $savedId")

                // Setup notifications
                try {
                    Log.d(TAG, "Scheduling notifications")
                    scheduleNotifications(savedId, todoToSave)
                    Log.d(TAG, "Notifications scheduled successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "Error scheduling notifications: ${e.message}", e)
                    // Continue despite error with notifications
                }

                // Schedule interval reminders if enabled
                try {
                    if (todoToSave.intervalReminder && !todoToSave.isCompleted) {
                        Log.d(TAG, "Scheduling interval reminders")
                        workerScheduler.scheduleIntervalReminder(todoToSave.copy(id = savedId))
                        Log.d(TAG, "Interval reminders scheduled successfully")
                    } else {
                        Log.d(TAG, "Canceling interval reminders if any")
                        workerScheduler.cancelIntervalReminder(savedId)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error with interval reminders: ${e.message}", e)
                    // Continue despite error with interval reminders
                }

                Log.d(TAG, "Todo save process completed successfully")

                _uiState.update {
                    it.copy(
                        isSaved = true,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving todo: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Terjadi kesalahan saat menyimpan",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun scheduleNotifications(todoId: Long, todoItem: TodoItem) {
        // Note: The actual scheduling will be done by Workers, but we'll show an immediate notification
        // to confirm to the user that reminders have been set

        try {
            if (todoItem.dailyReminder && !todoItem.isCompleted) {
                notificationHelper.showTodoNotification(
                    id = todoId.toInt(),
                    title = "Pengingat Dibuat: ${todoItem.title}",
                    content = "Anda akan diingatkan setiap hari tentang tugas ini.",
                    todoId = todoId
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error scheduling daily reminder: ${e.message}", e)
        }

        try {
            if (todoItem.dueDateReminder && todoItem.dueDate != null && !todoItem.isCompleted) {
                notificationHelper.showTodoNotification(
                    id = todoId.toInt() + 1000, // Use different ID to avoid overwriting
                    title = "Pengingat Tenggat Dibuat: ${todoItem.title}",
                    content = "Anda akan diingatkan ${todoItem.dueDateReminderDays} hari sebelum tenggat.",
                    todoId = todoId
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error scheduling due date reminder: ${e.message}", e)
        }

        try {
            if (todoItem.intervalReminder && !todoItem.isCompleted) {
                val intervalText = when (todoItem.intervalUnit) {
                    TimeUnit.SECONDS -> "detik"
                    TimeUnit.MINUTES -> "menit"
                    TimeUnit.HOURS -> "jam"
                }

                notificationHelper.showTodoNotification(
                    id = todoId.toInt() + 2000, // Use different ID to avoid overwriting
                    title = "Pengingat Interval Dibuat: ${todoItem.title}",
                    content = "Anda akan diingatkan setiap ${todoItem.intervalValue} $intervalText.",
                    todoId = todoId
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error scheduling interval reminder: ${e.message}", e)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}