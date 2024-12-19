package com.grifffith.mindfuljournal.ui.screens

import FitnessRepository
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grifffith.mindfuljournal.StepCounter
import com.grifffith.mindfuljournal.ui.components.StepGoalUpdater
import com.grifffith.mindfuljournal.ui.theme.CardBackground
import com.grifffith.mindfuljournal.ui.theme.CardTitleYellow
import com.grifffith.mindfuljournal.ui.theme.DarkBlue
import com.grifffith.mindfuljournal.ui.theme.GoldYellow
import com.grifffith.mindfuljournal.ui.theme.LightGray
import com.grifffith.mindfuljournal.ui.theme.PlaceholderGray
import com.grifffith.mindfuljournal.ui.theme.White

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FitnessScreen(fitnessRepository: FitnessRepository) {
    // State to track the number of daily steps
    var dailySteps by remember { mutableStateOf(0) }
    // State to track the step goal
    var stepGoal by remember { mutableStateOf(fitnessRepository.getStepGoal()) }
    // Coroutine scope for async tasks
    val coroutineScope = rememberCoroutineScope()

    // Step count from StepCounter
    val totalSteps = StepCounter()

    // Calculate today's steps using a LaunchedEffect block
    LaunchedEffect(totalSteps) {
        val stepsForToday = fitnessRepository.calculateTodaySteps(totalSteps)
        dailySteps = stepsForToday
    }

    // Calculate fitness metrics
    val caloriesBurned = calculateCalories(dailySteps) // Calculate calories based on steps
    val distanceCovered = calculateDistance(dailySteps) // Calculate distance based on steps
    val progress = (dailySteps.toFloat() / stepGoal).coerceIn(0f, 1f) // Progress towards the step goal

    Column(
        modifier = Modifier
            .fillMaxSize() // Fill the available space
            .background(DarkBlue) // Set background color
            .padding(16.dp) // Add padding around the column
            .verticalScroll(rememberScrollState()), // Enable vertical scrolling
        horizontalAlignment = Alignment.CenterHorizontally, // Center content horizontally
        verticalArrangement = Arrangement.Top // Align content to the top
    ) {
        // Header Text
        Text(
            text = "Fitness Tracker",
            color = GoldYellow, // Text color
            fontWeight = FontWeight.Bold, // Bold font
            fontSize = 30.sp, // Font size
            textAlign = TextAlign.Center, // Center align text
            modifier = Modifier
                .fillMaxWidth() // Stretch to full width
                .padding(vertical = 16.dp) // Add vertical padding
        )

        Spacer(modifier = Modifier.height(16.dp)) // Add space between header and content

        // Circular Step Progress Card
        CircularStepCard(value = dailySteps, progress = progress, color = GoldYellow)

        Spacer(modifier = Modifier.height(16.dp)) // Space between step card and metrics

        // Calories Burned Metric
        MetricCard(
            title = "Calories Burned",
            value = "$caloriesBurned kcal", // Display calories burned
            progress = null, // No progress bar for this metric
            color = CardTitleYellow, // Metric color
            icon = Icons.Filled.LocalFireDepartment // Icon for calories
        )

        Spacer(modifier = Modifier.height(16.dp)) // Space between metrics

        // Distance Covered Metric
        MetricCard(
            title = "Distance Covered",
            value = "$distanceCovered km", // Display distance covered
            progress = null, // No progress bar for this metric
            color = LightGray, // Metric color
            icon = Icons.Filled.Directions // Icon for distance
        )

        Spacer(modifier = Modifier.height(16.dp)) // Space between metrics

        // Step Goal Updater
        StepGoalUpdater(
            stepGoal = stepGoal,
            fitnessRepository = fitnessRepository
        ) { updatedGoal ->
            stepGoal = updatedGoal // Update the step goal when changed
        }
    }
}

@Composable
fun CircularStepCard(value: Int, progress: Float, color: Color) {
    // Display a circular progress indicator for step tracking
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, // Center content horizontally
        verticalArrangement = Arrangement.Center // Center content vertically
    ) {
        CircularProgressIndicator(
            progress = progress, // Progress towards the goal
            modifier = Modifier.size(250.dp), // Size of the progress indicator
            color = color, // Color of the progress indicator
            strokeWidth = 40.dp, // Width of the progress bar
            trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor, // Background color of the track
        )
        Spacer(modifier = Modifier.height(16.dp)) // Space between progress indicator and steps
        Row(
            verticalAlignment = Alignment.CenterVertically, // Align items in the row vertically
            horizontalArrangement = Arrangement.Center // Center content in the row
        ) {
            Icon(
                imageVector = Icons.Filled.DirectionsWalk, // Icon for steps
                contentDescription = "Steps Icon", // Content description for accessibility
                tint = color, // Icon color
                modifier = Modifier.size(32.dp) // Size of the icon
            )
            Spacer(modifier = Modifier.width(8.dp)) // Space between icon and text
            Text(
                text = "$value steps", // Display the step count
                color = color, // Text color
                fontSize = 22.sp, // Font size
                fontWeight = FontWeight.Bold // Bold font
            )
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, progress: Float?, color: Color, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    // Card to display a fitness metric
    Card(
        modifier = Modifier.fillMaxWidth(), // Card takes the full width
        colors = CardDefaults.cardColors(containerColor = CardBackground), // Card background color
        shape = RoundedCornerShape(16.dp), // Rounded corners
        elevation = CardDefaults.cardElevation(8.dp) // Elevation for shadow effect
    ) {
        Row(
            modifier = Modifier.padding(16.dp), // Padding inside the card
            verticalAlignment = Alignment.CenterVertically, // Align items in the row vertically
            horizontalArrangement = Arrangement.spacedBy(8.dp) // Space between elements in the row
        ) {
            Icon(
                imageVector = icon, // Icon for the metric
                contentDescription = null, // No content description
                tint = color, // Icon color
                modifier = Modifier.size(40.dp) // Size of the icon
            )
            Column(
                horizontalAlignment = Alignment.Start, // Align content to the start
                verticalArrangement = Arrangement.spacedBy(8.dp) // Space between elements in the column
            ) {
                // Metric Title
                Text(
                    text = title,
                    color = White, // Title text color
                    fontSize = 18.sp, // Font size
                    fontWeight = FontWeight.SemiBold // Semi-bold font
                )
                // Metric Value
                Text(
                    text = value,
                    color = color, // Value text color
                    fontSize = 22.sp, // Font size
                    fontWeight = FontWeight.Bold // Bold font
                )
                // Optional Progress Bar
                progress?.let {
                    LinearProgressIndicator(
                        progress = it, // Progress value
                        modifier = Modifier.fillMaxWidth(), // Progress bar takes full width
                        color = color, // Progress bar color
                        trackColor = PlaceholderGray // Background color for the track
                    )
                }
            }
        }
    }
}

// Helper functions for fitness calculations
fun calculateCalories(steps: Int): Int {
    // Calculate calories burned (rough estimation: 0.04 kcal per step)
    return (steps * 0.04).toInt()
}

@SuppressLint("DefaultLocale")
fun calculateDistance(steps: Int): String {
    // Calculate distance based on average step length (0.762m per step)
    val distance = steps * 0.000762f // Convert to kilometers
    return String.format("%.2f", distance) // Format to 2 decimal places
}
