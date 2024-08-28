package org.mql.laktam.prayertimesilencer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp


@Composable
fun DisplaySilenceTimes(viewModel: MainViewModel) {
    val context = LocalContext.current
    val prayerTimes by viewModel.prayerTimes.collectAsState()

    val isServiceRunning by viewModel.isServiceRunning.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initializeServiceState(context)
        viewModel.loadPrayerTimes(context)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (!isServiceRunning) {
            Text(
                text = "Click start to fetch prayer times and automatically schedule phone silence",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }else if (prayerTimes.isEmpty()) {
            LoadingIndicator()
        } else {
            Text(
                text = "Scheduled Silence Times",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            PrayerTimesList(prayerTimes)
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun PrayerTimesList(prayerTimes: List<Triple<String, String, String>>) {
    LazyColumn {
        items(prayerTimes) { (name, start, end) ->
            PrayerTimeCard(name, start, end)
        }
    }
}

@Composable
fun PrayerTimeCard(prayerName: String, start: String, end: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
//                .padding(16.dp)
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Prayer name in larger font
            Text(
                text = prayerName,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp)//padding(bottom = 8.dp)
            )

            // Start and end times
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            )
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
//                    .background(Color(234,245,249,255))
                    .padding(6.dp)  // Adjust padding as needed
            ) {
                Column {
                    Text(
                        text = "Start: $start",
                        style = MaterialTheme.typography.titleLarge,  // Use titleLarge or another large style
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 4.dp)  // Adjust spacing between texts as needed
                    )
                    Text(
                        text = "End: $end",
                        style = MaterialTheme.typography.titleLarge,  // Use titleLarge or another large style
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}