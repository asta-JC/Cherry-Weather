package com.asta.cherryweather.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.asta.cherryweather.data.model.RecentCity
import com.asta.cherryweather.data.model.TemperatureUnit
import com.asta.cherryweather.data.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * DataStore preferences repository to persist settings and offline cached weather state.
 */
class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    constructor(context: Context) : this(context.dataStore)

    private object PreferenceKeys {
        val TEMPERATURE_UNIT = stringPreferencesKey("temperature_unit")
        val ANIMATIONS_ENABLED = booleanPreferencesKey("animations_enabled")
        val LAST_LATITUDE = doublePreferencesKey("last_latitude")
        val LAST_LONGITUDE = doublePreferencesKey("last_longitude")
        val LAST_CITY_NAME = stringPreferencesKey("last_city_name")
        val CACHED_WEATHER_JSON = stringPreferencesKey("cached_weather_json")
        val HAS_ASKED_LOCATION_PERMISSION = booleanPreferencesKey("has_asked_location_permission")
        val RECENT_CITIES_CSV = stringPreferencesKey("recent_cities_csv")
    }

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val tempUnitCode = preferences[PreferenceKeys.TEMPERATURE_UNIT]
            val temperatureUnit = TemperatureUnit.fromCode(tempUnitCode)
            val isAnimationEnabled = preferences[PreferenceKeys.ANIMATIONS_ENABLED] ?: true
            val lastLatitude = preferences[PreferenceKeys.LAST_LATITUDE]
            val lastLongitude = preferences[PreferenceKeys.LAST_LONGITUDE]
            val lastCityName = preferences[PreferenceKeys.LAST_CITY_NAME]
            val cachedWeatherJson = preferences[PreferenceKeys.CACHED_WEATHER_JSON]
            val hasAskedLocation = preferences[PreferenceKeys.HAS_ASKED_LOCATION_PERMISSION] ?: false
            val recentCitiesRaw = preferences[PreferenceKeys.RECENT_CITIES_CSV]
            val recentCityItems = recentCitiesRaw
                ?.split(";;")
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
                ?.map { RecentCity.fromSerializedString(it) }
                ?: emptyList()
            val recentCities = recentCityItems.map { it.name }

            UserPreferences(
                temperatureUnit = temperatureUnit,
                isAnimationEnabled = isAnimationEnabled,
                lastLatitude = lastLatitude,
                lastLongitude = lastLongitude,
                lastCityName = lastCityName,
                cachedWeatherJson = cachedWeatherJson,
                hasAskedLocationPermission = hasAskedLocation,
                recentCities = recentCities,
                recentCityItems = recentCityItems
            )
        }

    suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.TEMPERATURE_UNIT] = unit.code
        }
    }

    suspend fun setAnimationEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.ANIMATIONS_ENABLED] = enabled
        }
    }

    suspend fun saveLastLocation(latitude: Double, longitude: Double, cityName: String?) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.LAST_LATITUDE] = latitude
            preferences[PreferenceKeys.LAST_LONGITUDE] = longitude
            if (cityName != null) {
                preferences[PreferenceKeys.LAST_CITY_NAME] = cityName
            }
        }
    }

    suspend fun saveCachedWeatherJson(json: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.CACHED_WEATHER_JSON] = json
        }
    }

    suspend fun setHasAskedLocationPermission(hasAsked: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.HAS_ASKED_LOCATION_PERMISSION] = hasAsked
        }
    }

    suspend fun saveRecentCity(
        cityName: String,
        latitude: Double? = null,
        longitude: Double? = null,
        country: String? = null
    ) {
        if (cityName.isBlank()) return
        val newItem = RecentCity(cityName.trim(), latitude, longitude, country)
        dataStore.edit { preferences ->
            val existing = preferences[PreferenceKeys.RECENT_CITIES_CSV]
                ?.split(";;")
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
                ?.map { RecentCity.fromSerializedString(it) }
                ?.filter { !it.name.equals(cityName.trim(), ignoreCase = true) }
                ?: emptyList()
            val updated = listOf(newItem) + existing.take(4)
            preferences[PreferenceKeys.RECENT_CITIES_CSV] = updated.joinToString(";;") { it.toSerializedString() }
        }
    }

    suspend fun saveRecentCity(cityName: String) {
        saveRecentCity(cityName, null, null, null)
    }


    suspend fun clearCache() {
        dataStore.edit { preferences ->
            preferences.remove(PreferenceKeys.CACHED_WEATHER_JSON)
        }
    }
}
