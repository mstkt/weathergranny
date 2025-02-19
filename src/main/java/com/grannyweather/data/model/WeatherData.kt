data class WeatherData(
    val currentTemp: Double,
    val condition: WeatherCondition,
    val timestamp: Long = System.currentTimeMillis()
)

enum class WeatherCondition {
    SUNNY, CLOUDY, RAINY, SNOWY, WINDY;
    
    fun getGrannyAdvice(): String = when(this) {
        SUNNY -> "It's a beautiful day, dear! Perfect for hanging the laundry outside."
        CLOUDY -> "A bit gloomy today, sweetie. Take a warm sweater just in case!"
        RAINY -> "Don't forget your umbrella, love! And wear those waterproof shoes I got you."
        SNOWY -> "Bundle up warm, dearie! And wear those thick socks I knitted for you."
        WINDY -> "Hold onto your hat, sweetie! And make sure to zip up your jacket properly."
    }
} 