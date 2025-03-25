package com.example.seka.data.local.dao

import androidx.room.*
import com.example.seka.data.local.entity.AirMinumItem
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface AirMinumDao {
    @Query("SELECT * FROM air_minum_items WHERE date(tanggal/1000, 'unixepoch') = date(:tanggal/1000, 'unixepoch')")
    fun getAirMinumByDate(tanggal: Date): Flow<AirMinumItem?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(airMinumItem: AirMinumItem): Long

    @Update
    suspend fun update(airMinumItem: AirMinumItem)

    @Query("SELECT * FROM air_minum_items ORDER BY tanggal DESC")
    fun getAllAirMinumRecords(): Flow<List<AirMinumItem>>

    @Query("SELECT * FROM air_minum_items WHERE tanggal BETWEEN :startDate AND :endDate ORDER BY tanggal DESC")
    fun getAllAirMinumRecordsBetweenDates(startDate: Date, endDate: Date): Flow<List<AirMinumItem>>

    @Query("SELECT COUNT(*) FROM air_minum_items WHERE jumlahGelas >= targetGelas")
    fun getTargetAchievedCount(): Flow<Int>

    // Menghitung jumlah hari aktif (dengan jumlahGelas > 0)
    @Query("SELECT COUNT(*) FROM air_minum_items WHERE jumlahGelas > 0")
    fun getActiveDaysCount(): Flow<Int>

    @Query("DELETE FROM air_minum_items WHERE tanggal < :cutoffDate")
    suspend fun deleteOldRecords(cutoffDate: Date)

    @Query("SELECT * FROM air_minum_items WHERE date(tanggal/1000, 'unixepoch') = date(:tanggal/1000, 'unixepoch') LIMIT 1")
    suspend fun getAirMinumByDateSuspend(tanggal: Date): AirMinumItem?

    @Query("SELECT AVG(jumlahGelas * 100.0 / targetGelas) FROM air_minum_items WHERE tanggal BETWEEN :startDate AND :endDate")
    fun getAverageCompletionRate(startDate: Date, endDate: Date): Flow<Double?>
}