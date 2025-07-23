package com.example.cleanwashlaundromat.ui.beranda

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.cleanwashlaundromat.R
import com.example.cleanwashlaundromat.data.model.BerandaResponse
import com.example.cleanwashlaundromat.databinding.ActivityBerandaBinding
import com.example.cleanwashlaundromat.ui.akun.AkunActivity
import com.example.cleanwashlaundromat.ui.order.OrderActivity
import com.example.cleanwashlaundromat.ui.riwayat.RiwayatActivity
import com.google.android.material.tabs.TabLayoutMediator

class BerandaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBerandaBinding
    private val viewModel: BerandaViewModel by viewModels()
    private lateinit var mesinAdapter: MesinAdapter
    private val apiHost = "192.168.42.157"

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private val scrollDelay: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBerandaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupBottomNavigation()
        setupBannerCarousel()
        observeViewModel()
        viewModel.fetchBerandaData()
    }

    private fun setupBannerCarousel() {
        val bannerUrls = listOf(
            "http://$apiHost:8000/img/beranda/beranda1.jpeg",
            "http://$apiHost:8000/img/beranda/beranda2.jpeg"
        )
        val bannerAdapter = BannerAdapter(bannerUrls)
        binding.viewPagerBanner.adapter = bannerAdapter

        TabLayoutMediator(binding.tabLayoutIndicator, binding.viewPagerBanner) { _, _ -> }.attach()

        runnable = Runnable {
            var currentItem = binding.viewPagerBanner.currentItem
            currentItem++
            if (currentItem >= bannerAdapter.itemCount) {
                currentItem = 0
            }
            binding.viewPagerBanner.setCurrentItem(currentItem, true)
            handler.postDelayed(runnable, scrollDelay)
        }
    }

    private fun setupRecyclerView() {
        mesinAdapter = MesinAdapter(emptyList())
        binding.rvDaftarMesin.adapter = mesinAdapter
        binding.rvDaftarMesin.layoutManager = GridLayoutManager(this, 2)
    }

    private fun observeViewModel() {
        viewModel.berandaResult.observe(this) { result ->
            when (result) {
                is BerandaResult.Loading -> {}
                is BerandaResult.Success -> {
                    populateData(result.data)
                }
                is BerandaResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun populateData(data: BerandaResponse) {
        binding.tvUserName.text = "Hi, ${data.user.name}"
        val correctedUserImageUrl = data.user.imageUrl?.replace("127.0.0.1", apiHost)
        Glide.with(this).load(correctedUserImageUrl).circleCrop()
            .placeholder(R.mipmap.ic_launcher_round)
            .error(R.mipmap.ic_launcher_round)
            .into(binding.ivUserImage)

        mesinAdapter = MesinAdapter(data.mesinReady)
        binding.rvDaftarMesin.adapter = mesinAdapter
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_beranda
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_beranda -> true
                R.id.nav_order -> {
                    startActivity(Intent(this, OrderActivity::class.java))
                     overridePendingTransition(0, 0)
                                      true
                }
                R.id.nav_riwayat -> {
                   startActivity(Intent(this, RiwayatActivity::class.java))
                    overridePendingTransition(0, 0)
                                       true
                }
                R.id.nav_akun -> {
                   startActivity(Intent(this, AkunActivity::class.java))
                   overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, scrollDelay)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }
}
