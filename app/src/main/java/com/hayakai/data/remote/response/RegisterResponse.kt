package com.hayakai.data.remote.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

    @field:SerializedName("status")
    val status: String? = null
)
