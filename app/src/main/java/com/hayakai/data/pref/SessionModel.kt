package com.hayakai.data.pref

data class SessionModel(
    val token: String
)

data class UserModel(
    val name: String,
    val email: String,
    val phone: String? = "",
    val image: String? = ""
)

data class SettingsModel(
    val darkMode: Boolean,
    val voiceDetection: Boolean,
    val locationTracking: Boolean
)