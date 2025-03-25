package com.example.seka.ui.utils // sesuaikan dengan package Anda

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun SystemBarColors(
    statusBarColor: Color,
    navigationBarColor: Color,
    isDarkTheme: Boolean
) {
    val view = LocalView.current
    val context = LocalContext.current

    if (!view.isInEditMode) {
        DisposableEffect(statusBarColor, navigationBarColor, isDarkTheme) {
            val window = (context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = statusBarColor.toArgb()
                window.navigationBarColor = navigationBarColor.toArgb()

                // Mengatur apakah konten di status bar harus menggunakan warna gelap
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !isDarkTheme
                    isAppearanceLightNavigationBars = !isDarkTheme
                }
            }
            onDispose {}
        }
    }
}