package com.example.cleanwashlaundromat.ui.akun

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cleanwashlaundromat.data.model.ChangePasswordRequest
import com.example.cleanwashlaundromat.data.model.ChangePasswordResponse
import com.example.cleanwashlaundromat.data.model.UpdateProfileResponse
import com.example.cleanwashlaundromat.data.model.User
import com.example.cleanwashlaundromat.data.repository.ProfileRepository
import com.example.cleanwashlaundromat.utils.ImageCompressor
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AkunViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProfileRepository(application)

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _updateResult = MutableLiveData<Result<UpdateProfileResponse>>()
    val updateResult: LiveData<Result<UpdateProfileResponse>> = _updateResult

    // LiveData baru untuk hasil ganti password
    private val _changePasswordResult = MutableLiveData<Result<ChangePasswordResponse>>()
    val changePasswordResult: LiveData<Result<ChangePasswordResponse>> = _changePasswordResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchProfile() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getProfile()
                if (response.isSuccessful && response.body() != null) {
                    _user.postValue(response.body()!!.user)
                } else {
                    _errorMessage.postValue("Gagal memuat profil: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Terjadi kesalahan: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(name: String, email: String, noHandphone: String, imageUri: Uri?) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
                val emailBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
                val noHandphoneBody = noHandphone.toRequestBody("text/plain".toMediaTypeOrNull())

                var imagePart: MultipartBody.Part? = null
                if (imageUri != null) {
                    val compressedFile = ImageCompressor.compress(getApplication(), imageUri)
                    val requestFile = compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    imagePart = MultipartBody.Part.createFormData("image", compressedFile.name, requestFile)
                }

                val response = repository.updateProfile(nameBody, emailBody, noHandphoneBody, imagePart)
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

    // Fungsi baru untuk mengganti password
    fun changePassword(request: ChangePasswordRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.changePassword(request)
                if (response.isSuccessful && response.body() != null) {
                    _changePasswordResult.postValue(Result.success(response.body()!!))
                } else {
                    _changePasswordResult.postValue(Result.failure(Exception("Gagal: ${response.errorBody()?.string()}")))
                }
            } catch (e: Exception) {
                _changePasswordResult.postValue(Result.failure(e))
            } finally {
                _isLoading.value = false
            }
        }
    }
}