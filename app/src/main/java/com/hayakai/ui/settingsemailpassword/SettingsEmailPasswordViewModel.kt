package com.hayakai.ui.settingsemailpassword

import androidx.lifecycle.ViewModel
import com.hayakai.data.repository.UserRepository

class SettingsEmailPasswordViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun getProfile() = userRepository.getProfile()
}