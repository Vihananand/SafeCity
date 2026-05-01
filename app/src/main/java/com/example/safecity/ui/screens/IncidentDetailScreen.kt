package com.example.safecity.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safecity.ui.theme.SafeGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentDetailScreen(onBack: () -> Unit) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Incident Details") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Report, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Status: High Alert - Active Investigation",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Map, modifier = Modifier.size(48.dp), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Location Preview Unavailable", color = MaterialTheme.colorScheme.secondary)
                    }
                }

                Text(text = "Unidentified Intrusion Detected", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                Text(
                    text = "Reported at 5th Avenue, Block C, near the central library. Multiple residents reported suspicious individuals attempting to bypass secure perimeters at 10:30 PM.",
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Text(text = "Incident Timeline", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                
                TimelineItem("10:30 PM", "Incident Reported by 3 Citizens", true)
                TimelineItem("10:32 PM", "Dispatch Notified (Unit 7)", false)
                TimelineItem("10:35 PM", "Officers En Route", false)
                TimelineItem("10:40 PM", "Live Feed Accessed", false)

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.VolunteerActivism, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Provide Witness Statement")
                }
            }
        }
    }
}

@Composable
fun TimelineItem(time: String, activity: String, isFirst: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(if (isFirst) Color.Red else SafeGreen, RoundedCornerShape(50))
            )
            Box(modifier = Modifier.width(2.dp).height(30.dp).background(Color.LightGray))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = time, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
            Text(text = activity, fontSize = 14.sp)
        }
    }
}
