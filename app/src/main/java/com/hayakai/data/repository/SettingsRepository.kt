package com.hayakai.data.repository

import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.hayakai.data.pref.SettingsModel
import com.hayakai.data.pref.UserPreference
import com.hayakai.data.remote.response.ErrorResponse
import com.hayakai.data.remote.retrofit.ApiService
import com.hayakai.utils.MyResult
import com.hayakai.utils.asJWT
import kotlinx.coroutines.flow.first
import retrofit2.HttpException

class SettingsRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    fun getSettings() = liveData {
        emit(MyResult.Loading)
        try {
            val response =
                apiService.getUserPreferences(userPreference.getSession().first().token.asJWT())
            response.data.let {
                userPreference.saveSettings(
                    SettingsModel(
                        it.darkMode,
                        it.voiceDetection,
                        it.locationTracking
                    )
                )
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(MyResult.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(MyResult.Error(e.message ?: "An error occurred"))
        } finally {
            emit(MyResult.Success(userPreference.getSettings().first()))
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SettingsRepository? = null

        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): SettingsRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingsRepository(apiService, userPreference)
                INSTANCE = instance
                instance
            }
        }
    }
}