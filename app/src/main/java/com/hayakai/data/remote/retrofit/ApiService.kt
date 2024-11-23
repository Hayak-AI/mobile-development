package com.hayakai.data.remote.retrofit

import LoginData
import LoginResponse
import com.hayakai.data.remote.response.ForgotPasswordResponse
import com.hayakai.data.remote.response.MapReportResponse
import com.hayakai.data.remote.response.PreferencesResponse
import com.hayakai.data.remote.response.ProfileResponse
import com.hayakai.data.remote.response.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    // Login Endpoint
    @POST("auth/login")
    suspend fun login(
        @Body loginRequest: LoginData
    ): LoginResponse

    // Register Endpoint
    @POST("auth/register")
    suspend fun register(
        @Body registerRequest: RegisterResponse
    ): RegisterResponse

    // Forgot Password Endpoint
    @POST("auth/forgot-password")
    suspend fun forgotPassword(
        @Body forgotPasswordRequest: ForgotPasswordResponse
    ): ForgotPasswordResponse

    // Get User Profile
    @GET("user/profile")
    suspend fun getProfile(): ProfileResponse

    // Get Preferences
    @GET("user/preferences")
    suspend fun getPreferences(): PreferencesResponse

    // Get Map Reports
    @GET("map/reports")
    suspend fun getMapReports(): MapReportResponse

    // Get Specific Map Report by ID
    @GET("map/reports/{id}")
    suspend fun getMapReportById(
        @Path("id") id: Int
    ): MapReportResponse
}
