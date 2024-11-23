package com.hayakai.data.remote.response

import com.google.gson.annotations.SerializedName

data class PreferencesResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("data")
    val data: Preferences?
)

data class Preferences(
    @SerializedName("voice_detection")
    val voiceDetection: Boolean,
    @SerializedName("dark_mode")
    val darkMode: Boolean,
    @SerializedName("location_tracking")
    val locationTracking: Boolean
)
