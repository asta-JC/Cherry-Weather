package com.asta.cherryweather.data.model

/**
 * Temperature unit preferences: Celsius or Fahrenheit.
 */
enum class TemperatureUnit(val code: String, val symbol: String) {
    CELSIUS("celsius", "°C"),
    FAHRENHEIT("fahrenheit", "°F");

    companion object {
        fun fromCode(code: String?): TemperatureUnit {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) }
                ?: CELSIUS
        }
    }
}
