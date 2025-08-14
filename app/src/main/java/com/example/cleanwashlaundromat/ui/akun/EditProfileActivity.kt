package com.example.cleanwashlaundromat.ui.akun

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.cleanwashlaundromat.R
import com.example.cleanwashlaundromat.data.model.User
import com.example.cleanwashlaundromat.data.remote.ApiClient
import com.example.cleanwashlaundromat.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val viewModel: AkunViewModel by viewModels()
    private var selectedImageUri: Uri? = null

    // PERBAIKAN: Menggunakan Glide untuk menampilkan pratinjau
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            Glide.with(this)
                .load(it)
                .into(binding.ivProfileEdit)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            galleryLauncher.launch("image/*")
        } else {
            Toast.makeText(this, "Izin galeri diperlukan untuk memilih gambar", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("EXTRA_USER", User::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("EXTRA_USER") as? User
        }

        if (user == null) {
            Toast.makeText(this, "Data user tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        populateData(user)
        setupClickListeners()
        observeViewModel()
    }

    private fun populateData(user: User) {
        binding.etNama.setText(user.name)
        binding.etEmail.setText(user.email)
        binding.etNoHandphone.setText(user.noHandphone)

        // PERBAIKAN: Cek jika gambar ada sebelum memuatnya
        if (!user.image.isNullOrEmpty()) {
            val imageUrl = "${ApiClient.BASE_URL}img/user/${user.image}"
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_person_placeholder)
                .error(R.drawable.ic_person_placeholder)
                .into(binding.ivProfileEdit)
        } else {
            binding.ivProfileEdit.setImageResource(R.drawable.ic_person_placeholder)
        }
    }

    private fun setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.fabEditImage.setOnClickListener { checkGalleryPermission() }
        binding.btnSimpan.setOnClickListener {
            val name = binding.etNama.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val noHp = binding.etNoHandphone.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || noHp.isEmpty()) {
                Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!noHp.startsWith("62")) {
                Toast.makeText(this, "Nomor handphone harus diawali dengan 62", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.updateProfile(name, email, noHp, selectedImageUri)
        }
    }

    private fun observeViewModel() {
        viewModel.updateResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                finish()
            }.onFailure {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkGalleryPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                galleryLauncher.launch("image/*")
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }
}
