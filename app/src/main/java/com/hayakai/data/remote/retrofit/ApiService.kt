package com.hayakai.data.remote.retrofit

import com.hayakai.data.remote.response.AddContactsResponse
import com.hayakai.data.remote.response.AddUserToEmergencyResponse
import com.hayakai.data.remote.response.CommunityCommentsForPostResponse
import com.hayakai.data.remote.response.CommunityCommentsForRepostResponse
import com.hayakai.data.remote.response.CreatePostResponse
import com.hayakai.data.remote.response.DeleteCommentsResponse
import com.hayakai.data.remote.response.DeleteContactsResponse
import com.hayakai.data.remote.response.DeletePostResponse
import com.hayakai.data.remote.response.DeleteReportMapsResponse
import com.hayakai.data.remote.response.ForgotPasswordResponse
import com.hayakai.data.remote.response.GetAllCommentsPostResponse
import com.hayakai.data.remote.response.GetAllCommentsRepostResponse
import com.hayakai.data.remote.response.GetAllPostResponse
import com.hayakai.data.remote.response.GetContactsResponse
import com.hayakai.data.remote.response.GetEmergencyResponse
import com.hayakai.data.remote.response.GetMyProfileResponse
import com.hayakai.data.remote.response.GetPostResponse
import com.hayakai.data.remote.response.GetReportMapsResponse
import com.hayakai.data.remote.response.GetUserPreferencesResponse
import com.hayakai.data.remote.response.LoginResponse
import com.hayakai.data.remote.response.PostUserPreferencesResponse
import com.hayakai.data.remote.response.RegisterResponse
import com.hayakai.data.remote.response.ReportMapsResponse
import com.hayakai.data.remote.response.ResetPasswordResponse
import com.hayakai.data.remote.response.UpdateContactsResponse
import com.hayakai.data.remote.response.UpdatePostResponse
import com.hayakai.data.remote.response.UpdateProfileResponse
import com.hayakai.data.remote.response.UploadProfilePhotoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

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
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): GetMyProfileResponse

    @GET("preferences")
    suspend fun getUserPreferences(
        @Header("Authorization") token: String
    ): GetUserPreferencesResponse

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

    // Endpoint untuk mendapatkan komentar komunitas berdasarkan ID Post
    @GET("community/posts/comments")
    fun getCommunityCommentsForPost(
        @Header("Authorization") token: String,
        @Query("post_id") postId: Int
    ): Call<CommunityCommentsForPostResponse>

    // Endpoint untuk mendapatkan komentar komunitas berdasarkan ID Report
    @GET("community/reports/comments")
    fun getCommunityCommentsForRepost(
        @Header("Authorization") token: String,
        @Query("report_id") reportId: Int
    ): Call<CommunityCommentsForRepostResponse>

    // Endpoint untuk mendapatkan semua komentar pada post
    @GET("community/posts/comments")
    fun getAllCommentsForPost(
        @Header("Authorization") token: String,
        @Query("post_id") postId: Int
    ): Call<GetAllCommentsPostResponse>

    // Endpoint untuk mendapatkan semua komentar pada repost
    @GET("community/reports/comments")
    fun getAllCommentsForRepost(
        @Header("Authorization") token: String,
        @Query("report_id") reportId: Int
    ): Call<GetAllCommentsRepostResponse>

    // Endpoint untuk menghapus komentar berdasarkan ID Comment
    @DELETE("comments/{comment_id}")
    fun deleteComment(
        @Header("Authorization") token: String,
        @Path("comment_id") commentId: Int
    ): Call<DeleteCommentsResponse>
}
