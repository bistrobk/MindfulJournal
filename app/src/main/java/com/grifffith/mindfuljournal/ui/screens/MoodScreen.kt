// MoodScreen.kt
package com.grifffith.mindfuljournal.ui.screens

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.grifffith.mindfuljournal.repository.MoodEntry
import com.grifffith.mindfuljournal.repository.MoodRepository
import com.grifffith.mindfuljournal.ui.theme.CardBackground
import com.grifffith.mindfuljournal.ui.theme.DarkBlue
import com.grifffith.mindfuljournal.ui.theme.GoldYellow
import com.grifffith.mindfuljournal.ui.theme.LightBlue
import com.grifffith.mindfuljournal.ui.theme.LightGray
import com.grifffith.mindfuljournal.ui.theme.White
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MoodScreen(
    moodRepository: MoodRepository, // Repository to manage mood data
    modifier: Modifier = Modifier // Modifier for styling
) {
    // State variables for managing UI and mood data
    var selectedMood by remember { mutableStateOf("🙂") } // Currently selected mood
    var moodNote by remember { mutableStateOf("") } // Notes for the mood
    val context = LocalContext.current // Get current context for Toast messages

    // List of questions for mood assessment
    val questions = listOf(
        "How would you describe your emotional state today?",
        "Do you feel overwhelmed or at ease with your responsibilities?",
        "How restful was your sleep last night?",
        "What level of energy do you feel right now?",
        "How motivated do you feel to work toward your goals?",
        "Did anything make you feel particularly happy or sad today?",
        "Are you able to focus and concentrate on tasks today?"
    )

    // User responses for the questions
    val userResponses = remember { mutableStateMapOf<String, String>() }

    // Track the current question index
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    questions.size + 1 // Total steps (questions + mood note)

    // List to hold previously recorded mood entries
    val previousRecords = remember { mutableStateListOf<MoodEntry>() }
    var isRecordingMood by remember { mutableStateOf(false) } // Flag for recording mood

    // Load mood entries from the repository on first composition
    LaunchedEffect(Unit) {
        previousRecords.clear()
        previousRecords.addAll(moodRepository.getAllMoodEntries())
        // Sort entries by `loggedAt` in descending order
        previousRecords.sortByDescending { it.loggedAt }
    }

    // Filter mood entries from the last 7 days
    val sevenDayEntries by remember {
        derivedStateOf {
            val nowDate = LocalDate.now()
            previousRecords.filter { entry ->
                val entryDate = Instant.ofEpochMilli(entry.loggedAt)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                ChronoUnit.DAYS.between(entryDate, nowDate) < 7 // Check if within the last 7 days
            }
        }
    }

    // Calculate mood summary (percentages and most frequent mood)
    val moodSummary by remember(sevenDayEntries) {
        derivedStateOf {
            val moodCounts = sevenDayEntries.groupingBy { it.mood }.eachCount()
            val mostFrequentMood = moodCounts.maxByOrNull { it.value }?.key ?: "🙂"
            val total = moodCounts.values.sum()
            val moodPercentages = moodCounts.mapValues { (_, v) -> (v.toDouble() / total) * 100 }
            moodPercentages to mostFrequentMood
        }
    }

    // Generate a mental health message based on mood data
    val mentalMessage by remember(moodSummary) {
        derivedStateOf {
            val (moodPercentages, _) = moodSummary
            when {
                moodPercentages.isEmpty() -> "No mood data available for the past 7 days."
                (moodPercentages["😀"] ?: 0.0) > 50 -> "You've been feeling great! Keep up the positive vibes."
                (moodPercentages["🙂"] ?: 0.0) > 50 -> "Your moods are mostly positive. Stay happy!"
                (moodPercentages["😐"] ?: 0.0) > 50 -> "You're maintaining a balanced state. Remember to take breaks."
                (moodPercentages["☹️"] ?: 0.0) > 50 -> "You've been feeling down lately. Consider reaching out to a friend or professional."
                (moodPercentages["😭"] ?: 0.0) > 50 -> "It seems you're going through a tough time. Take care of yourself."
                else -> "Your moods are varied. Keep monitoring and take care of your mental health."
            }
        }
    }

    // Main container for the mood screen
    Box(
        modifier = modifier
            .fillMaxSize() // Fill the available space
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DarkBlue, LightBlue) // Gradient background
                )
            )
            .padding(16.dp) // Padding inside the box
    )

    // Display mood data in a LazyColumn
    LazyColumn(
        modifier = modifier
            .fillMaxSize() // Fill the available space
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DarkBlue, LightBlue) // Gradient background
                )
            )
            .padding(16.dp), // Padding inside the column
        verticalArrangement = Arrangement.spacedBy(16.dp), // Space between items
        contentPadding = PaddingValues(bottom = 16.dp) // Padding at the bottom
    ) {
        // 7-Day Mood Summary
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "7-Day Mood Summary",
                    color = GoldYellow, // Title color
                    fontWeight = FontWeight.Bold, // Bold font
                    fontSize = 20.sp // Font size
                )
                Spacer(modifier = Modifier.height(8.dp)) // Space below the title
                if (sevenDayEntries.isEmpty()) {
                    Text(
                        text = "No mood records in the past 7 days.",
                        color = LightGray, // Text color
                        fontSize = 16.sp, // Font size
                        modifier = Modifier.padding(top = 4.dp) // Padding above the text
                    )
                } else {
                    MoodSummary(
                        moodPercentages = moodSummary.first, // Mood percentages
                        mentalMessage = mentalMessage // Mental health message
                    )
                }
            }
        }

        // Mood Log Section
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Your Mood Log",
                    color = GoldYellow, // Title color
                    fontWeight = FontWeight.Bold, // Bold font
                    fontSize = 24.sp // Font size
                )
                Spacer(modifier = Modifier.height(12.dp)) // Space below the title
                if (previousRecords.isEmpty()) {
                    Text(
                        text = "No mood records yet. Start by recording your mood!",
                        color = LightGray, // Text color
                        fontSize = 16.sp, // Font size
                        modifier = Modifier.padding(top = 8.dp) // Padding above the text
                    )
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp), // Space between items
                        modifier = Modifier.fillMaxWidth() // Row takes full width
                    ) {
                        items(previousRecords) { entry ->
                            MoodRecordCard(entry, questions) // Display mood entry as a card
                        }
                    }
                }
            }
        }

        // "Record Mood" Button
        item {
            Box(
                modifier = Modifier.fillMaxWidth(), // Button container takes full width
                contentAlignment = Alignment.Center // Center align the button
            ) {
                Button(
                    onClick = { isRecordingMood = true }, // Start recording mood
                    colors = ButtonDefaults.buttonColors(containerColor = GoldYellow), // Button color
                    shape = RoundedCornerShape(12.dp), // Rounded corners
                    modifier = Modifier
                        .width(140.dp) // Button width
                        .height(80.dp) // Button height
                ) {
                    Icon(
                        imageVector = Icons.Default.Add, // Add icon
                        contentDescription = "Add", // Icon description
                        tint = White, // Icon color
                        modifier = Modifier.size(20.dp) // Icon size
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // Space between icon and text
                    Text(
                        text = "Record Mood", // Button text
                        color = White, // Text color
                        fontWeight = FontWeight.Bold, // Bold font
                        fontSize = 16.sp // Font size
                    )
                }
            }
        }
    }

    // Mood recording dialog
    if (isRecordingMood) {
        MoodRecordingDialog(
            selectedMood = selectedMood, // Selected mood
            onMoodSelected = { selectedMood = it }, // Update selected mood
            questions = questions, // Mood questions
            currentQuestionIndex = currentQuestionIndex, // Current question index
            userResponses = userResponses, // User responses to questions
            moodNote = moodNote, // Mood note
            onMoodNoteChange = { moodNote = it }, // Update mood note
            onNext = { // Move to the next question
                val currentQuestion = questions[currentQuestionIndex]
                if (userResponses[currentQuestion].isNullOrEmpty()) {
                    Toast.makeText(context, "Please select an answer before proceeding.", Toast.LENGTH_SHORT).show()
                } else {
                    if (currentQuestionIndex < questions.lastIndex) {
                        currentQuestionIndex++
                    } else {
                        currentQuestionIndex++
                    }
                }
            },
            onSave = { // Save mood entry
                if (userResponses.size == questions.size && moodNote.isNotEmpty()) {
                    val answers = questions.map { userResponses[it] ?: "N/A" }
                    moodRepository.addMoodEntry(selectedMood, moodNote, answers) // Save to repository
                    Toast.makeText(context, "Mood saved successfully!", Toast.LENGTH_SHORT).show()
                    isRecordingMood = false
                    currentQuestionIndex = 0
                    userResponses.clear()
                    moodNote = ""
                    previousRecords.clear()
                    previousRecords.addAll(moodRepository.getAllMoodEntries())
                    // Re-sort after adding the new entry
                    previousRecords.sortByDescending { it.loggedAt }
                } else {
                    Toast.makeText(context, "Please complete all fields before saving.", Toast.LENGTH_SHORT).show()
                }
            },
            onCancel = { // Cancel mood recording
                isRecordingMood = false
                currentQuestionIndex = 0
                userResponses.clear()
                moodNote = ""
            }
        )
    }
}


@Composable
fun MoodSummary(
    moodPercentages: Map<String, Double>, // Map containing mood emoji and their respective percentages
    mentalMessage: String // Message summarizing the mental state
) {
    // Main container for the mood summary
    Column(
        modifier = Modifier
            .fillMaxWidth() // Occupies the full width of the parent
            .background(color = CardBackground, shape = RoundedCornerShape(8.dp)) // Background color and rounded corners
            .padding(12.dp) // Padding inside the container
    ) {
        // Display the mental message summarizing the mood
        Text(
            text = mentalMessage, // Summary message
            color = White, // Text color
            fontSize = 16.sp, // Font size
            fontWeight = FontWeight.Medium // Medium font weight
        )

        Spacer(modifier = Modifier.height(8.dp)) // Space between the message and the mood progress indicators

        // Loop through each mood and its percentage to display progress indicators
        moodPercentages.forEach { (mood, percentage) ->
            Row(
                verticalAlignment = Alignment.CenterVertically, // Align items vertically in the center
                modifier = Modifier.fillMaxWidth() // Row takes up full width
            ) {
                // Display the mood emoji
                Text(
                    text = mood, // Mood emoji
                    color = GoldYellow, // Text color
                    fontSize = 14.sp, // Font size
                    modifier = Modifier.width(24.dp) // Fixed width for the emoji column
                )

                Spacer(modifier = Modifier.width(8.dp)) // Space between emoji and progress bar

                // Display the progress bar representing the mood percentage
                LinearProgressIndicator(
                    progress = {
                        (percentage / 100.0).toFloat() // Progress as a fraction (0 to 1)
                    },
                    modifier = Modifier
                        .height(8.dp) // Height of the progress bar
                        .weight(1f) // Use remaining width for the progress bar
                        .clip(RoundedCornerShape(4.dp)), // Rounded corners for the progress bar
                    color = GoldYellow, // Color of the progress bar
                    trackColor = LightGray, // Background color of the track
                )

                Spacer(modifier = Modifier.width(8.dp)) // Space between progress bar and percentage text

                // Display the percentage value
                Text(
                    text = "${percentage.toInt()}%", // Convert percentage to integer and append %
                    color = White, // Text color
                    fontSize = 12.sp // Font size
                )
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MoodRecordCard(entry: MoodEntry, questions: List<String>) {
    // Formatter to display the date and time in a readable format
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
    val formattedDate = Instant.ofEpochMilli(entry.loggedAt) // Convert timestamp to date
        .atZone(ZoneId.systemDefault()) // Use system's default time zone
        .toLocalDateTime()
        .format(formatter) // Format the date-time

    // State to track whether the card is expanded
    var isExpanded by remember { mutableStateOf(false) }

    // Card container
    Card(
        shape = RoundedCornerShape(12.dp), // Rounded corners for the card
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // Card shadow elevation
        modifier = Modifier
            .width(if (isExpanded) 350.dp else 240.dp) // Width adjusts based on expansion state
            .wrapContentHeight() // Height adjusts to fit the content
            .animateContentSize() // Smooth animation when content size changes
            .clickable { isExpanded = !isExpanded } // Toggle expansion on click
    ) {
        // Main content of the card
        Column(
            modifier = Modifier
                .background(CardBackground) // Background color for the card
                .padding(12.dp), // Padding inside the card
            verticalArrangement = Arrangement.spacedBy(4.dp) // Space between items in the column
        ) {
            // Row to display mood emoji and formatted date
            Row(
                verticalAlignment = Alignment.CenterVertically, // Vertically align content
                horizontalArrangement = Arrangement.SpaceBetween, // Space out content in the row
                modifier = Modifier.fillMaxWidth() // Row takes up full width
            ) {
                // Display the mood emoji
                Text(
                    text = entry.mood, // Mood emoji
                    fontSize = 24.sp, // Font size for the emoji
                    modifier = Modifier.padding(end = 4.dp) // Padding to the right of the emoji
                )
                // Display the formatted date
                Text(
                    text = formattedDate, // Formatted date
                    color = LightGray, // Text color for the date
                    fontSize = 10.sp // Font size
                )
            }

            // Display the mood note
            Text(
                text = "Notes: ${entry.note}", // Mood note
                color = White, // Text color
                fontSize = 14.sp, // Font size
                fontWeight = FontWeight.Medium // Medium font weight
            )

            // Divider line
            HorizontalDivider(thickness = 0.3.dp, color = LightGray) // Thin divider line

            // Display the first two questions and answers
            questions.take(2).forEachIndexed { index, question ->
                val answer = entry.answers.getOrNull(index) ?: "N/A" // Get the answer or "N/A" if unavailable
                Column(modifier = Modifier.padding(vertical = 2.dp)) {
                    // Display the question
                    Text(
                        text = question,
                        color = GoldYellow, // Text color for the question
                        fontSize = 12.sp, // Font size
                        fontWeight = FontWeight.Medium // Medium font weight
                    )
                    // Display the answer
                    Text(
                        text = answer,
                        color = LightGray, // Text color for the answer
                        fontSize = 12.sp // Font size
                    )
                }
            }

            // Additional questions and answers (visible only when expanded)
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 4.dp)) {
                    questions.drop(2).forEachIndexed { index, question ->
                        val answer = entry.answers.getOrNull(index + 2) ?: "N/A" // Get answer or "N/A"
                        Column(modifier = Modifier.padding(vertical = 2.dp)) {
                            // Display the question
                            Text(
                                text = question,
                                color = GoldYellow, // Text color for the question
                                fontSize = 12.sp, // Font size
                                fontWeight = FontWeight.Medium // Medium font weight
                            )
                            // Display the answer
                            Text(
                                text = answer,
                                color = LightGray, // Text color for the answer
                                fontSize = 12.sp // Font size
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun MoodCarousel(
    selectedMood: String, // Currently selected mood
    onMoodSelected: (String) -> Unit // Callback when a mood is selected
) {
    // List of moods to display
    val moods = listOf("😀", "🙂", "😐", "☹️", "😭")
    val scrollState = rememberScrollState() // State to handle horizontal scrolling

    // Main container for the carousel
    Column {
        // Label for the carousel
        Text(
            text = "Select your mood:", // Instructional text
            color = White, // Text color
            fontSize = 20.sp, // Font size
            fontWeight = FontWeight.Medium, // Font weight
            modifier = Modifier.padding(bottom = 12.dp) // Space below the text
        )

        // Horizontal scrollable row for the moods
        Row(
            modifier = Modifier
                .fillMaxWidth() // Row takes full width
                .horizontalScroll(scrollState), // Enable horizontal scrolling
            horizontalArrangement = Arrangement.spacedBy(24.dp) // Space between mood items
        ) {
            // Iterate through each mood
            moods.forEach { mood ->
                val isSelected = mood == selectedMood // Check if this mood is selected

                // Animate the size of the selected mood
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.2f else 1.0f, // Scale up if selected
                    animationSpec = tween(durationMillis = 300), label = "" // Animation duration
                )

                // Box to represent each mood
                Box(
                    modifier = Modifier
                        .size(60.dp) // Fixed size for each mood box
                        .scale(scale) // Apply scaling based on selection
                        .background(
                            color = if (isSelected) GoldYellow else LightBlue, // Background color based on selection
                            shape = CircleShape // Circular shape for the mood box
                        )
                        .clickable(
                            onClick = { onMoodSelected(mood) }, // Trigger callback on click
                            indication = null, // Remove click ripple effect
                            interactionSource = remember { MutableInteractionSource() } // Manage touch interactions
                        )
                        .semantics { contentDescription = "Mood: $mood" }, // Accessibility description
                    contentAlignment = Alignment.Center // Center the content inside the box
                ) {
                    // Display the mood emoji
                    Text(
                        text = mood, // Mood emoji
                        color = DarkBlue, // Text color
                        fontSize = 24.sp // Font size
                    )
                }
            }
        }
    }
}

@Composable
fun MoodNoteInput(value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Write your thoughts...", color = LightGray) },
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = CardBackground,
            unfocusedContainerColor = CardBackground,
            focusedIndicatorColor = GoldYellow,
            unfocusedIndicatorColor = LightGray,
            cursorColor = GoldYellow
        ),
        maxLines = 6
    )
}

@Composable
fun QuestionWithOptions(
    question: String, // The question to be displayed
    options: List<String>, // List of possible answers
    selectedOption: String?, // Currently selected answer (nullable)
    onAnswerSelected: (String) -> Unit // Callback to handle answer selection
) {
    // Main container for the question and options
    Column(
        modifier = Modifier
            .fillMaxWidth() // Make the column take the full width of its parent
            .padding(vertical = 8.dp) // Add vertical padding around the column
    ) {
        // Display the question text
        Text(
            text = question, // The question to be displayed
            color = White, // Text color
            fontSize = 18.sp, // Font size for the question
            fontWeight = FontWeight.Medium, // Medium font weight for emphasis
            modifier = Modifier.padding(bottom = 8.dp) // Space below the question
        )

        // Loop through each option and display it with a RadioButton
        options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth() // Make the row take the full width of its parent
                    .selectable(
                        selected = (selectedOption == option), // Check if the option is selected
                        onClick = { onAnswerSelected(option) }, // Handle option selection
                        interactionSource = remember { MutableInteractionSource() }, // Manage touch interactions
                        indication = null // Remove the default ripple effect
                    )
                    .padding(vertical = 4.dp), // Space between options
                verticalAlignment = Alignment.CenterVertically // Align content vertically in the center
            ) {
                // Display the RadioButton for each option
                RadioButton(
                    selected = (selectedOption == option), // Check if the option is selected
                    onClick = { onAnswerSelected(option) }, // Handle RadioButton click
                    colors = RadioButtonDefaults.colors(
                        selectedColor = GoldYellow, // Color for selected state
                        unselectedColor = LightGray // Color for unselected state
                    )
                )
                // Display the option text
                Text(
                    text = option, // Option text
                    color = White, // Text color
                    fontSize = 16.sp, // Font size
                    modifier = Modifier.padding(start = 8.dp) // Space between RadioButton and text
                )
            }
        }
    }
}


// Step indicator to show progress during mood recording
@Composable
fun StepIndicator(currentStep: Int, totalSteps: Int) {
    Column(
        modifier = Modifier.fillMaxWidth(), // Take the full width of the parent
        verticalArrangement = Arrangement.Center, // Center vertically
        horizontalAlignment = Alignment.CenterHorizontally // Center horizontally
    ) {
        // LinearProgressIndicator to display step progress
        LinearProgressIndicator(
            progress = {
                (currentStep / totalSteps.toDouble()).toFloat() // Progress as a fraction
            },
            modifier = Modifier
                .fillMaxWidth(0.8f) // Set width to 80% of the parent
                .height(8.dp) // Set height of the progress bar
                .clip(RoundedCornerShape(4.dp)), // Rounded corners for the progress bar
            color = GoldYellow, // Progress bar color
            trackColor = LightGray, // Background color of the progress bar
        )
        Spacer(modifier = Modifier.height(4.dp)) // Space between progress bar and text
        // Display current step out of total steps
        Text(
            text = "Step $currentStep of $totalSteps",
            color = White, // Text color
            fontSize = 12.sp // Font size
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MoodRecordingDialog(
    selectedMood: String, // Selected mood
    onMoodSelected: (String) -> Unit, // Callback when a mood is selected
    questions: List<String>, // List of questions to ask the user
    currentQuestionIndex: Int, // The current question index
    userResponses: SnapshotStateMap<String, String>, // User responses to the questions
    moodNote: String, // Notes about the user's mood
    onMoodNoteChange: (String) -> Unit, // Callback to change mood note
    onNext: () -> Unit, // Callback for next question
    onSave: () -> Unit, // Callback to save mood entry
    onCancel: () -> Unit // Callback to cancel the mood recording
) {
    Dialog(onDismissRequest = onCancel) {
        Surface(
            shape = RoundedCornerShape(16.dp), // Rounded corners for the dialog
            color = CardBackground, // Background color
            shadowElevation = 8.dp // Shadow elevation for the dialog
        ) {
            // Column to layout all dialog content
            Column(
                modifier = Modifier
                    .fillMaxWidth() // Fill the width of the dialog
                    .padding(20.dp), // Padding inside the dialog
                verticalArrangement = Arrangement.spacedBy(16.dp) // Space between elements
            ) {
                // Header Row with title and close button
                Row(
                    modifier = Modifier.fillMaxWidth(), // Row takes full width
                    horizontalArrangement = Arrangement.SpaceBetween, // Space out items in the row
                    verticalAlignment = Alignment.CenterVertically // Align vertically in the center
                ) {
                    Text(
                        text = "Record Your Mood", // Title text
                        color = GoldYellow, // Title color
                        fontWeight = FontWeight.Bold, // Bold font
                        fontSize = 20.sp // Font size
                    )
                    // Close button to cancel mood recording
                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.Default.Close, // Close icon
                            contentDescription = "Close", // Content description for accessibility
                            tint = LightGray // Icon color
                        )
                    }
                }

                // MoodCarousel for selecting mood
                MoodCarousel(
                    selectedMood = selectedMood,
                    onMoodSelected = onMoodSelected
                )

                // Display questions if there are remaining ones
                if (currentQuestionIndex < questions.size) {
                    val question = questions[currentQuestionIndex] // Current question
                    QuestionWithOptions(
                        question = question, // Pass current question
                        options = listOf("Very Low", "Low", "Moderate", "High", "Very High"), // Options for answers
                        selectedOption = userResponses[question], // Selected option based on previous responses
                        onAnswerSelected = { userResponses[question] = it } // Update answer when selected
                    )
                } else {
                    // Additional notes input after all questions are answered
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp) // Space between elements
                    ) {
                        Text(
                            text = "Additional Notes:", // Label for the note input
                            color = GoldYellow, // Text color
                            fontWeight = FontWeight.Medium, // Medium font weight
                            fontSize = 18.sp // Font size
                        )
                        MoodNoteInput(
                            value = moodNote, // Current mood note
                            onValueChange = onMoodNoteChange // Callback to update mood note
                        )
                    }
                }

                // Display step indicator for progress
                StepIndicator(
                    currentStep = if (currentQuestionIndex <= questions.size) currentQuestionIndex else questions.size + 1,
                    totalSteps = questions.size + 1 // Total steps including notes
                )

                // Row with buttons for next, save, or cancel actions
                Row(
                    modifier = Modifier.fillMaxWidth(), // Row takes full width
                    horizontalArrangement = Arrangement.spacedBy(16.dp) // Space between buttons
                ) {
                    if (currentQuestionIndex < questions.size) {
                        // Next button for progressing through questions
                        Button(
                            onClick = onNext, // Move to the next question
                            colors = ButtonDefaults.buttonColors(containerColor = GoldYellow), // Button color
                            shape = RoundedCornerShape(8.dp), // Rounded corners
                            modifier = Modifier
                                .weight(1f) // Button takes equal space
                                .height(50.dp) // Button height
                        ) {
                            Text(
                                text = if (currentQuestionIndex < questions.size - 1) "Next" else "Add Notes", // Button text
                                color = DarkBlue, // Text color
                                fontWeight = FontWeight.Bold, // Bold font weight
                                fontSize = 18.sp // Font size
                            )
                        }
                    } else {
                        // Save button to submit the mood data
                        Button(
                            onClick = onSave, // Save the mood entry
                            colors = ButtonDefaults.buttonColors(containerColor = GoldYellow), // Button color
                            shape = RoundedCornerShape(8.dp), // Rounded corners
                            modifier = Modifier
                                .weight(1f) // Button takes equal space
                                .height(50.dp) // Button height
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, tint = DarkBlue) // Save icon
                            Spacer(modifier = Modifier.width(8.dp)) // Space between icon and text
                            Text(
                                text = "Save", // Button text
                                color = DarkBlue, // Text color
                                fontWeight = FontWeight.Bold, // Bold font weight
                                fontSize = 18.sp // Font size
                            )
                        }

                        // Cancel button to discard the changes
                        Button(
                            onClick = onCancel, // Cancel the mood recording
                            colors = ButtonDefaults.buttonColors(containerColor = LightBlue), // Button color
                            shape = RoundedCornerShape(8.dp), // Rounded corners
                            modifier = Modifier
                                .weight(1f) // Button takes equal space
                                .height(50.dp) // Button height
                        ) {
                            Text(
                                text = "Cancel", // Button text
                                color = DarkBlue, // Text color
                                fontWeight = FontWeight.Bold, // Bold font weight
                                fontSize = 18.sp // Font size
                            )
                        }
                    }
                }
            }
        }
    }
}
