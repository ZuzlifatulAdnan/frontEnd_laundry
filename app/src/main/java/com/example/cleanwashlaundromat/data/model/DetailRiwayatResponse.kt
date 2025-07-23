package com.example.cleanwashlaundromat.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DetailRiwayatResponse(
    val order: OrderRiwayatDetail
) : Serializable

data class OrderRiwayatDetail(
    val id: Int,
    @SerializedName("no_order") val noOrder: String,
    @SerializedName("service") val serviceType: String,
    @SerializedName("tanggal_order") val tanggalOrder: String,
    @SerializedName("jam_order") val jamOrder: String,
    val status: String,
    val catatan: String?,
    @SerializedName("total_biaya") val totalBiaya: Long,
    val user: UserDetail?,
    val mesin: MesinDetail?,
    val pembayaran: PembayaranDetailRiwayat?
) : Serializable

data class UserDetail(
    val id: Int,
    val name: String
) : Serializable

data class MesinDetail(
    val id: Int,
    @SerializedName("nama") val namaMesin: String
) : Serializable

data class PembayaranDetailRiwayat(
    val id: Int,
    @SerializedName("no_pembayaran") val noPembayaran: String,
    @SerializedName("metode_pembayaran") val metodePembayaran: String?,
    val status: String
) : Serializable
