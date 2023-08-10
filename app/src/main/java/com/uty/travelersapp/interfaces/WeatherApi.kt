package com.uty.travelersapp.interfaces

import com.uty.travelersapp.models.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("forecast")
    fun getForecastData(
        @Query("lat") lat: String,
        @Query("lon") lng: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "id"
    ): Call<WeatherResponse>
}