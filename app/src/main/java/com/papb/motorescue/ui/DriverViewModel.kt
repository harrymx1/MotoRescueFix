package com.papb.motorescue.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import com.papb.motorescue.data.local.AppDatabase
import com.papb.motorescue.data.model.RescueRequest
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

class DriverViewModel(application: Application) : AndroidViewModel(application) {

    // Sumber Data (DAO Room)
    private val dao = AppDatabase.getDatabase(application).rescueDao()

    // StateFlow
    val historyList: StateFlow<List<RescueRequest>> = dao.getAllHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val firebaseDb = FirebaseDatabase.getInstance("https://motorescue-52d7c-default-rtdb.asia-southeast1.firebasedatabase.app")
        .getReference("laporan")

    fun submitReport(
        photoFile: File?,
        description: String,
        lat: Double,
        long: Double,
        addressInfo: String
    ) {
        viewModelScope.launch {
            // 1. PROSES FOTO
            val base64Photo = if (photoFile != null) {
                ImageUtils.fileToBase64(photoFile)
            } else {
                ""
            }

            // 2. BUNGKUS DATA
            val reportId = UUID.randomUUID().toString()
            val newReport = RescueRequest(
                id = reportId,
                driverName = "Pengemudi (Anda)",
                problemDesc = description,
                photoUrl = base64Photo,
                latitude = lat,
                longitude = long,
                address = addressInfo,
                status = "WAITING"
            )

            // 3. SIMPAN KE ROOM (OFFLINE)
            dao.insertRescue(newReport)

            // 4. KIRIM KE FIREBASE (CLOUD)
            firebaseDb.child(reportId).setValue(newReport)
        }
    }

    // FUNGSI SINKRONISASI
    fun syncFirebaseToLocal(id: String, status: String, mechanicName: String?) {
        viewModelScope.launch {
            dao.updateStatus(id, status, mechanicName)
        }
    }
}