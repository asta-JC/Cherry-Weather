package com.asta.cherryweather.ui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AcUnit
import androidx.compose.material.icons.rounded.BlurOn
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.CloudQueue
import androidx.compose.material.icons.rounded.Grain
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.QuestionMark
import androidx.compose.material.icons.rounded.Thunderstorm
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WbCloudy
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp
import com.asta.cherryweather.data.model.TemperatureUnit
import com.asta.cherryweather.data.model.WeatherResponse
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

object WeatherUtils {

    /**
     * Standard Google Material Symbols Rainy vector icon (cloud with rain drops).
     */
    val CloudRainIcon: ImageVector by lazy {
        val pathString = "M13.95,21.9q-0.375,0.2 -0.762,0.063t-0.588,-0.513l-1.5,-3q-0.2,-0.375 -0.062,-0.762t0.512,-0.588t0.763,-0.062t0.587,0.512l1.5,3q0.2,0.375 0.063,0.763t-0.513,0.587m6,0q-0.375,0.2 -0.762,0.063t-0.588,-0.513l-1.5,-3q-0.2,-0.375 -0.062,-0.762t0.512,-0.588t0.763,-0.062t0.587,0.512l1.5,3q0.2,0.375 0.063,0.763t-0.513,0.587m-12,0q-0.375,0.2 -0.762,0.063T6.6,21.45l-1.5,-3q-0.2,-0.375 -0.062,-0.762t0.512,-0.588t0.763,-0.062t0.587,0.512l1.5,3q0.2,0.375 0.063,0.763t-0.513,0.587M7.5,16q-2.275,0 -3.887,-1.612T2,10.5q0,-2.075 1.375,-3.625t3.4,-1.825q0.8,-1.425 2.188,-2.238T12,2q2.25,0 3.913,1.438t2.012,3.587q1.725,0.15 2.9,1.425T22,11.5q0,1.875 -1.312,3.188T17.5,16z"
        ImageVector.Builder(
            name = "CloudRain",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).addPath(
            pathData = PathParser().parsePathString(pathString).toNodes(),
            fill = SolidColor(Color.White)
        ).build()
    }

    /**
     * Formats temperature double value with unit symbol (e.g. 24° or 75°).
     */
    fun formatTemperature(temp: Double?, unit: TemperatureUnit = TemperatureUnit.CELSIUS): String {
        if (temp == null) return "--°"
        return "${temp.roundToInt()}°"
    }

    /**
     * Returns human-readable description for Open-Meteo WMO weather code.
     */
    fun getWeatherDescription(weatherCode: Int?, isDay: Boolean = true): String {
        return when (weatherCode) {
            0 -> if (isDay) "Clear Sky" else "Clear Night"
            1 -> "Mainly Clear"
            2 -> "Partly Cloudy"
            3 -> "Overcast"
            45, 48 -> "Fog & Mist"
            51 -> "Light Drizzle"
            53 -> "Moderate Drizzle"
            55 -> "Dense Drizzle"
            56, 57 -> "Freezing Drizzle"
            61 -> "Slight Rain"
            63 -> "Moderate Rain"
            65 -> "Heavy Rain"
            66, 67 -> "Freezing Rain"
            71 -> "Slight Snow"
            73 -> "Moderate Snow"
            75 -> "Heavy Snow"
            77 -> "Snow Grains"
            80, 81, 82 -> "Rain Showers"
            85, 86 -> "Snow Showers"
            95 -> "Thunderstorm"
            96, 99 -> "Thunderstorm & Hail"
            else -> "Fair"
        }
    }

    /**
     * Maps WMO weather code to Compose Material ImageVector icon.
     */
    fun getWeatherIcon(weatherCode: Int?, isDay: Boolean = true): ImageVector {
        return when (weatherCode) {
            0 -> if (isDay) Icons.Rounded.WbSunny else Icons.Rounded.NightsStay
            1, 2 -> if (isDay) Icons.Rounded.WbCloudy else Icons.Rounded.CloudQueue
            3 -> Icons.Rounded.Cloud
            45, 48 -> Icons.Rounded.BlurOn
            51, 53, 55, 56, 57 -> CloudRainIcon
            61, 63, 65, 66, 67, 80, 81, 82 -> CloudRainIcon
            71, 73, 75, 77, 85, 86 -> Icons.Rounded.AcUnit
            95, 96, 99 -> Icons.Rounded.Thunderstorm
            else -> Icons.Rounded.QuestionMark
        }
    }

    /**
     * Converts a WeatherResponse between Metric (Celsius, km/h, mm) and Imperial (Fahrenheit, mph, inch)
     * instantly in-memory with zero network latency.
     */
    fun convertWeatherResponse(response: WeatherResponse, targetUnit: TemperatureUnit): WeatherResponse {
        val toFahrenheit = (targetUnit == TemperatureUnit.FAHRENHEIT)
        val convertTemp: (Double?) -> Double? = { t ->
            t?.let { if (toFahrenheit) (it * 9.0 / 5.0) + 32.0 else (it - 32.0) * 5.0 / 9.0 }
        }
        val convertSpeed: (Double?) -> Double? = { s ->
            s?.let { if (toFahrenheit) it * 0.621371 else it / 0.621371 }
        }
        val convertPrecip: (Double?) -> Double? = { p ->
            p?.let { if (toFahrenheit) it / 25.4 else it * 25.4 }
        }

        val updatedCurrent = response.current?.copy(
            temperature2m = convertTemp(response.current.temperature2m),
            apparentTemperature = convertTemp(response.current.apparentTemperature),
            windSpeed10m = convertSpeed(response.current.windSpeed10m),
            precipitation = convertPrecip(response.current.precipitation)
        )

        val updatedHourly = response.hourly?.copy(
            temperature2m = response.hourly.temperature2m?.map { convertTemp(it) ?: it },
            apparentTemperature = response.hourly.apparentTemperature?.map { convertTemp(it) ?: it },
            windSpeed10m = response.hourly.windSpeed10m?.map { convertSpeed(it) ?: it },
            precipitation = response.hourly.precipitation?.map { convertPrecip(it) ?: it }
        )

        val updatedDaily = response.daily?.copy(
            temperature2mMax = response.daily.temperature2mMax?.map { convertTemp(it) ?: it },
            temperature2mMin = response.daily.temperature2mMin?.map { convertTemp(it) ?: it },
            apparentTemperatureMax = response.daily.apparentTemperatureMax?.map { convertTemp(it) ?: it },
            apparentTemperatureMin = response.daily.apparentTemperatureMin?.map { convertTemp(it) ?: it },
            precipitationSum = response.daily.precipitationSum?.map { convertPrecip(it) ?: it },
            windSpeed10mMax = response.daily.windSpeed10mMax?.map { convertSpeed(it) ?: it }
        )

        return response.copy(
            current = updatedCurrent,
            hourly = updatedHourly,
            daily = updatedDaily
        )
    }


    /**
     * Formats date time string (e.g., 2026-03-31T12:00) into short hour string (e.g. "12:00" or "12 PM").
     */
    fun formatTime(timeString: String?): String {
        if (timeString.isNullOrBlank()) return "--:--"
        val str = timeString!!
        return try {
            val dt = if (str.contains("T")) {
                LocalDateTime.parse(str)
            } else {
                LocalDateTime.parse("${str}T00:00")
            }
            dt.format(DateTimeFormatter.ofPattern("HH:mm"))
        } catch (e: Exception) {
            str.takeLast(5)
        }
    }

    /**
     * Formats YYYY-MM-DD or ISO datetime into day name (e.g. "TODAY", "MON", "TUE").
     */
    fun formatDayName(dateString: String?, isFirstItem: Boolean = false): String {
        if (dateString.isNullOrBlank()) return "---"
        if (isFirstItem) return "Today"

        return try {
            val cleanDate = dateString.split("T").first()
            val date = LocalDate.parse(cleanDate)
            val today = LocalDate.now()
            if (date == today) {
                "Today"
            } else {
                date.dayOfWeek.name.take(3).lowercase(Locale.ROOT).replaceFirstChar { it.uppercase() }
            }
        } catch (e: Exception) {
            "---"
        }
    }

    /**
     * Formats current date for TopBar header (e.g. "Mon, Oct 24").
     */
    fun formatCurrentDate(): String {
        return try {
            LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, MMM d", Locale.US))
        } catch (e: Exception) {
            "Today"
        }
    }

    /**
     * Formats current date and live time for the specified timezone or system default.
     * E.g. "Sat, Oct 17 • 11:17 AM"
     */
    fun formatCurrentDateTime(timezone: String? = null): String {
        return try {
            val zoneId = if (!timezone.isNullOrBlank()) {
                try { ZoneId.of(timezone) } catch (e: Exception) { ZoneId.systemDefault() }
            } else {
                ZoneId.systemDefault()
            }
            val zonedDateTime = ZonedDateTime.now(zoneId)
            zonedDateTime.format(DateTimeFormatter.ofPattern("EEE, MMM d • h:mm a", Locale.US))
        } catch (e: Exception) {
            formatCurrentDate()
        }
    }

    /**
     * Formats detailed live date and time for Hero Card.
     * E.g. "Saturday, Oct 17 • 11:17 AM"
     */
    fun formatHeroDateTime(timezone: String? = null): String {
        return try {
            val zoneId = if (!timezone.isNullOrBlank()) {
                try { ZoneId.of(timezone) } catch (e: Exception) { ZoneId.systemDefault() }
            } else {
                ZoneId.systemDefault()
            }
            val zonedDateTime = ZonedDateTime.now(zoneId)
            zonedDateTime.format(DateTimeFormatter.ofPattern("EEEE, MMM d • h:mm a", Locale.US))
        } catch (e: Exception) {
            formatCurrentDate()
        }
    }
}

