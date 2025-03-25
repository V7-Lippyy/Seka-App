package com.example.seka.ui.screens.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seka.data.local.entity.TodoItem
import com.example.seka.data.local.entity.TodoUrgency
import com.example.seka.data.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class TodoUiState(
    val todos: List<TodoItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedUrgency: TodoUrgency? = null
)

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TodoUiState(isLoading = true))
    val uiState: StateFlow<TodoUiState> = _uiState

    init {
        loadTodos()
    }

    fun loadTodos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            todoRepository.getAllTodos()
                .catch { e ->
                    _uiState.update {
                        it.copy(error = e.message, isLoading = false)
                    }
                }
                .collect { todos ->
                    _uiState.update {
                        it.copy(todos = todos, isLoading = false)
                    }
                }
        }
    }

    fun searchTodos(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(searchQuery = query, isLoading = true) }

            if (query.isBlank()) {
                loadTodos()
                return@launch
            }

            todoRepository.searchTodos(query)
                .catch { e ->
                    _uiState.update {
                        it.copy(error = e.message, isLoading = false)
                    }
                }
                .collect { todos ->
                    _uiState.update {
                        it.copy(todos = todos, isLoading = false)
                    }
                }
        }
    }

    fun filterByUrgency(urgency: TodoUrgency?) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedUrgency = urgency, isLoading = true) }

            if (urgency == null) {
                loadTodos()
                return@launch
            }

            todoRepository.getTodosByUrgency(urgency)
                .catch { e ->
                    _uiState.update {
                        it.copy(error = e.message, isLoading = false)
                    }
                }
                .collect { todos ->
                    _uiState.update {
                        it.copy(todos = todos, isLoading = false)
                    }
                }
        }
    }

    fun toggleTodoCompletion(todoItem: TodoItem) {
        viewModelScope.launch {
            val updatedTodo = todoItem.copy(
                isCompleted = !todoItem.isCompleted,
                updatedAt = Date()
            )
            todoRepository.updateTodo(updatedTodo)
        }
    }

    fun deleteTodo(todoItem: TodoItem) {
        viewModelScope.launch {
            todoRepository.deleteTodo(todoItem)
        }
    }
}