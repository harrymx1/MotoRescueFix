package com.papb.motorescue.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.papb.motorescue.data.remote.Consultation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultationScreen(
    isMechanic: Boolean,
    onNavigateBack: () -> Unit,
    viewModel: ConsultationViewModel = viewModel()
) {
    // Kita hapus LaunchedEffect(Unit) { viewModel.fetch() }
    // Karena sekarang ViewModel sudah otomatis refresh sendiri.

    // State Input
    var inputMotor by remember { mutableStateOf("") }
    var inputKeluhan by remember { mutableStateOf("") }

    // State Dialog
    var showDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<Consultation?>(null) }

    var editMotor by remember { mutableStateOf("") }
    var editProblem by remember { mutableStateOf("") }
    var editReply by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                // JUDUL NETRAL: Tidak peduli siapa yang buka, namanya Forum Konsultasi Umum
                title = { Text("Forum Konsultasi Umum") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF3E5F5))
        ) {
            // --- LIST (Otomatis Update tiap 3 detik) ---
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.consultationList) { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.clickable {
                            selectedItem = item
                            editMotor = item.motorType
                            editProblem = item.problem
                            editReply = item.mechanicReply
                            showDialog = true
                        }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Tanya
                            Row {
                                Icon(Icons.Default.Help, null, tint = Color.Magenta)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(item.motorType, fontWeight = FontWeight.Bold)
                                    Text(item.problem)
                                }
                            }

                            // Jawab
                            if (item.mechanicReply.isNotEmpty()) {
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                                Row {
                                    Icon(Icons.Default.Build, null, tint = Color(0xFF4CAF50))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("Jawaban Montir:", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                                        Text(item.mechanicReply)
                                    }
                                }
                            } else if (isMechanic) {
                                Text("Klik untuk membalas...", style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.padding(top=8.dp))
                            }
                        }
                    }
                }
            }

            // --- INPUT BARU (Hanya Pengemudi) ---
            if (!isMechanic) {
                Card(
                    elevation = CardDefaults.cardElevation(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Tanya Baru:", fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = inputMotor,
                            onValueChange = { inputMotor = it },
                            label = { Text("Motor") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = inputKeluhan,
                                onValueChange = { inputKeluhan = it },
                                label = { Text("Keluhan") },
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = {
                                if (inputKeluhan.isNotEmpty()) {
                                    viewModel.sendConsultation(inputMotor, inputKeluhan)
                                    inputMotor = ""; inputKeluhan = ""
                                }
                            }) {
                                Icon(Icons.Default.Send, null, tint = Color(0xFF4A148C))
                            }
                        }
                        if (viewModel.uploadStatus.isNotEmpty()) {
                            Text(viewModel.uploadStatus, style = MaterialTheme.typography.labelSmall, color = Color.Blue)
                        }
                    }
                }
            }
        }

        // --- DIALOG CRUD ---
        if (showDialog && selectedItem != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(if (isMechanic) "Balas Konsultasi" else "Edit Pertanyaan") },
                text = {
                    Column {
                        if (isMechanic) {
                            Text("Motor: ${selectedItem!!.motorType}", fontWeight = FontWeight.Bold)
                            Text("Masalah: ${selectedItem!!.problem}")
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = editReply,
                                onValueChange = { editReply = it },
                                label = { Text("Jawaban Anda") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            OutlinedTextField(value = editMotor, onValueChange = { editMotor = it }, label = { Text("Jenis Motor") })
                            OutlinedTextField(value = editProblem, onValueChange = { editProblem = it }, label = { Text("Keluhan") })
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val updatedItem = if (isMechanic) {
                            selectedItem!!.copy(mechanicReply = editReply)
                        } else {
                            selectedItem!!.copy(motorType = editMotor, problem = editProblem)
                        }
                        viewModel.updateItem(updatedItem)
                        showDialog = false
                    }) {
                        Text("Simpan")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        viewModel.deleteItem(selectedItem!!.id)
                        showDialog = false
                    }, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) {
                        Text("Hapus")
                    }
                }
            )
        }
    }
}