package com.example.safecity.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.safecity.ui.theme.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.delay

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
    var userLocationDisplay by remember { mutableStateOf<String>("Detecting location...") }
    var isVisible by remember { mutableStateOf(false) }
    
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val routes = listOf(
        SafeRoute("Central Police HQ", "5 min", 99, "0.4 km", "Direct route with 24/7 high-intensity lighting.", "Police"),
        SafeRoute("City Care Hospital", "8 min", 98, "0.9 km", "Emergency zone with active security patrols.", "Hospital"),
        SafeRoute("Safe Zone Square", "15 min", 92, "1.5 km", "Verified public safe haven with CCTV coverage.", "Security"),
        SafeRoute("Night Market Hub", "12 min", 85, "1.1 km", "High-footfall commercial street, safe via main road.", "Market")
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        }
    )

    LaunchedEffect(Unit) {
        isVisible = true
        if (hasLocationPermission) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener { location ->
                    location?.let {
                        userLocationDisplay = "Live: ${String.format("%.4f", it.latitude)}, ${String.format("%.4f", it.longitude)}"
                    } ?: run { userLocationDisplay = "GPS Active • High Accuracy" }
                }
        } else {
            permissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(800)) + slideInVertically(initialOffsetY = { it / 2 })
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Safety Navigator", fontWeight = FontWeight.Black) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                            )
                        )
                    )
                    .padding(padding)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Enhanced Map Visualizer
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .padding(16.dp)
                            .shadow(12.dp, RoundedCornerShape(24.dp))
                            .clip(RoundedCornerShape(24.dp))
                    ) {
                        MapBackgroundSimulation()
                        
                        // Map HUD
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                color = DeepNavy.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.GpsFixed, contentDescription = null, tint = SafeGreen, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(userLocationDisplay, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                FloatingActionButton(
                                    onClick = { },
                                    modifier = Modifier.size(48.dp),
                                    containerColor = Color.White,
                                    contentColor = MaterialTheme.colorScheme.primary
                                ) {
                                    Icon(Icons.Default.MyLocation, contentDescription = null)
                                }
                            }
                        }
                    }

                    // Route Suggestions Section
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            "Smart Safe Routes", 
                            style = MaterialTheme.typography.titleLarge, 
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            "AI-calculated paths with highest lighting & CCTV density", 
                            style = MaterialTheme.typography.bodySmall, 
                            color = MaterialTheme.colorScheme.secondary
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 32.dp)
                        ) {
                            items(routes) { route ->
                                AnimatedSafeRouteCard(route)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MapBackgroundSimulation() {
    var step by remember { mutableFloatStateOf(0f) }
    val primaryColor = MaterialTheme.colorScheme.primary
    
    LaunchedEffect(Unit) {
        while(true) {
            delay(50)
            step = (step + 1) % 1000
        }
    }

    Canvas(modifier = Modifier.fillMaxSize().background(Color(0xFFE3F2FD))) {
        // Grid lines
        for (i in 0..size.width.toInt() step 80) {
            drawLine(Color.White, Offset(i.toFloat(), 0f), Offset(i.toFloat(), size.height), 2f)
        }
        for (i in 0..size.height.toInt() step 80) {
            drawLine(Color.White, Offset(0f, i.toFloat()), Offset(size.width, i.toFloat()), 2f)
        }

        // Mock Streets
        val streetPath = Path().apply {
            moveTo(0f, size.height * 0.3f)
            lineTo(size.width, size.height * 0.3f)
            moveTo(size.width * 0.4f, 0f)
            lineTo(size.width * 0.4f, size.height)
        }
        drawPath(streetPath, Color.White, style = Stroke(width = 40f))
        
        // Safe Route Highlight
        val safePath = Path().apply {
            moveTo(size.width * 0.4f, size.height)
            lineTo(size.width * 0.4f, size.height * 0.3f)
            lineTo(size.width, size.height * 0.3f)
        }
        drawPath(safePath, SafeGreen.copy(alpha = 0.3f), style = Stroke(width = 25f))

        // Pulse Animation for User
        val pulse = (step % 200) / 200f
        drawCircle(
            color = primaryColor.copy(alpha = 1f - pulse),
            radius = 20f + (pulse * 40f),
            center = Offset(size.width * 0.4f, size.height * 0.7f)
        )
        drawCircle(
            color = primaryColor,
            radius = 12f,
            center = Offset(size.width * 0.4f, size.height * 0.7f)
        )
        
        // POI Markers
        drawCircle(Color.Red, radius = 8f, center = Offset(size.width * 0.8f, size.height * 0.3f))
        drawCircle(InfoBlue, radius = 8f, center = Offset(size.width * 0.1f, size.height * 0.5f))
    }
}

@Composable
fun AnimatedSafeRouteCard(route: SafeRoute) {
    var expanded by remember { mutableStateOf(false) }
    val safetyColor = when {
        route.safetyScore >= 95 -> SafeGreen
        route.safetyScore >= 85 -> WarningOrange
        else -> AlertRed
    }

    val icon = when (route.category) {
        "Police" -> Icons.Default.LocalPolice
        "Hospital" -> Icons.Default.LocalHospital
        "Market" -> Icons.Default.Storefront
        else -> Icons.Default.Security
    }

    Card(
        onClick = { expanded = !expanded },
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(route.destination, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("${route.distance} • ${route.duration}", fontSize = 12.sp, color = Color.Gray)
                    }
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${route.safetyScore}%",
                        color = safetyColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                    Text("Safety", fontSize = 10.sp, color = Color.Gray)
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = route.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { /* Launch Navigation */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepNavy)
                ) {
                    Icon(Icons.Default.NearMe, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Initialize Safe Route", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
