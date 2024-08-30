package org.mql.laktam.prayertimesilencer

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.platform.LocalContext

@Composable
fun SilenceDurationChooser() {
    val context = LocalContext.current

    // List of numeric mute durations from 1 to 90 minutes
    val durations = (1..90).toList() // Creates a list with numbers 1 to 90

    // State to manage the expanded state of the dropdown menu
    var expanded by remember { mutableStateOf(false) }

    // State to manage the selected duration
    var selectedDuration by remember { mutableStateOf(durations[0]) }

    // Box to wrap the TextField and DropdownMenu
    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        // TextField to show the selected item and toggle the dropdown menu
        TextField(
            value = "$selectedDuration minutes", // Display selected duration with unit
            onValueChange = {},
            label = { Text("Select Mute Duration") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand")
                }
            }
        )

        // Dropdown menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            durations.forEach { duration ->
                DropdownMenuItem(
                    text = { Text("$duration minutes") }, // Show each duration with the unit
                    onClick = {
                        selectedDuration = duration // Set the selected duration
                        ServiceManager.silenceTime = duration * 60 * 1000L
                        expanded = false // Close the dropdown
                        stopService(context)
                        startService(context)
                    }
                )
            }
        }
    }
}
