package com.asta.cherryweather.data.model

/**
 * Domain model representing a recently searched or visited city,
 * caching coordinates to enable instantaneous forecast loading without geocoding.
 */
data class RecentCity(
    val name: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val country: String? = null
) {
    /**
     * Serializes to a compact persistent string format: name|lat|lon|country
     */
    fun toSerializedString(): String {
        val lat = latitude?.toString() ?: ""
        val lon = longitude?.toString() ?: ""
        val c = country ?: ""
        return "$name|$lat|$lon|$c"
    }

    companion object {
        /**
         * Parses from serialized string with backward compatibility for plain city names.
         */
        fun fromSerializedString(str: String): RecentCity {
            val parts = str.split("|")
            if (parts.size >= 3) {
                val name = parts[0].trim()
                val lat = parts[1].toDoubleOrNull()
                val lon = parts[2].toDoubleOrNull()
                val country = if (parts.size > 3) parts[3].trim().ifBlank { null } else null
                return RecentCity(name = name, latitude = lat, longitude = lon, country = country)
            }
            return RecentCity(name = str.trim())
        }
    }
}
