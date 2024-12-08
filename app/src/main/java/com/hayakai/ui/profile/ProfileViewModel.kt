package com.hayakai.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hayakai.data.pref.SettingsModel
import com.hayakai.data.pref.UserModel
import com.hayakai.data.remote.dto.UpdateUserPreferenceDto
import com.hayakai.data.repository.SettingsRepository
import com.hayakai.data.repository.UserRepository
import com.hayakai.utils.MyResult

class ProfileViewModel(
    private val settingsRepository: SettingsRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    lateinit var settingsData: LiveData<MyResult<SettingsModel>>
    lateinit var profileData: LiveData<MyResult<UserModel>>

    fun getSettings(): LiveData<MyResult<SettingsModel>> {
        settingsData = settingsRepository.getSettings()
        return settingsData
    }

    fun getProfile(): LiveData<MyResult<UserModel>> {
        profileData = userRepository.getProfile()
        return profileData
    }

    fun updateSettings(updateUserPreferenceDto: UpdateUserPreferenceDto) =
        settingsRepository.updateSettings(updateUserPreferenceDto)
}