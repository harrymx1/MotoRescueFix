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
        startAutoRefresh()
    }

    private fun startAutoRefresh() {
        viewModelScope.launch {
            fetchConsultations()

            while (isActive) {
                delay(3000)
                try {
                    val newData = RetrofitClient.instance.getConsultations().reversed()
                    consultationList = newData
                } catch (e: Exception) {
                }
            }
        }
    }

    // GET
    fun fetchConsultations() {
        viewModelScope.launch {
            isLoading = true
            try {
                consultationList = RetrofitClient.instance.getConsultations().reversed()
            } catch (e: Exception) {
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
            } catch (e: Exception) { uploadStatus = "Gagal kirim." }
        }
    }

    // DELETE
    fun deleteItem(id: String) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.deleteConsultation(id)
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