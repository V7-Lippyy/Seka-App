package com.example.seka.ui.screens.enkripsi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log

private const val TAG = "EnkripsiViewModel"

data class EnkripsiUiState(
    val originalText: String = "",
    val encryptedText: String = "",
    val encryptionCode: String = "",
    val mode: EncryptionMode = EncryptionMode.ENCRYPT,
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class EncryptionMode {
    ENCRYPT, DECRYPT
}

@HiltViewModel
class EnkripsiViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(EnkripsiUiState())
    val uiState: StateFlow<EnkripsiUiState> = _uiState.asStateFlow()

    init {
        Log.d(TAG, "Initialize EnkripsiViewModel")
    }

    fun updateOriginalText(text: String) {
        _uiState.update { it.copy(originalText = text) }
    }

    fun updateEncryptedText(text: String) {
        _uiState.update { it.copy(encryptedText = text) }
    }

    fun updateEncryptionCode(code: String) {
        _uiState.update { it.copy(encryptionCode = code) }
    }

    fun toggleMode() {
        val newMode = when (_uiState.value.mode) {
            EncryptionMode.ENCRYPT -> EncryptionMode.DECRYPT
            EncryptionMode.DECRYPT -> EncryptionMode.ENCRYPT
        }
        _uiState.update { it.copy(mode = newMode) }
    }

    fun processText() {
        val currentState = _uiState.value
        val inputText = when (currentState.mode) {
            EncryptionMode.ENCRYPT -> currentState.originalText
            EncryptionMode.DECRYPT -> currentState.encryptedText
        }

        if (inputText.isBlank()) {
            _uiState.update { it.copy(error = "Teks tidak boleh kosong") }
            return
        }

        if (currentState.encryptionCode.isBlank()) {
            _uiState.update { it.copy(error = "Kode enkripsi tidak boleh kosong") }
            return
        }

        val code = try {
            currentState.encryptionCode.toInt()
        } catch (e: Exception) {
            _uiState.update { it.copy(error = "Kode enkripsi harus berupa angka") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val result = when (currentState.mode) {
                    EncryptionMode.ENCRYPT -> {
                        Log.d(TAG, "Encrypting text with code: $code")
                        val encrypted = encryptText(inputText, code)
                        _uiState.update { it.copy(encryptedText = encrypted) }
                        encrypted
                    }
                    EncryptionMode.DECRYPT -> {
                        Log.d(TAG, "Decrypting text with code: $code")
                        val decrypted = decryptText(inputText, code)
                        _uiState.update { it.copy(originalText = decrypted) }
                        decrypted
                    }
                }

                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing text: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Terjadi kesalahan saat proses enkripsi/dekripsi",
                        isLoading = false
                    )
                }
            }
        }
    }

    // Implementasi sederhana enkripsi/dekripsi langsung di ViewModel
    private fun encryptText(text: String, key: Int): String {
        return text.map { char ->
            if (char.isLetter()) {
                val base = if (char.isUpperCase()) 'A' else 'a'
                val shifted = (char.code - base.code + key) % 26
                (base.code + shifted).toChar()
            } else {
                char
            }
        }.joinToString("")
    }

    private fun decryptText(text: String, key: Int): String {
        return text.map { char ->
            if (char.isLetter()) {
                val base = if (char.isUpperCase()) 'A' else 'a'
                val shifted = (char.code - base.code - key) % 26
                val finalShift = if (shifted < 0) shifted + 26 else shifted
                (base.code + finalShift).toChar()
            } else {
                char
            }
        }.joinToString("")
    }

    fun clearFields() {
        _uiState.update {
            it.copy(
                originalText = "",
                encryptedText = "",
                error = null
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}