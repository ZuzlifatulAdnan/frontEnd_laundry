package com.example.cleanwashlaundromat.data.repository

import android.content.Context
import com.example.cleanwashlaundromat.data.model.DropOffOrderRequest
import com.example.cleanwashlaundromat.data.model.SelfServiceOrderRequest
import com.example.cleanwashlaundromat.data.remote.ApiClient

class OrderRepository(context: Context) {
    private val apiService = ApiClient.getInstance(context)

    suspend fun getMesinReady() = apiService.getMesinReady()

    suspend fun storeSelfService(request: SelfServiceOrderRequest) = apiService.storeSelfService(request)

    suspend fun storeDropOff(request: DropOffOrderRequest) = apiService.storeDropOff(request)
}