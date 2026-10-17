package com.asta.cherryweather

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.asta.cherryweather.data.model.TemperatureUnit
import com.asta.cherryweather.ui.components.CherryWeatherView
import com.asta.cherryweather.ui.theme.CherryWeatherTheme
import com.asta.cherryweather.ui.utils.SampleData
import com.asta.cherryweather.viewmodel.WeatherUiState
import com.asta.cherryweather.viewmodel.WeatherViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CherryWeatherTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
                    val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
                    if (fineGranted || coarseGranted) {
                        viewModel.fetchWeatherForCurrentLocation()
                    } else {
                        viewModel.dismissPermissionBanner()
                    }
                }

                val onOpenGpsSettings = {
                    try {
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                val onRequestPermission = {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }

                when (val state = uiState) {
                    is WeatherUiState.Loading -> {
                        CherryWeatherView(
                            cityName = "Locating...",
                            weatherResponse = null,
                            isLoading = true,
                            unit = TemperatureUnit.CELSIUS,
                            animationsEnabled = true,
                            hasLocationPermission = true,
                            hasSavedLocation = false,
                            isGpsDisabled = false,
                            searchResults = emptyList(),
                            recentCities = emptyList(),
                            isSearchingLocations = false,
                            onRequestLocationPermission = onRequestPermission,
                            onRescanLocation = { viewModel.rescanGpsLocation(onRequestPermission) },
                            onManualSearchSubmit = { viewModel.searchCities(it) },
                            onSearchQueryChange = { viewModel.searchCities(it) },
                            onLocationSelected = { viewModel.selectCity(it) },
                            onRecentCitySelected = { viewModel.selectRecentCity(it) },
                            onDismissPermissionBanner = { viewModel.dismissPermissionBanner() },
                            onUnitToggle = { viewModel.toggleTemperatureUnit() },
                            onAnimationToggle = { viewModel.toggleAnimations() },
                            onRefresh = { viewModel.refreshWeather() },
                            onOpenGpsSettings = onOpenGpsSettings
                        )
                    }

                    is WeatherUiState.Success -> {
                        CherryWeatherView(
                            cityName = state.locationName,
                            weatherResponse = state.weatherResponse,
                            isLoading = false,
                            isRefreshing = state.isRefreshing,
                            unit = state.temperatureUnit,
                            animationsEnabled = state.animationsEnabled,
                            hasLocationPermission = state.locationPermissionGranted,
                            hasSavedLocation = state.hasSavedLocation,
                            isGpsDisabled = !state.gpsEnabled,
                            searchResults = state.searchResults,
                            recentCities = state.recentCities,
                            isSearchingLocations = state.isSearching,
                            onRequestLocationPermission = onRequestPermission,
                            onRescanLocation = { viewModel.rescanGpsLocation(onRequestPermission) },
                            onManualSearchSubmit = { viewModel.searchCities(it) },
                            onSearchQueryChange = { viewModel.searchCities(it) },
                            onLocationSelected = { viewModel.selectCity(it) },
                            onRecentCitySelected = { viewModel.selectRecentCity(it) },
                            onDismissPermissionBanner = { viewModel.dismissPermissionBanner() },
                            onUnitToggle = { viewModel.toggleTemperatureUnit() },
                            onAnimationToggle = { viewModel.toggleAnimations() },
                            onRefresh = { viewModel.refreshWeather() },
                            onOpenGpsSettings = onOpenGpsSettings
                        )
                    }

                    is WeatherUiState.Error -> {
                        CherryWeatherView(
                            cityName = "Offline",
                            weatherResponse = null,
                            isLoading = false,
                            errorMessage = state.message,
                            unit = TemperatureUnit.CELSIUS,
                            animationsEnabled = true,
                            hasLocationPermission = false,
                            hasSavedLocation = false,
                            isGpsDisabled = false,
                            searchResults = emptyList(),
                            recentCities = emptyList(),
                            isSearchingLocations = false,
                            onRequestLocationPermission = onRequestPermission,
                            onRescanLocation = { viewModel.rescanGpsLocation(onRequestPermission) },
                            onManualSearchSubmit = { viewModel.searchCities(it) },
                            onSearchQueryChange = { viewModel.searchCities(it) },
                            onLocationSelected = { viewModel.selectCity(it) },
                            onRecentCitySelected = { viewModel.selectRecentCity(it) },
                            onDismissPermissionBanner = { viewModel.dismissPermissionBanner() },
                            onUnitToggle = { viewModel.toggleTemperatureUnit() },
                            onAnimationToggle = { viewModel.toggleAnimations() },
                            onRefresh = { viewModel.refreshWeather() },
                            onRetry = { viewModel.refreshWeather() },
                            onOpenGpsSettings = onOpenGpsSettings
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Silently refresh forecast for active saved location without nagging for GPS
        viewModel.refreshWeatherSilent()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CherryWeatherViewPreview() {
    CherryWeatherTheme {
        CherryWeatherView(
            cityName = "Tokyo",
            weatherResponse = SampleData.mockWeatherResponse,
            isLoading = false,
            unit = TemperatureUnit.CELSIUS,
            animationsEnabled = true,
            hasLocationPermission = true,
            hasSavedLocation = true,
            isGpsDisabled = false,
            searchResults = emptyList(),
            recentCities = listOf("Tokyo", "Kyoto", "Paris"),
            isSearchingLocations = false,
            onRequestLocationPermission = {},
            onRescanLocation = {},
            onManualSearchSubmit = {},
            onSearchQueryChange = {},
            onLocationSelected = {},
            onRecentCitySelected = {},
            onDismissPermissionBanner = {},
            onUnitToggle = {},
            onAnimationToggle = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CherryWeatherLoadingPreview() {
    CherryWeatherTheme {
        CherryWeatherView(
            cityName = "Locating...",
            weatherResponse = null,
            isLoading = true,
            unit = TemperatureUnit.CELSIUS,
            animationsEnabled = true,
            hasLocationPermission = true,
            hasSavedLocation = false,
            isGpsDisabled = false,
            searchResults = emptyList(),
            recentCities = emptyList(),
            isSearchingLocations = false,
            onRequestLocationPermission = {},
            onRescanLocation = {},
            onManualSearchSubmit = {},
            onSearchQueryChange = {},
            onLocationSelected = {},
            onRecentCitySelected = {},
            onDismissPermissionBanner = {},
            onUnitToggle = {},
            onAnimationToggle = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CherryWeatherPermissionBannerPreview() {
    CherryWeatherTheme {
        CherryWeatherView(
            cityName = "Tokyo",
            weatherResponse = SampleData.mockWeatherResponse,
            isLoading = false,
            unit = TemperatureUnit.CELSIUS,
            animationsEnabled = true,
            hasLocationPermission = false,
            hasSavedLocation = false,
            isGpsDisabled = false,
            searchResults = emptyList(),
            recentCities = emptyList(),
            isSearchingLocations = false,
            onRequestLocationPermission = {},
            onRescanLocation = {},
            onManualSearchSubmit = {},
            onSearchQueryChange = {},
            onLocationSelected = {},
            onRecentCitySelected = {},
            onDismissPermissionBanner = {},
            onUnitToggle = {},
            onAnimationToggle = {}
        )
    }
}
