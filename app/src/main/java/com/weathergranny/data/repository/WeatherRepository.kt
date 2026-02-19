package com.weathergranny.data.repository

import com.weathergranny.data.local.PreferenceStore
import com.weathergranny.data.model.TemperatureComparison
import com.weathergranny.data.model.UserSettings
import com.weathergranny.data.model.WeatherCondition
import com.weathergranny.data.model.WeatherSnapshot
import com.weathergranny.data.network.WeatherApiService

interface WeatherRepository {
    suspend fun getTodayWeather(): WeatherSnapshot
    fun compareWithYesterday(todayTemperature: Double): TemperatureComparison?
    fun persistTodayAsYesterday(todayTemperature: Double)
    fun getSettings(): UserSettings
    fun saveSettings(settings: UserSettings)
}

class WeatherRepositoryImpl(
    private val apiService: WeatherApiService,
    private val apiKey: String,
    private val preferenceStore: PreferenceStore
) : WeatherRepository {

    override suspend fun getTodayWeather(): WeatherSnapshot {
        val settings = preferenceStore.getUserSettings()
        val locationQuery = if (settings.automaticLocation) "auto:ip" else settings.manualLocation
        return runCatching {
            val dto = apiService.getCurrentWeather(apiKey = apiKey, location = locationQuery)
            WeatherSnapshot(
                location = dto.location.name,
                temperatureCelsius = dto.current.temp_c,
                condition = mapCondition(dto.current.condition.text),
                description = dto.current.condition.text
            )
        }.getOrElse {
            WeatherSnapshot(
                location = settings.manualLocation,
                temperatureCelsius = 22.0,
                condition = WeatherCondition.CLOUDY,
                description = "Mock data (API unavailable)"
            )
        }
    }

    override fun compareWithYesterday(todayTemperature: Double): TemperatureComparison? {
        val yesterday = preferenceStore.getYesterdayTemperature() ?: return null
        return TemperatureComparison(
            today = todayTemperature,
            yesterday = yesterday,
            delta = todayTemperature - yesterday
        )
    }

    override fun persistTodayAsYesterday(todayTemperature: Double) {
        preferenceStore.setYesterdayTemperature(todayTemperature)
    }

    override fun getSettings(): UserSettings = preferenceStore.getUserSettings()

    override fun saveSettings(settings: UserSettings) = preferenceStore.saveUserSettings(settings)

    private fun mapCondition(text: String): WeatherCondition {
        val normalized = text.lowercase()
        return when {
            "rain" in normalized -> WeatherCondition.RAINY
            "sun" in normalized || "clear" in normalized -> WeatherCondition.SUNNY
            "cloud" in normalized || "overcast" in normalized -> WeatherCondition.CLOUDY
            "snow" in normalized || "sleet" in normalized -> WeatherCondition.SNOWY
            "wind" in normalized -> WeatherCondition.WINDY
            else -> WeatherCondition.UNKNOWN
        }
    }
}
