package com.asta.cherryweather.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asta.cherryweather.data.model.DailyItem
import com.asta.cherryweather.data.model.TemperatureUnit
import com.asta.cherryweather.ui.theme.BorderCrimson
import com.asta.cherryweather.ui.theme.CardDarkPlum
import com.asta.cherryweather.ui.theme.CardDarkPlumElevated
import com.asta.cherryweather.ui.theme.CherryBlossom
import com.asta.cherryweather.ui.theme.CherryBlossomSoft
import com.asta.cherryweather.ui.theme.GradientEnd
import com.asta.cherryweather.ui.theme.GradientStart
import com.asta.cherryweather.ui.theme.PrimaryCherry
import com.asta.cherryweather.ui.theme.TextMuted
import com.asta.cherryweather.ui.theme.TextWhite
import com.asta.cherryweather.ui.utils.WeatherUtils

@Composable
fun CherryDailyForecastCard(
    dailyItems: List<DailyItem>,
    unit: TemperatureUnit,
    modifier: Modifier = Modifier
) {
    if (dailyItems.isEmpty()) return

    // Calculate global week min and max temp for scaling gradient bar
    val weekMinTemp = dailyItems.mapNotNull { it.minTemp }.minOrNull() ?: 0.0
    val weekMaxTemp = dailyItems.mapNotNull { it.maxTemp }.maxOrNull() ?: 30.0
    val tempRange = (weekMaxTemp - weekMinTemp).coerceAtLeast(1.0)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        color = CardDarkPlum.copy(alpha = 0.90f),
        shape = RoundedCornerShape(28.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderCrimson)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
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
                    text = "7-Day Forecast",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextWhite
                    )
                )
            }

            dailyItems.take(7).forEachIndexed { index, item ->
                DailyRow(
                    item = item,
                    unit = unit,
                    tempRange = tempRange,
                    isFirst = index == 0
                )

                if (index < dailyItems.take(7).size - 1) {
                    HorizontalDivider(
                        color = BorderCrimson.copy(alpha = 0.25f),
                        thickness = 0.8.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyRow(
    item: DailyItem,
    unit: TemperatureUnit,
    tempRange: Double,
    isFirst: Boolean
) {
    val dayName = WeatherUtils.formatDayName(item.date, isFirstItem = isFirst)
    val icon = WeatherUtils.getWeatherIcon(item.weatherCode, isDay = true)

    val minT = item.minTemp ?: 0.0
    val maxT = item.maxTemp ?: 0.0

    // Fraction range inside mini temp bar
    val fillFraction = ((maxT - minT) / tempRange).toFloat().coerceIn(0.25f, 1f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Day Name
        Text(
            text = dayName,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (isFirst) FontWeight.Bold else FontWeight.Medium,
                color = if (isFirst) CherryBlossomSoft else TextWhite
            ),
            modifier = Modifier.width(64.dp)
        )

        // Weather Icon
        Icon(
            imageVector = icon,
            contentDescription = "Daily condition",
            tint = if (isFirst) PrimaryCherry else CherryBlossom,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Min Temp
        Text(
            text = WeatherUtils.formatTemperature(minT, unit),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = TextMuted
            ),
            modifier = Modifier.width(36.dp)
        )

        // Mini Cherry Gradient Temperature Bar
        Box(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(CircleShape)
                .background(CardDarkPlumElevated)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fillFraction)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(CherryBlossomSoft, GradientStart)
                        )
                    )
            )
        }

        // Max Temp
        Text(
            text = WeatherUtils.formatTemperature(maxT, unit),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextWhite
            ),
            modifier = Modifier
                .padding(start = 12.dp)
                .width(36.dp)
        )
    }
}
