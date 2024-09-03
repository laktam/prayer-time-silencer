package org.mql.laktam.prayertimesilencer

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.runtime.getValue
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat


@Composable
fun ActivationFab(viewModel: MainViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

//
    val locationPermissionsState = remember { mutableStateOf(ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) }
    val isServiceRunning by viewModel.isServiceRunning.collectAsState()
    val doNotDisturbPermissionGranted = remember { mutableStateOf(notificationManager.isNotificationPolicyAccessGranted()) }

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val locationPermissionsGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        doNotDisturbPermissionGranted.value =
        notificationManager.isNotificationPolicyAccessGranted

        if (locationPermissionsGranted && doNotDisturbPermissionGranted.value) {
            // permissions granted
            // first button click => start service
            startService(context)
            viewModel.setServiceRunning(context, true)
            println("permissions granted.")
        } else {
            // Location permissions denied
            println("permissions denied.")
        }
        locationPermissionsState.value = locationPermissionsGranted
        }

    val handleFabClick = {
        if(!doNotDisturbPermissionGranted.value && !locationPermissionsState.value){
            showDialog = true
        }
//        if (!doNotDisturbPermissionGranted.value) {
//                val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
//                context.startActivity(intent)
//        }
//        if(!locationPermissionsState.value) {
//            locationPermissionLauncher.launch(
//                arrayOf(
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//                )
//            )
//        }
//        if(!showDialog){
            if (locationPermissionsState.value && doNotDisturbPermissionGranted.value){
                if (isServiceRunning) {
                    stopService(context)
                    viewModel.setServiceRunning(context, false)
                } else {
                    startService(context)
                    viewModel.setServiceRunning(context, true)
                }
            }
//        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.permissions_dialog_title)) },
            text = { Text(stringResource(R.string.permissions_note)) },
            confirmButton = {
                Button(
                    onClick = {
                        if (!doNotDisturbPermissionGranted.value) {
                            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                            context.startActivity(intent)
                        }
                        if(!locationPermissionsState.value) {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                        showDialog = false
                    }
                ) {
                    Text(stringResource(R.string.give_permisssions_button))
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false },
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text(stringResource(R.string.cancel_button))
                }
            }
        )
    }

    FloatingActionButton(onClick = {
        handleFabClick()
    }) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isServiceRunning) Icons.Filled.Close else Icons.Filled.PlayArrow,
                contentDescription = if (isServiceRunning) "Stop" else "Start"
            )
            Text(
                text = if (isServiceRunning) stringResource(R.string.stop) else stringResource(R.string.start),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

fun startService(context: Context) {//, locationPermissionGranted: Boolean, doNotDisturbPermissionGranted : Boolean
//    if (locationPermissionGranted && doNotDisturbPermissionGranted) {
        val serviceIntent = Intent(context, PrayerTimeService::class.java)
        ContextCompat.startForegroundService(context, serviceIntent)
        ServiceManager.setServiceRunning(context, true) // Update service state
//    }

}

fun stopService(context: Context) {
    val serviceIntent = Intent(context, PrayerTimeService::class.java)
    context.stopService(serviceIntent)
    ServiceManager.setServiceRunning(context, false) // Update service state
}

//@Composable
//fun ActivationFab(viewModel: MainViewModel) {
//    val context = LocalContext.current
//
//    // State variables for permissions and service status
//    var locationPermissionGranted by remember { mutableStateOf(false) }
//    var doNotDisturbPermissionGranted by remember { mutableStateOf(false) }
//    val isServiceRunning by viewModel.isServiceRunning.collectAsState()
//
//    // Permission checks and requests
//    LaunchedEffect(Unit) {
//        // Check for location permission
//        if (!locationPermissionGranted) {
//            locationPermissionGranted = ActivityCompat.checkSelfPermission(
//                context, Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//
//            if (!locationPermissionGranted) {
//                ActivityCompat.requestPermissions(
//                    context as Activity,
//                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                    1
//                )
//            }
//        }
//
//        // Check for Do Not Disturb permission
//        if (!doNotDisturbPermissionGranted) {
//            val notificationManager =
//                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            doNotDisturbPermissionGranted =
//                notificationManager.isNotificationPolicyAccessGranted
//
//            if (!doNotDisturbPermissionGranted) {
//                val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
//                context.startActivity(intent)
//            }
//        }
//    }
//
//    val handleFabClick = {
//        if (isServiceRunning) {
//            stopService(context)
//            viewModel.setServiceRunning(context, false)
//        } else {
//            startService(context)
//            viewModel.setServiceRunning(context, true)
//        }
//    }
//
//    FloatingActionButton(onClick = { handleFabClick() }) {
//        Row(
//            modifier = Modifier.padding(horizontal = 8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Icon(
//                imageVector = if (isServiceRunning) Icons.Filled.Close else Icons.Filled.PlayArrow,
//                contentDescription = if (isServiceRunning) "Stop" else "Start"
//            )
//            Text(
//                text = if (isServiceRunning) stringResource(R.string.stop) else stringResource(R.string.start),
//                style = MaterialTheme.typography.bodyLarge,
//                modifier = Modifier.padding(start = 8.dp)
//            )
//        }
//    }
//}