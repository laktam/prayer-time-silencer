package org.mql.laktam.prayertimesilencer

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat

@Composable
fun ActivationButton(modifier: Modifier = Modifier) {
     val context = LocalContext.current

        // State variables for permissions and service status
        var locationPermissionGranted by remember { mutableStateOf(false) }
        var doNotDisturbPermissionGranted by remember { mutableStateOf(false) }
//        var isServiceRunning by remember { mutableStateOf(false) }
    var isServiceRunning by remember { mutableStateOf(ServiceManager.isServiceRunning(context)) }

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

            // Check if service should be running
//            println("permissions $locationPermissionGranted ,$doNotDisturbPermissionGranted")
//            println("service status $isServiceRunning")
//            if (locationPermissionGranted && doNotDisturbPermissionGranted && !isServiceRunning) {
//                startService(context)
//                isServiceRunning = true
//            }
        }

        val handleButtonClick = {
            if (isServiceRunning) {
                stopService(context)
                isServiceRunning = false
            } else {
                startService(context)
                isServiceRunning = true
            }
        }

    Column {
        Button(onClick = { handleButtonClick() }, modifier = modifier) {
            Text(if (isServiceRunning) "Stop Service" else "Start Service")
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
