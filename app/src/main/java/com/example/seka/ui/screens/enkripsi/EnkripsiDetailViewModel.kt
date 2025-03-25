package com.example.seka.ui.screens.enkripsi

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class EnkripsiDetailUiState(
    val id: Long = 0,
    val title: String = "",
    val originalText: String = "",
    val encryptedText: String = "",
    val encryptionCode: Int = 0,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class EnkripsiDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val enkripsiId: Long? = savedStateHandle["enkripsiId"]

    private val _uiState = MutableStateFlow(EnkripsiDetailUiState())
    val uiState: StateFlow<EnkripsiDetailUiState> = _uiState.asStateFlow()

    // Fungsi untuk validasi kode
    fun verifyCode(code: Int): Boolean {
        val currentState = _uiState.value
        return code == currentState.encryptionCode
    }
}