package com.hayakai.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hayakai.data.repository.AuthRepository
import com.hayakai.data.repository.SettingsRepository
import com.hayakai.data.repository.UserRepository
import com.hayakai.di.Injection
import com.hayakai.ui.common.SessionViewModel
import com.hayakai.ui.editprofile.EditProfileViewModel
import com.hayakai.ui.home.HomeViewModel
import com.hayakai.ui.login.LoginViewModel
import com.hayakai.ui.profile.ProfileViewModel
import com.hayakai.ui.settingsemailpassword.SettingsEmailPasswordViewModel

class ViewModelFactory(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(authRepository) as T
            }

            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(userRepository, settingsRepository) as T
            }

            modelClass.isAssignableFrom(SessionViewModel::class.java) -> {
                SessionViewModel(authRepository) as T
            }

            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(settingsRepository, userRepository) as T
            }

            modelClass.isAssignableFrom(EditProfileViewModel::class.java) -> {
                EditProfileViewModel(userRepository) as T
            }

            modelClass.isAssignableFrom(SettingsEmailPasswordViewModel::class.java) -> {
                SettingsEmailPasswordViewModel(userRepository) as T
            }

            else -> throw Throwable("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                val instance = ViewModelFactory(
                    Injection.provideAuthRepository(context),
                    Injection.provideUserRepository(context),
                    Injection.provideSettingsRepository(context)
                )
                INSTANCE = instance
                instance
            }
        }
    }
}