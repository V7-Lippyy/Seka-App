package com.example.seka.ui.screens.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seka.data.local.entity.NoteItem
import com.example.seka.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class NoteUiState(
    val notes: List<NoteItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteUiState(isLoading = true))
    val uiState: StateFlow<NoteUiState> = _uiState

    init {
        loadNotes()
    }

    fun loadNotes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            noteRepository.getAllNotes()
                .catch { e ->
                    _uiState.update {
                        it.copy(error = e.message, isLoading = false)
                    }
                }
                .collect { notes ->
                    _uiState.update {
                        it.copy(notes = notes, isLoading = false)
                    }
                }
        }
    }

    fun searchNotes(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(searchQuery = query, isLoading = true) }

            if (query.isBlank()) {
                loadNotes()
                return@launch
            }

            noteRepository.searchNotes(query)
                .catch { e ->
                    _uiState.update {
                        it.copy(error = e.message, isLoading = false)
                    }
                }
                .collect { notes ->
                    _uiState.update {
                        it.copy(notes = notes, isLoading = false)
                    }
                }
        }
    }

    fun deleteNote(noteItem: NoteItem) {
        viewModelScope.launch {
            noteRepository.deleteNote(noteItem)
        }
    }
}