# Weather App

## Libraries

```kotlin
implementation(libs.androidx.core.ktx)
implementation(libs.androidx.appcompat)
implementation(libs.material)
implementation(libs.androidx.runtime.livedata)
testImplementation(libs.junit)
androidTestImplementation(libs.androidx.junit)
androidTestImplementation(libs.androidx.espresso.core)

implementation(libs.androidx.core.ktx)
implementation(libs.androidx.appcompat)
implementation(libs.material)
implementation(libs.androidx.ui) // Jetpack Compose UI
implementation(libs.androidx.material3) // Compose Material
implementation(libs.androidx.ui.tooling.preview)
implementation(libs.androidx.navigation.compose) // Navigation with Compose

// Hilt for dependency injection
implementation(libs.hilt.android)
ksp(libs.hilt.compiler)

// Retrofit for network requests
implementation(libs.retrofit)
implementation(libs.converter.gson)

// Gson for converting JSON to objects
implementation(libs.gson)

// Lifecycle components (LiveData, ViewModel)
implementation(libs.androidx.lifecycle.viewmodel.ktx)
implementation(libs.androidx.lifecycle.livedata.ktx)

// Testing
testImplementation(libs.junit)
androidTestImplementation(libs.androidx.junit)
androidTestImplementation(libs.androidx.espresso.core)

// Room and Datastore for local data storage (optional for caching)
implementation(libs.androidx.room.runtime)
ksp(libs.androidx.room.compiler)
implementation(libs.androidx.datastore.preferences)

implementation(libs.coil.compose)
```

## Demo

![Demo Video](https://github.com/ifenil/Weather-Tracker/blob/master/1000020559.gif)

## 🥱 Direct Link
### App Link

[![Download](https://openclipart.org/download/218662/Download-Button.svg)](https://github.com/ifenil/Weather-Tracker/blob/master/app-release.apk)
