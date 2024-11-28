package com.hayakai.data.remote.response

import com.google.gson.annotations.SerializedName

data class GetUserPreferencesResponse(

    @field:SerializedName("data")
    val data: UserPreferencesData,

    @field:SerializedName("status")
    val status: String? = null
)

data class UserPreferencesData(

    @field:SerializedName("dark_mode")
    val darkMode: Boolean,

    @field:SerializedName("voice_detection")
    val voiceDetection: Boolean,

    @field:SerializedName("location_tracking")
    val locationTracking: Boolean
)
