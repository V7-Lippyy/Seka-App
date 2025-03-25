package com.example.seka

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.seka.ui.navigation.SekaNavHost
import com.example.seka.ui.theme.SekaTheme
import com.example.seka.util.ReminderScheduler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var reminderScheduler: ReminderScheduler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi pengingat air minum saat startup
        setupInitialReminder()

        setContent {
            SekaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SekaNavHost()
                }
            }
        }
    }

    private fun setupInitialReminder() {
        // Jadwalkan pengingat dengan nilai default true
        // Nanti akan diperbarui berdasarkan preferensi pengguna saat ViewModel dijalankan
        reminderScheduler.scheduleWaterReminder(true)
    }
}