package com.asta.cherryweather.ui.components

import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.rounded.Thermostat
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.asta.cherryweather.ui.theme.CherryBlossom
import com.asta.cherryweather.ui.theme.CherryBlossomSoft
import com.asta.cherryweather.ui.theme.GradientEnd
import com.asta.cherryweather.ui.theme.GradientStart
import com.asta.cherryweather.ui.theme.PrimaryCherry
import com.asta.cherryweather.ui.theme.TextMuted
import com.asta.cherryweather.ui.theme.TextWhite
import com.asta.cherryweather.ui.utils.WeatherUtils
import kotlinx.coroutines.delay

@Composable
fun CherryHeroSection(
    currentWeather: CurrentWeather?,
    todayDaily: DailyItem?,
    unit: TemperatureUnit,
    modifier: Modifier = Modifier,
    timezone: String? = null
) {
    val temp = currentWeather?.temperature2m
    val apparentTemp = currentWeather?.apparentTemperature
    val code = currentWeather?.weatherCode
    val isDay = currentWeather?.isDay == 1

    val description = WeatherUtils.getWeatherDescription(code, isDay)
    val icon = WeatherUtils.getWeatherIcon(code, isDay)

    val maxTemp = todayDaily?.maxTemp
    val minTemp = todayDaily?.minTemp
    val precipSum = todayDaily?.precipitationSum ?: currentWeather?.precipitation

    // Real-time ticking date & clock
    var heroDateTimeText by remember(timezone) {
        mutableStateOf(WeatherUtils.formatHeroDateTime(timezone))
    }

    LaunchedEffect(timezone) {
        while (true) {
            heroDateTimeText = WeatherUtils.formatHeroDateTime(timezone)
            delay(10_000L)
        }
    }

    // Gentle pulsing animation for the live status dot
    val infiniteTransition = rememberInfiniteTransition(label = "live_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

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
                .padding(vertical = 24.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Real-Time Live Status Pill with Date and Clock
            Surface(
                color = CardDarkPlumElevated.copy(alpha = 0.85f),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderCrimson.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Pulsing emerald indicator for live real-time status
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4ADE80).copy(alpha = pulseAlpha))
                    )

                    Text(
                        text = "LIVE NOW",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.sp,
                            letterSpacing = 1.sp,
                            color = Color(0xFF4ADE80)
                        )
                    )

                    Text(
                        text = "•",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = BorderCrimson
                        )
                    )

                    Text(
                        text = heroDateTimeText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = CherryBlossomSoft
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Weather Icon with soft glowing sakura badge
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .shadow(16.dp, CircleShape, spotColor = PrimaryCherry, ambientColor = PrimaryCherry)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(GradientStart, GradientEnd)
                        )
                    )
                    .border(1.5.dp, CherryBlossomSoft.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = "Weather condition",
                    tint = TextWhite,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Temperature Display
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = WeatherUtils.formatTemperature(temp, unit),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 80.sp,
                        lineHeight = 84.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                )
            }

            // Condition Title
            Text(
                text = description,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = CherryBlossomSoft
                )
            )

            // Frosted Badges Row (Feels Like, High/Low, Precipitation)
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Feels like badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardDarkPlumElevated)
                        .border(1.dp, BorderCrimson, RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Thermostat,
                            contentDescription = "Feels Like",
                            tint = PrimaryCherry,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Feels ${WeatherUtils.formatTemperature(apparentTemp, unit)}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = TextWhite
                            )
                        )
                    }
                }

                // High / Low badge
                if (maxTemp != null && minTemp != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(CardDarkPlumElevated)
                            .border(1.dp, BorderCrimson, RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "H: ${WeatherUtils.formatTemperature(maxTemp, unit)}  •  L: ${WeatherUtils.formatTemperature(minTemp, unit)}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = TextMuted
                            )
                        )
                    }
                }
            }
        }
    }
}
