package com.asta.cherryweather.data.repository

import com.asta.cherryweather.data.local.UserPreferencesRepository
import com.asta.cherryweather.data.location.LocationData
import com.asta.cherryweather.data.location.LocationService
import com.asta.cherryweather.data.model.GeocodingResult
import com.asta.cherryweather.data.model.TemperatureUnit
import com.asta.cherryweather.data.model.UserPreferences
import com.asta.cherryweather.data.model.WeatherResponse
import com.asta.cherryweather.data.remote.GeocodingApiService
import com.asta.cherryweather.data.remote.NetworkClient
import com.asta.cherryweather.data.remote.OpenMeteoApiService
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

/**
 * Main weather repository uniting remote forecast & geocoding network calls,
 * device location acquisition, and DataStore caching/preferences.
 */
class WeatherRepository(
    private val openMeteoApiService: OpenMeteoApiService = NetworkClient.openMeteoApiService,
    private val geocodingApiService: GeocodingApiService = NetworkClient.geocodingApiService,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val locationService: LocationService,
) {

    private val weatherAdapter: JsonAdapter<WeatherResponse> by lazy {
        NetworkClient.moshi.adapter(WeatherResponse::class.java)
    }

    /**
     * Flow of user preferences state (unit, animations, last location, cached weather).
     */
    val userPreferencesFlow: Flow<UserPreferences> = userPreferencesRepository.userPreferencesFlow

    /**
     * Fetches forecast from Open-Meteo for given coordinates and unit settings.
     * Caches response JSON on success, or falls back to offline cached weather response.
     */
    suspend fun getForecast(
        latitude: Double,
        longitude: Double,
        unit: TemperatureUnit = TemperatureUnit.CELSIUS,
        cityName: String? = null
    ): Result<WeatherResponse> = withContext(Dispatchers.IO) {
        try {
            val tempUnit = unit.code
            val windSpeedUnit = if (unit == TemperatureUnit.FAHRENHEIT) "mph" else "kmh"
            val precipitationUnit = if (unit == TemperatureUnit.FAHRENHEIT) "inch" else "mm"

            val response = openMeteoApiService.getForecast(
                latitude = latitude,
                longitude = longitude,
                temperatureUnit = tempUnit,
                windSpeedUnit = windSpeedUnit,
                precipitationUnit = precipitationUnit
            )

            if (response.isSuccessful) {
                val weather = response.body()
                if (weather != null) {
                    val jsonString = weatherAdapter.toJson(weather)
                    userPreferencesRepository.saveCachedWeatherJson(jsonString)
                    userPreferencesRepository.saveLastLocation(latitude, longitude, cityName)
                    return@withContext Result.success(weather)
                }
            }

            // Fallback to cache if response unsuccessful
            val cachedWeather = getCachedWeather()
            if (cachedWeather != null) {
                Result.success(cachedWeather)
            } else {
                Result.failure(Exception("Failed to fetch weather data: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val cachedWeather = getCachedWeather()
            if (cachedWeather != null) {
                Result.success(cachedWeather)
            } else {
                Result.failure(e)
            }
        }
    }

    /**
     * Searches location by query string using Open-Meteo Geocoding API.
     */
    suspend fun searchLocations(query: String): Result<List<GeocodingResult>> = withContext(Dispatchers.IO) {
        try {
            if (query.isBlank()) {
                return@withContext Result.success(emptyList())
            }
            val response = geocodingApiService.searchLocations(name = query)
            if (response.isSuccessful) {
                val results = response.body()?.results ?: emptyList()
                Result.success(results)
            } else {
                Result.failure(Exception("Geocoding search failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Attempts to acquire device's current location and fetch weather forecast.
     * Falls back to stored last known location if device location is unavailable.
     */
    suspend fun getCurrentLocationWeather(
        unit: TemperatureUnit = TemperatureUnit.CELSIUS
    ): Result<Pair<LocationData, WeatherResponse>> = withContext(Dispatchers.IO) {
        val currentLocation = locationService.getCurrentLocation()

        val locationData = if (currentLocation != null) {
            currentLocation
        } else {
            // Check stored location in DataStore
            val prefs = userPreferencesRepository.userPreferencesFlow.firstOrNull()
            if ((prefs?.lastLatitude != null) && (prefs.lastLongitude != null)) {
                LocationData(
                    latitude = prefs.lastLatitude,
                    longitude = prefs.lastLongitude,
                    cityName = prefs.lastCityName ?: "Saved Location"
                )
            } else {
                null
            }
        }

        if (locationData == null) {
            return@withContext Result.failure(Exception("Location unavailable and no cached location found."))
        }

        val weatherResult = getForecast(
            latitude = locationData.latitude,
            longitude = locationData.longitude,
            unit = unit,
            cityName = locationData.cityName
        )

        weatherResult.fold(
            onSuccess = { weather -> Result.success(Pair(locationData, weather)) },
            onFailure = { error -> Result.failure(error) }
        )
    }

    /**
     * Deserializes cached weather JSON from DataStore preferences if present.
     */
    suspend fun getCachedWeather(): WeatherResponse? = withContext(Dispatchers.IO) {
        val prefs = userPreferencesRepository.userPreferencesFlow.firstOrNull()
        val json = prefs?.cachedWeatherJson ?: return@withContext null
        return@withContext try {
            weatherAdapter.fromJson(json)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Sets temperature unit preference (CELSIUS / FAHRENHEIT).
     */
    suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        userPreferencesRepository.setTemperatureUnit(unit)
    }

    /**
     * Toggles animation preference.
     */
    suspend fun setAnimationEnabled(enabled: Boolean) {
        userPreferencesRepository.setAnimationEnabled(enabled)
    }
}
