package com.hayakai.data.remote.response

import com.google.gson.annotations.SerializedName

data class GetMyProfileResponse(

    @field:SerializedName("data")
    val data: ProfileData,

    @field:SerializedName("status")
    val status: String
)

data class ProfileData(

    @field:SerializedName("profile_photo")
    val profilePhoto: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("phone_number")
    val phoneNumber: String,

    @field:SerializedName("email")
    val email: String
)