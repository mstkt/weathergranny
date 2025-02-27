@Entity(tableName = "weather_data")
data class WeatherEntity(
    @PrimaryKey
    val id: String,
    val temperature: Double,
    val timestamp: Long,
    val location: String,
    val conditions: String
)

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather_history ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastWeather(): WeatherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)

    @Query("DELETE FROM weather_history WHERE timestamp < :timestamp")
    suspend fun deleteOldEntries(timestamp: Long)
}

@Database(
    entities = [WeatherEntity::class],
    version = 1
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
} 