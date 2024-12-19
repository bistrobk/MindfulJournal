package com.grifffith.mindfuljournal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.grifffith.mindfuljournal.R
import kotlinx.coroutines.delay

// Define constants for maintainability
private const val SPLASH_DELAY = 2000L // Delay before navigating to the home screen (in milliseconds)
private const val HOME_ROUTE = "home" // Define the home route for navigation

@Composable
fun SplashScreen(navController: NavController) {
    // Box that holds the splash screen content
    Box(
        modifier = Modifier
            .fillMaxSize() // Make the Box fill the entire screen
            .background(Color(0xFF02253B)), // Background color (a dark shade of blue)
        contentAlignment = Alignment.Center // Center the content (logo) within the Box
    ) {
        // Display the logo in the center of the screen
        Image(
            painter = painterResource(id = R.drawable.mindfulness), // Load the logo image
            contentDescription = "Logo", // Accessibility description for the logo image
            modifier = Modifier.size(150.dp) // Set the size of the logo
        )

        // LaunchedEffect block to handle the splash screen delay and navigation
        LaunchedEffect(Unit) {
            delay(SPLASH_DELAY) // Wait for the defined splash screen delay
            navController.navigate(HOME_ROUTE) { // Navigate to the home screen after delay
                popUpTo("splash") { inclusive = true } // Remove the splash screen from the back stack
            }
        }
    }
}
