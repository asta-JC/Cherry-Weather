package com.asta.cherryweather.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.MotionPhotosAuto
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asta.cherryweather.data.model.TemperatureUnit
import com.asta.cherryweather.ui.theme.BorderCrimson
import com.asta.cherryweather.ui.theme.CardDarkPlum
import com.asta.cherryweather.ui.theme.CardDarkPlumElevated
import com.asta.cherryweather.ui.theme.PrimaryCherry
import com.asta.cherryweather.ui.theme.TextMuted
import com.asta.cherryweather.ui.theme.TextWhite
import com.asta.cherryweather.ui.utils.WeatherUtils

@Composable
fun CherryTopBar(
    cityName: String?,
    unit: TemperatureUnit,
    animationsEnabled: Boolean,
    onSearchClick: () -> Unit,
    onUnitToggle: () -> Unit,
    onAnimationToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        color = CardDarkPlum,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderCrimson)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // City name & Date
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = "Location Pin",
                        tint = PrimaryCherry,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = (cityName ?: "LOCATION UNKNOWN").uppercase(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.0.sp
                        ),
                        color = TextWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = WeatherUtils.formatCurrentDate(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.5.sp
                    ),
                    color = TextMuted,
                    modifier = Modifier.padding(start = 22.dp)
                )
            }

            // Action Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Search Action
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CardDarkPlumElevated)
                        .border(1.dp, BorderCrimson, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Search City",
                        tint = TextWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // C / F Unit Toggle
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

                // Animation Toggle Button
                val animBgColor by animateColorAsState(
                    targetValue = if (animationsEnabled) PrimaryCherry.copy(alpha = 0.2f) else CardDarkPlumElevated,
                    label = "AnimBgColor"
                )
                val animIconColor by animateColorAsState(
                    targetValue = if (animationsEnabled) PrimaryCherry else TextMuted,
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
                            if (animationsEnabled) PrimaryCherry else BorderCrimson,
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
