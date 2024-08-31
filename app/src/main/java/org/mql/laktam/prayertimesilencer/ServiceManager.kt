package org.mql.laktam.prayertimesilencer

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object ServiceManager {
    private const val PREFS_NAME = "PrayerTimeServicePrefs"
    private const val KEY_SERVICE_RUNNING = "service_running"
    private const val KEY_SILENCE_TIME = "silence_time"
    private val _isServiceRunning = MutableStateFlow(false)
    val isServiceRunning: StateFlow<Boolean> = _isServiceRunning

    var silenceTime = 30 //* 60 * 1000L

    fun initializeServiceState(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _isServiceRunning.value = sharedPreferences.getBoolean(KEY_SERVICE_RUNNING, false)
        // Load silence time from shared preferences or use default if not set
        initSilenceTime(context)
    }

    fun initSilenceTime(context: Context){
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        silenceTime = sharedPreferences.getInt(KEY_SILENCE_TIME, silenceTime)
    }

    fun setServiceRunning(context: Context, isRunning: Boolean) {
        _isServiceRunning.value = isRunning
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(KEY_SERVICE_RUNNING, isRunning).apply()
    }

    // Function to save silence time to shared preferences
    fun saveSilenceTime(context: Context, time: Int) {
        silenceTime = time
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt(KEY_SILENCE_TIME, time).apply()
    }
}
