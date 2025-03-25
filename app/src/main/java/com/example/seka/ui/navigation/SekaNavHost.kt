package com.example.seka.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.seka.ui.screens.airminum.AirMinumScreen
import com.example.seka.ui.screens.enkripsi.EnkripsiDetailScreen
import com.example.seka.ui.screens.enkripsi.EnkripsiScreen
import com.example.seka.ui.screens.home.HomeScreen
import com.example.seka.ui.screens.keuangan.KeuanganDetailScreen
import com.example.seka.ui.screens.keuangan.KeuanganScreen
import com.example.seka.ui.screens.note.NoteDetailScreen
import com.example.seka.ui.screens.note.NoteScreen
import com.example.seka.ui.screens.paraphrase.ParaphraseScreen
import com.example.seka.ui.screens.sekaai.SekaAIScreen
import com.example.seka.ui.screens.splash.SplashScreen
import com.example.seka.ui.screens.summary.SummaryScreen
import com.example.seka.ui.screens.tabungan.TabunganDetailScreen
import com.example.seka.ui.screens.tabungan.TabunganScreen
import com.example.seka.ui.screens.terjemahan.TerjemahanScreen
import com.example.seka.ui.screens.todo.TodoDetailScreen
import com.example.seka.ui.screens.todo.TodoScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Todo : Screen("todo")
    object TodoDetail : Screen("todo_detail/{todoId}") {
        fun createRoute(todoId: Long = -1L) = "todo_detail/$todoId"
    }
    object Note : Screen("note")
    object NoteDetail : Screen("note_detail/{noteId}") {
        fun createRoute(noteId: Long = -1L) = "note_detail/$noteId"
    }
    object Tabungan : Screen("tabungan")
    object TabunganDetail : Screen("tabungan_detail/{tabunganId}") {
        fun createRoute(tabunganId: Long = -1L) = "tabungan_detail/$tabunganId"
    }
    object Keuangan : Screen("keuangan")
    object KeuanganDetail : Screen("keuangan_detail/{keuanganId}") {
        fun createRoute(keuanganId: Long = -1L) = "keuangan_detail/$keuanganId"
    }
    object AirMinum : Screen("air_minum")
    object Summary : Screen("summary")
    object Paraphrase : Screen("paraphrase")
    object Terjemahan : Screen("terjemahan")
    object SekaAI : Screen("sekaai")
    object Enkripsi : Screen("enkripsi")
    object EnkripsiDetail : Screen("enkripsi_detail/{enkripsiId}") {
        fun createRoute(enkripsiId: Long = -1L) = "enkripsi_detail/$enkripsiId"
    }
}

@Composable
fun SekaNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(Screen.Todo.route) {
            TodoScreen(navController = navController)
        }

        composable(
            route = Screen.TodoDetail.route,
            arguments = listOf(navArgument("todoId") { type = NavType.LongType })
        ) { backStackEntry ->
            val todoId = backStackEntry.arguments?.getLong("todoId") ?: -1L
            TodoDetailScreen(navController = navController, todoId = todoId)
        }

        composable(Screen.Note.route) {
            NoteScreen(navController = navController)
        }

        composable(
            route = Screen.NoteDetail.route,
            arguments = listOf(navArgument("noteId") { type = NavType.LongType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId") ?: -1L
            NoteDetailScreen(navController = navController, noteId = noteId)
        }

        composable(Screen.Tabungan.route) {
            TabunganScreen(navController = navController)
        }

        composable(
            route = Screen.TabunganDetail.route,
            arguments = listOf(navArgument("tabunganId") { type = NavType.LongType })
        ) { backStackEntry ->
            val tabunganId = backStackEntry.arguments?.getLong("tabunganId") ?: -1L
            TabunganDetailScreen(navController = navController, tabunganId = tabunganId)
        }

        composable(Screen.Keuangan.route) {
            KeuanganScreen(navController = navController)
        }

        composable(
            route = Screen.KeuanganDetail.route,
            arguments = listOf(navArgument("keuanganId") { type = NavType.LongType })
        ) { backStackEntry ->
            val keuanganId = backStackEntry.arguments?.getLong("keuanganId") ?: -1L
            KeuanganDetailScreen(navController = navController, keuanganId = keuanganId)
        }

        composable(Screen.AirMinum.route) {
            AirMinumScreen(navController = navController)
        }

        composable(Screen.Summary.route) {
            SummaryScreen(navController = navController)
        }

        composable(Screen.Paraphrase.route) {
            ParaphraseScreen(navController = navController)
        }

        composable(Screen.Terjemahan.route) {
            TerjemahanScreen(navController = navController)
        }

        composable(Screen.SekaAI.route) {
            SekaAIScreen(navController = navController)
        }

        composable(Screen.Enkripsi.route) {
            EnkripsiScreen(navController = navController)
        }

        composable(
            route = Screen.EnkripsiDetail.route,
            arguments = listOf(navArgument("enkripsiId") { type = NavType.LongType })
        ) { backStackEntry ->
            val enkripsiId = backStackEntry.arguments?.getLong("enkripsiId") ?: -1L
            EnkripsiDetailScreen(navController = navController, enkripsiId = enkripsiId)
        }
    }
}