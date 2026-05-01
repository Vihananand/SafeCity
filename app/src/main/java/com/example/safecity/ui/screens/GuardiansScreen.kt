package com.example.safecity.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.safecity.ui.theme.SafeGreen

@Composable
fun GuardiansScreen() {
    val guardians = listOf(
        Guardian("Mom", "Online", true),
        Guardian("Dad", "Offline", false),
        Guardian("Brother", "Online", true)
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { }) {
                Icon(Icons.Default.Add, contentDescription = "Add Guardian")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Your Trusted Guardians",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "These contacts will be notified in case of emergency.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(guardians) { guardian ->
                GuardianItem(guardian)
            }
        }
    }
}

data class Guardian(val name: String, val status: String, val isOnline: Boolean)

@Composable
fun GuardianItem(guardian: Guardian) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, contentDescription = null)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = guardian.name, fontWeight = FontWeight.Bold)
                Text(
                    text = guardian.status,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (guardian.isOnline) SafeGreen else Color.Gray
                )
            }
            Icon(
                Icons.Default.Shield,
                contentDescription = null,
                tint = if (guardian.isOnline) SafeGreen else Color.LightGray
            )
        }
    }
}
