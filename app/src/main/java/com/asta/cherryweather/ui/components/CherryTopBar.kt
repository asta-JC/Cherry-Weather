package com.asta.cherryweather.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.MotionPhotosAuto
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asta.cherryweather.data.model.TemperatureUnit
import com.asta.cherryweather.ui.theme.BorderCrimson
import com.asta.cherryweather.ui.theme.CardDarkPlum
import com.asta.cherryweather.ui.theme.CardDarkPlumElevated
import com.asta.cherryweather.ui.theme.CherryBlossom
import com.asta.cherryweather.ui.theme.CherryGlow
import com.asta.cherryweather.ui.theme.GradientEnd
import com.asta.cherryweather.ui.theme.GradientStart
import com.asta.cherryweather.ui.theme.PrimaryCherry
import com.asta.cherryweather.ui.theme.TextMuted
import com.asta.cherryweather.ui.theme.TextWhite
import com.asta.cherryweather.ui.utils.WeatherUtils
import kotlinx.coroutines.delay

@Composable
fun CherryTopBar(
    cityName: String?,
    unit: TemperatureUnit,
    animationsEnabled: Boolean,
    onSearchClick: () -> Unit,
    onRescanLocation: () -> Unit,
    onUnitToggle: () -> Unit,
    onAnimationToggle: () -> Unit,
    modifier: Modifier = Modifier,
    timezone: String? = null
) {
    var currentDateTimeText by remember(timezone) {
        mutableStateOf(WeatherUtils.formatCurrentDateTime(timezone))
    }

    LaunchedEffect(timezone) {
        while (true) {
            currentDateTimeText = WeatherUtils.formatCurrentDateTime(timezone)
            delay(10_000L)
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        color = CardDarkPlum.copy(alpha = 0.85f),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderCrimson)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Interactive Location Chip (Clickable to open search & change location)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(onClick = onSearchClick)
                    .padding(vertical = 4.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Cherry Glowing Brand Dot / Pin
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .shadow(8.dp, CircleShape, spotColor = PrimaryCherry, ambientColor = PrimaryCherry)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(GradientStart, GradientEnd)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🍒",
                        fontSize = 18.sp
                    )
                }

                Column(
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = (cityName ?: "Detecting..."),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = "Change Location",
                            tint = CherryBlossom,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = currentDateTimeText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // Action Buttons Cluster
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Rescan GPS / Locate Me
                IconButton(
                    onClick = onRescanLocation,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CardDarkPlumElevated)
                        .border(1.dp, BorderCrimson, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MyLocation,
                        contentDescription = "Rescan Current Location",
                        tint = CherryBlossom,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // 2. Temperature Unit Toggle (°C / °F)
                IconButton(
                    onClick = onUnitToggle,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CardDarkPlumElevated)
                        .border(1.dp, BorderCrimson, CircleShape)
                ) {
                    Text(
                        text = if (unit == TemperatureUnit.CELSIUS) "°C" else "°F",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = PrimaryCherry
                    )
                }

                // 4. Animation Toggle (Sakura particles)
                val animBgColor by animateColorAsState(
                    targetValue = if (animationsEnabled) PrimaryCherry.copy(alpha = 0.25f) else CardDarkPlumElevated,
                    label = "AnimBgColor"
                )
                val animIconColor by animateColorAsState(
                    targetValue = if (animationsEnabled) CherryBlossom else TextMuted,
                    label = "AnimIconColor"
                )

                IconButton(
                    onClick = onAnimationToggle,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(animBgColor)
                        .border(
                            1.dp,
                            if (animationsEnabled) PrimaryCherry.copy(alpha = 0.6f) else BorderCrimson,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (animationsEnabled) Icons.Rounded.AutoAwesome else Icons.Rounded.MotionPhotosAuto,
                        contentDescription = "Toggle Animations",
                        tint = animIconColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
