package com.example.cleanwashlaundromat.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RiwayatResponse(
    val orders: OrderPaging
) : Serializable

data class OrderPaging(
    val data: List<OrderRiwayat>
    // Anda bisa menambahkan field paginasi lain jika diperlukan
) : Serializable

data class OrderRiwayat(
    val id: Int,
    @SerializedName("no_order") val noOrder: String,

    // PERBAIKAN: Mengubah key JSON yang diharapkan dari "service" menjadi "service_type"
    @SerializedName("service_type") val serviceType: String,

    @SerializedName("tanggal_order") val tanggalOrder: String,
    @SerializedName("jam_order") val jamOrder: String,
    @SerializedName("total_biaya") val totalBiaya: Long,
    val status: String,
    val pembayaran: PembayaranRiwayat?
) : Serializable
data class PembayaranRiwayat(
    val id: Int,
    val status: String
) : Serializable
