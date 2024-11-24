package com.hayakai.data.remote.response

import com.google.gson.annotations.SerializedName

data class GetAllCommentsRepostResponse(

    @field:SerializedName("data")
    val data: List<RepostCommentItem?>? = null,

    @field:SerializedName("status")
    val status: String? = null
)

data class RepostCommentItem(

    @field:SerializedName("report_id")
    val reportId: Int? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("comment_id")
    val commentId: Int? = null,

    @field:SerializedName("user")
    val user: RepostCommentUser? = null,

    @field:SerializedName("content")
    val content: String? = null
)

data class RepostCommentUser(

    @field:SerializedName("profile_photo")
    val profilePhoto: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
)
