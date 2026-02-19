package com.weathergranny

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weathergranny.data.local.PreferenceStore
import com.weathergranny.data.model.TemperatureUnit
import com.weathergranny.data.model.UserSettings
import com.weathergranny.data.network.WeatherApiService
import com.weathergranny.data.repository.WeatherRepositoryImpl
import com.weathergranny.notifications.NotificationScheduler
import com.weathergranny.ui.MainUiState
import com.weathergranny.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private val AppBg = Color(0xFFCEC8AC)
private val PanelBg = Color(0xFFEDE4C7)
private val Ink = Color(0xFF262873)
private val SoftInk = Color(0xFF5E6297)
private val BubbleBg = Color(0xFFF2BFAF)
private val BubbleAccent = Color(0xFFA8D5E6)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    private val viewModel: MainViewModel by viewModels {
        MainViewModel.Factory(
            WeatherRepositoryImpl(
                apiService = Retrofit.Builder()
                    .baseUrl("https://api.weatherapi.com/")
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()
                    .create(WeatherApiService::class.java),
                apiKey = BuildConfig.WEATHER_API_KEY,
                preferenceStore = PreferenceStore(applicationContext)
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askNotificationPermissionIfNeeded()

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WeatherGrannyApp(viewModel)
                }
            }
        }
    }

    private fun askNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

private enum class AppScreen { HOME, SETTINGS, ABOUT }

@Composable
fun WeatherGrannyApp(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedScreen by remember { mutableStateOf(AppScreen.HOME) }
    val context = LocalContext.current

    LaunchedEffect(Unit) { viewModel.loadWeather() }

    Scaffold(
        containerColor = AppBg,
        bottomBar = {
            NavigationBar(containerColor = AppBg) {
                NavigationBarItem(
                    selected = selectedScreen == AppScreen.HOME,
                    onClick = { selectedScreen = AppScreen.HOME },
                    icon = { Text("🏠") },
                    label = { Text("TO GRANNY", color = Ink) }
                )
                NavigationBarItem(
                    selected = selectedScreen == AppScreen.SETTINGS,
                    onClick = { selectedScreen = AppScreen.SETTINGS },
                    icon = { Text("⚙️") },
                    label = { Text("SETTINGS", color = Ink) }
                )
                NavigationBarItem(
                    selected = selectedScreen == AppScreen.ABOUT,
                    onClick = { selectedScreen = AppScreen.ABOUT },
                    icon = { Text("ℹ️") },
                    label = { Text("ABOUT", color = Ink) }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBg)
                .padding(padding)
                .padding(12.dp)
        ) {
            when (selectedScreen) {
                AppScreen.HOME -> HomeScreen(
                    state = uiState,
                    onSchedule = {
                        NotificationScheduler.scheduleDailyNotification(
                            context = context,
                            hour = uiState.settings.notificationHour,
                            minute = uiState.settings.notificationMinute,
                            message = uiState.grannyMessage
                        )
                    }
                )

                AppScreen.SETTINGS -> SettingsScreen(
                    settings = uiState.settings,
                    onSave = viewModel::saveSettings
                )

                AppScreen.ABOUT -> AboutScreen()
            }
        }
    }
}

@Composable
private fun HomeScreen(state: MainUiState, onSchedule: () -> Unit) {
    Panel {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SpeechBubble(
                text = state.weather?.let { "IT’S 5°C WARMER
THAN YESTERDAY!" }
                    ?: "IT’S 5°C WARMER
THAN YESTERDAY!"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.wg_frame),
                        contentDescription = "Frame",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.wg_sun),
                            contentDescription = "Sun",
                            modifier = Modifier.size(86.dp)
                        )
                        Text(
                            text = state.weather?.let { "${it.temperatureCelsius.toInt()}°C" } ?: "24°C",
                            color = Ink,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Image(
                            painter = painterResource(id = R.drawable.wg_grass),
                            contentDescription = "Grass",
                            modifier = Modifier.fillMaxWidth(0.6f)
                        )
                    }
                }

                Image(
                    painter = painterResource(id = R.drawable.wg_granny),
                    contentDescription = "Granny",
                    modifier = Modifier.size(150.dp)
                )
            }

            Image(
                painter = painterResource(id = R.drawable.wg_granny_says),
                contentDescription = "Granny says logo",
                modifier = Modifier.fillMaxWidth(0.62f)
            )

            Text(
                text = "LEAVE THAT JACKET AT HOME, DARLING;
WE DON’T WANT YOU MELTING OUT THERE!",
                color = Ink,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = onSchedule,
                colors = ButtonDefaults.buttonColors(containerColor = Ink),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("SCHEDULE NOTIFICATION", color = PanelBg)
            }

            state.error?.let { Text("Error: $it", color = Color(0xFF7A1F1F)) }
        }
    }
}

@Composable
private fun SettingsScreen(settings: UserSettings, onSave: (UserSettings) -> Unit) {
    var draft by remember(settings) { mutableStateOf(settings) }
    val languages = listOf("English", "Türkçe", "Deutsch")
    val cities = listOf("Istanbul", "Kadikoy", "Besiktas", "Ankara", "Cankaya", "Izmir", "Bornova")
    var showCityMenu by remember { mutableStateOf(false) }
    var showUnitMenu by remember { mutableStateOf(false) }
    var showHourMenu by remember { mutableStateOf(false) }
    var showMinuteMenu by remember { mutableStateOf(false) }

    Panel {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            SectionTitle("SETTINGS")

            SettingsGroup("Language & Location Settings") {
                Text("Language: ${draft.language}", color = SoftInk)
                Button(
                    onClick = {
                        val idx = languages.indexOf(draft.language).takeIf { it >= 0 } ?: 0
                        draft = draft.copy(language = languages[(idx + 1) % languages.size])
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Ink)
                ) {
                    Text("Change Language", color = PanelBg)
                }

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        "Auto-detect Location: ${if (draft.automaticLocation) "ON" else "OFF"}",
                        color = SoftInk
                    )
                    Switch(
                        checked = draft.automaticLocation,
                        onCheckedChange = { draft = draft.copy(automaticLocation = it) }
                    )
                }

                if (!draft.automaticLocation) {
                    Box {
                        Button(
                            onClick = { showCityMenu = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Ink)
                        ) {
                            Text("Choose City / District", color = PanelBg)
                        }

                        DropdownMenu(expanded = showCityMenu, onDismissRequest = { showCityMenu = false }) {
                            cities.forEach { city ->
                                DropdownMenuItem(
                                    text = { Text(city) },
                                    onClick = {
                                        draft = draft.copy(manualLocation = city)
                                        showCityMenu = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = draft.manualLocation,
                        onValueChange = { draft = draft.copy(manualLocation = it) },
                        label = { Text("Set Location Manually (city / district / neighborhood)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            SettingsGroup("Notification Settings") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Notification Time", color = SoftInk)

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box {
                            Button(
                                onClick = { showHourMenu = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Ink),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(draft.notificationHour.toString().padStart(2, '0'), color = PanelBg)
                            }
                            DropdownMenu(expanded = showHourMenu, onDismissRequest = { showHourMenu = false }) {
                                (0..23).forEach { hour ->
                                    DropdownMenuItem(
                                        text = { Text(hour.toString().padStart(2, '0')) },
                                        onClick = {
                                            draft = draft.copy(notificationHour = hour)
                                            showHourMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        Text(":", color = SoftInk, modifier = Modifier.align(Alignment.CenterVertically))

                        Box {
                            Button(
                                onClick = { showMinuteMenu = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Ink),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(draft.notificationMinute.toString().padStart(2, '0'), color = PanelBg)
                            }
                            DropdownMenu(expanded = showMinuteMenu, onDismissRequest = { showMinuteMenu = false }) {
                                (0..55 step 5).forEach { minute ->
                                    DropdownMenuItem(
                                        text = { Text(minute.toString().padStart(2, '0')) },
                                        onClick = {
                                            draft = draft.copy(notificationMinute = minute)
                                            showMinuteMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            SettingsGroup("Temperature Preferences") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Temperature Unit", color = SoftInk)
                    Box {
                        Button(
                            onClick = { showUnitMenu = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Ink),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                if (draft.temperatureUnit == TemperatureUnit.CELSIUS) "Celcius" else "Fahrenheit",
                                color = PanelBg
                            )
                        }

                        DropdownMenu(expanded = showUnitMenu, onDismissRequest = { showUnitMenu = false }) {
                            DropdownMenuItem(
                                text = { Text("Celcius") },
                                onClick = {
                                    draft = draft.copy(temperatureUnit = TemperatureUnit.CELSIUS)
                                    showUnitMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Fahrenheit") },
                                onClick = {
                                    draft = draft.copy(temperatureUnit = TemperatureUnit.FAHRENHEIT)
                                    showUnitMenu = false
                                }
                            )
                        }
                    }
                }
            }

            SettingsGroup("Feedback & Support") {
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = Ink)
                ) {
                    Text("Send Feedback", color = PanelBg)
                }
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = Ink)
                ) {
                    Text("Help / FAQ", color = PanelBg)
                }
            }

            Button(
                onClick = { onSave(draft) },
                colors = ButtonDefaults.buttonColors(containerColor = Ink),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(10.dp)
            ) {
                Text("SAVE SETTINGS", color = PanelBg)
            }
        }
    }
}

@Composable
private fun AboutScreen() {
    Panel {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            SectionTitle("ABOUT")

            SettingsGroup("INFO") {
                Text("Granny Weather helps you compare today with yesterday.", color = SoftInk)
                Text("You receive warm, caring weather advice daily.", color = SoftInk)
                Text("Long text", color = SoftInk)
                Text("Long text", color = SoftInk)
            }

            SettingsGroup("VERSION") {
                Text("v1.0", color = SoftInk)
                Text("Contact: support@weathergranny.app", color = SoftInk)
            }
        }
    }
}

@Composable
private fun Panel(content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = PanelBg),
        modifier = Modifier
            .fillMaxSize()
            .border(1.dp, SoftInk, RoundedCornerShape(22.dp))
            .padding(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            content()
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SpeechBubble(text: String) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BubbleAccent),
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = BubbleBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, Ink),
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = text,
                    color = Ink,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = MaterialTheme.typography.displaySmall.lineHeight,
                    modifier = Modifier.weight(1f)
                )
                Text("🌼", style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.headlineMedium,
        color = Ink,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun SettingsGroup(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            color = Ink,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWeatherGrannyApp() {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize().background(AppBg), contentAlignment = Alignment.Center) {
            Text("WeatherGranny Vintage Preview", color = Ink)
        }
    }
}
