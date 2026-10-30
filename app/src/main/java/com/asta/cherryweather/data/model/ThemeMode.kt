package com.asta.cherryweather.data.model

enum class ThemeMode(val label: String) {
    SYSTEM("System default"), LIGHT("Light"), DARK("Dark");

    companion object {
        fun fromCode(code: String?): ThemeMode = entries.firstOrNull { it.name == code } ?: SYSTEM
    }
}
