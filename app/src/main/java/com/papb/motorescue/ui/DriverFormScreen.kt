package com.papb.motorescue.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.location.LocationServices
import com.papb.motorescue.ui.components.CameraPreview
import com.papb.motorescue.ui.components.takePhoto
import java.io.File
import java.util.concurrent.Executors

@Composable
fun DriverFormScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    // --- STATE ---
    var photoFile by remember { mutableStateOf<File?>(null) }
    var locationText by remember { mutableStateOf("Lokasi belum diambil") }
    var problemDesc by remember { mutableStateOf("") }

    // Setup Kamera
    val imageCapture = remember { ImageCapture.Builder().build() }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    var showCameraPreview by remember { mutableStateOf(false) }

    // Setup Lokasi (FusedLocation)
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Izin Launcher (Meminta izin Lokasi & Kamera ke User)
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            // Izin Lokasi diberikan, ambil lokasi
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        locationText = "Lat: ${location.latitude}, Long: ${location.longitude}"
                    } else {
                        locationText = "GPS aktif tapi lokasi null. Coba buka Google Maps dulu."
                    }
                }
            } catch (e: SecurityException) { /* Handle error */ }
        }
    }

    // --- UI UTAMA ---
    if (showCameraPreview) {
        // TAMPILAN FULL SCREEN KAMERA
        Box(modifier = Modifier.fillMaxSize()) {
            CameraPreview(imageCapture = imageCapture)
            Button(
                onClick = {
                    takePhoto(
                        imageCapture = imageCapture,
                        outputDirectory = context.cacheDir,
                        executor = cameraExecutor,
                        onImageCaptured = { file ->
                            photoFile = file
                            showCameraPreview = false
                        },
                        onError = { Toast.makeText(context, "Gagal Foto", Toast.LENGTH_SHORT).show() }
                    )
                },
                modifier = Modifier.align(Alignment.BottomCenter).padding(32.dp)
            ) {
                Text("JEPRET FOTO")
            }
        }
    } else {
        // TAMPILAN FORMULIR
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Lapor Masalah", style = MaterialTheme.typography.headlineMedium)

            // 1. INPUT FOTO
            Button(onClick = { showCameraPreview = true }, modifier = Modifier.fillMaxWidth()) {
                Text(if (photoFile == null) "AMBIL FOTO BAN" else "FOTO ULANG")
            }

            // Preview Foto Kecil
            if (photoFile != null) {
                Image(
                    painter = rememberAsyncImagePainter(photoFile),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp).align(Alignment.CenterHorizontally)
                )
            }

            // 2. INPUT LOKASI
            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Lokasi Anda:", style = MaterialTheme.typography.labelLarge)
                    Text(locationText, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.CAMERA
                            )
                        )
                    }) {
                        Text("CEK LOKASI SAYA")
                    }
                }
            }

            // 3. INPUT MASALAH
            OutlinedTextField(
                value = problemDesc,
                onValueChange = { problemDesc = it },
                label = { Text("Deskripsi Masalah (cth: Ban Bocor)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            // 4. TOMBOL KIRIM
            Button(
                onClick = {
                    Toast.makeText(context, "Data Siap Dikirim!", Toast.LENGTH_SHORT).show()
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = photoFile != null && problemDesc.isNotEmpty()
            ) {
                Text("KIRIM SINYAL DARURAT")
            }
        }
    }
}