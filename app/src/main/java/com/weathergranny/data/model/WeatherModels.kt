package com.weathergranny.data.model

import kotlin.math.absoluteValue

enum class WeatherCondition {
    SUNNY,
    RAINY,
    CLOUDY,
    WINDY,
    SNOWY,
    UNKNOWN
}

data class WeatherSnapshot(
    val location: String,
    val temperatureCelsius: Double,
    val condition: WeatherCondition,
    val description: String,
    val timestampMillis: Long = System.currentTimeMillis()
)

data class TemperatureComparison(
    val today: Double,
    val yesterday: Double,
    val delta: Double
) {
    fun summary(unit: TemperatureUnit): String {
        val rounded = delta.absoluteValue.toInt()
        val suffix = unit.symbol
        return when {
            delta > 0 -> "$rounded$suffix warmer than yesterday"
            delta < 0 -> "$rounded$suffix colder than yesterday"
            else -> "same as yesterday"
        }
    }
}

enum class AdviceTone {
    PLAYFUL,
    CONCERNED,
    CARING
}

enum class TemperatureUnit(val symbol: String) {
    CELSIUS("°C"),
    FAHRENHEIT("°F")
}

data class UserSettings(
    val language: String = "English",
    val notificationHour: Int = 7,
    val notificationMinute: Int = 30,
    val notificationEnabledDays: Set<Int> = setOf(1, 2, 3, 4, 5, 6, 7),
    val adviceTone: AdviceTone = AdviceTone.CARING,
    val voiceNotificationsEnabled: Boolean = false,
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val thresholdDelta: Int = 2,
    val grannyAvatar: String = "classic",
    val backgroundTheme: String = "auto",
    val automaticLocation: Boolean = true,
    val manualLocation: String = "Istanbul"
)
