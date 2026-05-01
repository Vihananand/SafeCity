package com.example.safecity.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CommunityScreen() {
    val posts = listOf(
        CommunityPost("Safe Walk Group", "Starting a group walk from the library to the metro at 9 PM. Join us!", "15 mins ago", 24),
        CommunityPost("Suspicious Activity", "Spotted someone trying car doors on 4th Ave. Be careful!", "1h ago", 56),
        CommunityPost("New Street Lights", "Thanks to the city council for the new LEDs in Sector 7!", "3h ago", 120),
        CommunityPost("Lost Pet", "Found a brown lab near the park. Safe at the police station.", "5h ago", 12)
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Community Hub", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(text = "Connect with your neighbors for a safer city.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(posts) { post ->
                CommunityPostCard(post)
            }
        }
    }
}

data class CommunityPost(val title: String, val content: String, val time: String, val likes: Int)

@Composable
fun CommunityPostCard(post: CommunityPost) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = post.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(text = post.time, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = post.content, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ThumbUp, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                Text(text = " ${post.likes}", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(end = 16.dp))
                IconButton(onClick = { }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Favorite, contentDescription = "Like", modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = { }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
