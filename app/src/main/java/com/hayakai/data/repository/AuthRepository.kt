package com.hayakai.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.hayakai.data.pref.UserModel
import com.hayakai.data.pref.UserPreference

class AuthRepository private constructor(
    private val userPreference: UserPreference
) {
    suspend fun saveSession(userModel: UserModel) {
        userPreference.saveSession(userModel)
    }

    fun getSession(): LiveData<UserModel> {
        return userPreference.getSession().asLiveData()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthRepository? = null

        fun getInstance(userPreference: UserPreference): AuthRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = AuthRepository(userPreference)
                INSTANCE = instance
                instance
            }
        }
    }
}