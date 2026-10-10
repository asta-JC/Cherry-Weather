package com.asta.cherryweather.ui.utils

import com.asta.cherryweather.data.model.CurrentWeather
import com.asta.cherryweather.data.model.DailyForecast
import com.asta.cherryweather.data.model.HourlyForecast
import com.asta.cherryweather.data.model.WeatherResponse

object SampleData {
    val mockWeatherResponse = WeatherResponse(
        latitude = 35.6762,
        longitude = 139.6503,
        timezone = "Asia/Tokyo",
        current = CurrentWeather(
            time = "2026-03-31T12:00",
            temperature2m = 24.5,
            apparentTemperature = 23.0,
            relativeHumidity2m = 68,
            isDay = 1,
            precipitation = 1.2,
            weatherCode = 61, // Rain
            surfacePressure = 1013.2,
            windSpeed10m = 14.5,
            uvIndex = 6.2
        ),
        hourly = HourlyForecast(
            time = List(24) { index -> "2026-03-31T%02d:00".format(index) },
            temperature2m = List(24) { 20.0 + (it % 6) },
            apparentTemperature = List(24) { 19.0 + (it % 6) },
            relativeHumidity2m = List(24) { 60 + (it % 20) },
            precipitation = List(24) { if (it % 4 == 0) 1.5 else 0.0 },
            weatherCode = List(24) { if (it % 4 == 0) 61 else 1 },
            surfacePressure = List(24) { 1013.0 },
            windSpeed10m = List(24) { 12.0 },
            uvIndex = List(24) { 5.0 },
            isDay = List(24) { if (it in 6..18) 1 else 0 }
        ),
        daily = DailyForecast(
            time = listOf(
                "2026-03-31",
                "2026-04-01",
                "2026-04-02",
                "2026-04-03",
                "2026-04-04",
                "2026-04-05",
                "2026-04-06"
            ),
            weatherCode = listOf(61, 0, 2, 95, 71, 0, 3),
            temperature2mMax = listOf(26.0, 24.0, 22.0, 20.0, 18.0, 23.0, 25.0),
            temperature2mMin = listOf(18.0, 16.0, 15.0, 14.0, 12.0, 15.0, 17.0),
            apparentTemperatureMax = listOf(25.0, 23.0, 21.0, 19.0, 17.0, 22.0, 24.0),
            apparentTemperatureMin = listOf(17.0, 15.0, 14.0, 13.0, 11.0, 14.0, 16.0),
            sunrise = listOf("2026-03-31T05:32", "2026-04-01T05:30", "2026-04-02T05:29", "2026-04-03T05:27", "2026-04-04T05:26", "2026-04-05T05:24", "2026-04-06T05:23"),
            sunset = listOf("2026-03-31T18:01", "2026-04-01T18:02", "2026-04-02T18:03", "2026-04-03T18:04", "2026-04-04T18:05", "2026-04-05T18:06", "2026-04-06T18:07"),
            uvIndexMax = listOf(6.2, 7.5, 5.0, 4.0, 6.0, 8.0, 7.0),
            precipitationSum = listOf(2.4, 0.0, 0.5, 12.0, 3.0, 0.0, 0.0),
            windSpeed10mMax = listOf(18.0, 12.0, 15.0, 28.0, 14.0, 10.0, 11.0)
        )
    )
}
