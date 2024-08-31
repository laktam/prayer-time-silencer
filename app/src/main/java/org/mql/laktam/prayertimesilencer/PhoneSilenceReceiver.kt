package org.mql.laktam.prayertimesilencer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import java.util.Timer
import kotlin.concurrent.timerTask

class PhoneSilenceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // Check if the phone is already in silent mode
        if (audioManager.ringerMode != AudioManager.RINGER_MODE_SILENT) {
            // Save the current ringer mode
            val originalRingerMode = audioManager.ringerMode

            // Set the phone to silent mode
            audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
            println("Phone silenced by AlarmManager")

            // Schedule another alarm to restore the ringer mode
            scheduleRingerRestore(context, originalRingerMode, ServiceManager.silenceTime * 60 * 1000L)
        }
    }

    private fun scheduleRingerRestore(context: Context, originalRingerMode: Int, delayMillis: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val intent = Intent(context, RingerRestoreReceiver::class.java)
        intent.putExtra("originalRingerMode", originalRingerMode)
        val triggerAtMillis = System.currentTimeMillis() + delayMillis

        val pendingIntent = PendingIntent.getBroadcast(context,0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

//        val triggerAtMillis = System.currentTimeMillis() + delayMillis
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    // Use setExactAndAllowWhileIdle to wake up and execute in Doze mode
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                    println("Ringer restore scheduled with AlarmManager")
                } else {
                    throw SecurityException("Cannot schedule exact alarms.")
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
                println("Ringer restore scheduled with AlarmManager")
            }
        }catch (e: Exception){
            println("Exact alarm failed: ${e.message}. Using Timer instead.")
            val restoreTimer = Timer()
            restoreTimer.schedule(timerTask {
                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                println("Phone restored to normal mode using Timer")
            }, triggerAtMillis)
        }
    }
}
