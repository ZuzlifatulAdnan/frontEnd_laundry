package com.example.cleanwashlaundromat.ui.beranda

import android.app.Application
import androidx.lifecycle.*
import com.example.cleanwashlaundromat.data.model.BerandaResponse
import com.example.cleanwashlaundromat.data.repository.BerandaRepository
import kotlinx.coroutines.launch

sealed class BerandaResult {
    data class Success(val data: BerandaResponse) : BerandaResult()
    data class Error(val message: String) : BerandaResult()
    object Loading : BerandaResult()
}

class BerandaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BerandaRepository(application)

    private val _berandaResult = MutableLiveData<BerandaResult>()
    val berandaResult: LiveData<BerandaResult> = _berandaResult

    fun fetchBerandaData() {
        _berandaResult.value = BerandaResult.Loading
        viewModelScope.launch {
            try {
                val response = repository.getBerandaData()
                if (response.isSuccessful && response.body() != null) {
                    _berandaResult.postValue(BerandaResult.Success(response.body()!!))
                } else {
                    _berandaResult.postValue(BerandaResult.Error("Gagal memuat data: ${response.message()}"))
                }
            } catch (e: Exception) {
                _berandaResult.postValue(BerandaResult.Error("Terjadi kesalahan: ${e.message}"))
            }
        }
    }
}
