package com.hayakai.data.remote.response

import com.google.gson.annotations.SerializedName

data class GetReportMapCommentsResponse(

    @field:SerializedName("data")
    val data: List<DataItemComment>,

    @field:SerializedName("status")
    val status: String
)

data class UserComment(

    @field:SerializedName("profile_photo")
    val profilePhoto: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("id")
    val id: Int
)

data class DataItemComment(

    @field:SerializedName("report_id")
    val reportId: Int,

    @field:SerializedName("update_at")
    val updateAt: String,

    @field:SerializedName("created_at")
    val createdAt: String,

    @field:SerializedName("comment_id")
    val commentId: Int,

    @field:SerializedName("user")
    val user: UserComment,

    @field:SerializedName("content")
    val content: String,

    @field:SerializedName("by_me")
    val byMe: Boolean
)
