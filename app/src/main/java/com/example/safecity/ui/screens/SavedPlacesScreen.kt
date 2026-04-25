package com.example.safecity.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.UUID

data class SavedPlace(
    val id: String,
    val name: String,
    val address: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedPlacesScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var savedPlaces by remember {
        mutableStateOf(
            listOf(
                SavedPlace("1", "Home", "123 Green Valley, New Delhi", Icons.Default.Home),
                SavedPlace("2", "Work", "Tech Park Tower B, Noida", Icons.Default.Work)
            )
        )
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var newPlaceName by remember { mutableStateOf("") }
    var newPlaceAddress by remember { mutableStateOf("") }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add New Place") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newPlaceName,
                        onValueChange = { newPlaceName = it },
                        label = { Text("Place Name (e.g. Gym)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newPlaceAddress,
                        onValueChange = { newPlaceAddress = it },
                        label = { Text("Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextButton(
                        onClick = {
                            val gmmIntentUri = Uri.parse("geo:0,0?q=select+location")
                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                            mapIntent.setPackage("com.google.android.apps.maps")
                            context.startActivity(mapIntent)
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Pick from Map")
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (newPlaceName.isNotBlank() && newPlaceAddress.isNotBlank()) {
                        savedPlaces = savedPlaces + SavedPlace(
                            id = UUID.randomUUID().toString(),
                            name = newPlaceName,
                            address = newPlaceAddress,
                            icon = Icons.Default.Place
                        )
                        newPlaceName = ""
                        newPlaceAddress = ""
                        showAddDialog = false
                    }
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Places") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.AddLocationAlt, contentDescription = null) },
                text = { Text("Add New Place") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                "Quick access to your safe havens",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (savedPlaces.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No saved places yet.", color = MaterialTheme.colorScheme.outline)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(savedPlaces) { place ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ListItem(
                                headlineContent = { Text(place.name, fontWeight = FontWeight.Bold) },
                                supportingContent = { Text(place.address) },
                                leadingContent = {
                                    IconButton(onClick = {
                                        val gmmIntentUri = Uri.parse("google.navigation:q=${place.address}")
                                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                        mapIntent.setPackage("com.google.android.apps.maps")
                                        context.startActivity(mapIntent)
                                    }) {
                                        Icon(
                                            place.icon,
                                            contentDescription = "Navigate",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                },
                                trailingContent = {
                                    IconButton(onClick = {
                                        savedPlaces = savedPlaces.filter { it.id != place.id }
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
