package com.asta.cherryweather

import com.asta.cherryweather.data.model.DailyForecast
import com.asta.cherryweather.data.model.GeocodingResult
import com.asta.cherryweather.data.model.HourlyForecast
import com.asta.cherryweather.data.model.TemperatureUnit
import com.asta.cherryweather.data.model.UserPreferences
import com.asta.cherryweather.data.model.WeatherResponse
import com.asta.cherryweather.data.remote.NetworkClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WeatherRepositoryTest {

    @Test
    fun testTemperatureUnitFromCode() {
        assertEquals(TemperatureUnit.CELSIUS, TemperatureUnit.fromCode("celsius"))
        assertEquals(TemperatureUnit.FAHRENHEIT, TemperatureUnit.fromCode("fahrenheit"))
        assertEquals(TemperatureUnit.FAHRENHEIT, TemperatureUnit.fromCode("FAHRENHEIT"))
        assertEquals(TemperatureUnit.CELSIUS, TemperatureUnit.fromCode("unknown"))
    }

    @Test
    fun testGeocodingResultDisplayName() {
        val result1 = GeocodingResult(
            name = "Tokyo",
            latitude = 35.6895,
            longitude = 139.6917,
            admin1 = "Tokyo",
            country = "Japan"
        )
        // admin1 equals name -> omitted
        assertEquals("Tokyo, Japan", result1.displayName)

        val result2 = GeocodingResult(
            name = "Shinjuku",
            latitude = 35.6938,
            longitude = 139.7034,
            admin1 = "Tokyo",
            country = "Japan"
        )
        assertEquals("Shinjuku, Tokyo, Japan", result2.displayName)
    }

    @Test
    fun testHourlyAndDailyForecastMapping() {
        val hourly = HourlyForecast(
            time = listOf("2026-03-30T00:00", "2026-03-30T01:00"),
            temperature2m = listOf(18.5, 17.2),
            apparentTemperature = listOf(18.0, 16.8),
            relativeHumidity2m = listOf(65, 70),
            precipitation = listOf(0.0, 0.1),
            weatherCode = listOf(0, 1),
            surfacePressure = listOf(1013.2, 1012.8),
            windSpeed10m = listOf(12.0, 14.5),
            uvIndex = listOf(0.0, 0.0),
            isDay = listOf(0, 0)
        )

        val items = hourly.toHourlyItems()
        assertEquals(2, items.size)
        assertEquals("2026-03-30T00:00", items[0].time)
        assertEquals(18.5, items[0].temperature)
        assertEquals(false, items[0].isDay)

        val daily = DailyForecast(
            time = listOf("2026-03-30"),
            weatherCode = listOf(0),
            temperature2mMax = listOf(22.0),
            temperature2mMin = listOf(14.0),
            apparentTemperatureMax = listOf(21.5),
            apparentTemperatureMin = listOf(13.5),
            sunrise = listOf("2026-03-30T06:00"),
            sunset = listOf("2026-03-30T18:00"),
            uvIndexMax = listOf(6.5),
            precipitationSum = listOf(0.0),
            windSpeed10mMax = listOf(18.0)
        )

        val dailyItems = daily.toDailyItems()
        assertEquals(1, dailyItems.size)
        assertEquals("2026-03-30", dailyItems[0].date)
        assertEquals(22.0, dailyItems[0].maxTemp)
        assertEquals(14.0, dailyItems[0].minTemp)
    }

    @Test
    fun testWeatherResponseMoshiSerialization() {
        val adapter = NetworkClient.moshi.adapter(WeatherResponse::class.java)
        val json = """
            {
                "latitude": 35.6895,
                "longitude": 139.6917,
                "timezone": "Asia/Tokyo",
                "current": {
                    "time": "2026-03-30T12:00",
                    "temperature_2m": 20.5,
                    "apparent_temperature": 20.1,
                    "relative_humidity_2m": 55,
                    "is_day": 1,
                    "weather_code": 0,
                    "wind_speed_10m": 15.2,
                    "uv_index": 5.4
                }
            }
        """.trimIndent()

        val parsed = adapter.fromJson(json)
        assertNotNull(parsed)
        assertEquals(35.6895, parsed?.latitude)
        assertEquals(139.6917, parsed?.longitude)
        assertEquals(20.5, parsed?.current?.temperature2m)
        assertEquals(1, parsed?.current?.isDay)
    }

    @Test
    fun testUserPreferencesDefaults() {
        val prefs = UserPreferences()
        assertEquals(TemperatureUnit.CELSIUS, prefs.temperatureUnit)
        assertTrue(prefs.isAnimationEnabled)
        assertEquals(false, prefs.hasAskedLocationPermission)
        assertTrue(prefs.recentCities.isEmpty())
        assertTrue(prefs.recentCityItems.isEmpty())
    }

    @Test
    fun testRecentCitySerializationAndDeserialization() {
        val city = com.asta.cherryweather.data.model.RecentCity(
            name = "Paris",
            latitude = 48.8566,
            longitude = 2.3522,
            country = "France"
        )
        val serialized = city.toSerializedString()
        assertEquals("Paris|48.8566|2.3522|France", serialized)

        val parsed = com.asta.cherryweather.data.model.RecentCity.fromSerializedString(serialized)
        assertEquals("Paris", parsed.name)
        assertEquals(48.8566, parsed.latitude!!, 0.0001)
        assertEquals(2.3522, parsed.longitude!!, 0.0001)
        assertEquals("France", parsed.country)

        // Legacy format without coordinates
        val legacy = com.asta.cherryweather.data.model.RecentCity.fromSerializedString("Tokyo")
        assertEquals("Tokyo", legacy.name)
        assertEquals(null, legacy.latitude)
        assertEquals(null, legacy.longitude)
    }

    @Test
    fun testRainIconIsCloudRainIcon() {
        val rainCodes = listOf(51, 53, 55, 61, 63, 65, 80, 81, 82)
        for (code in rainCodes) {
            val icon = com.asta.cherryweather.ui.utils.WeatherUtils.getWeatherIcon(code)
            assertEquals("Code $code should map to CloudRainIcon", com.asta.cherryweather.ui.utils.WeatherUtils.CloudRainIcon, icon)
        }
    }

    @Test
    fun testConvertWeatherResponseUnits() {
        val mock = com.asta.cherryweather.ui.utils.SampleData.mockWeatherResponse
        val converted = com.asta.cherryweather.ui.utils.WeatherUtils.convertWeatherResponse(
            mock,
            TemperatureUnit.FAHRENHEIT
        )

        // 22°C should convert to 71.6°F
        val originalTemp = mock.current?.temperature2m ?: 0.0
        val expectedF = (originalTemp * 9.0 / 5.0) + 32.0
        assertEquals(expectedF, converted.current?.temperature2m!!, 0.1)

        // Converting back to Celsius
        val backToC = com.asta.cherryweather.ui.utils.WeatherUtils.convertWeatherResponse(
            converted,
            TemperatureUnit.CELSIUS
        )
        assertEquals(originalTemp, backToC.current?.temperature2m!!, 0.1)
    }

    @Test
    fun testDateAndClockFormatting() {
        val topBarDateTime = com.asta.cherryweather.ui.utils.WeatherUtils.formatCurrentDateTime("Asia/Tokyo")
        assertNotNull(topBarDateTime)
        assertTrue("Top bar datetime should contain bullet separator", topBarDateTime.contains("•"))

        val heroDateTime = com.asta.cherryweather.ui.utils.WeatherUtils.formatHeroDateTime("Asia/Tokyo")
        assertNotNull(heroDateTime)
        assertTrue("Hero datetime should contain bullet separator", heroDateTime.contains("•"))

        // Fallback for null timezone
        val fallbackDateTime = com.asta.cherryweather.ui.utils.WeatherUtils.formatHeroDateTime(null)
        assertNotNull(fallbackDateTime)
        assertTrue("Fallback datetime should contain bullet separator", fallbackDateTime.contains("•"))
    }
}


