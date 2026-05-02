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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safecity.ui.theme.*

@Composable
fun GuardiansScreen() {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    val guardians = listOf(
        Guardian("Mom", "Family", "Online", true),
        Guardian("Dad", "Family", "Offline", false),
        Guardian("Brother", "Family", "Online", true),
        Guardian("Security Office", "Professional", "Active", true)
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
            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { },
                        containerColor = SafeGreen,
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier.shadow(8.dp, CircleShape)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Guardian")
                    }
                },
                containerColor = Color.Transparent
            ) { padding ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
                ) {
                    item {
                        Text(
                            text = "Trusted Guardians",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "Instant alerts will be sent to these contacts",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(guardians) { guardian ->
                        GuardianItemCard(guardian)
                    }
                }
            }
        }
    }
}

data class Guardian(val name: String, val relation: String, val status: String, val isOnline: Boolean)

@Composable
fun GuardianItemCard(guardian: Guardian) {
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
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                    }
                }
                Surface(
                    modifier = Modifier.size(16.dp),
                    shape = CircleShape,
                    color = if (guardian.isOnline) SafeGreen else Color.Gray,
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color.Black)
                ) {}
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = guardian.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                Text(text = guardian.relation, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { }, modifier = Modifier.background(Color.White.copy(alpha = 0.05f), CircleShape)) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = InfoBlue, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { }, modifier = Modifier.background(Color.White.copy(alpha = 0.05f), CircleShape)) {
                    Icon(Icons.Default.Message, contentDescription = null, tint = SafeGreen, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
