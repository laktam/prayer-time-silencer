package org.mql.laktam.prayertimesilencer



import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    private val viewModel: MainViewModel by viewModels()
    private val prayerTimesUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "PRAYER_TIMES_UPDATED") {
                viewModel.loadPrayerTimes(this@MainActivity)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContent {
            PrayerTimeSilencerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp)
                    ) {
                        ActivationButton(modifier = Modifier.padding(bottom = 16.dp))
                        DisplaySilenceTimes(viewModel)
                    }
                }
            }
        }

        registerReceiver(prayerTimesUpdateReceiver, IntentFilter("PRAYER_TIMES_UPDATED"))


        // Register the receiver using LocalBroadcastManager
//        LocalBroadcastManager.getInstance(this).registerReceiver(
//            prayerTimesUpdateReceiver,
//            IntentFilter("PRAYER_TIMES_UPDATED")
//        )

// Register the receiver with the appropriate flag
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            registerReceiver(
//                prayerTimesUpdateReceiver,
//                IntentFilter("PRAYER_TIMES_UPDATED"),
//                RECEIVER_NOT_EXPORTED
//            )
//        } else {
//            registerReceiver(prayerTimesUpdateReceiver, IntentFilter("PRAYER_TIMES_UPDATED"))
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(prayerTimesUpdateReceiver)
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(prayerTimesUpdateReceiver)

    }
}
@Composable
fun DisplaySilenceTimes(viewModel: MainViewModel) {
    val context = LocalContext.current
    val prayerTimes by viewModel.prayerTimes.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPrayerTimes(context)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        if (prayerTimes.isEmpty()) {
            Text(
                text = "Loading prayer times...",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Text(
                text = "Scheduled Silence Times:",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            prayerTimes.forEach { (start, end) ->
                Text(text = "from $start to $end", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}