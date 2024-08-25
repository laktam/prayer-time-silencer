package org.mql.laktam.prayertimesilencer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Handler
import android.os.Looper

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

            // Use a Handler to restore the ringer mode after the silence time
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                // Restore the phone's original ringer mode
                audioManager.ringerMode = originalRingerMode
                println("Phone restored to normal mode by AlarmManager")
            }, ServiceManager.silenceTime)
        }
    }
}


//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.media.AudioManager
//import android.widget.Toast
//import java.util.Timer
//import kotlin.concurrent.timerTask
///*
//BroadcastReceiver:
//
//A component that responds to broadcast messages from other applications or from the system itself.
// In this case, PhoneSilenceReceiver responds to an alarm broadcast.
// */
//class PhoneSilenceReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context, intent: Intent?) {
//        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
//        audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
//
//        println("Phone silenced by AlarmManager")
//
//        // Schedule to turn the ringer mode back to normal
//        Timer().schedule(timerTask {
//            audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
//            println("Phone restored to normal mode by AlarmManager")
//        }, ServiceManager.silenceTime)
//    }
//}