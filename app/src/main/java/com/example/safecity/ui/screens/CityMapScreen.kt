package com.example.safecity.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

data class SafeRoute(
    val destination: String,
    val duration: String,
    val safetyScore: Int, // 0-100
    val distance: String,
    val description: String,
    val category: String // Police, Hospital, Market, etc.
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityMapScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var userLocationDisplay by remember { mutableStateOf<String>("Detecting...") }
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Mocked nearby safe destinations
    val routes = listOf(
        SafeRoute("Nearest Police Station", "5 min", 99, "0.4 km", "Central Police Station - 24/7 guarded route.", "Police"),
        SafeRoute("City General Hospital", "8 min", 98, "0.9 km", "Emergency zone with well-lit ambulance corridors.", "Hospital"),
        SafeRoute("Main Market (Sector 18)", "15 min", 92, "1.5 km", "Busy commercial area, safest via high-street.", "Market"),
        SafeRoute("Tech Park Security Hub", "12 min", 85, "1.1 km", "Private security patrols active along this path.", "Security")
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        }
    )

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener { location ->
                    location?.let {
                        userLocationDisplay = "Lat: ${String.format("%.4f", it.latitude)}, Lon: ${String.format("%.4f", it.longitude)}"
                    } ?: run { userLocationDisplay = "Location unavailable" }
                }
        } else {
            permissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Safety Navigator") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Refresh could re-fetch location */ }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Mock Map Visualizer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text("Nearby Safety Hubs", fontWeight = FontWeight.Bold)
                    Text(userLocationDisplay, fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                    
                    Row(
                        modifier = Modifier.padding(top = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(modifier = Modifier.size(10.dp), color = Color.Green, shape = MaterialTheme.shapes.extraSmall) {}
                        Text(" High Safety", fontSize = 10.sp, modifier = Modifier.padding(start = 4.dp, end = 12.dp))
                        Surface(modifier = Modifier.size(10.dp), color = Color.Yellow, shape = MaterialTheme.shapes.extraSmall) {}
                        Text(" Moderate", fontSize = 10.sp, modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }

            // Route Suggestions
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Suggested Safe Destinations", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Based on your current location", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                
                Spacer(modifier = Modifier.height(12.dp))
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(routes) { route ->
                        SafeRouteCard(route)
                    }
                }
            }
        }
    }
}

@Composable
fun SafeRouteCard(route: SafeRoute) {
    val context = LocalContext.current
    val safetyColor = when {
        route.safetyScore >= 95 -> Color(0xFF4CAF50)
        route.safetyScore >= 85 -> Color(0xFFFFC107)
        else -> Color(0xFFF44336)
    }

    val icon = when (route.category) {
        "Police" -> Icons.Default.LocalPolice
        "Hospital" -> Icons.Default.LocalHospital
        "Market" -> Icons.Default.Storefront
        else -> Icons.Default.Security
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(modifier = Modifier.weight(1f)) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(route.destination, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("${route.distance} • ${route.duration}", fontSize = 13.sp, color = MaterialTheme.colorScheme.secondary)
                    }
                }
                Surface(
                    color = safetyColor.copy(alpha = 0.15f),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = "${route.safetyScore}% Safe",
                        color = safetyColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = route.description,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    val gmmIntentUri = Uri.parse("google.navigation:q=${route.destination}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    if (mapIntent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(mapIntent)
                    } else {
                        // Fallback if Google Maps is not installed
                        val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${route.destination}"))
                        context.startActivity(fallbackIntent)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small
            ) {
                Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start Safe Navigation")
            }
        }
    }
}
