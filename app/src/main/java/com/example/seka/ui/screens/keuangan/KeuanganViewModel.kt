package com.example.seka.ui.screens.keuangan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seka.data.local.entity.KeuanganItem
import com.example.seka.data.local.entity.TransactionType
import com.example.seka.data.repository.KeuanganRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class KeuanganUiState(
    val transactions: List<KeuanganItem> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val totalBalance: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedType: TransactionType? = null,
    val selectedKategori: String? = null
)

@HiltViewModel
class KeuanganViewModel @Inject constructor(
    private val keuanganRepository: KeuanganRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(KeuanganUiState(isLoading = true))
    val uiState: StateFlow<KeuanganUiState> = _uiState

    init {
        loadAllTransactions()
    }

    fun loadAllTransactions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            keuanganRepository.getAllTransactions()
                .catch { e ->
                    _uiState.update {
                        it.copy(error = e.message, isLoading = false)
                    }
                }
                .combine(keuanganRepository.getTotalByType(TransactionType.INCOME)) { transactions, income ->
                    _uiState.update { state ->
                        state.copy(
                            transactions = transactions,
                            totalIncome = income ?: 0.0
                        )
                    }
                    transactions
                }
                .combine(keuanganRepository.getTotalByType(TransactionType.EXPENSE)) { transactions, expense ->
                    val income = _uiState.value.totalIncome
                    _uiState.update { state ->
                        state.copy(
                            totalExpense = expense ?: 0.0,
                            totalBalance = income - (expense ?: 0.0),
                            isLoading = false
                        )
                    }
                }
                .collect()
        }
    }

    fun searchTransactions(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(searchQuery = query, isLoading = true) }

            if (query.isBlank()) {
                loadAllTransactions()
                return@launch
            }

            keuanganRepository.searchTransactions(query)
                .catch { e ->
                    _uiState.update {
                        it.copy(error = e.message, isLoading = false)
                    }
                }
                .collect { transactions ->
                    val income = transactions
                        .filter { it.tipe == TransactionType.INCOME }
                        .sumOf { it.jumlah }

                    val expense = transactions
                        .filter { it.tipe == TransactionType.EXPENSE }
                        .sumOf { it.jumlah }

                    _uiState.update {
                        it.copy(
                            transactions = transactions,
                            totalIncome = income,
                            totalExpense = expense,
                            totalBalance = income - expense,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun filterByType(type: TransactionType?) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedType = type, isLoading = true) }

            if (type == null) {
                loadAllTransactions()
                return@launch
            }

            keuanganRepository.getTransactionsByType(type)
                .catch { e ->
                    _uiState.update {
                        it.copy(error = e.message, isLoading = false)
                    }
                }
                .collect { transactions ->
                    val income = if (type == TransactionType.INCOME) {
                        transactions.sumOf { it.jumlah }
                    } else {
                        0.0
                    }

                    val expense = if (type == TransactionType.EXPENSE) {
                        transactions.sumOf { it.jumlah }
                    } else {
                        0.0
                    }

                    _uiState.update {
                        it.copy(
                            transactions = transactions,
                            totalIncome = income,
                            totalExpense = expense,
                            totalBalance = income - expense,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun filterByKategori(kategori: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedKategori = kategori, isLoading = true) }

            if (kategori == null) {
                loadAllTransactions()
                return@launch
            }

            keuanganRepository.getTransactionsByKategori(kategori)
                .catch { e ->
                    _uiState.update {
                        it.copy(error = e.message, isLoading = false)
                    }
                }
                .collect { transactions ->
                    val income = transactions
                        .filter { it.tipe == TransactionType.INCOME }
                        .sumOf { it.jumlah }

                    val expense = transactions
                        .filter { it.tipe == TransactionType.EXPENSE }
                        .sumOf { it.jumlah }

                    _uiState.update {
                        it.copy(
                            transactions = transactions,
                            totalIncome = income,
                            totalExpense = expense,
                            totalBalance = income - expense,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun deleteTransaction(keuanganItem: KeuanganItem) {
        viewModelScope.launch {
            keuanganRepository.deleteTransaction(keuanganItem)
        }
    }
}