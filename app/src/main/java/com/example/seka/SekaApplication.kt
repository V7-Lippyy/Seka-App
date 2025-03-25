package com.example.seka

import android.app.Application
import com.example.seka.util.workers.WorkerScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SekaApplication : Application() {

    @Inject
    lateinit var workerScheduler: WorkerScheduler

    override fun onCreate() {
        super.onCreate()

        // Jadwalkan worker untuk notifikasi dan pengingat
        setupWorkers()
    }

    private fun setupWorkers() {
        workerScheduler.scheduleAllWorkers()
    }
}