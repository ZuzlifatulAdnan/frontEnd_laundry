package com.example.cleanwashlaundromat.ui.akun

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.cleanwashlaundromat.R
import com.example.cleanwashlaundromat.data.local.SessionManager
import com.example.cleanwashlaundromat.databinding.ActivityAkunBinding
import com.example.cleanwashlaundromat.ui.beranda.BerandaActivity
import com.example.cleanwashlaundromat.ui.auth.LoginActivity
import com.example.cleanwashlaundromat.ui.order.OrderActivity
import com.example.cleanwashlaundromat.ui.riwayat.RiwayatActivity

class AkunActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAkunBinding
    private val viewModel: AkunViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAkunBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        setupBottomNavigation()
        observeViewModel()
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchProfile()
    }

    private fun setupClickListeners() {
        binding.btnEdit.setOnClickListener {
            viewModel.user.value?.let { user ->
                val intent = Intent(this, EditProfileActivity::class.java)
                intent.putExtra("EXTRA_USER", user)
                startActivity(intent)
            }
        }

        binding.btnChangePassword.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        binding.btnKeluar.setOnClickListener {
            sessionManager.clearSession()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun observeViewModel() {
        viewModel.user.observe(this) { user ->
            binding.profileContent.visibility = View.VISIBLE
            binding.tvNama.text = user.name
            binding.tvEmail.text = user.email
            binding.tvNoHandphone.text = user.noHandphone ?: "-"

            val imageUrl = "http://10.0.2.2:8000/img/user/${user.image}"
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_person_placeholder)
                .error(R.drawable.ic_person_placeholder)
                .into(binding.ivProfile)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if(isLoading) binding.profileContent.visibility = View.GONE
        }

        viewModel.errorMessage.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_akun
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_beranda -> startActivity(Intent(this, BerandaActivity::class.java))
                R.id.nav_order -> startActivity(Intent(this, OrderActivity::class.java))
                R.id.nav_riwayat -> startActivity(Intent(this, RiwayatActivity::class.java))
                R.id.nav_akun -> return@setOnItemSelectedListener true
            }
            overridePendingTransition(0, 0)
            true
        }
    }
}
