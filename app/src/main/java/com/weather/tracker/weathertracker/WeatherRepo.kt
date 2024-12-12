package com.weather.tracker.weathertracker

import android.content.Context
import android.util.Log
import retrofit2.Response
import javax.inject.Inject


class WeatherRepository @Inject constructor(
    private val weatherService: WeatherService
) {
    suspend fun getWeather(location: String, context: Context): Result<WeatherResponse?> {
        return try {
            // Check if there is an internet connection
            if (!NetworkUtils.isInternetAvailable(context)) {
                return Result.failure(Exception("No internet connection"))
            }

            val apiKey = context.getString(R.string.weather_api_key)
            val response = weatherService.getWeather(apiKey, location)

            if (response.isSuccessful) {
                // Return the weather data if the response is successful
                Result.success(response.body())
            } else {
                // Handle different API errors based on the status code
                when (response.code()) {
                    400 -> {
                        // City not found
                        Result.failure(Exception("400"))
                    }
                    else -> {
                        // Handle other API errors
                        Result.failure(Exception("404"))
                    }
                }
            }
        } catch (e: Exception) {
            // Catch any other errors, such as network issues or unexpected exceptions
            Result.failure(e)
        }
    }
}