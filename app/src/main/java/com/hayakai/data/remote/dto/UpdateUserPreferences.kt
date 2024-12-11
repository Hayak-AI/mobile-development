package com.hayakai.data.remote.dto

class UpdateUserPreferenceDto(
    var dark_mode: Boolean? = null,
    var voice_detection: Boolean? = null,
    var voice_sensitivity: String? = null,
)