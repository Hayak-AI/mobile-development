package com.hayakai.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hayakai.data.repository.AuthRepository
import com.hayakai.data.repository.CommentRepository
import com.hayakai.data.repository.ContactRepository
import com.hayakai.data.repository.MapReportRepository
import com.hayakai.data.repository.SettingsRepository
import com.hayakai.data.repository.UserRepository
import com.hayakai.di.Injection
import com.hayakai.ui.common.SessionViewModel
import com.hayakai.ui.createaccount.RegisterViewModel
import com.hayakai.ui.detailcontact.DetailContactViewModel
import com.hayakai.ui.editprofile.EditProfileViewModel
import com.hayakai.ui.home.HomeViewModel
import com.hayakai.ui.login.LoginViewModel
import com.hayakai.ui.map.MapViewModel
import com.hayakai.ui.mapreportpost.MapReportPostFragmentViewModel
import com.hayakai.ui.newcontact.NewContactViewModel
import com.hayakai.ui.newmapreport.NewMapReportViewModel
import com.hayakai.ui.profile.ProfileViewModel
import com.hayakai.ui.settingsemailpassword.SettingsEmailPasswordViewModel

class ViewModelFactory(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository,
    private val contactRepository: ContactRepository,
    private val mapReportRepository: MapReportRepository,
    private val commentRepository: CommentRepository
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(authRepository) as T
            }

            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(userRepository, settingsRepository, contactRepository) as T
            }

            modelClass.isAssignableFrom(SessionViewModel::class.java) -> {
                SessionViewModel(authRepository, contactRepository) as T
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

            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(authRepository) as T
            }

            modelClass.isAssignableFrom(DetailContactViewModel::class.java) -> {
                DetailContactViewModel(contactRepository) as T
            }

            modelClass.isAssignableFrom(NewContactViewModel::class.java) -> {
                NewContactViewModel(contactRepository) as T
            }

            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                MapViewModel(mapReportRepository) as T
            }

            modelClass.isAssignableFrom(MapReportPostFragmentViewModel::class.java) -> {
                MapReportPostFragmentViewModel(mapReportRepository, commentRepository) as T
            }

            modelClass.isAssignableFrom(NewMapReportViewModel::class.java) -> {
                NewMapReportViewModel(mapReportRepository) as T
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
                    Injection.provideSettingsRepository(context),
                    Injection.provideContactRepository(context),
                    Injection.provideMapReportRepository(context),
                    Injection.provideCommentRepository(context)
                )
                INSTANCE = instance
                instance
            }
        }
    }
}