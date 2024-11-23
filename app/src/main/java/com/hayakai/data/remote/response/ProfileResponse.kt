package com.hayakai.data.remote.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("data")
    val data: UserProfile?
)

data class UserProfile(
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("phone_number")
    val phoneNumber: String?
)


