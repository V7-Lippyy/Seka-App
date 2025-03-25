package com.example.seka.data.local.dao

import androidx.room.*
import com.example.seka.data.local.entity.TabunganItem
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TabunganDao {
    @Query("SELECT * FROM tabungan_items ORDER BY createdAt DESC")
    fun getAllTabungan(): Flow<List<TabunganItem>>

    @Query("SELECT * FROM tabungan_items WHERE nama LIKE '%' || :searchQuery || '%'")
    fun searchTabungan(searchQuery: String): Flow<List<TabunganItem>>

    @Query("SELECT * FROM tabungan_items WHERE kategori = :kategori")
    fun getTabunganByKategori(kategori: String): Flow<List<TabunganItem>>

    @Query("SELECT * FROM tabungan_items WHERE createdAt BETWEEN :startDate AND :endDate")
    fun getTabunganByDateRange(startDate: Date, endDate: Date): Flow<List<TabunganItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tabunganItem: TabunganItem): Long

    @Update
    suspend fun update(tabunganItem: TabunganItem)

    @Delete
    suspend fun delete(tabunganItem: TabunganItem)

    @Query("SELECT * FROM tabungan_items WHERE id = :id")
    suspend fun getTabunganById(id: Long): TabunganItem?
}