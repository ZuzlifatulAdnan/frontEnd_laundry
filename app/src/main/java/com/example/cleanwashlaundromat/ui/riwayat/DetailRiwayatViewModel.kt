package com.example.cleanwashlaundromat.ui.riwayat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cleanwashlaundromat.data.model.OrderRiwayatDetail
import com.example.cleanwashlaundromat.data.repository.DetailRiwayatRepository
import kotlinx.coroutines.launch

class DetailRiwayatViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DetailRiwayatRepository(application)
    private val _detailOrder = MutableLiveData<OrderRiwayatDetail>()
    val detailOrder: LiveData<OrderRiwayatDetail> = _detailOrder
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchDetailRiwayat(orderId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getDetailRiwayat(orderId)
                if (response.isSuccessful && response.body() != null) {
                    _detailOrder.postValue(response.body()!!.order)
                } else {
                    _errorMessage.postValue("Gagal memuat detail: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Terjadi kesalahan: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
