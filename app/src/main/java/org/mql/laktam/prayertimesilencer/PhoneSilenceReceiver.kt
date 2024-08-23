package org.mql.laktam.prayertimesilencer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.widget.Toast
import java.util.Timer
import kotlin.concurrent.timerTask
/*
BroadcastReceiver:

A component that responds to broadcast messages from other applications or from the system itself.
 In this case, PhoneSilenceReceiver responds to an alarm broadcast.
 */
class PhoneSilenceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
//        Toast.makeText(context, "Alarm received, silencing phone", Toast.LENGTH_SHORT).show()
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT

        println("Phone silenced by AlarmManager")

        // Schedule to turn the ringer mode back to normal after 8 minutes
        Timer().schedule(timerTask {
            audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
            println("Phone restored to normal mode after 8 minutes by AlarmManager")
        }, ServiceManager.silenceTime)
    }
}