package com.hayakai.data.repository

import com.google.gson.Gson
import com.hayakai.data.pref.UserPreference
import com.hayakai.data.remote.dto.AddUserToEmergencyDto
import com.hayakai.data.remote.response.ErrorResponse
import com.hayakai.data.remote.retrofit.ApiService
import com.hayakai.utils.MyResult
import com.hayakai.utils.asJWT
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

class EmergencyRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    fun addUserToEmergency(addUserToEmergencyDto: AddUserToEmergencyDto) =
        flow {
            emit(MyResult.Loading)
            try {
                val response = apiService.addUserToEmergency(
                    addUserToEmergencyDto,
                    userPreference.getSession().first().token.asJWT()
                )
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
        private var INSTANCE: EmergencyRepository? = null

        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): EmergencyRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = EmergencyRepository(apiService, userPreference)
                INSTANCE = instance
                instance
            }
        }
    }


}