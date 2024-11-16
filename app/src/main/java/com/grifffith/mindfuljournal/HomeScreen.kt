package com.grifffith.mindfuljournal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF02253B)) // Dark blue background color
    ) {
        // Display the logo centered at the top
        Image(
            painter = painterResource(id = R.drawable.mindfulness), // Replace with your logo file name
            contentDescription = "Logo",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 40.dp)
        )

        // Greeting text
        Text(
            text = "Welcome to Mindful Journal",
            color = Color(0xFFE9B546), // Set text color to #E9B546
            fontSize = 24.sp, // Increase font size
            fontWeight = FontWeight.Bold, // Optional: make the text bold
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 16.dp)
        )
    }
}
