package com.asta.cherryweather.data.remote

import com.asta.cherryweather.data.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service for Open-Meteo Forecast API.
 */
interface OpenMeteoApiService {

    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = CURRENT_PARAMS,
        @Query("hourly") hourly: String = HOURLY_PARAMS,
        @Query("daily") daily: String = DAILY_PARAMS,
        @Query("temperature_unit") temperatureUnit: String = "celsius",
        @Query("wind_speed_unit") windSpeedUnit: String = "kmh",
        @Query("precipitation_unit") precipitationUnit: String = "mm",
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = 7,
        @Query("forecast_hours") forecastHours: Int = 24
    ): Response<WeatherResponse>

    companion object {
        const val BASE_URL = "https://api.open-meteo.com/"

        const val CURRENT_PARAMS =
            "temperature_2m,relative_humidity_2m,apparent_temperature,is_day,precipitation,weather_code,surface_pressure,wind_speed_10m,uv_index"
        const val HOURLY_PARAMS =
            "temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,weather_code,surface_pressure,wind_speed_10m,uv_index,is_day"
        const val DAILY_PARAMS =
            "weather_code,temperature_2m_max,temperature_2m_min,apparent_temperature_max,apparent_temperature_min,sunrise,sunset,uv_index_max,precipitation_sum,wind_speed_10m_max"
    }
}
