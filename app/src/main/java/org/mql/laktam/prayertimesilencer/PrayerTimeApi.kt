package org.mql.laktam.prayertimesilencer


import retrofit2.http.GET
import retrofit2.http.Query

interface PrayerTimeApi {
    @GET("timings")
    suspend fun getPrayerTimes(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): PrayerTimesResponse
}