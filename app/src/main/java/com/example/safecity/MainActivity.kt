package com.example.safecity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.safecity.data.PreferenceManager
import com.example.safecity.ui.screens.*
import com.example.safecity.ui.theme.SafeCityTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        createNotificationChannel()
        
        setContent {
            val context = LocalContext.current
            val preferenceManager = remember { PreferenceManager(context) }
            val dataStoreDarkMode by preferenceManager.isDarkMode.collectAsState(initial = isSystemInDarkTheme())
            val scope = rememberCoroutineScope()

            SafeCityTheme(darkTheme = dataStoreDarkMode) {
                SafeCityApp(
                    isDarkMode = dataStoreDarkMode,
                    onThemeToggle = { enabled ->
                        scope.launch {
                            preferenceManager.setDarkMode(enabled)
                        }
                    }
                )
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Safety Alerts"
            val descriptionText = "Notifications for local safety incidents"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("SAFETY_CHANNEL", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

@Composable
fun SafeCityApp(isDarkMode: Boolean, onThemeToggle: (Boolean) -> Unit) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(onAnimationFinished = {
                navController.navigate("dashboard") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }
        composable("dashboard") {
            MainScreen(
                navController = navController,
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToAbout = { navController.navigate("about") },
                onNavigateToSavedPlaces = { navController.navigate("saved_places") },
                onNavigateToAlerts = { navController.navigate("alerts") }
            ) { padding ->
                SafetyDashboard(
                    padding = padding,
                    onNavigateToMap = { navController.navigate("map") },
                    onNavigateToReport = { navController.navigate("report") },
                    onNavigateToEmergency = { navController.navigate("emergency") }
                )
            }
        }
        composable("map") {
            CityMapScreen(onBack = { navController.popBackStack() })
        }
        composable("report") {
            ReportIncidentScreen(onBack = { navController.popBackStack() })
        }
        composable("emergency") {
            EmergencyAssistanceScreen(onBack = { navController.popBackStack() })
        }
        composable("settings") {
            SettingsScreen(
                isDarkMode = isDarkMode,
                onThemeToggle = onThemeToggle,
                onBack = { navController.popBackStack() }
            )
        }
        composable("about") {
            AboutScreen(onBack = { navController.popBackStack() })
        }
        composable("saved_places") {
            SavedPlacesScreen(onBack = { navController.popBackStack() })
        }
        composable("alerts") {
            AlertsScreen(onBack = { navController.popBackStack() })
        }
    }
}
