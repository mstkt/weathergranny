package com.weathergranny.data.local

import android.content.Context
import android.content.SharedPreferences
import com.weathergranny.data.model.AdviceTone
import com.weathergranny.data.model.TemperatureUnit
import com.weathergranny.data.model.UserSettings

class PreferenceStore(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun getYesterdayTemperature(): Double? {
        return if (prefs.contains(KEY_YESTERDAY_TEMP)) {
            prefs.getFloat(KEY_YESTERDAY_TEMP, 0f).toDouble()
        } else {
            null
        }
    }

    fun setYesterdayTemperature(value: Double) {
        prefs.edit().putFloat(KEY_YESTERDAY_TEMP, value.toFloat()).apply()
    }

    fun getUserSettings(): UserSettings {
        return UserSettings(
            language = prefs.getString(KEY_LANGUAGE, "English") ?: "English",
            notificationHour = prefs.getInt(KEY_NOTIFICATION_HOUR, 7),
            notificationMinute = prefs.getInt(KEY_NOTIFICATION_MINUTE, 30),
            adviceTone = AdviceTone.valueOf(
                prefs.getString(KEY_ADVICE_TONE, AdviceTone.CARING.name) ?: AdviceTone.CARING.name
            ),
            voiceNotificationsEnabled = prefs.getBoolean(KEY_VOICE_NOTIFICATIONS, false),
            temperatureUnit = TemperatureUnit.valueOf(
                prefs.getString(KEY_TEMPERATURE_UNIT, TemperatureUnit.CELSIUS.name)
                    ?: TemperatureUnit.CELSIUS.name
            ),
            thresholdDelta = prefs.getInt(KEY_THRESHOLD_DELTA, 2),
            grannyAvatar = prefs.getString(KEY_GRANNY_AVATAR, "classic") ?: "classic",
            backgroundTheme = prefs.getString(KEY_BACKGROUND_THEME, "auto") ?: "auto",
            automaticLocation = prefs.getBoolean(KEY_AUTOMATIC_LOCATION, true),
            manualLocation = prefs.getString(KEY_MANUAL_LOCATION, "Istanbul") ?: "Istanbul"
        )
    }

    fun saveUserSettings(settings: UserSettings) {
        prefs.edit()
            .putString(KEY_LANGUAGE, settings.language)
            .putInt(KEY_NOTIFICATION_HOUR, settings.notificationHour)
            .putInt(KEY_NOTIFICATION_MINUTE, settings.notificationMinute)
            .putString(KEY_ADVICE_TONE, settings.adviceTone.name)
            .putBoolean(KEY_VOICE_NOTIFICATIONS, settings.voiceNotificationsEnabled)
            .putString(KEY_TEMPERATURE_UNIT, settings.temperatureUnit.name)
            .putInt(KEY_THRESHOLD_DELTA, settings.thresholdDelta)
            .putString(KEY_GRANNY_AVATAR, settings.grannyAvatar)
            .putString(KEY_BACKGROUND_THEME, settings.backgroundTheme)
            .putBoolean(KEY_AUTOMATIC_LOCATION, settings.automaticLocation)
            .putString(KEY_MANUAL_LOCATION, settings.manualLocation)
            .apply()
    }

    companion object {
        private const val PREF_NAME = "weather_granny_prefs"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_YESTERDAY_TEMP = "yesterday_temp"
        private const val KEY_NOTIFICATION_HOUR = "notification_hour"
        private const val KEY_NOTIFICATION_MINUTE = "notification_minute"
        private const val KEY_ADVICE_TONE = "advice_tone"
        private const val KEY_VOICE_NOTIFICATIONS = "voice_notifications"
        private const val KEY_TEMPERATURE_UNIT = "temperature_unit"
        private const val KEY_THRESHOLD_DELTA = "threshold_delta"
        private const val KEY_GRANNY_AVATAR = "granny_avatar"
        private const val KEY_BACKGROUND_THEME = "background_theme"
        private const val KEY_AUTOMATIC_LOCATION = "automatic_location"
        private const val KEY_MANUAL_LOCATION = "manual_location"
    }
}
