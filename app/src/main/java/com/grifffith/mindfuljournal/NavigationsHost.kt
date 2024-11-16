// AppNavigation.kt
package com.griffith.outfitter.outfit

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.grifffith.mindfuljournal.FitnessScreen
import com.grifffith.mindfuljournal.HomeScreen
import com.grifffith.mindfuljournal.JournalScreen

//setting the navigation for the app
@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {

    NavHost(navController = navController, startDestination = "home", modifier = modifier) {
        // Composable function for the Home screen
        composable("home") {
            HomeScreen()
        }

        // Composable function for the Search screen
        composable("Journal") {

            JournalScreen()
        }

        // Composable function for the Settings screen
        composable("Fitness") {

            FitnessScreen()
        }
    }
}
