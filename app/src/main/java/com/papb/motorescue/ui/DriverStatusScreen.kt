package com.papb.motorescue.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.papb.motorescue.ui.theme.DarkSurface
import com.papb.motorescue.ui.theme.GoldPrimary

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
                    viewModel.syncFirebaseToLocal(data.id, data.status, data.mechanicName)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = GoldPrimary)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Menghubungkan satelit...", color = Color.Gray)
            } else if (currentOrder != null) {

                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("STATUS LAPORAN", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
                        Spacer(modifier = Modifier.height(32.dp))

                        if (currentOrder!!.status == "ACCEPTED") {
                            // DITERIMA
                            Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(100.dp), tint = Color(0xFF4CAF50))
                            Spacer(modifier = Modifier.height(24.dp))
                            Text("MONTIR DITEMUKAN!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50), textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Montir ${currentOrder!!.mechanicName ?: "Jagoan"} sedang menuju ke lokasimu.", textAlign = TextAlign.Center, color = Color.White)
                        } else {
                            // MENUNGGU
                            CircularProgressIndicator(modifier = Modifier.size(80.dp), color = GoldPrimary, strokeWidth = 6.dp)
                            Spacer(modifier = Modifier.height(32.dp))
                            Text("MENCARI MONTIR...", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = GoldPrimary, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Mohon tunggu, kami sedang menghubungi bengkel terdekat.", textAlign = TextAlign.Center, color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GoldPrimary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = GoldPrimary
                    )
                ) {
                    Text("KEMBALI KE DASHBOARD", fontWeight = FontWeight.Bold)
                }
            } else {
                Text("Data tidak ditemukan.", color = Color.White)
                Button(onClick = onNavigateBack) { Text("Kembali") }
            }
        }
    }
}