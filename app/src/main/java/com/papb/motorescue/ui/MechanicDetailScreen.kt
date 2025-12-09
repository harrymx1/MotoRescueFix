package com.papb.motorescue.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.papb.motorescue.ui.theme.DarkSurface
import com.papb.motorescue.ui.theme.GoldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MechanicDetailScreen(
    orderId: String,
    onNavigateBack: () -> Unit,
    viewModel: MechanicViewModel = viewModel()
) {
    val context = LocalContext.current
    val order = viewModel.getOrderById(orderId)

    var showFullImage by remember { mutableStateOf(false) }

    if (order == null) {
        Text("Data tidak ditemukan", color = Color.White)
        return
    }

    val imageBitmap = if (order.photoUrl.isNotEmpty()) ImageUtils.base64ToBitmap(order.photoUrl) else null

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Detail Masalah", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = GoldPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. FOTO BUKTI
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clickable {
                        if (imageBitmap != null) showFullImage = true
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                border = BorderStroke(1.dp, Color.DarkGray)
            ) {
                if (imageBitmap != null) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            bitmap = imageBitmap,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Surface(
                            modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp),
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                "Klik untuk memperbesar",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Tidak ada foto", color = Color.Gray)
                    }
                }
            }

            // 2. INFO DETAIL
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp).fillMaxWidth()) {
                    Text("Pengemudi:", style = MaterialTheme.typography.labelMedium, color = GoldPrimary)
                    Text(order.driverName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Kendala:", style = MaterialTheme.typography.labelMedium, color = GoldPrimary)
                    Text(order.problemDesc, style = MaterialTheme.typography.bodyLarge, color = Color.White)

                    Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color.DarkGray)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = Color.Red)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(order.address, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. AKSI
            OutlinedButton(
                onClick = {
                    if (order.latitude != 0.0 && order.longitude != 0.0) {
                        val gmmIntentUri = Uri.parse("google.navigation:q=${order.latitude},${order.longitude}&mode=d")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        try { context.startActivity(mapIntent) } catch (e: Exception) { Toast.makeText(context, "No Maps", Toast.LENGTH_SHORT).show() }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                border = BorderStroke(1.dp, GoldPrimary),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary)
            ) {
                Text("BUKA PETA NAVIGASI")
            }

            Button(
                onClick = {
                    viewModel.acceptOrder(order.id)
                    Toast.makeText(context, "Order Diterima!", Toast.LENGTH_SHORT).show()
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("TERIMA PANGGILAN", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showFullImage && imageBitmap != null) {
        Dialog(
            onDismissRequest = { showFullImage = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = "Full Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { showFullImage = false },
                    contentScale = ContentScale.Fit
                )

                IconButton(
                    onClick = { showFullImage = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(50))
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        }
    }
}