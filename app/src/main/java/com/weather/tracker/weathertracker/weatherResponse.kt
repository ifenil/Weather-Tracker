package com.weather.tracker.weathertracker;

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val location: Location,
    val current: Current
)

data class Location(
        val name: String,
        val region: String,
        val country: String
)

data class Condition(
    val text: String,
    val icon: String
)

data class Current(
    @SerializedName("temp_f") val temperature: Double,
    val humidity: Int,
    val condition: Condition,
    val uv: Double,
    @SerializedName("feelslike_f") val FeelsLike: Double
)

