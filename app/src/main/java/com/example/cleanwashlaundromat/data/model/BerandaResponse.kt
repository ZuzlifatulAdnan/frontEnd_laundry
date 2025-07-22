package com.example.cleanwashlaundromat.data.model

import com.google.gson.annotations.SerializedName

// Kelas utama yang membungkus seluruh response
data class BerandaResponse(
    @SerializedName("user")
    val user: UserData,

    @SerializedName("mesin_ready")
    val mesinReady: List<Mesin>
)

// Kelas untuk menampung data user
data class UserData(
    @SerializedName("name")
    val name: String,

    @SerializedName("image")
    val imageUrl: String? // Dibuat nullable jika gambar bisa kosong
)

