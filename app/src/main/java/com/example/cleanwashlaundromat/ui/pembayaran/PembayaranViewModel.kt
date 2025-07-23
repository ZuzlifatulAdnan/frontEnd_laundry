package com.example.cleanwashlaundromat.ui.pembayaran

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cleanwashlaundromat.data.model.PembayaranDetail
import com.example.cleanwashlaundromat.data.model.PembayaranUpdateResponse
import com.example.cleanwashlaundromat.data.repository.PembayaranRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class PembayaranViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PembayaranRepository(application)

    private val _pembayaranDetails = MutableLiveData<PembayaranDetail>()
    val pembayaranDetails: LiveData<PembayaranDetail> = _pembayaranDetails

    private val _updateResult = MutableLiveData<Result<PembayaranUpdateResponse>>()
    val updateResult: LiveData<Result<PembayaranUpdateResponse>> = _updateResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchPembayaranDetails(pembayaranId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getPembayaranDetails(pembayaranId)
                if (response.isSuccessful && response.body() != null) {
                    _pembayaranDetails.postValue(response.body()!!.pembayaran)
                } else {
                    _errorMessage.postValue("Gagal memuat detail pembayaran: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Terjadi kesalahan: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updatePembayaran(pembayaranId: Int, metode: String, imageUri: Uri?) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val metodeBody = metode.toRequestBody("text/plain".toMediaTypeOrNull())

                var buktiBayarPart: MultipartBody.Part? = null
                if (imageUri != null) {
                    // PERBAIKAN: Kompres gambar sebelum diunggah
                    val compressedFile = compressImage(imageUri)
                    val requestFile = compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    buktiBayarPart = MultipartBody.Part.createFormData("bukti_bayar", compressedFile.name, requestFile)
                }

                val response = repository.updatePembayaran(pembayaranId, metodeBody, buktiBayarPart)
                if (response.isSuccessful && response.body() != null) {
                    _updateResult.postValue(Result.success(response.body()!!))
                } else {
                    _updateResult.postValue(Result.failure(Exception("Gagal update: ${response.errorBody()?.string()}")))
                }

            } catch (e: Exception) {
                _updateResult.postValue(Result.failure(e))
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Fungsi baru untuk mengompres gambar dari URI.
     */
    private fun compressImage(uri: Uri): File {
        val application = getApplication<Application>()
        val inputStream = application.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)

        // Tentukan lokasi file hasil kompresi di cache
        val outputDir = application.cacheDir
        val outputFile = File(outputDir, "compressed_bukti_bayar.jpg")

        // Lakukan kompresi
        FileOutputStream(outputFile).use { out ->
            // Kompres ke format JPEG dengan kualitas 80%
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
        }
        return outputFile
    }
}
