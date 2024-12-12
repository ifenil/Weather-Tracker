package com.weather.tracker.weathertracker

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val sharedPreferencesManager: SharedPreferencesManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _weather = mutableStateOf<WeatherResponse?>(null)
    val weather: State<WeatherResponse?> = _weather

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    init {
        val savedCity = sharedPreferencesManager.getSavedCity()
        savedCity?.let {
            getWeather(it)
        }
    }

    fun getWeather(city: String) {
        _loading.value = true
        viewModelScope.launch {
            val result = weatherRepository.getWeather(city, context)
            _loading.value = false

            result.onSuccess { weatherResponse ->
                _weather.value = weatherResponse
                _error.value = null  // Clear any previous errors
            }.onFailure { throwable ->
                _weather.value = null
                _error.value = throwable.message
            }
        }
    }

    fun saveCity(city: String) {
        sharedPreferencesManager.saveCity(city)
        getWeather(city)
    }

    fun getSavedCity(): String? {
        return sharedPreferencesManager.getSavedCity()
    }
}