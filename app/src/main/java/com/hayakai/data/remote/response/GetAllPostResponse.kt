package com.hayakai.data.remote.response

import com.google.gson.annotations.SerializedName

data class GetAllPostResponse(

    @field:SerializedName("data")
    val postList: List<PostItem?>? = null, // Mengganti nama 'data' menjadi 'postList'

    @field:SerializedName("status")
    val responseStatus: String? = null // Mengganti 'status' menjadi 'responseStatus'
)

data class PostLocation(

    @field:SerializedName("latitude")
    val latitude: String? = null,

    @field:SerializedName("name")
    val locationName: String? = null, // Mengganti 'name' menjadi 'locationName'

    @field:SerializedName("longitude")
    val longitude: String? = null
)

data class PostUser(

    @field:SerializedName("profile_photo")
    val profilePhotoUrl: String? = null, // Mengganti 'profile_photo' menjadi 'profilePhotoUrl'

    @field:SerializedName("name")
    val userName: String? = null, // Mengganti 'name' menjadi 'userName'

    @field:SerializedName("id")
    val userId: Int? = null // Mengganti 'id' menjadi 'userId'
)

data class PostItem(

    @field:SerializedName("post_id")
    val postId: Int? = null, // ID unik dari post

    @field:SerializedName("updated_at")
    val updatedAt: String? = null, // Waktu post terakhir diperbarui

    @field:SerializedName("created_at")
    val createdAt: String? = null, // Waktu post pertama kali dibuat

    @field:SerializedName("location")
    val postLocation: PostLocation? = null, // Mengganti 'location' menjadi 'postLocation'

    @field:SerializedName("title")
    val postTitle: String? = null, // Mengganti 'title' menjadi 'postTitle'

    @field:SerializedName("category")
    val postCategory: String? = null, // Mengganti 'category' menjadi 'postCategory'

    @field:SerializedName("user")
    val postUser: PostUser? = null, // Mengganti 'user' menjadi 'postUser'

    @field:SerializedName("content")
    val postContent: String? = null // Mengganti 'content' menjadi 'postContent'
)
