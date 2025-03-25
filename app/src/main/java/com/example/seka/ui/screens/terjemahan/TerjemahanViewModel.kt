package com.example.seka.ui.screens.terjemahan

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

data class TerjemahanUiState(
    val originalText: String = "",
    val translatedText: String = "",
    val sourceLanguage: String = "Indonesia",
    val targetLanguage: String = "Inggris",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TerjemahanViewModel @Inject constructor(
    private val openAIService: OpenAIService
) : ViewModel() {

    private val _uiState = MutableStateFlow(TerjemahanUiState())
    val uiState: StateFlow<TerjemahanUiState> = _uiState.asStateFlow()

    val availableLanguages = listOf(
        "Indonesia", "Inggris", "Spanyol", "Prancis", "Jerman",
        "Italia", "Portugis", "Belanda", "Rusia", "Jepang",
        "Korea", "Mandarin", "Arab", "Hindi", "Bengali",
        "Jawa", "Sunda"
    )

    fun updateOriginalText(text: String) {
        _uiState.update { it.copy(originalText = text) }
    }

    fun updateSourceLanguage(language: String) {
        _uiState.update { it.copy(sourceLanguage = language) }
    }

    fun updateTargetLanguage(language: String) {
        _uiState.update { it.copy(targetLanguage = language) }
    }

    fun translate() {
        val text = _uiState.value.originalText
        val sourceLanguage = _uiState.value.sourceLanguage
        val targetLanguage = _uiState.value.targetLanguage

        if (text.isBlank()) {
            _uiState.update { it.copy(error = "Teks tidak boleh kosong") }
            return
        }

        if (sourceLanguage == targetLanguage) {
            _uiState.update { it.copy(error = "Bahasa sumber dan target tidak boleh sama") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val translated = openAIService.translate(text, sourceLanguage, targetLanguage)
                _uiState.update {
                    it.copy(
                        translatedText = translated,
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

    fun clearTranslation() {
        _uiState.update { it.copy(translatedText = "", originalText = "") }
    }

    fun swapLanguages() {
        _uiState.update {
            it.copy(
                sourceLanguage = it.targetLanguage,
                targetLanguage = it.sourceLanguage,
                originalText = it.translatedText,
                translatedText = it.originalText
            )
        }
    }
}