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
import kotlinx.coroutines.Job
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
        val locationPermissionGranted: Boolean = true,
        val gpsEnabled: Boolean = true,
        val isRefreshing: Boolean = false
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

            // Load cached weather first on startup for instant display
            val cachedWeather = repository.getCachedWeather()
            val prefs = repository.userPreferencesFlow.firstOrNull() ?: UserPreferences()
            currentPreferences = prefs

            if (cachedWeather != null) {
                currentLat = prefs.lastLatitude
                currentLon = prefs.lastLongitude
                _uiState.value = WeatherUiState.Success(
                    weatherResponse = cachedWeather,
                    locationName = prefs.lastCityName ?: "Saved Location",
                    temperatureUnit = prefs.temperatureUnit,
                    animationsEnabled = prefs.isAnimationEnabled,
                    locationPermissionGranted = locationService.hasLocationPermission(),
                    gpsEnabled = locationService.isGpsEnabled()
                )
            }

            // Then refresh weather with real-time data
            fetchWeatherForCurrentLocation()
        }
    }

    private fun updateStateFromPreferences(prefs: UserPreferences) {
        val currentState = _uiState.value
        if (currentState is WeatherUiState.Success) {
            _uiState.value = currentState.copy(
                temperatureUnit = prefs.temperatureUnit,
                animationsEnabled = prefs.isAnimationEnabled
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
                        locationPermissionGranted = hasPerm,
                        gpsEnabled = isGpsOn,
                        isRefreshing = false
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
     * Searches cities by query string via GeocodingApiService.
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
            val result = repository.searchLocations(query)
            val currentState = _uiState.value
            if (currentState is WeatherUiState.Success) {
                result.fold(
                    onSuccess = { locations ->
                        _uiState.value = currentState.copy(
                            searchResults = locations,
                            isSearching = false
                        )
                    },
                    onFailure = {
                        _uiState.value = currentState.copy(
                            searchResults = emptyList(),
                            isSearching = false
                        )
                    }
                )
            }
        }
    }

    /**
     * Selects location from search results, updates DataStore, and fetches weather.
     */
    fun selectCity(location: GeocodingResult) {
        val currentState = _uiState.value
        if (currentState is WeatherUiState.Success) {
            _uiState.value = currentState.copy(
                searchResults = emptyList(),
                isSearching = false
            )
        }

        val cityName = location.displayName
        currentLat = location.latitude
        currentLon = location.longitude

        fetchWeatherForLocation(location.latitude, location.longitude, cityName)
    }

    /**
     * Toggles temperature unit (Imperial/Metric) in DataStore and updates forecast.
     */
    fun toggleTemperatureUnit() {
        val nextUnit = if (currentPreferences.temperatureUnit == TemperatureUnit.CELSIUS) {
            TemperatureUnit.FAHRENHEIT
        } else {
            TemperatureUnit.CELSIUS
        }

        viewModelScope.launch {
            repository.setTemperatureUnit(nextUnit)
            currentPreferences = currentPreferences.copy(temperatureUnit = nextUnit)

            val lat = currentLat ?: currentPreferences.lastLatitude
            val lon = currentLon ?: currentPreferences.lastLongitude
            val cityName = (uiState.value as? WeatherUiState.Success)?.locationName
                ?: currentPreferences.lastCityName

            if (lat != null && lon != null) {
                fetchWeatherForLocation(lat, lon, cityName)
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
}
