package com.example.cleanwashlaundromat.data.repository

import android.content.Context
import com.example.cleanwashlaundromat.data.model.ForgotPasswordRequest
import com.example.cleanwashlaundromat.data.model.LoginRequest
import com.example.cleanwashlaundromat.data.model.RegisterRequest
import com.example.cleanwashlaundromat.data.model.ResetPasswordRequest
import com.example.cleanwashlaundromat.data.remote.ApiClient
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AuthRepository(context: Context) {
    private val apiService = ApiClient.getInstance(context)

    suspend fun login(request: LoginRequest) = apiService.loginAdmin(request)

    suspend fun register(
        name: RequestBody,
        email: RequestBody,
        noHandphone: RequestBody,
        password: RequestBody,
        passwordConfirmation: RequestBody,
        image: MultipartBody.Part?
    ) = apiService.register(name, email, noHandphone, password, passwordConfirmation, image)

    suspend fun forgotPassword(request: ForgotPasswordRequest) = apiService.forgotPassword(request)

    suspend fun resetPassword(request: ResetPasswordRequest) = apiService.resetPassword(request)
}
