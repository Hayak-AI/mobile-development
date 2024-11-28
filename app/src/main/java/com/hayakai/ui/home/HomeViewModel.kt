package com.hayakai.ui.home

import androidx.lifecycle.ViewModel
import com.hayakai.data.repository.SettingsRepository
import com.hayakai.data.repository.UserRepository

class HomeViewModel(
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    fun getProfile() = userRepository.getProfile()

    fun getSettings() = settingsRepository.getSettings()
}