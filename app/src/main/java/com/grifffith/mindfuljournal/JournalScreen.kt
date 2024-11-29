package com.grifffith.mindfuljournal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(modifier: Modifier = Modifier) {
    // State to hold the text entered in the TextField
    var journalText by remember { mutableStateOf("") }
    var showTextField by remember { mutableStateOf(false) }
    val journalEntries = remember { mutableStateListOf<Pair<String, String>>() } // Pair of date/time and text

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF02253B)) // Dark blue background color
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Title for the journal screen
            Text(
                text = "Your Journal",
                color = Color(0xFFE9B546), // Gold text color
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Display previous journal entries
            if (journalEntries.isEmpty()) {
                Text(
                    text = "No journal entries yet.",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    journalEntries.forEach { entry ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3D58)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = entry.first, // Date and time
                                    color = Color(0xFFE9B546), // Gold text color
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = entry.second, // Journal entry text
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f)) // Push the FAB to the bottom

            // Journal Entry Input Box
            if (showTextField) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Display Date and Time above the TextField
                    val currentDateTime = SimpleDateFormat(
                        "EEEE dd MMMM yyyy, hh:mm a",
                        Locale.getDefault()
                    ).format(Date())

                    Text(
                        text = currentDateTime, // Current date and time
                        color = Color(0xFFE9B546), // Gold text color
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .align(Alignment.Start) // Align to the start of the TextField
                    )

                    TextField(
                        value = journalText,
                        onValueChange = { journalText = it },
                        label = { Text("Write your thoughts...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp) // Adjust height
                            .background(Color.White, shape = RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.White,
                            focusedIndicatorColor = Color(0xFFE9B546), // Gold border when focused
                            unfocusedIndicatorColor = Color.Gray
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Button(
                            onClick = {
                                if (journalText.isNotBlank()) {
                                    journalEntries.add(currentDateTime to journalText)
                                    journalText = ""
                                    showTextField = false
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE9B546))
                        ) {
                            Text("Save", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedButton(
                            onClick = {
                                journalText = ""
                                showTextField = false
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE9B546))
                        ) {
                            Text("Cancel", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Floating Action Button to toggle TextField
        FloatingActionButton(
            onClick = { showTextField = !showTextField },
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFFE9B546)
        ) {
            Text(
                text = "+",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
fun PreviewJournalScreen() {
    JournalScreen()
}
