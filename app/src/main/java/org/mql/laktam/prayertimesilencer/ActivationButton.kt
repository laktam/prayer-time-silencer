package org.mql.laktam.prayertimesilencer

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
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
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun ActivationFab(viewModel: MainViewModel) {
    val context = LocalContext.current

    // State variables for permissions and service status
    var locationPermissionGranted by remember { mutableStateOf(false) }
    var doNotDisturbPermissionGranted by remember { mutableStateOf(false) }
    val isServiceRunning by viewModel.isServiceRunning.collectAsState()

    // Permission checks and requests
    LaunchedEffect(Unit) {
        // Check for location permission
        if (!locationPermissionGranted) {
            locationPermissionGranted = ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (!locationPermissionGranted) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            }
        }

        // Check for Do Not Disturb permission
        if (!doNotDisturbPermissionGranted) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            doNotDisturbPermissionGranted =
                notificationManager.isNotificationPolicyAccessGranted

            if (!doNotDisturbPermissionGranted) {
                val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                context.startActivity(intent)
            }
        }
    }

    val handleFabClick = {
        if (isServiceRunning) {
            stopService(context)
            viewModel.setServiceRunning(context, false)
        } else {
            startService(context)
            viewModel.setServiceRunning(context, true)
        }
    }

    FloatingActionButton(onClick = { handleFabClick() }) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isServiceRunning) Icons.Filled.Close else Icons.Filled.PlayArrow,
                contentDescription = if (isServiceRunning) "Stop" else "Start"
            )
            Text(
                text = if (isServiceRunning) "Stop" else "Start",
                style = MaterialTheme.typography.titleLarge,
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
