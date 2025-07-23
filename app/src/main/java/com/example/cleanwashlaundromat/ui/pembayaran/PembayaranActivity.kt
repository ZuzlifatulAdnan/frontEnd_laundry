package com.example.cleanwashlaundromat.ui.pembayaran

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.cleanwashlaundromat.R
import com.example.cleanwashlaundromat.databinding.ActivityPembayaranBinding
import com.example.cleanwashlaundromat.ui.order.OrderActivity
import com.example.cleanwashlaundromat.ui.riwayat.RiwayatActivity
import java.io.File
import java.text.NumberFormat
import java.util.Locale

class PembayaranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPembayaranBinding
    private val viewModel: PembayaranViewModel by viewModels()

    private var pembayaranId: Int = -1
    private var selectedImageUri: Uri? = null
    private var cameraImageUri: Uri? = null

    // Launcher untuk mengambil gambar dari kamera
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            cameraImageUri?.let {
                selectedImageUri = it
                binding.ivBuktiBayarPreview.setImageURI(it)
                showImagePreview()
            }
        }
    }

    // Launcher untuk memilih gambar dari galeri
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.ivBuktiBayarPreview.setImageURI(it)
            showImagePreview()
        }
    }

    // Launcher untuk meminta izin kamera
    private val requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(this, "Izin kamera diperlukan", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher untuk meminta izin galeri
    private val requestGalleryPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            galleryLauncher.launch("image/*")
        } else {
            Toast.makeText(this, "Izin galeri diperlukan", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPembayaranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pembayaranId = intent.getIntExtra(OrderActivity.EXTRA_PEMBAYARAN_ID, -1)
        if (pembayaranId == -1) {
            Toast.makeText(this, "Error: ID Pembayaran tidak ditemukan", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupMetodePembayaranDropdown()
        setupClickListeners()
        observeViewModel()

        viewModel.fetchPembayaranDetails(pembayaranId)
    }

    private fun setupMetodePembayaranDropdown() {
        val metode = listOf("Tunai", "Transfer Bank", "QRIS")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, metode)
        binding.actvMetodePembayaran.setAdapter(adapter)

        binding.actvMetodePembayaran.setOnItemClickListener { _, _, position, _ ->
            when (metode[position]) {
                "Transfer Bank" -> {
                    binding.layoutInfoBank.visibility = View.VISIBLE
                    binding.ivQris.visibility = View.GONE
                }
                "QRIS" -> {
                    binding.layoutInfoBank.visibility = View.GONE
                    binding.ivQris.visibility = View.VISIBLE
                }
                else -> { // Termasuk Tunai
                    binding.layoutInfoBank.visibility = View.GONE
                    binding.ivQris.visibility = View.GONE
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.cardUpload.setOnClickListener {
            showImageSourceDialog()
        }

        binding.btnUpdatePembayaran.setOnClickListener {
            val metode = binding.actvMetodePembayaran.text.toString()
            if (metode.isEmpty()) {
                Toast.makeText(this, "Silakan pilih metode pembayaran", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (metode != "Tunai" && selectedImageUri == null) {
                Toast.makeText(this, "Mohon unggah bukti pembayaran", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.updatePembayaran(pembayaranId, metode, selectedImageUri)
        }

        binding.btnKembali.setOnClickListener {
            // Jika batal, kembali ke Beranda adalah tindakan yang benar
            val intent = Intent(this, com.example.cleanwashlaundromat.ui.beranda.BerandaActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun observeViewModel() {
        viewModel.pembayaranDetails.observe(this) { details ->
            val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
            binding.etJumlahBayar.setText(formatRupiah.format(details.jumlahDibayar))
        }

        viewModel.updateResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()

                // PERUBAHAN: Pindah ke RiwayatActivity setelah pembayaran berhasil
                val intent = Intent(this, RiwayatActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()

            }.onFailure {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.btnUpdatePembayaran.isEnabled = !isLoading
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Kamera", "Galeri")
        AlertDialog.Builder(this)
            .setTitle("Pilih Sumber Gambar")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> checkCameraPermission() // Kamera
                    1 -> checkGalleryPermission() // Galeri
                }
                dialog.dismiss()
            }
            .show()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        val file = File(cacheDir, "camera_photo.jpg")
        cameraImageUri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", file)
        cameraImageUri?.let { uri ->
            cameraLauncher.launch(uri)
        }
    }

    private fun checkGalleryPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            galleryLauncher.launch("image/*")
        } else {
            requestGalleryPermissionLauncher.launch(permission)
        }
    }

    private fun showImagePreview() {
        binding.ivBuktiBayarPreview.visibility = View.VISIBLE
        binding.layoutPlaceholderUpload.visibility = View.GONE
    }
}
