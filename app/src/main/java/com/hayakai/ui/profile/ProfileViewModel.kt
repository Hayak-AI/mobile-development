package com.hayakai.ui.profile

import androidx.lifecycle.ViewModel
import com.hayakai.data.repository.SettingsRepository
import com.hayakai.data.repository.UserRepository

class ProfileViewModel(
    private val settingsRepository: SettingsRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    fun getSettings() = settingsRepository.getSettings()

    fun getProfile() = userRepository.getProfile()
}