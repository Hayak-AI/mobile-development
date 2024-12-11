package com.hayakai.ui.forgotpassword

import androidx.lifecycle.ViewModel
import com.hayakai.data.remote.dto.ForgotPasswordDto
import com.hayakai.data.remote.dto.ResetPasswordDto
import com.hayakai.data.repository.AuthRepository

class ForgotPasswordViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun forgotPassword(forgotPasswordDto: ForgotPasswordDto) =
        authRepository.forgotPassword(forgotPasswordDto)

    fun resetPassword(resetPasswordDto: ResetPasswordDto) =
        authRepository.resetPassword(resetPasswordDto)
}