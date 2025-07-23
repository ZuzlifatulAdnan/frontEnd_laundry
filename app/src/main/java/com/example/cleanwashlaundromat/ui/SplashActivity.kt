package com.example.cleanwashlaundromat.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.cleanwashlaundromat.data.local.SessionManager
import com.example.cleanwashlaundromat.databinding.ActivitySplashBinding
import com.example.cleanwashlaundromat.ui.auth.LoginActivity
import com.example.cleanwashlaundromat.ui.beranda.BerandaActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val splashTimeOut: Long = 3000 // 3 detik

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Gunakan Handler untuk menunda perpindahan ke activity berikutnya
        Handler(Looper.getMainLooper()).postDelayed({
            checkUserSession()
        }, splashTimeOut)
    }

    private fun checkUserSession() {
        val sessionManager = SessionManager(this)
        val token = sessionManager.fetchAuthToken()

        // Cek apakah token ada (user sudah login)
        if (token != null) {
            // Jika sudah login, arahkan ke BerandaActivity
            val intent = Intent(this, BerandaActivity::class.java)
            startActivity(intent)
        } else {
            // Jika belum login, arahkan ke LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Tutup SplashActivity agar tidak bisa kembali
        finish()
    }
}
