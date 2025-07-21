package com.example.cleanwashlaundromat

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class RegisterActivity : AppCompatActivity() {

    private val PICK_IMAGE = 1
    private val TAKE_PHOTO = 2
    private var imageUri: Uri? = null
    private lateinit var imgFoto: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etNama = findViewById<EditText>(R.id.etNama)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etKonfirmasi = findViewById<EditText>(R.id.etKonfirmasi)
        val etHp = findViewById<EditText>(R.id.etHp)
        imgFoto = findViewById(R.id.imgFoto)
        val btnAmbilFoto = findViewById<Button>(R.id.btnAmbilFoto)
        val btnDaftar = findViewById<Button>(R.id.btnDaftar)

        btnAmbilFoto.setOnClickListener {
            val pilih = arrayOf("Dari Galeri", "Dari Kamera")
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Pilih Foto")
            builder.setItems(pilih) { _, which ->
                when (which) {
                    0 -> openGallery()
                    1 -> openCamera()
                }
            }
            builder.show()
        }

        btnDaftar.setOnClickListener {
            val nama = etNama.text.toString()
            val email = etEmail.text.toString()
            val pass = etPassword.text.toString()
            val konfir = etKonfirmasi.text.toString()
            val hp = etHp.text.toString()

            if (pass != konfir) {
                Toast.makeText(this, "Password tidak sama", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            uploadData(nama, email, pass, hp, imageUri)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, TAKE_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE -> {
                    imageUri = data?.data
                    imgFoto.setImageURI(imageUri)
                }
                TAKE_PHOTO -> {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    imageUri = getImageUri(bitmap)
                    imgFoto.setImageBitmap(bitmap)
                }
            }
        }
    }

    private fun getImageUri(bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }

    private fun uploadData(nama: String, email: String, pass: String, hp: String, fotoUri: Uri?) {
        val client = OkHttpClient()

        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("nama", nama)
            .addFormDataPart("email", email)
            .addFormDataPart("password", pass)
            .addFormDataPart("no_hp", hp)

        fotoUri?.let {
            val file = File(getRealPathFromURI(it))
            val reqFile = RequestBody.create(MediaType.parse("image/*"), file)
            builder.addFormDataPart("foto", file.name, reqFile)
        }

        val requestBody = builder.build()

        val request = Request.Builder()
            .url("http://10.0.2.2:8000/api/admin/register")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@RegisterActivity, "Berhasil Daftar", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@RegisterActivity, "Gagal: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun getRealPathFromURI(contentUri: Uri): String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(contentUri, proj, null, null, null)
        cursor?.moveToFirst()
        val idx = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        val path = cursor?.getString(idx ?: 0)
        cursor?.close()
        return path ?: ""
    }
}
