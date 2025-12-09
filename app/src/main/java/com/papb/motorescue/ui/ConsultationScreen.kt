package com.papb.motorescue.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.papb.motorescue.data.remote.Consultation
import com.papb.motorescue.ui.theme.DarkSurface
import com.papb.motorescue.ui.theme.GoldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultationScreen(
    isMechanic: Boolean,
    onNavigateBack: () -> Unit,
    viewModel: ConsultationViewModel = viewModel()
) {
    var inputMotor by remember { mutableStateOf("") }
    var inputKeluhan by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<Consultation?>(null) }
    var editMotor by remember { mutableStateOf("") }
    var editProblem by remember { mutableStateOf("") }
    var editReply by remember { mutableStateOf("") }

    val commonTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = GoldPrimary,
        unfocusedBorderColor = Color.Gray,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedLabelColor = GoldPrimary,
        unfocusedLabelColor = Color.Gray,
        cursorColor = GoldPrimary,
        focusedContainerColor = DarkSurface,
        unfocusedContainerColor = DarkSurface
    )
    val commonShape = RoundedCornerShape(12.dp)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Forum Konsultasi", color = Color.White) },
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
                .fillMaxSize()
        ) {
            Box(modifier = Modifier.weight(1f).padding(horizontal = 20.dp)) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = GoldPrimary, modifier = Modifier.align(Alignment.Center))
                } else if (viewModel.consultationList.isEmpty()) {
                    Text("Belum ada diskusi.", color = Color.Gray, modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(viewModel.consultationList) { item ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                shape = RoundedCornerShape(12.dp),
                                border = if (item.mechanicReply.isNotEmpty()) BorderStroke(1.dp, Color(0xFF4CAF50)) else null,
                                modifier = Modifier.clickable {
                                    selectedItem = item
                                    editMotor = item.motorType
                                    editProblem = item.problem
                                    editReply = item.mechanicReply
                                    showDialog = true
                                }
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row {
                                        Icon(Icons.Default.Help, null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(item.motorType, fontWeight = FontWeight.Bold, color = Color.White)
                                            Text(item.problem, style = MaterialTheme.typography.bodyMedium, color = Color.LightGray)
                                        }
                                    }
                                    if (item.mechanicReply.isNotEmpty()) {
                                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.DarkGray)
                                        Row {
                                            Icon(Icons.Default.Build, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text("Jawaban Montir:", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50), style = MaterialTheme.typography.bodySmall)
                                                Text(item.mechanicReply, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                                            }
                                        }
                                    } else if (isMechanic) {
                                        Text("Klik untuk menjawab...", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // INPUT FORM (Driver Only)
            if (!isMechanic) {
                Surface(
                    color = DarkSurface,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    shadowElevation = 8.dp
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("Tanya Baru:", fontWeight = FontWeight.Bold, color = GoldPrimary)
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = inputMotor,
                            onValueChange = { inputMotor = it },
                            label = { Text("Motor") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = commonTextFieldColors,
                            shape = commonShape
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = inputKeluhan,
                                onValueChange = { inputKeluhan = it },
                                label = { Text("Keluhan") },
                                modifier = Modifier.weight(1f),
                                colors = commonTextFieldColors,
                                shape = commonShape
                            )
                            Spacer(modifier = Modifier.width(12.dp))

                            IconButton(
                                onClick = {
                                    if (inputKeluhan.isNotEmpty()) {
                                        viewModel.sendConsultation(inputMotor, inputKeluhan)
                                        inputMotor = ""; inputKeluhan = ""
                                    }
                                },
                                modifier = Modifier.size(56.dp),
                                colors = IconButtonDefaults.iconButtonColors(containerColor = GoldPrimary)
                            ) {
                                Icon(Icons.Default.Send, null, tint = Color.White)
                            }
                        }

                        if (viewModel.uploadStatus.isNotEmpty()) {
                            Text(viewModel.uploadStatus, style = MaterialTheme.typography.labelSmall, color = Color.Blue, modifier = Modifier.padding(top=8.dp))
                        }
                    }
                }
            }
        }

        // DIALOG (CRUD)
        if (showDialog && selectedItem != null) {
            AlertDialog(
                containerColor = DarkSurface,
                titleContentColor = GoldPrimary,
                textContentColor = Color.White,
                onDismissRequest = { showDialog = false },
                title = { Text(if (isMechanic) "Jawab" else "Edit") },
                text = {
                    Column {
                        if (isMechanic) {
                            Text("Masalah: ${selectedItem!!.problem}", color = Color.Gray)
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = editReply,
                                onValueChange = { editReply = it },
                                label = { Text("Jawaban") },
                                colors = commonTextFieldColors,
                                shape = commonShape,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            OutlinedTextField(
                                value = editMotor,
                                onValueChange = { editMotor = it },
                                label = { Text("Motor") },
                                colors = commonTextFieldColors,
                                shape = commonShape,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = editProblem,
                                onValueChange = { editProblem = it },
                                label = { Text("Keluhan") },
                                colors = commonTextFieldColors,
                                shape = commonShape,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val updated = if(isMechanic) selectedItem!!.copy(mechanicReply = editReply) else selectedItem!!.copy(motorType = editMotor, problem = editProblem)
                            viewModel.updateItem(updated)
                            showDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Simpan", color = Color.White) }
                },
                dismissButton = {
                    TextButton(onClick = {
                        if (isMechanic) {
                            val clearReply = selectedItem!!.copy(mechanicReply = "")
                            viewModel.updateItem(clearReply)
                        } else {
                            viewModel.deleteItem(selectedItem!!.id)
                        }
                        showDialog = false
                    }) { Text("Hapus", color = Color.Red) }
                }
            )
        }
    }
}