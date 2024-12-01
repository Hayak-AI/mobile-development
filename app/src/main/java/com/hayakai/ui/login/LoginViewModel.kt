package com.hayakai.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hayakai.data.pref.SessionModel
import com.hayakai.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun saveSession(sessionModel: SessionModel) {
        viewModelScope.launch {
            authRepository.saveSession(sessionModel)
        }
    }

    fun login(email: String, password: String) = authRepository.login(email, password)
}