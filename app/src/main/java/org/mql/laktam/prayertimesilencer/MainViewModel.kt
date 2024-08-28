package org.mql.laktam.prayertimesilencer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel : ViewModel() {
    private val _prayerTimes = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val prayerTimes: StateFlow<List<Pair<String, String>>> = _prayerTimes.asStateFlow()
    val isServiceRunning: StateFlow<Boolean> = ServiceManager.isServiceRunning

    fun loadPrayerTimes(context: Context) {
        viewModelScope.launch {
            val sharedPreferences = context.getSharedPreferences("PrayerTimesPreferences", Context.MODE_PRIVATE)
            val timeStrings = sharedPreferences.getStringSet("scheduledTimes", setOf()) ?: setOf()

            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val silenceDuration = ServiceManager.silenceTime

            val silenceTimes = timeStrings.map { timeString ->
                val startTime = timeFormat.parse(timeString)
                val endTime = Date(startTime.time + silenceDuration)

                timeString to timeFormat.format(endTime)
            }.sortedBy { it.first }

            _prayerTimes.value = silenceTimes
        }
    }

    fun initializeServiceState(context: Context) {
        ServiceManager.initializeServiceState(context)
    }

    fun setServiceRunning(context: Context, isRunning: Boolean) {
        ServiceManager.setServiceRunning(context, isRunning)
    }
}