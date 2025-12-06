package com.papb.motorescue.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MechanicDetailScreen(
    orderId: String,
    onNavigateBack: () -> Unit,
    viewModel: MechanicViewModel = viewModel()
) {
    val context = LocalContext.current
    val order = viewModel.getOrderById(orderId)

    if (order == null) {
        Text("Data tidak ditemukan")
        return
    }

    val imageBitmap = if (order.photoUrl.isNotEmpty()) {
        ImageUtils.base64ToBitmap(order.photoUrl)
    } else null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Masalah") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. FOTO BUKTI
            Card(
                modifier = Modifier.fillMaxWidth().height(250.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = "Foto Masalah",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Tidak ada foto")
                    }
                }
            }

            // 2. INFO PENGEMUDI
            Text("Pengemudi: ${order.driverName}", style = MaterialTheme.typography.titleLarge)
            Text("Masalah: ${order.problemDesc}", style = MaterialTheme.typography.bodyLarge)

            Divider()

            // 3. LOKASI
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Red)
                Spacer(modifier = Modifier.width(8.dp))
                Text(order.address, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. TOMBOL NAVIGASI MAPS
            OutlinedButton(
                onClick = {
                    if (order.latitude != 0.0 && order.longitude != 0.0) {
                        val gmmIntentUri = Uri.parse("google.navigation:q=${order.latitude},${order.longitude}&mode=d")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")

                        try {
                            context.startActivity(mapIntent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Install Google Maps dulu", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Lokasi tidak valid (0.0, 0.0)", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("BUKA PETA (GOOGLE MAPS)")
            }

            // 5. TOMBOL TERIMA ORDER
            Button(
                onClick = {
                    viewModel.acceptOrder(order.id)
                    Toast.makeText(context, "Order Diterima!", Toast.LENGTH_SHORT).show()
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("TERIMA PANGGILAN")
            }
        }
    }
}