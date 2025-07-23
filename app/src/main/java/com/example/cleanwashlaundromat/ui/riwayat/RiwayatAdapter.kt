package com.example.cleanwashlaundromat.ui.riwayat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cleanwashlaundromat.R
import com.example.cleanwashlaundromat.data.model.OrderRiwayat
import com.example.cleanwashlaundromat.databinding.ItemRiwayatBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class RiwayatAdapter(private var orders: List<OrderRiwayat>) :
    RecyclerView.Adapter<RiwayatAdapter.RiwayatViewHolder>() {

    var onAksiClickListener: ((OrderRiwayat) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiwayatViewHolder {
        val binding = ItemRiwayatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RiwayatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RiwayatViewHolder, position: Int) {
        holder.bind(orders[position], position + 1)
    }

    override fun getItemCount(): Int = orders.size

    fun updateData(newOrders: List<OrderRiwayat>) {
        orders = newOrders
        notifyDataSetChanged()
    }

    inner class RiwayatViewHolder(private val binding: ItemRiwayatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderRiwayat, nomor: Int) {
            // Mengisi data ke semua komponen yang ada di XML baru
            binding.tvNomor.text = nomor.toString()
            binding.tvNoOrder.text = order.noOrder
            binding.tvService.text = order.serviceType
            binding.tvJam.text = order.jamOrder // Mengembalikan jam

            // Format Tanggal
            binding.tvTanggal.text = try {
                val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val displayFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
                displayFormat.format(apiFormat.parse(order.tanggalOrder)!!)
            } catch (e: Exception) {
                order.tanggalOrder
            }

            // Format Total Biaya
            val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
            binding.tvTotalBiaya.text = formatRupiah.format(order.totalBiaya).replace(",00", "")

            // Mengatur status order pada Chip
            binding.chipStatusOrder.text = order.status
            binding.chipStatusOrder.setChipBackgroundColorResource(getStatusOrderColor(order.status))

            // Mengatur status pembayaran pada Chip
            val statusPembayaran = order.pembayaran?.status ?: "N/A"
            binding.chipStatusPembayaran.text = statusPembayaran
            binding.chipStatusPembayaran.setChipBackgroundColorResource(getStatusPembayaranColor(statusPembayaran))

            // Listener untuk tombol aksi
            binding.btnAksi.setOnClickListener {
                onAksiClickListener?.invoke(order)
            }
        }

        private fun getStatusOrderColor(status: String): Int {
            return when (status.lowercase()) {
                "diterima" -> R.color.status_diterima
                "diproses" -> R.color.status_diproses
                "ditunda" -> R.color.status_ditunda
                "selesai" -> R.color.status_selesai
                "dibatalkan" -> R.color.status_dibatalkan
                else -> R.color.grey_dark
            }
        }

        private fun getStatusPembayaranColor(status: String): Int {
            return when (status.lowercase()) {
                "unpaid", "menunggu pembayaran" -> R.color.status_dibatalkan // Merah
                "proses pembayaran" -> R.color.status_diproses // Biru
                "paid", "Pembayaran Berhasil" -> R.color.status_selesai // Hijau
                else -> R.color.grey_dark
            }
        }
    }
}