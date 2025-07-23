package com.example.cleanwashlaundromat.data.remote

import com.example.cleanwashlaundromat.data.model.BerandaResponse
import com.example.cleanwashlaundromat.data.model.ChangePasswordRequest
import com.example.cleanwashlaundromat.data.model.ChangePasswordResponse
import com.example.cleanwashlaundromat.data.model.DetailRiwayatResponse
import com.example.cleanwashlaundromat.data.model.DropOffOrderRequest
import com.example.cleanwashlaundromat.data.model.ForgotPasswordRequest
import com.example.cleanwashlaundromat.data.model.ForgotPasswordResponse
import com.example.cleanwashlaundromat.data.model.LoginRequest
import com.example.cleanwashlaundromat.data.model.LoginResponse
import com.example.cleanwashlaundromat.data.model.MesinReadyResponse
import com.example.cleanwashlaundromat.data.model.OrderSubmissionResponse
import com.example.cleanwashlaundromat.data.model.PembayaranDetailResponse
import com.example.cleanwashlaundromat.data.model.PembayaranUpdateResponse
import com.example.cleanwashlaundromat.data.model.ProfileResponse
import com.example.cleanwashlaundromat.data.model.RegisterRequest
import com.example.cleanwashlaundromat.data.model.RegisterResponse
import com.example.cleanwashlaundromat.data.model.ResetPasswordRequest
import com.example.cleanwashlaundromat.data.model.ResetPasswordResponse
import com.example.cleanwashlaundromat.data.model.RiwayatResponse
import com.example.cleanwashlaundromat.data.model.SelfServiceOrderRequest
import com.example.cleanwashlaundromat.data.model.UpdateProfileResponse
import retrofit2.http.Body
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("api/admin/login")
    suspend fun loginAdmin(@Body request: LoginRequest): Response<LoginResponse>

    @Multipart
    @POST("api/admin/register")
    suspend fun register(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("no_handphone") noHandphone: RequestBody,
        @Part("password") password: RequestBody,
        @Part("password_confirmation") passwordConfirmation: RequestBody,
        @Part image: MultipartBody.Part? // Untuk file gambar
    ): Response<RegisterResponse>

    @POST("api/admin/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ForgotPasswordResponse>

    @POST("api/admin/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ResetPasswordResponse>

    // Ganti endpoint dan response type
    @GET("api/admin/beranda") // Asumsi endpoint baru adalah /api/beranda
    suspend fun getBerandaData(): Response<BerandaResponse>

    @GET("api/admin/order/mesin-ready")
    suspend fun getMesinReady(): Response<MesinReadyResponse>

    @POST("api/admin/order/selfservice")
    suspend fun storeSelfService(@Body request: SelfServiceOrderRequest): Response<OrderSubmissionResponse>

    @POST("api/admin/order/dropoff")
    suspend fun storeDropOff(@Body request: DropOffOrderRequest): Response<OrderSubmissionResponse>

    @GET("api/admin/pembayaran/{id}")
    suspend fun getPembayaranDetails(@Path("id") pembayaranId: Int): Response<PembayaranDetailResponse>

    @Multipart
    @POST("api/admin/pembayaran/{id}")
    suspend fun updatePembayaran(
        @Path("id") pembayaranId: Int,
        @Part("metode_pembayaran") metodePembayaran: RequestBody,
        @Part buktiBayar: MultipartBody.Part? // Nullable jika bukti bayar tidak wajib
    ): Response<PembayaranUpdateResponse>
    @GET("api/admin/riwayat")
    suspend fun getRiwayat(
        @Query("status") status: String? = null,
        @Query("bulan") bulan: String? = null,
        @Query("tahun") tahun: String? = null,
        @Query("sort") sort: String? = "desc"
    ): Response<RiwayatResponse>

    @GET("api/admin/riwayat/{id}")
    suspend fun getDetailRiwayat(@Path("id") orderId: Int): Response<DetailRiwayatResponse>
    @GET("api/admin/profile")
    suspend fun getProfile(): Response<ProfileResponse>

    @Multipart
    @POST("api/admin/profile/update")
    suspend fun updateProfile(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("no_handphone") noHandphone: RequestBody,
        @Part image: MultipartBody.Part? // Nullable jika gambar tidak diubah
    ): Response<UpdateProfileResponse>

    @POST("api/admin/profile/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ChangePasswordResponse>
}
