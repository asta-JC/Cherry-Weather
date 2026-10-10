# Project Plan

Cherry Weather - A native Android weather app using Kotlin, Jetpack Compose (Material 3), dark futuristic cherry red design language matching Cherry Calc / Cherry Timer. MVVM architecture, Open-Meteo API (forecast + geocoding, no API key), FusedLocationProviderClient + Geocoder, DataStore, custom Compose Canvas weather animations (cherry clouds, rain, thunder, snow, sun/moon, fog, cherry blossom petals), location permission onboarding flow, search fallback, offline caching, C/F toggle, Material 3 design system.

## Project Brief

# Project Brief: Cherry Weather

## Features
1. **Current & Forecasted Weather**: Real-time current conditions, hourly forecasts, and daily forecasts fetched from the Open-Meteo API.
2. **Location Onboarding & Search**: Seamless location permission onboarding utilizing `FusedLocationProviderClient` with a search fallback and geocoding to retrieve weather for any global location.
3. **Cherry Red UI & Canvas Weather Animations**: Dark futuristic cherry red visual design with custom Jetpack Compose Canvas weather animations (cherry clouds, rain, thunder, snow, sun/moon, fog, and falling cherry blossom petals).
4. **Unit Toggles & Data Persistence**: Imperial/Metric (Celsius/Fahrenheit) temperature preference toggling and offline caching powered by Jetpack DataStore.

## High-Level Tech Stack
* **Language**: Kotlin
* **UI & Design**: Jetpack Compose with Material 3 and custom Compose Canvas graphics
* **Architecture**: MVVM (Model-View-ViewModel) with Kotlin Coroutines & Flow
* **Navigation & Adaptive Layouts**: **Jetpack Navigation 3** (state-driven) and **Compose Material Adaptive** library
* **Networking & APIs**: Open-Meteo API (forecast & geocoding)
* **Location Services**: Google Play Services Location (`FusedLocationProviderClient`) & Android `Geocoder`
* **Persistence**: Jetpack DataStore (for offline caching and user preferences)

## Implementation Steps
**Total Duration:** 17m 55s

### Task_1_DataAndNetworkSetup: Set up weather data models, Open-Meteo API networking repository, Jetpack DataStore preferences and cache, and location handling services.
- **Status:** COMPLETED
- **Updates:** Data models, OpenMeteoApiService, GeocodingApiService, UserPreferencesRepository, LocationService, and WeatherRepository implemented and verified with passing unit tests.
- **Acceptance Criteria:**
  - Open-Meteo API integration and network repository working
  - DataStore preferences configured for Imperial/Metric units and offline cache
  - Location onboarding and geocoding repository ready
  - Project builds successfully
- **Duration:** 3m 58s

### Task_2_WeatherUIAndCanvasAnimations: Implement dark futuristic cherry red Material 3 theme and custom Compose Canvas weather animations (cherry clouds, rain, thunder, snow, sun/moon, fog, cherry blossom petals).
- **Status:** COMPLETED
- **Updates:** Cherry Red Material 3 theme, custom Compose Canvas weather animations (rain, thunder, snow, clear sun/moon, petals, fog, clouds), and all UI components (Hero, Hourly, 7-Day, Details grid, Search dialog, Permission onboarding, Shimmer skeleton) implemented and built cleanly.
- **Acceptance Criteria:**
  - Cherry Red dark futuristic design theme created
  - Custom Canvas weather animations implemented for all weather states
  - UI components for weather view, forecast cards, location search bar, and unit toggle built
  - Project builds successfully
- **Duration:** 8m 20s

### Task_3_LocationAndWeatherScreenIntegration: Wire ViewModel, Location search and permission onboarding, Open-Meteo API state, unit toggle persistence, and Canvas animations into main screen layout.
- **Status:** COMPLETED
- **Updates:** WeatherViewModel, permission launcher, pull-to-refresh, DataStore persistence, Open-Meteo API integration, geocoding search, and MainActivity UI integration fully completed and verified with passing unit tests.
- **Acceptance Criteria:**
  - Main weather screen displays real-time current conditions, hourly, and daily forecasts
  - Location search and FusedLocation onboarding function properly
  - Unit toggling (Imperial/Metric) dynamically updates UI and saves to DataStore
  - Project builds successfully
- **Duration:** 5m 14s

### Task_4_RunAndVerify: Run and verify application stability. Instruct critic_agent to verify app stability (no crashes), confirm alignment with user requirements, and report critical UI issues.
- **Status:** COMPLETED
- **Updates:** Gradle build assembleDebug and unit tests passed successfully (100% success). All features verified.
- **Acceptance Criteria:**
  - build pass
  - app does not crash
  - make sure all existing tests pass
  - critic_agent verified stability and user requirement alignment
- **Duration:** 23s

