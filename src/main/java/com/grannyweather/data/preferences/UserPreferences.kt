data class UserPreferences(
    val notificationHour: Int = 8,
    val notificationMinute: Int = 0,
    val useAutoLocation: Boolean = true,
    val grannyStyle: String = "Caring",
    val useCelsius: Boolean = true,
    val manualLocation: Location? = null
)

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object PreferencesKeys {
        val NOTIFICATION_HOUR = intPreferencesKey("notification_hour")
        val NOTIFICATION_MINUTE = intPreferencesKey("notification_minute")
        val USE_AUTO_LOCATION = booleanPreferencesKey("use_auto_location")
        val GRANNY_STYLE = stringPreferencesKey("granny_style")
        val USE_CELSIUS = booleanPreferencesKey("use_celsius")
    }

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data.map { preferences ->
        UserPreferences(
            notificationHour = preferences[PreferencesKeys.NOTIFICATION_HOUR] ?: 8,
            notificationMinute = preferences[PreferencesKeys.NOTIFICATION_MINUTE] ?: 0,
            useAutoLocation = preferences[PreferencesKeys.USE_AUTO_LOCATION] ?: true,
            grannyStyle = preferences[PreferencesKeys.GRANNY_STYLE] ?: "Caring",
            useCelsius = preferences[PreferencesKeys.USE_CELSIUS] ?: true
        )
    }

    suspend fun updateNotificationTime(hour: Int, minute: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATION_HOUR] = hour
            preferences[PreferencesKeys.NOTIFICATION_MINUTE] = minute
        }
    }

    // Additional preference update methods...
} 