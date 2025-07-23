package com.example.cleanwashlaundromat.data.repository

import android.content.Context
import com.example.cleanwashlaundromat.data.model.ChangePasswordRequest
import com.example.cleanwashlaundromat.data.remote.ApiClient
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ProfileRepository(context: Context) {
    private val apiService = ApiClient.getInstance(context)

    suspend fun getProfile() = apiService.getProfile()

    suspend fun updateProfile(
        name: RequestBody,
        email: RequestBody,
        noHandphone: RequestBody,
        image: MultipartBody.Part?
    ) = apiService.updateProfile(name, email, noHandphone, image)

    suspend fun changePassword(request: ChangePasswordRequest) = apiService.changePassword(request)
}
