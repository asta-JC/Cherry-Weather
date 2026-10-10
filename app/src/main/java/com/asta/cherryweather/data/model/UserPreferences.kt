package com.asta.cherryweather.data.model

/**
 * Data model representing user settings and last known weather/location cache state.
 */
data class UserPreferences(
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val isAnimationEnabled: Boolean = true,
    val lastLatitude: Double? = null,
    val lastLongitude: Double? = null,
    val lastCityName: String? = null,
    val cachedWeatherJson: String? = null
)
