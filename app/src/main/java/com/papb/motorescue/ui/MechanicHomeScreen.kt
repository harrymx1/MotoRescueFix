package com.papb.motorescue.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.papb.motorescue.data.model.RescueRequest

@Composable
fun MechanicHomeScreen(
    navController: androidx.navigation.NavController,
    viewModel: MechanicViewModel = viewModel()
) {
    val orderList by viewModel.orderList.collectAsState()

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Halo, Montir!", style = MaterialTheme.typography.headlineMedium)
            Text("Orderan Masuk", style = MaterialTheme.typography.titleMedium, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            if (orderList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada panggilan darurat.")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(orderList) { order ->
                        MechanicOrderCard(
                            order = order,
                            onItemClick = {
                                // PINDAH KE DETAIL BAWA ID
                                navController.navigate("mechanic_detail/${order.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MechanicOrderCard(
    order: RescueRequest,
    onItemClick: () -> Unit
){
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Header: Nama & Masalah
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.Blue)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = order.driverName, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Badge(containerColor = Color.Red) { Text("WAITING", color = Color.White) }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Masalah
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Build, contentDescription = null, tint = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = order.problemDesc, style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Lokasi
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = order.address, style = MaterialTheme.typography.bodySmall, maxLines = 1)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onItemClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("LIHAT DETAIL & TERIMA")
            }
        }
    }
}