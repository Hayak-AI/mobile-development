package com.hayakai.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hayakai.data.pref.SessionModel
import com.hayakai.data.repository.AuthRepository
import kotlinx.coroutines.launch

class SessionViewModel(private val authRepository: AuthRepository) : ViewModel() {
    fun getSession() = authRepository.getSession()

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun saveSession(sessionModel: SessionModel) {
        viewModelScope.launch {
            authRepository.saveSession(sessionModel)
        }
    }
}