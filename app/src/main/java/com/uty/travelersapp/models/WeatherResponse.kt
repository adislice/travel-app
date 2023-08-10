package com.uty.travelersapp.models

data class WeatherResponse(
    val cod: String?,
    val message: Int?,
    val list: List<ForecastData>?
)

data class ForecastData(
    val dt: Long?,
    val main: MainData?,
    val weather: List<WeatherData>?
)

data class MainData(
    val temp: Double?,
)

data class WeatherData(
    val id: String?,
    val description: String?,
    val icon: String?,
    val main: String?
)
