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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.times
import org.mql.laktam.prayertimesilencer.ServiceManager.initSilenceTime

@Composable
fun SilenceDurationChooser(viewModel: MainViewModel) {
    val context = LocalContext.current

    // List of numeric mute durations from 2 to 90 minutes
    val durations = (2..90).toList()

    // State to manage the expanded state of the dropdown menu
    var expanded by remember { mutableStateOf(false) }
    // init time duration
    initSilenceTime(context)
    // State to manage the selected duration
    var selectedDuration by remember { mutableStateOf(ServiceManager.silenceTime) }

    val isServiceRunning by viewModel.isServiceRunning.collectAsState()

    // Box to wrap the TextField and DropdownMenu
    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        // TextField to show the selected item and toggle the dropdown menu
        TextField(
            value = "$selectedDuration",
            onValueChange = {},
            label = { Text(stringResource(R.string.select_mute_duration)) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand")
                }
            }
        )

        // Dropdown menu with limited height to show only 6 items at a time
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 6 * 48.dp) // Adjusting height to show 6 items
        ) {
            durations.forEach { duration ->
                DropdownMenuItem(
                    text = { Text("$duration") },
                    onClick = {
                        selectedDuration = duration
//                        val silenceDurationInMillis = duration// * 60 * 1000L
//                        ServiceManager.silenceTime = silenceDurationInMillis
//                        ServiceManager.silenceTime = selectedDuration
                        ServiceManager.saveSilenceTime(context, selectedDuration) // Save to SharedPreferences
                        expanded = false
                        if (isServiceRunning) {
                            stopService(context)
                            startService(context)
                        }
                    }
                )
            }
        }
    }
}

//@Composable
//fun SilenceDurationChooser(viewModel: MainViewModel) {
//    val context = LocalContext.current
//
//    // List of numeric mute durations from 1 to 90 minutes
//    val durations = (2..90).toList() // Creates a list with numbers 1 to 90
//
//    // State to manage the expanded state of the dropdown menu
//    var expanded by remember { mutableStateOf(false) }
//
//    // State to manage the selected duration
//    var selectedDuration by remember { mutableStateOf(durations[28]) }
//
//    val isServiceRunning by viewModel.isServiceRunning.collectAsState()
//    // Box to wrap the TextField and DropdownMenu
//    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
//        // TextField to show the selected item and toggle the dropdown menu
//        TextField(
//            value = "$selectedDuration", // Display selected duration with unit
//            onValueChange = {},
//            label = { Text(stringResource(R.string.select_mute_duration)) },
//            modifier = Modifier.fillMaxWidth(),
//            readOnly = true,
//            trailingIcon = {
//                IconButton(onClick = { expanded = true }) {
//                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand")
//                }
//            }
//        )
//
//        // Dropdown menu
//        DropdownMenu(
//            expanded = expanded,
//            onDismissRequest = { expanded = false }
//        ) {
//            durations.forEach { duration ->
//                DropdownMenuItem(
//                    text = { Text("$duration") }, // Show each duration with the unit
//                    onClick = {
//                        selectedDuration = duration // Set the selected duration
//                        ServiceManager.silenceTime = duration * 60 * 1000L
//                        expanded = false // Close the dropdown
//                        if(isServiceRunning){
//                            stopService(context)
//                            startService(context)
//                        }
//                    }
//                )
//            }
//        }
//    }
//}
