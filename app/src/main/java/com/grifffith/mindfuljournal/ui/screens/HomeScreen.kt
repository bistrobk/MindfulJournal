@file:OptIn(ExperimentalFoundationApi::class)

package com.grifffith.mindfuljournal.ui.screens

import FitnessRepository
import JournalEntry
import JournalRepository
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.grifffith.mindfuljournal.R
import com.grifffith.mindfuljournal.StepCounter
import com.grifffith.mindfuljournal.db.MindfulJournalDBHelper
import com.grifffith.mindfuljournal.repository.MoodEntry
import com.grifffith.mindfuljournal.repository.MoodRepository
import com.grifffith.mindfuljournal.ui.theme.CardBackground
import com.grifffith.mindfuljournal.ui.theme.CardTitleYellow
import com.grifffith.mindfuljournal.ui.theme.DarkBlue
import com.grifffith.mindfuljournal.ui.theme.DateTextGray
import com.grifffith.mindfuljournal.ui.theme.GoldYellow
import com.grifffith.mindfuljournal.ui.theme.LightGray
import com.grifffith.mindfuljournal.ui.theme.PlaceholderGray
import com.grifffith.mindfuljournal.ui.theme.White
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(dbHelper: MindfulJournalDBHelper, fitnessRepository: FitnessRepository) {
    // Initialize the JournalRepository using the provided database helper
    val journalRepository = remember { JournalRepository(dbHelper) }

    // Retrieve and sort loved journal entries by their creation date
    val lovedEntries = remember {
        journalRepository.getLovedJournalEntries()
            .sortedBy { it.createdAt }
    }

    // Holds the ID of the currently expanded journal entry
    var expandedEntryId by remember { mutableStateOf<Int?>(null) }

    // Initialize the MoodRepository
    val moodRepository = remember { MoodRepository(dbHelper) }
    // Mutable list to hold all mood entries retrieved from the database
    val previousRecords = remember { mutableStateListOf<MoodEntry>() }

    // LaunchEffect ensures the mood records are fetched only once when the composable is first composed
    LaunchedEffect(Unit) {
        previousRecords.clear()
        previousRecords.addAll(moodRepository.getAllMoodEntries())
    }

    // Calculate the mood entries for the last 7 days
    val sevenDayEntries by remember {
        derivedStateOf {
            val nowDate = LocalDate.now()
            previousRecords.filter { entry ->
                val entryDate = Instant.ofEpochMilli(entry.loggedAt)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                ChronoUnit.DAYS.between(entryDate, nowDate) < 7 // Filter entries within the past 7 days
            }
        }
    }

    // Generate a summary of mood percentages for the past 7 days
    val moodSummary by remember(sevenDayEntries) {
        derivedStateOf {
            val moodCounts = sevenDayEntries.groupingBy { it.mood }.eachCount()
            val total = moodCounts.values.sum()
            if (total > 0) {
                moodCounts.mapValues { (_, v) -> (v.toDouble() / total) * 100 }
            } else {
                emptyMap()
            }
        }
    }

    // Generate a message based on the mood summary for user feedback
    val mentalMessage by remember(moodSummary) {
        derivedStateOf {
            when {
                moodSummary.isEmpty() -> "No mood data available for the past 7 days."
                (moodSummary["\uD83D\uDE00"] ?: 0.0) > 50 -> "You've been feeling great! Keep up the positive vibes."
                (moodSummary["\uD83D\uDE42"] ?: 0.0) > 50 -> "Your moods are mostly positive. Stay happy!"
                (moodSummary["\uD83D\uDE10"] ?: 0.0) > 50 -> "You're maintaining a balanced state. Remember to take breaks."
                (moodSummary["\u2639\uFE0F"] ?: 0.0) > 50 -> "You've been feeling down lately. Consider reaching out to a friend or professional."
                (moodSummary["\uD83D\uDE2D"] ?: 0.0) > 50 -> "It seems you're going through a tough time. Take care of yourself."
                else -> "Your moods are varied. Keep monitoring and take care of your mental health."
            }
        }
    }

    // Step count metrics
    var dailySteps by remember { mutableStateOf(0) } // Tracks the daily steps
    var stepGoal by remember { mutableStateOf(fitnessRepository.getStepGoal()) } // Retrieves the step goal from repository
    val totalSteps = StepCounter() // Placeholder for total steps tracking
    val progress = (dailySteps.toFloat() / stepGoal).coerceIn(0f, 1f) // Calculate progress towards the goal

    // LaunchEffect to update daily steps
    LaunchedEffect(totalSteps) {
        val stepsForToday = fitnessRepository.calculateTodaySteps(totalSteps)
        dailySteps = stepsForToday
    }

    // Main container for the home screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 1.dp) // Reduced padding at the bottom
            .background(DarkBlue)
    ) {
        // Top content section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
            verticalArrangement = Arrangement.Top, // Align content to the top
            horizontalAlignment = Alignment.Start
        ) {
            // Display an image (e.g., app logo)
            Image(
                painter = painterResource(id = R.drawable.mindfulness), // Replace with your image resource
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(200.dp) // Adjust size as needed
                    .padding(top = 2.dp, bottom = 5.dp) // Add spacing above and below
            )
        }
    }

    // Scrollable content section
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 196.dp) // Leave space for the fixed title
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .background(DarkBlue)
    ) {
        // Loved Journal Entries Section
        SectionHeader("\u2728 Loved Journal Entries")
        if (lovedEntries.isNotEmpty()) {
            LovedJournalLazyRow(
                lovedEntries = lovedEntries,
                expandedEntryId = expandedEntryId,
                onToggleExpand = { id -> expandedEntryId = id }
            )
        } else {
            PlaceholderText("Loading your loved journals...")
        }

        Spacer(modifier = Modifier.height(82.dp))

// Fitness Section (Step Count Ring)
        SectionHeader("\uD83D\uDEB6\u200D♂\uFE0F Daily Step Progress")

//  Spacer to create margin between the title and the CircularStepCard
        Spacer(modifier = Modifier.height(16.dp)) // Adjust height as needed for the margin

// Center-align the CircularStepCard
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp), // Add optional padding
            contentAlignment = Alignment.Center // Align content to the center
        ) {
            CircularStepCard(value = dailySteps, progress = progress, color = GoldYellow)
        }

        Spacer(modifier = Modifier.height(92.dp)) // Spacer for the margin below the section


        // 7-Day Mood Summary Section
        SectionHeader("\uD83E\uDE7A 7-Day Mood Summary")
        if (sevenDayEntries.isEmpty()) {
            PlaceholderText("No mood records in the past 7 days.")
        } else {
            CompactMoodSummary(
                moodPercentages = moodSummary,
                mentalMessage = mentalMessage
            )
        }

        Spacer(modifier = Modifier.height(64.dp)) // Add more bottom margin
    }
}



@Composable
fun LovedJournalLazyRow(
    lovedEntries: List<JournalEntry>, // List of journal entries to display
    expandedEntryId: Int?, // ID of the currently expanded entry
    onToggleExpand: (Int?) -> Unit // Callback to toggle expansion of an entry
) {
    // LazyRow to horizontally scroll through journal entries
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp), // Spacing between items
        contentPadding = PaddingValues(horizontal = 16.dp) // Padding on left and right
    ) {
        // Iterate through each journal entry and display it
        items(lovedEntries) { entry ->
            LovedJournalGlassCard(
                entry = entry,
                isOnTop = entry.entryId == expandedEntryId, // Check if the entry is expanded
                onToggleExpand = {
                    // Toggle the expanded state of the entry
                    onToggleExpand(if (entry.entryId == expandedEntryId) null else entry.entryId)
                }
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    // Display section headers with consistent styling
    Text(
        text = title,
        color = White, // Text color
        fontSize = 22.sp, // Font size
        fontWeight = FontWeight.Bold, // Bold text
        modifier = Modifier.padding(vertical = 8.dp) // Padding above and below the text
    )
}

@Composable
fun PlaceholderText(text: String) {
    // Placeholder text displayed when no data is available
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp) // Fixed height
            .background(PlaceholderGray, shape = RoundedCornerShape(16.dp)) // Background and rounded corners
            .padding(16.dp) // Padding inside the box
    ) {
        Text(
            text = text,
            color = LightGray, // Text color for placeholders
            fontSize = 16.sp, // Font size
            textAlign = TextAlign.Center, // Center-aligned text
            modifier = Modifier.align(Alignment.Center) // Align text to the center of the box
        )
    }
}

@Composable
fun LovedJournalGlassCard(entry: JournalEntry, isOnTop: Boolean, onToggleExpand: () -> Unit) {
    // Scroll state to manage scrolling when the card is expanded
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope() // Coroutine scope for asynchronous actions
    val bringIntoViewRequester = remember { BringIntoViewRequester() } // Requester to bring the card into view

    Box(
        modifier = Modifier
            .zIndex(if (isOnTop) 1f else 0f) // Ensure expanded cards are drawn on top
            .padding(3.dp) // Padding around the card
            .bringIntoViewRequester(bringIntoViewRequester) // Enable view scrolling
            .clickable {
                onToggleExpand() // Toggle card expansion
                if (!isOnTop) {
                    coroutineScope.launch {
                        bringIntoViewRequester.bringIntoView() // Scroll to the card when expanded
                    }
                }
            }
    ) {
        Card(
            modifier = Modifier
                .width(if (isOnTop) 350.dp else 150.dp) // Width depends on whether the card is expanded
                .heightIn(min = 250.dp, max = if (isOnTop) 500.dp else 250.dp) // Dynamic height
                .animateContentSize(), // Smoothly animate size changes
            colors = CardDefaults.cardColors(containerColor = CardBackground), // Card background color
            shape = RoundedCornerShape(16.dp) // Rounded corners for the card
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp) // Padding inside the card
                    .verticalScroll(if (isOnTop) scrollState else rememberScrollState()) // Enable scrolling for expanded cards
            ) {
                if (!isOnTop) Spacer(modifier = Modifier.height(20.dp)) // Add spacing for non-expanded cards

                // Display entry title
                Text(
                    text = entry.title,
                    color = CardTitleYellow, // Title color
                    fontSize = 18.sp, // Font size

                    modifier = Modifier.padding(bottom = 8.dp) // Spacing below the title
                                        .width(200.dp), // Fixed width for the title
                )

                // Display entry content
                Text(
                    text = entry.content,
                    color = White, // Text color
                    fontSize = 14.sp, // Font size
                    lineHeight = 20.sp, // Line height for better readability
                    maxLines = if (isOnTop) Int.MAX_VALUE else 3 // Limit lines for non-expanded cards
                )

                // Display entry creation date
                Text(
                    text = entry.createdAt,
                    color = DateTextGray, // Date text color
                    fontSize = 12.sp, // Font size
                    fontWeight = FontWeight.Light, // Light font weight
                    modifier = Modifier.padding(top = 8.dp) // Spacing above the date
                )
            }
        }
    }
}

@Composable
fun CompactMoodSummary(moodPercentages: Map<String, Double>, mentalMessage: String) {
    // Card to display mood summary
    Card(
        shape = RoundedCornerShape(16.dp), // Rounded corners
        modifier = Modifier
            .fillMaxWidth() // Card takes the full width
            .padding(vertical = 8.dp) // Vertical spacing around the card
    ) {
        Column(
            modifier = Modifier
                .background(CardBackground) // Card background color
                .padding(16.dp), // Padding inside the card
            verticalArrangement = Arrangement.spacedBy(8.dp) // Spacing between elements
        ) {
            // Mood summary header
            Text(
                text = "Mood Overview",
                color = GoldYellow, // Header text color
                fontSize = 20.sp, // Font size
                fontWeight = FontWeight.Bold // Bold font weight
            )
            // Display the mental message
            Text(
                text = mentalMessage,
                color = White, // Text color
                fontSize = 14.sp, // Font size
                fontWeight = FontWeight.Medium // Medium font weight
            )
            Spacer(modifier = Modifier.height(8.dp)) // Spacing below the message

            // Display mood percentages in a row
            Row(
                modifier = Modifier.fillMaxWidth(), // Row takes full width
                horizontalArrangement = Arrangement.SpaceBetween // Space between mood columns
            ) {
                moodPercentages.forEach { (mood, percentage) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Display mood icon
                        Text(text = mood, color = GoldYellow, fontSize = 18.sp)
                        // Display percentage
                        Text(text = "${percentage.toInt()}%", color = White, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
