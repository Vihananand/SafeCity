package com.example.safecity.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safecity.ui.theme.*

@Composable
fun HavensScreen() {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    val havens = listOf(
        Haven("Central Police Station", "24/7 Police Support", "450m", "100", InfoBlue),
        Haven("City Community Center", "Safe Shelter", "1.2km", "555-0123", SafeGreen),
        Haven("St. Mary's Hospital", "Medical Emergency", "800m", "102", AlertRed),
        Haven("Guardian Safe Zone A", "Private Security Hub", "2.5km", "555-9999", WarningOrange)
    )

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
            enter = fadeIn(tween(800)) + slideInVertically()
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Safe Havens Nearby",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "Verified locations for immediate safety",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    items(havens) { haven ->
                        HavenItemCard(haven)
                    }
                }
            }
        }
    }
}

data class Haven(val name: String, val type: String, val distance: String, val phone: String, val themeColor: Color)

@Composable
fun HavenItemCard(haven: Haven) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(16.dp),
                color = haven.themeColor.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when(haven.type) {
                            "24/7 Police Support" -> Icons.Default.LocalPolice
                            "Medical Emergency" -> Icons.Default.LocalHospital
                            else -> Icons.Default.Apartment
                        },
                        contentDescription = null, 
                        tint = haven.themeColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = haven.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 17.sp)
                Text(text = haven.type, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.5f))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = SafeGreen
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = haven.distance, style = MaterialTheme.typography.labelSmall, color = SafeGreen, fontWeight = FontWeight.Bold)
                }
            }

            Row {
                IconButton(onClick = { }, modifier = Modifier.background(Color.White.copy(alpha = 0.05f), CircleShape)) {
                    Icon(Icons.Default.Phone, contentDescription = "Call", tint = InfoBlue, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { }, modifier = Modifier.background(Color.White.copy(alpha = 0.05f), CircleShape)) {
                    Icon(Icons.Default.Directions, contentDescription = "Navigate", tint = SafeGreen, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
