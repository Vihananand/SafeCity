package com.example.safecity.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safecity.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun ScanScreen() {
    var isScanning by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) { isVisible = true }

    val infiniteTransition = rememberInfiniteTransition(label = "rotate")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        ), label = "rotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "pulse"
    )

    LaunchedEffect(isScanning) {
        if (isScanning) {
            progress = 0f
            while (progress < 1f) {
                delay(40)
                progress += 0.01f
            }
            isScanning = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DeepNavy, MaterialTheme.colorScheme.surface)
                )
            )
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(800)) + expandVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Environment Scanner",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "AI-powered detection for hidden threats",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(60.dp))

                Box(contentAlignment = Alignment.Center) {
                    // Outer pulsing rings
                    Box(
                        modifier = Modifier
                            .size(280.dp)
                            .scale(pulseScale)
                            .border(2.dp, SafeGreen.copy(alpha = 0.2f), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .scale(pulseScale * 0.9f)
                            .border(1.dp, InfoBlue.copy(alpha = 0.2f), CircleShape)
                    )

                    // Radar Radar
                    Box(
                        modifier = Modifier
                            .size(250.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.2f))
                    )

                    if (isScanning) {
                        Box(
                            modifier = Modifier
                                .size(250.dp)
                                .rotate(rotation)
                                .background(
                                    brush = Brush.sweepGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            SafeGreen.copy(alpha = 0.6f),
                                            Color.Transparent
                                        )
                                    ),
                                    shape = CircleShape
                                )
                        )
                    }

                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = if (isScanning) SafeGreen else Color.White.copy(alpha = 0.1f),
                        shadowElevation = 8.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                if (isScanning) Icons.Default.Radar else Icons.Default.Security,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = if (isScanning) Color.White else SafeGreen
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(60.dp))

                if (isScanning) {
                    Text(
                        text = "Analyzing Signals: ${(progress * 100).toInt()}%",
                        color = SafeGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = SafeGreen,
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )
                } else {
                    Button(
                        onClick = { isScanning = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SafeGreen)
                    ) {
                        Icon(Icons.Default.WifiTethering, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Start Deep Analysis", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ScanResultCard(Modifier.weight(1f), Icons.Default.Router, "Network", progress > 0.4f)
                    ScanResultCard(Modifier.weight(1f), Icons.Default.Bluetooth, "Trackers", progress > 0.8f)
                    ScanResultCard(Modifier.weight(1f), Icons.Default.Videocam, "Cameras", progress > 0.95f)
                }
            }
        }
    }
}

@Composable
fun ScanResultCard(modifier: Modifier, icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, isSecure: Boolean) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = if (isSecure) SafeGreen else InfoBlue)
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text(if (isSecure) "SECURE" else "SCANNING", color = if (isSecure) SafeGreen else Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}
