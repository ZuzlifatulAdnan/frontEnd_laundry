package com.example.cleanwashlaundromat.data.remote

import android.content.Context
import com.example.cleanwashlaundromat.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context) : Interceptor {
    private val sessionManager = SessionManager(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // PERBAIKAN: Tambahkan header ini untuk memastikan server selalu mengembalikan JSON
        requestBuilder.addHeader("Accept", "application/json")

        // Tambahkan token otorisasi jika ada
        sessionManager.fetchAuthToken()?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        return chain.proceed(requestBuilder.build())
    }
}