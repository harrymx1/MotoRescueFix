package com.papb.motorescue.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
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

@Composable
fun DriverHomeScreen(
    navController: androidx.navigation.NavController,
    viewModel: DriverViewModel = viewModel()
) {
    val historyList by viewModel.historyList.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("driver_form")
            }) {
                Icon(Icons.Default.Add, contentDescription = "Lapor")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Header
            Text("Halo, Pengemudi!", style = MaterialTheme.typography.headlineMedium)
            Text("Riwayat Bantuan", style = MaterialTheme.typography.titleMedium, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            // LIST DATA (Collection Implementation)
            if (historyList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada riwayat laporan.")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(historyList) { item ->
                        RescueItemCard(item)
                    }
                }
            }
        }
    }
}

@Composable
fun RescueItemCard(item: com.papb.motorescue.data.model.RescueRequest) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF9800))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = item.problemDesc, fontWeight = FontWeight.Bold)
                Text(text = "Status: ${item.status}", style = MaterialTheme.typography.bodySmall)
                Text(text = item.address, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}