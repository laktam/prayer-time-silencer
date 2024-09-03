package org.mql.laktam.prayertimesilencer

import android.content.Context
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.util.Locale


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
                text = stringResource(R.string.stopped_service_note),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }else if (prayerTimes.isEmpty()) {
            LoadingIndicator()
        } else {

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
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Text(
                text = stringResource(R.string.searching_for_prayer_times),
                style = MaterialTheme.typography.titleLarge,
            )
        }

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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .drawBehind {
                // Customize the shadow for the bottom only with rounded corners
                drawRoundRect(
                    color = Color.Black.copy(alpha = 0.1f), // Adjust color and opacity
                    topLeft = Offset(0f, size.height), // Start at the bottom
                    size = Size(size.width, 4.dp.toPx()), // Set the shadow size
                )
            }
    ) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
//            .padding(vertical = 0.dp),
            .padding(top = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RectangleShape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,

        ) {
            println("language ${Locale.getDefault().language}")
            PrayerTimeCardContent(Locale.getDefault().language, prayerName, start, end)

        }
    }
    }
}

@Composable
fun PrayerTimeCardContent(language:String, prayerName: String, start: String, end: String){
    val context = LocalContext.current
    val resourceId = getStringResourceByName(context, prayerName)
    // Prayer name in larger font
    Text(
        text = stringResource(resourceId),
        style = MaterialTheme.typography.displaySmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp)//padding(bottom = 8.dp)
    )
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
//            .padding(6.dp)  // Adjust padding as needed
    ) {
        Column {
            Text(
                text = stringResource(R.string.from) +" $start\n" + stringResource(R.string.to) + " $end",
                style = MaterialTheme.typography.titleLarge,  // Use titleLarge or another large style
                color = MaterialTheme.colorScheme.onBackground,
            )

        }
    }

}

fun getStringResourceByName(context: Context, resourceName: String): Int {
    return context.resources.getIdentifier(resourceName, "string", context.packageName)
}

