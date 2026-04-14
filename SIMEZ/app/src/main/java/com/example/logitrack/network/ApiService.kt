package com.example.logitrack.network

import com.example.logitrack.data.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

const val BASE_URL = "http://10.0.2.2:5170/"

interface ApiService {

    // AUTH
    @POST("api/Usuaris/login")
    suspend fun login(@Body request: LoginRequest): Response<Usuari>

    // OFERTES - agente ve todas
    @GET("api/Ofertes")
    suspend fun getOfertes(): Response<List<Oferte>>

    // OFERTES - cliente ve las suyas
    @GET("api/Ofertes/client/{id}")
    suspend fun getOfertesByClient(@Path("id") clientId: Int): Response<List<Oferte>>

    @GET("api/Usuaris/{id}")
    suspend fun getUsuari(@Path("id") id: Int): Response<Usuari>

    @GET("api/Ofertes/{id}")
    suspend fun getOferte(@Path("id") id: Int): Response<Oferte>

    // ACTUALIZAR ESTAT - agente cambia estado
    @PUT("api/Ofertes/{id}")
    suspend fun updateOferte(@Path("id") id: Int, @Body oferte: Oferte): Response<Unit>

    // TRACKING STEPS
    @GET("api/TrackingSteps")
    suspend fun getTrackingSteps(): Response<List<TrackingStep>>

    // ESTATS OFERTES
    @GET("api/EstatsOfertes")
    suspend fun getEstatsOfertes(): Response<List<EstatsOferte>>

    @POST("api/Ofertes/{id}/acceptar")
    suspend fun acceptarOferta(@Path("id") id: Int): Response<Unit>

    @POST("api/Ofertes/{id}/rebutjar")
    suspend fun rebutjarOferta(@Path("id") id: Int, @Body request: RebutjarRequest): Response<Unit>
}

object RetrofitClient {
    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}