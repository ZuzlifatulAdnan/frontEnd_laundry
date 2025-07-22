package com.example.cleanwashlaundromat.data.model

import com.google.gson.annotations.SerializedName

// Model untuk data Mesin
data class Mesin(
    @SerializedName("id")
    val id: Int,
    @SerializedName("nama")
    val nama_mesin: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("durasi")
    val durasi: Int, // Durasi ditambahkan
    @SerializedName("status")
    val status: String
)

// Model untuk mengirim order Self Service
data class SelfServiceOrderRequest(
    @SerializedName("mesin_id") val mesinId: Int,
    @SerializedName("tanggal_order") val tanggalOrder: String,
    @SerializedName("jam_order") val jamOrder: String,
    @SerializedName("durasi") val durasi: Int,
    @SerializedName("koin") val koin: Int,
    @SerializedName("catatan") val catatan: String?,
    @SerializedName("total_biaya") val totalBiaya: Long
)

// Model untuk mengirim order Drop Off
data class DropOffOrderRequest(
    @SerializedName("tanggal_order") val tanggalOrder: String,
    @SerializedName("jam_order") val jamOrder: String,
    @SerializedName("berat") val berat: Double,
    @SerializedName("detergent") val detergent: Int,
    @SerializedName("tanggal_ambil") val tanggalAmbil: String,
    @SerializedName("catatan") val catatan: String?,
    @SerializedName("total_biaya") val totalBiaya: Long
)

// Model untuk response saat order berhasil dibuat
data class OrderSubmissionResponse(
    val message: String,
    val order: Order,
    val pembayaran: Pembayaran
)

data class Order(
    val id: Int,
    @SerializedName("total_biaya")
    val totalBiaya: Long
)

data class Pembayaran(
    val id: Int
)

// Model baru untuk response mesin ready
data class MesinReadyResponse(
    @SerializedName("mesins_ready")
    val mesinsReady: List<Mesin>
)