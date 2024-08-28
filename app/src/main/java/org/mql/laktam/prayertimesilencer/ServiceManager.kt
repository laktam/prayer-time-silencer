package org.mql.laktam.prayertimesilencer

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object ServiceManager {
    private const val PREFS_NAME = "PrayerTimeServicePrefs"
    private const val KEY_SERVICE_RUNNING = "service_running"
    private val _isServiceRunning = MutableStateFlow(false)
    val isServiceRunning: StateFlow<Boolean> = _isServiceRunning

    var silenceTime = 30 * 60 * 1000L

    fun initializeServiceState(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _isServiceRunning.value = sharedPreferences.getBoolean(KEY_SERVICE_RUNNING, false)
    }

    fun setServiceRunning(context: Context, isRunning: Boolean) {
        _isServiceRunning.value = isRunning
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(KEY_SERVICE_RUNNING, isRunning).apply()
    }
}
//package org.mql.laktam.prayertimesilencer
//
//import android.content.Context
//
////object ServiceManager {
////    var isServiceRunning: Boolean = false
////}
//object ServiceManager {
//    private const val PREFS_NAME = "PrayerTimeServicePrefs"
//    private const val KEY_SERVICE_RUNNING = "service_running"
//    var silenceTime = 30  * 60 * 1000L
//
//    fun isServiceRunning(context: Context): Boolean {
//        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//        return sharedPreferences.getBoolean(KEY_SERVICE_RUNNING, false)
//    }
//
//    fun setServiceRunning(context: Context, isRunning: Boolean) {
//        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//        sharedPreferences.edit().putBoolean(KEY_SERVICE_RUNNING, isRunning).apply()
//    }
//}