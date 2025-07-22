package com.example.cleanwashlaundromat.ui.login

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

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // LOG DASAR: Untuk memastikan Activity ini dibuat
        Log.d("AppFlow", "LOGIN_ACTIVITY: onCreate dimulai.")

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeViewModel()
        Log.d("AppFlow", "LOGIN_ACTIVITY: onCreate selesai, listener dan observer sudah di-set.")
    }

    private fun setupListeners() {
        binding.btnMasuk.setOnClickListener {
            // LOG DASAR: Untuk memastikan tombol Masuk berfungsi
            Log.d("AppFlow", "BUTTON_CLICK: Tombol Masuk diklik.")

            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                Log.d("AppFlow", "VALIDATION_SUCCESS: Input valid, memanggil ViewModel.")
                loginViewModel.executeLogin(email, password)
            }
        }
    }

    private fun observeViewModel() {
        loginViewModel.loginResult.observe(this) { result ->
            when (result) {
                is LoginResult.Loading -> showLoading(true)
                is LoginResult.Success -> {
                    showLoading(false)
                    val response = result.data
                    if (response.success && response.token != null) {
                        handleLoginSuccess(response.token)
                    } else {
                        val errorMessage = response.message ?: "Username atau password salah."
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
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
        Log.d("AppFlow", "LOGIN_SUCCESS: Memulai proses perpindahan ke Beranda.")

        val sessionManager = SessionManager(this)
        sessionManager.saveAuthToken(token)

        val intent = Intent(this, BerandaActivity::class.java)
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