package com.example.cleanwashlaundromat.data.model

import com.google.gson.annotations.SerializedName

// Response untuk GET /pembayaran/{id}
data class PembayaranDetailResponse(
    val pembayaran: PembayaranDetail
)

data class PembayaranDetail(
    val id: Int,
    @SerializedName("order_id")
    val orderId: Int,
    @SerializedName("jumlah_dibayar")
    val jumlahDibayar: Long,
    @SerializedName("metode_pembayaran")
    val metodePembayaran: String?,
    @SerializedName("bukti_bayar")
    val buktiBayar: String?,
    val status: String,
    val order: OrderDetail // Objek order yang di-nest
)

data class OrderDetail(
    val id: Int,
    @SerializedName("nama_pemesan")
    val namaPemesan: String,
    // Tambahkan field lain dari order jika perlu ditampilkan
)

// Response untuk POST /pembayaran/{id}
data class PembayaranUpdateResponse(
    val message: String,
    val pembayaran: PembayaranDetail
)
