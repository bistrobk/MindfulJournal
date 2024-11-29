package com.grifffith.mindfuljournal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.text.TextStyle
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    // Get current date, time, and day
    val currentDate = remember { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date()) } // Date format "29 November 2024"
    val currentTime = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()) }
    val currentDay = remember { SimpleDateFormat("EEEE", Locale.getDefault()).format(Date()) } // Day of the week

    val currentYear = remember { SimpleDateFormat("yyyy", Locale.getDefault()).format(Date()) } // Year

    // Determine Greeting based on the time of the day
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour in 0..11 -> "Good Morning !"
        hour in 12..17 -> "Good Afternoon !"
        else -> "Good Evening !"
    }

    // Animating background color of the box
    val animatedAlpha by animateFloatAsState(targetValue = 1f, animationSpec = tween(durationMillis = 1000))

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ComposeColor(0xFF02253B)) // Dark Blue background color
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
        ) {
            // Greeting Text in Yellow at the Top
            Text(
                text = greeting,  // Dynamic greeting based on time
                color = ComposeColor(0xFFE9B546), // Yellow color
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(top = 20.dp)
                    .padding(bottom = 49.dp)

            )

            // "Welcome" Text below the greeting
            Text(
                text = "Welcome to Mindful Journal",
                color = ComposeColor(0xFFE9B546), // Yellow color
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // "Mental and Physical Health" Tagline
            Text(
                text = "Mental and Physical Health",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp, vertical = 16.dp) // Outer padding for spacing
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFFFC107), // Bright Yellow
                                Color(0xFFFFD740)  // Lighter Yellow for gradient effect
                            )
                        ),
                        shape = RoundedCornerShape(
                            topEnd = 24.dp,
                            bottomEnd = 24.dp
                        ) // Rounded corners for a sleek look
                    )
                    .clip(
                        RoundedCornerShape(
                            topEnd = 24.dp,
                            bottomEnd = 24.dp
                        )
                    )
                    .padding(16.dp) // Inner padding for content
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(start = 24.dp) // Indentation for a clean look
                ) {



                    // Day Display
                    Text(
                        text = currentDay.uppercase(), // Display day in uppercase
                        color = Color.White, // Soft yellow for subtle contrast
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )

                    Spacer(modifier = Modifier.height(4.dp)) // Subtle spacing

                    // Date Display (e.g., "Friday 29 November 2024")
                    Text(
                        text = currentDate, // Display date
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.SansSerif
                    )

                    Spacer(modifier = Modifier.height(4.dp)) // Subtle spacing


                }
            }

        }
    }
    }


@Preview
@Composable
fun PreviewHomeScreen() {
    HomeScreen()
}
