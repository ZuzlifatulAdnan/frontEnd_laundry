package com.example.cleanwashlaundromat.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.cleanwashlaundromat.data.local.SessionManager
import com.example.cleanwashlaundromat.databinding.ActivityLoginBinding
import com.example.cleanwashlaundromat.ui.beranda.BerandaActivity
import com.example.cleanwashlaundromat.ui.auth.ForgotPasswordActivity
import com.example.cleanwashlaundromat.ui.auth.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnMasuk.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                loginViewModel.executeLogin(email, password)
            }
        }

        binding.tvLupaPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        binding.tvDaftarSekarang.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        loginViewModel.loginResult.observe(this) { result ->
            when (result) {
                is LoginResult.Loading -> showLoading(true)
                is LoginResult.Success -> {
                    showLoading(false)
                    val response = result.data

                    // PERBAIKAN: Cek apakah login berhasil DAN rolenya adalah "Customer"
                    if (response.token != null && response.user?.role?.equals("Customer", ignoreCase = true) == true) {
                        // Jika berhasil dan role sesuai, lanjutkan login
                        handleLoginSuccess(response.token)
                    } else if (response.token != null) {
                        // Jika login berhasil tapi role TIDAK sesuai
                        Toast.makeText(this, "Hanya customer yang dapat login melalui aplikasi ini.", Toast.LENGTH_LONG).show()
                    } else {
                        // Jika login gagal (token null)
                        Toast.makeText(this, "Username atau password salah.", Toast.LENGTH_LONG).show()
                    }
                }
                is LoginResult.Error -> {
                    showLoading(false)
                    val errorMessage = result.message ?: "Terjadi kesalahan, coba lagi."
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun handleLoginSuccess(token: String) {
        Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show()

        val sessionManager = SessionManager(this)
        sessionManager.saveAuthToken(token)

        val intent = Intent(this, BerandaActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun validateInput(email: String, password: String): Boolean {
        binding.etEmail.error = null
        binding.etPassword.error = null
        if (email.isEmpty()) {
            binding.etEmail.error = "Email tidak boleh kosong"
            binding.etEmail.requestFocus()
            return false
        }
        if (password.isEmpty()) {
            binding.etPassword.error = "Password tidak boleh kosong"
            binding.etPassword.requestFocus()
            return false
        }
        return true
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnMasuk.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
    }
}
