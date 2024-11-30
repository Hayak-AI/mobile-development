package com.hayakai.data.remote.retrofit

import com.hayakai.data.remote.dto.DeleteCommentDto
import com.hayakai.data.remote.dto.DeleteContactDto
import com.hayakai.data.remote.dto.DeleteReportMapDto
import com.hayakai.data.remote.dto.NewCommentReportDto
import com.hayakai.data.remote.dto.NewContactDto
import com.hayakai.data.remote.dto.NewReportMapDto
import com.hayakai.data.remote.dto.UpdateContactDto
import com.hayakai.data.remote.dto.UpdateProfileDto
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
import com.hayakai.data.remote.response.GetReportMapCommentsResponse
import com.hayakai.data.remote.response.GetReportMapsResponse
import com.hayakai.data.remote.response.GetUserPreferencesResponse
import com.hayakai.data.remote.response.LoginResponse
import com.hayakai.data.remote.response.NewCommentResponse
import com.hayakai.data.remote.response.PostUserPreferencesResponse
import com.hayakai.data.remote.response.RegisterResponse
import com.hayakai.data.remote.response.ReportMapsResponse
import com.hayakai.data.remote.response.ResetPasswordResponse
import com.hayakai.data.remote.response.UpdateContactsResponse
import com.hayakai.data.remote.response.UpdatePostResponse
import com.hayakai.data.remote.response.UpdateProfileResponse
import com.hayakai.data.remote.response.UploadEvidence
import com.hayakai.data.remote.response.UploadProfilePhotoResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HTTP
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
    suspend fun uploadProfilePhoto(
        @Part file: MultipartBody.Part,
        @Header("Authorization") token: String
    ): UploadProfilePhotoResponse

    @PUT("users")
    suspend fun updateProfile(
        @Body updateProfileDto: UpdateProfileDto,
        @Header("Authorization") token: String
    ): UpdateProfileResponse

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

    @POST("/maps-report")
    suspend fun reportMaps(
        @Body newReportMapDto: NewReportMapDto,
        @Header("Authorization") token: String
    ): ReportMapsResponse

    @GET("/maps-report")
    suspend fun getReportedMaps(
        @Header("Authorization") token: String
    ): GetReportMapsResponse

    @HTTP(method = "DELETE", path = "/maps-report", hasBody = true)
    suspend fun deleteReportMap(
        @Body deleteReportMapDto: DeleteReportMapDto,
        @Header("Authorization") token: String
    ): DeleteReportMapsResponse

    @GET("report/{report_id}/comments")
    suspend fun getReportComments(
        @Path("report_id") reportId: Int,
        @Header("Authorization") token: String,
    ): GetReportMapCommentsResponse

    @HTTP(method = "DELETE", path = "/comments", hasBody = true)
    suspend fun deleteReportMap(
        @Body deleteCommentDto: DeleteCommentDto,
        @Header("Authorization") token: String
    ): DeleteCommentsResponse

    @POST("comments")
    suspend fun newCommentReport(
        @Body newCommentReportDto: NewCommentReportDto,
        @Header("Authorization") token: String
    ): NewCommentResponse

    @Multipart
    @POST("maps-report/upload-evidence")
    suspend fun uploadEvidence(
        @Part file: MultipartBody.Part,
        @Header("Authorization") token: String
    ): UploadEvidence

    @GET("contacts")
    suspend fun getAllContacts(
        @Header("Authorization") token: String
    ): GetContactsResponse

    @POST("contacts")
    suspend fun addContact(
        @Body newContactDto: NewContactDto,
        @Header("Authorization") token: String
    ): AddContactsResponse

    @PUT("/contacts")
    suspend fun updateContact(
        @Body updateContactDto: UpdateContactDto,
        @Header("Authorization") token: String
    ): UpdateContactsResponse

    @HTTP(method = "DELETE", path = "/contacts", hasBody = true)
    suspend fun deleteContact(
        @Body deleteContactDto: DeleteContactDto,
        @Header("Authorization") token: String
    ): DeleteContactsResponse

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
}
