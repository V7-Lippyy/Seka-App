package com.example.seka.ui.screens.tabungan

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seka.data.local.entity.TabunganItem
import com.example.seka.data.repository.TabunganRepository
import com.example.seka.util.FileUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date
import java.util.UUID
import javax.inject.Inject

data class TabunganDetailUiState(
    val tabungan: TabunganItem? = null,
    val nama: String = "",
    val hargaTarget: Double = 0.0,
    val tabunganTerkumpul: Double = 0.0,
    val cicilanJumlah: Double = 0.0,
    val kategori: String = "",
    val targetDate: Date? = null,
    val imagePath: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false,
    val estimasiHari: Int = 0
)

@HiltViewModel
class TabunganDetailViewModel @Inject constructor(
    private val tabunganRepository: TabunganRepository,
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val tabunganId: Long = checkNotNull(savedStateHandle["tabunganId"])

    private val _uiState = MutableStateFlow(TabunganDetailUiState(isLoading = true))
    val uiState: StateFlow<TabunganDetailUiState> = _uiState.asStateFlow()

    init {
        if (tabunganId != -1L) {
            loadTabungan()
        } else {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun loadTabungan() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val tabungan = tabunganRepository.getTabunganById(tabunganId)
                if (tabungan != null) {
                    _uiState.update { state ->
                        state.copy(
                            tabungan = tabungan,
                            nama = tabungan.nama,
                            hargaTarget = tabungan.hargaTarget,
                            tabunganTerkumpul = tabungan.tabunganTerkumpul,
                            cicilanJumlah = tabungan.cicilanJumlah,
                            kategori = tabungan.kategori,
                            targetDate = tabungan.targetDate,
                            imagePath = tabungan.imagePath,
                            isLoading = false,
                            estimasiHari = calculateEstimasiHari(tabungan)
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            error = "Tabungan tidak ditemukan",
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

    fun updateNama(nama: String) {
        _uiState.update { it.copy(nama = nama) }
    }

    fun updateHargaTarget(harga: String) {
        try {
            val hargaDouble = harga.toDoubleOrNull() ?: 0.0
            _uiState.update {
                it.copy(
                    hargaTarget = hargaDouble,
                    estimasiHari = calculateEstimasiHari(
                        it.copy(hargaTarget = hargaDouble)
                    )
                )
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = "Format harga tidak valid") }
        }
    }

    fun updateTabunganTerkumpul(jumlah: String) {
        try {
            val jumlahDouble = jumlah.toDoubleOrNull() ?: 0.0
            _uiState.update {
                it.copy(
                    tabunganTerkumpul = jumlahDouble,
                    estimasiHari = calculateEstimasiHari(
                        it.copy(tabunganTerkumpul = jumlahDouble)
                    )
                )
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = "Format jumlah tidak valid") }
        }
    }

    fun updateCicilanJumlah(cicilan: String) {
        try {
            val cicilanDouble = cicilan.toDoubleOrNull() ?: 0.0
            _uiState.update {
                it.copy(
                    cicilanJumlah = cicilanDouble,
                    estimasiHari = calculateEstimasiHari(
                        it.copy(cicilanJumlah = cicilanDouble)
                    )
                )
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = "Format cicilan tidak valid") }
        }
    }

    fun updateKategori(kategori: String) {
        _uiState.update { it.copy(kategori = kategori) }
    }

    fun updateTargetDate(date: Date?) {
        _uiState.update { it.copy(targetDate = date) }
    }

    fun updateImageUri(uri: Uri?) {
        if (uri == null) {
            _uiState.update { it.copy(imagePath = null) }
            return
        }

        viewModelScope.launch {
            try {
                // Simpan gambar ke folder aplikasi
                val imageFile = saveImageToInternalStorage(uri)
                _uiState.update { it.copy(imagePath = imageFile.absolutePath) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Gagal menyimpan gambar: ${e.message}") }
            }
        }
    }

    private suspend fun saveImageToInternalStorage(uri: Uri): File {
        val newFileName = "tabungan_image_${UUID.randomUUID()}.jpg"
        val imagePath = File(context.filesDir, "images")
        if (!imagePath.exists()) {
            imagePath.mkdirs()
        }

        val destinationFile = File(imagePath, newFileName)

        // Salin file dari Uri ke direktori internal
        FileUtils.copyUriToFile(context, uri, destinationFile)

        return destinationFile
    }

    fun saveTabungan() {
        val currentState = _uiState.value

        if (currentState.nama.isBlank()) {
            _uiState.update { it.copy(error = "Nama barang tidak boleh kosong") }
            return
        }

        if (currentState.hargaTarget <= 0) {
            _uiState.update { it.copy(error = "Harga target harus lebih dari 0") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val tabunganToSave = if (currentState.tabungan != null) {
                    currentState.tabungan.copy(
                        nama = currentState.nama,
                        hargaTarget = currentState.hargaTarget,
                        tabunganTerkumpul = currentState.tabunganTerkumpul,
                        cicilanJumlah = currentState.cicilanJumlah,
                        kategori = currentState.kategori,
                        targetDate = currentState.targetDate,
                        imagePath = currentState.imagePath,
                        updatedAt = Date()
                    )
                } else {
                    TabunganItem(
                        nama = currentState.nama,
                        hargaTarget = currentState.hargaTarget,
                        tabunganTerkumpul = currentState.tabunganTerkumpul,
                        cicilanJumlah = currentState.cicilanJumlah,
                        kategori = currentState.kategori,
                        targetDate = currentState.targetDate,
                        imagePath = currentState.imagePath
                    )
                }

                if (tabunganId == -1L) {
                    tabunganRepository.insertTabungan(tabunganToSave)
                } else {
                    tabunganRepository.updateTabungan(tabunganToSave)
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

    private fun calculateEstimasiHari(state: TabunganDetailUiState): Int {
        return if (state.cicilanJumlah > 0) {
            val remaining = state.hargaTarget - state.tabunganTerkumpul
            if (remaining <= 0) 0 else Math.ceil(remaining / state.cicilanJumlah).toInt()
        } else {
            0
        }
    }

    private fun calculateEstimasiHari(tabungan: TabunganItem): Int {
        return if (tabungan.cicilanJumlah > 0) {
            val remaining = tabungan.hargaTarget - tabungan.tabunganTerkumpul
            if (remaining <= 0) 0 else Math.ceil(remaining / tabungan.cicilanJumlah).toInt()
        } else {
            0
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun tambahTabungan(amount: Double) {
        if (amount <= 0) {
            _uiState.update { it.copy(error = "Jumlah harus lebih dari 0") }
            return
        }

        _uiState.update {
            val newAmount = it.tabunganTerkumpul + amount
            it.copy(
                tabunganTerkumpul = newAmount,
                estimasiHari = calculateEstimasiHari(it.copy(tabunganTerkumpul = newAmount))
            )
        }
    }
}