package com.hayakai.data.remote.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @field:SerializedName("data")
    val data: Data,

    @field:SerializedName("status")
    val status: String
)

data class Data(

    @field:SerializedName("access_token")
    val accessToken: String
)
