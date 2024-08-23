package org.mql.laktam.prayertimesilencer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED && !ServiceManager.isServiceRunning) {
            // Start the PrayerTimeService after reboot if it wasn't started manually
            val serviceIntent = Intent(context, PrayerTimeService::class.java)
            ServiceManager.isServiceRunning = true
            ContextCompat.startForegroundService(context, serviceIntent)
        }
    }
}
