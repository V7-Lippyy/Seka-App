package com.example.seka.ui.screens.note

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seka.data.local.entity.NoteItem
import com.example.seka.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class NoteDetailUiState(
    val noteItem: NoteItem? = null,
    val title: String = "",
    val content: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val noteId: Long = checkNotNull(savedStateHandle["noteId"])

    private val _uiState = MutableStateFlow(NoteDetailUiState(isLoading = true))
    val uiState: StateFlow<NoteDetailUiState> = _uiState.asStateFlow()

    init {
        if (noteId != -1L) {
            loadNote()
        } else {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun loadNote() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val note = noteRepository.getNoteById(noteId)
                if (note != null) {
                    _uiState.update { state ->
                        state.copy(
                            noteItem = note,
                            title = note.title,
                            content = note.content,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            error = "Catatan tidak ditemukan",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
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

    fun saveNote() {
        val currentState = _uiState.value

        if (currentState.title.isBlank()) {
            _uiState.update { it.copy(error = "Judul tidak boleh kosong") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val noteToSave = if (currentState.noteItem != null) {
                    currentState.noteItem.copy(
                        title = currentState.title,
                        content = currentState.content,
                        updatedAt = Date()
                    )
                } else {
                    NoteItem(
                        title = currentState.title,
                        content = currentState.content
                    )
                }

                if (noteId == -1L) {
                    noteRepository.insertNote(noteToSave)
                } else {
                    noteRepository.updateNote(noteToSave)
                }

                _uiState.update {
                    it.copy(
                        isSaved = true,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Terjadi kesalahan saat menyimpan",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}