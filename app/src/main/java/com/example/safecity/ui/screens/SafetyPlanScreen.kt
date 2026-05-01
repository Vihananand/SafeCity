package com.example.safecity.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SafetyPlanScreen() {
    val planItems = remember {
        mutableStateListOf(
            SafetyItem("Identify 3 trusted guardians", true),
            SafetyItem("Set up emergency SOS shortcut", false),
            SafetyItem("Download offline safety maps", false),
            SafetyItem("Share live location with family", true),
            SafetyItem("Complete basic self-defense training", false),
            SafetyItem("Add emergency medical info to profile", false)
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.FactCheck, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "My Safety Plan", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
        
        val completedCount = planItems.count { it.isCompleted }
        val progress = completedCount.toFloat() / planItems.size
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(text = "Completion: $completedCount/${planItems.size}")
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp),
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(planItems) { item ->
                SafetyPlanItem(item) {
                    val index = planItems.indexOf(item)
                    planItems[index] = item.copy(isCompleted = !item.isCompleted)
                }
            }
        }
    }
}

data class SafetyItem(val title: String, val isCompleted: Boolean)

@Composable
fun SafetyPlanItem(item: SafetyItem, onToggle: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onToggle
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (item.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (item.isCompleted) Color(0xFF4CAF50) else Color.Gray
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (item.isCompleted) Color.Gray else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
