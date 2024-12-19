package com.grifffith.mindfuljournal.ui.components

import FitnessRepository
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.grifffith.mindfuljournal.ui.theme.CardBackground
import com.grifffith.mindfuljournal.ui.theme.GoldYellow
import com.grifffith.mindfuljournal.ui.theme.LightGray
import com.grifffith.mindfuljournal.ui.theme.White
import kotlinx.coroutines.launch



// The step count goal updates while the screen is active, but the updates stop working after navigating to a different screen.

// When navigating away from this screen, the listener is unregistered, preventing further updates.

@Composable
fun StepGoalUpdater(
    stepGoal: Int,
    fitnessRepository: FitnessRepository,
    onGoalUpdated: (Int) -> Unit
) {
    var newGoalText by remember { mutableStateOf(stepGoal.toString()) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.DirectionsWalk,
                contentDescription = "Edit Step Goal",
                tint = GoldYellow,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = newGoalText,
                onValueChange = { newGoalText = it },
                label = { Text("Enter Step Goal", color = White) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = CardBackground,
                    unfocusedContainerColor = CardBackground,
                    focusedIndicatorColor = GoldYellow,
                    unfocusedIndicatorColor = LightGray,
                    cursorColor = GoldYellow
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                newGoalText.toIntOrNull()?.let { newGoal ->
                    if (newGoal > 0) {
                        coroutineScope.launch {
                            val success = fitnessRepository.setStepGoal(newGoal)
                            if (true) {
                                onGoalUpdated(newGoal)
                                Toast.makeText(
                                    context,
                                    "Step goal updated to $newGoal!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Failed to update step goal. Please try again.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Please enter a valid goal.", Toast.LENGTH_SHORT).show()
                    }
                } ?: Toast.makeText(context, "Please enter a numeric value.", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = GoldYellow)
        ) {
            Text(text = "Save Goal", color = Color.Black)
        }
}
    }