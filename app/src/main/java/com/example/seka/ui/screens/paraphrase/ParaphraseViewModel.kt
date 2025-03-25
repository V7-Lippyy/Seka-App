package com.example.seka.ui.screens.paraphrase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seka.util.api.OpenAIService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ParaphraseUiState(
    val originalText: String = "",
    val paraphrasedText: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ParaphraseViewModel @Inject constructor(
    private val openAIService: OpenAIService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ParaphraseUiState())
    val uiState: StateFlow<ParaphraseUiState> = _uiState.asStateFlow()

    fun updateOriginalText(text: String) {
        _uiState.update { it.copy(originalText = text) }
    }

    fun generateParaphrase() {
        val text = _uiState.value.originalText

        if (text.isBlank()) {
            _uiState.update { it.copy(error = "Teks tidak boleh kosong") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val paraphrased = openAIService.generateParaphrase(text)
                _uiState.update {
                    it.copy(
                        paraphrasedText = paraphrased,
                        isLoading = false
                    )
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

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearParaphrase() {
        _uiState.update { it.copy(paraphrasedText = "", originalText = "") }
    }
}