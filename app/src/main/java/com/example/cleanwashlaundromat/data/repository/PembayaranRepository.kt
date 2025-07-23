package com.example.cleanwashlaundromat.data.repository

import android.content.Context
import com.example.cleanwashlaundromat.data.remote.ApiClient
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PembayaranRepository(context: Context) {
    private val apiService = ApiClient.getInstance(context)

    suspend fun getPembayaranDetails(pembayaranId: Int) =
        apiService.getPembayaranDetails(pembayaranId)

    suspend fun updatePembayaran(
        pembayaranId: Int,
        metodePembayaran: RequestBody,
        buktiBayar: MultipartBody.Part?
    ) = apiService.updatePembayaran(pembayaranId, metodePembayaran, buktiBayar)
}
