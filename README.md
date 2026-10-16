<div align="center">

# 🍒 Cherry Weather

<p align="center">
  <img src="app/src/main/res/drawable/icon.png" width="108" alt="Cherry Weather Logo" />
</p>

> A modern, beautifully crafted Android weather application featuring real-time Open-Meteo forecasts, dynamic custom Canvas weather animations, and Material 3 Expressive theming. 🌤️✨

</div>

---

## 📱 Overview

**Cherry Weather** is a premium weather companion designed for Android with an expressive Material 3 dark aesthetic. Powered by the keyless Open-Meteo API, it delivers hyper-local current weather conditions, 24-hour hourly trends, 7-day extended forecasts, and advanced meteorological metrics (UV index, wind speed, humidity, pressure, visibility, and dew point) with zero configuration.

---

## ✨ Key Features

- 🌤️ **Open-Meteo Integration**: Fast, reliable, keyless weather data, hourly trends, and 7-day outlooks.
- 📍 **Smart Location & Geocoding**: GPS location detection combined with an interactive city search dialog supporting auto-complete.
- 🎨 **Custom Canvas Animations**: Dynamic particle and vector weather effects rendered via Jetpack Compose `Canvas` (rain, snow, clouds, sun rays).
- 🌡️ **Instant Unit Toggle**: Switch between Metric (`°C`, `km/h`) and Imperial (`°F`, `mph`) with reactive instant UI updates.
- 💾 **DataStore Caching**: Persistent user preferences and cached forecasts for instant cold starts and offline resilience.
- 📱 **Material 3 Expressive**: Edge-to-edge layout immersion with dynamic theming and smooth state transitions.

---

## 📸 Screenshots & UI Preview

| Home | Details | Search |
| :---: | :---: | :---: |
| <img src="screenshots/screenshot-home.jpg" width="220" alt="Home" /> | <img src="screenshots/screenshot-details.jpg" width="220" alt="Details" /> | <img src="screenshots/screenshot-search.jpg" width="220" alt="Search" /> |

---

## 🛠️ Tech Stack & Architecture

- **UI Toolkit**: [Jetpack Compose](https://developer.android.com/jetpack/compose) & [Material 3](https://m3.material.io/)
- **Architecture**: MVVM with Unidirectional Data Flow (`StateFlow`)
- **Networking**: OkHttp & Moshi for JSON parsing
- **Local Storage**: Jetpack Preferences DataStore
- **Location**: Android Location Services & Geocoding API
- **Canvas Graphics**: Custom Compose Canvas particle physics

---

## 📂 Project Structure

```tree
com.asta.cherryweather/
├── data/
│   ├── local/              # DataStore user preferences
│   ├── location/           # GPS location service & permissions
│   ├── model/              # Weather & Geocoding response models
│   ├── remote/             # OkHttp, Moshi, and Open-Meteo API clients
│   └── repository/         # Single source of truth WeatherRepository
├── ui/
│   ├── components/         # TopBar, HeroSection, HourlyForecast, DailyForecast, Canvas animations
│   ├── theme/              # Color palettes, typography, and M3 theme
│   └── utils/              # Weather code mappers & sample data
├── viewmodel/              # WeatherViewModel & WeatherUiState
└── MainActivity.kt         # Entry point activity & lifecycle host
```

---

## 🚀 Getting Started & Build Instructions

### Prerequisites
- Android Studio Ladybug or newer
- JDK 17 or higher
- Android SDK 26+ (Target SDK 35)

### Build Steps

1. **Clone the repository**:
   ```bash
   git clone https://github.com/asta-JC/Cherry-Weather.git
   cd Cherry-Weather
   ```

2. **Open in Android Studio**:
   - Open Android Studio and select **Open**.
   - Navigate to the cloned `Cherry-Weather` directory and let Gradle sync.

3. **Build & Run**:
   - Connect an Android device or start an emulator.
   - Run the app via Android Studio or execute via terminal:
     ```bash
     ./gradlew :app:assembleDebug
     ```

4. **Run Unit Tests**:
   ```bash
   ./gradlew testDebugUnitTest
   ```

---

## 📄 License

This project is developed as part of Asta's project series. Feel free to use and adapt for personal and educational use!

---

<p align="center">
  Made with ❤️ & 🍒 by Asta
</p>
