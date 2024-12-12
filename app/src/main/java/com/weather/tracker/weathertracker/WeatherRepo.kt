package com.weather.tracker.weathertracker

import android.content.Context
import android.util.Log
import retrofit2.Response
import javax.inject.Inject


class WeatherRepository @Inject constructor(
    private val weatherService: WeatherService
) {
    suspend fun getWeather(location: String, context: Context): Response<WeatherResponse> {
        Log.d("WeatherRepository", "Making API call for $location")

        val apiKey = context.getString(R.string.weather_api_key)
        val response = weatherService.getWeather(apiKey, location)

        // Log the response for debugging
        if (response.isSuccessful) {
            Log.d("WeatherRepository", "Response: ${response.body()}")
        } else {
            Log.e("WeatherRepository", "Error fetching weather: ${response.message()}")
        }
        return response
    }
}
