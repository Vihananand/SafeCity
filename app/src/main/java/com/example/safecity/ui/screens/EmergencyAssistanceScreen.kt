package com.example.safecity.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.safecity.R
import com.example.safecity.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

data class EmergencyContact(val name: String, val number: String, val icon: ImageVector, val color: Color)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyAssistanceScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
    
    var isRecording by remember { mutableStateOf(false) }
    var recording: Recording? by remember { mutableStateOf(null) }
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) { isVisible = true }

    val emergencyContacts = listOf(
        EmergencyContact("National Emergency", "112", Icons.Default.Emergency, AlertRed),
        EmergencyContact("Police", "100", Icons.Default.LocalPolice, InfoBlue),
        EmergencyContact("Ambulance", "102", Icons.Default.LocalHospital, SafeGreen),
        EmergencyContact("Fire Brigade", "101", Icons.Default.FireTruck, WarningOrange),
        EmergencyContact("Women Helpline", "1091", Icons.Default.Woman, Color(0xFFE91E63)),
        EmergencyContact("Child Helpline", "1098", Icons.Default.ChildCare, Color(0xFF9C27B0))
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { _ -> }
    )

    fun startSosTrigger() {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0)
        
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, R.raw.siren).apply {
            isLooping = true
            start()
        }

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val videoCapture = VideoCapture.withOutput(Recorder.Builder().setQualitySelector(QualitySelector.from(Quality.LOWEST)).build())
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, videoCapture)
                val name = "SafeCity_SOS_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date()) + ".mp4"
                val contentValues = android.content.ContentValues().apply {
                    put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, name)
                    put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                }
                val mediaStoreOutputOptions = MediaStoreOutputOptions.Builder(context.contentResolver, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI).setContentValues(contentValues).build()
                recording = videoCapture.output.prepareRecording(context, mediaStoreOutputOptions).withAudioEnabled().start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                    when(recordEvent) {
                        is VideoRecordEvent.Start -> { isRecording = true }
                        is VideoRecordEvent.Finalize -> { isRecording = false }
                    }
                }
            } catch (e: Exception) { Log.e("CameraX", "Binding failed", e) }
        }, ContextCompat.getMainExecutor(context))
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            recording?.stop()
            cameraExecutor.shutdown()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = listOf(DeepNavy, MaterialTheme.colorScheme.surface)))
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(800)) + slideInVertically()
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("SOS & Assistance", color = Color.White, fontWeight = FontWeight.Black) },
                        navigationIcon = {
                            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White) }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                    )
                },
                containerColor = Color.Transparent
            ) { padding ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 32.dp, top = 8.dp)
                ) {
                    item {
                        SOSPanicButton(isRecording) {
                            val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                            if (permissions.all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }) {
                                if (!isRecording) startSosTrigger() else { recording?.stop(); mediaPlayer?.stop() }
                            } else { permissionLauncher.launch(permissions) }
                        }
                    }

                    item {
                        Text("Instant Emergency Call", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))
                    }

                    items(emergencyContacts) { contact ->
                        EmergencyContactCard(contact)
                    }
                }
            }
        }
    }
}

@Composable
fun SOSPanicButton(isActive: Boolean, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Reverse),
        label = "scale"
    )

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (isActive) {
                Box(modifier = Modifier.size(180.dp).scale(scale).background(AlertRed.copy(alpha = 0.3f), CircleShape))
                Box(modifier = Modifier.size(220.dp).scale(scale * 0.8f).background(AlertRed.copy(alpha = 0.15f), CircleShape))
            }
            
            Button(
                onClick = onClick,
                modifier = Modifier.size(150.dp).shadow(12.dp, CircleShape),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = if (isActive) Color.White else AlertRed),
                contentPadding = PaddingValues(0.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        if (isActive) Icons.Default.Stop else Icons.Default.Campaign,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = if (isActive) AlertRed else Color.White
                    )
                    Text(
                        if (isActive) "STOP SOS" else "PANIC SOS",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = if (isActive) AlertRed else Color.White
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = if (isActive) "EMERGENCY PROTOCOL ACTIVE" else "TAP FOR IMMEDIATE HELP",
            color = if (isActive) AlertRed else Color.White.copy(alpha = 0.7f),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        
        if (isActive) {
            Text(
                "Siren blasting & Video recording in progress...",
                color = AlertRed,
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun EmergencyContactCard(contact: EmergencyContact) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(50.dp),
                shape = CircleShape,
                color = contact.color.copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(contact.icon, contentDescription = null, tint = contact.color, modifier = Modifier.size(28.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(contact.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                Text(contact.number, color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
            }
            IconButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL).apply { data = Uri.parse("tel:${contact.number}") }
                    context.startActivity(intent)
                },
                modifier = Modifier.background(contact.color.copy(alpha = 0.15f), CircleShape)
            ) {
                Icon(Icons.Default.Call, contentDescription = "Call", tint = contact.color)
            }
        }
    }
}
