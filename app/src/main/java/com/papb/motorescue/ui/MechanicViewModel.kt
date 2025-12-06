package com.papb.motorescue.ui

import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.papb.motorescue.data.model.RescueRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MechanicViewModel : ViewModel() {

    private val firebaseDb = FirebaseDatabase.getInstance("https://motorescue-52d7c-default-rtdb.asia-southeast1.firebasedatabase.app")
        .getReference("laporan")

    private val _orderList = MutableStateFlow<List<RescueRequest>>(emptyList())
    val orderList: StateFlow<List<RescueRequest>> = _orderList.asStateFlow()

    init {
        startListening()
    }

    private fun startListening() {
        firebaseDb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updatedList = mutableListOf<RescueRequest>()

                for (child in snapshot.children) {
                    val order = child.getValue(RescueRequest::class.java)
                    if (order != null && order.status == "WAITING") {
                        updatedList.add(order)
                    }
                }

                _orderList.value = updatedList
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}