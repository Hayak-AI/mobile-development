package com.hayakai.data.remote.response

import com.google.gson.annotations.SerializedName

data class UserProfileResponse(

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("image_url")
    val imageUrl: String? = null,

    @SerializedName("phone_number")
    val phoneNumber: String? = null
)