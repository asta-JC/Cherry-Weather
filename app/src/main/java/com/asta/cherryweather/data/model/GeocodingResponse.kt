package com.asta.cherryweather.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Open-Meteo geocoding search response model.
 */
@JsonClass(generateAdapter = true)
data class GeocodingResponse(
    @field:Json(name = "results") val results: List<GeocodingResult>? = null,
    @field:Json(name = "generationtime_ms") val generationTimeMs: Double? = null
)

/**
 * Geocoding location result item.
 */
@JsonClass(generateAdapter = true)
data class GeocodingResult(
    @field:Json(name = "id") val id: Long? = null,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "latitude") val latitude: Double,
    @field:Json(name = "longitude") val longitude: Double,
    @field:Json(name = "elevation") val elevation: Double? = null,
    @field:Json(name = "feature_code") val featureCode: String? = null,
    @field:Json(name = "country_code") val countryCode: String? = null,
    @field:Json(name = "admin1") val admin1: String? = null,
    @field:Json(name = "admin2") val admin2: String? = null,
    @field:Json(name = "admin3") val admin3: String? = null,
    @field:Json(name = "admin4") val admin4: String? = null,
    @field:Json(name = "country") val country: String? = null,
    @field:Json(name = "timezone") val timezone: String? = null,
    @field:Json(name = "population") val population: Long? = null,
    @field:Json(name = "postcodes") val postcodes: List<String>? = null
) {
    /**
     * Displayable location title (e.g. "Tokyo, Kanto, Japan").
     */
    val displayName: String
        get() {
            val parts = mutableListOf<String>()
            parts.add(name)
            if (!admin1.isNullOrBlank() && admin1 != name) {
                parts.add(admin1)
            }
            if (!country.isNullOrBlank()) {
                parts.add(country)
            }
            return parts.joinToString(", ")
        }
}
