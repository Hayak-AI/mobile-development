package com.hayakai.data.repository

import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.hayakai.data.pref.UserModel
import com.hayakai.data.pref.UserPreference
import com.hayakai.data.remote.dto.UpdateEmailPassDto
import com.hayakai.data.remote.dto.UpdateProfileDto
import com.hayakai.data.remote.response.ErrorResponse
import com.hayakai.data.remote.retrofit.ApiService
import com.hayakai.utils.MyResult
import com.hayakai.utils.asJWT
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import retrofit2.HttpException

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    suspend fun saveUser(user: UserModel) {
        userPreference.saveUser(user)
    }

    fun getUser() = userPreference.getUser()


    fun getProfile() = liveData {
        emit(MyResult.Loading)
        try {
            val response = apiService.getProfile(userPreference.getSession().first().token.asJWT())
            response.data.let {
                saveUser(UserModel(it.name, it.email, it.phoneNumber, it.profilePhoto))
            }

        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(MyResult.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(MyResult.Error(e.message ?: "An error occurred"))
        } finally {
            emit(MyResult.Success(getUser().first()))
        }
    }

    fun updateProfile(updateProfileDto: UpdateProfileDto) = liveData {
        emit(MyResult.Loading)
        try {
            val response = apiService.updateProfile(
                updateProfileDto,
                userPreference.getSession().first().token.asJWT()
            )
            emit(MyResult.Success(response.status))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(MyResult.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(MyResult.Error(e.message ?: "An error occurred"))
        }
    }

    fun uploadProfilePhoto(photo: MultipartBody.Part) = liveData {
        emit(MyResult.Loading)
        try {
            val response = apiService.uploadProfilePhoto(
                photo,
                userPreference.getSession().first().token.asJWT()
            )
            emit(MyResult.Success(response.data))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(MyResult.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(MyResult.Error(e.message ?: "An error occurred"))
        }
    }

    fun updateEmailPass(updateEmailPassDto: UpdateEmailPassDto) = liveData {
        emit(MyResult.Loading)
        try {
            val response = apiService.updateEmailPassword(
                updateEmailPassDto,
                userPreference.getSession().first().token.asJWT()
            )
            userPreference.getUser().first().let {
                saveUser(UserModel(it.name, updateEmailPassDto.email, it.phone, it.image))
            }
            emit(MyResult.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(MyResult.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(MyResult.Error(e.message ?: "An error occurred"))
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserRepository? = null

        fun getInstance(apiService: ApiService, userPreference: UserPreference): UserRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = UserRepository(apiService, userPreference)
                INSTANCE = instance
                instance
            }
        }
    }
}