package org.mql.laktam.prayertimesilencer

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.media.AudioManager
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import org.mql.laktam.prayertimesilencer.ui.theme.PrayerTimeSilencerTheme
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Timer
import kotlin.concurrent.timerTask
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.*
import androidx.lifecycle.lifecycleScope


class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContent {
            PrayerTimeSilencerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SilenceButton(
                        modifier = Modifier.padding(innerPadding),
                        fusedLocationClient = fusedLocationClient
                    )
                }
            }
        }
    }
}

@Composable
fun SilenceButton(modifier: Modifier = Modifier, fusedLocationClient: FusedLocationProviderClient) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var locationPermissionGranted by remember { mutableStateOf(false) }
    var doNotDisturbPermissionGranted by remember { mutableStateOf(false) }

    if (!locationPermissionGranted) {
        locationPermissionGranted = ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!locationPermissionGranted) {
            LaunchedEffect(Unit) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            }
        }
    }

    // Check and request Do Not Disturb permission
    if (!doNotDisturbPermissionGranted) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        doNotDisturbPermissionGranted = notificationManager.isNotificationPolicyAccessGranted

        if (!doNotDisturbPermissionGranted) {
            LaunchedEffect(Unit) {
                val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                context.startActivity(intent)
            }
        }
    }

    // Button to silence the phone based on location
    Button(
        onClick = {
            if (locationPermissionGranted && doNotDisturbPermissionGranted) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        println("Latitude: $latitude, Longitude: $longitude")
//
//                        // Perform the API call and silence the phone
                        coroutineScope.launch {
                            try {
                                val response = RetrofitInstance.api.getPrayerTimes(latitude, longitude)
                                val fajrTime = response.data.timings.Fajr
                                println("Fajr time: $fajrTime")
                                // Use the fetched prayer time
//                                val fajrDate =  stringToDate(fajrTime);
                                val fajrDate =  getPrayerTime("19:47");
                                if(fajrDate != null){
                                    schedulePhoneSilence(context, fajrDate)
                                }
//                                schedulePhoneSilence(context, fajrTime)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                println("Failed to fetch prayer times")
                            }
                        }
//                        (context as Activity).lifecycleScope.launch {
//                            val prayerTime = fetchPrayerTime(latitude, longitude)
//                            if (prayerTime != null) {
//                                schedulePhoneSilence(context, prayerTime)
//                            }
//                        }
                    } else {
                        println("Location not available")
                    }
                }
            } else {
                println("Permissions not granted")
            }
        },
        modifier = modifier
    ) {
        Text(text = "Silence Phone")
    }

//
//    // Request location permission if not granted
//    val locationPermissionState = remember {
//        // Check and request location permission
//        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//    }
//
//    if (!locationPermissionState) {
//        // Request permission
//        LaunchedEffect(Unit) {
//            ActivityCompat.requestPermissions(
//                context as ComponentActivity,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                1
//            )
//        }
//    }
//
//    Button(
//        onClick = {
//            // Check if the permission is granted
//            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
//                    if (location != null) {
//                        val latitude = location.latitude
//                        val longitude = location.longitude
//                        // Use latitude and longitude as needed
//                        println("Latitude: $latitude, Longitude: $longitude")
//                    } else {
//                        println("Location not available")
//                    }
//                }
//            } else {
//                // Request Do Not Disturb access if not granted
//                val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
//                context.startActivity(intent)
//            }
//        },
//        modifier = modifier
//    ) {
//        Text(text = "Silence Phone")
//    }
}

// Function to fetch prayer time from the API
//suspend fun fetchPrayerTime(latitude: Double, longitude: Double): Date? {
//    try {
//        val apiUrl = "https://api.aladhan.com/v1/timings?latitude=$latitude&longitude=$longitude"
//        val response = URL(apiUrl).readText()
//
//        // Parsing the prayer time from the response
//        val prayerTimeString = parsePrayerTimeFromResponse(response)
//
//        return SimpleDateFormat("HH:mm", Locale.getDefault()).parse(prayerTimeString)
//    } catch (e: Exception) {
//        e.printStackTrace()
//        return null
//    }
//}

fun getPrayerTime(timeString: String): Date? {
    return try {
        // Parse the time string "HH:mm"
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val parsedTime = timeFormat.parse(timeString)

        // Combine the parsed time with the current date
        val calendar = Calendar.getInstance()
        val currentDate = calendar.time

        if (parsedTime != null) {
            calendar.time = parsedTime
            val hours = calendar.get(Calendar.HOUR_OF_DAY)
            val minutes = calendar.get(Calendar.MINUTE)

            calendar.time = currentDate
            calendar.set(Calendar.HOUR_OF_DAY, hours)
            calendar.set(Calendar.MINUTE, minutes)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            return calendar.time
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
// Function to schedule the phone to be silenced for 25 minutes
fun schedulePhoneSilence(context: Context, prayerTime: Date) {
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val currentTime = Calendar.getInstance().time
    println("prayer time $prayerTime.time")
    val startDelay = prayerTime.time - currentTime.time
    val endDelay = startDelay + 2 * 60 * 1000  // 2 minutes in milliseconds
// Schedule the phone to be silenced at the start time
    if (startDelay > 0) {
        Timer().schedule(timerTask {
            audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
            println("Phone silenced at $prayerTime")

            // Schedule the phone to return to normal mode at the end time
            Timer().schedule(timerTask {
                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                println("Phone restored to normal mode 2 minutes after $prayerTime")
            }, endDelay)
        }, startDelay)
    } else {
        println("Scheduled prayer time has already passed.")
    }
//    calendar.add(Calendar.MINUTE, 2)
//    val endSilence = calendar.time
//
//    // Schedule the phone to be silenced at the start time
//    Timer().schedule(timerTask {
//        audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
//        println("Phone silenced at $startSilence")
//
//        // Schedule the phone to return to normal mode at the end time
//        Timer().schedule(timerTask {
//            audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
//            println("Phone restored to normal mode at $endSilence")
//        }, endSilence)
//    }, startSilence)
}
//@Preview(showBackground = true)
//@Composable
//fun SilenceButtonPreview() {
//    PrayerTimeSilencerTheme {
//        SilenceButton(fusedLocationClient = FusedLocationProviderClient(LocalContext.current))
//    }
//}
