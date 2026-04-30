package com.example.safecity.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safecity.ui.theme.SafeGreen
import com.example.safecity.ui.theme.WarningOrange
import kotlinx.coroutines.delay

@Composable
fun MapScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Safe Routes", "Heatmap", "Live CCTV")
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(1000)) + slideInVertically(initialOffsetY = { it / 2 })
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> LiveMapSimulation()
                    1 -> HeatmapSimulation()
                    2 -> CctvSimulation()
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Search for safety zones...", color = Color.Gray)
                    }
                }

                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                            val alpha by infiniteTransition.animateFloat(
                                initialValue = 0.4f,
                                targetValue = 1f,
                                animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
                                label = "alpha"
                            )
                            Box(modifier = Modifier.size(12.dp).background(SafeGreen, RoundedCornerShape(50)).padding(2.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Live Safety Tracking Active", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { 0.75f },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = SafeGreen,
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                    }
                }

                FloatingActionButton(
                    onClick = { },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 120.dp, end = 16.dp),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Locate Me")
                }
            }
        }
    }
}

@Composable
fun LiveMapSimulation() {
    var step by remember { mutableFloatStateOf(0f) }
    val primaryColor = MaterialTheme.colorScheme.primary
    LaunchedEffect(Unit) {
        while(true) {
            delay(50)
            step = (step + 1) % 1000
        }
    }

    Canvas(modifier = Modifier.fillMaxSize().background(Color(0xFFE8EAF6))) {
        for (i in 0..size.width.toInt() step 100) {
            drawLine(Color.LightGray.copy(alpha = 0.3f), Offset(i.toFloat(), 0f), Offset(i.toFloat(), size.height), 1f)
        }
        for (i in 0..size.height.toInt() step 100) {
            drawLine(Color.LightGray.copy(alpha = 0.3f), Offset(0f, i.toFloat()), Offset(size.width, i.toFloat()), 1f)
        }

        val path = Path().apply {
            moveTo(100f, 100f)
            lineTo(size.width - 100f, 100f)
            lineTo(size.width - 100f, size.height - 200f)
            lineTo(100f, size.height - 200f)
            close()
        }
        drawPath(path, Color.Gray.copy(alpha = 0.5f), style = Stroke(width = 40f))
        drawPath(path, SafeGreen.copy(alpha = 0.4f), style = Stroke(width = 25f))

        val pos = (step / 1000f)
        val currentX = 100f + (size.width - 200f) * pos
        drawCircle(color = primaryColor, radius = 20f, center = Offset(currentX, 100f))
        drawCircle(color = primaryColor.copy(alpha = 0.3f), radius = 40f, center = Offset(currentX, 100f))
    }
}

@Composable
fun HeatmapSimulation() {
    Canvas(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        drawCircle(Color.Red.copy(alpha = 0.2f), radius = 250f, center = Offset(300f, 400f))
        drawCircle(WarningOrange.copy(alpha = 0.2f), radius = 350f, center = Offset(size.width - 300f, size.height / 2))
        drawCircle(SafeGreen.copy(alpha = 0.2f), radius = 450f, center = Offset(size.width / 2, size.height - 400f))
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("High Safety Zones Density", fontWeight = FontWeight.ExtraBold, color = Color.DarkGray, fontSize = 20.sp)
    }
}

@Composable
fun CctvSimulation() {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly) {
            Row(modifier = Modifier.weight(1f)) {
                CctvCamera("Mall Entrance 01")
                CctvCamera("Parking Lot B")
            }
            Row(modifier = Modifier.weight(1f)) {
                CctvCamera("Subway Terminal")
                CctvCamera("Main St Crossing")
            }
        }
    }
}

@Composable
fun RowScope.CctvCamera(label: String) {
    Box(modifier = Modifier.weight(1f).fillMaxHeight().padding(4.dp).background(Color(0xFF1A1A1A), RoundedCornerShape(8.dp))) {
        val infiniteTransition = rememberInfiniteTransition(label = "recording")
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
            label = "alpha"
        )
        Row(modifier = Modifier.padding(12.dp).align(Alignment.TopStart), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(Color.Red.copy(alpha = alpha), RoundedCornerShape(50)))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "LIVE • $label", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Icon(
            Icons.Default.Videocam,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.1f),
            modifier = Modifier.size(120.dp).align(Alignment.Center)
        )
    }
}
