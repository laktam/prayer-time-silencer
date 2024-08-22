package org.mql.laktam.prayertimesilencer

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Timer
import kotlin.concurrent.timerTask


class PrayerTimeService : Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val CHANNEL_ID = "PrayerTimeServiceChannel"
    private val NOTIFICATION_ID = 1
    private val networkMonitor by lazy { NetworkMonitor(this) }

    override fun onCreate() {
        super.onCreate()
        // Initialize service
        createNotificationChannel()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        ServiceManager.isServiceRunning = true

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("service started")
        startForegroundService()
        getLocation()
        scheduleDailyPrayerTimes()
//        scheduleDailyLocationCheck()
        return START_STICKY
    }

    private fun scheduleDailyPrayerTimes() {
        // Cancel any existing alarm
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, PrayerTimeReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Schedule the alarm to trigger at midnight every day
        val calendar = Calendar.getInstance()
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 1)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_MONTH, 1)  // Trigger tomorrow at midnight
        }

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
    private fun scheduleDailyLocationCheck() {
        val calendar = Calendar.getInstance()
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 1)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // If the time is already passed for today, schedule for tomorrow
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        val delay = calendar.timeInMillis - System.currentTimeMillis()
        val dayInMillis = 24 * 60 * 60 * 1000L  // 24 hours in milliseconds

        Timer().schedule(timerTask {
            getLocation()
        }, delay, dayInMillis)
    }

    // the permission is checked in ActivationButton before launching the service
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                fetchPrayerTimes(it.latitude, it.longitude)
            }
        }
    }
    private fun fetchPrayerTimes(latitude: Double, longitude: Double) {
        if (networkMonitor.isInternetAvailable()) {
            val prayerTimeApi = RetrofitInstance.api
            coroutineScope.launch {
                try {
                    val prayerTimesResponse = prayerTimeApi.getPrayerTimes(latitude, longitude)
                    val prayerTimes = prayerTimesResponse.data.timings
                    schedulePrayerTimeSilence(this@PrayerTimeService, prayerTimes)
                } catch (e: Exception) {
                    println("Error fetching prayer times: ${e.message}")
                    retryFetchPrayerTimes(latitude, longitude)
                }
            }
        } else {
            println("No internet connection. Will retry once connection is restored.")
            // Start monitoring for network changes and retry fetching prayer times when the internet is back
            retryFetchPrayerTimes(latitude, longitude)
        }
    }
    private fun retryFetchPrayerTimes(latitude: Double, longitude: Double) {
        networkMonitor.startMonitoring {
            // Retry fetching prayer times when internet is back
            fetchPrayerTimes(latitude, longitude)
        }
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        ServiceManager.isServiceRunning = false
    }
    private fun schedulePrayerTimeSilence(context: Context, prayerTimes: Timings) {
//        val fajrTime = parseTime(prayerTimes.Fajr)
//        val dhuhrTime = parseTime(prayerTimes.Dhuhr)
//        val asrTime = parseTime(prayerTimes.Asr)
        val fajrTime = parseTime("23:42")
        val dhuhrTime = parseTime("23:50")
        val asrTime = parseTime("23:56")

        val maghribTime = parseTime(prayerTimes.Maghrib)
        val ishaTime = parseTime(prayerTimes.Isha)

        schedulePhoneSilence(context, fajrTime)
        schedulePhoneSilence(context, dhuhrTime)
        schedulePhoneSilence(context, asrTime)
        schedulePhoneSilence(context, maghribTime)
        schedulePhoneSilence(context, ishaTime)
    }

    private fun schedulePhoneSilence(context: Context, prayerTime: Date) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val currentTime = Calendar.getInstance().time
        val startDelay = prayerTime.time - currentTime.time
//        println("Start Delay $startDelay")
//        val endDelay = startDelay + 2 * 60 * 1000  // 2 minutes in milliseconds
        val endDelay = 2 * 60 * 1000L  // 2 minutes in milliseconds

//        println("Start Delay $endDelay")
        if (startDelay > 0) {
            Timer().schedule(timerTask {
                audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                println("Phone silenced at $prayerTime")

                Timer().schedule(timerTask {
                    audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                    println("Phone restored to normal mode 2 minutes after $prayerTime")
                }, endDelay)
            }, startDelay)
        } else {
            println("Scheduled prayer time has already passed.")
        }
    }


    private fun parseTime(timeString: String): Date {
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
            Calendar.getInstance().time
        }
    } catch (e: Exception) {
        println("##### error in parseTime ")
        e.printStackTrace()
        Calendar.getInstance().time
    }


}
// Required for Android O and above to create a notification channel
private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Prayer Time Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(serviceChannel)
    }
}
private fun startForegroundService() {
    val notification = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Prayer Time Service")
        .setContentText("Silencing your phone during prayer times.")
        .setSmallIcon(R.drawable.ic_notification)
        .build()

    startForeground(NOTIFICATION_ID, notification)
}

//    fun schedulePhoneSilence(context: Context, prayerTime: Date) {
//        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
//        val currentTime = Calendar.getInstance().time
//        println("prayer time $prayerTime.time")
//        val startDelay = prayerTime.time - currentTime.time
//        val endDelay = startDelay + 2 * 60 * 1000  // 2 minutes in milliseconds
//// Schedule the phone to be silenced at the start time
//        if (startDelay > 0) {
//            Timer().schedule(timerTask {
//                audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
//                println("Phone silenced at $prayerTime")
//
//                // Schedule the phone to return to normal mode at the end time
//                Timer().schedule(timerTask {
//                    audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
//                    println("Phone restored to normal mode 2 minutes after $prayerTime")
//                }, endDelay)
//            }, startDelay)
//        } else {
//            println("Scheduled prayer time has already passed.")
//        }
//    }

}