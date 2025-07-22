package com.example.cleanwashlaundromat.data.model

data class LoginResponse(
    val success: Boolean, // <-- Laravel Anda tidak mengirim ini
    val message: String,  // <-- Laravel Anda tidak mengirim ini
    val token: String?,   // <-- Nama kuncinya 'token', bukan 'jwt-token'
    val user: User?
)
data class User(
    val id: Int,
    val name: String,
    val email: String
)