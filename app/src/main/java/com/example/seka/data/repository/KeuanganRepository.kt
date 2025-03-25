package com.example.seka.data.repository

import com.example.seka.data.local.dao.KeuanganDao
import com.example.seka.data.local.entity.KeuanganItem
import com.example.seka.data.local.entity.TransactionType
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class KeuanganRepository @Inject constructor(
    private val keuanganDao: KeuanganDao
) {
    fun getAllTransactions(): Flow<List<KeuanganItem>> = keuanganDao.getAllTransactions()

    fun searchTransactions(query: String): Flow<List<KeuanganItem>> =
        keuanganDao.searchTransactions(query)

    fun getTransactionsByKategori(kategori: String): Flow<List<KeuanganItem>> =
        keuanganDao.getTransactionsByKategori(kategori)

    fun getTransactionsByType(type: TransactionType): Flow<List<KeuanganItem>> =
        keuanganDao.getTransactionsByType(type)

    fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<KeuanganItem>> =
        keuanganDao.getTransactionsByDateRange(startDate, endDate)

    fun getTotalByType(type: TransactionType): Flow<Double?> =
        keuanganDao.getTotalByType(type)

    suspend fun insertTransaction(keuanganItem: KeuanganItem): Long =
        keuanganDao.insert(keuanganItem)

    suspend fun updateTransaction(keuanganItem: KeuanganItem) =
        keuanganDao.update(keuanganItem)

    suspend fun deleteTransaction(keuanganItem: KeuanganItem) =
        keuanganDao.delete(keuanganItem)

    suspend fun getTransactionById(id: Long): KeuanganItem? =
        keuanganDao.getTransactionById(id)
}