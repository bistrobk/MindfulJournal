package com.grifffith.mindfuljournal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodScreen(modifier: Modifier = Modifier) {
    // State for selected mood, mood rating, and mood note
    var selectedMood by remember { mutableStateOf("🙂") } // Single emoji initially
    var moodNote by remember { mutableStateOf("") }
    val moodOptions = listOf("😀", "🙂", "😐", "☹️", "😭") // 5 Emojis for mood
    val questions = listOf(
        "How are you feeling emotionally?",
        "How anxious are you feeling?",
        "How much energy do you have today?"
    )
    var selectedAnswers = remember { mutableStateOf(listOf<String>()) } // Store answers for multiple questions

    // Scrollable Column for the whole screen
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF02253B)) // Dark blue background color
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp), // Space between items
        contentPadding = PaddingValues(bottom = 16.dp) // Padding at the bottom
    ) {
        item {
            // Header Text
            Text(
                text = "Track Your Mood",
                color = Color(0xFFE9B546), // Gold text color
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp, // Increased font size
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        item {
            // Mood Selection Question
            Text(
                text = "How are you feeling today?",
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                moodOptions.forEach { mood ->
                    MoodSelectionChip(mood = mood, selectedMood = selectedMood) {
                        selectedMood = it
                    }
                }
            }
        }

        // Loop through questions and display them with multiple-choice (Low, Medium, High)
        questions.forEachIndexed { index, question ->
            item {
                QuestionWithOptions(
                    question = question,
                    onAnswerSelected = { answer ->
                        selectedAnswers.value = selectedAnswers.value.toMutableList().apply {
                            if (index < selectedAnswers.value.size) {
                                set(index, answer)
                            } else {
                                add(answer)
                            }
                        }
                    }
                )
            }
        }

        item {
            // New Question: Would you like to add anything else?
            Text(
                text = "Would you like to add anything else?",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        item {
            // Optional Mood Note TextField
            MoodNoteTextField(value = moodNote, onValueChange = { moodNote = it })
        }

        item {
            // Save Button
            SaveButton(
                onClick = {
                    // Handle save logic
                    println("Mood Saved: $selectedMood")
                    println("Answers: ${selectedAnswers.value}")
                    println("Mood Note: $moodNote")
                }
            )
        }
    }
}

@Composable
fun MoodSelectionChip(mood: String, selectedMood: String, onSelect: (String) -> Unit) {
    Box(
        modifier = Modifier
            .size(50.dp) // Increased size for better clickability
            .clickable { onSelect(mood) }
            .background(
                color = if (selectedMood == mood) Color(0xFFE9B546) else Color.Transparent,
                shape = MaterialTheme.shapes.medium
            )
            .border(2.dp, Color(0xFFE9B546), MaterialTheme.shapes.medium),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = mood,
            fontSize = 32.sp, // Larger font size for mood emoji
            color = if (selectedMood == mood) Color(0xFF02253B) else Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun QuestionWithOptions(question: String, onAnswerSelected: (String) -> Unit) {
    var selectedOption by remember { mutableStateOf<String>("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = question,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val options = listOf("Low", "Medium", "High")

        options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (selectedOption == option),
                        onClick = {
                            selectedOption = option
                            onAnswerSelected(option)
                        }
                    )
                    .padding(8.dp)
            ) {
                RadioButton(
                    selected = (selectedOption == option),
                    onClick = {
                        selectedOption = option
                        onAnswerSelected(option)
                    },
                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFE9B546))
                )
                Text(
                    text = option,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun MoodNoteTextField(value: String, onValueChange: (String) -> Unit) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp) // Increased height for larger note area
            .background(Color.White)
            .padding(16.dp),
        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp)
    )
}

@Composable
fun SaveButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE9B546)),
        shape = MaterialTheme.shapes.medium // Rounded corners for the button
    ) {
        Text("Save", color = Color(0xFF02253B)) // Button text in dark blue
    }
}

@Preview
@Composable
fun PreviewMoodScreen() {
    MoodScreen()
}
