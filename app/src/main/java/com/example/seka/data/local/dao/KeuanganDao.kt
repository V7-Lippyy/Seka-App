package com.example.seka.data.local.dao

import androidx.room.*
import com.example.seka.data.local.entity.KeuanganItem
import com.example.seka.data.local.entity.TransactionType
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface KeuanganDao {
    @Query("SELECT * FROM keuangan_items ORDER BY tanggal DESC")
    fun getAllTransactions(): Flow<List<KeuanganItem>>

    @Query("SELECT * FROM keuangan_items WHERE judul LIKE '%' || :searchQuery || '%' OR deskripsi LIKE '%' || :searchQuery || '%'")
    fun searchTransactions(searchQuery: String): Flow<List<KeuanganItem>>

    @Query("SELECT * FROM keuangan_items WHERE kategori = :kategori")
    fun getTransactionsByKategori(kategori: String): Flow<List<KeuanganItem>>

    @Query("SELECT * FROM keuangan_items WHERE tipe = :tipe")
    fun getTransactionsByType(tipe: TransactionType): Flow<List<KeuanganItem>>

    @Query("SELECT * FROM keuangan_items WHERE tanggal BETWEEN :startDate AND :endDate")
    fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<KeuanganItem>>

    @Query("SELECT SUM(jumlah) FROM keuangan_items WHERE tipe = :tipe")
    fun getTotalByType(tipe: TransactionType): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keuanganItem: KeuanganItem): Long

    @Update
    suspend fun update(keuanganItem: KeuanganItem)

    @Delete
    suspend fun delete(keuanganItem: KeuanganItem)

    @Query("SELECT * FROM keuangan_items WHERE id = :id")
    suspend fun getTransactionById(id: Long): KeuanganItem?
}