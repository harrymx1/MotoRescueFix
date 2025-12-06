package com.papb.motorescue.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.papb.motorescue.data.model.RescueRequest
import kotlinx.coroutines.flow.Flow

@Dao
interface RescueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRescue(rescue: RescueRequest)

    @Query("SELECT * FROM rescue_request ORDER BY id DESC")
    fun getAllHistory(): Flow<List<RescueRequest>>

    @Query("UPDATE rescue_request SET status = :newStatus, mechanicName = :mechName WHERE id = :reportId")
    suspend fun updateStatus(reportId: String, newStatus: String, mechName: String?)
}