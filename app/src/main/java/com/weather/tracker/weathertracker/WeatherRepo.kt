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
            if (!NetworkUtils.isInternetAvailable(context)) {
                return Result.failure(Exception("No internet connection"))
            }

            val apiKey = context.getString(R.string.weather_api_key)
            val response = weatherService.getWeather(apiKey, location)

            if (response.isSuccessful) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("API Error: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
