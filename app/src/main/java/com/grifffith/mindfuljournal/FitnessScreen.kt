package com.grifffith.mindfuljournal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FitnessScreen() {
    // Get step count from the StepCounter composable
    val stepCount = StepCounter() // This will return the number of steps

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF02253B)) // Dark blue background color
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title with a more stylish font and spacing
        Text(
            text = "Fitness Tracker",
            color = Color(0xFFE9B546), // Gold text color
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            modifier = Modifier.padding(bottom = 30.dp),
        )

        // Step count with a larger, bolder style
        Text(
            text = "Steps: $stepCount",
            color = Color.White,
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(bottom = 30.dp),
        )

        // Add a rounded container for additional info or actions
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1E3D58), // Slightly darker background for contrast

        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Keep Going!",
                    color = Color(0xFFE9B546),
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Placeholder for future functionality (e.g., a button, graphs)
                Button(
                    onClick = { /* Handle Click */ },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE9B546)),
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(
                        text = "Track More",
                        color = Color(0xFF02253B),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewFitnessScreen() {
    FitnessScreen()
}
