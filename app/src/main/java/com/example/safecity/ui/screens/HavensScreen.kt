package com.example.safecity.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.safecity.ui.theme.SafeGreen

@Composable
fun HavensScreen() {
    val havens = listOf(
        Haven("Central Police Station", "24/7 Police Support", "450m", "100"),
        Haven("City Community Center", "Safe Shelter", "1.2km", "555-0123"),
        Haven("St. Mary's Hospital", "Medical Emergency", "800m", "102"),
        Haven("Guardian Safe Zone A", "Private Security Hub", "2.5km", "555-9999")
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Safe Havens Nearby",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Verified locations where you can find immediate help.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(havens) { haven ->
                HavenCard(haven)
            }
        }
    }
}

data class Haven(val name: String, val type: String, val distance: String, val phone: String)

@Composable
fun HavenCard(haven: Haven) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Apartment, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = haven.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(text = haven.type, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = SafeGreen
                    )
                    Text(text = haven.distance, style = MaterialTheme.typography.labelSmall, color = SafeGreen)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                IconButton(onClick = { /* TODO: Implement call functionality */ }) {
                    Icon(Icons.Default.Phone, contentDescription = "Call", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = { /* TODO: Implement navigation functionality */ }) {
                    Icon(Icons.Default.Directions, contentDescription = "Navigate", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
