package org.mql.laktam.prayertimesilencer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager

class RingerRestoreReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val originalRingerMode = intent?.getIntExtra("originalRingerMode", AudioManager.RINGER_MODE_NORMAL)
        originalRingerMode?.let {
            audioManager.ringerMode = it
            println("Phone restored to normal mode by RingerRestoreReceiver")
        }
    }
}
