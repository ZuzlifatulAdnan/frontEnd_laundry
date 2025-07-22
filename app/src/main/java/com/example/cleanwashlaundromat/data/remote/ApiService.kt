package com.example.cleanwashlaundromat.data.remote

import com.example.cleanwashlaundromat.data.model.BerandaResponse
import com.example.cleanwashlaundromat.data.model.DropOffOrderRequest
import com.example.cleanwashlaundromat.data.model.LoginRequest
import com.example.cleanwashlaundromat.data.model.LoginResponse
import com.example.cleanwashlaundromat.data.model.Mesin
import com.example.cleanwashlaundromat.data.model.MesinReadyResponse
import com.example.cleanwashlaundromat.data.model.OrderSubmissionResponse
import com.example.cleanwashlaundromat.data.model.SelfServiceOrderRequest
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("api/admin/login")
    suspend fun loginAdmin(@Body request: LoginRequest): Response<LoginResponse>

    // Ganti endpoint dan response type
    @GET("api/admin/beranda") // Asumsi endpoint baru adalah /api/beranda
    suspend fun getBerandaData(): Response<BerandaResponse>

    @GET("api/admin/order/mesin-ready")
    suspend fun getMesinReady(): Response<MesinReadyResponse>

    @POST("api/admin/order/selfservice")
    suspend fun storeSelfService(@Body request: SelfServiceOrderRequest): Response<OrderSubmissionResponse>

    @POST("api/admin/order/dropoff")
    suspend fun storeDropOff(@Body request: DropOffOrderRequest): Response<OrderSubmissionResponse>
}

