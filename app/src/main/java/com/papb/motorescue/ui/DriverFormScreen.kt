package com.papb.motorescue.ui

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.papb.motorescue.ui.components.CameraPreview
import com.papb.motorescue.ui.components.takePhoto
import com.papb.motorescue.ui.theme.DarkSurface
import com.papb.motorescue.ui.theme.GoldPrimary
import java.io.File
import java.util.concurrent.Executors

@Composable
fun DriverFormScreen(
    onNavigateBack: () -> Unit,
    viewModel: DriverViewModel
) {
    val context = LocalContext.current

    // State
    var photoFile by remember { mutableStateOf<File?>(null) }
    var locationText by remember { mutableStateOf("Lokasi belum diambil") }
    var problemDesc by remember { mutableStateOf("") }
    var currentLat by remember { mutableStateOf(0.0) }
    var currentLong by remember { mutableStateOf(0.0) }

    // Camera Setup
    val imageCapture = remember { ImageCapture.Builder().build() }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    var showCameraPreview by remember { mutableStateOf(false) }

    // Location Setup
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            try {
                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            currentLat = location.latitude
                            currentLong = location.longitude
                            locationText = "Lat: ${location.latitude}, Long: ${location.longitude}"
                        } else {
                            locationText = "Gagal deteksi. Coba buka Google Maps."
                        }
                    }
            } catch (e: SecurityException) { }
        }
    }

    if (showCameraPreview) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            CameraPreview(imageCapture = imageCapture)
            Button(
                onClick = {
                    takePhoto(
                        imageCapture = imageCapture,
                        outputDirectory = context.cacheDir,
                        executor = cameraExecutor,
                        onImageCaptured = { file -> photoFile = file; showCameraPreview = false },
                        onError = { Toast.makeText(context, "Gagal Foto", Toast.LENGTH_SHORT).show() }
                    )
                },
                modifier = Modifier.align(Alignment.BottomCenter).padding(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
            ) {
                Text("JEPRET FOTO", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    } else {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text("Lapor Masalah", style = MaterialTheme.typography.headlineMedium, color = GoldPrimary)

                // 1. INPUT FOTO (Card Style)
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.DarkGray)
                ) {
                    Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        if (photoFile != null) {
                            Image(
                                painter = rememberAsyncImagePainter(photoFile),
                                contentDescription = null,
                                modifier = Modifier.height(200.dp).fillMaxWidth().padding(bottom = 16.dp)
                            )
                        }
                        Button(
                            onClick = { showCameraPreview = true },
                            colors = ButtonDefaults.buttonColors(containerColor = if (photoFile == null) GoldPrimary else Color.DarkGray),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(if (photoFile == null) "AMBIL FOTO BUKTI" else "FOTO ULANG", color = Color.White)
                        }
                    }
                }

                // 2. INPUT LOKASI
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.DarkGray)
                ) {
                    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                        Text("Lokasi Anda:", style = MaterialTheme.typography.labelMedium, color = GoldPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(locationText, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = {
                                permissionLauncher.launch(
                                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
                            border = BorderStroke(1.dp, GoldPrimary)
                        ) {
                            Text("CEK LOKASI SAYA")
                        }
                    }
                }

                // 3. INPUT MASALAH
                OutlinedTextField(
                    value = problemDesc,
                    onValueChange = { problemDesc = it },
                    label = { Text("Deskripsi Masalah") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = GoldPrimary,
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = GoldPrimary,
                        focusedContainerColor = DarkSurface,
                        unfocusedContainerColor = DarkSurface
                    )
                )

                Spacer(modifier = Modifier.weight(1f))

                // 4. TOMBOL KIRIM
                Button(
                    onClick = {
                        viewModel.submitReport(photoFile, problemDesc, currentLat, currentLong, locationText)
                        Toast.makeText(context, "Laporan Terkirim!", Toast.LENGTH_SHORT).show()
                        onNavigateBack()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = photoFile != null && problemDesc.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        disabledContainerColor = Color.DarkGray
                    )
                ) {
                    Text("KIRIM SINYAL DARURAT", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}