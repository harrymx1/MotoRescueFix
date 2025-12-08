package com.papb.motorescue.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class Consultation(
    val id: String = "",
    val motorType: String,
    val problem: String,
    val mechanicReply: String = ""
)

interface ApiService {
    @GET("consultations")
    suspend fun getConsultations(): List<Consultation>

    @POST("consultations")
    suspend fun addConsultation(@Body data: Consultation): Consultation

    @PUT("consultations/{id}")
    suspend fun updateConsultation(@Path("id") id: String, @Body data: Consultation): Consultation

    @DELETE("consultations/{id}")
    suspend fun deleteConsultation(@Path("id") id: String): Consultation
}

object RetrofitClient {
    private const val BASE_URL = "https://693596eafa8e704dafbe4907.mockapi.io/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}