package com.papb.motorescue

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.lifecycle.lifecycleScope
import com.papb.motorescue.data.local.AppDatabase
import com.papb.motorescue.data.model.RescueRequest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)
        val dao = database.rescueDao()

        lifecycleScope.launch {
            Log.d("TES_ROOM", "Sedang menyimpan data...")
            dao.insertRescue(
                RescueRequest(
                    id = "tes_01",
                    driverName = "Tes Driver Room",
                    problemDesc = "Cek Database Berhasil",
                    status = "WAITING"
                )
            )
            Log.d("TES_ROOM", "Data berhasil disimpan!")
        }

        lifecycleScope.launch {
            dao.getAllHistory().collect { daftarLaporan ->
                Log.d("TES_ROOM", "=== ISI DATABASE SEKARANG ===")
                daftarLaporan.forEach {
                    Log.d("TES_ROOM", "Data: ${it.driverName} - ${it.problemDesc}")
                }
            }
        }

        setContent {
            Text(text = "Cek Logcat keyword: TES_ROOM")
        }
    }
}