package com.papb.motorescue.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.papb.motorescue.data.local.AppDatabase
import com.papb.motorescue.data.model.RescueRequest
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class DriverViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).rescueDao()

    val historyList: StateFlow<List<RescueRequest>> = dao.getAllHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addDummyData() {
        viewModelScope.launch {
            val dummy = RescueRequest(
                id = UUID.randomUUID().toString(),
                driverName = "Harry Driver",
                problemDesc = "Ban Bocor (Tes Dummy)",
                status = "WAITING",
                address = "Jl. Percobaan No. ${ (1..100).random() }",
                latitude = -7.9,
                longitude = 112.6
            )
            dao.insertRescue(dummy)
        }
    }
}