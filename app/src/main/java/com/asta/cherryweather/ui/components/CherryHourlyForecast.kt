package com.asta.cherryweather.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asta.cherryweather.data.model.HourlyItem
import com.asta.cherryweather.data.model.TemperatureUnit
import com.asta.cherryweather.ui.theme.BorderCrimson
import com.asta.cherryweather.ui.theme.CardDarkPlum
import com.asta.cherryweather.ui.theme.CardDarkPlumElevated
import com.asta.cherryweather.ui.theme.CherryBlossom
import com.asta.cherryweather.ui.theme.CherryBlossomSoft
import com.asta.cherryweather.ui.theme.PrimaryCherry
import com.asta.cherryweather.ui.theme.TextMuted
import com.asta.cherryweather.ui.theme.TextWhite
import com.asta.cherryweather.ui.utils.WeatherUtils

@Composable
fun CherryHourlyForecast(
    hourlyItems: List<HourlyItem>,
    unit: TemperatureUnit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
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
                text = "Hourly Forecast",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextWhite
                )
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(hourlyItems) { index, item ->
                val isNow = index == 0
                HourlyCard(
                    item = item,
                    unit = unit,
                    isNow = isNow
                )
            }
        }
    }
}

@Composable
private fun HourlyCard(
    item: HourlyItem,
    unit: TemperatureUnit,
    isNow: Boolean
) {
    val timeLabel = if (isNow) "Now" else WeatherUtils.formatTime(item.time)
    val icon = WeatherUtils.getWeatherIcon(item.weatherCode, item.isDay)

    val surfaceModifier = if (isNow) {
        Modifier
            .width(80.dp)
            .shadow(10.dp, RoundedCornerShape(22.dp), spotColor = PrimaryCherry, ambientColor = PrimaryCherry)
            .clip(RoundedCornerShape(22.dp))
    } else {
        Modifier
            .width(80.dp)
            .clip(RoundedCornerShape(22.dp))
    }

    Surface(
        modifier = surfaceModifier,
        color = if (isNow) CardDarkPlumElevated else CardDarkPlum.copy(alpha = 0.85f),
        shape = RoundedCornerShape(22.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isNow) 1.5.dp else 1.dp,
            color = if (isNow) PrimaryCherry else BorderCrimson
        )
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Time Text
            Text(
                text = timeLabel,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isNow) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.sp
                ),
                color = if (isNow) CherryBlossom else TextMuted
            )

            // Weather Icon
            Icon(
                imageVector = icon,
                contentDescription = "Hourly Weather",
                tint = if (isNow) PrimaryCherry else TextWhite,
                modifier = Modifier.size(26.dp)
            )

            // Temp Text
            Text(
                text = WeatherUtils.formatTemperature(item.temperature, unit),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextWhite
            )

            // Rain amount if any
            if ((item.precipitation ?: 0.0) > 0.0) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryCherry.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${item.precipitation}mm",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = CherryBlossomSoft
                    )
                }
            }
        }
    }
}
