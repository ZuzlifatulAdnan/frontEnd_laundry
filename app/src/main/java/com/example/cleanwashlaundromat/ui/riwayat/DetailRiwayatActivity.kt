package com.example.cleanwashlaundromat.ui.riwayat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.cleanwashlaundromat.R
import com.example.cleanwashlaundromat.data.model.OrderRiwayatDetail
import com.example.cleanwashlaundromat.databinding.ActivityDetailRiwayatBinding
import com.example.cleanwashlaundromat.ui.order.OrderActivity
import com.example.cleanwashlaundromat.ui.pembayaran.PembayaranActivity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class DetailRiwayatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailRiwayatBinding
    private val viewModel: DetailRiwayatViewModel by viewModels()
    private var orderId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailRiwayatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderId = intent.getIntExtra("EXTRA_ORDER_ID", -1)
        if (orderId == -1) {
            Toast.makeText(this, "ID Order tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        observeViewModel()
        viewModel.fetchDetailRiwayat(orderId)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun observeViewModel() {
        viewModel.detailOrder.observe(this) {
            populateData(it)
        }
        viewModel.isLoading.observe(this) {
            // Tampilkan/sembunyikan ProgressBar jika ada
        }
        viewModel.errorMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }
    }

    private fun populateData(order: OrderRiwayatDetail) {
        val tanggalFormatted = try {
            val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            displayFormat.format(apiFormat.parse(order.tanggalOrder)!!)
        } catch (e: Exception) {
            order.tanggalOrder
        }

        binding.tvNamaPengguna.text = order.user?.name ?: "-"
        binding.tvNoOrder.text = order.noOrder
        binding.tvNamaMesin.text = order.mesin?.namaMesin ?: "-"
        binding.tvServiceType.text = order.serviceType
        binding.tvTanggalOrder.text = tanggalFormatted
        binding.tvJamOrder.text = order.jamOrder
        binding.tvCatatan.text = order.catatan ?: "-"
        binding.chipStatusOrder.text = order.status
        binding.chipStatusOrder.setChipBackgroundColorResource(getStatusColor(order.status))

        order.pembayaran?.let { pembayaran ->
            val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
            binding.tvNoPembayaran.text = pembayaran.noPembayaran
            binding.tvMetodePembayaran.text = pembayaran.metodePembayaran ?: "Belum dipilih"
            binding.tvJumlahDibayar.text = formatRupiah.format(order.totalBiaya)
            binding.chipStatusPembayaran.text = pembayaran.status
            binding.chipStatusPembayaran.setChipBackgroundColorResource(getStatusColor(pembayaran.status))

            if (pembayaran.status.equals("unpaid", ignoreCase = true) || pembayaran.status.equals("menunggu pembayaran", ignoreCase = true)) {
                binding.btnLanjutkanPembayaran.visibility = View.VISIBLE
                binding.btnLanjutkanPembayaran.setOnClickListener {
                    val intent = Intent(this, PembayaranActivity::class.java)
                    intent.putExtra(OrderActivity.EXTRA_PEMBAYARAN_ID, pembayaran.id)
                    startActivity(intent)
                }
            } else {
                binding.btnLanjutkanPembayaran.visibility = View.GONE
            }
        }
    }

    private fun getStatusColor(status: String): Int {
        return when (status.lowercase()) {
            "pending" -> R.color.status_pending
            "diproses" -> R.color.status_diproses
            "selesai" -> R.color.status_selesai
            "dibatalkan" -> R.color.status_dibatalkan
            "unpaid", "menunggu pembayaran" -> R.color.status_dibatalkan
            "proses pembayaran" -> R.color.status_diproses
            "paid", "lunas" -> R.color.status_selesai
            else -> R.color.grey_dark
        }
    }
}
