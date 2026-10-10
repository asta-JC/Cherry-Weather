package com.asta.cherryweather.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

/**
 * Data class encapsulating current device coordinates and resolved city name.
 */
data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val cityName: String,
)

/**
 * Service managing FusedLocationProviderClient location requests and reverse geocoding via Geocoder.
 */
class LocationService(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    /**
     * Checks if fine or coarse location permissions are granted.
     */
    fun hasLocationPermission(): Boolean {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocationGranted || coarseLocationGranted
    }

    /**
     * Checks if location provider (GPS or Network) is enabled on the device.
     */
    fun isGpsEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? android.location.LocationManager
        return locationManager?.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) == true ||
                locationManager?.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER) == true
    }

    /**
     * Fetches current location and resolves city name.
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LocationData? = withContext(Dispatchers.IO) {
        if (!hasLocationPermission()) {
            return@withContext null
        }

        val location = fetchFusedLocation() ?: return@withContext null
        val cityName = reverseGeocode(location.latitude, location.longitude) ?: "Your location"

        LocationData(
            latitude = location.latitude,
            longitude = location.longitude,
            cityName = cityName
        )
    }

    @SuppressLint("MissingPermission")
    private suspend fun fetchFusedLocation(): Location? = suspendCancellableCoroutine { continuation ->
        val cancellationTokenSource = CancellationTokenSource()

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { location: Location? ->
            if (location != null) {
                if (continuation.isActive) {
                    continuation.resume(location)
                }
            } else {
                fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                    if (continuation.isActive) {
                        continuation.resume(lastLoc)
                    }
                }.addOnFailureListener {
                    if (continuation.isActive) {
                        continuation.resume(null)
                    }
                }
            }
        }.addOnFailureListener {
            fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                if (continuation.isActive) {
                    continuation.resume(lastLoc)
                }
            }.addOnFailureListener {
                if (continuation.isActive) {
                    continuation.resume(null)
                }
            }
        }

        continuation.invokeOnCancellation {
            cancellationTokenSource.cancel()
        }
    }

    /**
     * Reverse geocodes latitude/longitude into a city or locality name.
     */
    suspend fun reverseGeocode(latitude: Double, longitude: Double): String? = withContext(Dispatchers.IO) {
        if (!Geocoder.isPresent()) {
            return@withContext null
        }

        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                return@withContext suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocation(
                        latitude,
                        longitude,
                        1,
                        object : Geocoder.GeocodeListener {
                            override fun onGeocode(addresses: MutableList<Address>) {
                                val address = addresses.firstOrNull()
                                if (continuation.isActive) {
                                    continuation.resume(formatCityName(address))
                                }
                            }

                            override fun onError(errorMessage: String?) {
                                if (continuation.isActive) {
                                    continuation.resume(null)
                                }
                            }
                        }
                    )
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                val address = addresses?.firstOrNull()
                return@withContext formatCityName(address)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    private fun formatCityName(address: Address?): String? {
        if (address == null) return null
        return address.locality
            ?: address.subAdminArea
            ?: address.adminArea
            ?: address.featureName
    }
}
