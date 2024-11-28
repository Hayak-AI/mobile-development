package com.hayakai.ui.editprofile

import androidx.lifecycle.ViewModel
import com.hayakai.data.repository.UserRepository

class EditProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    fun getProfile() = userRepository.getProfile()

}