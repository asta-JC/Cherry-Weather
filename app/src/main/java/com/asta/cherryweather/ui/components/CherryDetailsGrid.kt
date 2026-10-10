package com.asta.cherryweather.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.Compress
import androidx.compose.material.icons.rounded.Grain
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.WbTwilight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asta.cherryweather.data.model.CurrentWeather
import com.asta.cherryweather.data.model.DailyItem
import com.asta.cherryweather.data.model.TemperatureUnit
import com.asta.cherryweather.ui.theme.BorderCrimson
import com.asta.cherryweather.ui.theme.CardDarkPlum
import com.asta.cherryweather.ui.theme.PrimaryCherry
import com.asta.cherryweather.ui.theme.TextMuted
import com.asta.cherryweather.ui.theme.TextWhite
import com.asta.cherryweather.ui.utils.WeatherUtils
import kotlin.math.roundToInt

@Composable
fun CherryDetailsGrid(
    currentWeather: CurrentWeather?,
    todayDaily: DailyItem?,
    unit: TemperatureUnit,
    modifier: Modifier = Modifier
) {
    val humidityStr = currentWeather?.relativeHumidity2m?.let { "$it%" } ?: "--%"

    val windSpeedVal = currentWeather?.windSpeed10m
    val windUnitStr = if (unit == TemperatureUnit.FAHRENHEIT) "mph" else "km/h"
    val windSpeedStr = windSpeedVal?.let { "${it.roundToInt()} $windUnitStr" } ?: "-- $windUnitStr"

    val uvVal = currentWeather?.uvIndex ?: todayDaily?.maxUvIndex
    val uvStr = uvVal?.let {
        val level = when {
            it <= 2.0 -> "LOW"
            it <= 5.0 -> "MODERATE"
            it <= 7.0 -> "HIGH"
            it <= 10.0 -> "VERY HIGH"
            else -> "EXTREME"
        }
        "${it.roundToInt()} // $level"
    } ?: "--"

    val precipVal = currentWeather?.precipitation ?: todayDaily?.precipitationSum
    val precipUnitStr = if (unit == TemperatureUnit.FAHRENHEIT) "in" else "mm"
    val precipStr = precipVal?.let { "$it $precipUnitStr" } ?: "0 $precipUnitStr"

    val sunriseTime = todayDaily?.sunrise?.let { WeatherUtils.formatTime(it) } ?: "--:--"
    val sunsetTime = todayDaily?.sunset?.let { WeatherUtils.formatTime(it) } ?: "--:--"
    val sunStr = "$sunriseTime / $sunsetTime"

    val pressureVal = currentWeather?.surfacePressure
    val pressureStr = pressureVal?.let { "${it.roundToInt()} hPa" } ?: "-- hPa"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Section Title
        Text(
            text = "ATMOSPHERIC METRICS",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.0.sp,
                color = TextMuted
            ),
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        // Row 1: Humidity & Wind
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DetailCard(
                label = "HUMIDITY",
                value = humidityStr,
                icon = Icons.Rounded.WaterDrop,
                modifier = Modifier.weight(1f)
            )
            DetailCard(
                label = "WIND SPEED",
                value = windSpeedStr,
                icon = Icons.Rounded.Air,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: UV Index & Precipitation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DetailCard(
                label = "UV INDEX",
                value = uvStr,
                icon = Icons.Rounded.WbSunny,
                modifier = Modifier.weight(1f)
            )
            DetailCard(
                label = "PRECIPITATION",
                value = precipStr,
                icon = Icons.Rounded.Grain,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 3: Sunrise/Sunset & Pressure
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DetailCard(
                label = "SUNRISE / SUNSET",
                value = sunStr,
                icon = Icons.Rounded.WbTwilight,
                modifier = Modifier.weight(1f)
            )
            DetailCard(
                label = "PRESSURE",
                value = pressureStr,
                icon = Icons.Rounded.Compress,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DetailCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = CardDarkPlum,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderCrimson)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = PrimaryCherry,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    ),
                    color = TextMuted
                )
            }

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            )
        }
    }
}
