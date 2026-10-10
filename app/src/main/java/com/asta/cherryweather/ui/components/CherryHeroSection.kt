package com.asta.cherryweather.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asta.cherryweather.data.model.CurrentWeather
import com.asta.cherryweather.data.model.DailyItem
import com.asta.cherryweather.data.model.TemperatureUnit
import com.asta.cherryweather.ui.theme.BorderCrimson
import com.asta.cherryweather.ui.theme.CardDarkPlum
import com.asta.cherryweather.ui.theme.CardDarkPlumElevated
import com.asta.cherryweather.ui.theme.GradientEnd
import com.asta.cherryweather.ui.theme.GradientStart
import com.asta.cherryweather.ui.theme.PrimaryCherry
import com.asta.cherryweather.ui.theme.TextMuted
import com.asta.cherryweather.ui.theme.TextWhite
import com.asta.cherryweather.ui.utils.WeatherUtils

@Composable
fun CherryHeroSection(
    currentWeather: CurrentWeather?,
    todayDaily: DailyItem?,
    unit: TemperatureUnit,
    modifier: Modifier = Modifier
) {
    val temp = currentWeather?.temperature2m
    val apparentTemp = currentWeather?.apparentTemperature
    val code = currentWeather?.weatherCode
    val isDay = currentWeather?.isDay == 1

    val description = WeatherUtils.getWeatherDescription(code, isDay)
    val icon = WeatherUtils.getWeatherIcon(code, isDay)

    val maxTemp = todayDaily?.maxTemp
    val minTemp = todayDaily?.minTemp

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = CardDarkPlum,
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderCrimson)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Weather Icon Badge
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .shadow(16.dp, CircleShape, spotColor = PrimaryCherry, ambientColor = PrimaryCherry)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(GradientStart, GradientEnd)
                        )
                    )
                    .border(1.5.dp, Color.White.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Current Weather Condition",
                    tint = TextWhite,
                    modifier = Modifier.size(36.dp)
                )
            }

            // Temperature Hero Display
            Text(
                text = WeatherUtils.formatTemperature(temp, unit),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 72.sp,
                    lineHeight = 76.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryCherry
                )
            )

            // Weather Condition Description
            Text(
                text = description,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.0.sp,
                    color = TextWhite
                )
            )

            // Feels Like & High/Low Row
            Row(
                modifier = Modifier.padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Feels like badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardDarkPlumElevated)
                        .border(1.dp, BorderCrimson, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "FEELS ${WeatherUtils.formatTemperature(apparentTemp, unit)}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextMuted,
                            letterSpacing = 1.0.sp
                        )
                    )
                }

                // High / Low badge
                if (maxTemp != null && minTemp != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardDarkPlumElevated)
                            .border(1.dp, BorderCrimson, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "H: ${WeatherUtils.formatTemperature(maxTemp, unit)}  L: ${WeatherUtils.formatTemperature(minTemp, unit)}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextWhite,
                                letterSpacing = 1.0.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
