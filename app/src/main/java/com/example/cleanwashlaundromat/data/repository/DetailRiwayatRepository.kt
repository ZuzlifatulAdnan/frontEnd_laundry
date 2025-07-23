package com.example.cleanwashlaundromat.data.repository

import android.content.Context
import com.example.cleanwashlaundromat.data.remote.ApiClient

class DetailRiwayatRepository(context: Context) {
    private val apiService = ApiClient.getInstance(context)

    suspend fun getDetailRiwayat(orderId: Int) = apiService.getDetailRiwayat(orderId)
}