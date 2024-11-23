package com.hayakai.data.remote.response

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String
)
