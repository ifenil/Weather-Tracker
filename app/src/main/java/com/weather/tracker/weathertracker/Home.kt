package com.weather.tracker.weathertracker

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage


@Composable
fun WeatherApp(
    weatherViewModel: WeatherViewModel
) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    val weather = weatherViewModel.weather.value
    val savedCity = weatherViewModel.getSavedCity()
    var showCard by remember { mutableStateOf(false) }
    val isLoading = weatherViewModel.loading.value
    val error = weatherViewModel.error.value

    var showErrorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(error) {
        showErrorDialog = error != null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Search Bar
        CustomSearchBar(
            query,
            onQueryChanged = { query = it },
            onImeActionPerformed = {
                showErrorDialog = error != null
                weatherViewModel.getWeather(query, context)
                showCard = true
            }
        )
        // Error Handling for No Internet or City Not Found
        if (error != null && showErrorDialog) {
            when {
                error.contains("No Internet") -> {
                    ErrorDialog(
                        error = "No Internet Connection",
                        message = "This app requires an active internet connection to function. Please check your connection and restart the app.",
                        onRetry = {
                            query = ""  // Clear the query
                            showErrorDialog = false  // Dismiss the dialog
                        }
                    )
                }
                error.contains("City Not Found") -> {
                    ErrorDialog(
                        error = "City Not Found",
                        message = "We couldn't find the city. Please check the city name and try again.",
                        onRetry = {
                            query = ""  // Clear the query
                            showErrorDialog = false  // Dismiss the dialog
                        }
                    )
                }
                else -> {
                    ErrorDialog(
                        error = "Try Again",
                        message = "Something Went Wrong",
                        onRetry = {
                            query = ""  // Clear the query
                            showErrorDialog = false  // Dismiss the dialog
                        }
                    )
                }
            }
        }

        // Loading Indicator
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFBEB0B0))
            }
        } else {
            // Display weather or other UI based on conditions
            when {
                savedCity != null && query.isEmpty() -> {
                    weather?.let {
                        FullScreenWeatherDetails(weather = it)
                        showCard = false
                    }
                }
                showCard && query.isNotEmpty() -> {
                    weather?.let {
                        SearchResultCard(weather = it, onClick = {
                            weatherViewModel.saveCity(query, context)
                            query = ""
                            showCard = false
                        })
                    }
                }
                savedCity == null -> {
                    NoCitySelected()
                }
            }
        }
    }
}

@Composable
fun ErrorDialog(error: String, message: String, onRetry: () -> Unit) {
    AlertDialog(
        onDismissRequest = { /* Do nothing or handle dismiss action */ },
        title = { Text(error) },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    )
}

@Composable
fun CustomSearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    onImeActionPerformed: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 44.dp, start = 24.dp, end = 24.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF2F2F2))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = query,
                onValueChange = onQueryChanged,
                textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onImeActionPerformed() }
                ),
                cursorBrush = SolidColor(Color.Black),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (query.isEmpty()) {
                            Text(
                                text = "Search Location",
                                color = Color(0xFFC4C4C4),
                                fontFamily = FontFamily(Font(R.font.popreg)),
                                fontSize = 15.sp
                            )
                        }
                        innerTextField()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )

            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = Color(0xFFC4C4C4)
            )
        }
    }
}

@Composable
fun FullScreenWeatherDetails(weather: WeatherResponse) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = "https:${weather.current.condition.icon}",
                contentDescription = "Weather Icon",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(123.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {

                Box(
                    modifier = Modifier.wrapContentWidth(Alignment.Start)
                ) {
                    Text(
                        text = weather.location.name,
                        color = Color.Black,
                        fontFamily = FontFamily(Font(R.font.popsemi)),
                        fontSize = 30.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(end = 4.dp)
                    )

                    Image(
                        painter = painterResource(R.drawable.location),
                        contentDescription = "City Name Icon",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.CenterEnd)
                    )
                }
            }

            Row {
                Text(
                    text = "${weather.current.temperature.toInt()}",
                    color = Color.Black,
                    fontFamily = FontFamily(Font(R.font.popsemi)),
                    fontSize = 70.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Text(
                    text = "°",
                    color = Color.Black,
                    fontFamily = FontFamily(Font(R.font.popsemi)),
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 12.dp)
                )

            }

            Row(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF2F2F2))
                    .padding(16.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Humidity",
                        color = Color(0xFFC4C4C4),
                        fontFamily = FontFamily(Font(R.font.popreg)),
                        fontSize = 12.sp
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = "${weather.current.humidity}%",
                        color = Color(0xFF9A9A9A),
                        fontFamily = FontFamily(Font(R.font.popmid)),
                        fontSize = 15.sp
                    )
                }

                Spacer(Modifier.weight(1f))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "UV",
                        color = Color(0xFFC4C4C4),
                        fontFamily = FontFamily(Font(R.font.popmid)),
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "${weather.current.uv.toInt()}",
                        color = Color(0xFF9A9A9A),
                        fontFamily = FontFamily(Font(R.font.popmid)),
                        fontSize = 15.sp
                    )
                }

                Spacer(Modifier.weight(1f))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Feels Like",
                        color = Color(0xFFC4C4C4),
                        fontFamily = FontFamily(Font(R.font.popmid)),
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "${weather.current.FeelsLike.toInt()}°",
                        color = Color(0xFF9A9A9A),
                        fontFamily = FontFamily(Font(R.font.popmid)),
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SearchResultCard(weather: WeatherResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF2F2F2)),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = weather.location.name,
                    color = Color.Black,
                    fontFamily = FontFamily(Font(R.font.popsemi)),
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 3.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row {
                    Text(
                        text = "${weather.current.temperature.toInt()}",
                        color = Color.Black,
                        fontFamily = FontFamily(Font(R.font.popsemi)),
                        fontSize = 60.sp,
                    )

                    Text(
                        text = "°",
                        color = Color.Black,
                        fontFamily = FontFamily(Font(R.font.popsemi)),
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 12.dp, start = 8.dp)
                    )
                }
            }

            // Image section, the image will take up a fixed space
            AsyncImage(
                model = "https:${weather.current.condition.icon}",
                contentDescription = "Weather Icon",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(100.dp, 87.dp)
            )
        }
    }
}

@Composable
fun NoCitySelected() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "No City Selected",
            color = Color.Black,
            fontFamily = FontFamily(Font(R.font.popsemi)),
            fontSize = 30.sp
        )

        Text(
            text = "Please Search For A City",
            color = Color.Black,
            fontFamily = FontFamily(Font(R.font.popsemi)),
            fontSize = 15.sp
        )
    }
}