package com.example.cleanwashlaundromat.data.repository

import android.content.Context
import com.example.cleanwashlaundromat.data.model.LoginRequest
import com.example.cleanwashlaundromat.data.remote.ApiClient

class AuthRepository(context: Context) { // Tambahkan constructor context
    // Panggil getInstance(context)
    private val apiService = ApiClient.getInstance(context)

    suspend fun login(request: LoginRequest) = apiService.loginAdmin(request)
}
