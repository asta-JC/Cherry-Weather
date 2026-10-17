package com.asta.cherryweather.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asta.cherryweather.data.model.GeocodingResult
import com.asta.cherryweather.data.model.TemperatureUnit
import com.asta.cherryweather.data.model.WeatherResponse
import com.asta.cherryweather.ui.components.canvas.CherryWeatherCanvasAnimation
import com.asta.cherryweather.ui.theme.BackgroundDark
import com.asta.cherryweather.ui.theme.BorderCrimson
import com.asta.cherryweather.ui.theme.CardDarkPlum
import com.asta.cherryweather.ui.theme.GradientEnd
import com.asta.cherryweather.ui.theme.GradientStart
import com.asta.cherryweather.ui.theme.PrimaryCherry
import com.asta.cherryweather.ui.theme.TextMuted
import com.asta.cherryweather.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CherryWeatherView(
    cityName: String?,
    weatherResponse: WeatherResponse?,
    isLoading: Boolean,
    isRefreshing: Boolean = false,
    errorMessage: String? = null,
    unit: TemperatureUnit,
    animationsEnabled: Boolean,
    hasLocationPermission: Boolean,
    hasSavedLocation: Boolean = true,
    isGpsDisabled: Boolean,
    searchResults: List<GeocodingResult>,
    recentCities: List<String> = emptyList(),
    isSearchingLocations: Boolean,
    onRequestLocationPermission: () -> Unit,
    onRescanLocation: () -> Unit = {},
    onManualSearchSubmit: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onLocationSelected: (GeocodingResult) -> Unit,
    onRecentCitySelected: (String) -> Unit = {},
    onDismissPermissionBanner: () -> Unit = {},
    onUnitToggle: () -> Unit,
    onAnimationToggle: () -> Unit,
    onRefresh: () -> Unit = {},
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier,
    onOpenGpsSettings: (() -> Unit)? = null
) {
    var isSearchDialogOpen by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isPermissionBannerDismissed by remember { mutableStateOf(false) }

    val currentWeather = weatherResponse?.current
    val hourlyItems = remember(weatherResponse) {
        weatherResponse?.hourly?.toHourlyItems(limit = 24) ?: emptyList()
    }
    val dailyItems = remember(weatherResponse) {
        weatherResponse?.daily?.toDailyItems(limit = 7) ?: emptyList()
    }
    val todayDaily = dailyItems.firstOrNull()

    val weatherCode = currentWeather?.weatherCode ?: 0
    val isDay = currentWeather?.isDay == 1

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Background Custom Canvas Particle Animation
            CherryWeatherCanvasAnimation(
                weatherCode = weatherCode,
                isDay = isDay,
                animationsEnabled = animationsEnabled,
                modifier = Modifier.fillMaxSize()
            )

            // 2. Scrollable Weather Content Overlay with PullToRefresh
            val systemBarInsets = WindowInsets.systemBars.asPaddingValues()

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = systemBarInsets.calculateTopPadding(),
                        bottom = systemBarInsets.calculateBottomPadding()
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(top = 10.dp)
                ) {
                    // Unified Top Navigation & Location Controls
                    CherryTopBar(
                        cityName = cityName,
                        unit = unit,
                        animationsEnabled = animationsEnabled,
                        onSearchClick = {
                            isSearchDialogOpen = true
                        },
                        onRescanLocation = onRescanLocation,
                        onUnitToggle = onUnitToggle,
                        onAnimationToggle = onAnimationToggle,
                        timezone = weatherResponse?.timezone
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Location Permission or GPS Warning Banner (only if GPS disabled or no saved location and not dismissed)
                    val showBanner = !isPermissionBannerDismissed && (
                        (isGpsDisabled && hasLocationPermission) ||
                        (!hasLocationPermission && !hasSavedLocation)
                    )

                    if (showBanner) {
                        CherryLocationPermissionBanner(
                            onRequestPermission = onRequestLocationPermission,
                            onManualSearchSubmit = {
                                isSearchDialogOpen = true
                            },
                            onDismiss = {
                                isPermissionBannerDismissed = true
                                onDismissPermissionBanner()
                            },
                            isGpsDisabled = isGpsDisabled,
                            onOpenGpsSettings = onOpenGpsSettings
                        )
                    }

                    if (isLoading && weatherResponse == null) {
                        // Loading Shimmer Skeleton
                        CherryLoadingSkeleton()
                    } else if (errorMessage != null && weatherResponse == null) {
                        // Friendly Error Card
                        ErrorStateCard(
                            message = errorMessage,
                            onRetry = onRetry
                        )
                    } else {
                        // Main Weather Hero Section
                        CherryHeroSection(
                            currentWeather = currentWeather,
                            todayDaily = todayDaily,
                            unit = unit,
                            timezone = weatherResponse?.timezone
                        )


                        // 24-Hour Forecast
                        if (hourlyItems.isNotEmpty()) {
                            CherryHourlyForecast(
                                hourlyItems = hourlyItems,
                                unit = unit
                            )
                        }

                        // 7-Day Forecast Card
                        if (dailyItems.isNotEmpty()) {
                            CherryDailyForecastCard(
                                dailyItems = dailyItems,
                                unit = unit
                            )
                        }

                        // Weather Highlights Grid
                        CherryDetailsGrid(
                            currentWeather = currentWeather,
                            todayDaily = todayDaily,
                            unit = unit
                        )
                    }

                    CherryFooter()
                }
            }

            // 3. Search & Location Selection Dialog Modal
            if (isSearchDialogOpen) {
                CherrySearchDialog(
                    query = searchQuery,
                    onQueryChange = { newQuery ->
                        searchQuery = newQuery
                        onSearchQueryChange(newQuery)
                    },
                    searchResults = searchResults,
                    recentCities = recentCities,
                    isSearching = isSearchingLocations,
                    onLocationSelected = { location ->
                        onLocationSelected(location)
                        isSearchDialogOpen = false
                    },
                    onRecentCityClick = { recentCity ->
                        onRecentCitySelected(recentCity)
                        isSearchDialogOpen = false
                    },
                    onUseCurrentLocation = onRescanLocation,
                    onDismiss = {
                        isSearchDialogOpen = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ErrorStateCard(
    message: String,
    onRetry: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        color = CardDarkPlum,
        shape = RoundedCornerShape(26.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderCrimson)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .shadow(12.dp, CircleShape, spotColor = PrimaryCherry, ambientColor = PrimaryCherry)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(GradientStart, GradientEnd)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.CloudOff,
                    contentDescription = "Error Icon",
                    tint = TextWhite,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = "Unable to Load Forecast",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextMuted,
                    lineHeight = 20.sp
                ),
                textAlign = TextAlign.Center
            )

            Button(
                onClick = onRetry,
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
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "Retry",
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Retry Connection",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}
