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
    val items = listOf(
        BottomNavItem("Home", "home"),
        BottomNavItem("Journal", "journal"),
        BottomNavItem("Fitness", "fitness"),
        BottomNavItem("Mood", "mood")
    )

    // Navigation Bar with a custom background color
    NavigationBar(
        containerColor = Color(0xFF02253B), // Background color of the bottom navigation bar

    ) {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = {}, // No icon, just text
                label = {
                    Text(
                        text = item.label,
                        color = if (currentRoute == item.route) Color.White else Color(0xFFBCC1C7), // Active text color (white) and inactive (light gray)
                        fontSize = 16.sp, // Font size for the text
                        fontWeight = if (currentRoute == item.route) FontWeight.Bold else FontWeight.Normal, // Bold for active item
                        letterSpacing = 1.sp // Spacing between letters for a more polished look
                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

data class BottomNavItem(val label: String, val route: String)
