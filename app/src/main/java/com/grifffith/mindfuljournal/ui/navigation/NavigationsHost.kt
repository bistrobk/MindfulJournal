package com.grifffith.mindfuljournal.ui.navigation

import FitnessRepository
import JournalRepository
import JournalScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.grifffith.mindfuljournal.db.MindfulJournalDBHelper
import com.grifffith.mindfuljournal.repository.MoodRepository
import com.grifffith.mindfuljournal.ui.components.BottomNavigationBar
import com.grifffith.mindfuljournal.ui.screens.FitnessScreen
import com.grifffith.mindfuljournal.ui.screens.HomeScreen
import com.grifffith.mindfuljournal.ui.screens.MoodScreen
import com.grifffith.mindfuljournal.ui.screens.SplashScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Journal : Screen("journal")
    object Fitness : Screen("fitness")
    object Mood : Screen("mood")
    object Splash : Screen("splash")
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    dbHelper: MindfulJournalDBHelper
) {
    // Remember repositories to ensure they're only created once
    val journalRepository = remember { JournalRepository(dbHelper) }
    val moodRepository = remember { MoodRepository(dbHelper) }
    val fitnessRepository = remember { FitnessRepository(dbHelper) }

    // Get the current route
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route ?: Screen.Home.route

    Column(modifier = modifier) {
        // Navigation host
        NavHost(navController = navController, startDestination = Screen.Splash.route) {
            composable(Screen.Splash.route) {
                SplashScreen(navController) // Ensure SplashScreen accepts navController
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    dbHelper = dbHelper,
                    fitnessRepository = fitnessRepository
                )
            }
            composable(Screen.Journal.route) {
                JournalScreen(journalRepository = journalRepository)
            }
            composable(Screen.Fitness.route) {
                FitnessScreen(fitnessRepository = fitnessRepository)
            }
            composable(Screen.Mood.route) {
                MoodScreen(moodRepository = moodRepository)
            }
        }

        // Show BottomNavigationBar only if not on Splash screen
        if (currentRoute != Screen.Splash.route) {
            BottomNavigationBar(navController = navController)
        }
    }
}
