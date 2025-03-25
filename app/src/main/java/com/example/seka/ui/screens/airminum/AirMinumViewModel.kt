package com.example.seka.ui.screens.airminum

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seka.data.local.entity.AirMinumItem
import com.example.seka.data.local.entity.IntervalUnit
import com.example.seka.data.repository.AirMinumRepository
import com.example.seka.util.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AirMinumUiState(
    val airMinumHariIni: AirMinumItem? = null,
    val semuaRecord: List<AirMinumItem> = emptyList(),
    val targetTercapai: Int = 0,
    val hariAktif: Int = 0,
    val rataRataKeberhasilan: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AirMinumViewModel @Inject constructor(
    private val airMinumRepository: AirMinumRepository,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(AirMinumUiState(isLoading = true))
    val uiState: StateFlow<AirMinumUiState> = _uiState.asStateFlow()

    init {
        setupDataCollection()
    }

    private fun setupDataCollection() {
        viewModelScope.launch {
            try {
                // Set up continuous monitoring of the records
                combine(
                    airMinumRepository.getAirMinumHariIni(),
                    airMinumRepository.getAllAirMinumRecords(),
                    airMinumRepository.getTargetAchievedCount(),
                    airMinumRepository.getActiveDaysCount(),
                    airMinumRepository.getAverageLast7Days()
                ) { hariIni, semuaRecord, targetTercapai, hariAktif, rataRata ->
                    AirMinumUiState(
                        airMinumHariIni = hariIni,
                        semuaRecord = semuaRecord,
                        targetTercapai = targetTercapai,
                        hariAktif = hariAktif,
                        rataRataKeberhasilan = rataRata ?: 0.0,
                        isLoading = false
                    )
                }.collect { newState ->
                    _uiState.update { currentState ->
                        newState.copy(
                            error = currentState.error
                        )
                    }

                    // Update reminder schedule based on current settings
                    newState.airMinumHariIni?.let { item ->
                        // Make sure to reschedule reminders when settings change
                        reminderScheduler.scheduleWaterReminder(item.pengingat)
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Gagal memuat data",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun tambahGelas() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                airMinumRepository.tambahGelas()
                // Flow will update automatically
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Gagal menambah gelas: ${e.message}",
                        isLoading = false
                    )
                }
                e.printStackTrace()
            }
        }
    }

    fun updatePengaturan(
        targetGelas: Int? = null,
        ukuranGelas: Int? = null,
        pengingat: Boolean? = null,
        intervalPengingat: Int? = null,
        intervalUnit: IntervalUnit? = null
    ) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                // Update settings in repository
                airMinumRepository.updatePengaturan(
                    targetGelas = targetGelas,
                    ukuranGelas = ukuranGelas,
                    pengingat = pengingat,
                    intervalPengingat = intervalPengingat,
                    intervalUnit = intervalUnit
                )

                // Immediately reschedule reminders if any reminder-related setting has changed
                if (pengingat != null || intervalPengingat != null || intervalUnit != null) {
                    // Use current value if new value not provided
                    val shouldEnableReminder = pengingat ?: uiState.value.airMinumHariIni?.pengingat ?: true
                    reminderScheduler.scheduleWaterReminder(shouldEnableReminder)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Gagal memperbarui pengaturan: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    // Reset data hari ini
    fun resetDataHariIni() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                airMinumRepository.resetDataHariIni()
                // Flow akan otomatis memperbarui UI
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Gagal me-reset data: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    // Reset data minggu ini
    fun resetDataMingguan() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                airMinumRepository.resetDataMingguan()
                // Flow akan otomatis memperbarui UI
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Gagal me-reset data mingguan: ${e.message}",
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