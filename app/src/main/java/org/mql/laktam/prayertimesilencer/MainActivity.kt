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
                    ActivationButton(modifier = Modifier.padding(innerPadding))
                    DisplayPrayerTimes()
                }
            }
        }
    }
}

@Composable
fun DisplayPrayerTimes() {
    val context = LocalContext.current
//    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val prayerTimesState = remember { mutableStateOf<List<String>?>(null) }

    // Load prayer times when the composable is launched
    LaunchedEffect(Unit) {
        val sharedPreferences = context.getSharedPreferences("PrayerTimesPreferences", Context.MODE_PRIVATE)
        val timeStrings = sharedPreferences.getStringSet("scheduledTimes", setOf()) ?: setOf()

        // Sort and store the prayer times in the state
        val formattedTimes = timeStrings.toList().sorted()
        prayerTimesState.value = formattedTimes

        // Debug log
        println("Loaded prayer times: $formattedTimes")
    }

    Column(modifier = Modifier.padding(16.dp)) {
        val times = prayerTimesState.value
        if (times == null) {
            Text(
                text = "Loading prayer times...",
                style = MaterialTheme.typography.bodyMedium
            )
        } else if (times.isNotEmpty()) {
            Text(
                text = "Scheduled Prayer Times:",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            times.forEach { time ->
                Text(text = time, style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            Text(
                text = "No prayer times scheduled.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

}

