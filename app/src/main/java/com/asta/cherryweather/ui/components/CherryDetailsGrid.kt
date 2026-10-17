package com.asta.cherryweather.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.Compress
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.WbTwilight
import androidx.compose.material.icons.rounded.Grain
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asta.cherryweather.data.model.CurrentWeather
import com.asta.cherryweather.data.model.DailyItem
import com.asta.cherryweather.data.model.TemperatureUnit
import com.asta.cherryweather.ui.theme.BorderCrimson
import com.asta.cherryweather.ui.theme.CardDarkPlum
import com.asta.cherryweather.ui.theme.CardDarkPlumElevated
import com.asta.cherryweather.ui.theme.CherryBlossom
import com.asta.cherryweather.ui.theme.CherryBlossomSoft
import com.asta.cherryweather.ui.theme.CherryGold
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
    val uvCategory = when {
        uvVal == null -> "Unknown"
        uvVal <= 2.0 -> "Low"
        uvVal <= 5.0 -> "Moderate"
        uvVal <= 7.0 -> "High"
        uvVal <= 10.0 -> "Very High"
        else -> "Extreme"
    }
    val uvNumStr = uvVal?.let { "${it.roundToInt()}" } ?: "--"

    val precipVal = currentWeather?.precipitation ?: todayDaily?.precipitationSum
    val precipUnitStr = if (unit == TemperatureUnit.FAHRENHEIT) "in" else "mm"
    val precipStr = precipVal?.let { "$it $precipUnitStr" } ?: "0 $precipUnitStr"

    val sunriseTime = todayDaily?.sunrise?.let { WeatherUtils.formatTime(it) } ?: "--:--"
    val sunsetTime = todayDaily?.sunset?.let { WeatherUtils.formatTime(it) } ?: "--:--"
    val sunStr = "$sunriseTime  •  $sunsetTime"

    val pressureVal = currentWeather?.surfacePressure
    val pressureStr = pressureVal?.let { "${it.roundToInt()} hPa" } ?: "-- hPa"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Section Title
        Row(
            modifier = Modifier.padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(PrimaryCherry)
            )
            Text(
                text = "Weather Highlights",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextWhite
                )
            )
        }

        // Row 1: Humidity & Wind
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DetailCard(
                label = "Humidity",
                value = humidityStr,
                icon = Icons.Rounded.WaterDrop,
                modifier = Modifier.weight(1f)
            )
            DetailCard(
                label = "Wind Speed",
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
                label = "UV Index",
                value = uvNumStr,
                badgeText = uvCategory,
                icon = Icons.Rounded.WbSunny,
                modifier = Modifier.weight(1f)
            )
            DetailCard(
                label = "Precipitation",
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
                label = "Sunrise & Sunset",
                value = sunStr,
                icon = Icons.Rounded.WbTwilight,
                modifier = Modifier.weight(1f)
            )
            DetailCard(
                label = "Pressure",
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
    badgeText: String? = null,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = CardDarkPlum.copy(alpha = 0.85f),
        shape = RoundedCornerShape(22.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderCrimson)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = CherryBlossom,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = TextMuted
                        )
                    )
                }

                if (badgeText != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CardDarkPlumElevated)
                            .border(1.dp, BorderCrimson, RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CherryGold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            )
        }
    }
}
