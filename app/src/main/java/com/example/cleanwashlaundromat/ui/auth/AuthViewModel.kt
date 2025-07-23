package com.example.cleanwashlaundromat.ui.auth

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cleanwashlaundromat.data.model.*
import com.example.cleanwashlaundromat.data.repository.AuthRepository
import kotlinx.coroutines.launch
import com.example.cleanwashlaundromat.utils.ImageCompressor // Pastikan Anda punya file ini
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

// Sealed class untuk merepresentasikan state UI
sealed class AuthResult<T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error<T>(val message: String) : AuthResult<T>()
    class Loading<T> : AuthResult<T>()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application)

    private val _registerResult = MutableLiveData<AuthResult<RegisterResponse>>()
    val registerResult: LiveData<AuthResult<RegisterResponse>> = _registerResult

    private val _forgotPasswordResult = MutableLiveData<AuthResult<ForgotPasswordResponse>>()
    val forgotPasswordResult: LiveData<AuthResult<ForgotPasswordResponse>> = _forgotPasswordResult

    private val _resetPasswordResult = MutableLiveData<AuthResult<ResetPasswordResponse>>()
    val resetPasswordResult: LiveData<AuthResult<ResetPasswordResponse>> = _resetPasswordResult

    fun registerUser(request: RegisterRequest, imageUri: Uri?) {
        _registerResult.value = AuthResult.Loading()
        viewModelScope.launch {
            try {
                // Konversi semua data teks ke RequestBody
                val nameBody = request.name.toRequestBody("text/plain".toMediaTypeOrNull())
                val emailBody = request.email.toRequestBody("text/plain".toMediaTypeOrNull())
                val noHandphoneBody = request.noHandphone.toRequestBody("text/plain".toMediaTypeOrNull())
                val passwordBody = request.password.toRequestBody("text/plain".toMediaTypeOrNull())
                val passwordConfirmationBody = request.passwordConfirmation.toRequestBody("text/plain".toMediaTypeOrNull())

                // Konversi gambar ke MultipartBody.Part jika ada
                var imagePart: MultipartBody.Part? = null
                if (imageUri != null) {
                    val compressedFile = ImageCompressor.compress(getApplication(), imageUri)
                    val requestFile = compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    imagePart = MultipartBody.Part.createFormData("image", compressedFile.name, requestFile)
                }

                val response = repository.register(nameBody, emailBody, noHandphoneBody, passwordBody, passwordConfirmationBody, imagePart)

                if (response.isSuccessful && response.body() != null) {
                    _registerResult.postValue(AuthResult.Success(response.body()!!))
                } else {
                    _registerResult.postValue(AuthResult.Error("Registrasi Gagal: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                _registerResult.postValue(AuthResult.Error("Terjadi Kesalahan: ${e.message}"))
            }
        }
    }

    fun sendForgotPasswordLink(request: ForgotPasswordRequest) {
        _forgotPasswordResult.value = AuthResult.Loading()
        viewModelScope.launch {
            try {
                val response = repository.forgotPassword(request)
                if (response.isSuccessful && response.body() != null) {
                    _forgotPasswordResult.postValue(AuthResult.Success(response.body()!!))
                } else {
                    _forgotPasswordResult.postValue(AuthResult.Error("Gagal: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                _forgotPasswordResult.postValue(AuthResult.Error("Terjadi Kesalahan: ${e.message}"))
            }
        }
    }

    fun resetPassword(request: ResetPasswordRequest) {
        _resetPasswordResult.value = AuthResult.Loading()
        viewModelScope.launch {
            try {
                val response = repository.resetPassword(request)
                if (response.isSuccessful && response.body() != null) {
                    _resetPasswordResult.postValue(AuthResult.Success(response.body()!!))
                } else {
                    _resetPasswordResult.postValue(AuthResult.Error("Gagal: ${response.errorBody()?.string()}"))
                }
            } catch (e: Exception) {
                _resetPasswordResult.postValue(AuthResult.Error("Terjadi Kesalahan: ${e.message}"))
            }
        }
    }
}
