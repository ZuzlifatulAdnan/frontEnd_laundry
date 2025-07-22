package com.example.cleanwashlaundromat.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleanwashlaundromat.data.model.LoginRequest
import com.example.cleanwashlaundromat.data.model.LoginResponse
import com.example.cleanwashlaundromat.data.repository.AuthRepository
import kotlinx.coroutines.launch

// Kelas helper untuk merepresentasikan state UI
sealed class LoginResult {
    data class Success(val data: LoginResponse) : LoginResult()
    data class Error(val message: String) : LoginResult()
    object Loading : LoginResult()
}

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    // Berikan context ke repository
    private val repository = AuthRepository(application)

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    // Fungsi executeLogin tidak perlu diubah
    fun executeLogin(email: String, password: String) {
        _loginResult.value = LoginResult.Loading
        viewModelScope.launch {
            try {
                val request = LoginRequest(email, password)
                val response = repository.login(request)
                if (response.isSuccessful && response.body() != null) {
                    _loginResult.postValue(LoginResult.Success(response.body()!!))
                } else {
                    _loginResult.postValue(LoginResult.Error("Login Gagal: ${response.message()}"))
                }
            } catch (e: Exception) {
                _loginResult.postValue(LoginResult.Error("Terjadi Kesalahan: ${e.message}"))
            }
        }
    }
}