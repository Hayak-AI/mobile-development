package com.hayakai.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.hayakai.data.pref.SessionModel
import com.hayakai.data.pref.UserPreference
import com.hayakai.data.remote.response.ErrorResponse
import com.hayakai.data.remote.retrofit.ApiService
import com.hayakai.utils.MyResult
import retrofit2.HttpException

class AuthRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {
    suspend fun saveSession(sessionModel: SessionModel) {
        userPreference.saveSession(sessionModel)
    }

    fun getSession(): LiveData<SessionModel> {
        return userPreference.getSession().asLiveData()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun login(email: String, password: String) = liveData {
        emit(MyResult.Loading)
        try {
            val response = apiService.login(email, password)
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
        private var INSTANCE: AuthRepository? = null

        fun getInstance(userPreference: UserPreference, apiService: ApiService): AuthRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = AuthRepository(userPreference, apiService)
                INSTANCE = instance
                instance
            }
        }
    }
}