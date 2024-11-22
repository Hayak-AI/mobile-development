package com.hayakai.di

import android.content.Context
import com.hayakai.data.pref.UserPreference
import com.hayakai.data.pref.dataStore
import com.hayakai.data.repository.AuthRepository

object Injection {
    fun provideAuthRepository(context: Context): AuthRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        return AuthRepository.getInstance(userPreference)
    }
}