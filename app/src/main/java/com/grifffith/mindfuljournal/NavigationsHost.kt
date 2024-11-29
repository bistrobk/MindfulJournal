package com.grifffith.mindfuljournal

import BottomNavigationBar
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Journal : Screen("journal")
    object Fitness : Screen("fitness")
    object Mood : Screen("mood")
    object Splash : Screen("splash")
}

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    // Get the current backstack entry
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    Column(modifier = modifier) {
        // NavHost for screen navigation
        NavHost(navController = navController, startDestination = Screen.Splash.route) {
            composable(Screen.Splash.route) {
                SplashScreen(navController)
            }
            composable(Screen.Home.route) {
                HomeScreen()
            }
            composable(Screen.Journal.route) {
                JournalScreen()
            }
            composable(Screen.Fitness.route) {
                FitnessScreen()
            }
            composable(Screen.Mood.route) {

                MoodScreen()


            }

        }

        // Only show the BottomNavigationBar if the current screen is not Splash
        if (currentRoute != Screen.Splash.route) {
            BottomNavigationBar(navController = navController)
        }
    }
}
