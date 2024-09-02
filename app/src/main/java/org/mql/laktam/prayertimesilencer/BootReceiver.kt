package org.mql.laktam.prayertimesilencer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // Check if the intent action matches the boot completed action
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            // Initialize the service state from shared preferences
            ServiceManager.initializeServiceState(context)

            // Check if the service was running before the reboot
            if (ServiceManager.isServiceRunning.value) {
                // Start the PrayerTimeService if it was running before reboot
                val serviceIntent = Intent(context, PrayerTimeService::class.java)
                ContextCompat.startForegroundService(context, serviceIntent)
            }
        }
    }
}
