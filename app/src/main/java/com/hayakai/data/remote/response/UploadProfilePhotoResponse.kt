package com.hayakai.data.remote.response

import com.google.gson.annotations.SerializedName

data class UploadProfilePhotoResponse(

    @field:SerializedName("data")
    val data: ImageData,

    @field:SerializedName("status")
    val status: String? = null
)

data class ImageData(

    @field:SerializedName("image_url")
    val imageUrl: String
)
