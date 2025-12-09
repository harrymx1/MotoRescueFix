package com.papb.motorescue.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forum
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
import com.papb.motorescue.ui.theme.DarkSurface
import com.papb.motorescue.ui.theme.GoldPrimary

@Composable
fun MechanicHomeScreen(
    navController: androidx.navigation.NavController,
    viewModel: MechanicViewModel = viewModel()
) {
    val orderList by viewModel.orderList.collectAsState()

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            Button(
                onClick = { navController.navigate("consultation/mechanic") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, GoldPrimary)
            ) {
                Icon(Icons.Default.Forum, contentDescription = null, tint = GoldPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("JAWAB KONSULTASI", color = GoldPrimary)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Halo, Montir!", style = MaterialTheme.typography.headlineMedium)
            Text("Orderan Masuk", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            if (orderList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Menunggu panggilan...", color = Color.DarkGray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(orderList) { order ->
                        MechanicOrderCard(
                            order = order,
                            onItemClick = { navController.navigate("mechanic_detail/${order.id}") }
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
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, GoldPrimary)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, null, tint = GoldPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = order.driverName, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.weight(1f))

                // Badge Status
                Surface(
                    color = Color.Red.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "WAITING",
                        color = Color.Red,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(text = order.problemDesc, style = MaterialTheme.typography.bodyLarge, color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = order.address, style = MaterialTheme.typography.bodySmall, maxLines = 1)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onItemClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("LIHAT & TERIMA", color = Color.White)
            }
        }
    }
}