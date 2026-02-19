package com.weathergranny.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weathergranny.data.model.UserSettings
import com.weathergranny.data.model.WeatherSnapshot
import com.weathergranny.data.repository.WeatherRepository
import com.weathergranny.domain.GrannyAdviceGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MainUiState(
    val weather: WeatherSnapshot? = null,
    val grannyMessage: String = "Loading weather advice...",
    val settings: UserSettings = UserSettings(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class MainViewModel(
    private val repository: WeatherRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState(settings = repository.getSettings()))
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun loadWeather() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            runCatching {
                repository.getTodayWeather()
            }.onSuccess { weather ->
                val settings = repository.getSettings()
                val comparison = repository.compareWithYesterday(weather.temperatureCelsius)
                val advice = GrannyAdviceGenerator.generateAdvice(
                    weather.condition,
                    comparison,
                    settings.adviceTone
                )

                if (comparison == null || kotlin.math.abs(comparison.delta) >= settings.thresholdDelta) {
                    repository.persistTodayAsYesterday(weather.temperatureCelsius)
                }

                _uiState.value = _uiState.value.copy(
                    weather = weather,
                    grannyMessage = advice,
                    settings = settings,
                    isLoading = false
                )
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = throwable.message ?: "Failed to load weather"
                )
            }
        }
    }

    fun saveSettings(settings: UserSettings) {
        repository.saveSettings(settings)
        _uiState.value = _uiState.value.copy(settings = settings)
    }

    class Factory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(repository) as T
        }
    }
}
