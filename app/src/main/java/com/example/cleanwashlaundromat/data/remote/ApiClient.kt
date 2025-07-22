package com.example.cleanwashlaundromat.data.remote

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor // Import baru
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // Pastikan IP ini sudah benar untuk perangkat Anda (HP fisik)
    private const val BASE_URL = "http://192.168.1.9:8000/"

    fun getInstance(context: Context): ApiService {
        // 1. Buat Logging Interceptor
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Tampilkan semua detail: header dan body
        }

        // 2. Buat OkHttpClient dan tambahkan kedua interceptor
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .addInterceptor(loggingInterceptor) // Tambahkan logging interceptor
            .build()

        // 3. Buat Retrofit dengan OkHttpClient yang baru
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Gunakan client yang sudah ada logger-nya
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
