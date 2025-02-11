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
    private var prayerTimers: MutableList<Timer> = mutableListOf() // List to store Timer objects
    private var pendingIntents: MutableList<PendingIntent> = mutableListOf() // List to store PendingIntents

    override fun onCreate() {
        super.onCreate()
        // Initialize service
        createNotificationChannel()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        ServiceManager.setServiceRunning(this@PrayerTimeService, true)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("service started")
        startForegroundService()
        getLocation()
        scheduleDailyPrayerTimes()
        return START_STICKY
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        ServiceManager.setServiceRunning(this@PrayerTimeService, false)
        // Cancel all alarms
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        for (pendingIntent in pendingIntents) {
            alarmManager.cancel(pendingIntent)
        }
        // Cancel all timers
        for (timer in prayerTimers) {
            timer.cancel()
        }
        println("All alarms and timers cancelled.")
        deletePrayerTimes()

    }

    private fun scheduleDailyPrayerTimes() {
        // Cancel any existing alarm
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, PrayerTimeReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        pendingIntents.add(pendingIntent)
        // Schedule the alarm to trigger at midnight every day
        val calendar = Calendar.getInstance()
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 1)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_MONTH, 1)  // Trigger tomorrow at midnight
        }

//        alarmManager.setInexactRepeating(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            AlarmManager.INTERVAL_DAY,
//            pendingIntent
//        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
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
                    println(" fetched prayer times $prayerTimes")
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
    private fun schedulePrayerTimeSilence(context: Context, prayerTimes: Timings) {
        // Parse and save prayer times
        val prayerTimesList = listOf(
            "Fajr" to parseTime(prayerTimes.Fajr),
            "Dhuhr" to parseTime(prayerTimes.Dhuhr),
            "Asr" to parseTime(prayerTimes.Asr),
            "Maghrib" to parseTime(prayerTimes.Maghrib),
            "Isha" to parseTime(prayerTimes.Isha)
        )
        savePrayerTimes(prayerTimesList)

        // Schedule alarms
        for ((name, time) in prayerTimesList) {
            schedulePhoneSilence(context, time)
        }
    }
    private fun schedulePhoneSilence(context: Context, prayerTime: Date) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, PhoneSilenceReceiver::class.java)

        val startDelay = prayerTime.time - System.currentTimeMillis()
        val pendingIntent = PendingIntent.getBroadcast(context, startDelay.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
        pendingIntents.add(pendingIntent)

        println("Start delay: $startDelay")
        val endDelay = ServiceManager.silenceTime * 60 * 1000L

        if (startDelay > 0) {
            try {
                // Check if the API level is 31 or higher
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                    // Use setExactAndAllowWhileIdle to handle different power modes
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        prayerTime.time,
                        pendingIntent
                    )
                    println("Exact alarm scheduled at $prayerTime with setExactAndAllowWhileIdle")

                    } else {
                        throw SecurityException("Cannot schedule exact alarms.")
                    }
                } else {
                    // Assume the alarm can be scheduled for API levels below 31
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, prayerTime.time, pendingIntent)
                    println("Exact alarm scheduled at $prayerTime")
                }
            } catch (e: Exception) {
                println("Exact alarm failed: ${e.message}. Using Timer instead.")

                // Fallback to Timer
                val silenceTimer = Timer()
                silenceTimer.schedule(timerTask {
                    audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                    println("Phone silenced at $prayerTime using Timer")

                    val restoreTimer = Timer()
                    restoreTimer.schedule(timerTask {
                        audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                        println("Phone restored to normal mode using Timer")
                    }, endDelay)

                    prayerTimers.add(restoreTimer) // Store restore Timer
                }, startDelay)

                prayerTimers.add(silenceTimer) // Store silence Timer
            }
        } else {
            println("Scheduled prayer time has already passed.")
        }
    }

    private fun parseTime(timeString: String): Date {
    return try {
        // Parse the time string "HH:mm"
        val timeFormat = SimpleDateFormat("HH:mm",Locale.ENGLISH )//Locale.getDefault()
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
        .setContentText(getString(R.string.service_note))
        .setSmallIcon(android.R.drawable.ic_menu_info_details
        )
        .build()

    startForeground(NOTIFICATION_ID, notification)
}
private fun savePrayerTimes(prayerTimes: List<Pair<String, Date>>) {
    val sharedPreferences = getSharedPreferences("PrayerTimesPreferences", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    // use Locale.ENGLISH to not store numbers in arabic
    val timeFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)//Locale.getDefault()

    // Convert the list of Pair<String, Date> to a set of formatted strings
    val timeStrings = prayerTimes.map { (name, date) -> "$name:${timeFormat.format(date)}" }.toSet()
    editor.putStringSet("scheduledTimes", timeStrings)
    editor.apply()

    println("Prayer times saved: $timeStrings")

    // Notify the ViewModel to reload prayer times
    // prayerTimesUpdateReceiver is in main activity
    val intent = Intent("PRAYER_TIMES_UPDATED")
    sendBroadcast(intent)
}

    private fun deletePrayerTimes() {
        // Get the SharedPreferences instance
        val sharedPreferences = getSharedPreferences("PrayerTimesPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Remove the key that stores the prayer times
        editor.remove("scheduledTimes")
        editor.apply() // or editor.commit() if you want to ensure immediate saving

        println("Prayer times deleted")

        // Notify the ViewModel to reload or update the UI accordingly
        // prayerTimesUpdateReceiver is in main activity
        val intent = Intent("PRAYER_TIMES_UPDATED")
        sendBroadcast(intent)
    }

}