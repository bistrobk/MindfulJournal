
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grifffith.mindfuljournal.ui.theme.CardBackground
import com.grifffith.mindfuljournal.ui.theme.DarkBlue
import com.grifffith.mindfuljournal.ui.theme.GoldYellow
import com.grifffith.mindfuljournal.ui.theme.LightBlue
import com.grifffith.mindfuljournal.ui.theme.LightGray
import com.grifffith.mindfuljournal.ui.theme.White
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
@Composable
fun JournalScreen(journalRepository: JournalRepository) {
    // Mutable state list to store journal entries
    val journalEntries = remember { mutableStateListOf<JournalEntry>() }
    var journalInput by remember { mutableStateOf("") } // Input field for new journal entry
    var isExpanded by remember { mutableStateOf(false) } // Tracks whether the new entry input is expanded

    // Load all journal entries from the repository on first composition
    LaunchedEffect(Unit) {
        journalEntries.clear()
        journalEntries.addAll(journalRepository.getAllJournalEntries())
    }

    // Get screen configuration and calculate screen height in dp
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    // Main container for the Journal screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DarkBlue, LightBlue) // Gradient background
                )
            )
            .pointerInput(isExpanded) {
                detectTapGestures { if (isExpanded) isExpanded = false } // Collapse input on tap outside
            }
    ) {
        // Column to hold all journal UI elements
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp) // Padding around the entire column
        ) {
            // Header with title and icon
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.MenuBook, // Icon for the header
                    contentDescription = "Journal Icon",
                    tint = GoldYellow,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Journal", // Main title
                    color = GoldYellow,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Divider(color = LightGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp)) // Divider below title

            // Subheading for journal entries
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = "Entries Icon",
                    tint = White,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Your Journal Entries", // Subheading text
                    color = White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Divider(color = LightGray.copy(alpha = 0.5f), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 12.dp)) // Divider

            // Display all journal entries in a LazyColumn
            LazyColumn(
                modifier = Modifier.weight(1f), // Occupies available vertical space
                contentPadding = PaddingValues(bottom = 16.dp), // Padding at the bottom
                verticalArrangement = Arrangement.spacedBy(12.dp) // Space between items
            ) {
                items(journalEntries, key = { it.entryId }) { entry ->
                    // Display each journal entry as a card
                    DisplayJournalEntryCard(
                        entry = entry,
                        onDelete = {
                            journalEntries.remove(entry) // Remove from local state
                            journalRepository.deleteJournalEntry(entry.entryId.toString()) // Delete from database
                        },
                        onEdit = { newContent, newImagePath ->
                            // Update the entry content and/or image
                            val updatedEntry = entry.copy(content = newContent, imagePath = newImagePath)
                            journalEntries[journalEntries.indexOf(entry)] = updatedEntry
                            journalRepository.updateJournalEntry(entry.entryId.toString(), newContent, newImagePath)
                        },
                        onLove = { isLoved ->
                            // Toggle love status for the entry
                            val updatedLoves = if (isLoved) 1 else 0
                            entry.loves = updatedLoves
                            journalRepository.updateLoves(entry.entryId.toString(), updatedLoves)
                        },
                        modifier = Modifier.animateItemPlacement(tween(300)) // Smooth animation for item placement
                    )
                }
            }

            // Animated visibility for adding a new journal entry
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 2 }, // Enter animation
                exit = fadeOut(tween(300)) + slideOutVertically(tween(300)) { it / 2 } // Exit animation
            ) {
                Column(modifier = Modifier.animateContentSize()) {
                    // Input field for new journal entry
                    OutlinedTextField(
                        value = journalInput,
                        onValueChange = { journalInput = it },
                        label = { Text("Title and Text (new line separated)", color = White) },
                        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp, color = White),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(screenHeight / 2) // Takes up half the screen height
                            .padding(bottom = 8.dp),
                        singleLine = false,
                        maxLines = 10,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = CardBackground,
                            unfocusedContainerColor = CardBackground,
                            focusedIndicatorColor = GoldYellow,
                            unfocusedIndicatorColor = LightGray,
                            cursorColor = GoldYellow
                        )
                    )
                }
            }
        }

        // Animated Floating Action Button (FAB) for adding new entries
        val fabRotation by animateFloatAsState(
            targetValue = if (isExpanded) 45f else 0f, // Rotate to 45 degrees when expanded
            animationSpec = tween(300)
        )

        val infiniteTransition = rememberInfiniteTransition(label = "")
        val fabScale by infiniteTransition.animateFloat(
            initialValue = 1.0f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
            ), label = ""
        )

        // Conditional layout for FAB and action buttons
        if (isExpanded) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Save button for adding the journal entry
                Button(
                    onClick = {
                        if (journalInput.isNotEmpty() && "\n" in journalInput) {
                            val formatter = SimpleDateFormat("EEEE dd MMM yyyy, hh:mm a", Locale.getDefault())
                            val currentDateTime = formatter.format(Date())
                            val (title, content) = journalInput.split("\n", limit = 2)
                            journalRepository.addJournalEntry(title, content, currentDateTime, null) // No image
                            journalEntries.add(JournalEntry(0, title, content, currentDateTime, null, 0))
                            journalInput = "" // Reset input
                            isExpanded = false // Collapse the input
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save", tint = DarkBlue)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Save", color = DarkBlue, fontWeight = FontWeight.Bold)
                }

                // Cancel button to discard the entry
                Button(
                    onClick = {
                        journalInput = "" // Clear input
                        isExpanded = false // Collapse the input
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LightGray),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text("Cancel", color = DarkBlue, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            // Default FAB for adding new entries
            FloatingActionButton(
                onClick = { isExpanded = true },
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .rotate(fabRotation)
                    .scale(fabScale),
                containerColor = GoldYellow
            ) {
                Text("+", color = White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DisplayJournalEntryCard(
    entry: JournalEntry, // Represents a single journal entry
    onDelete: () -> Unit, // Callback when the entry is deleted
    onEdit: (String, String?) -> Unit, // Callback when the entry is edited
    onLove: (Boolean) -> Unit, // Callback when the 'love' status is toggled
    modifier: Modifier = Modifier // Modifier for custom styling
) {
    // State to determine if the card is expanded
    var isExpanded by remember { mutableStateOf(false) }
    // State to determine if the card is in editing mode
    var isEditing by remember { mutableStateOf(false) }
    // Holds the editable content of the journal entry
    var editableText by remember { mutableStateOf(entry.content) }
    // Tracks whether the journal entry is 'loved'
    var isLoved by remember { mutableStateOf(entry.loves > 0) }

    // Main card container
    Card(
        shape = RoundedCornerShape(8.dp), // Rounded corners for the card
        modifier = modifier
            .fillMaxWidth() // Card takes full width
            .animateContentSize() // Smoothly animate size changes
            .clickable { if (!isEditing) isExpanded = !isExpanded }, // Toggle expansion on click
        colors = CardDefaults.cardColors(containerColor = CardBackground), // Set background color
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Card elevation for shadow effect
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth() // Content fills the card's width
                .padding(horizontal = 16.dp, vertical = 12.dp) // Padding inside the card
        ) {
            // Row for the title and love button
            Row(
                verticalAlignment = Alignment.CenterVertically, // Vertically center-align content
                horizontalArrangement = Arrangement.SpaceBetween, // Space out items in the row
                modifier = Modifier.fillMaxWidth() // Row takes full width
            ) {
                // Display the journal entry title
                Text(
                    text = entry.title,
                    color = GoldYellow, // Title color
                    fontWeight = FontWeight.Bold, // Bold font weight
                    fontSize = 20.sp // Font size
                )

                // Display the love button if not expanded or editing
                if (!isExpanded && !isEditing) {
                    LoveButton(
                        isLoved = isLoved,
                        onClick = {
                            isLoved = !isLoved // Toggle 'loved' status
                            onLove(isLoved) // Call the callback
                        }
                    )
                }
            }

            // Display the date of the journal entry
            Text(
                text = entry.createdAt,
                color = LightGray, // Text color for the date
                fontSize = 14.sp, // Font size
                modifier = Modifier.padding(top = 4.dp) // Spacing above the date
            )

            Spacer(modifier = Modifier.height(16.dp)) // Space between the date and the content

            // Editing mode visibility
            AnimatedVisibility(
                visible = isEditing, // Show if editing
                enter = fadeIn(tween(300)) + slideInHorizontally { it / 2 }, // Enter animation
                exit = fadeOut(tween(300)) + slideOutHorizontally { it / 2 } // Exit animation
            ) {
                Column {
                    // Editable text field
                    OutlinedTextField(
                        value = editableText, // Current editable content
                        onValueChange = { editableText = it }, // Update content on change
                        label = { Text("Edit your thoughts...") },
                        modifier = Modifier
                            .fillMaxWidth() // Text field takes full width
                            .padding(8.dp), // Padding around the text field
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = CardBackground, // Background color when focused
                            unfocusedContainerColor = CardBackground, // Background color when unfocused
                            focusedIndicatorColor = GoldYellow, // Border color when focused
                            unfocusedIndicatorColor = LightGray, // Border color when unfocused
                            cursorColor = GoldYellow // Cursor color
                        )
                    )
                    // Update button
                    Button(
                        onClick = {
                            onEdit(editableText, entry.imagePath) // Save changes
                            isEditing = false // Exit editing mode
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldYellow), // Button color
                        modifier = Modifier
                            .padding(8.dp) // Padding around the button
                            .align(Alignment.End) // Align button to the end
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Update", tint = DarkBlue)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Update", color = DarkBlue, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Content visibility when not editing
            AnimatedVisibility(
                visible = !isEditing, // Show if not editing
                enter = fadeIn(tween(300)), // Fade-in animation
                exit = fadeOut(tween(300)) // Fade-out animation
            ) {
                Text(
                    text = entry.content, // Display the content
                    color = White, // Text color
                    fontSize = 16.sp, // Font size
                    maxLines = if (isExpanded) Int.MAX_VALUE else 2 // Show full content if expanded
                )
            }

            // Additional actions when expanded
            if (isExpanded && !isEditing) {
                Spacer(modifier = Modifier.height(16.dp)) // Space above the actions

                Row(
                    modifier = Modifier.fillMaxWidth(), // Row takes full width
                    horizontalArrangement = Arrangement.spacedBy(8.dp), // Space between buttons
                    verticalAlignment = Alignment.CenterVertically // Center-align content vertically
                ) {
                    // Love button
                    LoveButton(
                        isLoved = isLoved,
                        onClick = {
                            isLoved = !isLoved // Toggle 'loved' status
                            onLove(isLoved) // Call the callback
                        }
                    )

                    Spacer(modifier = Modifier.weight(1f)) // Spacer to push buttons to the right

                    // Edit button
                    Button(
                        onClick = { isEditing = true }, // Enter editing mode
                        colors = ButtonDefaults.buttonColors(containerColor = GoldYellow), // Button color
                        shape = RoundedCornerShape(8.dp) // Rounded corners
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit", color = White, fontWeight = FontWeight.Bold)
                    }

                    // Delete button
                    Button(
                        onClick = { onDelete() }, // Trigger delete callback
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE95353)), // Button color
                        shape = RoundedCornerShape(8.dp) // Rounded corners
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete", color = White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun LoveButton(
    isLoved: Boolean, // Indicates whether the entry is 'loved'
    onClick: () -> Unit, // Callback for love button click
    modifier: Modifier = Modifier // Modifier for custom styling
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = Icons.Filled.Favorite, // Heart icon
            contentDescription = "Love", // Description for accessibility
            tint = if (isLoved) Color.Red else White, // Red if loved, white otherwise
            modifier = Modifier.size(32.dp) // Icon size
        )
    }
}
