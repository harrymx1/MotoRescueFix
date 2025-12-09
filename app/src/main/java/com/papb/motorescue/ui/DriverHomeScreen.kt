package com.papb.motorescue.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.papb.motorescue.data.model.RescueRequest
import com.papb.motorescue.ui.theme.DarkSurface
import com.papb.motorescue.ui.theme.GoldPrimary

@Composable
fun DriverHomeScreen(
    navController: androidx.navigation.NavController,
    viewModel: DriverViewModel
) {
    val historyList by viewModel.historyList.collectAsState()


    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("driver_form") },
                containerColor = GoldPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Lapor")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, Color.DarkGray),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .clickable {
                        navController.navigate("consultation/driver")
                    }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("FORUM BENGKEL", color = GoldPrimary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Tanya jawab kerusakan motor disini", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                    Icon(Icons.Default.ArrowForward, null, tint = GoldPrimary)
                }
            }

            Text("Halo, Pengemudi!", style = MaterialTheme.typography.headlineMedium)
            Text("Riwayat Bantuan", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            if (historyList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada riwayat.", color = Color.DarkGray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(historyList) { item ->
                        Box(modifier = Modifier.clickable {
                            navController.navigate("driver_status/${item.id}")
                        }) {
                            RescueItemCard(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RescueItemCard(item: RescueRequest) {
    val statusColor = if (item.status == "ACCEPTED") Color(0xFF4CAF50) else Color(0xFFFF9800)

    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(12.dp),
                shape = RoundedCornerShape(50),
                color = statusColor
            ) {}

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(item.problemDesc, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Status: ${item.status}",
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor
                )
                Text(item.address, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 1)
            }
        }
    }
}