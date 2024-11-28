package com.hayakai.di

import android.content.Context
import com.hayakai.data.pref.UserPreference
import com.hayakai.data.pref.dataStore
import com.hayakai.data.remote.retrofit.ApiConfig
import com.hayakai.data.repository.AuthRepository
import com.hayakai.data.repository.SettingsRepository
import com.hayakai.data.repository.UserRepository

object Injection {
    fun provideAuthRepository(context: Context): AuthRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return AuthRepository.getInstance(userPreference, apiService)
    }

    fun provideUserRepository(context: Context): UserRepository {
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(apiService, userPreference)
    }

    fun provideSettingsRepository(context: Context): SettingsRepository {
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(context.dataStore)
        return SettingsRepository.getInstance(apiService, userPreference)
    }
}