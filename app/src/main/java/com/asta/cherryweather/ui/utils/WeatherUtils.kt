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
import androidx.compose.ui.graphics.vector.ImageVector
import com.asta.cherryweather.data.model.TemperatureUnit
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

object WeatherUtils {

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
            0 -> if (isDay) "CLEAR SKY" else "CLEAR NIGHT"
            1 -> "MAINLY CLEAR"
            2 -> "PARTLY CLOUDY"
            3 -> "OVERCAST"
            45, 48 -> "FOG // MIST"
            51 -> "LIGHT DRIZZLE"
            53 -> "MODERATE DRIZZLE"
            55 -> "DENSE DRIZZLE"
            56, 57 -> "FREEZING DRIZZLE"
            61 -> "SLIGHT RAIN"
            63 -> "MODERATE RAIN"
            65 -> "HEAVY RAIN"
            66, 67 -> "FREEZING RAIN"
            71 -> "SLIGHT SNOW"
            73 -> "MODERATE SNOW"
            75 -> "HEAVY SNOW"
            77 -> "SNOW GRAINS"
            80, 81, 82 -> "RAIN SHOWERS"
            85, 86 -> "SNOW SHOWERS"
            95 -> "THUNDERSTORM"
            96, 99 -> "THUNDERSTORM // HAIL"
            else -> "UNKNOWN"
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
            51, 53, 55, 56, 57 -> Icons.Rounded.Grain
            61, 63, 65, 66, 67, 80, 81, 82 -> Icons.Rounded.WaterDrop
            71, 73, 75, 77, 85, 86 -> Icons.Rounded.AcUnit
            95, 96, 99 -> Icons.Rounded.Thunderstorm
            else -> Icons.Rounded.QuestionMark
        }
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
        if (isFirstItem) return "TODAY"

        return try {
            val cleanDate = dateString!!.split("T").first()
            val date = LocalDate.parse(cleanDate)
            val today = LocalDate.now()
            if (date == today) {
                "TODAY"
            } else {
                date.dayOfWeek.name.take(3).uppercase(Locale.ROOT)
            }
        } catch (e: Exception) {
            "---"
        }
    }

    /**
     * Formats current date for TopBar header (e.g. "MON, OCT 24").
     */
    fun formatCurrentDate(): String {
        return try {
            LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, MMM d", Locale.US)).uppercase(Locale.ROOT)
        } catch (e: Exception) {
            "TODAY"
        }
    }
}
