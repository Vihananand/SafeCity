package com.example.safecity.ui.screens

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.safecity.ui.components.CustomSpinner
import com.example.safecity.ui.theme.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportIncidentScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    var incidentType by remember { mutableStateOf("Theft") }
    var description by remember { mutableStateOf("") }
    var detectedLocation by remember { mutableStateOf("Detecting location...") }
    var manualLocation by remember { mutableStateOf("") }
    var isManualLocationEnabled by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showPhotoDialog by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { isVisible = true }

    // Date and Time states
    val calendar = Calendar.getInstance()
    var dateString by remember { mutableStateOf("Select Date") }
    var timeString by remember { mutableStateOf("Select Time") }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth -> dateString = "$dayOfMonth/${month + 1}/$year" },
        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute -> timeString = String.format("%02d:%02d", hour, minute) },
        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
    )

    val tempUri = remember { mutableStateOf<Uri?>(null) }
    val photoPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri -> selectedImageUri = uri }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success -> if (success) selectedImageUri = tempUri.value }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted -> if (isGranted) showPhotoDialog = true }

    fun fetchLocation() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener { location ->
                    location?.let {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        try {
                            val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                            detectedLocation = addresses?.firstOrNull()?.getAddressLine(0) ?: "${it.latitude}, ${it.longitude}"
                        } catch (e: Exception) { detectedLocation = "${it.latitude}, ${it.longitude}" }
                    } ?: run { detectedLocation = "Location unavailable." }
                }
        }
    }

    LaunchedEffect(Unit) { fetchLocation() }

    fun createImageUri(): Uri? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    if (showPhotoDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoDialog = false },
            title = { Text("Add Evidence") },
            text = { Text("Capture a photo or select from gallery to support your report.") },
            confirmButton = {
                Button(onClick = {
                    showPhotoDialog = false
                    val uri = createImageUri()
                    tempUri.value = uri
                    uri?.let { cameraLauncher.launch(it) }
                }) { Text("Camera") }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showPhotoDialog = false
                    photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }) { Text("Gallery") }
            }
        )
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
                        title = { Text("Report Incident", color = Color.White, fontWeight = FontWeight.Bold) },
                        navigationIcon = {
                            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White) }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                    )
                },
                containerColor = Color.Transparent
            ) { padding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(bottom = 32.dp, top = 8.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text("Incident Category", color = SafeGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                CustomSpinner(
                                    options = listOf("Theft", "Accident", "Harassment", "Pothole", "Street Light Issue", "Other"),
                                    selectedOption = incidentType,
                                    onOptionSelected = { incidentType = it },
                                    label = ""
                                )
                            }
                        }
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Card(
                                modifier = Modifier.weight(1f).height(100.dp),
                                onClick = { datePickerDialog.show() },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = InfoBlue.copy(alpha = 0.1f))
                            ) {
                                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                    Icon(Icons.Default.DateRange, contentDescription = null, tint = InfoBlue)
                                    Text(dateString, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                            Card(
                                modifier = Modifier.weight(1f).height(100.dp),
                                onClick = { timePickerDialog.show() },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = WarningOrange.copy(alpha = 0.1f))
                            ) {
                                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                    Icon(Icons.Default.Schedule, contentDescription = null, tint = WarningOrange)
                                    Text(timeString, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("What happened?", color = Color.White.copy(alpha = 0.6f)) },
                            modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp)),
                            minLines = 4,
                            shape = RoundedCornerShape(20.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White.copy(alpha = 0.05f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.05f),

                                focusedIndicatorColor = SafeGreen,
                                unfocusedIndicatorColor = Color.White.copy(alpha = 0.1f),

                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,

                                cursorColor = SafeGreen
                            )
                        )
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = SafeGreen, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Incident Location", color = Color.White, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.weight(1f))
                                    IconButton(onClick = { fetchLocation() }) {
                                        Icon(Icons.Default.Refresh, contentDescription = null, tint = SafeGreen)
                                    }
                                }
                                Text(detectedLocation, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                                TextButton(onClick = { isManualLocationEnabled = !isManualLocationEnabled }) {
                                    Text(if (isManualLocationEnabled) "Use Automatic" else "Edit Manually", color = InfoBlue)
                                }
                                if (isManualLocationEnabled) {
                                    TextField(
                                        value = manualLocation,
                                        onValueChange = { manualLocation = it },
                                        placeholder = { Text("Enter correct address...") },
                                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color.White.copy(alpha = 0.1f),
                                            unfocusedContainerColor = Color.White.copy(alpha = 0.1f)
                                        )
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = { 
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) showPhotoDialog = true
                                else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            },
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f))
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = SafeGreen)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(if (selectedImageUri != null) "Evidence Attached" else "Attach Photo Evidence", color = Color.White)
                        }
                    }

                    item {
                        Button(
                            onClick = { 
                                scope.launch {
                                    isSubmitting = true
                                    delay(2000)
                                    isSubmitting = false
                                    onBack()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(60.dp).shadow(8.dp, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                            enabled = !isSubmitting
                        ) {
                            if (isSubmitting) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                            else Text("Submit Urgent Report", fontSize = 18.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }
}
