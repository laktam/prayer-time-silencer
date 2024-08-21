package org.mql.laktam.prayertimesilencer

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.media.AudioManager
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Timer
import kotlin.concurrent.timerTask

class MyForegroundService : Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        // Initialize service
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("service started")
        getLocation()
        return START_STICKY
    }

    // the permission is checked in activationbutton before launching the service
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                fetchPrayerTimes(it.latitude, it.longitude)
            }
        }
    }
    private fun fetchPrayerTimes(latitude: Double, longitude: Double) {
        val prayerTimeApi = RetrofitInstance.api

        coroutineScope.launch {
            try {
                val prayerTimesResponse = prayerTimeApi.getPrayerTimes(latitude, longitude)
                val prayerTimes = prayerTimesResponse.data.timings
                println("prayer times $prayerTimes")
            } catch (e: Exception) {
                // Handle the error
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up resources
    }


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

}