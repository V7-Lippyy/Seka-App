package com.example.seka.data.repository

import com.example.seka.data.local.dao.TabunganDao
import com.example.seka.data.local.entity.TabunganItem
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class TabunganRepository @Inject constructor(
    private val tabunganDao: TabunganDao
) {
    fun getAllTabungan(): Flow<List<TabunganItem>> = tabunganDao.getAllTabungan()

    fun searchTabungan(query: String): Flow<List<TabunganItem>> = tabunganDao.searchTabungan(query)

    fun getTabunganByKategori(kategori: String): Flow<List<TabunganItem>> =
        tabunganDao.getTabunganByKategori(kategori)

    fun getTabunganByDateRange(startDate: Date, endDate: Date): Flow<List<TabunganItem>> =
        tabunganDao.getTabunganByDateRange(startDate, endDate)

    suspend fun insertTabungan(tabunganItem: TabunganItem): Long = tabunganDao.insert(tabunganItem)

    suspend fun updateTabungan(tabunganItem: TabunganItem) = tabunganDao.update(tabunganItem)

    suspend fun deleteTabungan(tabunganItem: TabunganItem) = tabunganDao.delete(tabunganItem)

    suspend fun getTabunganById(id: Long): TabunganItem? = tabunganDao.getTabunganById(id)
}