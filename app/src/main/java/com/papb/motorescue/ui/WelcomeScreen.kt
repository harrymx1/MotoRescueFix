package com.papb.motorescue.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.papb.motorescue.ui.theme.BlackMatte
import com.papb.motorescue.ui.theme.GoldPrimary

@Composable
fun WelcomeScreen(
    onLoginDriver: () -> Unit,
    onLoginMechanic: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackMatte)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.TwoWheeler,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = GoldPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "MOTO RESCUE",
            style = MaterialTheme.typography.displayMedium,
            color = GoldPrimary
        )

        Text(
            text = "Bantuan Darurat & Solusi Cepat",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.LightGray
        )

        Spacer(modifier = Modifier.height(64.dp))

        // Tombol Driver
        Button(
            onClick = onLoginDriver,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
        ) {
            Text("MASUK SEBAGAI PENGEMUDI", color = Color.White, style = MaterialTheme.typography.labelLarge)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tombol Montir
        OutlinedButton(
            onClick = onLoginMechanic,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(2.dp, GoldPrimary), // Border Emas
            colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary) // Teks Emas
        ) {
            Text("MASUK SEBAGAI MONTIR")
        }
    }
}