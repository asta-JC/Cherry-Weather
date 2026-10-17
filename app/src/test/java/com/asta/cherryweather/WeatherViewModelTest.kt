package com.asta.cherryweather

import com.asta.cherryweather.data.model.GeocodingResult
import com.asta.cherryweather.data.model.TemperatureUnit
import com.asta.cherryweather.ui.utils.SampleData
import com.asta.cherryweather.viewmodel.WeatherUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WeatherViewModelTest {

    @Test
    fun testWeatherUiStateSuccessCopy() {
        val weatherResponse = SampleData.mockWeatherResponse
        val state = WeatherUiState.Success(
            weatherResponse = weatherResponse,
            locationName = "Tokyo",
            temperatureUnit = TemperatureUnit.CELSIUS,
            animationsEnabled = true,
            searchResults = emptyList(),
            isSearching = false,
            recentCities = listOf("Tokyo", "Kyoto"),
            locationPermissionGranted = true,
            gpsEnabled = true,
            isRefreshing = false,
            hasSavedLocation = true
        )

        assertEquals("Tokyo", state.locationName)
        assertEquals(TemperatureUnit.CELSIUS, state.temperatureUnit)
        assertTrue(state.animationsEnabled)
        assertTrue(state.locationPermissionGranted)
        assertTrue(state.gpsEnabled)
        assertTrue(state.hasSavedLocation)
        assertEquals(2, state.recentCities.size)

        val updatedState = state.copy(
            temperatureUnit = TemperatureUnit.FAHRENHEIT,
            animationsEnabled = false,
            hasSavedLocation = false
        )

        assertEquals(TemperatureUnit.FAHRENHEIT, updatedState.temperatureUnit)
        assertFalse(updatedState.animationsEnabled)
        assertFalse(updatedState.hasSavedLocation)
    }

    @Test
    fun testWeatherUiStateError() {
        val errorState = WeatherUiState.Error("Network Timeout")
        assertEquals("Network Timeout", errorState.message)
    }

    @Test
    fun testGeocodingResultFormattingInSearch() {
        val result = GeocodingResult(
            name = "Paris",
            latitude = 48.8566,
            longitude = 2.3522,
            admin1 = "Ile-de-France",
            country = "France"
        )
        assertEquals("Paris, Ile-de-France, France", result.displayName)
    }
}
