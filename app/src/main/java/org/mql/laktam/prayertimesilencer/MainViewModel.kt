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
    private val _prayerTimes = MutableStateFlow<List<Triple<String, String, String>>>(emptyList())
    val prayerTimes: StateFlow<List<Triple<String, String, String>>> = _prayerTimes.asStateFlow()
    val isServiceRunning: StateFlow<Boolean> = ServiceManager.isServiceRunning

    fun loadPrayerTimes(context: Context) {
        viewModelScope.launch {
            val sharedPreferences =
                context.getSharedPreferences("PrayerTimesPreferences", Context.MODE_PRIVATE)
            val timeStrings = sharedPreferences.getStringSet("scheduledTimes", setOf()) ?: setOf()

            val timeFormat = SimpleDateFormat("HH:mm",Locale.ENGLISH)//  Locale.getDefault()
            val silenceDuration = ServiceManager.silenceTime *  60 * 1000L

            // Map each string back to a Triple (name, start, end)
            val silenceTimes = timeStrings.map { timeString ->
                val parts = timeString.split(":")
                if (parts.size == 3) {
                    val name = parts[0]
                    val startTimeString = "${parts[1]}:${parts[2]}"
                    val startTime = timeFormat.parse(startTimeString)
                    val endTime = Date(startTime.time + silenceDuration)

                    Triple(name, startTimeString, timeFormat.format(endTime))
                } else {
                    Triple("Unknown", "00:00", "00:00")
                }
            }.sortedBy { it.second }

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