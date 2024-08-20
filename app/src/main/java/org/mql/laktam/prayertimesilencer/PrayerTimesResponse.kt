package org.mql.laktam.prayertimesilencer

data class PrayerTimesResponse (
    val data: Data
)

data class Data(
    val timings: Timings
)

data class Timings(
    val fajr: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String
)