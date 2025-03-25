package com.example.seka.ui.screens.keuangan

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seka.data.local.entity.KeuanganItem
import com.example.seka.data.local.entity.TransactionType
import com.example.seka.data.repository.KeuanganRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class KeuanganDetailUiState(
    val keuanganItem: KeuanganItem? = null,
    val tanggal: Date = Date(),
    val judul: String = "",
    val deskripsi: String = "",
    val jumlah: Double = 0.0,
    val tipe: TransactionType = TransactionType.INCOME,
    val kategori: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class KeuanganDetailViewModel @Inject constructor(
    private val keuanganRepository: KeuanganRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val keuanganId: Long = checkNotNull(savedStateHandle["keuanganId"])

    private val _uiState = MutableStateFlow(KeuanganDetailUiState(isLoading = true))
    val uiState: StateFlow<KeuanganDetailUiState> = _uiState.asStateFlow()

    init {
        if (keuanganId != -1L) {
            loadKeuangan()
        } else {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun loadKeuangan() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val keuangan = keuanganRepository.getTransactionById(keuanganId)
                if (keuangan != null) {
                    _uiState.update { state ->
                        state.copy(
                            keuanganItem = keuangan,
                            tanggal = keuangan.tanggal,
                            judul = keuangan.judul,
                            deskripsi = keuangan.deskripsi,
                            jumlah = keuangan.jumlah,
                            tipe = keuangan.tipe,
                            kategori = keuangan.kategori,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            error = "Transaksi tidak ditemukan",
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

    fun updateTanggal(tanggal: Date) {
        _uiState.update { it.copy(tanggal = tanggal) }
    }

    fun updateJudul(judul: String) {
        _uiState.update { it.copy(judul = judul) }
    }

    fun updateDeskripsi(deskripsi: String) {
        _uiState.update { it.copy(deskripsi = deskripsi) }
    }

    fun updateJumlah(jumlah: String) {
        try {
            val jumlahDouble = jumlah.toDoubleOrNull() ?: 0.0
            _uiState.update { it.copy(jumlah = jumlahDouble) }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = "Format jumlah tidak valid") }
        }
    }

    fun updateTipe(tipe: TransactionType) {
        _uiState.update { it.copy(tipe = tipe) }
    }

    fun updateKategori(kategori: String) {
        _uiState.update { it.copy(kategori = kategori) }
    }

    fun saveKeuangan() {
        val currentState = _uiState.value

        if (currentState.judul.isBlank()) {
            _uiState.update { it.copy(error = "Judul tidak boleh kosong") }
            return
        }

        if (currentState.jumlah <= 0) {
            _uiState.update { it.copy(error = "Jumlah harus lebih dari 0") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val keuanganToSave = if (currentState.keuanganItem != null) {
                    currentState.keuanganItem.copy(
                        tanggal = currentState.tanggal,
                        judul = currentState.judul,
                        deskripsi = currentState.deskripsi,
                        jumlah = currentState.jumlah,
                        tipe = currentState.tipe,
                        kategori = currentState.kategori,
                        updatedAt = Date()
                    )
                } else {
                    KeuanganItem(
                        tanggal = currentState.tanggal,
                        judul = currentState.judul,
                        deskripsi = currentState.deskripsi,
                        jumlah = currentState.jumlah,
                        tipe = currentState.tipe,
                        kategori = currentState.kategori
                    )
                }

                if (keuanganId == -1L) {
                    keuanganRepository.insertTransaction(keuanganToSave)
                } else {
                    keuanganRepository.updateTransaction(keuanganToSave)
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