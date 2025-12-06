package com.papb.motorescue.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WelcomeScreen(
    onLoginDriver: () -> Unit,
    onLoginMechanic: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Selamat Datang di", style = MaterialTheme.typography.headlineSmall)
        Text(text = "MOTO RESCUE", style = MaterialTheme.typography.displayMedium)

        Spacer(modifier = Modifier.height(48.dp))

        // Tombol Driver
        Button(
            onClick = onLoginDriver,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("MASUK SEBAGAI PENGEMUDI")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tombol Montir
        OutlinedButton(
            onClick = onLoginMechanic,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("MASUK SEBAGAI MONTIR")
        }
    }
}