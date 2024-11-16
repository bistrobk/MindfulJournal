package com.grifffith.mindfuljournal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(modifier: Modifier = Modifier) {
    // State to hold the text entered in the TextField
    val journalText = remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF02253B)) // Dark blue background color
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Your Journal",
                color = Color(0xFFE9B546), // Gold text color
                fontWeight = FontWeight.Bold, // Bold text
                fontSize = 24.sp, // Larger font size
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // TextField to allow user input
            TextField(
                value = journalText.value,
                onValueChange = { journalText.value = it },
                label = { Text("Write your thoughts...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp) // Adjust the height of the TextField
                    .background(Color.White),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedIndicatorColor = Color(0xFFE9B546), // Gold border when focused
                    unfocusedIndicatorColor = Color.Gray // Gray border when not focused
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Save Button (for now, it just prints the input to the log)
            Button(
                onClick = {
                    // Here you can add the logic to save the text, e.g., save to local storage
                    println("Saved Journal Entry: ${journalText.value}")
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Save")
            }
        }
    }
}
