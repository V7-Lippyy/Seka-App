package com.example.seka.data.repository

import com.example.seka.data.local.dao.AirMinumDao
import com.example.seka.data.local.entity.AirMinumItem
import com.example.seka.data.local.entity.IntervalUnit
import com.example.seka.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class AirMinumRepository @Inject constructor(
    private val airMinumDao: AirMinumDao
) {
    // Tambah satu gelas untuk hari ini
    suspend fun tambahGelas(tanggal: Date = Date()) {
        val startOfDay = DateUtils.getStartOfDay(tanggal)
        val existingRecord = airMinumDao.getAirMinumByDate(startOfDay).first()
        val currentTime = Date()

        if (existingRecord != null) {
            val updatedRecord = existingRecord.copy(
                jumlahGelas = existingRecord.jumlahGelas + 1,
                waktuGelasTerakhir = currentTime,
                updatedAt = currentTime
            )
            airMinumDao.update(updatedRecord)
        } else {
            // Jika tidak ada record, buat record baru
            val newRecord = AirMinumItem(
                tanggal = startOfDay,
                jumlahGelas = 1,
                targetGelas = 8,  // Default target
                ukuranGelas = 250, // Default ukuran
                intervalPengingat = 60, // Default 60 menit
                intervalUnit = IntervalUnit.MINUTES, // Default unit menit
                waktuGelasTerakhir = currentTime
            )
            airMinumDao.insert(newRecord)
        }
    }

    // Dapatkan record air minum hari ini
    fun getAirMinumHariIni(): Flow<AirMinumItem?> {
        val startOfDay = DateUtils.getStartOfDay()
        return airMinumDao.getAirMinumByDate(startOfDay)
    }

    // Update pengaturan
    suspend fun updatePengaturan(
        targetGelas: Int? = null,
        ukuranGelas: Int? = null,
        pengingat: Boolean? = null,
        intervalPengingat: Int? = null,
        intervalUnit: IntervalUnit? = null
    ) {
        val startOfDay = DateUtils.getStartOfDay()
        val existingRecord = airMinumDao.getAirMinumByDate(startOfDay).first()
        val currentTime = Date()

        if (existingRecord != null) {
            val updatedRecord = existingRecord.copy(
                targetGelas = targetGelas ?: existingRecord.targetGelas,
                ukuranGelas = ukuranGelas ?: existingRecord.ukuranGelas,
                pengingat = pengingat ?: existingRecord.pengingat,
                intervalPengingat = intervalPengingat ?: existingRecord.intervalPengingat,
                intervalUnit = intervalUnit ?: existingRecord.intervalUnit,
                updatedAt = currentTime
            )
            airMinumDao.update(updatedRecord)
        } else {
            val newRecord = AirMinumItem(
                tanggal = startOfDay,
                targetGelas = targetGelas ?: 8,
                ukuranGelas = ukuranGelas ?: 250,
                pengingat = pengingat ?: true,
                intervalPengingat = intervalPengingat ?: 60, // Default 60 menit
                intervalUnit = intervalUnit ?: IntervalUnit.MINUTES // Default unit menit
            )
            airMinumDao.insert(newRecord)
        }
    }

    // Dapatkan semua record
    fun getAllAirMinumRecords(): Flow<List<AirMinumItem>> {
        return airMinumDao.getAllAirMinumRecords()
    }

    // Hitung berapa kali target tercapai dalam seminggu
    fun getTargetAchievedCount(): Flow<Int> {
        return airMinumDao.getTargetAchievedCount()
    }

    // Hitung berapa hari aktif (dengan jumlahGelas > 0)
    fun getActiveDaysCount(): Flow<Int> {
        return airMinumDao.getActiveDaysCount()
    }

    // Hapus record lama (misalnya setiap minggu)
    suspend fun bersihkanRecordLama() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -30) // Simpan data 30 hari untuk statistik yang lebih baik
        val cutoffDate = calendar.time

        airMinumDao.deleteOldRecords(cutoffDate)
    }

    // Update waktu pengingat terakhir
    suspend fun updateWaktuPengingatTerakhir(waktu: Date = Date()) {
        val startOfDay = DateUtils.getStartOfDay()
        val existingRecord = airMinumDao.getAirMinumByDate(startOfDay).first()

        if (existingRecord != null) {
            val updatedRecord = existingRecord.copy(
                waktuPengingatTerakhir = waktu,
                updatedAt = Date()
            )
            airMinumDao.update(updatedRecord)
        }
    }

    // Cek apakah perlu menampilkan pengingat - REFACTORED
    suspend fun perluPengingat(): Boolean {
        val startOfDay = DateUtils.getStartOfDay()
        val record = airMinumDao.getAirMinumByDateSuspend(startOfDay) ?: return true

        // Jika pengingat tidak aktif, tidak perlu menampilkan
        if (!record.pengingat) return false

        // Jika sudah mencapai target, tidak perlu pengingat lagi
        if (record.jumlahGelas >= record.targetGelas) return false

        val currentTime = Date()

        // Konversi interval ke milidetik berdasarkan unit
        val intervalMillis = when (record.intervalUnit) {
            IntervalUnit.MINUTES -> record.intervalPengingat * 60 * 1000L
            IntervalUnit.HOURS -> record.intervalPengingat * 60 * 60 * 1000L
        }

        // Jika belum pernah menambah gelas hari ini
        if (record.waktuGelasTerakhir == null) {
            val dayStartTime = startOfDay.time
            val nextReminderTime = dayStartTime + intervalMillis
            return currentTime.time >= nextReminderTime
        }

        // Jika sudah pernah menambah gelas, cek apakah sudah melewati interval sejak gelas terakhir
        val lastGlassTime = record.waktuGelasTerakhir!!.time
        val nextReminderTime = lastGlassTime + intervalMillis

        // Jika waktu sekarang sudah melewati waktu pengingat berikutnya
        if (currentTime.time >= nextReminderTime) {
            // Jika belum pernah ada pengingat atau pengingat terakhir
            // lebih dari 15 menit yang lalu (mencegah spam notifikasi)
            if (record.waktuPengingatTerakhir == null) {
                return true
            }

            val lastReminderTime = record.waktuPengingatTerakhir!!.time
            val minTimeBetweenReminders = 15 * 60 * 1000L // 15 menit minimum antara pengingat

            return currentTime.time >= (lastReminderTime + minTimeBetweenReminders)
        }

        return false
    }

    // Dapatkan rata-rata pencapaian target 7 hari terakhir
    fun getAverageLast7Days(): Flow<Double?> {
        val endDate = DateUtils.getStartOfDay()
        val startDate = DateUtils.getDaysAgo(7)

        return airMinumDao.getAverageCompletionRate(startDate, endDate)
    }

    // Reset data hari ini (set jumlah gelas ke 0)
    suspend fun resetDataHariIni() {
        val startOfDay = DateUtils.getStartOfDay()
        val existingRecord = airMinumDao.getAirMinumByDate(startOfDay).first()

        if (existingRecord != null) {
            // Pertahankan preferensi pengguna (target, ukuran, dll) tetapi reset jumlah gelas
            val resetRecord = existingRecord.copy(
                jumlahGelas = 0,  // Reset ke 0
                waktuGelasTerakhir = null,  // Reset waktu terakhir minum
                updatedAt = Date()
            )
            airMinumDao.update(resetRecord)
        }
        // Jika tidak ada record untuk hari ini, tidak perlu melakukan apa-apa
    }

    // Reset data minggu ini (hapus semua record dari 7 hari terakhir)
    suspend fun resetDataMingguan() {
        val endDate = DateUtils.getStartOfDay()
        val startDate = DateUtils.getDaysAgo(7)

        // Dapatkan semua record dari 7 hari terakhir
        val recentRecords = airMinumDao.getAllAirMinumRecordsBetweenDates(startDate, endDate).first()

        // Perbarui semua record dengan jumlahGelas = 0
        for (record in recentRecords) {
            val resetRecord = record.copy(
                jumlahGelas = 0,
                waktuGelasTerakhir = null,
                updatedAt = Date()
            )
            airMinumDao.update(resetRecord)
        }
    }
}