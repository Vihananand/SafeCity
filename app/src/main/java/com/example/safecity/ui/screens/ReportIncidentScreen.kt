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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.safecity.ui.components.CustomSpinner
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
    
    // Date and Time states
    val calendar = Calendar.getInstance()
    var dateString by remember { mutableStateOf("Select Date") }
    var timeString by remember { mutableStateOf("Select Time") }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            dateString = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            timeString = String.format("%02d:%02d", hour, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    // Camera URI logic
    val tempUri = remember { mutableStateOf<Uri?>(null) }

    // Launchers
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success -> if (success) selectedImageUri = tempUri.value }
    )

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> if (isGranted) showPhotoDialog = true }
    )

    fun fetchLocation() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener { location ->
                    location?.let {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        try {
                            val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                            detectedLocation = addresses?.firstOrNull()?.getAddressLine(0) ?: "${it.latitude}, ${it.longitude}"
                        } catch (e: Exception) {
                            detectedLocation = "${it.latitude}, ${it.longitude}"
                        }
                    } ?: run {
                        detectedLocation = "Location unavailable. Please use manual entry."
                    }
                }
        }
    }

    LaunchedEffect(Unit) {
        fetchLocation()
    }

    fun createImageUri(): Uri? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    if (showPhotoDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoDialog = false },
            title = { Text("Add Photo") },
            text = { Text("Choose a source for your evidence.") },
            confirmButton = {
                TextButton(onClick = {
                    showPhotoDialog = false
                    val uri = createImageUri()
                    tempUri.value = uri
                    uri?.let { cameraLauncher.launch(it) }
                }) { Text("Camera") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPhotoDialog = false
                    photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }) { Text("Gallery") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Incident") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text("Incident Details", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    CustomSpinner(
                        options = listOf("Theft", "Accident", "Harassment", "Pothole", "Street Light Issue", "Other"),
                        selectedOption = incidentType,
                        onOptionSelected = { incidentType = it },
                        label = "Incident Category"
                    )
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { datePickerDialog.show() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.DateRange, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(dateString)
                        }
                        OutlinedButton(
                            onClick = { timePickerDialog.show() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(timeString)
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Detailed Description") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }

                item {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Location", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(onClick = { fetchLocation() }) {
                                Icon(Icons.Default.MyLocation, contentDescription = "Refresh Location", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = detectedLocation,
                            onValueChange = { },
                            label = { Text("Detected Location") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            leadingIcon = { Icon(Icons.Default.EditLocation, contentDescription = null) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            )
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = { isManualLocationEnabled = !isManualLocationEnabled }) {
                                Text(if (isManualLocationEnabled) "Use Detected Location" else "Edit Manually")
                            }
                        }
                        if (isManualLocationEnabled) {
                            OutlinedTextField(
                                value = manualLocation,
                                onValueChange = { manualLocation = it },
                                label = { Text("Enter Location Manually") },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Enter correct address...") }
                            )
                        }
                    }
                }

                item {
                    Button(
                        onClick = { 
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                showPhotoDialog = true
                            } else {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (selectedImageUri != null) "Photo Added" else "Add Photo/Evidence")
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { 
                            scope.launch {
                                isSubmitting = true
                                delay(2000) // Simulate network call
                                isSubmitting = false
                                onBack()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        enabled = !isSubmitting
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                        } else {
                            Text("Submit Report", fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
