package com.example.cleanwashlaundromat.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

// --- Register ---
data class RegisterRequest(
    val name: String,
    val email: String,
    @SerializedName("no_handphone")
    val noHandphone: String,
    val password: String,
    @SerializedName("password_confirmation")
    val passwordConfirmation: String
)

data class RegisterResponse(
    val message: String,
    val user: User,
    val token: String
) : Serializable

// --- Forgot Password ---
data class ForgotPasswordRequest(
    val email: String
)

data class ForgotPasswordResponse(
    val message: String
)

// --- Reset Password ---
data class ResetPasswordRequest(
    val token: String,
    val email: String,
    val password: String,
    @SerializedName("password_confirmation")
    val passwordConfirmation: String
)

data class ResetPasswordResponse(
    val message: String
)