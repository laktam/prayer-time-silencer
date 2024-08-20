package org.mql.laktam.prayertimesilencer

data class PrayerTimesResponse (
    val data: Data
)

data class Data(
    val timings: Timings
)


data class Timings(
    val Fajr: String,
    val Sunrise: String,
    val Dhuhr: String,
    val Asr: String,
    val Sunset: String,
    val Maghrib: String,
    val Isha: String,
    val Imsak: String,
    val Midnight: String,
    val Firstthird: String,
    val Lastthird: String
)