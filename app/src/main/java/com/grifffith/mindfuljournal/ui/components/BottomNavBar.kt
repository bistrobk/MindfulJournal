package com.grifffith.mindfuljournal.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {
    // List of bottom navigation items, each having a label, route, and an icon.
    val items = listOf(
        BottomNavItem("Home", "home", icon = { Icon(Icons.Default.Home, contentDescription = "Home") }),
        BottomNavItem("Journal", "journal", icon = { Icon(Icons.Default.Book, contentDescription = "Journal") }),
        BottomNavItem("Fitness", "fitness", icon = { Icon(Icons.Default.FitnessCenter, contentDescription = "Fitness") }),
        BottomNavItem("Mood", "mood", icon = { Icon(Icons.Default.Mood, contentDescription = "Mood") })
    )

    // Get the current route from the navigation controller's back stack
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    // Retrieve the current route, or fallback to the first item's route if none is selected
    val currentRoute = navBackStackEntry.value?.destination?.route ?: items.first().route

    // Bottom navigation bar container with a specific color and elevation
    NavigationBar(
        containerColor = Color(0xFF02253B), // Background color of the bottom bar
        tonalElevation = 4.dp // Shadow elevation for the navigation bar
    ) {
        // Iterate over each item in the items list to display the navigation items
        items.forEach { item ->
            // Only add a navigation item if the icon is provided
            item.icon?.let {
                NavigationBarItem(
                    selected = currentRoute == item.route, // Highlight the selected item
                    onClick = {
                        // If the clicked route is not the current route, navigate to it
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                // Pop the navigation stack to the start destination and save the state
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true // Launch only a single top of the stack to avoid duplicate navigation
                                restoreState = true // Restore the state of the previous destinations
                            }
                        }
                    },
                    icon = it, // Dynamically assign the icon based on the item
                    label = {
                        // Dynamically display the label text
                        Text(
                            text = item.label, // The label of the item
                            fontSize = 14.sp, // Font size for the label
                            fontWeight = if (currentRoute == item.route) FontWeight.Bold else FontWeight.Medium, // Bold if selected
                            color = if (currentRoute == item.route) Color.White else Color(0xFFBCC1C7) // White color for selected item
                        )
                    }
                )
            }
        }
    }
}

// Data class to represent each bottom navigation item
data class BottomNavItem(
    val label: String, // Label text of the item
    val route: String, // Route to navigate to
    val icon: (@Composable () -> Unit)? = null // Composable for the icon (nullable)
)
