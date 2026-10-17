package com.asta.cherryweather.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.asta.cherryweather.data.local.UserPreferencesRepository
import com.asta.cherryweather.data.location.LocationService
import com.asta.cherryweather.data.model.GeocodingResult
import com.asta.cherryweather.data.model.TemperatureUnit
import com.asta.cherryweather.data.model.UserPreferences
import com.asta.cherryweather.data.model.WeatherResponse
import com.asta.cherryweather.data.repository.WeatherRepository
import com.asta.cherryweather.ui.utils.WeatherUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

sealed interface WeatherUiState {
    data object Loading : WeatherUiState

    data class Success(
        val weatherResponse: WeatherResponse,
        val locationName: String,
        val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
        val animationsEnabled: Boolean = true,
        val searchResults: List<GeocodingResult> = emptyList(),
        val isSearching: Boolean = false,
        val recentCities: List<String> = emptyList(),
        val locationPermissionGranted: Boolean = true,
        val gpsEnabled: Boolean = true,
        val isRefreshing: Boolean = false,
        val hasSavedLocation: Boolean = false
    ) : WeatherUiState

    data class Error(
        val message: String
    ) : WeatherUiState
}

class WeatherViewModel(
    application: Application,
    private val repository: WeatherRepository,
    private val locationService: LocationService
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application = application,
        repository = WeatherRepository(
            userPreferencesRepository = UserPreferencesRepository(application),
            locationService = LocationService(application)
        ),
        locationService = LocationService(application)
    )

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private var currentPreferences = UserPreferences()
    private var currentLat: Double? = null
    private var currentLon: Double? = null
    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            // Observe preferences changes
            launch {
                repository.userPreferencesFlow.collect { prefs ->
                    currentPreferences = prefs
                    updateStateFromPreferences(prefs)
                }
            }

            // Load cached weather and saved preferences first for instant display
            val cachedWeather = repository.getCachedWeather()
            val prefs = repository.userPreferencesFlow.firstOrNull() ?: UserPreferences()
            currentPreferences = prefs

            val hasSaved = prefs.lastLatitude != null && prefs.lastLongitude != null
            if (cachedWeather != null) {
                currentLat = prefs.lastLatitude
                currentLon = prefs.lastLongitude
                _uiState.value = WeatherUiState.Success(
                    weatherResponse = cachedWeather,
                    locationName = prefs.lastCityName ?: "Saved Location",
                    temperatureUnit = prefs.temperatureUnit,
                    animationsEnabled = prefs.isAnimationEnabled,
                    recentCities = prefs.recentCities,
                    locationPermissionGranted = locationService.hasLocationPermission(),
                    gpsEnabled = locationService.isGpsEnabled(),
                    hasSavedLocation = hasSaved
                )
            }

            // If user has a saved location, fetch fresh weather for it without nagging for GPS
            if (hasSaved) {
                currentLat = prefs.lastLatitude
                currentLon = prefs.lastLongitude
                fetchWeatherForLocation(prefs.lastLatitude!!, prefs.lastLongitude!!, prefs.lastCityName)
            } else {
                // First run: check if device permission already granted
                if (locationService.hasLocationPermission()) {
                    fetchWeatherForCurrentLocation()
                } else {
                    // Fallback to pleasant default city (Tokyo) without locking the UI
                    fetchWeatherForLocation(35.6762, 139.6503, "Tokyo")
                }
            }
        }
    }

    private fun updateStateFromPreferences(prefs: UserPreferences) {
        val currentState = _uiState.value
        if (currentState is WeatherUiState.Success) {
            _uiState.value = currentState.copy(
                temperatureUnit = prefs.temperatureUnit,
                animationsEnabled = prefs.isAnimationEnabled,
                recentCities = prefs.recentCities,
                hasSavedLocation = prefs.lastLatitude != null && prefs.lastLongitude != null
            )
        }
    }

    /**
     * Fetches weather forecast for given latitude/longitude.
     */
    fun fetchWeatherForLocation(lat: Double, lon: Double, cityName: String? = null) {
        viewModelScope.launch {
            currentLat = lat
            currentLon = lon
            val unit = currentPreferences.temperatureUnit
            val anims = currentPreferences.isAnimationEnabled
            val hasPerm = locationService.hasLocationPermission()
            val isGpsOn = locationService.isGpsEnabled()

            val currentState = _uiState.value
            val existingSearchResults = if (currentState is WeatherUiState.Success) currentState.searchResults else emptyList()
            val existingRecents = currentPreferences.recentCities

            // If in-memory cache has this forecast, display it immediately!
            val memoryCached = repository.getCachedForecast(lat, lon, unit)
            if (memoryCached != null && currentState is WeatherUiState.Success) {
                _uiState.value = currentState.copy(
                    weatherResponse = memoryCached,
                    locationName = cityName ?: currentState.locationName,
                    temperatureUnit = unit,
                    isRefreshing = true
                )
            }

            val result = repository.getForecast(lat, lon, unit, cityName)


            result.fold(
                onSuccess = { weatherResponse ->
                    val resolvedName = cityName
                        ?: locationService.reverseGeocode(lat, lon)
                        ?: currentPreferences.lastCityName
                        ?: "Selected Location"

                    _uiState.value = WeatherUiState.Success(
                        weatherResponse = weatherResponse,
                        locationName = resolvedName,
                        temperatureUnit = unit,
                        animationsEnabled = anims,
                        searchResults = existingSearchResults,
                        isSearching = false,
                        recentCities = existingRecents,
                        locationPermissionGranted = hasPerm,
                        gpsEnabled = isGpsOn,
                        isRefreshing = false,
                        hasSavedLocation = true
                    )
                },
                onFailure = { error ->
                    if (currentState is WeatherUiState.Success) {
                        _uiState.value = currentState.copy(isRefreshing = false)
                    } else {
                        val errorMsg = error.message ?: "Failed to fetch weather data. Please check connection."
                        _uiState.value = WeatherUiState.Error(errorMsg)
                    }
                }
            )
        }
    }

    /**
     * Requests location via LocationService, fetches geocoded name, and loads forecast.
     */
    fun fetchWeatherForCurrentLocation() {
        viewModelScope.launch {
            val unit = currentPreferences.temperatureUnit
            val anims = currentPreferences.isAnimationEnabled
            val hasPerm = locationService.hasLocationPermission()
            val isGpsOn = locationService.isGpsEnabled()

            if (!hasPerm) {
                handleMissingLocationAccess(
                    hasPerm = false,
                    isGpsOn = isGpsOn,
                    reason = "Location permission is required to fetch local weather."
                )
                return@launch
            }

            if (!isGpsOn) {
                handleMissingLocationAccess(
                    hasPerm = true,
                    isGpsOn = false,
                    reason = "GPS is disabled on device."
                )
                return@launch
            }

            val locationData = locationService.getCurrentLocation()
            if (locationData != null) {
                currentLat = locationData.latitude
                currentLon = locationData.longitude
                fetchWeatherForLocation(locationData.latitude, locationData.longitude, locationData.cityName)
            } else {
                val lastLat = currentPreferences.lastLatitude
                val lastLon = currentPreferences.lastLongitude
                if (lastLat != null && lastLon != null) {
                    fetchWeatherForLocation(lastLat, lastLon, currentPreferences.lastCityName)
                } else {
                    handleMissingLocationAccess(
                        hasPerm = true,
                        isGpsOn = isGpsOn,
                        reason = "Unable to determine current GPS location."
                    )
                }
            }
        }
    }

    private fun handleMissingLocationAccess(hasPerm: Boolean, isGpsOn: Boolean, reason: String) {
        val currentState = _uiState.value
        if (currentState is WeatherUiState.Success) {
            _uiState.value = currentState.copy(
                locationPermissionGranted = hasPerm,
                gpsEnabled = isGpsOn,
                isRefreshing = false
            )
        } else {
            viewModelScope.launch {
                val cached = repository.getCachedWeather()
                if (cached != null) {
                    _uiState.value = WeatherUiState.Success(
                        weatherResponse = cached,
                        locationName = currentPreferences.lastCityName ?: "Saved Location",
                        temperatureUnit = currentPreferences.temperatureUnit,
                        animationsEnabled = currentPreferences.isAnimationEnabled,
                        locationPermissionGranted = hasPerm,
                        gpsEnabled = isGpsOn,
                        isRefreshing = false
                    )
                } else {
                    _uiState.value = WeatherUiState.Error(reason)
                }
            }
        }
    }

    /**
     * Searches cities by query string via GeocodingApiService with 300ms debounce.
     */
    fun searchCities(query: String) {
        searchJob?.cancel()

        if (query.isBlank()) {
            val currentState = _uiState.value
            if (currentState is WeatherUiState.Success) {
                _uiState.value = currentState.copy(
                    searchResults = emptyList(),
                    isSearching = false
                )
            }
            return
        }

        val currentState = _uiState.value
        if (currentState is WeatherUiState.Success) {
            _uiState.value = currentState.copy(isSearching = true)
        }

        searchJob = viewModelScope.launch {
            delay(300) // 300ms debounce to prevent thrashing
            val result = repository.searchLocations(query)
            val state = _uiState.value
            if (state is WeatherUiState.Success) {
                result.fold(
                    onSuccess = { locations ->
                        _uiState.value = state.copy(
                            searchResults = locations,
                            isSearching = false
                        )
                    },
                    onFailure = {
                        _uiState.value = state.copy(
                            searchResults = emptyList(),
                            isSearching = false
                        )
                    }
                )
            }
        }
    }

    /**
     * Selects location from search results, gives immediate UI feedback, and fetches weather.
     */
    fun selectCity(location: GeocodingResult) {
        val cityName = location.displayName
        currentLat = location.latitude
        currentLon = location.longitude

        val currentState = _uiState.value
        if (currentState is WeatherUiState.Success) {
            // Immediate optimistic feedback: update city name and start refresh spinner right away
            _uiState.value = currentState.copy(
                locationName = cityName,
                searchResults = emptyList(),
                isSearching = false,
                isRefreshing = true
            )
        }

        viewModelScope.launch {
            repository.saveRecentCity(
                cityName = cityName,
                latitude = location.latitude,
                longitude = location.longitude,
                country = location.country
            )
        }

        fetchWeatherForLocation(location.latitude, location.longitude, cityName)
    }

    /**
     * Toggles temperature unit (Imperial/Metric) with instantaneous 0ms in-memory conversion,
     * persisting to DataStore and syncing fresh data in the background.
     */
    fun toggleTemperatureUnit() {
        val nextUnit = if (currentPreferences.temperatureUnit == TemperatureUnit.CELSIUS) {
            TemperatureUnit.FAHRENHEIT
        } else {
            TemperatureUnit.CELSIUS
        }

        // 1. Instantaneous 0ms UI update
        val currentState = _uiState.value
        if (currentState is WeatherUiState.Success) {
            val converted = WeatherUtils.convertWeatherResponse(currentState.weatherResponse, nextUnit)
            _uiState.value = currentState.copy(
                weatherResponse = converted,
                temperatureUnit = nextUnit
            )
        }
        currentPreferences = currentPreferences.copy(temperatureUnit = nextUnit)

        // 2. Persist & sync in background
        viewModelScope.launch {
            repository.setTemperatureUnit(nextUnit)

            val lat = currentLat ?: currentPreferences.lastLatitude
            val lon = currentLon ?: currentPreferences.lastLongitude
            val cityName = (uiState.value as? WeatherUiState.Success)?.locationName
                ?: currentPreferences.lastCityName

            if (lat != null && lon != null) {
                repository.getForecast(lat, lon, nextUnit, cityName).onSuccess { freshWeather ->
                    val latestState = _uiState.value
                    if (latestState is WeatherUiState.Success && latestState.temperatureUnit == nextUnit) {
                        _uiState.value = latestState.copy(weatherResponse = freshWeather)
                    }
                }
            }
        }
    }


    /**
     * Toggles canvas particle animation state.
     */
    fun toggleAnimations() {
        val nextState = !currentPreferences.isAnimationEnabled
        viewModelScope.launch {
            repository.setAnimationEnabled(nextState)
            currentPreferences = currentPreferences.copy(isAnimationEnabled = nextState)

            val currentState = _uiState.value
            if (currentState is WeatherUiState.Success) {
                _uiState.value = currentState.copy(animationsEnabled = nextState)
            }
        }
    }

    /**
     * Re-fetches weather for current or active city location (for pull-to-refresh).
     */
    fun refreshWeather() {
        val currentState = _uiState.value
        if (currentState is WeatherUiState.Success) {
            _uiState.value = currentState.copy(isRefreshing = true)
        }

        val lat = currentLat ?: currentPreferences.lastLatitude
        val lon = currentLon ?: currentPreferences.lastLongitude
        val cityName = (currentState as? WeatherUiState.Success)?.locationName
            ?: currentPreferences.lastCityName

        if (locationService.hasLocationPermission() && locationService.isGpsEnabled()) {
            fetchWeatherForCurrentLocation()
        } else if (lat != null && lon != null) {
            fetchWeatherForLocation(lat, lon, cityName)
        } else {
            fetchWeatherForCurrentLocation()
        }
    }

    /**
     * Explicit user trigger to rescan GPS coordinates.
     */
    fun rescanGpsLocation(onRequestPermission: () -> Unit) {
        if (!locationService.hasLocationPermission()) {
            onRequestPermission()
            return
        }
        if (!locationService.isGpsEnabled()) {
            val currentState = _uiState.value
            if (currentState is WeatherUiState.Success) {
                _uiState.value = currentState.copy(gpsEnabled = false)
            }
            return
        }
        fetchWeatherForCurrentLocation()
    }

    /**
     * Silently refreshes weather on returning to foreground without prompting for GPS or permissions.
     */
    fun refreshWeatherSilent() {
        val lat = currentLat ?: currentPreferences.lastLatitude
        val lon = currentLon ?: currentPreferences.lastLongitude
        val cityName = (uiState.value as? WeatherUiState.Success)?.locationName
            ?: currentPreferences.lastCityName

        if (lat != null && lon != null) {
            fetchWeatherForLocation(lat, lon, cityName)
        }
    }

    /**
     * Selects a city by name from recent searches, using cached coordinates when available
     * for instant zero-latency loading.
     */
    fun selectRecentCity(cityName: String) {
        val cachedRecent = currentPreferences.recentCityItems.firstOrNull {
            it.name.equals(cityName.trim(), ignoreCase = true)
        }
        if (cachedRecent?.latitude != null && cachedRecent.longitude != null) {
            val currentState = _uiState.value
            if (currentState is WeatherUiState.Success) {
                _uiState.value = currentState.copy(
                    locationName = cachedRecent.name,
                    searchResults = emptyList(),
                    isSearching = false,
                    isRefreshing = true
                )
            }
            currentLat = cachedRecent.latitude
            currentLon = cachedRecent.longitude
            fetchWeatherForLocation(cachedRecent.latitude, cachedRecent.longitude, cachedRecent.name)
            return
        }

        // Fallback for legacy searches without cached coordinates
        val currentState = _uiState.value
        if (currentState is WeatherUiState.Success) {
            _uiState.value = currentState.copy(
                locationName = cityName,
                isRefreshing = true
            )
        }
        viewModelScope.launch {
            val result = repository.searchLocations(cityName)
            result.fold(
                onSuccess = { results ->
                    val match = results.firstOrNull()
                    if (match != null) {
                        selectCity(match)
                    } else {
                        searchCities(cityName)
                    }
                },
                onFailure = {
                    searchCities(cityName)
                }
            )
        }
    }


    /**
     * Dismisses permission banner and prevents re-prompting.
     */
    fun dismissPermissionBanner() {
        viewModelScope.launch {
            repository.setHasAskedLocationPermission(true)
        }
    }
}
