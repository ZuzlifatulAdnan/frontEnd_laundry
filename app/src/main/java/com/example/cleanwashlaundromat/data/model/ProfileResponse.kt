package com.example.cleanwashlaundromat.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

// Wrapper untuk response GET /profile
data class ProfileResponse(
    val user: User
) : Serializable

// Model utama untuk data user
data class User(
    val id: Int,
    val name: String,
    val email: String,
    @SerializedName("no_handphone")
    val noHandphone: String?,
    val role: String,
    val image: String? // Nama file gambar, bukan URL lengkap
) : Serializable

// Wrapper untuk response POST /profile/update
data class UpdateProfileResponse(
    val message: String,
    val user: User
) : Serializable

// Model untuk request ganti password
data class ChangePasswordRequest(
    @SerializedName("current_password")
    val currentPassword: String,
    @SerializedName("new_password")
    val newPassword: String,
    @SerializedName("new_password_confirmation")
    val newPasswordConfirmation: String
)

// Model untuk response ganti password
data class ChangePasswordResponse(
    val message: String
)
