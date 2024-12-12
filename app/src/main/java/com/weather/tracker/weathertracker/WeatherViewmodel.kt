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
        // Fetch the saved city and load weather data when the ViewModel is initialized
        val savedCity = sharedPreferencesManager.getSavedCity()
        savedCity?.let {
            getWeather(it, context)  // Fetch weather data for the saved city
        }
    }

    fun getWeather(city: String, context: Context) {
        _loading.value = true  // Set loading to true when starting the fetch
        _error.value = null    // Reset error state before making a new API call

        viewModelScope.launch {
            val response = weatherRepository.getWeather(city, context = context)
            _loading.value = false  // Set loading to false after the fetch is complete

            if (response.isSuccess) {
                val body = response.getOrNull()
                if (body != null) {
                    _weather.value = body
                    _error.value = null  // Clear any previous error if data is found
                } else {
                    // This case can be ignored if repository already handles "City Not Found"
                    _error.value = "City Not Found"
                }
            } else {
                // Handle failure based on the error returned by the repository
                val errorMessage = response.exceptionOrNull()?.message

                // Check for specific error message or status code
                if (errorMessage?.contains("No internet connection") == true) {
                    _error.value = "No Internet Connection"
                } else if (errorMessage?.contains("400") == true) {
                    _error.value = "City Not Found"
                } else {
                    _error.value = errorMessage ?: "Something Went Wrong"
                }
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