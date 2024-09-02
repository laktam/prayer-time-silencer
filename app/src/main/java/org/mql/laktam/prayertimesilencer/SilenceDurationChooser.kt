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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.times
import org.mql.laktam.prayertimesilencer.ServiceManager.initSilenceTime

@Composable
fun SilenceDurationChooser(viewModel: MainViewModel) {
    val context = LocalContext.current
    val durations = (2..90).toList()
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        initSilenceTime(context)
    }

    var selectedDuration by remember { mutableStateOf(ServiceManager.silenceTime) }
    val isServiceRunning by viewModel.isServiceRunning.collectAsState()

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        // Title
        Text(
            text = stringResource(R.string.select_mute_duration),
            style = MaterialTheme.typography.titleLarge,
//            modifier = Modifier.padding(bottom = 0.dp)
        )

        // Dropdown
        Box(modifier = Modifier.fillMaxWidth(0.65f)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$selectedDuration",
                    style = MaterialTheme.typography.titleLarge,//MaterialTheme.typography.bodyLarge
                )
                Icon(Icons.Default.Settings, contentDescription = "Expand")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.53f).heightIn(max = 6 * 48.dp)
            ) {
                durations.forEach { duration ->
                    DropdownMenuItem(
                        modifier = Modifier.fillMaxWidth(),
                        text = {
                            Text(
                                text = "$duration",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.fillMaxWidth(),
                            )

                        },
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

}
