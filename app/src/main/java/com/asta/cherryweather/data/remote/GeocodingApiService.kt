package com.asta.cherryweather.data.remote

import com.asta.cherryweather.data.model.GeocodingResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service for Open-Meteo Geocoding Search API.
 */
interface GeocodingApiService {

    @GET("v1/search")
    suspend fun searchLocations(
        @Query("name") name: String,
        @Query("count") count: Int = 10,
        @Query("language") language: String = "en",
        @Query("format") format: String = "json"
    ): Response<GeocodingResponse>

    companion object {
        const val BASE_URL = "https://geocoding-api.open-meteo.com/"
    }
}
