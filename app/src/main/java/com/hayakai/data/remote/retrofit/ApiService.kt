package com.hayakai.data.remote.retrofit

import com.hayakai.data.remote.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Header("Authorization") token: String? = null
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("forgot-password")
    fun forgotPassword(
        @Field("email") email: String
    ): Call<ForgotPasswordResponse>

    @FormUrlEncoded
    @POST("reset-password")
    fun resetPassword(
        @Field("email") email: String,
        @Field("new_password") newPassword: String
    ): Call<ResetPasswordResponse>

    @Multipart
    @POST("users/upload-profile-photo")
    fun uploadProfilePhoto(
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody? = null,
        @Header("Authorization") token: String
    ): Call<UploadProfilePhotoResponse>

    @FormUrlEncoded
    @POST("users")
    fun updateProfile(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Header("Authorization") token: String
    ): Call<UpdateProfileResponse>

    @GET("users/me")
    fun getProfile(
        @Header("Authorization") token: String
    ): Call<GetMyProfileResponse>

    @GET("preferences")
    fun getUserPreferences(
        @Header("Authorization") token: String
    ): Call<GetUserPreferencesResponse>

    @FormUrlEncoded
    @POST("preferences")
    fun postUserPreferences(
        @Field("dark_mode") darkMode: Boolean,
        @Field("voice_detection") voiceDetection: Boolean,
        @Field("location_tracking") locationTracking: Boolean,
        @Header("Authorization") token: String
    ): Call<PostUserPreferencesResponse>

    @FormUrlEncoded
    @POST("/maps-report")
    fun reportMaps(
        @Field("map_id") mapId: String,
        @Field("description") description: String,
        @Header("Authorization") token: String
    ): Call<ReportMapsResponse>

    @GET("/maps-report")
    fun getReportedMaps(
        @Header("Authorization") token: String
    ): Call<GetReportMapsResponse>

    @DELETE("v")
    fun deleteReportMap(
        @Path("report_id") reportId: Int,
        @Header("Authorization") token: String
    ): Call<DeleteReportMapsResponse>

    @GET("contacts")
    fun getAllContacts(
        @Header("Authorization") token: String
    ): Call<GetContactsResponse>

    @FormUrlEncoded
    @POST("contacts")
    fun addContact(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("notify") notify: Boolean,
        @Field("message") message: String,
        @Header("Authorization") token: String
    ): Call<AddContactsResponse>

    @FormUrlEncoded
    @POST("/contacts")
    fun updateContact(
        @Path("contact_id") contactId: Int,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("notify") notify: Boolean,
        @Field("message") message: String,
        @Header("Authorization") token: String
    ): Call<UpdateContactsResponse>

    @DELETE("/contacts")
    fun deleteContact(
        @Path("contact_id") contactId: Int,
        @Header("Authorization") token: String
    ): Call<DeleteContactsResponse>

    @FormUrlEncoded
    @POST("/emergencies")
    fun addUserToEmergency(
        @Field("user_id") userId: Int,
        @Field("name") name: String,
        @Field("phone") phone: String,
        @Field("relationship") relationship: String,
        @Field("emergency_contact") emergencyContact: Boolean,
        @Header("Authorization") token: String
    ): Call<AddUserToEmergencyResponse>

    @GET("emergencies")
    fun getEmergencies(
        @Header("Authorization") token: String
    ): Call<GetEmergencyResponse>

    @FormUrlEncoded
    @POST("posts/create")
    fun createPost(
        @Field("title") title: String,
        @Field("content") content: String,
        @Field("author") author: String,
        @Header("Authorization") token: String
    ): Call<CreatePostResponse>

    @GET("posts")
    fun getAllPosts(
        @Header("Authorization") token: String
    ): Call<GetAllPostResponse>

    @GET("posts")
    fun getPost(
        @Header("Authorization") token: String,
        @Path("post_id") postId: Int
    ): Call<GetPostResponse>

    @FormUrlEncoded
    @PUT("posts")
    fun updatePost(
        @Path("post_id") postId: Int,
        @Field("title") title: String,
        @Field("content") content: String,
        @Field("category") category: String,
        @Header("Authorization") token: String
    ): Call<UpdatePostResponse>

    @DELETE("posts")
    fun deletePost(
        @Path("post_id") postId: Int,
        @Header("Authorization") token: String
    ): Call<DeletePostResponse>
}
