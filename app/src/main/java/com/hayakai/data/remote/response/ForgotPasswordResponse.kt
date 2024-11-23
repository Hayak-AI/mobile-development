package com.hayakai.data.remote.response

import com.google.gson.annotations.SerializedName

data class ForgotPasswordResponse(
    @SerializedName("status")
    val status: String
)
