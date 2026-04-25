package com.example.safecity.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.*

@Composable
fun SafetyDashboard(
    padding: PaddingValues,
    onNavigateToMap: () -> Unit,
    onNavigateToReport: () -> Unit,
    onNavigateToEmergency: () -> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var currentCity by remember { mutableStateOf("Detecting...") }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                // Permission granted, re-detect city logic if needed
            }
        }
    )

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener { location ->
                    location?.let {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        try {
                            val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                            currentCity = addresses?.firstOrNull()?.locality ?: "Nearby Area"
                        } catch (e: Exception) {
                            currentCity = "Your Area"
                        }
                    } ?: run { currentCity = "Your Area" }
                }
        } else {
            locationPermissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Welcome, Citizen", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Your safety is our priority in $currentCity.", color = MaterialTheme.colorScheme.secondary)
        }

        item {
            SafetyAlertPanel(currentCity)
        }

        item {
            QuickActions(
                onNavigateToMap = onNavigateToMap,
                onNavigateToReport = onNavigateToReport,
                onNavigateToEmergency = onNavigateToEmergency
            )
        }

        item {
            Text("Recent Activity", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        // Mock recent activity
        items(3) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                ListItem(
                    headlineContent = { Text("Safe route found in $currentCity") },
                    supportingContent = { Text("20 mins ago • Green Zone") },
                    leadingContent = { Icon(Icons.Default.Route, contentDescription = null) }
                )
            }
        }
    }
}

@Composable
fun SafetyAlertPanel(city: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Live Alerts for $city", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
            }
            Text("General caution advised in $city. Stay updated with local news for safety protocols.", modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
fun QuickActions(
    onNavigateToMap: () -> Unit,
    onNavigateToReport: () -> Unit,
    onNavigateToEmergency: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Map,
                label = "Safety Map",
                onClick = onNavigateToMap
            )
            ActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Report,
                label = "Report Incident",
                onClick = onNavigateToReport
            )
        }
        ActionButton(
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Default.Phone,
            label = "Emergency SOS",
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError,
            onClick = onNavigateToEmergency
        )
    }
}

@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onPrimaryContainer,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColor)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(label)
        }
    }
}
