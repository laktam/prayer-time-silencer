package org.mql.laktam.prayertimesilencer

import android.content.Context

//object ServiceManager {
//    var isServiceRunning: Boolean = false
//}
object ServiceManager {
    private const val PREFS_NAME = "PrayerTimeServicePrefs"
    private const val KEY_SERVICE_RUNNING = "service_running"
    var silenceTime = 2  * 60 * 1000L

    fun isServiceRunning(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(KEY_SERVICE_RUNNING, false)
    }

    fun setServiceRunning(context: Context, isRunning: Boolean) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(KEY_SERVICE_RUNNING, isRunning).apply()
    }
}