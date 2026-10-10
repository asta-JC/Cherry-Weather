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
            locationName = "TOKYO",
            temperatureUnit = TemperatureUnit.CELSIUS,
            animationsEnabled = true,
            searchResults = emptyList(),
            isSearching = false,
            locationPermissionGranted = true,
            gpsEnabled = true,
            isRefreshing = false
        )

        assertEquals("TOKYO", state.locationName)
        assertEquals(TemperatureUnit.CELSIUS, state.temperatureUnit)
        assertTrue(state.animationsEnabled)
        assertTrue(state.locationPermissionGranted)
        assertTrue(state.gpsEnabled)

        val updatedState = state.copy(
            temperatureUnit = TemperatureUnit.FAHRENHEIT,
            animationsEnabled = false
        )

        assertEquals(TemperatureUnit.FAHRENHEIT, updatedState.temperatureUnit)
        assertFalse(updatedState.animationsEnabled)
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
