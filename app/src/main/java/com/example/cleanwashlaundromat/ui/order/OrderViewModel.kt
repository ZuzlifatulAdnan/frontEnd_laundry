package com.example.cleanwashlaundromat.ui.order

import android.app.Application
import androidx.lifecycle.*
import com.example.cleanwashlaundromat.data.model.DropOffOrderRequest
import com.example.cleanwashlaundromat.data.model.Mesin
import com.example.cleanwashlaundromat.data.model.OrderSubmissionResponse
import com.example.cleanwashlaundromat.data.model.SelfServiceOrderRequest
import com.example.cleanwashlaundromat.data.repository.OrderRepository
import kotlinx.coroutines.launch
import kotlin.math.ceil

class OrderViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = OrderRepository(application)

    private val _mesinList = MutableLiveData<List<Mesin>>()
    val mesinList: LiveData<List<Mesin>> = _mesinList

    private val _totalBiaya = MutableLiveData<Long>()
    val totalBiaya: LiveData<Long> = _totalBiaya

    private val _orderResult = MutableLiveData<Result<OrderSubmissionResponse>>()
    val orderResult: LiveData<Result<OrderSubmissionResponse>> = _orderResult

    fun fetchMesinReady() {
        viewModelScope.launch {
            try {
                val response = repository.getMesinReady()
                if (response.isSuccessful) {
                    _mesinList.postValue(response.body()?.mesinsReady)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun submitSelfServiceOrder(request: SelfServiceOrderRequest) {
        viewModelScope.launch {
            try {
                val response = repository.storeSelfService(request)
                if (response.isSuccessful && response.body() != null) {
                    _orderResult.postValue(Result.success(response.body()!!))
                } else {
                    _orderResult.postValue(Result.failure(Exception("Gagal membuat order: ${response.errorBody()?.string()}")))
                }
            } catch (e: Exception) {
                _orderResult.postValue(Result.failure(e))
            }
        }
    }

    fun submitDropOffOrder(request: DropOffOrderRequest) {
        viewModelScope.launch {
            try {
                val response = repository.storeDropOff(request)
                if (response.isSuccessful && response.body() != null) {
                    _orderResult.postValue(Result.success(response.body()!!))
                } else {
                    _orderResult.postValue(Result.failure(Exception("Gagal membuat order: ${response.errorBody()?.string()}")))
                }
            } catch (e: Exception) {
                _orderResult.postValue(Result.failure(e))
            }
        }
    }

    fun calculateSelfServiceTotal(jumlahKoin: Int) {
        val total = jumlahKoin * 12000L
        _totalBiaya.postValue(total)
    }

    fun calculateDropOffTotal(berat: Double, jumlahDetergen: Int) {
        val hargaBerat = if (berat > 0) ceil(berat / 7.0).toLong() * 31000L else 0L
        val hargaDetergen = jumlahDetergen * 1000L
        _totalBiaya.postValue(hargaBerat + hargaDetergen)
    }
}
