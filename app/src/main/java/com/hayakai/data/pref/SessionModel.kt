package com.hayakai.data.pref

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class SessionModel(
    val token: String
)

data class UserModel(
    val name: String,
    val email: String,
    val phone: String? = "",
    val image: String? = ""
)

@Parcelize
data class SettingsModel(
    val darkMode: Boolean,
    val voiceDetection: Boolean,
    val voiceSensitivity: String
) : Parcelable