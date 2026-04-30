package com.example.safecity.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun VaultScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Evidence Vault", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
        Text(text = "Securely stored incident recordings and photos", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        
        Spacer(modifier = Modifier.height(24.dp))

        // Unit I: Grid Layout
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(6) { index ->
                VaultItem(index)
            }
        }

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth().height(56.dp).padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.CloudUpload, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Upload to Cloud Backup")
        }
    }
}

@Composable
fun VaultItem(index: Int) {
    Card(
        modifier = Modifier.fillMaxWidth().aspectRatio(1f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray)) {
                Icon(
                    Icons.Default.VideoLibrary, 
                    contentDescription = null, 
                    modifier = Modifier.align(Alignment.Center).size(48.dp),
                    tint = Color.White.copy(alpha = 0.3f)
                )
            }
            
            Column(modifier = Modifier.align(Alignment.BottomStart).padding(8.dp)) {
                Text(text = "REC_00${index + 1}.mp4", color = Color.White, style = MaterialTheme.typography.labelSmall)
                Text(text = "24 Oct 2023", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall)
            }
            
            IconButton(onClick = { }, modifier = Modifier.align(Alignment.Center)) {
                Icon(Icons.Default.PlayCircle, contentDescription = "Play", tint = Color.White, modifier = Modifier.size(40.dp))
            }
        }
    }
}
