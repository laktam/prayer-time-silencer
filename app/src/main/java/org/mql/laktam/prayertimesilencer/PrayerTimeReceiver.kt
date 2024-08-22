package org.mql.laktam.prayertimesilencer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
/*
When the BroadcastReceiver is triggered, it will start the PrayerTimeService.
 If the service is already running, this command will essentially "relaunch" it,
  meaning it will restart the service, allowing it to fetch and schedule new prayer times.
If the service was stopped or not running for any reason, this will start it again.
 */
class PrayerTimeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // Start the service to fetch and schedule new prayer times
        val serviceIntent = Intent(context, PrayerTimeService::class.java)
        ContextCompat.startForegroundService(context, serviceIntent)
    }
}
