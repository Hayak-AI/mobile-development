package com.hayakai.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class CommentPost(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "post_id")
    var postId: Int = 0,

    @ColumnInfo(name = "content")
    var content: String = "",

    @ColumnInfo(name = "user_id")
    var userId: Int = 0,

    @ColumnInfo(name = "user_name")
    var userName: String = "",

    @ColumnInfo(name = "user_image")
    var userImage: String? = "",

    @ColumnInfo(name = "by_me")
    var byMe: Boolean = false,

    @ColumnInfo(name = "created_at")
    var createdAt: String = ""
) : Parcelable