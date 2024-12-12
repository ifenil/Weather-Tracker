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

    init {
        // Fetch the saved city and load weather data when the ViewModel is initialized
        val savedCity = sharedPreferencesManager.getSavedCity()
        savedCity?.let {
            getWeather(it, context)  // Fetch weather data for the saved city
        }
    }

    fun getWeather(city: String, context: Context) {
        _loading.value = true  // Set loading to true when starting the fetch
        viewModelScope.launch {
            val response = weatherRepository.getWeather(city, context = context)
            _loading.value = false  // Set loading to false after the fetch is complete
            if (response.isSuccessful) {
                _weather.value = response.body()
            }
        }
    }

    fun saveCity(city: String, context: Context) {
        sharedPreferencesManager.saveCity(city)
        getWeather(city, context = context)  // After saving the city, fetch the weather data for it
    }

    fun getSavedCity(): String? {
        return sharedPreferencesManager.getSavedCity()
    }
}