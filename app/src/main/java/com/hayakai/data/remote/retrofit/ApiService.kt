package com.hayakai.data.remote.retrofit

import com.hayakai.data.remote.dto.AddUserToEmergencyDto
import com.hayakai.data.remote.dto.DeleteCommentDto
import com.hayakai.data.remote.dto.DeleteContactDto
import com.hayakai.data.remote.dto.DeletePostDto
import com.hayakai.data.remote.dto.DeleteReportMapDto
import com.hayakai.data.remote.dto.ForgotPasswordDto
import com.hayakai.data.remote.dto.NewCommentReportDto
import com.hayakai.data.remote.dto.NewContactDto
import com.hayakai.data.remote.dto.NewPostCommentDto
import com.hayakai.data.remote.dto.NewPostDto
import com.hayakai.data.remote.dto.NewReportMapDto
import com.hayakai.data.remote.dto.ResetPasswordDto
import com.hayakai.data.remote.dto.UpdateContactDto
import com.hayakai.data.remote.dto.UpdateEmailPassDto
import com.hayakai.data.remote.dto.UpdatePostDto
import com.hayakai.data.remote.dto.UpdateProfileDto
import com.hayakai.data.remote.dto.UpdateUserPreferenceDto
import com.hayakai.data.remote.response.AddContactsResponse
import com.hayakai.data.remote.response.AddUserToEmergencyResponse
import com.hayakai.data.remote.response.CreatePostResponse
import com.hayakai.data.remote.response.DeleteCommentsResponse
import com.hayakai.data.remote.response.DeleteContactsResponse
import com.hayakai.data.remote.response.DeletePostResponse
import com.hayakai.data.remote.response.DeleteReportMapsResponse
import com.hayakai.data.remote.response.ForgotPasswordResponse
import com.hayakai.data.remote.response.GetAllPostResponse
import com.hayakai.data.remote.response.GetContactsResponse
import com.hayakai.data.remote.response.GetEmergencyResponse
import com.hayakai.data.remote.response.GetMyProfileResponse
import com.hayakai.data.remote.response.GetPostCommentsResponse
import com.hayakai.data.remote.response.GetPostResponse
import com.hayakai.data.remote.response.GetReportMapCommentsResponse
import com.hayakai.data.remote.response.GetReportMapsResponse
import com.hayakai.data.remote.response.GetUserPreferencesResponse
import com.hayakai.data.remote.response.LoginResponse
import com.hayakai.data.remote.response.NewCommentResponse
import com.hayakai.data.remote.response.NewsResponse
import com.hayakai.data.remote.response.RegisterResponse
import com.hayakai.data.remote.response.ReportMapsResponse
import com.hayakai.data.remote.response.ResetPasswordResponse
import com.hayakai.data.remote.response.UpdateContactsResponse
import com.hayakai.data.remote.response.UpdateEmailPassResponse
import com.hayakai.data.remote.response.UpdatePostResponse
import com.hayakai.data.remote.response.UpdateProfileResponse
import com.hayakai.data.remote.response.UpdateUserPreferenceResponse
import com.hayakai.data.remote.response.UploadEvidence
import com.hayakai.data.remote.response.UploadProfilePhotoResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
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

    @POST("forgot-password")
    suspend fun forgotPassword(
        @Body forgotPasswordDto: ForgotPasswordDto
    ): ForgotPasswordResponse

    @PUT("reset-password")
    suspend fun resetPassword(
        @Body resetPasswordDto: ResetPasswordDto
    ): ResetPasswordResponse

    @PUT("update-email-password")
    suspend fun updateEmailPassword(
        @Body updateEmailPassDto: UpdateEmailPassDto,
        @Header("Authorization") token: String
    ): UpdateEmailPassResponse

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

    @PUT("preferences")
    suspend fun updateUserPreference(
        @Body updateUserPreferenceDto: UpdateUserPreferenceDto,
        @Header("Authorization") token: String
    ): UpdateUserPreferenceResponse

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
        @Query("limit") limit: Int,
        @Query("skip") skip: Int,
        @Header("Authorization") token: String,
    ): GetReportMapCommentsResponse

    @HTTP(method = "DELETE", path = "/comments", hasBody = true)
    suspend fun deleteReportComment(
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

    @POST("/emergencies")
    suspend fun addUserToEmergency(
        @Body addUserToEmergencyDto: AddUserToEmergencyDto,
        @Header("Authorization") token: String
    ): AddUserToEmergencyResponse

    @GET("emergencies")
    suspend fun getEmergencies(
        @Header("Authorization") token: String
    ): GetEmergencyResponse

    @POST("posts")
    suspend fun newPost(
        @Body newPostDto: NewPostDto,
        @Header("Authorization") token: String
    ): CreatePostResponse

    @PUT("posts")
    suspend fun updatePost(
        @Body updatePostDto: UpdatePostDto,
        @Header("Authorization") token: String
    ): UpdatePostResponse

    @GET("posts")
    suspend fun getAllPosts(
        @Query("limit") limit: Int,
        @Query("skip") skip: Int,
        @Header("Authorization") token: String
    ): GetAllPostResponse

    @GET("posts")
    suspend fun getMyPosts(
        @Query("limit") limit: Int,
        @Query("skip") skip: Int,
        @Header("Authorization") token: String,
        @Query("from") from: String = "me"
    ): GetAllPostResponse

    @GET("posts")
    fun getPost(
        @Header("Authorization") token: String,
        @Path("post_id") postId: Int
    ): Call<GetPostResponse>

    @HTTP(method = "DELETE", path = "/posts", hasBody = true)
    suspend fun deletePost(
        @Body deletePostDto: DeletePostDto,
        @Header("Authorization") token: String
    ): DeletePostResponse

    @GET("post/{post_id}/comments")
    suspend fun getPostComments(
        @Path("post_id") reportId: Int,
        @Query("limit") limit: Int,
        @Query("skip") skip: Int,
        @Header("Authorization") token: String,
    ): GetPostCommentsResponse

    @HTTP(method = "DELETE", path = "/comments", hasBody = true)
    suspend fun deletePostComment(
        @Body deleteCommentDto: DeleteCommentDto,
        @Header("Authorization") token: String
    ): DeleteCommentsResponse

    @POST("comments")
    suspend fun newPostComment(
        @Body newPostCommentDto: NewPostCommentDto,
        @Header("Authorization") token: String
    ): NewCommentResponse

    @GET("news/{location}")
    suspend fun getNews(
        @Path("location") location: String,
        @Header("Authorization") token: String
    ): NewsResponse
}
