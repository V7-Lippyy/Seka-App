package com.example.seka.ui.screens.tabungan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seka.data.local.entity.TabunganItem
import com.example.seka.data.repository.TabunganRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class TabunganUiState(
    val tabunganItems: List<TabunganItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedKategori: String? = null
)

@HiltViewModel
class TabunganViewModel @Inject constructor(
    private val tabunganRepository: TabunganRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TabunganUiState(isLoading = true))
    val uiState: StateFlow<TabunganUiState> = _uiState

    init {
        loadAllTabungan()
    }

    fun loadAllTabungan() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            tabunganRepository.getAllTabungan()
                .catch { e ->
                    _uiState.update {
                        it.copy(error = e.message, isLoading = false)
                    }
                }
                .collect { items ->
                    _uiState.update {
                        it.copy(tabunganItems = items, isLoading = false)
                    }
                }
        }
    }

    fun searchTabungan(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(searchQuery = query, isLoading = true) }

            if (query.isBlank()) {
                loadAllTabungan()
                return@launch
            }

            tabunganRepository.searchTabungan(query)
                .catch { e ->
                    _uiState.update {
                        it.copy(error = e.message, isLoading = false)
                    }
                }
                .collect { items ->
                    _uiState.update {
                        it.copy(tabunganItems = items, isLoading = false)
                    }
                }
        }
    }

    fun filterByKategori(kategori: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedKategori = kategori, isLoading = true) }

            if (kategori == null) {
                loadAllTabungan()
                return@launch
            }

            tabunganRepository.getTabunganByKategori(kategori)
                .catch { e ->
                    _uiState.update {
                        it.copy(error = e.message, isLoading = false)
                    }
                }
                .collect { items ->
                    _uiState.update {
                        it.copy(tabunganItems = items, isLoading = false)
                    }
                }
        }
    }

    fun updateTabungan(tabunganItem: TabunganItem, newAmount: Double) {
        viewModelScope.launch {
            val updated = tabunganItem.copy(
                tabunganTerkumpul = tabunganItem.tabunganTerkumpul + newAmount,
                updatedAt = Date()
            )
            tabunganRepository.updateTabungan(updated)
        }
    }

    fun deleteTabungan(tabunganItem: TabunganItem) {
        viewModelScope.launch {
            tabunganRepository.deleteTabungan(tabunganItem)
        }
    }
}