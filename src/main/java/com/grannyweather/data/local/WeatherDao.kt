@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather_data WHERE location = :location AND timestamp >= :timestamp")
    suspend fun getRecentWeather(location: String, timestamp: Long): List<WeatherEntity>

    @Query("SELECT * FROM weather_data WHERE location = :location ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastWeather(location: String): WeatherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)

    @Query("DELETE FROM weather_data WHERE timestamp < :timestamp")
    suspend fun deleteOldData(timestamp: Long)
} 