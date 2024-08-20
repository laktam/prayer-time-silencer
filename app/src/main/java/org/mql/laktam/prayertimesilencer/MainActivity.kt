package org.mql.laktam.prayertimesilencer

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import org.mql.laktam.prayertimesilencer.ui.theme.PrayerTimeSilencerTheme

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

    // Request location permission if not granted
    val locationPermissionState = remember {
        // Check and request location permission
        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    if (!locationPermissionState) {
        // Request permission
        LaunchedEffect(Unit) {
            ActivityCompat.requestPermissions(
                context as ComponentActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }

    Button(
        onClick = {
            // Check if the permission is granted
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        // Use latitude and longitude as needed
                        println("Latitude: $latitude, Longitude: $longitude")
                    } else {
                        println("Location not available")
                    }
                }
            } else {
                // Request Do Not Disturb access if not granted
                val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                context.startActivity(intent)
            }
        },
        modifier = modifier
    ) {
        Text(text = "Silence Phone")
    }
}

//@Preview(showBackground = true)
//@Composable
//fun SilenceButtonPreview() {
//    PrayerTimeSilencerTheme {
//        SilenceButton(fusedLocationClient = FusedLocationProviderClient(LocalContext.current))
//    }
//}
