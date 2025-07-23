package com.example.cleanwashlaundromat.ui.riwayat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cleanwashlaundromat.data.model.OrderRiwayat
import com.example.cleanwashlaundromat.data.repository.RiwayatRepository
import kotlinx.coroutines.launch

class RiwayatViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RiwayatRepository(application)
    private val _riwayatList = MutableLiveData<List<OrderRiwayat>>()
    val riwayatList: LiveData<List<OrderRiwayat>> = _riwayatList
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    var currentStatus: String? = null
    var currentBulan: String? = null
    var currentTahun: String? = null
    var currentSort: String? = "desc"

    init {
        fetchRiwayat()
    }

    fun fetchRiwayat() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getRiwayat(currentStatus, currentBulan, currentTahun, currentSort)
                if (response.isSuccessful && response.body() != null) {
                    _riwayatList.postValue(response.body()!!.orders.data)
                } else {
                    _errorMessage.postValue("Gagal memuat riwayat: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Terjadi kesalahan: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}