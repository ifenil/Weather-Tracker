package com.weather.tracker.weathertracker

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("WeatherAppPreferences", Context.MODE_PRIVATE)

    private val cityKey = "saved_city"

    // Save the city name
    fun saveCity(city: String) {
        sharedPreferences.edit().putString(cityKey, city).apply()
    }

    // Get the saved city name
    fun getSavedCity(): String? {
        return sharedPreferences.getString(cityKey, null)
    }
}