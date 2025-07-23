package com.example.cleanwashlaundromat.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.cleanwashlaundromat.data.model.ForgotPasswordRequest
import com.example.cleanwashlaundromat.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // PERBAIKAN: Menggunakan ID baru dari XML Anda
        binding.btnKirimEmail.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                authViewModel.sendForgotPasswordLink(ForgotPasswordRequest(email))
            } else {
                Toast.makeText(this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        authViewModel.forgotPasswordResult.observe(this) { result ->
            when(result) {
                is AuthResult.Loading -> showLoading(true)
                is AuthResult.Success -> {
                    showLoading(false)
                    Toast.makeText(this, result.data.message, Toast.LENGTH_LONG).show()
                    finish() // Kembali ke halaman login
                }
                is AuthResult.Error -> {
                    showLoading(false)
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        // PERBAIKAN: Menonaktifkan tombol karena ProgressBar sudah tidak ada
        binding.btnKirimEmail.isEnabled = !isLoading
    }
}