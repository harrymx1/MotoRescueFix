package com.papb.motorescue.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.papb.motorescue.data.remote.Consultation
import com.papb.motorescue.data.remote.RetrofitClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ConsultationViewModel : ViewModel() {
    var consultationList by mutableStateOf<List<Consultation>>(emptyList())
    var isLoading by mutableStateOf(false)
    var uploadStatus by mutableStateOf("")

    init {
        // Otomatis mulai memantau data saat ViewModel dibuat
        startAutoRefresh()
    }

    // FUNGSI BARU: Polling (Cek server tiap 3 detik)
    private fun startAutoRefresh() {
        viewModelScope.launch {
            // Loading awal saja yang pakai spinner
            fetchConsultations()

            // Selanjutnya refresh diam-diam (Silent Refresh)
            while (isActive) {
                delay(3000) // Tunggu 3 detik
                try {
                    // Ambil data terbaru tanpa mengubah status 'isLoading' biar layar gak kedip
                    val newData = RetrofitClient.instance.getConsultations().reversed()
                    consultationList = newData
                } catch (e: Exception) {
                    // Kalau error koneksi saat refresh, abaikan saja (tunggu loop berikutnya)
                }
            }
        }
    }

    // GET Manual (Dipakai untuk loading pertama)
    fun fetchConsultations() {
        viewModelScope.launch {
            isLoading = true
            try {
                consultationList = RetrofitClient.instance.getConsultations().reversed()
            } catch (e: Exception) {
                // Error handling
            } finally {
                isLoading = false
            }
        }
    }

    // POST
    fun sendConsultation(motor: String, keluhan: String) {
        viewModelScope.launch {
            uploadStatus = "Mengirim..."
            try {
                val newData = Consultation(motorType = motor, problem = keluhan, mechanicReply = "")
                RetrofitClient.instance.addConsultation(newData)
                uploadStatus = "Terkirim!"
                // Gak perlu fetch manual, nanti auto-refresh yang akan ambil datanya
            } catch (e: Exception) { uploadStatus = "Gagal kirim." }
        }
    }

    // DELETE
    fun deleteItem(id: String) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.deleteConsultation(id)
                // Data akan hilang otomatis di refresh berikutnya (max 3 detik)
            } catch (e: Exception) { }
        }
    }

    // PUT
    fun updateItem(item: Consultation) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.updateConsultation(item.id, item)
            } catch (e: Exception) { }
        }
    }
}