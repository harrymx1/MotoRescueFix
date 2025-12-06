package com.papb.motorescue.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.papb.motorescue.data.model.RescueRequest

@Composable
fun DriverStatusScreen(
    orderId: String,
    onNavigateBack: () -> Unit,
    viewModel: DriverViewModel
) {
    var currentOrder by remember { mutableStateOf<RescueRequest?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(orderId) {
        val dbRef = FirebaseDatabase.getInstance("https://motorescue-52d7c-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("laporan").child(orderId)

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(RescueRequest::class.java)

                if (data != null) {
                    currentOrder = data
                    isLoading = false
                    // UPDATE ROOM AGAR LIST DI HOME BERUBAH
                    viewModel.syncFirebaseToLocal(data.id, data.status, data.mechanicName)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
                Text("Menghubungkan ke satelit...")
            } else if (currentOrder != null) {
                Text("STATUS LAPORAN", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(32.dp))

                if (currentOrder!!.status == "ACCEPTED") {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(100.dp), tint = Color(0xFF4CAF50))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("MONTIR DITEMUKAN!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Montir ${currentOrder!!.mechanicName ?: "Jagoan"} sedang menuju ke lokasimu.", textAlign = TextAlign.Center)
                } else {
                    CircularProgressIndicator(modifier = Modifier.size(80.dp), color = Color(0xFFFF9800), strokeWidth = 6.dp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("MENCARI MONTIR...", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color(0xFFFF9800))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Mohon tunggu, kami sedang menghubungi bengkel terdekat.", textAlign = TextAlign.Center)
                }

                Spacer(modifier = Modifier.height(48.dp))
                Button(onClick = onNavigateBack) {
                    Text("KEMBALI KE LIST")
                }
            } else {
                Text("Data tidak ditemukan di Cloud.")
                Button(onClick = onNavigateBack) { Text("Kembali") }
            }
        }
    }
}