package com.example.cleanwashlaundromat.data.repository

import android.content.Context
import com.example.cleanwashlaundromat.data.remote.ApiClient

class RiwayatRepository(context: Context) {
    private val apiService = ApiClient.getInstance(context)

    suspend fun getRiwayat(status: String?, bulan: String?, tahun: String?, sort: String?) =
        apiService.getRiwayat(status, bulan, tahun, sort)
}