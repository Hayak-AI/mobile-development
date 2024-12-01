package com.hayakai.data.remote.response

import com.google.gson.annotations.SerializedName

data class GetAllPostResponse(

    @field:SerializedName("data")
    val data: List<PostItem>, // Mengganti nama 'data' menjadi 'postList'

    @field:SerializedName("status")
    val status: String? = null // Mengganti 'status' menjadi 'responseStatus'
)

data class PostLocation(

    @field:SerializedName("latitude")
    val latitude: Double,

    @field:SerializedName("name")
    val locationName: String, // Mengganti 'name' menjadi 'locationName'

    @field:SerializedName("longitude")
    val longitude: Double
)

data class PostUser(

    @field:SerializedName("profile_photo")
    val image: String? = null, // Mengganti 'profile_photo' menjadi 'profilePhotoUrl'

    @field:SerializedName("name")
    val name: String, // Mengganti 'name' menjadi 'userName'

    @field:SerializedName("id")
    val id: Int // Mengganti 'id' menjadi 'userId'
)

data class PostItem(

    @field:SerializedName("post_id")
    val id: Int, // ID unik dari post

    @field:SerializedName("updated_at")
    val updatedAt: String, // Waktu post terakhir diperbarui

    @field:SerializedName("created_at")
    val createdAt: String, // Waktu post pertama kali dibuat

    @field:SerializedName("location")
    val location: PostLocation? = null, // Mengganti 'location' menjadi 'postLocation'

    @field:SerializedName("title")
    val title: String, // Mengganti 'title' menjadi 'postTitle'

    @field:SerializedName("category")
    val category: String, // Mengganti 'category' menjadi 'postCategory'

    @field:SerializedName("user")
    val user: PostUser, // Mengganti 'user' menjadi 'postUser'

    @field:SerializedName("content")
    val content: String, // Mengganti 'content' menjadi 'postContent'

    @field:SerializedName("by_me")
    val byMe: Boolean
)
