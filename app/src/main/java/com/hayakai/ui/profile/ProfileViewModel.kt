package com.hayakai.ui.profile

import androidx.lifecycle.ViewModel
import com.hayakai.data.remote.dto.UpdateUserPreferenceDto
import com.hayakai.data.repository.SettingsRepository
import com.hayakai.data.repository.UserRepository

class ProfileViewModel(
    private val settingsRepository: SettingsRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    fun getSettings() = settingsRepository.getSettings()

    fun getProfile() = userRepository.getProfile()

    fun updateSettings(updateUserPreferenceDto: UpdateUserPreferenceDto) =
        settingsRepository.updateSettings(updateUserPreferenceDto)
}