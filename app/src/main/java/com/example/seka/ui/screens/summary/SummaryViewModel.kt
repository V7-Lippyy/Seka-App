package com.example.seka.ui.screens.summary

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

data class SummaryUiState(
    val originalText: String = "",
    val summaryText: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val openAIService: OpenAIService
) : ViewModel() {

    private val _uiState = MutableStateFlow(SummaryUiState())
    val uiState: StateFlow<SummaryUiState> = _uiState.asStateFlow()

    fun updateOriginalText(text: String) {
        _uiState.update { it.copy(originalText = text) }
    }

    fun generateSummary() {
        val text = _uiState.value.originalText

        if (text.isBlank()) {
            _uiState.update { it.copy(error = "Teks tidak boleh kosong") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val summary = openAIService.generateSummary(text)
                _uiState.update {
                    it.copy(
                        summaryText = summary,
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

    fun clearSummary() {
        _uiState.update { it.copy(summaryText = "", originalText = "") }
    }
}