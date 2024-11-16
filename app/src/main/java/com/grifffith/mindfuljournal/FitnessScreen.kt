package com.grifffith.mindfuljournal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FitnessScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF02253B)) // Dark blue background color
    ) {
        Text(
            text = "Fitness Tracker",
            color = Color(0xFFE9B546), // Gold text color
            fontWeight = FontWeight.Bold, // Bold text
            fontSize = 24.sp, // Larger font size
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
        )
    }
}
