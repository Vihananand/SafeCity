package com.example.safecity.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

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
            enter = fadeIn(tween(800)) + slideInHorizontally(initialOffsetX = { it })
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("App Settings", color = Color.White, fontWeight = FontWeight.Bold) },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                    )
                },
                containerColor = Color.Transparent
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "General Configuration",
                        color = SafeGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    SettingsCard {
                        ListItem(
                            headlineContent = { Text("Dark Theme", color = Color.White) },
                            supportingContent = { Text("Eye-comfort mode for night use", color = Color.White.copy(alpha = 0.6f)) },
                            leadingContent = { Icon(Icons.Default.DarkMode, contentDescription = null, tint = InfoBlue) },
                            trailingContent = {
                                Switch(
                                    checked = isDarkMode,
                                    onCheckedChange = onThemeToggle,
                                    colors = SwitchDefaults.colors(checkedThumbColor = SafeGreen)
                                )
                            },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                    }

                    SettingsCard {
                        ListItem(
                            headlineContent = { Text("Language", color = Color.White) },
                            supportingContent = { Text("English (Default)", color = Color.White.copy(alpha = 0.6f)) },
                            leadingContent = { Icon(Icons.Default.Language, contentDescription = null, tint = WarningOrange) },
                            trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray) },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                    }

                    Text(
                        text = "Privacy & Security",
                        color = SafeGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    SettingsCard {
                        Column {
                            SettingsToggleItem("Biometric Unlock", true, Icons.Default.Fingerprint, InfoBlue)
                            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                            SettingsToggleItem("Incognito Mode", false, Icons.Default.VisibilityOff, WarningOrange)
                            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                            SettingsToggleItem("Auto-Erasure", false, Icons.Default.AutoDelete, AlertRed)
                        }
                    }

                    Text(
                        text = "Emergency Triggers",
                        color = SafeGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    SettingsCard {
                        Column {
                            SettingsToggleItem("Power Button SOS", true, Icons.Default.PowerSettingsNew, AlertRed)
                            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                            SettingsToggleItem("Voice Activation", true, Icons.Default.RecordVoiceOver, SafeGreen)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Button(
                        onClick = { /* Reset */ },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Reset to Factory Defaults", color = AlertRed, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        content()
    }
}

@Composable
fun SettingsToggleItem(title: String, initialValue: Boolean, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    var checked by remember { mutableStateOf(initialValue) }
    ListItem(
        headlineContent = { Text(title, color = Color.White) },
        leadingContent = { Icon(icon, contentDescription = null, tint = color) },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = { checked = it },
                colors = SwitchDefaults.colors(checkedThumbColor = SafeGreen)
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}
