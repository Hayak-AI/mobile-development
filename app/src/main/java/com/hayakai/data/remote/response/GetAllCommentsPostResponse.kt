package com.hayakai.data.remote.response

import com.google.gson.annotations.SerializedName

data class GetAllCommentsPostResponse(

    @field:SerializedName("data")
    val data: List<PostCommentItem?>? = null,

    @field:SerializedName("status")
    val status: String? = null
)

data class PostCommentItem(

    @field:SerializedName("post_id")
    val postId: Int? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("comment_id")
    val commentId: Int? = null,

    @field:SerializedName("user")
    val user: PostCommentUser? = null,

    @field:SerializedName("content")
    val content: String? = null
)

data class PostCommentUser(

    @field:SerializedName("profile_photo")
    val profilePhoto: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
)
