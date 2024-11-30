package com.hayakai.data.remote.response

import com.google.gson.annotations.SerializedName

data class UploadEvidence(

    @field:SerializedName("data")
    val data: ImageDataEvidence,

    @field:SerializedName("status")
    val status: String? = null
)

data class ImageDataEvidence(

    @field:SerializedName("image_url")
    val imageUrl: String
)
