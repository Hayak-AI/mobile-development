package com.hayakai.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hayakai.data.pref.SessionModel
import com.hayakai.data.repository.AuthRepository
import com.hayakai.data.repository.ContactRepository
import kotlinx.coroutines.launch

class SessionViewModel(
    private val authRepository: AuthRepository,
    private val contactRepository: ContactRepository
) : ViewModel() {
    fun getSession() = authRepository.getSession()

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            contactRepository.clearLocalData()
        }
    }

    fun saveSession(sessionModel: SessionModel) {
        viewModelScope.launch {
            authRepository.saveSession(sessionModel)
        }
    }
}