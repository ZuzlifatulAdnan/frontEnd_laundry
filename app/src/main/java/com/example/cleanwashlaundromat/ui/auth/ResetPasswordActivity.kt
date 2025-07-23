package com.example.cleanwashlaundromat.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.cleanwashlaundromat.data.model.ResetPasswordRequest
import com.example.cleanwashlaundromat.databinding.ActivityResetPasswordBinding

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnUbahPassword.setOnClickListener { validateAndSubmit() }
        observeViewModel()
    }

    private fun validateAndSubmit() {
        // Menggunakan ID yang sudah disesuaikan
        val token = binding.etToken.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPasswordBaru.text.toString()
        val confirmPassword = binding.etKonfirmasiPasswordBaru.text.toString()

        if (token.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }
        if (password != confirmPassword) {
            Toast.makeText(this, "Konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show()
            return
        }

        val request = ResetPasswordRequest(token, email, password, confirmPassword)
        authViewModel.resetPassword(request)
    }

    private fun observeViewModel() {
        authViewModel.resetPasswordResult.observe(this) { result ->
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
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnUbahPassword.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
    }
}