package com.hayakai.data.remote.response

import com.google.gson.annotations.SerializedName

data class GetPostResponse(

    @field:SerializedName("data")
    val data: PostData? = null,  // Mengganti nama 'data' menjadi 'postData'

    @field:SerializedName("status")
    val status: String? = null
)

data class User(

    @field:SerializedName("profile_photo")
    val profilePhoto: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
)

// Mengganti nama 'Data' menjadi 'PostData' untuk menghindari redeclaration
data class PostData(

    @field:SerializedName("post_id")
    val postId: Int? = null,  // ID post

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,  // Waktu post terakhir diperbarui

    @field:SerializedName("created_at")
    val createdAt: String? = null,  // Waktu post pertama kali dibuat

    @field:SerializedName("location")
    val location: PostLocationData? = null,  // Mengganti 'location' menjadi 'postLocation'

    @field:SerializedName("title")
    val title: String? = null,  // Judul post

    @field:SerializedName("category")
    val category: String? = null,  // Kategori post

    @field:SerializedName("user")
    val user: User? = null,  // Pengguna yang membuat post

    @field:SerializedName("content")
    val content: String? = null  // Konten post
)

// Mengganti 'PostLocation' menjadi 'PostLocationData' untuk menghindari redeclaration
data class PostLocationData(

    @field:SerializedName("latitude")
    val latitude: String? = null,  // Latitude lokasi

    @field:SerializedName("name")
    val name: String? = null,  // Nama lokasi

    @field:SerializedName("longitude")
    val longitude: String? = null  // Longitude lokasi
)
