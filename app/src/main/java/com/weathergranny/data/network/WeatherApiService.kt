package com.weathergranny.data.network

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("v1/current.json")
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("aqi") aqi: String = "no"
    ): WeatherResponseDto
}

data class WeatherResponseDto(
    val location: LocationDto,
    val current: CurrentDto
)

data class LocationDto(
    val name: String
)

data class CurrentDto(
    val temp_c: Double,
    val condition: ConditionDto,
    val wind_kph: Double
)

data class ConditionDto(
    val text: String
)
