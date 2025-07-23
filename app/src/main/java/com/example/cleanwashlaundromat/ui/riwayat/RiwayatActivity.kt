package com.example.cleanwashlaundromat.ui.riwayat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cleanwashlaundromat.R
import com.example.cleanwashlaundromat.databinding.ActivityRiwayatBinding
import com.example.cleanwashlaundromat.ui.akun.AkunActivity
import com.example.cleanwashlaundromat.ui.beranda.BerandaActivity
import com.example.cleanwashlaundromat.ui.order.OrderActivity
import java.util.Calendar

class RiwayatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRiwayatBinding
    private val viewModel: RiwayatViewModel by viewModels()
    private lateinit var riwayatAdapter: RiwayatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiwayatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupFilters()
        setupBottomNavigation()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        riwayatAdapter = RiwayatAdapter(emptyList())
        binding.rvRiwayat.apply {
            layoutManager = LinearLayoutManager(this@RiwayatActivity)
            adapter = riwayatAdapter
        }

        riwayatAdapter.onAksiClickListener = { order ->
            val intent = Intent(this, DetailRiwayatActivity::class.java)
            intent.putExtra("EXTRA_ORDER_ID", order.id)
            startActivity(intent)
        }
    }

    private fun setupFilters() {
        // PERUBAHAN: Menambahkan status baru ke filter
        val statusList = listOf("Semua Status", "Diterima", "Pending", "Diproses", "Ditunda", "Selesai", "Dibatalkan")
        val statusAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, statusList)
        binding.actvStatus.setAdapter(statusAdapter)
        binding.actvStatus.setOnItemClickListener { _, _, position, _ ->
            viewModel.currentStatus = if (position == 0) null else binding.actvStatus.text.toString().lowercase()
            viewModel.fetchRiwayat()
        }

        val bulanList = listOf("Semua Bulan") + (1..12).map { it.toString() }
        val bulanAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, bulanList)
        binding.actvBulan.setAdapter(bulanAdapter)
        binding.actvBulan.setOnItemClickListener { _, _, position, _ ->
            viewModel.currentBulan = if (position == 0) null else position.toString()
            viewModel.fetchRiwayat()
        }

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val tahunList = listOf("Semua Tahun") + (currentYear - 5..currentYear).map { it.toString() }.reversed()
        val tahunAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, tahunList)
        binding.actvTahun.setAdapter(tahunAdapter)
        binding.actvTahun.setOnItemClickListener { _, _, position, _ ->
            viewModel.currentTahun = if (position == 0) null else binding.actvTahun.text.toString()
            viewModel.fetchRiwayat()
        }

        val sortAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mapOf("Terbaru" to "desc", "Terlama" to "asc").keys.toList())
        binding.actvSort.setAdapter(sortAdapter)
        binding.actvSort.setOnItemClickListener { _, _, position, _ ->
            viewModel.currentSort = if (position == 0) "desc" else "asc"
            viewModel.fetchRiwayat()
        }
    }

    private fun observeViewModel() {
        viewModel.riwayatList.observe(this) { orders ->
            binding.tvEmpty.visibility = if (orders.isEmpty()) View.VISIBLE else View.GONE
            riwayatAdapter.updateData(orders)
        }
        viewModel.isLoading.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
        viewModel.errorMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_riwayat
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_beranda -> startActivity(Intent(this, BerandaActivity::class.java))
                R.id.nav_order -> startActivity(Intent(this, OrderActivity::class.java))
                R.id.nav_riwayat -> return@setOnItemSelectedListener true
                R.id.nav_akun -> startActivity(Intent(this, AkunActivity::class.java))
            }
            overridePendingTransition(0, 0)
            true
        }
    }
}

