interface WeatherApiService {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String = "455b10606002a22eb570f12f4e0caee8",
        @Query("units") units: String = "metric"
    ): WeatherResponse
}

data class WeatherResponse(
    val main: Main,
    val weather: List<Weather>
) {
    data class Main(
        val temp: Double
    )
    
    data class Weather(
        val id: Int,
        val main: String,
        val description: String
    )
}

// Weather Repository Implementation
class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApiService,
    private val locationManager: LocationManager
) : WeatherRepository {
    
    override suspend fun getCurrentWeather(): WeatherData {
        val location = locationManager.getLastLocation()
        val response = api.getCurrentWeather(
            lat = location.latitude,
            lon = location.longitude
        )
        
        return WeatherData(
            currentTemp = response.main.temp,
            condition = response.weather.first().toWeatherCondition()
        )
    }
} 