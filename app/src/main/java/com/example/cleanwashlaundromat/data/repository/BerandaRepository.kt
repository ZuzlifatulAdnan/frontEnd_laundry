package com.example.cleanwashlaundromat.data.repository

import android.content.Context
import com.example.cleanwashlaundromat.data.remote.ApiClient

class BerandaRepository(context: Context) {
    private val apiService = ApiClient.getInstance(context)

    // Panggil fungsi API yang baru dan benar
    suspend fun getBerandaData() = apiService.getBerandaData()
}
