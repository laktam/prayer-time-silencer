package org.mql.laktam.prayertimesilencer

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.times
import org.mql.laktam.prayertimesilencer.ServiceManager.initSilenceTime

@Composable
fun SilenceDurationChooser(viewModel: MainViewModel) {
    val context = LocalContext.current
    val durations = (2..90).toList()
    var expanded by remember { mutableStateOf(false) }

    // Initialize silence time
    LaunchedEffect(Unit) {
        initSilenceTime(context)
    }

    var selectedDuration by remember { mutableStateOf(ServiceManager.silenceTime) }
    val isServiceRunning by viewModel.isServiceRunning.collectAsState()

    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$selectedDuration",
                style = MaterialTheme.typography.titleLarge
            )
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 6 * 48.dp)
        ) {
            durations.forEach { duration ->
                DropdownMenuItem(
                    text = { Text("$duration") },
                    onClick = {
                        selectedDuration = duration
                        ServiceManager.saveSilenceTime(context, duration)
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