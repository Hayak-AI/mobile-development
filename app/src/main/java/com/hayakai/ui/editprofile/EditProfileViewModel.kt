package com.hayakai.ui.editprofile

import androidx.lifecycle.ViewModel
import com.hayakai.data.remote.dto.UpdateProfileDto
import com.hayakai.data.repository.UserRepository
import okhttp3.MultipartBody

class EditProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    fun getProfile() = userRepository.getProfile()

    fun updateProfile(updateProfileDto: UpdateProfileDto) =
        userRepository.updateProfile(updateProfileDto)

    fun uploadProfilePhoto(photo: MultipartBody.Part) = userRepository.uploadProfilePhoto(photo)
}