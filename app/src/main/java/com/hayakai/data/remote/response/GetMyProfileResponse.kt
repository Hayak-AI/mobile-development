package com.hayakai.data.remote.response

import com.google.gson.annotations.SerializedName

data class GetMyProfileResponse(

    @field:SerializedName("data")
    val data: ProfileData? = null,

    @field:SerializedName("status")
    val status: String? = null
)

data class ProfileData(

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("phone_number")
    val phoneNumber: String? = null,

    @field:SerializedName("email")
    val email: String? = null
)