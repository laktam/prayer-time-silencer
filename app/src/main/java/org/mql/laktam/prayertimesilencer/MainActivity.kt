package org.mql.laktam.prayertimesilencer



import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.mql.laktam.prayertimesilencer.ui.theme.PrayerTimeSilencerTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContent {
            PrayerTimeSilencerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Use a Column to prevent overlap and add spacing
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp) // additional padding to ensure content isn't too close to edges
                    ) {
                        ActivationButton(modifier = Modifier.padding(bottom = 16.dp))
                        DisplaySilenceTimes()
                    }
                }
            }
        }
    }
}

@Composable
fun DisplaySilenceTimes() {
    val context = LocalContext.current
    val prayerTimesState = remember { mutableStateOf<List<Pair<String, String>>?>(null) } // List of pairs for start and end times

    // Load prayer times and calculate silence periods when the composable is launched
    LaunchedEffect(Unit) {
        val sharedPreferences = context.getSharedPreferences("PrayerTimesPreferences", Context.MODE_PRIVATE)
        val timeStrings = sharedPreferences.getStringSet("scheduledTimes", setOf()) ?: setOf()

        // Sort and store the prayer times in the state
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val silenceDuration = ServiceManager.silenceTime

        val silenceTimes = timeStrings.map { timeString ->
            val startTime = timeFormat.parse(timeString)
            val endTime = Date(startTime.time + silenceDuration)

            timeString to timeFormat.format(endTime)
        }.sortedBy { it.first }

        prayerTimesState.value = silenceTimes

        // Debug log
        println("Loaded and calculated prayer times: $silenceTimes")
    }
    Column(modifier = Modifier.padding(16.dp)) {
        val silenceTimes = prayerTimesState.value
        if (silenceTimes == null) {
            Text(
                text = "Loading prayer times...",
                style = MaterialTheme.typography.bodyMedium
            )
        } else if (silenceTimes.isNotEmpty()) {
            Text(
                text = "Scheduled Silence Times:",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            silenceTimes.forEach { (start, end) ->
                Text(text = "from $start to $end", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            Text(
                text = "No Silence times scheduled.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

}

