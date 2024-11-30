package com.hayakai.di

import android.content.Context
import com.hayakai.data.local.room.CommentReportRoomDatabase
import com.hayakai.data.local.room.ContactRoomDatabase
import com.hayakai.data.local.room.MapReportRoomDatabase
import com.hayakai.data.pref.UserPreference
import com.hayakai.data.pref.dataStore
import com.hayakai.data.remote.retrofit.ApiConfig
import com.hayakai.data.repository.AuthRepository
import com.hayakai.data.repository.CommentRepository
import com.hayakai.data.repository.ContactRepository
import com.hayakai.data.repository.MapReportRepository
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

    fun provideContactRepository(context: Context): ContactRepository {
        val contactDao = ContactRoomDatabase.getDatabase(context).contactDao()
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(context.dataStore)
        return ContactRepository.getInstance(contactDao, apiService, userPreference)
    }

    fun provideMapReportRepository(context: Context): MapReportRepository {
        val mapReportDao = MapReportRoomDatabase.getDatabase(context).mapReportDao()
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(context.dataStore)
        return MapReportRepository.getInstance(mapReportDao, apiService, userPreference)
    }

    fun provideCommentRepository(context: Context): CommentRepository {
        val commentReportDao = CommentReportRoomDatabase.getDatabase(context).commentReportDao()
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(context.dataStore)
        return CommentRepository.getInstance(commentReportDao, apiService, userPreference)
    }
}