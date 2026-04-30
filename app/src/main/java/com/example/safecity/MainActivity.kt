package com.example.safecity

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.safecity.receiver.AlarmReceiver
import com.example.safecity.ui.Screen
import com.example.safecity.ui.screens.*
import com.example.safecity.ui.theme.SafeCityTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        enableEdgeToEdge()
        setContent {
            var isDarkMode by remember { mutableStateOf(false) }
            SafeCityTheme(darkTheme = isDarkMode) {
                var showSplash by remember { mutableStateOf(true) }
                if (showSplash) {
                    SplashScreen(onAnimationFinished = { showSplash = false })
                } else {
                    MainScreen(
                        isDarkMode = isDarkMode,
                        onThemeToggle = { isDarkMode = it }
                    )
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Safety Alerts"
            val channel = NotificationChannel("safety_alerts", name, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Notifications for safety checks and alerts"
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    isDarkMode: Boolean,
    onThemeToggle: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        delay(45000)
        snackbarHostState.showSnackbar(
            message = "Unusual activity detected in your vicinity. Stay alert!",
            actionLabel = "SOS",
            duration = SnackbarDuration.Long
        )
        setSafetyAlarm(context, 1000)
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { _ -> }
    LaunchedEffect(Unit) {
        permissionLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE
        ))
    }

    val bottomNavScreens = listOf(Screen.Dashboard, Screen.Map, Screen.Report, Screen.Community, Screen.Emergency)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("SafeCity Pro", style = MaterialTheme.typography.headlineLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    Text("Ultimate Safety Suite v6.5", style = MaterialTheme.typography.bodySmall)
                }
                HorizontalDivider()
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        NavigationDrawerItem(
                            label = { Text("Profile Dashboard") },
                            selected = false,
                            onClick = { navController.navigate(Screen.Profile.route); scope.launch { drawerState.close() } },
                            icon = { Icon(Icons.Default.AccountBox, null) },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                        NavigationDrawerItem(
                            label = { Text("Evidence Vault") },
                            selected = false,
                            onClick = { navController.navigate(Screen.Vault.route); scope.launch { drawerState.close() } },
                            icon = { Icon(Icons.Default.PhotoLibrary, null) },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                        NavigationDrawerItem(
                            label = { Text("Safety Training") },
                            selected = false,
                            onClick = { navController.navigate(Screen.Training.route); scope.launch { drawerState.close() } },
                            icon = { Icon(Icons.Default.Quiz, null) },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                        NavigationDrawerItem(
                            label = { Text("Environment Scanner") },
                            selected = false,
                            onClick = { navController.navigate(Screen.Scan.route); scope.launch { drawerState.close() } },
                            icon = { Icon(Icons.Default.WifiTethering, null) },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                        NavigationDrawerItem(
                            label = { Text("Emergency Recorder") },
                            selected = false,
                            onClick = { navController.navigate(Screen.Recorder.route); scope.launch { drawerState.close() } },
                            icon = { Icon(Icons.Default.Mic, null) },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                        NavigationDrawerItem(
                            label = { Text("Safe Havens") },
                            selected = false,
                            onClick = { navController.navigate(Screen.Havens.route); scope.launch { drawerState.close() } },
                            icon = { Icon(Icons.Default.Apartment, null) },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                        NavigationDrawerItem(
                            label = { Text("Safety Checklist") },
                            selected = false,
                            onClick = { navController.navigate(Screen.SafetyPlan.route); scope.launch { drawerState.close() } },
                            icon = { Icon(Icons.AutoMirrored.Filled.FactCheck, null) },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                        HorizontalDivider(Modifier.padding(vertical = 8.dp))
                        Text("Settings & Config", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.labelMedium)
                        NavigationDrawerItem(
                            label = { Text("App Settings") },
                            selected = false,
                            onClick = { navController.navigate(Screen.Settings.route); scope.launch { drawerState.close() } },
                            icon = { Icon(Icons.Default.Settings, null) },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val route = navBackStackEntry?.destination?.route
                if (route != Screen.WalkingMode.route) {
                    TopAppBar(
                        title = {
                            val title = route?.substringAfterLast(".")?.replaceFirstChar { it.uppercase() } ?: "SafeCity"
                            Text(text = title)
                        },
                        navigationIcon = {
                            if (navController.previousBackStackEntry != null && route !in listOf(Screen.Dashboard.route, Screen.Map.route, Screen.Report.route, Screen.Community.route, Screen.Emergency.route)) {
                                IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                            } else {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, contentDescription = "Menu") }
                            }
                        },
                        actions = {
                            IconButton(onClick = { navController.navigate(Screen.Search.route) }) { Icon(Icons.Default.Search, contentDescription = "Search") }
                            IconButton(onClick = { navController.navigate(Screen.Alerts.route) }) {
                                BadgedBox(badge = { Badge { Text("15") } }) { Icon(Icons.Default.Notifications, contentDescription = "Alerts") }
                            }
                        }
                    )
                }
            },
            bottomBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val route = navBackStackEntry?.destination?.route
                val hidden = listOf(Screen.WalkingMode.route, Screen.Search.route, Screen.IncidentDetail.route, Screen.Profile.route, Screen.Vault.route, Screen.Training.route, Screen.Scan.route, Screen.Havens.route, Screen.SafetyPlan.route, Screen.Recorder.route)
                if (route !in hidden) {
                    NavigationBar {
                        val currentDestination = navBackStackEntry?.destination
                        bottomNavScreens.forEach { screen ->
                            NavigationBarItem(
                                icon = { Icon(screen.icon, contentDescription = null) },
                                label = { Text(screen.title) },
                                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(navController, Screen.Dashboard.route, Modifier.padding(innerPadding)) {
                composable(Screen.Dashboard.route) {
                    DashboardScreen(
                        onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                        onNavigateToWalking = { navController.navigate(Screen.WalkingMode.route) },
                        onNavigateToIncident = { navController.navigate(Screen.IncidentDetail.route) }
                    )
                }
                composable(Screen.Map.route) { MapScreen() }
                composable(Screen.Community.route) { CommunityScreen() }
                composable(Screen.Report.route) { ReportIncidentScreen(onBack = { navController.popBackStack() }) }
                composable(Screen.Emergency.route) { EmergencyAssistanceScreen(onBack = { navController.popBackStack() }) }
                composable(Screen.Alerts.route) { AlertsScreen(onBack = { navController.popBackStack() }) }
                composable(Screen.Profile.route) { ProfileScreen() }
                composable(Screen.Settings.route) {
                    SettingsScreen(
                        isDarkMode = isDarkMode,
                        onThemeToggle = onThemeToggle,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.Search.route) { SearchScreen() }
                composable(Screen.WalkingMode.route) { WalkingModeScreen { navController.popBackStack() } }
                composable(Screen.Guardians.route) { GuardiansScreen() }
                composable(Screen.IncidentDetail.route) { IncidentDetailScreen { navController.popBackStack() } }
                composable(Screen.Vault.route) { VaultScreen() }
                composable(Screen.Training.route) { TrainingScreen() }
                composable(Screen.Scan.route) { ScanScreen() }
                composable(Screen.Havens.route) { HavensScreen() }
                composable(Screen.SafetyPlan.route) { SafetyPlanScreen() }
                composable(Screen.Recorder.route) { RecorderScreen() }
            }
        }
    }
}

private fun setSafetyAlarm(context: Context, delayMillis: Long) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    val triggerTime = System.currentTimeMillis() + delayMillis
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (alarmManager.canScheduleExactAlarms()) alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        else alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    } else alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
}
