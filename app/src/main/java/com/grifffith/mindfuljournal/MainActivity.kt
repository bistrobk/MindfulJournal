package com.grifffith.mindfuljournal

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.grifffith.mindfuljournal.db.MindfulJournalDBHelper
import com.grifffith.mindfuljournal.ui.components.BottomNavigationBar
import com.grifffith.mindfuljournal.ui.navigation.AppNavigation
import com.grifffith.mindfuljournal.ui.theme.MindfulJournalTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the database helper
        val dbHelper = MindfulJournalDBHelper(this)

        setContent {
            // Pass the database helper to the composable
            MindfulJournalApp(dbHelper = dbHelper)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MindfulJournalApp(dbHelper: MindfulJournalDBHelper) {
    MindfulJournalTheme {
        val navController = rememberNavController()

        Scaffold(
            bottomBar = {
                // Add a bottom navigation bar
                BottomNavigationBar(navController = navController)
            }
        ) { innerPadding ->
            // Pass dbHelper to navigation
            AppNavigation(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                dbHelper = dbHelper
            )
        }
    }
}
