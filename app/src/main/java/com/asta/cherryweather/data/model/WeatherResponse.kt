package com.asta.cherryweather.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Open-Meteo forecast response root model.
 */
@JsonClass(generateAdapter = true)
data class WeatherResponse(
    @field:Json(name = "latitude") val latitude: Double? = null,
    @field:Json(name = "longitude") val longitude: Double? = null,
    @field:Json(name = "timezone") val timezone: String? = null,
    @field:Json(name = "current") val current: CurrentWeather? = null,
    @field:Json(name = "hourly") val hourly: HourlyForecast? = null,
    @field:Json(name = "daily") val daily: DailyForecast? = null
)

/**
 * Current weather metrics.
 */
@JsonClass(generateAdapter = true)
data class CurrentWeather(
    @field:Json(name = "time") val time: String? = null,
    @field:Json(name = "temperature_2m") val temperature2m: Double? = null,
    @field:Json(name = "apparent_temperature") val apparentTemperature: Double? = null,
    @field:Json(name = "relative_humidity_2m") val relativeHumidity2m: Int? = null,
    @field:Json(name = "is_day") val isDay: Int? = null,
    @field:Json(name = "precipitation") val precipitation: Double? = null,
    @field:Json(name = "weather_code") val weatherCode: Int? = null,
    @field:Json(name = "surface_pressure") val surfacePressure: Double? = null,
    @field:Json(name = "wind_speed_10m") val windSpeed10m: Double? = null,
    @field:Json(name = "uv_index") val uvIndex: Double? = null
)

/**
 * Hourly forecast metrics arrays.
 */
@JsonClass(generateAdapter = true)
data class HourlyForecast(
    @field:Json(name = "time") val time: List<String>? = null,
    @field:Json(name = "temperature_2m") val temperature2m: List<Double>? = null,
    @field:Json(name = "apparent_temperature") val apparentTemperature: List<Double>? = null,
    @field:Json(name = "relative_humidity_2m") val relativeHumidity2m: List<Int>? = null,
    @field:Json(name = "precipitation") val precipitation: List<Double>? = null,
    @field:Json(name = "weather_code") val weatherCode: List<Int>? = null,
    @field:Json(name = "surface_pressure") val surfacePressure: List<Double>? = null,
    @field:Json(name = "wind_speed_10m") val windSpeed10m: List<Double>? = null,
    @field:Json(name = "uv_index") val uvIndex: List<Double>? = null,
    @field:Json(name = "is_day") val isDay: List<Int>? = null
) {
    /**
     * Converts raw parallel array lists into a structured list of HourlyItem domain models.
     */
    fun toHourlyItems(limit: Int = 24): List<HourlyItem> {
        val times = time ?: return emptyList()
        val count = minOf(times.size, limit)
        val result = mutableListOf<HourlyItem>()

        for (i in 0 until count) {
            result.add(
                HourlyItem(
                    time = times[i],
                    temperature = temperature2m?.getOrNull(i),
                    apparentTemperature = apparentTemperature?.getOrNull(i),
                    humidity = relativeHumidity2m?.getOrNull(i),
                    precipitation = precipitation?.getOrNull(i),
                    weatherCode = weatherCode?.getOrNull(i),
                    surfacePressure = surfacePressure?.getOrNull(i),
                    windSpeed = windSpeed10m?.getOrNull(i),
                    uvIndex = uvIndex?.getOrNull(i),
                    isDay = isDay?.getOrNull(i) == 1
                )
            )
        }
        return result
    }
}

/**
 * Daily forecast metrics arrays.
 */
@JsonClass(generateAdapter = true)
data class DailyForecast(
    @field:Json(name = "time") val time: List<String>? = null,
    @field:Json(name = "weather_code") val weatherCode: List<Int>? = null,
    @field:Json(name = "temperature_2m_max") val temperature2mMax: List<Double>? = null,
    @field:Json(name = "temperature_2m_min") val temperature2mMin: List<Double>? = null,
    @field:Json(name = "apparent_temperature_max") val apparentTemperatureMax: List<Double>? = null,
    @field:Json(name = "apparent_temperature_min") val apparentTemperatureMin: List<Double>? = null,
    @field:Json(name = "sunrise") val sunrise: List<String>? = null,
    @field:Json(name = "sunset") val sunset: List<String>? = null,
    @field:Json(name = "uv_index_max") val uvIndexMax: List<Double>? = null,
    @field:Json(name = "precipitation_sum") val precipitationSum: List<Double>? = null,
    @field:Json(name = "wind_speed_10m_max") val windSpeed10mMax: List<Double>? = null
) {
    /**
     * Converts raw parallel array lists into a structured list of DailyItem domain models.
     */
    fun toDailyItems(limit: Int = 7): List<DailyItem> {
        val times = time ?: return emptyList()
        val count = minOf(times.size, limit)
        val result = mutableListOf<DailyItem>()

        for (i in 0 until count) {
            result.add(
                DailyItem(
                    date = times[i],
                    weatherCode = weatherCode?.getOrNull(i),
                    maxTemp = temperature2mMax?.getOrNull(i),
                    minTemp = temperature2mMin?.getOrNull(i),
                    maxApparentTemp = apparentTemperatureMax?.getOrNull(i),
                    minApparentTemp = apparentTemperatureMin?.getOrNull(i),
                    sunrise = sunrise?.getOrNull(i),
                    sunset = sunset?.getOrNull(i),
                    maxUvIndex = uvIndexMax?.getOrNull(i),
                    precipitationSum = precipitationSum?.getOrNull(i),
                    maxWindSpeed = windSpeed10mMax?.getOrNull(i)
                )
            )
        }
        return result
    }
}

/**
 * Domain item representing forecast for a single hour.
 */
data class HourlyItem(
    val time: String,
    val temperature: Double?,
    val apparentTemperature: Double?,
    val humidity: Int?,
    val precipitation: Double?,
    val weatherCode: Int?,
    val surfacePressure: Double?,
    val windSpeed: Double?,
    val uvIndex: Double?,
    val isDay: Boolean
)

/**
 * Domain item representing forecast for a single day.
 */
data class DailyItem(
    val date: String,
    val weatherCode: Int?,
    val maxTemp: Double?,
    val minTemp: Double?,
    val maxApparentTemp: Double?,
    val minApparentTemp: Double?,
    val sunrise: String?,
    val sunset: String?,
    val maxUvIndex: Double?,
    val precipitationSum: Double?,
    val maxWindSpeed: Double?
)
