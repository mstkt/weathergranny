class MainViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            updateWeatherData()
        }
    }

    private suspend fun updateWeatherData() {
        try {
            val currentWeather = weatherRepository.getCurrentWeather()
            val yesterdayWeather = preferencesRepository.getYesterdayWeather()
            
            val tempDiff = currentWeather.currentTemp - (yesterdayWeather?.currentTemp ?: currentWeather.currentTemp)
            val grannyMessage = "${GrannyAdvice.getTemperatureAdvice(tempDiff)}\n${GrannyAdvice.getRandomAdvice()}"
            
            _uiState.value = WeatherUiState.Success(
                currentWeather = currentWeather,
                grannyMessage = grannyMessage,
                tempDifference = tempDiff
            )
            
            // Store today's weather for tomorrow's comparison
            preferencesRepository.saveYesterdayWeather(currentWeather)
        } catch (e: Exception) {
            _uiState.value = WeatherUiState.Error("Oh dear! Granny couldn't check the weather. ${e.message}")
        }
    }
}

sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(
        val currentWeather: WeatherData,
        val grannyMessage: String,
        val tempDifference: Double
    ) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
} 