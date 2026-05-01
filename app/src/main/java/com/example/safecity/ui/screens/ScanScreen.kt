package com.example.safecity.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

// Define SafeGreen if not available in your theme
val SafeGreen = Color(0xFF4CAF50)

@Composable
fun ScanScreen() {
    var isScanning by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    val infiniteTransition = rememberInfiniteTransition(label = "rotate")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        ), label = "rotation"
    )

    LaunchedEffect(isScanning) {
        if (isScanning) {
            progress = 0f
            while (progress < 1f) {
                delay(50)
                progress += 0.01f
            }
            isScanning = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Environmental Safety Scan", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(text = "Scanning for hidden cameras, trackers, and malicious hotspots.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

        Spacer(modifier = Modifier.height(48.dp))

        Box(contentAlignment = Alignment.Center) {
            // Radar UI
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .background(Color.Black.copy(alpha = 0.05f), CircleShape)
            )

            if (isScanning) {
                Box(
                    modifier = Modifier
                        .size(250.dp)
                        .rotate(rotation)
                        .background(
                            brush = Brush.sweepGradient(
                                colors = listOf(Color.Transparent, SafeGreen.copy(alpha = 0.5f), Color.Transparent)
                            ),
                            shape = CircleShape
                        )
                )
            }

            Icon(
                Icons.Default.RadioButtonChecked,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = if (isScanning) SafeGreen else Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        if (isScanning) {
            Text(text = "Scanning Area: ${(progress * 100).toInt()}%", fontWeight = FontWeight.Bold)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            )
        } else {
            Button(
                onClick = { isScanning = true },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Start Deep Scan")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Detected Items (UI Simulation)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            ScanResultItem(Icons.Default.Wifi, "Wi-Fi", if (progress > 0.4f) "Secure" else "Pending")
            ScanResultItem(Icons.Default.Bluetooth, "Bluetooth", if (progress > 0.7f) "No Trackers" else "Pending")
            ScanResultItem(Icons.Default.Router, "Hidden IP", if (progress > 0.9f) "None" else "Pending")
        }
    }
}

@Composable
fun ScanResultItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, status: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Text(text = status, style = MaterialTheme.typography.labelSmall, color = if (status == "Secure" || status == "No Trackers" || status == "None") SafeGreen else Color.Gray)
    }
}