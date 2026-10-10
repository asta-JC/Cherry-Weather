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
import androidx.compose.material.icons.rounded.GpsFixed
import androidx.compose.material.icons.rounded.GpsOff
import androidx.compose.material.icons.rounded.LocationOff
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asta.cherryweather.ui.theme.BorderCrimson
import com.asta.cherryweather.ui.theme.CardDarkPlum
import com.asta.cherryweather.ui.theme.CardDarkPlumElevated
import com.asta.cherryweather.ui.theme.GradientEnd
import com.asta.cherryweather.ui.theme.GradientStart
import com.asta.cherryweather.ui.theme.PrimaryCherry
import com.asta.cherryweather.ui.theme.TextMuted
import com.asta.cherryweather.ui.theme.TextWhite

@Composable
fun CherryLocationPermissionBanner(
    onRequestPermission: () -> Unit,
    onManualSearchSubmit: (String) -> Unit,
    modifier: Modifier = Modifier,
    isGpsDisabled: Boolean = false,
    onOpenGpsSettings: (() -> Unit)? = null
) {
    var searchQuery by remember { mutableStateOf("") }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        color = CardDarkPlum,
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderCrimson)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .shadow(12.dp, CircleShape, spotColor = PrimaryCherry, ambientColor = PrimaryCherry)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(GradientStart, GradientEnd)
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isGpsDisabled) Icons.Rounded.GpsOff else Icons.Rounded.LocationOff,
                    contentDescription = "Location Sensor",
                    tint = TextWhite,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Title
            Text(
                text = if (isGpsDisabled) "GPS SERVICES DISABLED" else "LOCATION ACCESS REQUIRED",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = TextWhite
                ),
                textAlign = TextAlign.Center
            )

            // Description
            Text(
                text = if (isGpsDisabled) {
                    "Device GPS is currently disabled in system settings. Turn on location services or search for a city manually."
                } else {
                    "Cherry Weather needs location permission to stream real-time atmospheric metrics for your exact coordinates."
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextMuted,
                    lineHeight = 20.sp
                ),
                textAlign = TextAlign.Center
            )

            // Primary Action Button (Grant location or open GPS settings)
            if (isGpsDisabled && onOpenGpsSettings != null) {
                Button(
                    onClick = onOpenGpsSettings,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryCherry,
                        contentColor = TextWhite
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.GpsFixed,
                            contentDescription = "Enable GPS",
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "ENABLE GPS IN SETTINGS",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        )
                    }
                }
            } else {
                Button(
                    onClick = onRequestPermission,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryCherry,
                        contentColor = TextWhite
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.GpsFixed,
                            contentDescription = "Allow Location",
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "ALLOW LOCATION ACCESS",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Or Manual Search Box
            Text(
                text = "// OR SEARCH LOCATION MANUALLY",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = TextMuted
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Enter city name...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CardDarkPlumElevated,
                        unfocusedContainerColor = CardDarkPlumElevated,
                        focusedBorderColor = PrimaryCherry,
                        unfocusedBorderColor = BorderCrimson,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                OutlinedButton(
                    onClick = {
                        if (searchQuery.isNotBlank()) {
                            onManualSearchSubmit(searchQuery.trim())
                        }
                    },
                    modifier = Modifier.height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryCherry),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = PrimaryCherry
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Search",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
