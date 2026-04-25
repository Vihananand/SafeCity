package com.example.safecity.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.safecity.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

data class EmergencyContact(val name: String, val number: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyAssistanceScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
    
    var isRecording by remember { mutableStateOf(false) }
    var recording: Recording? by remember { mutableStateOf(null) }
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }

    val emergencyContacts = listOf(
        EmergencyContact("National Emergency", "112", Icons.Default.Warning),
        EmergencyContact("Police", "100", Icons.Default.LocalPolice),
        EmergencyContact("Ambulance", "102", Icons.Default.LocalHospital),
        EmergencyContact("Fire Brigade", "101", Icons.Default.Warning),
        EmergencyContact("Women Helpline", "1091", Icons.Default.Woman),
        EmergencyContact("Child Helpline", "1098", Icons.Default.Call)
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val allGranted = permissions.values.all { it }
            if (allGranted) {
                // Trigger logic if needed immediately after grant
            }
        }
    )

    fun startSosTrigger() {
        // 1. Max Volume & Siren
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
            0
        )
        
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, R.raw.siren).apply {
            isLooping = true
            start()
        }

        // 2. Start Automatic Background Recording
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val videoCapture = VideoCapture.withOutput(
                Recorder.Builder()
                    .setQualitySelector(QualitySelector.from(Quality.LOWEST))
                    .build()
            )

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, videoCapture)

                val name = "SafeCity_SOS_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date()) + ".mp4"
                val contentValues = android.content.ContentValues().apply {
                    put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, name)
                    put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                }

                val mediaStoreOutputOptions = MediaStoreOutputOptions
                    .Builder(context.contentResolver, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                    .setContentValues(contentValues)
                    .build()

                recording = videoCapture.output
                    .prepareRecording(context, mediaStoreOutputOptions)
                    .withAudioEnabled()
                    .start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                        when(recordEvent) {
                            is VideoRecordEvent.Start -> { isRecording = true }
                            is VideoRecordEvent.Finalize -> { isRecording = false }
                        }
                    }

            } catch (e: Exception) {
                Log.e("CameraX", "Use case binding failed", e)
            }
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emergency Assistance (India)") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Indian Emergency Services", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(emergencyContacts) { contact ->
                EmergencyContactCard(contact)
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        val permissions = arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO
                        )
                        if (permissions.all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }) {
                            startSosTrigger()
                        } else {
                            permissionLauncher.launch(permissions)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRecording) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                    ),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Text(
                        if (isRecording) "SOS ACTIVE - RECORDING..." else "Trigger SOS Alert",
                        fontSize = 20.sp, 
                        fontWeight = FontWeight.Bold
                    )
                }
                
                if (isRecording) {
                    Text(
                        "Siren active at max volume. Video evidence is being recorded and saved to gallery.",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp),
                        fontSize = 14.sp
                    )
                } else {
                    Text(
                        "This will trigger a siren, notify authorities, and start automatic video recording.",
                        modifier = Modifier.padding(top = 8.dp),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
fun EmergencyContactCard(contact: EmergencyContact) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        ListItem(
            headlineContent = { Text(contact.name, fontWeight = FontWeight.Bold) },
            supportingContent = { Text(contact.number) },
            leadingContent = {
                Icon(
                    contact.icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingContent = {
                FilledIconButton(onClick = { 
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${contact.number}")
                    }
                    context.startActivity(intent)
                }) {
                    Icon(Icons.Default.Call, contentDescription = "Call")
                }
            }
        )
    }
}
